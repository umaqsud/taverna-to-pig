package edu.tuberlin.dima.taverna_to_pig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.digester.plugins.Declaration;
import org.apache.tools.ant.util.FileUtils;
import org.stringtemplate.v4.ST;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import edu.tuberlin.dima.taverna_to_pig.exception.UnsupportedWorkflowException;
import edu.tuberlin.dima.taverna_to_pig.model.DECLAREStatement;
import edu.tuberlin.dima.taverna_to_pig.model.FOREACHStatement;
import edu.tuberlin.dima.taverna_to_pig.model.LOADStatement;
import edu.tuberlin.dima.taverna_to_pig.model.PigStatement;
import edu.tuberlin.dima.taverna_to_pig.model.STREAMStatement;
import edu.tuberlin.dima.taverna_to_pig.model.UDFXPathStatement;
import edu.tuberlin.dima.taverna_to_pig.utils.TavernaToPigConverterUtil;

/**
 * 
 * @author Umar Maqsud
 * 
 */
public class TavernaToPigConverter {

	private final String STORE = "STORE <name> INTO '$<path>';";
	private final String XPATH_EXP = "%DECLARE <exp_name> '<exp>';";
	private final String DEFINE = "DEFINE <name1> `$<name2>`;";

	private final String TOOL_COMMAND_EXPRESSION = "/workflow/dataflow/processors/processor[name='fitsValidation']/activities/activity/configBean/net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean/useCaseDescription/command";

	private String PYTHON_STREAM;

	private static URI TOOL_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/tool");
	private static URI XPATH_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/xpath");
	private static URI BEANSHELL_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/beanshell");
	private static String FLATTEN_LIST_URI = "http://ns.taverna.org.uk/2010/activity/localworker/org.embl.ebi.escience.scuflworkers.java.FlattenList";

	private File file;

	public TavernaToPigConverter(File file) {
		this.file = file;

		try {

			URL url = Resources.getResource("templates/python_stream.st");
			PYTHON_STREAM = Resources.toString(url, Charsets.UTF_8);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public PigScript transform() throws Exception {

		/* XML Parsing for XPath */

		String xml = FileUtils.readFully(new FileReader(file));

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));

		Document xmlDocument = builder.parse(is);

		XPath xPath = XPathFactory.newInstance().newXPath();

		/* END */

		WorkflowBundleIO io = new WorkflowBundleIO();

		WorkflowBundle wfBundle = io.readBundle(file,
				"application/vnd.taverna.t2flow+xml");

		Workflow workflow = wfBundle.getMainWorkflow();

		// pig script
		String workflowName = workflow.getName();
		List<String> pigLines = new ArrayList<String>();
		List<String> pigHeaderLines = new ArrayList<String>();
		List<String> pigParameters = new ArrayList<String>();
		HashMap<String, String> pythonStreams = new HashMap<String, String>();

		NamedSet<InputWorkflowPort> inputPorts = workflow.getInputPorts();

		Scufl2Tools scufl2Tools = new Scufl2Tools();

		HashMap<Processor, PigStatement> processorMap = new HashMap<Processor, PigStatement>();
		HashMap<InputWorkflowPort, PigStatement> inputWorkflowMap = new HashMap<InputWorkflowPort, PigStatement>();

		/* handling InputWorkflowPorts */

		for (InputWorkflowPort inputWorkflowPort : inputPorts) {
			Integer depth = inputWorkflowPort.getDepth();

			if (depth >= 1) {
				LOADStatement loadStatement = new LOADStatement(
						inputWorkflowPort);

				pigLines.add(loadStatement.render());
				pigParameters.add(loadStatement.getDirectoryParameterName());

				inputWorkflowMap.put(inputWorkflowPort, loadStatement);

			} else {
				DECLAREStatement declareStatement = new DECLAREStatement(
						inputWorkflowPort);

				pigLines.add(declareStatement.render());
				pigParameters.add(declareStatement.getParameterName());

				inputWorkflowMap.put(inputWorkflowPort, declareStatement);
			}

		}

		/* handling Processors */

		List<Processor> reverseProcessors = TavernaToPigConverterUtil
				.reverseProcessors(workflow.getProcessors());

		int index = 0;

		for (Processor processor : reverseProcessors) {

			String outputRelationName = processor.getName();

			String inputRelationName = "";

			String inputFieldName = "";

			List<DataLink> datalinksTo = scufl2Tools.datalinksTo(processor
					.getInputPorts().first());

			if (datalinksTo.get(0).getReceivesFrom() instanceof OutputProcessorPort) {
				OutputProcessorPort o = (OutputProcessorPort) datalinksTo
						.get(0).getReceivesFrom();
				inputRelationName = o.getParent().getName();

				if (processorMap.containsKey(o.getParent())) {
					PigStatement pigStatement = processorMap.get(o.getParent());

					if (pigStatement instanceof STREAMStatement) {
						STREAMStatement streamStatement = (STREAMStatement) pigStatement;
						inputFieldName = streamStatement.getFieldName();
					} else if (pigStatement instanceof UDFXPathStatement) {
						UDFXPathStatement udfStatement = (UDFXPathStatement) pigStatement;
						inputFieldName = udfStatement.getFieldName();
					}

				}

			}

			if (datalinksTo.get(0).getReceivesFrom() instanceof InputWorkflowPort) {
				InputWorkflowPort i = (InputWorkflowPort) datalinksTo.get(0)
						.getReceivesFrom();
				inputRelationName = i.getName();

				if (inputWorkflowMap.containsKey(i)) {

					PigStatement pigStatement = inputWorkflowMap.get(i);

					if (pigStatement instanceof LOADStatement) {
						LOADStatement loadStatement = (LOADStatement) pigStatement;
						inputFieldName = loadStatement.getFieldName();
					}

				}

			}

			Configuration configuration = scufl2Tools
					.configurationForActivityBoundToProcessor(processor,
							workflow.getParent().getMainProfile());

			URI type = configuration.getConfigures().getType();

			System.out.println(type);

			// tool
			if (type.equals(TOOL_URI)) {

				STREAMStatement streamStatement = new STREAMStatement(
						processor, inputRelationName, outputRelationName);

				pigLines.add(streamStatement.render());

				ST stDef = new ST(DEFINE);
				stDef.add("name1", streamStatement.getStreamName());
				stDef.add("name2", streamStatement.getStreamName());

				pigHeaderLines.add(stDef.render());

				pigParameters.add(streamStatement.getStreamName());

				String command = xPath.compile(TOOL_COMMAND_EXPRESSION)
						.evaluate(xmlDocument);

				command = command.replaceAll("%%.*%%", "");

				ST stPythonStream = new ST(PYTHON_STREAM);
				stPythonStream.add("command", command);

				pythonStreams.put(streamStatement.getStreamName(),
						stPythonStream.render());

				processorMap.put(processor, streamStatement);

			} else if (type.equals(XPATH_URI)) {

				System.out.println(inputRelationName);
				System.out.println(outputRelationName);
				System.out.println(inputFieldName);

				String xpathExp = TavernaToPigConverterUtil
						.cleanXPathExpression(configuration.getJson()
								.get("xpathExpression").textValue());

				UDFXPathStatement udfStatement = new UDFXPathStatement(
						processor, inputRelationName, outputRelationName,
						inputFieldName);

				pigLines.add(udfStatement.render());

				ST stExp = new ST(XPATH_EXP);
				stExp.add("exp_name", udfStatement.getXpathExpParemeterName());
				stExp.add("exp", xpathExp);

				pigHeaderLines.add(stExp.render());

				processorMap.put(processor, udfStatement);

			} else if (type.equals(BEANSHELL_URI)) {

				JsonNode derivedFrom = configuration.getJson().get(
						"derivedFrom");

				if (derivedFrom.asText().equals(FLATTEN_LIST_URI)) {
					
					String fieldName = inputFieldName + "_flatten";
					
					FOREACHStatement foreachStatement = new FOREACHStatement(
							processor, inputRelationName, outputRelationName,
							inputFieldName, fieldName, "FLATTEN");
					
					pigLines.add(foreachStatement.render());
					
					processorMap.put(processor, foreachStatement);

				} else {
					throw new UnsupportedWorkflowException(
							"Unsupported Processor Type: " + derivedFrom);
				}

				System.out.println(inputRelationName);
				System.out.println(outputRelationName);
				System.out.println(inputFieldName);

			} else {
				throw new UnsupportedWorkflowException(
						"Unsupported Processor Type: " + type);
			}

			index++;

		}

		/* handling OutputWorkflowPorts */

		for (OutputWorkflowPort outputWorkflowPort : workflow.getOutputPorts()) {

			List<DataLink> datalinksTo = scufl2Tools
					.datalinksTo(outputWorkflowPort);

			String outPutRelationName = "";

			if (datalinksTo.get(0).getReceivesFrom() instanceof OutputProcessorPort) {
				OutputProcessorPort o = (OutputProcessorPort) datalinksTo
						.get(0).getReceivesFrom();
				outPutRelationName = o.getParent().getName();
			}

			ST st = new ST(STORE);
			st.add("name", outPutRelationName);
			st.add("path", outputWorkflowPort.getName());

			pigLines.add(st.render());

			pigParameters.add(outputWorkflowPort.getName());
		}

		return new PigScript(workflowName, pigHeaderLines, pigLines,
				pigParameters, pythonStreams);
	}
}
