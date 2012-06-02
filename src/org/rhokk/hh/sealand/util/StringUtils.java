package org.rhokk.hh.sealand.util;


public class StringUtils {

	public static boolean isNotBlank(String string) {
		return !isBlank( string );
	}

	private static boolean isBlank(String string) {
		return string == null || "".equals( string.trim() );
	}

}
