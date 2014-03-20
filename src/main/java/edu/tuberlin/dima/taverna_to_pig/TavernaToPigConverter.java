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

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import edu.tuberlin.dima.taverna_to_pig.exception.UnsupportedWorkflowException;
import edu.tuberlin.dima.taverna_to_pig.utils.TavernaToPigConverterUtil;

/**
 * 
 * @author Umar Maqsud
 * 
 */
public class TavernaToPigConverter {

	private final String LOAD = "<name> = LOAD '$<data>' USING <function>() AS <schema>;";
	private final String PARAM = "%DECLARE <name> '$<vname>';";
	private final String STREAM = "<outputRelationName> = STREAM <inputRelationName> THROUGH <streamName> AS (stream_stdout: chararray);";
	private final String STORE = "STORE <name> INTO '$<path>';";
	private final String XPATH = "<outputRelationName> = FOREACH <inputRelationName> GENERATE XPathService('$<exp_name>', stream_stdout) AS node_list;";
	private final String XPATH_EXP = "%DECLARE <exp_name> '<exp>';";
	private final String DEFINE = "DEFINE <name1> `$<name2>`;";

	private final String TOOL_COMMAND_EXPRESSION = "/workflow/dataflow/processors/processor[name='fitsValidation']/activities/activity/configBean/net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean/useCaseDescription/command";

	private String PYTHON_STREAM;

	private static URI TOOL_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/tool");
	private static URI XPATH_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/xpath");

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

		for (InputWorkflowPort inputWorkflowPort : inputPorts) {
			String name = inputWorkflowPort.getName();
			Integer depth = inputWorkflowPort.getDepth();

			if (depth >= 1) {
				ST st = new ST(LOAD);
				st.add("name", name);
				st.add("data", name);
				st.add("function", "PigStorage");
				st.add("schema",
						TavernaToPigConverterUtil.createSchemaForDepth(depth));
				String pigLine = st.render();
				pigLines.add(pigLine);
			} else {
				ST st = new ST(PARAM);
				st.add("name", name);
				st.add("vname", name);
				String pigLine = st.render();
				pigLines.add(pigLine);
			}

			pigParameters.add(name);
		}

		for (Processor processor : TavernaToPigConverterUtil
				.reverseProcessors(workflow.getProcessors())) {

			String outputRelationName = processor.getName();

			String inputRelationName = "";

			List<DataLink> datalinksTo = scufl2Tools.datalinksTo(processor
					.getInputPorts().first());

			if (datalinksTo.get(0).getReceivesFrom() instanceof OutputProcessorPort) {
				OutputProcessorPort o = (OutputProcessorPort) datalinksTo
						.get(0).getReceivesFrom();
				inputRelationName = o.getParent().getName();
			}

			if (datalinksTo.get(0).getReceivesFrom() instanceof InputWorkflowPort) {
				InputWorkflowPort i = (InputWorkflowPort) datalinksTo.get(0)
						.getReceivesFrom();
				inputRelationName = i.getName();
			}

			Configuration configuration = scufl2Tools
					.configurationForActivityBoundToProcessor(processor,
							workflow.getParent().getMainProfile());

			URI type = configuration.getConfigures().getType();

			// tool
			if (type.equals(TOOL_URI)) {

				String streamName = processor.getName() + "_stream";

				ST st = new ST(STREAM);
				st.add("inputRelationName", inputRelationName);
				st.add("outputRelationName", outputRelationName);
				st.add("streamName", streamName);

				pigLines.add(st.render());

				ST stDef = new ST(DEFINE);
				stDef.add("name1", streamName);
				stDef.add("name2", streamName);

				pigHeaderLines.add(stDef.render());

				pigParameters.add(streamName);

				String command = xPath.compile(TOOL_COMMAND_EXPRESSION)
						.evaluate(xmlDocument);

				command = command.replaceAll("%%.*%%", "");

				ST stPythonStream = new ST(PYTHON_STREAM);
				stPythonStream.add("command", command);

				pythonStreams.put(streamName, stPythonStream.render());

			} else if (type.equals(XPATH_URI)) {

				String xpathExpName = processor.getName() + "_xpath_exp";
				String xpathExp = TavernaToPigConverterUtil
						.cleanXPathExpression(configuration.getJson()
								.get("xpathExpression").textValue());

				ST st = new ST(XPATH);
				st.add("inputRelationName", inputRelationName);
				st.add("outputRelationName", outputRelationName);
				st.add("exp_name", xpathExpName);

				pigLines.add(st.render());

				ST stExp = new ST(XPATH_EXP);
				stExp.add("exp_name", xpathExpName);
				stExp.add("exp", xpathExp);

				pigHeaderLines.add(stExp.render());

			} else {
				throw new UnsupportedWorkflowException(
						"Unsupported Processor Type: " + type);
			}

		}

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
