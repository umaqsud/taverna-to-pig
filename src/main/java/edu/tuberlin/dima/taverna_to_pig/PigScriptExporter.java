package edu.tuberlin.dima.taverna_to_pig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import org.stringtemplate.v4.ST;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;

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

		String pigFileName = pigScript.getName() + ".pig";
		File pigFile = new File(directory, pigFileName);
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

		String pigParamFileName = pigScript.getName() + ".param";
		File pigParamFile = new File(directory, pigParamFileName);

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
		
		URL readmeTemplateUrl = Resources.getResource("templates/readme.st");
		String readmeTemplate = Resources.toString(readmeTemplateUrl, Charsets.UTF_8); 
		
		String pigRunClusterCommand = "pig -param_file " + pigParamFile.getName()
				+ " " + pigFile.getName();
		
		String tree = ". \n";
		tree += "|-- " + pigParamFileName + "\n";
		tree += "|-- " + pigFileName + "\n";
		tree += "|-- README \n";
		tree += "|-- run.sh \n";
		tree += "|-- scripts \n";
		
		for (Entry<String, String> entry : pythonStreams.entrySet()) {
			tree += "    |-- " + entry.getKey() + ".py \n";
		}

		ST st = new ST(readmeTemplate);
		st.add("tree", tree);
		st.add("pigParamFileName", pigParamFileName);
		st.add("pigRunClusterCommand", pigRunClusterCommand);
		String readme = st.render();
		
		Files.write(readme, new File(directory, "README"), Charsets.UTF_8);

		/* shell script */
		
		String pigRunLocalCommand = "pig -x local -param_file " + pigParamFile.getName()
				+ " " + pigFile.getName();
		
		String shellScript = "#!/bin/sh";
		shellScript += "\n";
		
		shellScript += pigRunLocalCommand;

		Files.write(shellScript, new File(directory, "run.sh"), Charsets.UTF_8);
	}

}
