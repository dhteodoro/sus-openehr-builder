package br.uerj.lampada.openehr.susbuilder.mapping;

import java.util.ArrayList;
import java.util.List;

public class PathMetadata {
	// private String path;
	private Object column; // a single o list of columns mapped to a path
	private String dataType;
	private List<String> nullValues;
	private String template;
	private String terminologyName;
	private String unit;

	public PathMetadata(String template) {
		nullValues = new ArrayList<String>();
		this.setTemplate(template);
	}

	public Object getColumn() {
		return column;
	}

	public String getDataType() {
		return dataType;
	}

	public List<String> getNullValues() {
		return nullValues;
	}

	public String getTemplate() {
		return template;
	}

	public String getTerminologyName() {
		return terminologyName;
	}

	public String getUnit() {
		return unit;
	}

	public void setColumn(Object column) {
		this.column = column;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void setNullValues(List<String> nullValues) {
		this.nullValues = nullValues;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public void setTerminologyName(String terminologyName) {
		this.terminologyName = terminologyName;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
