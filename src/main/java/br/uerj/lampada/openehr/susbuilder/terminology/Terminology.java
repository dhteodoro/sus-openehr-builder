package br.uerj.lampada.openehr.susbuilder.terminology;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openehr.rm.datatypes.text.CodePhrase;

public class Terminology {

	private static final String DELIMITER = "::";

	private static final Logger log = Logger.getLogger(Terminology.class);

	private static final String UTF8 = "UTF-8";

	// in-memory representation as [terminology name, code, text]
	private MultiKeyMap terminology;

	public Terminology() {
		terminology = new MultiKeyMap();
	}

	/**
	 * Parses given lines and loads a terminology
	 * 
	 * @param lines
	 * @return
	 */
	private void fromLines(List<String> lines) {
		addAll(lines);
	}

	/**
	 * Adds all content from given inputStream
	 * 
	 * @param input
	 * @throws Exception
	 */
	public void addAll(InputStream input) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input,
				UTF8));
		String line = reader.readLine();
		List<String> lines = new ArrayList<String>();
		while (line != null) {
			lines.add(line);
			line = reader.readLine();
		}
		addAll(lines);
	}

	/**
	 * Adds all given lines into this terminology
	 * 
	 * @param lines
	 */
	public void addAll(List<String> lines) {
		String[] tokens = null;
		int totalTokens = 0;

		for (String line : lines) {
			if (line.trim().length() > 0) {
				tokens = line.split(DELIMITER);
				totalTokens = tokens.length;

				if (totalTokens != 3) {

					log.error("Wrong formatted line skipped: " + line);

					continue;
				}

				String name = tokens[0];
				String code = tokens[1];
				String text = tokens[2];
				addTerm(name, code, text);
			}
		}
	}

	/**
	 * Adds a new term and its text into this terminology
	 * 
	 * @param terminology
	 * @param code
	 * @param text
	 */
	public void addTerm(String name, String code, String text) {

		// if(log.isDebugEnabled()) {
		// log.debug("adding term: " + name + DELIMITER + code + DELIMITER +
		// text);
		// }

		terminology.put(name, code, text);
	}

	/**
	 * Clears all terms from this terminology
	 * 
	 */
	public void clear() {
		terminology.clear();
	}

	/**
	 * Counts the total number of terminologies
	 * 
	 * @return 0 if empty
	 */
	public int countTerminologies() {
		return terminology.size();
	}

	/**
	 * Gets the internal representation of terms, mainly for testing purpose
	 * 
	 * @return
	 */
	public Map<String, Map<String, Map<String, String>>> getTerminology() {
		return terminology;
	}

	/**
	 * Retrieves the text for given code_prhase
	 * 
	 * @param codePhrase
	 * @return
	 */
	public String getText(CodePhrase codePhrase) {
		return getText(codePhrase.getTerminologyId().toString(),
				codePhrase.getCodeString());
	}

	/**
	 * Retrieves the text for given code_prhase in syntax: {terminology}::{code}
	 * 
	 * @param codePhrase
	 * @return
	 */
	public String getText(String codePhrase) {
		int i = codePhrase.indexOf(DELIMITER);
		if (i <= 0 || i == codePhrase.length()) {
			throw new IllegalArgumentException(
					"wrong format, expected {terminology}::{code}");
		}
		String name = codePhrase.substring(0, i);
		String code = codePhrase.substring(i + 2);
		return getText(name, code);
	}

	/**
	 * Retrieves the text for given terminology and code
	 * 
	 * @param terminology
	 * @param code
	 * @param path
	 * @return null if not found
	 */
	public String getText(String name, String code) {
		String text = (String) terminology.get(name, code);

		// log.debug("Retrieved text '" + text + "' for [" + terminology + "::"
		// + code + "] ");

		return text;
	}

	/**
	 * Loads an in-memory terminology by given inputStream
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public void load(InputStream input) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input,
				UTF8));
		String line = reader.readLine();
		List<String> lines = new ArrayList<String>();
		while (line != null) {
			lines.add(line);
			line = reader.readLine();
		}
		fromLines(lines);
	}

	/**
	 * Loads an in-memory terminology by given file
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public void load(String filename) throws IOException {
		List<String> lines = FileUtils.readLines(new File(filename), UTF8);
		fromLines(lines);
	}

	/**
	 * Writes this terminology into external file using the following syntax:
	 * {terminology}::{code}::{text} for each line
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public void writeTerminology(String filename) throws IOException {
		StringBuffer buf = new StringBuffer();

		Iterator<MultiKey> it = terminology.mapIterator();

		while (it.hasNext()) {
			MultiKey obj = it.next();
			String name = (String) obj.getKey(0);
			String code = (String) obj.getKey(1);
			String text = (String) terminology.get(obj);

			buf.append(name);
			buf.append(DELIMITER);
			buf.append(code);
			buf.append(DELIMITER);
			buf.append(text);
			if (it.hasNext()) {
				buf.append("\r\n");
			}
		}
		File termFile = new File(filename);
		FileUtils.deleteQuietly(termFile);
		FileUtils.writeStringToFile(termFile, buf.toString(), UTF8);
	}
}