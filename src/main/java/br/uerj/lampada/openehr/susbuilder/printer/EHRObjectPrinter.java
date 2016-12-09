/**
 * 
 */
package br.uerj.lampada.openehr.susbuilder.printer;

import java.util.List;

import org.openehr.rm.composition.Composition;

/**
 * @author teodoro
 * 
 */
public interface EHRObjectPrinter {

	/**
	 * Interface to write EHR objects
	 * 
	 * @param uuid
	 * @param compositions
	 * @throws Exception
	 */
	public void writeOpenEHRObject(String uuid, String outputFolder,
			List<Composition> compositions) throws Exception;

	/**
	 * Interface do set output folder
	 * 
	 * @param folder
	 */
	public void setOutputFolder(String folder);
}
