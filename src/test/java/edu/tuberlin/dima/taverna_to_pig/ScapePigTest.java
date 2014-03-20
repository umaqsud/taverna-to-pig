package edu.tuberlin.dima.taverna_to_pig;

import org.apache.pig.pigunit.PigTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Resources;

/**
 * @author Umar Maqsud
 */
public class ScapePigTest extends PigUnitTest {

	@Rule
	public final TemporaryFolder tmpDir = new TemporaryFolder();

	@Before
	public void setUp() {
		System.out.println(tmpDir.getRoot().getAbsolutePath());
	}

	@Test
	@Ignore
	public void testScapeToolSample() throws Exception {

		String pigFile = Resources.getResource(
				"pig/scripts/scape_tool_sample.pig").getPath();

		String[] args = {
				"image_pathes="
						+ Resources.getResource("pig/input/image_pathes.txt")
								.getPath(),
				"jhove_stream="
						+ Resources.getResource("pig/scripts/jhove_stream.py")
								.getPath(),
				"fits_stream="
						+ Resources.getResource("pig/scripts/fits_stream.py")
								.getPath(),
				"opj_stream="
						+ Resources.getResource("pig/scripts/opj_stream.py")
								.getPath(),
				"jpylyzer_stream="
						+ Resources.getResource(
								"pig/scripts/jpylyzer_stream.py").getPath(),
				"opj_decompress_stream="
						+ Resources.getResource(
								"pig/scripts/opj_decompress_stream.py")
								.getPath(),
				"compare_stream="
						+ Resources
								.getResource("pig/scripts/compare_stream.py")
								.getPath(),
				"tmp_dir=" + tmpDir.getRoot().getAbsolutePath() };

		PigTest test = new PigTest(pigFile, args);

		printAlias(test, "image_pathes");
		printAlias(test, "fits_validation");
		printAlias(test, "jhove2");
		printAlias(test, "flatten_jhove2_list");
		printAlias(test, "flatten_jhove_list_filtered");
		printAlias(test, "opj_compress");
		printAlias(test, "jpylyzer_validation");
		printAlias(test, "jpylyzer");
		printAlias(test, "flatten_jpylyzer_list_filtered");
		printAlias(test, "opj_decompress");
		printAlias(test, "opj_decompress_projected");
		printAlias(test, "compared");
	}

}
