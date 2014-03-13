package edu.tuberlin.dima.taverna_to_pig.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.core.Processor;

/**
 * 
 * @author Umar Maqsud
 * 
 */
public class TavernaToPigConverterUtil {

	public static String createSchemaForDepth(int depth) {

		if (depth < 1) {
			throw new IllegalArgumentException(
					"depth must be greater or equal than 1.");
		}

		if (depth == 1) {
			return "(val: chararray)";
		}

		if (depth == 2) {
			return "(l: {t: " + createSchemaForDepth(1) + "})";
		}

		return "(l: {t: " + createSchemaForDepth(depth - 1) + "})";
	}

	public static String cleanXPathExpression(String expression) {
		String cleaned = expression.replaceAll("default:", "");
		cleaned = cleaned.replaceAll("//text\\(\\)", "");

		return cleaned;
	}

	public static List<Processor> reverseProcessors(
			NamedSet<Processor> processorsSet) {
		List<Processor> processors = new ArrayList<Processor>();
		processors.addAll(processorsSet);

		Collections.reverse(processors);

		return processors;
	}

}
