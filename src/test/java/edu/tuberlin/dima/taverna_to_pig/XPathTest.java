package edu.tuberlin.dima.taverna_to_pig;

import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.tools.ant.util.FileUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.common.io.Resources;

/**
 * 
 * @author Umar Maqsud
 * 
 */
public class XPathTest {

	@Test
	public void testJhoveXPath() throws Exception {
		String xml = FileUtils.readFully(new FileReader(new File(Resources
				.getResource("pig/input/jhove_validation/19191227b.xml")
				.getFile())));

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));

		Document xmlDocument = builder.parse(is);

		XPath xPath = XPathFactory.newInstance().newXPath();

		String expression = "/jhove/repInfo/status";
		String status = xPath.compile(expression).evaluate(xmlDocument);

		assertEquals("Well-Formed and valid", status);
	}

	@Test
	public void testFitsXPath() throws Exception {

		String filePath = Resources.getResource(
				"pig/input/fits_validation/19191227b.xml").getPath();

		FileInputStream file = new FileInputStream(new File(filePath));

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();

		DocumentBuilder builder = builderFactory.newDocumentBuilder();

		Document xmlDocument = builder.parse(file);

		XPath xPath = XPathFactory.newInstance().newXPath();

		String expression = "/fits/filestatus/valid";
		String status = xPath.compile(expression).evaluate(xmlDocument);

		assertEquals("true", status);

		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(
				xmlDocument, XPathConstants.NODESET);

		for (int i = 0; i < nodeList.getLength(); i++) {
			// System.out.println(nodeList.item(0).getFirstChild().getNodeValue());
		}

		assertEquals(1, nodeList.getLength());
		assertEquals("true", nodeList.item(0).getFirstChild().getNodeValue());
	}

}
