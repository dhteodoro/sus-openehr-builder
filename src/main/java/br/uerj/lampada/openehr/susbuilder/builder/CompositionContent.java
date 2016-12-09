package br.uerj.lampada.openehr.susbuilder.builder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openehr.rm.datatypes.basic.DvBoolean;
import org.openehr.rm.datatypes.basic.ReferenceModelName;
import org.openehr.rm.datatypes.quantity.DvCount;
import org.openehr.rm.datatypes.quantity.DvOrdinal;
import org.openehr.rm.datatypes.quantity.DvProportion;
import org.openehr.rm.datatypes.quantity.DvQuantity;
import org.openehr.rm.datatypes.quantity.ProportionKind;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.quantity.datetime.DvDuration;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;

import br.uerj.lampada.openehr.susbuilder.mapping.Mapping;
import br.uerj.lampada.openehr.susbuilder.mapping.PathMapping;
import br.uerj.lampada.openehr.susbuilder.mapping.PathMetadata;
import br.uerj.lampada.openehr.susbuilder.mapping.PathState;
import br.uerj.lampada.openehr.susbuilder.utils.Constants;

public class CompositionContent {

	private static Logger log = Logger.getLogger(CompositionContent.class);
	private static final String TERM_DEFAULT = "SUS";
	private Map<String, Object> content;
	private Map<String, List<String>> elementToTree;
	private PathMapping pathMapping;

	private Map<String, PathState> pathState;

	public CompositionContent(PathMapping pathMapping) {
		this.content = new HashMap<String, Object>();
		this.pathState = new HashMap<String, PathState>();
		this.elementToTree = new HashMap<String, List<String>>();

		this.pathMapping = pathMapping;

		initiateContent(pathMapping.getTemplate());
		updatePathState();
	}

	public Map<String, Object> getContent() {
		return content;
	}

	public PathMapping getPathMap() {
		return pathMapping;
	}

	public Map<String, PathState> getPathState() {
		return pathState;
	}

	public void setPathState(Map<String, PathState> pathState) {
		this.pathState = pathState;
	}

	public void updateContent(HashMap<String, Object> dbValues)
			throws Exception {
		content = new HashMap<String, Object>();
		for (String path : dbValues.keySet()) {
			Object dv = null;
			Object dbValue = dbValues.get(path);

			if (dbValue instanceof String) {
				dv = getDvData(((String) dbValue).trim(), path);
			} else if (dbValue instanceof List) {
				List<Object> container = new ArrayList<Object>();
				for (String val : (List<String>) dbValue) {
					container.add(getDvData(val, path));
				}
				dv = container;
			}
			content.put(path, dv);
		}
		updatePathState();
	}

	// DV_BOOLEAN("DV_BOOLEAN")
	// DV_COUNT("DV_COUNT")
	// DV_QUANTITY("DV_QUANTITY")
	// DV_PROPORTION("DV_PROPORTION")
	// DV_TEXT("DV_TEXT")
	// DV_CODED_TEXT("DV_CODED_TEXT")
	// DV_ORDINAL("DV_ORDINAL")
	// CODE_PHRASE("CODE_PHRASE")
	// DV_DATE_TIME("DV_DATE_TIME")
	// DV_DURATION("DV_DURATION")

	private Object getDvData(String dbValue, String path) throws Exception {

		PathMetadata pm = pathMapping.getPathMetadata(path);

		Date date = new Date();

		if (pm == null) {
			pm = getPathMetadata(pathMapping.getTemplate(), path);
		}

		String rmTypeName = pm.getDataType();

		Object dv = null;
		if (dbValue.equals("")) {
			dbValue = null;
		}
		if (dbValue != null) {
			try {
				// DV_BOOLEAN
				if (ReferenceModelName.DV_BOOLEAN.getName().equals(rmTypeName)) {
					Boolean value = parseBoolean(dbValue);
					if (value != null) {
						dv = new DvBoolean(value);
					}
				}
				// DV_COUNT
				else if (ReferenceModelName.DV_COUNT.getName().equals(
						rmTypeName)) {
					int magnitude = Integer.parseInt(dbValue);
					dv = new DvCount(magnitude);
				}
				// DV_QUANTITY
				else if (ReferenceModelName.DV_QUANTITY.getName().equals(
						rmTypeName)) {

					double magnitude = parseDouble(dbValue);

					String units = pm.getUnit();
					if (units == null) {
						units = "units";
					}

					int precision = 0;
					dv = new DvQuantity(units, magnitude, precision);
				}
				// DV_PROPORTION
				else if (ReferenceModelName.DV_PROPORTION.getName().equals(
						rmTypeName)) {
					double numerator = parseDouble(dbValue);
					double denominator = 1;
					int precision = 0;
					dv = new DvProportion(numerator, denominator,
							ProportionKind.UNITARY, precision);
				}
				// DV_TEXT
				else if (ReferenceModelName.DV_TEXT.getName()
						.equals(rmTypeName)) {
					String value = dbValue;
					String name = pm.getTerminologyName();
					if (name != null) {
						value = Constants.terminology.getText(name, value);
					}
					if (value != null) {
						dv = new DvText(value);
					}
				}
				// DV_CODED_TEXT
				// DEFINING_CODE
				// CODE_PHRASE
				else if (ReferenceModelName.DV_CODED_TEXT.getName().equals(
						rmTypeName)
						|| ReferenceModelName.CODE_PHRASE.getName().equals(
								rmTypeName)
						|| "DEFINING_CODE".equals(rmTypeName)) {
					String value = null;
					String code = dbValue;
					String name = pm.getTerminologyName();

					if (name != null) {
						if (Constants.terminology.getText(name, code) != null) {
							value = Constants.terminology.getText(name, code);
						}
					} else {
						name = TERM_DEFAULT;
					}

					if (value != null
							|| (pm.getNullValues() == null
									|| pm.getNullValues().size() == 0 || !pm
									.getNullValues().contains(code))) {
						CodePhrase definingCode = new CodePhrase(name, code);
						dv = new DvCodedText(value, definingCode);
					}
				}
				// DV_ORDINAL
				else if (ReferenceModelName.DV_ORDINAL.getName().equals(
						rmTypeName)) {
					int value = Integer.parseInt(dbValue);
					DvCodedText symbol = new DvCodedText("ord_symbol",
							new CodePhrase("APAC", "ord_code"));
					dv = new DvOrdinal(value, symbol);
				}
				// DV_DATE_TIME
				else if (ReferenceModelName.DV_DATE_TIME.getName().equals(
						rmTypeName)) {
					String value = parseDateTime(dbValue);
					if (value != null) {
						dv = new DvDateTime(value);
					}
				}
				// DV_DATE
				else if ("DV_DATE".equals(rmTypeName)) {
					String value = parseDate(dbValue);
					if (value != null) {
						dv = new DvDate(value);
					}
				}
				// DV_DURATION
				else if (ReferenceModelName.DV_DURATION.getName().equals(
						rmTypeName)) {
					String value = dbValue;
					dv = new DvDuration(value);
				} else {
					dv = dbValue;
					// throw new Exception("Unknown DV type: " + rmTypeName);
				}
			} catch (Exception e) {
				date = new Date();
				log.warn("[" + new Timestamp(date.getTime())
						+ "] Cannot instanciate " + rmTypeName + " for value "
						+ dbValue + "(" + pm.getColumn() + ")");
				return null;
			}
		}
		return dv;
	}

	private PathMetadata getPathMetadata(String template, String path) {

		PathMetadata pm = new PathMetadata(template);

		pm.setColumn(Mapping.getColumnMap(pm.getTemplate()).get(path));
		String rmTypeName = "";
		if (path.matches(".*time.*")) {
			rmTypeName = ReferenceModelName.DV_DATE_TIME.getName();
		}
		;
		pm.setDataType(rmTypeName);
		pm.setNullValues(Mapping.nullMap.get(path));
		pm.setTerminologyName(Mapping.terminologyMap.get(path));
		pm.setUnit(Mapping.unitMap.get(path));
		pathMapping.putPathMapping(path, pm);

		return pm;
	}

	private void initiateContent(String template) {
		content = new HashMap<String, Object>();
		for (String path : Mapping.getColumnMap(template).keySet()) {
			content.put(path, null);
		}
	}

	private Boolean parseBoolean(String dbValue) {
		if (dbValue.toUpperCase().equals("")) {
			return null;
		} else if (dbValue.toUpperCase().equals("T")
				|| dbValue.toUpperCase().equals("TRUE")
				|| dbValue.toUpperCase().equals("Y")
				|| dbValue.toUpperCase().equals("YES")
				|| dbValue.toUpperCase().equals("V")
				|| dbValue.toUpperCase().matches("VER.*")
				|| dbValue.toUpperCase().equals("P")
				|| dbValue.toUpperCase().equals("POS.*")
				|| dbValue.toUpperCase().equals("S")
				|| dbValue.toUpperCase().equals("SIM")
				|| dbValue.toUpperCase().equals("1")) {
			return true;
		} else {
			return false;
		}
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

	private Double parseDouble(String dbValue) {
		return Double.parseDouble(dbValue.replaceAll(",", "."));
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

	private void updatePathState() {
		if (pathState != null && !pathState.isEmpty()) {
			for (String path : pathMapping.getPaths()) {
				boolean isNull = true;
				if (pathState.containsKey(path)) {
					if (pathState.get(path).isMapped()) {
						for (String mappedPath : content.keySet()) {
							if (elementToTree.containsKey(mappedPath)
									&& elementToTree.get(mappedPath).contains(
											path)
									&& content.get(mappedPath) != null) {
								isNull = false;
								break;
							}
						}
						pathState.put(path, new PathState(true, isNull));
					}
				} else {
					pathState.put(path, new PathState(true, false));
				}
			}
		} else {
			for (String path : pathMapping.getPaths()) {
				boolean isMapped = false;
				String regex = "^" + path + ".*$";
				regex = regex.replaceAll("\\[", "\\\\[");
				regex = regex.replaceAll("\\]", "\\\\]");
				for (String mappedPath : content.keySet()) {
					if (mappedPath.matches(regex)) {
						isMapped = true;
						if (elementToTree.containsKey(mappedPath)) {
							elementToTree.get(mappedPath).add(path);
						} else {
							List<String> elementTree = new ArrayList<String>();
							elementTree.add(path);
							elementToTree.put(mappedPath, elementTree);
						}
						break;
					}
				}
				pathState.put(path, new PathState(isMapped, true));
			}
		}
	}
}
