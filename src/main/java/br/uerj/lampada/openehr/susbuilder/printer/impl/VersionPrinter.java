/**
 * 
 */
package br.uerj.lampada.openehr.susbuilder.printer.impl;

import java.util.List;

import org.openehr.rm.common.changecontrol.Version;
import org.openehr.rm.composition.Composition;

import br.uerj.lampada.openehr.susbuilder.builder.EHRBuilder;
import br.uerj.lampada.openehr.susbuilder.printer.EHRObjectPrinter;
import br.uerj.lampada.openehr.susbuilder.printer.EHRPrintCore;
import br.uerj.lampada.openehr.susbuilder.utils.Constants;

/**
 * @author teodoro
 * 
 */
public class VersionPrinter extends EHRPrintCore implements EHRObjectPrinter {

	public VersionPrinter(String outputFolder, String format) {
		super(outputFolder, format);
	}

	/**
	 * Write Versions
	 * 
	 * @param uuid
	 * @param compositions
	 * @param outputFolder
	 * @throws Exception
	 */
	@Override
	public void writeOpenEHRObject(String uuid, String outputFolder,
			List<Composition> compositions) throws Exception {

		String patientId = Constants.EHR_UUID_PREFIX + uuid;

		EHRBuilder pc = new EHRBuilder(patientId, uuid, compositions);
		List<Version<Composition>> versions = pc.getVersionFromComposition();

		// Write Versioned Composition
		for (int i = 0; i < versions.size(); i++) {
			Version<Composition> version = versions.get(i);

			String docId = Constants.COMPOSITION_UUID_PREFIX + (i + 1) + "."
					+ uuid;
			String filename = outputFolder + "/" + docId + "." + getFormat();

			writeInFormat(version, filename);
		}
	}

}
