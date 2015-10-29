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
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

import engine.Workflow;
import engine.model.WfRecord;
import engine.module.Module;

/*
 * 控制类 - 工作流相关
 */
@SuppressWarnings("unchecked")
public class WorkflowController extends Controller {

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
	 * 获取工作流程实例
	 */
	public void index() {
		Integer page = getParaToInt("page");
		if (blank(page))
			page = 1;
		Integer rows = getParaToInt("rows");
		if (blank(rows))
			rows = 30;
		Page<Workflow> pager = snakerService.findInstance(page, rows);
		for (Workflow workflow : pager.getList()) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("processId", workflow.getProcessId());
			m.put("processName", workflow.getProcessName());
			m.put("instanceId", workflow.getInstanceId());
			dataList.add(m);
		}
		dataMap.put("total", pager.getTotalRow());
		dataMap.put("rows", dataList);
		renderJson(dataMap);
	}
	
	/**
	 * 获取工作实例
	 * @param processId 工作流程主键
	 * @param instance 工作实例主键
	 */
	public void all() {
		Long instanceId = getParaToLong("instance");

		if (notBlank(instanceId)) {
			Workflow workflow = snakerService.getInstance(instanceId);
			setAttr("process", workflow.getProcessId());
			setAttr("processName", workflow.getProcessName());
			setAttr("instance", workflow.getInstanceId());
			setAttr("instanceParams", workflow.getInstanceParams());
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
	 * params 实例参数
	 */
	public void start() {
		Long processId = getParaToLong();
		Boolean daemon = getParaToBoolean("daemon");
		if (blank(daemon))
			daemon = false;
		String params = getPara("params");
		if (blank(params))
			params = "";
		if (notBlank(processId)) {
			Workflow workflow = snakerService.startProcess(processId, params, daemon);
			if (workflow != null) {
				dataMap.put("success", true);
				dataMap.put("process", workflow.getProcessId());
				dataMap.put("instance", workflow.getInstanceId());
			}
		}
		renderJson(dataMap);
	}

	/**
	 * 工作流运行参数
	 * process 流程主键
	 */
	public void params() {
		Long processId = getParaToLong();

		if (notBlank(processId)) {
			Workflow workflow = snakerService.getProcess(processId);
			for (Module module : workflow.getModules()) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("module", module.getName());
				m.put("params", module.getParams());
				dataList.add(m);
			}
		}
		renderJson(dataList);
	}
	
	/**
	 * 上传文件
	 * file 文件
	 */
	public void upload() {
		UploadFile uploadFile = getFile("file");
		dataMap.put("success", true);
		dataMap.put("file", uploadFile.getFileName());
		dataMap.put("path", uploadFile.getSaveDirectory() + uploadFile.getFileName());
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
		Long instanceId = getParaToLong("instance");
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

		renderJson(Json.toString(dataMap));
	}
	
}
