package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

/*
 * JSON操作工具
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Json {

	public static String toString(Object object) {
		if (object instanceof List) {
			return JSONArray.fromObject(object).toString();
		} else {
			return JSONObject.fromObject(object).toString();
		}
	}
	
	/**
	 * json格式字符串转Map对象
	 * @param jsonString json格式字符串
	 * @return Map对象
	 */
	public static Map parseToMap(String jsonString) {
		JSONObject jsonObject  = JSONObject.fromObject(jsonString);
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		for (Object key : jsonObject.keySet()) {
			Object value  = jsonObject.get(key);
			if (value instanceof JSONNull) {
				jsonMap.put(key.toString(), null);
			} else if (value instanceof JSONArray) {
				List<Object> valueList = new ArrayList<Object>();
				Iterator<Object> valueIterator = ((JSONArray) value).iterator();
				while (valueIterator.hasNext()) {
					Object valueObject = valueIterator.next();
					if (value instanceof JSONNull) {
						valueList.add(null);
					} else if (valueObject instanceof JSONObject) {
						valueList.add(parseToMap((JSONObject) valueObject));
					} else {
						valueList.add(valueObject);
					}

				}
				jsonMap.put(key.toString(), valueList);
			} else {
				jsonMap.put(key.toString(), value);
			}
		}
		return jsonMap;
	}
	
	/**
	 * JSONObject对象转Map对象
	 * @param jsonObject JSONObject对象
	 * @return Map对象
	 */
	public static Map parseToMap(JSONObject jsonObject) {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		for (Object key : jsonObject.keySet()) {
			Object value = jsonObject.get(key);
			if (value instanceof JSONNull) {
				jsonMap.put(key.toString(), null);
			} else if (value instanceof JSONArray) {
				List<Object> valueList = new ArrayList<Object>();
				Iterator<Object> valueIterator = ((JSONArray) value).iterator();
				while (valueIterator.hasNext()) {
					Object valueObject = valueIterator.next();
					if (value instanceof JSONNull) {
						valueList.add(null);
					} else if (valueObject instanceof JSONObject) {
						valueList.add(parseToMap((JSONObject) valueObject));
					} else {
						valueList.add(valueObject);
					}

				}
				jsonMap.put(key.toString(), valueList);
			} else {
				jsonMap.put(key.toString(), value);
			}
		}
		return jsonMap;
	}
	
	public static List parseToList(String jsonString) {
		JSONArray jsonArray  = JSONArray.fromObject(jsonString);
		List<Object> jsonList = new ArrayList<Object>();
		Iterator<Object> jsonIterator = jsonArray.iterator();
		while (jsonIterator.hasNext()) {
			Object object = jsonIterator.next();
			if (object instanceof JSONNull) {
				jsonList.add(null);
			} else if (object instanceof JSONObject) {
				jsonList.add(parseToMap((JSONObject) object));
			} else {
				jsonList.add(object);
			}
		}
		return jsonList;
	}
	
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
