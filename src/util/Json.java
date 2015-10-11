package util;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
 * JSON操作工具
 */
public class Json {

	/**
	 * 提取
	 * @param jsonObject json对象
	 * @param key 关键字
	 * @return json对象
	 */
	public static JSONObject catchJSONObject(JSONObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			Object value = jsonObject.get(key);
			if (value instanceof JSONObject) {
				return (JSONObject) value;
			}
		}
		return null;
	}

	/**
	 * 提取
	 * @param jsonObject json对象
	 * @param key 关键字
	 * @return array对象
	 */
	public static JSONArray catchJSONArray(JSONObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			Object value = jsonObject.get(key);
			if (value instanceof JSONArray) {
				return (JSONArray) value;
			}
		}
		return null;
	}

	/**
	 * 提取
	 * @param jsonObject json对象
	 * @param key 关键字
	 * @return 字符串
	 */
	public static String catchString(JSONObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			Object value = jsonObject.get(key);
			if (value instanceof String) {
				return (String) value;
			}
		}
		return null;
	}

	/**
	 * 提取
	 * @param jsonObject json对象
	 * @param key 关键字
	 * @return 字符串数据
	 */
	public static String[] catchStrings(JSONObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			Object value = jsonObject.get(key);
			if (value instanceof JSONArray) {
				Object[] objects = ((JSONArray) value).toArray();
				List<String> values = new ArrayList<String>();
				for (Object object : objects) {
					values.add(object.toString());
				}
				return values.toArray(new String[] {});
			}
		}
		return null;
	}

	/**
	 * 提取
	 * @param jsonObject json对象
	 * @param key 关键字
	 * @return 整形
	 */
	public static Integer catchInt(JSONObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			Object value = jsonObject.get(key);
			if (value instanceof Number) {
				return (Integer) value;
			}
		}
		return null;
	}

	/**
	 * 提取
	 * @param jsonObject json对象
	 * @param key 关键字
	 * @return 长整形
	 */
	public static Long catchLong(JSONObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			Object value = jsonObject.get(key);
			if (value instanceof Number) {
				return (Long) value;
			}
		}
		return null;
	}

}
