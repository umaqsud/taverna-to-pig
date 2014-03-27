package edu.tuberlin.dima.taverna_to_pig;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Resources;

/**
 * @author Umar Maqsud
 */
public class TavernaToPigConverterTest extends PigUnitTest {

	@Rule
	public final TemporaryFolder tmpDir = new TemporaryFolder();

	@Before
	public void setUp() {
		System.out.println(tmpDir.getRoot().getAbsolutePath());
	}

	@Test
	public void testScapeSimple() throws Exception {

		File file = new File(Resources.getResource(
				"scufl/workflows/scape_ffff/FFFF-Workflows-simple.t2flow")
				.getFile());

		TavernaToPigConverter tavernaToPigConverter = new TavernaToPigConverter(
				file);

		PigScript pigScript = tavernaToPigConverter.transform();

		// pig lines

		List<String> pigLines = pigScript.getPigLines();

		assertEquals(5, pigLines.size());

		assertEquals(
				"imagePathes = LOAD '$imagePathes' USING PigStorage() AS (val: chararray);",
				pigLines.get(0));

		assertEquals(
				"fitsValidation = STREAM imagePathes THROUGH fitsValidation_stream AS (stream_stdout: chararray);",
				pigLines.get(1));

		assertEquals(
				"XPathJhove2 = FOREACH fitsValidation GENERATE XPathService('$XPathJhove2_xpath_exp', stream_stdout) AS node_list;",
				pigLines.get(2));

		assertEquals(
				"Flatten_List = FOREACH XPathJhove2 GENERATE FLATTEN(node_list) as node_list_flatten;",
				pigLines.get(3));

		assertEquals("STORE Flatten_List INTO '$out';", pigLines.get(4));

		// PigScriptExporter.export(pigScript, tmpDir.getRoot());

	}

	@Test
	public void testMultipleInputs() throws Exception {

		File file = new File(Resources.getResource(
				"scufl/workflows/test/multiple-input-ports.t2flow").getFile());

		TavernaToPigConverter tavernaToPigConverter = new TavernaToPigConverter(
				file);

		PigScript pigScript = tavernaToPigConverter.transform();

		List<String> pigLines = pigScript.getPigLines();

		assertEquals(6, pigLines.size());

		List<String> expectedPigLines = new ArrayList<String>();

		expectedPigLines
				.add("inputList1 = LOAD '$inputList1' USING PigStorage() AS (val: chararray);");
		expectedPigLines
				.add("inputList2 = LOAD '$inputList2' USING PigStorage() AS (l: {t: (val: chararray)});");
		expectedPigLines
				.add("inputList3 = LOAD '$inputList3' USING PigStorage() AS (l: {t: (l: {t: (val: chararray)})});");
		expectedPigLines.add("%DECLARE inputSingle '$inputSingle';");
		expectedPigLines
				.add("testTool = STREAM inputList1 THROUGH testTool_stream AS (stream_stdout: chararray);");
		expectedPigLines.add("STORE testTool INTO '$out';");

		assertEquals(expectedPigLines, pigLines);

	}
}
