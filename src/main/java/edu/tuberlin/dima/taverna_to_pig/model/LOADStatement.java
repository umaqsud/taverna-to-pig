package edu.tuberlin.dima.taverna_to_pig.model;

import org.stringtemplate.v4.ST;

import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import edu.tuberlin.dima.taverna_to_pig.utils.TavernaToPigConverterUtil;

/**
 * Wrapper class for Pig LOAD Statements.
 * 
 * @author Umar Maqsud
 *
 */
public class LOADStatement implements PigStatement {

	// A = LOAD 'data' [USING function] [AS schema];

	private final String LOAD = "<relationName> = LOAD '$<directoryName>' USING <function>() AS <schema>;";

	private String relationName;
	private String directoryParameterName;
	private String schema;
	private String fieldName;
	
	private InputWorkflowPort inputWorkflowPort;

	public LOADStatement(String relationName, String directoryName,
			String schema, String fieldName) {
		super();
		this.relationName = relationName;
		this.directoryParameterName = directoryName;
		this.schema = schema;
		this.fieldName = fieldName;
	}

	public LOADStatement(InputWorkflowPort inputWorkflowPort) {
		
		if (inputWorkflowPort.getDepth() == 0) {
			throw new IllegalArgumentException("Bad list depth for inputWorkflowPort. Use DECLAREStatement.");
		}
		
		this.inputWorkflowPort = inputWorkflowPort;	
		
		this.relationName = inputWorkflowPort.getName();
		this.directoryParameterName = inputWorkflowPort.getName();
		
		int depth = inputWorkflowPort.getDepth();
		
		this.schema = TavernaToPigConverterUtil.createSchemaForDepth(depth);
		
		if (depth == 1) {
			this.fieldName = "val";
		} else {
			this.fieldName = "l";
		}
	}

	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public String getDirectoryParameterName() {
		return directoryParameterName;
	}

	public void setDirectoryParameterName(String directoryParameterName) {
		this.directoryParameterName = directoryParameterName;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public InputWorkflowPort getInputWorkflowPort() {
		return inputWorkflowPort;
	}

	public void setInputWorkflowPort(InputWorkflowPort inputWorkflowPort) {
		this.inputWorkflowPort = inputWorkflowPort;
	}

	public String getLOAD() {
		return LOAD;
	}

	@Override
	public String render() {
		ST st = new ST(LOAD);
		st.add("relationName", relationName);
		st.add("directoryName", directoryParameterName);
		st.add("function", "PigStorage");
		st.add("schema", schema);
		return st.render();
	}

}
