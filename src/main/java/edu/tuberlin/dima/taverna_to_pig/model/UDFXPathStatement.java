package edu.tuberlin.dima.taverna_to_pig.model;

import org.stringtemplate.v4.ST;

import uk.org.taverna.scufl2.api.core.Processor;

public class UDFXPathStatement implements PigStatement {

	private final String XPATH = "<outputRelationName> = FOREACH <inputRelationName> GENERATE XPathService('$<xpathExpParemeterName>', <inputFieldName>) AS <fieldName>;";

	private String inputRelationName;
	private String outputRelationName;
	private String inputFieldName;
	private String fieldName;
	private String xpathExpParemeterName;

	private Processor processor;

	@Override
	public String render() {
		ST st = new ST(XPATH);
		st.add("inputRelationName", inputRelationName);
		st.add("outputRelationName", outputRelationName);
		st.add("xpathExpParemeterName", xpathExpParemeterName);
		st.add("fieldName", fieldName);
		st.add("inputFieldName", inputFieldName);
		
		return st.render();
	}

	public UDFXPathStatement(String inputRelationName,
			String outputRelationName, String inputFieldName,
			String xpathExpParemeterName) {
		super();
		this.inputRelationName = inputRelationName;
		this.outputRelationName = outputRelationName;
		this.inputFieldName = inputFieldName;
		this.xpathExpParemeterName = xpathExpParemeterName;

		this.fieldName = "node_list";
	}

	public UDFXPathStatement(Processor processor, String inputRelationName,
			String outputRelationName, String inputFieldName) {
		this.processor = processor;
		this.inputRelationName = inputRelationName;
		this.outputRelationName = outputRelationName;
		this.inputFieldName = inputFieldName;

		this.fieldName = "node_list";

		this.xpathExpParemeterName = processor.getName() + "_xpath_exp";
	}

	public String getXpathExpParemeterName() {
		return xpathExpParemeterName;
	}

	public void setXpathExpParemeterName(String xpathExpParemeterName) {
		this.xpathExpParemeterName = xpathExpParemeterName;
	}

	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public String getInputRelationName() {
		return inputRelationName;
	}

	public void setInputRelationName(String inputRelationName) {
		this.inputRelationName = inputRelationName;
	}

	public String getOutputRelationName() {
		return outputRelationName;
	}

	public void setOutputRelationName(String outputRelationName) {
		this.outputRelationName = outputRelationName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
