package br.uerj.lampada.openehr.susbuilder;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTimeParser;

public class DataTypeTest extends TestCase {

	public void testDvData() throws Exception {
		assertTrue(true);
	}

	public void testDvDateTime1() throws Exception {
		String date = "19971006";
		// String date = "2012-07-01T00:00:00-03:00";
		// DateTime dt =
		// ISODateTimeFormat.dateElementParser().withOffsetParsed().parseDateTime(date);
		DateTime dt = DvDateTimeParser.parseDate(date);
		assertNotNull(dt);
	}

	public void testDvDateTime2() throws Exception {
		String date = "1997-10-07";
		DateTime dt = DvDateTimeParser.parseDate(date);
		assertNotNull(dt);
	}
}
