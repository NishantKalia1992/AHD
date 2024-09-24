package com.all.homedesire.common;

import java.util.Set;



/**
 * @author Arun Kumar Tiwari 
 * {@summary This class is to take care validations required
 *         with application.} 
 */
public class RestValidation {

	public static boolean isTextData(String data) {
		boolean result = false;
		if (data != null && data.trim().length() > 0) {
			result = true;
		}
		return result;
	}

	public static boolean isLongData(long data) {
		boolean result = false;
		if (data > 0) {
			result = true;
		}
		return result;
	}

	public static boolean isDoubleData(double data) {
		boolean result = false;
		if (data > 0) {
			result = true;
		}
		return result;
	}
	
	public static boolean isBooleanData(Boolean data) {
		boolean result = false;
		if (data != null) {
			result = true;
		}
		return result;
	}

	public static boolean isSetCollection(Set<Long> datas) {
		boolean result = false;
		if (datas != null && datas.size() > 0) {
			result = true;
		}
		return result;
	}
}
