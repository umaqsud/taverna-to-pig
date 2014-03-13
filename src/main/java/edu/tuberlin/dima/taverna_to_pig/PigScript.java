package edu.tuberlin.dima.taverna_to_pig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Umar Maqsud
 * 
 */
public class PigScript {

	private String name;
	private List<String> pigHeaderLines = new ArrayList<String>();
	private List<String> pigLines = new ArrayList<String>();
	private List<String> pigParameters = new ArrayList<String>();
	private HashMap<String, String> pythonStreams = new HashMap<String, String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getPigHeaderLines() {
		return pigHeaderLines;
	}

	public void setPigHeaderLines(List<String> pigHeaderLines) {
		this.pigHeaderLines = pigHeaderLines;
	}

	public List<String> getPigLines() {
		return pigLines;
	}

	public void setPigLines(List<String> pigLines) {
		this.pigLines = pigLines;
	}

	public List<String> getPigParameters() {
		return pigParameters;
	}

	public void setPigParameters(List<String> pigParameters) {
		this.pigParameters = pigParameters;
	}

	public HashMap<String, String> getPythonStreams() {
		return pythonStreams;
	}

	public void setPythonStreams(HashMap<String, String> pythonStreams) {
		this.pythonStreams = pythonStreams;
	}

	public PigScript(String name, List<String> pigHeaderLines,
			List<String> pigLines, List<String> pigParameters,
			HashMap<String, String> pythonStreams) {
		this.name = name;
		this.pigHeaderLines = pigHeaderLines;
		this.pigLines = pigLines;
		this.pigParameters = pigParameters;
		this.pythonStreams = pythonStreams;
	}

}
