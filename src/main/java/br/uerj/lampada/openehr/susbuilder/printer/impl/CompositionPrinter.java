/**
 * 
 */
package br.uerj.lampada.openehr.susbuilder.printer.impl;

import java.io.File;
import java.util.List;

import org.openehr.rm.composition.Composition;

import br.uerj.lampada.openehr.susbuilder.printer.EHRObjectPrinter;
import br.uerj.lampada.openehr.susbuilder.printer.EHRPrintCore;
import br.uerj.lampada.openehr.susbuilder.utils.Constants;

/**
 * @author teodoro
 * 
 */
public class CompositionPrinter extends EHRPrintCore implements
		EHRObjectPrinter {

	public CompositionPrinter(String outputFolder, String format) {
		super(outputFolder, format);
	}

	/**
	 * Write Compositions
	 * 
	 * @param uuid
	 * @param compositions
	 * @param dirName
	 * @throws Exception
	 */
	public void writeOpenEHRObject(String uuid, List<Composition> compositions)
			throws Exception {
		String compositionFolder = Constants.COMPOSITION_STR;
		// Write Composition
		for (int i = 0; i < compositions.size(); i++) {
			Composition composition = compositions.get(i);
			String ehrDir = getOutputFolder() + "/" + compositionFolder + "/"
					+ Constants.EHR_UUID_PREFIX + uuid;
			File file = new File(ehrDir);
			// if the directory does not exist, create it
			if (!file.exists()) {
				file.mkdirs();
			}
			String docId = Constants.COMPOSITION_UUID_PREFIX + (i + 1) + "."
					+ uuid;
			String filename = ehrDir + "/" + docId + "." + getFormat();

			writeInFormat(composition, filename);
		}
	}

}
