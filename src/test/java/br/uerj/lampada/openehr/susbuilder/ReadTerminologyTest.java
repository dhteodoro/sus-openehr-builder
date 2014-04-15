package br.uerj.lampada.openehr.susbuilder;

import junit.framework.TestCase;
import br.uerj.lampada.openehr.susbuilder.terminology.Terminology;

public class ReadTerminologyTest extends TestCase {

	private static final String termFile = "/terminology.csv";

	public void testReadTerminology() throws Exception {
		Terminology terminology = new Terminology();
		terminology.load(ReadTerminologyTest.class
				.getResourceAsStream(termFile));
		assertNotNull(terminology);
	}

	public void testWriteTerminology() throws Exception {
		Terminology terminology = new Terminology();
		terminology.load(ReadTerminologyTest.class
				.getResourceAsStream(termFile));
		String newTermFile = "." + termFile + ".bak";
		terminology.writeTerminology(newTermFile);
	}
}
