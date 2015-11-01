package controller;

import static util.Validator.notBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.SnakerService;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;

import engine.Workflow;
import engine.module.Module;

/*
 * 控制类 - 工作流程相关
 */
public class ProcessController extends Controller{

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
	 * 获取工作流程，可以根据以下条件过滤
	 * name 流程名称
	 */
	public void index() {
		String name = getPara("name");

		for (Workflow workflow : snakerService.findProcess(name)) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("id", workflow.getProcessId());
			m.put("name", workflow.getProcessName());
			m.put("content", workflow.getProcessContent());
			dataList.add(m);
		}
		
		renderJson(dataList);
	}
	
	/**
	 * 导入默认的工作流程
	 * 目录：/src/flows
	 */
	public void init() {
		dataMap.put("success", snakerService.initFlows());

		renderJson(dataMap);
	}
	
	/**
	 * 工作流程编辑
	 * {process} 流程主键
	 */
	public void edit() {
		Long processId = getParaToLong();

		if (notBlank(processId)) {
			Workflow process = snakerService.getProcess(processId);
			if (notBlank(process)) {
				dataMap.put("success", true);
				dataMap.put("process", process.getProcessId());
				dataMap.put("content", process.getProcessContent());
			}
		}

		renderJson(dataMap);
	}
	
	/**
	 * 工作流程保存
	 * process 流程主键
	 * content 流程XML内容
	 */
	public void save() {
		Long processId = getParaToLong("process");
		String content = getPara("content");

		if (notBlank(content)) {
			Workflow process = snakerService.saveProcesss(processId, content);
			if (notBlank(process)) {
				dataMap.put("success", true);
				dataMap.put("process", process.getProcessId());
			}
		}

		renderJson(dataMap);
	}
	
	/**
	 * 工作流程删除
	 * {process} 流程主键
	 */
	public void delete() {
		Long processId = getParaToLong();

		if (notBlank(processId))
			dataMap.put("success", snakerService.deleteProcess(processId));

		renderJson(dataMap);
	}
	
	/**
	 * 工作流程设计
	 */
	public void designer() {
		Long processId = getParaToLong();

		setAttr("process", processId);
		if (notBlank(processId)) {
			Workflow process = snakerService.getProcess(processId);
			if (notBlank(process)) {
				setAttr("content", process.getProcessContent());
			}
		}
		render("designer.html");
	}
	
	/**
	 * 运行工作流程
	 * process 流程主键
	 * daemon 后台运行
	 * params 实例参数
	 */
	public void start() {
		Long processId = getParaToLong();
		Boolean daemon = getParaToBoolean("daemon", false);
		String params = getPara("params", "{}");

		if (notBlank(processId)) {
			Workflow instance = snakerService.startProcess(processId, params, daemon);
			if (instance != null) {
				dataMap.put("success", true);
				dataMap.put("process", instance.getProcessId());
				dataMap.put("instance", instance.getInstanceId());
			}
		}
		renderJson(dataMap);
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
	 * 工作流程运行参数
	 * process 流程主键
	 */
	public void params() {
		Long processId = getParaToLong();

		if (notBlank(processId)) {
			Workflow process = snakerService.getProcess(processId);
			for (Module module : process.getModules()) {
				if (notBlank(module.getParams())) {
					Map<String, Object> row = new HashMap<String, Object>();
					row.put("module", module.getName());
					row.put("params", module.getParams());
					dataList.add(row);
				}
			}
		}
		renderJson(dataList);
	}
	
}
