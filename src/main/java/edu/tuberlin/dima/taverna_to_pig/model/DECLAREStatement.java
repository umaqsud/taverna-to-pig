package edu.tuberlin.dima.taverna_to_pig.model;

import org.stringtemplate.v4.ST;

import uk.org.taverna.scufl2.api.port.InputWorkflowPort;

public class DECLAREStatement implements PigStatement {

	private final String DECLARE = "%DECLARE <variableName> '$<parameterName>';";

	private String variableName;
	private String parameterName;
	
	private InputWorkflowPort inputWorkflowPort;
	
	public DECLAREStatement(InputWorkflowPort inputWorkflowPort) {
		this.inputWorkflowPort = inputWorkflowPort;
		this.variableName = inputWorkflowPort.getName();
		this.parameterName = inputWorkflowPort.getName();
	}

	@Override
	public String render() {
		ST st = new ST(DECLARE);
		st.add("variableName", variableName);
		st.add("parameterName", parameterName);
		return st.render();
	}
	
	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public InputWorkflowPort getInputWorkflowPort() {
		return inputWorkflowPort;
	}

	public void setInputWorkflowPort(InputWorkflowPort inputWorkflowPort) {
		this.inputWorkflowPort = inputWorkflowPort;
	}
	
}
