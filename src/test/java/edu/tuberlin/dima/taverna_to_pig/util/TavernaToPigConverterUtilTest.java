package edu.tuberlin.dima.taverna_to_pig.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.tuberlin.dima.taverna_to_pig.utils.TavernaToPigConverterUtil;

/**
 * 
 * @author Umar Maqsud
 * 
 */
public class TavernaToPigConverterUtilTest {
	
	@Test
	public void testCreateSchemaForDepth() {
		
		String schema = TavernaToPigConverterUtil.createSchemaForDepth(1);
		assertEquals("(val: chararray)", schema);
		
		schema = TavernaToPigConverterUtil.createSchemaForDepth(2);
		assertEquals("(l: {t: (val: chararray)})", schema);
		
		schema = TavernaToPigConverterUtil.createSchemaForDepth(3);
		assertEquals("(l: {t: (l: {t: (val: chararray)})})", schema);
		
		schema = TavernaToPigConverterUtil.createSchemaForDepth(4);
		assertEquals("(l: {t: (l: {t: (l: {t: (val: chararray)})})})", schema);

	}
	
	@Test
	public void testCleanXPathExpression() {
		String expression = "/default:fits/default:filestatus/default:valid//text()";
		
		String cleaned = TavernaToPigConverterUtil.cleanXPathExpression(expression);
		
		System.out.println(cleaned);
	}
	
}
