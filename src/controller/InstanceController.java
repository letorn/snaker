package controller;

import static util.Validator.notBlank;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.SnakerService;
import util.Json;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import engine.ModuleData;
import engine.Workflow;
import engine.module.Module;

/*
 * 控制类 - 流程实例相关
 */
public class InstanceController extends Controller {

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/*
	 * snaker工作流程服务类
	 */
	private SnakerService snakerService = enhance(SnakerService.class);
	
	/*
	 * 返回到页面的json数据
	 */
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	private List<Object> dataList = new ArrayList<Object>();
	
	/**
	 * 列表
	 * page 页码
	 * rows 每页多少条记录
	 * name 工作流程名称
	 */
	public void index() {
		Integer page = getParaToInt("page", 1);
		Integer rows = getParaToInt("rows", 30);
		String name = getPara("name", "");
		if (page < 1) page = 1;
		if (rows < 1) rows = 1;

		Page<Workflow> pager = snakerService.findInstance(page, rows, name);
		for (Workflow instance : pager.getList()) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("processId", instance.getProcessId());
			data.put("processName", instance.getProcessName());
			data.put("instanceId", instance.getInstanceId());
			data.put("instanceParams", instance.getInstanceParams());
			data.put("instanceCreateDate", dateFormat.format(instance.getInstanceCreateDate()));
			dataList.add(data);
		}
		dataMap.put("total", pager.getTotalRow());
		dataMap.put("rows", dataList);
		renderJson(dataMap);
	}
	
	/**
	 * 获取流程实例
	 * {instanceId} 流程实例主键
	 */
	public void all() {
		Long instanceId = getParaToLong();

		if (notBlank(instanceId)) {
			Workflow instance = snakerService.getInstance(instanceId);
			if (notBlank(instance)) {
				setAttr("process", instance.getProcessId());
				setAttr("processName", instance.getProcessName());
				setAttr("instance", instance.getInstanceId());
				setAttr("instanceParams", instance.getInstanceParams());
				List<Map<String, Object>> views = new ArrayList<Map<String, Object>>();
				for (Module module : instance.getModules()) {
					if (notBlank(module.getController())) {
						Map<String, Object> view = new HashMap<String, Object>();
						view.put("mtype", module.getMtype());
						view.put("name", module.getName());
						view.put("form", module.getController());
						views.add(view);
					}
					if (module.isDoRecord() && notBlank(module.getRecordView())) {
						Map<String, Object> view = new HashMap<String, Object>();
						view.put("mtype", module.getMtype());
						view.put("name", module.getName());
						view.put("form", module.getRecordView());
						views.add(view);
					}
				}
				setAttr("views", Json.toString(views));
			}
		}

		render("/instance/all.html");
	}
	
	/**
	 * 运行输出日志
	 * instance 实例参数
	 * module 模型名称
	 * page 页码
	 * refresh 是否刷新表头
	 */
	public void record() {
		Long instanceId = getParaToLong("instance");
		String module = getPara("module");
		Integer page = getParaToInt("page", 1);
		Integer rows = getParaToInt("rows", 30);
		Boolean refresh = getParaToBoolean("refresh", true);
		if (page < 1) page = 1;
		if (rows < 1) rows = 1;

		if (notBlank(instanceId) && notBlank(module)) {
			ModuleData records = snakerService.getRecords(instanceId, module);
			if (notBlank(records)) {
				int fromIndex = (page - 1) * rows;
				int toIndex = fromIndex + rows;
				if (toIndex > records.getRows().size()) toIndex = records.getRows().size();

				List<Map<String, Object>> data = records.getRows().subList(fromIndex, toIndex);
				for (Map<String, Object> d : data) {
					for (String key : d.keySet()) {
						Object value = d.get(key);
						if (notBlank(value) && value instanceof String) {
							String str = (String) value;
							if (str.length() > 1000)
								d.put(key, str.substring(1000) + "...");
						}
					}
				}
				dataMap.put("rows", data);
				if (refresh) {
					dataMap.put("headers", records.getHeaders());
					dataMap.put("total", records.getRows().size());
				}
			}
		}

		renderJson(Json.toString(dataMap));
	}
	
}
