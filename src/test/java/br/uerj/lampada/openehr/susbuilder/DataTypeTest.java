package br.uerj.lampada.openehr.susbuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;

public class DataTypeTest extends TestCase {

	// public void testDvData() throws Exception {
	// assertTrue(true);
	// }

	public void testDvDateTime1() throws Exception {
		String date = "19971006";
		String value = parseDate(date);
		DvDate dv = null;
		if (value != null) {
			dv = new DvDate(value);
		}
		assertNotNull(dv);
	}

	public void testDvDateTime2() throws Exception {
		String date = "1997-10-07";
		String value = parseDateTime(date);
		DvDateTime dv = null;
		if (value != null) {
			dv = new DvDateTime(value);
		}
		assertNotNull(dv);
	}

	private String parseDate(String dbValue) {
		String dateSuffix = "TZ";
		// String dateSuffix = "";
		return parseISODate(dbValue, dateSuffix);
	}

	private String parseDateTime(String dbValue) {
		String dateTimeSuffix = "T00:00:00-03:00";
		return parseISODate(dbValue, dateTimeSuffix);
	}

	// Date parser that matches YYYY-MM-DD or YYYYMMDD formats and their shorter
	// forms
	private String parseISODate(String dbValue, String dateSuffix) {
		String newDate = dbValue;

		Matcher dayMatcher = Pattern.compile("^(\\w{4})(\\w{2})(\\w{2})$")
				.matcher(newDate);
		Matcher monthMatcher = Pattern.compile("^(\\w{4})(\\w{2})$").matcher(
				newDate);
		Matcher yearMatcher = Pattern.compile("^(\\w{4})$").matcher(newDate);

		if (dayMatcher.find()) {
			newDate = dayMatcher.group(1) + "-" + dayMatcher.group(2) + "-"
					+ dayMatcher.group(3);
			newDate += dateSuffix;
		} else if (dbValue.matches("^(\\w{4})-(\\w{2})-(\\w{2})$")) {
			newDate += dateSuffix;
		} else if (monthMatcher.find()) {
			newDate = monthMatcher.group(1) + "-" + monthMatcher.group(2);
			newDate += "-01" + dateSuffix;
		} else if (dbValue.matches("^(\\w{4})-(\\w{2})$")) {
			newDate += "-01" + dateSuffix;
		} else if (yearMatcher.find()) {
			newDate = yearMatcher.group(1);
			newDate += "-01-01" + dateSuffix;
		} else {
			newDate = null;
		}

		return newDate;
	}
}
