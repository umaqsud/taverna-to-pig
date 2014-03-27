package edu.tuberlin.dima.taverna_to_pig.model;

import org.stringtemplate.v4.ST;

import uk.org.taverna.scufl2.api.core.Processor;

public class STREAMStatement implements PigStatement {
	
	// alias = STREAM alias [, alias …] THROUGH {`command` | cmd_alias } [AS schema] ;
	
	private final String STREAM = "<outputRelationName> = STREAM <inputRelationName> THROUGH <streamName> AS (<fieldName>: chararray);";
	
	private String inputRelationName;
	private String outputRelationName;
	private String streamName;
	private String fieldName;
	
	private Processor processor;

	public STREAMStatement(String inputRelationName, String outputRelationName,
			String streamName) {
		super();
		this.inputRelationName = inputRelationName;
		this.outputRelationName = outputRelationName;
		this.streamName = streamName;
		this.fieldName = "stream_stdout";
	}
	
	public STREAMStatement(Processor processor, String inputRelationName, String outputRelationName) {
		this.processor = processor;
		this.inputRelationName = inputRelationName;
		this.outputRelationName = outputRelationName;
		
		this.streamName = processor.getName() + "_stream";
		this.fieldName = "stream_stdout";
	}
	
	@Override
	public String render() {
		ST st = new ST(STREAM);
		st.add("inputRelationName", inputRelationName);
		st.add("outputRelationName", outputRelationName);
		st.add("streamName", streamName);
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

	public String getStreamName() {
		return streamName;
	}

	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}
	
}
