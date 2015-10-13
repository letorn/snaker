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

		if (notBlank(processId)) {
			Workflow workflow = snakerService.getWorkflow(processId, instanceId);
			setAttr("processId", workflow.getProcessId());
			setAttr("processName", workflow.getProcessName());
			List<Map<String, Object>> views = new ArrayList<Map<String, Object>>();
			for (Module module : workflow.getModules()) {
				if (notBlank(module.getController())) {
					Map<String, Object> view = new HashMap<String, Object>();
					view.put("mtype", module.getMtype());
					view.put("name", module.getName());
					view.put("form", module.getController());
					views.add(view);
				}
				if (notBlank(module.getView())) {
					Map<String, Object> view = new HashMap<String, Object>();
					view.put("mtype", module.getMtype());
					view.put("name", module.getName());
					view.put("form", module.getView());
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
		Long processId = getParaToLong("process");
		String param = getPara("param");

		dataMap.put("success", false);
		if (notBlank(processId)) {
			Workflow workflow = snakerService.runWorkflow(processId, param);
			if (workflow != null) {
				dataMap.put("success", true);
				dataMap.put("instanceId", workflow.getInstanceId());
			}
		}
		renderJson(dataMap);
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

		ModuleData moduleData = new ModuleData();
		List<WfRecord> records = WfRecord.dao.find("select * from wf_record where instance_id=? and module=?", instanceId, module);
		for (WfRecord record : records) {
			List<Map<String, Object>> rows = (List<Map<String, Object>>) Json.parseToList(record.getStr("rows"));
			moduleData.addAll(rows);
		}
		renderJson(Json.toString(moduleData));
	}
	
}
