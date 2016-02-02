package controller;

import java.util.HashMap;
import java.util.Map;

import util.FileKit;
import util.VarKit;

import com.jfinal.core.Controller;

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
	
	public void vars() {
		renderJson(VarKit.getAll());
	}
	
	public void save() {
		String name = getPara("name", "");
		String value = getPara("value", "");
		dataMap.put("success", VarKit.set(name, value));
		renderJson(dataMap);
	}
	
	public void pathsize() {
		dataMap.put("datalog", FileKit.getWebPathSize("/log/data"));
		dataMap.put("servicelog", FileKit.getWebPathSize("/log/service"));
		dataMap.put("upload", FileKit.getWebPathSize("/upload"));
		dataMap.put("download", FileKit.getWebPathSize("/download"));
		renderJson(dataMap);
	}
	
	public void deletepath() {
		String path = getPara();
		if ("datalog".equals(path)) dataMap.put("success", FileKit.deleteWebPath("/log/data"));
		else if ("servicelog".equals(path)) dataMap.put("success", FileKit.deleteWebPath("/log/service"));
		else if ("upload".equals(path)) dataMap.put("success", FileKit.deleteWebPath("/upload"));
		else if ("download".equals(path)) dataMap.put("success", FileKit.deleteWebPath("/download"));
		renderJson(dataMap);
	}
	
	public void delete() {
		// String name = getPara("name", "");
	}
	
}
