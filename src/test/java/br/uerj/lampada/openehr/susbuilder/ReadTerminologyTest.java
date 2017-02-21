package br.uerj.lampada.openehr.susbuilder;

import java.io.FileInputStream;
import java.util.Properties;

import junit.framework.TestCase;
import br.uerj.lampada.openehr.susbuilder.terminology.Terminology;
import br.uerj.lampada.openehr.susbuilder.utils.Constants;

public class ReadTerminologyTest extends TestCase {

	public void testReadTerminology() throws Exception {

		Properties props = new Properties();
		props.load(new FileInputStream(Constants.builderConfig));

		String terminologyFile = props.getProperty("terminology.file");

		Terminology terminology = new Terminology();
		terminology.load(new FileInputStream(terminologyFile));

		assertNotNull(terminology);
	}

	public void testWriteTerminology() throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream(Constants.builderConfig));

		String terminologyFile = props.getProperty("terminology.file");

		Terminology terminology = new Terminology();
		terminology.load(new FileInputStream(terminologyFile));
		String newTermFile = "./test/terminology/terminology.csv.bak";
		terminology.writeTerminology(newTermFile);
	}
}
