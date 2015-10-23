package controller;

import static util.Validator.blank;
import static util.Validator.notBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.SnakerService;
import util.Json;

import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;

import engine.ModuleData;
import engine.Workflow;
import engine.model.WfRecord;
import engine.module.Module;

/*
 * 控制类 - 工作流相关
 */
public class WorkflowController extends Controller {

	/*
	 * snaker工作流程服务类
	 */
	private SnakerService snakerService = enhance(SnakerService.class);

	/*
	 * 返回到页面的json数据
	 */
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	
	/**
	 * 获取工作实例
	 * @param processId 工作流程主键
	 * @param instance 工作实例主键
	 */
	public void index() {
		Long processId = getParaToLong("process");
		Long instanceId = getParaToLong("instance");

		if (notBlank(processId) && notBlank(instanceId)) {
			Workflow workflow = snakerService.getWorkflow(processId, instanceId);
			setAttr("process", workflow.getProcessId());
			setAttr("processName", workflow.getProcessName());
			setAttr("instance", workflow.getInstanceId());
			setAttr("instanceParam", workflow.getInstanceParam());
			List<Map<String, Object>> views = new ArrayList<Map<String, Object>>();
			for (Module module : workflow.getModules()) {
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
			setAttr("views", JsonKit.toJson(views));
		}

		render("/workflow/all.html");
	}
	
	/**
	 * 运行工作流
	 * process 流程主键
	 * param 实例参数
	 */
	public void run() {
		Long processId = getParaToLong();

		dataMap.put("success", false);
		if (notBlank(processId)) {
			Workflow workflow = snakerService.runWorkflow(processId, "{}");
			if (workflow != null) {
				dataMap.put("success", true);
			}
		}
		renderJson(dataMap);
	}

	/**
	 * 获取工作流程实例
	 */
	public void list() {
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		for (Workflow workflow : snakerService.findWorkflow()) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("processId", workflow.getProcessId());
			m.put("processName", workflow.getProcessName());
			m.put("instanceId", workflow.getInstanceId());
			dataList.add(m);
		}
		renderJson(dataList);
	}
	
	/**
	 * 运行输出日志
	 * process 流程主键
	 * instance 实例参数
	 * record 记录主键
	 * module 模型名称
	 */
	public void record() {
		Long processId = getParaToLong("process");
		Long instanceId = getParaToLong("instance");
		Long recordId = getParaToLong("record");
		String module = getPara("module");
		Integer page = getParaToInt("page");
		Boolean refresh = getParaToBoolean("refresh");

		if (notBlank(instanceId) && notBlank(module)) {
			if (blank(page) || page < 1)
				page = 1;
			if (blank(refresh))
				refresh = true;
			WfRecord record = WfRecord.dao.findFirst("select * from wf_record where instance_id=? and module=? limit ?,1", instanceId, module, page - 1);
			List<Map<String, Object>> rows = Json.parseToList(record.getStr("rows"));
			for (Map<String, Object> row : rows) {
				for (String key : row.keySet()) {
					Object value = row.get(key);
					if (notBlank(value) && value instanceof String) {
						String str = (String) value;
						if (str.length() > 100) {
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

		renderJson(Json.toString(dataMap));
	}
	
}
