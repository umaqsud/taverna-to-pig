package edu.tuberlin.dima.taverna_to_pig.main;

import java.io.File;

import edu.tuberlin.dima.taverna_to_pig.PigScript;
import edu.tuberlin.dima.taverna_to_pig.PigScriptExporter;
import edu.tuberlin.dima.taverna_to_pig.TavernaToPigConverter;

/**
 * 
 * @author Umar Maqsud
 * 
 */
public class TavernaToPigMain {

	public static void main(String[] args) throws Exception {
		
		if (args.length != 4) {
			System.err.println("Usage: -i path_to_taverna_workflow -o path_to_output");
			System.err.println("-i path to taverna workflow");
			System.err.println("-o path to output");
		}
		
		if (!args[0].equals("-i") || !args[2].equals("-o")) {
			System.err.println("Usage: -i path_to_taverna_workflow -o path_to_output");
			System.err.println("-i path to taverna workflow");
			System.err.println("-o path to output");
		}
		
		TavernaToPigConverter tavernaToPigConverter = new TavernaToPigConverter(
				new File(args[1]));

		PigScript pigScript = tavernaToPigConverter.transform();
		
		PigScriptExporter.export(pigScript, new File(args[3]));
		
	}

}
