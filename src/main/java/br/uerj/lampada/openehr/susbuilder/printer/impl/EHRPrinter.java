/**
 * 
 */
package br.uerj.lampada.openehr.susbuilder.printer.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openehr.rm.composition.Composition;

import br.uerj.lampada.openehr.susbuilder.builder.EHRBuilder;
import br.uerj.lampada.openehr.susbuilder.printer.EHRObjectPrinter;
import br.uerj.lampada.openehr.susbuilder.printer.EHRPrintCore;
import br.uerj.lampada.openehr.susbuilder.utils.Constants;

/**
 * @author teodoro
 * 
 */
public class EHRPrinter extends EHRPrintCore implements EHRObjectPrinter {

	private static Map<String, String> docTypePrefix;
	static {
		docTypePrefix = new HashMap<String, String>();
		docTypePrefix.put(Constants.EHR_STR, Constants.EHR_UUID_PREFIX);
		docTypePrefix.put(Constants.EHRACCESS_STR,
				Constants.EHRACCESS_UUID_PREFIX);
		docTypePrefix.put(Constants.EHRSTATUS_STR,
				Constants.EHRSTATUS_UUID_PREFIX);
		docTypePrefix.put(Constants.COMPOSITION_STR,
				Constants.COMPOSITION_UUID_PREFIX);
		docTypePrefix.put(Constants.CONTRIBUTION_STR,
				Constants.CONTRIBUTION_UUID_PREFIX);
	}

	public EHRPrinter(String outputFolder, String format) {
		super(outputFolder, format);
	}

	private String createOutputFolder(String folder) {
		String ehrFolder = Constants.EHR_STR;
		String objDir = getOutputFolder() + "/" + ehrFolder + "/" + folder;

		// File file = new File(objDir);
		// // if the directory does not exist, create it
		// if (!file.exists()) {
		// file.mkdirs();
		// }
		return objDir;
	}

	/**
	 * Write EHRs
	 * 
	 * @param uuid
	 * @param compositions
	 * @param outputFolder
	 * @throws Exception
	 */
	private void writeEHRObj(String uuid, EHRBuilder ebuilder) throws Exception {

		// Write to file
		for (String docType : docTypePrefix.keySet()) {
			if (docType.equals(Constants.COMPOSITION_STR)) {
				// Write Composition
				writeListEHRObj(uuid, docType,
						(List<Object>) (List<?>) ebuilder
								.getVersionedCompositions());
			} else if (docType.equals(Constants.CONTRIBUTION_STR)) {
				// Write Contribution
				writeListEHRObj(uuid, docType,
						(List<Object>) (List<?>) ebuilder.getContributions());
			} else {
				// Write EHR
				if (docType.equals(Constants.EHR_STR)) {
					writeSingleEHRObj(uuid, docType, ebuilder.getEhr());
					// Write EHRAccess
				} else if (docType.equals(Constants.EHRACCESS_STR)) {
					writeSingleEHRObj(uuid, docType,
							ebuilder.getVersionedEHRAccess());
					// Write EHRStatus
				} else if (docType.equals(Constants.EHRSTATUS_STR)) {
					writeSingleEHRObj(uuid, docType,
							ebuilder.getVersionedEHRStatus());
				}
			}
		}
	}

	private void writeListEHRObj(String uuid, String docType,
			List<Object> ehrListObj) throws Exception {
		// Write Compositions and Constributions

		for (int i = 0; i < ehrListObj.size(); i++) {
			String docId = docTypePrefix.get(docType) + (i + 1) + "." + uuid;
			Object ehrObj = ehrListObj.get(i);

			// String outputFolder = createOutputFolder(docType + "/"+
			// Constants.EHR_UUID_PREFIX + uuid);
			String outputFolder = createOutputFolder(docType);

			String filename = outputFolder + "/" + docId + "." + getFormat();

			writeInFormat(ehrObj, filename);
		}
	}

	private void writeSingleEHRObj(String uuid, String docType, Object ehrObj)
			throws Exception {
		// Write EHRStatus

		String docId = docTypePrefix.get(docType) + uuid;

		String outputFolder = createOutputFolder(docType);

		String filename = outputFolder + "/" + docId + "." + getFormat();

		writeInFormat(ehrObj, filename);
	}

	/**
	 * Write EHRs
	 * 
	 * @param uuid
	 * @param compositions
	 * @param outputFolder
	 * @throws Exception
	 */
	@Override
	public void writeOpenEHRObject(String uuid, List<Composition> compositions)
			throws Exception {

		String patientId = Constants.EHR_UUID_PREFIX + uuid;

		EHRBuilder eb = new EHRBuilder(patientId, uuid, compositions);
		eb.createEHRObj();

		writeEHRObj(uuid, eb);
	}
}
