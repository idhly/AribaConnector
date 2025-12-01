package com.example.ariba;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

public class XmlUtils {

	public static String serializeXml(org.w3c.dom.Document doc) throws TransformerException {

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		return writer.toString();
	}

	public static boolean isXmlContentType(String contentType) {

		if (contentType == null) {
			return false;
		}
		// case insensitive
		contentType = contentType.toLowerCase().trim();
		return contentType.startsWith("text/xml") || contentType.startsWith("application/xml");
	}

	public static Document parseCXML(String xmlContent) {

		if (xmlContent == null || xmlContent.trim().isEmpty()) {
			return null;
		}

		Document doc = null;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);

			// Disable external DTD and entities for security
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

			factory.setXIncludeAware(false);
			factory.setExpandEntityReferences(false);

			// Optional, disable XML validation
			factory.setValidating(false);

			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes("UTF-8")));

		} catch (ParserConfigurationException e) {
			// System.err.println("Parser configuration error: " + e.getMessage());
			doc = null;
		} catch (SAXException e) {
			// System.err.println("XML parsing error: " + e.getMessage());
			doc = null;
		} catch (IOException e) {
			// System.err.println("IO error while parsing XML: " + e.getMessage());
			doc = null;
		}

		return doc;
	}

}