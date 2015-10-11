package util;

import java.util.List;
import java.util.Map;

/*
 * 数据校验工具
 */
@SuppressWarnings("rawtypes")
public class Validator {

	/**
	 * 检验对象
	 * @param obj 对象
	 * @return 是否为空
	 */
	public static boolean empty(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof Integer) {
			return false;
		} else if (obj instanceof String) {
			String str = String.valueOf(obj);
			return str.length() > 0 ? false : true;
		} else if (obj instanceof List) {
			return ((List)obj).size() > 0 ? false : true;
		} else if (obj instanceof Map) {
			return ((Map)obj).size() > 0 ? false : true;
		}
		return false;
	}

	public static boolean notEmpty(Object obj) {
		return !empty(obj);
	}
	
	/**
	 * 检验对象
	 * @param obj 对象
	 * @return 是否为空
	 */
	public static boolean blank(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof Integer) {
			return false;
		} else if (obj instanceof String) {
			String str = String.valueOf(obj);
			return str.trim().length() > 0 ? false : true;
		} else if (obj instanceof List) {
			return ((List)obj).size() > 0 ? false : true;
		} else if (obj instanceof Map) {
			return ((Map)obj).size() > 0 ? false : true;
		}
		return false;
	}

	public static boolean notBlank(Object obj) {
		return !blank(obj);
	}
	
}
