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
	 * @param patients
	 */
	public void writeOpenEHRObject(String uuid, List<Composition> compositions)
			throws Exception;
}
