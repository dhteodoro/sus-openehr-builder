package br.uerj.lampada.openehr.susbuilder.printer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.openehr.binding.XMLBinding;
import org.openehr.binding.XMLBindingException;
import org.openehr.schemas.v1.COMPOSITION;
import org.openehr.schemas.v1.CONTRIBUTION;
import org.openehr.schemas.v1.CompositionDocument;
import org.openehr.schemas.v1.ContributionDocument;
import org.openehr.schemas.v1.EhrDocument;
import org.openehr.schemas.v1.VERSION;
import org.openehr.schemas.v1.VERSIONEDCOMPOSITION;
import org.openehr.schemas.v1.VERSIONEDEHRACCESS;
import org.openehr.schemas.v1.VERSIONEDEHRSTATUS;
import org.openehr.schemas.v1.VersionDocument;
import org.openehr.schemas.v1.VersionedCompositionDocument;
import org.openehr.schemas.v1.VersionedEhrAccessDocument;
import org.openehr.schemas.v1.VersionedEhrStatusDocument;

import br.uerj.lampada.openehr.susbuilder.serializer.JsonExclusionStrategy;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Class used to generate EHR objects and print in the available formats EHR:
 * XML and JSON Composition and Version: XML
 * 
 * @author teodoro
 * 
 */
public class EHRPrintCore {

	private static String builderConfig = "builder.properties";

	private static String JSON = "json";
	private static Logger log = Logger.getLogger(EHRPrintCore.class);

	private static boolean prettyPrint;

	private static String XML = "xml";

	static {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(builderConfig));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		prettyPrint = Boolean.parseBoolean(props.getProperty("pretty.print"));

	}

	private String format;

	private String outputFolder;

	private XmlOptions xmlOptions;

	public EHRPrintCore(String outputFolder, String format) {
		this.outputFolder = outputFolder;

		this.format = format;

		this.xmlOptions = new XmlOptions();
		if (prettyPrint) {
			xmlOptions.setSavePrettyPrint();
		}
		xmlOptions.setSaveAggressiveNamespaces();
		xmlOptions.setSaveInner();
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @return the outputFolder
	 */
	public String getOutputFolder() {
		return outputFolder;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @param outputFolder
	 *            the outputFolder to set
	 */
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	/**
	 * Write EHR objects in the implemented formats
	 * 
	 * EHR objects implemented: Composition, Composition Version, EHR Output
	 * formats implemented: XML or JSON
	 * 
	 * @param ehrObj
	 * @param filename
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws XMLBindingException
	 */
	public void writeInFormat(Object ehrObj, String filename)
			throws JsonGenerationException, JsonMappingException, IOException,
			XMLBindingException {

		if (getFormat().equals(JSON)) {
			writeObjectToJSON(ehrObj, filename);
		} else if (getFormat().equals(XML)) {
			writeObjectToXML(ehrObj, filename);
		} else {
			System.err.println("Unknow format: " + getFormat());
			System.exit(1);
		}
	}

	/**
	 * Write RM objects in the JSON format
	 * 
	 * @param obj
	 * @param filename
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws XMLBindingException
	 */
	protected void writeObjectToJSON(Object obj, String filename)
			throws JsonGenerationException, JsonMappingException, IOException,
			XMLBindingException {

		File file = new File(filename);
		// convert xml object into JSON (String)
		XmlObject xmlObj = getXMLObject(obj);

		String doc = insertEhrObjType(xmlObj);

		JSON json = getJSONObject(doc);

		// TODO
		// Did not manage to remove this prefixes using the API
		// There is probably a more elegant solution
		int identFactor = 0;
		if (prettyPrint) {
			identFactor = 2;
		}
		String jsonString = json.toString(identFactor)
				.replaceAll("\"v1:", "\"").replaceAll("\"@", "\"");

		FileUtils.writeStringToFile(file, jsonString);
	}

	/**
	 * Write RM objects in the XML format
	 * 
	 * @param obj
	 * @param filename
	 * @throws XMLBindingException
	 * @throws IOException
	 */
	protected void writeObjectToXML(Object obj, String filename)
			throws XMLBindingException, IOException {

		File file = new File(filename);
		XmlObject xmlObj = getXMLObject(obj);

		String doc = insertEhrObjType(xmlObj);
		if (doc != null) {
			FileUtils.writeStringToFile(file, doc);
		}
	}

	/**
	 * 
	 */

	private String insertEhrObjType(XmlObject xmlObj) {

		String xmlStr = null;

		Date date = new Date();

		XmlObject doc = null;
		if (xmlObj instanceof org.openehr.schemas.v1.EHR) {
			doc = EhrDocument.Factory.newInstance();
			((EhrDocument) doc).setEhr((org.openehr.schemas.v1.EHR) xmlObj);
		} else if (xmlObj instanceof VERSIONEDEHRACCESS) {
			doc = VersionedEhrAccessDocument.Factory.newInstance();
			((VersionedEhrAccessDocument) doc)
					.setVersionedEhrAccess((VERSIONEDEHRACCESS) xmlObj);
		} else if (xmlObj instanceof VERSIONEDEHRSTATUS) {
			doc = VersionedEhrStatusDocument.Factory.newInstance();
			((VersionedEhrStatusDocument) doc)
					.setVersionedEhrStatus((VERSIONEDEHRSTATUS) xmlObj);
		} else if (xmlObj instanceof VERSIONEDCOMPOSITION) {
			doc = VersionedCompositionDocument.Factory.newInstance();
			((VersionedCompositionDocument) doc)
					.setVersionedComposition((VERSIONEDCOMPOSITION) xmlObj);
		} else if (xmlObj instanceof CONTRIBUTION) {
			doc = ContributionDocument.Factory.newInstance();
			((ContributionDocument) doc).setContribution((CONTRIBUTION) xmlObj);
		} else if (xmlObj instanceof COMPOSITION) {
			doc = CompositionDocument.Factory.newInstance();
			((CompositionDocument) doc).setComposition((COMPOSITION) xmlObj);
		} else if (xmlObj instanceof VERSION) {
			doc = VersionDocument.Factory.newInstance();
			((VersionDocument) doc).setVersion((VERSION) xmlObj);
		}

		if (doc != null) {
			xmlStr = doc.xmlText(xmlOptions);
		} else {
			log.error("[" + new Timestamp(date.getTime())
					+ "] Unknow type of XML object: "
					+ xmlObj.getClass().toString());
		}

		return xmlStr;
	}

	/**
	 * Function to convert directly a RM object to JSON (without conversion to
	 * XML)
	 * 
	 * TODO Implementation not working properly Exclusion strategy needs to be
	 * fixed Some objects cannot be converted
	 * 
	 * @param obj
	 * @param filename
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws XMLBindingException
	 */
	private void writeObjectToJSON2(Object obj, String filename)
			throws JsonGenerationException, JsonMappingException, IOException,
			XMLBindingException {
		ExclusionStrategy excludeStrings = new JsonExclusionStrategy();
		Gson gson = new GsonBuilder().setExclusionStrategies(excludeStrings)
				.create();
		// Gson gson = new Gson();
		String json = gson.toJson(obj);
		File file = new File(filename);
		FileUtils.writeStringToFile(file, json);
	}

	/**
	 * Convert a RM object in a XML object
	 * 
	 * @param obj
	 * @return
	 * @throws XMLBindingException
	 */
	public static XmlObject getXMLObject(Object obj) throws XMLBindingException {

		XMLBinding xmlBinding = new XMLBinding();
		Object value = xmlBinding.bindToXML(obj);
		XmlObject xmlObj = (XmlObject) value;
		return xmlObj;
	}

	/**
	 * Generate a JSON object from a XML object
	 * 
	 * @param xmlObj
	 * @return
	 * @throws XMLBindingException
	 */
	private static JSON getJSONObject(String xmlObj) throws XMLBindingException {

		XMLSerializer xmlSerializer = new XMLSerializer();

		// xmlSerializer.setSkipWhitespace(true);
		xmlSerializer.setTypeHintsEnabled(false);
		xmlSerializer.setNamespaceLenient(true);
		xmlSerializer.setSkipNamespaces(true);
		xmlSerializer.setRemoveNamespacePrefixFromElements(true);
		xmlSerializer.setSkipNamespaces(true);

		// convert String into Json
		return xmlSerializer.read(xmlObj);
	}
}
