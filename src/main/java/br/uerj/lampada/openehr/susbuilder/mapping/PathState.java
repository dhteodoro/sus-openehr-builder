package br.uerj.lampada.openehr.susbuilder.mapping;

/**
 * Auxiliar class to store the state of path: - mapped to a column - null value
 * 
 * @author teodoro
 * 
 */
public class PathState {

	private boolean isMapped;
	private boolean isNull;

	public PathState(boolean isMapped, boolean isNull) {
		this.isMapped = isMapped;
		this.isNull = isNull;
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
