package edu.tuberlin.dima.taverna_to_pig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * 
 * @author Umar Maqsud
 * 
 */
public class PigScriptExporter {

	public static void export(PigScript pigScript, File directory)
			throws Exception {

		if (!directory.isDirectory()) {
			directory.mkdir();
		}

		/* Pig File */

		File pigFile = new File(directory, pigScript.getName() + ".pig");
		pigFile.createNewFile();

		FileWriter fw = new FileWriter(pigFile);
		BufferedWriter bw = new BufferedWriter(fw);

		// register jar
		bw.write("REGISTER taverna-to-pig-1.0-SNAPSHOT-withDependencies.jar;");
		bw.newLine();
		bw.newLine();

		// define part
		bw.write("DEFINE XPathService edu.tuberlin.dima.taverna_to_pig.udf.XPathFunction();");
		bw.newLine();
		bw.newLine();

		for (String line : pigScript.getPigHeaderLines()) {
			bw.write(line);
			bw.newLine();
			bw.newLine();
		}

		for (String line : pigScript.getPigLines()) {
			bw.write(line);
			bw.newLine();
			bw.newLine();
		}

		bw.flush();
		bw.close();

		HashMap<String, String> pythonStreams = pigScript.getPythonStreams();

		/* Param File */

		File pigParamFile = new File(directory, pigScript.getName() + ".param");

		FileWriter pigParamFileFW = new FileWriter(pigParamFile);
		BufferedWriter pigParamFileBW = new BufferedWriter(pigParamFileFW);

		for (String parameter : pigScript.getPigParameters()) {
			pigParamFileBW.write(parameter + "=Please define this parameter");
			pigParamFileBW.newLine();
		}

		pigParamFileBW.flush();
		pigParamFileFW.close();

		/* scripts */

		File scriptsDirectory = new File(directory, "scripts");

		if (!scriptsDirectory.isDirectory()) {
			scriptsDirectory.mkdir();
		}

		for (Entry<String, String> entry : pythonStreams.entrySet()) {
			Files.write(entry.getValue(),
					new File(scriptsDirectory, entry.getKey() + ".py"),
					Charsets.UTF_8);
		}

		/* README */

		String readme = "First make executable: sudo chmod +x run.sh";
		readme += "\n";
		readme += "\n";
		readme += "To run the pig script on local mode: ./run.sh";

		Files.write(readme, new File(directory, "README"), Charsets.UTF_8);

		/* shell script */

		String shellScript = "#!/bin/sh";
		shellScript += "\n";
		shellScript += ("pig -x local -param_file " + pigParamFile.getName()
				+ " " + pigFile.getName());

		Files.write(shellScript, new File(directory, "run.sh"), Charsets.UTF_8);
	}

}
