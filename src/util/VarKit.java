package util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import model.SkConfig;

/*
 * 全局系统变量
 */
public class VarKit {
	
	private static final Map<String, String> vars = new ConcurrentHashMap<String, String>();

	/**
	 * 初始化
	 * 从数据库取值，同时保存到缓存
	 */
	public static void init() {
		List<SkConfig> configs = SkConfig.dao.find("select name,value from sk_config");
		for (SkConfig config : configs) {
			vars.put(config.getStr("name"), config.getStr("value"));
		}
	}
	
	/**
	 * 保存值，同时更新到数据库
	 * @param name 参数名
	 * @param value 参数值
	 * @return 是否保存成功
	 */
	public static boolean set(String name, String value) {
		SkConfig config = SkConfig.dao.findFirst("select * from sk_config where name=?", name);
		if (config != null) {
			config.set("value", value);
			if (config.update()) {
				vars.put(name, value);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取值
	 * @param name 参数名
	 * @return
	 */
	public static String get(String name) {
		return vars.get(name);
	}
	
	/**
	 * 获取值
	 * @param name 参数名
	 * @return
	 */
	public static Integer getInt(String name) {
		return Integer.parseInt(vars.get(name));
	}
	
	/**
	 * 获取值
	 * @param name 参数名
	 * @return
	 */
	public static Long getLong(String name) {
		return Long.parseLong(vars.get(name));
	}
	
	/**
	 * 获取值
	 * @param name 参数名
	 * @return
	 */
	public static Boolean getBoolean(String name) {
		return Boolean.parseBoolean(vars.get(name));
	}

	/**
	 * 获取所有
	 * @return
	 */
	public static Map<String, String> getAll() {
		return vars;
	}
	
}
