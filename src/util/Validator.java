package util;

public class Validator {

	public static boolean empty(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof Integer) {
			return false;
		} else if (obj instanceof String) {
			String str = String.valueOf(obj);
			return str.length() > 0 ? false : true;
		}
		return false;
	}

	public static boolean notEmpty(Object obj) {
		return !empty(obj);
	}
	
	public static boolean blank(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof Integer) {
			return false;
		} else if (obj instanceof String) {
			String str = String.valueOf(obj);
			return str.trim().length() > 0 ? false : true;
		}
		return false;
	}

	public static boolean notBlank(Object obj) {
		return !blank(obj);
	}
	
}
