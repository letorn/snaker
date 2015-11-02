package controller;

import static util.Validator.notBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.SnakerService;
import util.Json;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import engine.Workflow;
import engine.model.WfRecord;
import engine.module.Module;

/*
 * 控制类 - 流程实例相关
 */
@SuppressWarnings("unchecked")
public class InstanceController extends Controller {

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
		
		Page<Workflow> pager = snakerService.findInstance(page, rows, name);
		for (Workflow instance : pager.getList()) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("processId", instance.getProcessId());
			row.put("processName", instance.getProcessName());
			row.put("instanceId", instance.getInstanceId());
			dataList.add(row);
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
		Boolean refresh = getParaToBoolean("refresh", true);
		
		if (notBlank(instanceId) && notBlank(module)) {
			if (page <= 0)
				page = 1;
			WfRecord record = WfRecord.dao.findFirst("select * from wf_record where instance_id=? and module=? limit ?,1", instanceId, module, page - 1);
			if (notBlank(record)) {
				List<Map<String, Object>> rows = Json.parseToList(record.getStr("rows"));
				for (Map<String, Object> row : rows) {
					for (String key : row.keySet()) {
						Object value = row.get(key);
						if (notBlank(value) && value instanceof String) {
							String str = (String) value;
							if (str.length() > 1000) {
								row.put(key, str.substring(1000) + "...");
							}
						}
					}
				}
				dataMap.put("rows", Json.parseToList(record.getStr("rows")));
				if (refresh) {
					dataMap.put("headers", Json.parseToList(record.getStr("headers")));
					dataMap.put("pageTotal", Db.queryLong("select count(*) from wf_record where instance_id=? and module=?", instanceId, module));
				}
			}
		}

		renderJson(Json.toString(dataMap));
	}
	
}
