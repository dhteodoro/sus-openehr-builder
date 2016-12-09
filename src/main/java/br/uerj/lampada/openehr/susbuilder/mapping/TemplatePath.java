package br.uerj.lampada.openehr.susbuilder.mapping;

public class TemplatePath {

	// private String path;
	private boolean isMapped;

	// public String getPath() {
	// return path;
	// }
	// public void setPath(String path) {
	// this.path = path;
	// }

	private boolean isNull;

	// public String hasParent;
	// public String hasChild;

	public TemplatePath(boolean isMapped, boolean isNull) {
		// this.setPath(path);
		this.setMapped(isMapped);
		this.setNull(isNull);
	}

	public boolean isMapped() {
		return isMapped;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setMapped(boolean isMapped) {
		this.isMapped = isMapped;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}
}
