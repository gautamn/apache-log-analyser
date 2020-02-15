package com.nayaka.loganalyser.enums;

public enum UTILITY {

	ANALYSE_LOGS("ANALYSE_LOGS");

	private final String name;

	private UTILITY(String s) {
		name = s;
	}

	public boolean equalsName(String otherName) {
		return name.equals(otherName);
	}

	public String toString() {
		return this.name;
	}
}