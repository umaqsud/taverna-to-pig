package edu.tuberlin.dima.taverna_to_pig.model;

import org.stringtemplate.v4.ST;

import uk.org.taverna.scufl2.api.core.Processor;

public class FOREACHStatement implements PigStatement {
	
	// alias = FOREACH alias GENERATE expression [AS schema]
	
	private final String FOREACH = "<outputRelationName> = FOREACH <inputRelationName> GENERATE <functionName>(<inputFieldName>) as <fieldName>;";
	
	private String inputRelationName;
	private String outputRelationName;
	private String inputFieldName;
	private String fieldName;
	private String functionName;
	
	private Processor processor;
	
	public FOREACHStatement(Processor processor, String inputRelationName,
			String outputRelationName, String inputFieldName, String fieldNane,
			String funtionName) {
		super();
		this.processor = processor;
		this.inputRelationName = inputRelationName;
		this.outputRelationName = outputRelationName;
		this.inputFieldName = inputFieldName;
		this.fieldName = fieldNane;
		this.functionName = funtionName;
	}
	
	@Override
	public String render() {
		ST st = new ST(FOREACH);
		st.add("inputRelationName", inputRelationName);
		st.add("outputRelationName", outputRelationName);
		st.add("functionName", functionName);
		st.add("inputFieldName", inputFieldName);
		st.add("fieldName", fieldName);
		
		return st.render();
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

	public String getInputFieldName() {
		return inputFieldName;
	}

	public void setInputFieldName(String inputFieldName) {
		this.inputFieldName = inputFieldName;
	}

	public String getFieldNane() {
		return fieldName;
	}

	public void setFieldNane(String fieldNane) {
		this.fieldName = fieldNane;
	}

	public String getFuntionName() {
		return functionName;
	}

	public void setFuntionName(String funtionName) {
		this.functionName = funtionName;
	}

	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}
	
}