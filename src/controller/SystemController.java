package controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.SkConfig;
import util.FileKit;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;

/*
 * 控制类 - 系统相关
 */
public class SystemController extends Controller {

	/*
	 * 返回到页面的json数据
	 */
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	// private List<Object> dataList = new ArrayList<Object>();

	/**
	 * 系统配置
	 */
	public void index() {
		render("/index.html");
	}
	
	public void config() {
		List<SkConfig> configs = SkConfig.dao.find("select name,value from sk_config");
		for (SkConfig config : configs) dataMap.put(config.getStr("name"), config.getStr("value")); 
		renderJson(dataMap);
	}
	
	public void save() {
		String name = getPara("name", "");
		String value = getPara("value", "");
		Db.update("update sk_config set value=? where name=?", value, name);
		dataMap.put("success", true);
		renderJson(dataMap);
	}
	
	public void logsize() {
		dataMap.put("datalog", FileKit.getWebPathSize("/common"));
		dataMap.put("servicelog", FileKit.getWebPathSize("/common"));
		renderJson(dataMap);
	}
	
}
