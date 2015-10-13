package controller;

import static util.Validator.blank;
import static util.Validator.notBlank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.SnakerService;

import com.jfinal.core.Controller;

import engine.model.WfProcess;

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
	
	/**
	 * 导入默认的工作流程
	 * 目录：/src/flows
	 */
	public void init() {
		dataMap.put("success", snakerService.initFlows());

		renderJson(dataMap);
	}
	
	/**
	 * 获取工作流程，可以根据以下条件过滤
	 * name 流程名称
	 */
	public void list() {
		String name = getPara("name");

		if (blank(name))
			name = "";
		List<WfProcess> processes = snakerService.findProcess("%" + name + "%");

		renderJson(processes);
	}
	
	/**
	 * 工作流程编辑
	 * {process} 流程主键
	 */
	public void edit() {
		Long processId = getParaToLong();

		dataMap.put("success", false);
		if (notBlank(processId)) {
			WfProcess process = snakerService.getProcess(processId);
			if (notBlank(process)) {
				dataMap.put("success", true);
				dataMap.put("process", process.getLong("id"));
				dataMap.put("content", process.getStr("content"));
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

		dataMap.put("success", false);
		if (notBlank(content))
			dataMap.put("success", snakerService.saveProcesss(processId, content));

		renderJson(dataMap);
	}
	
	/**
	 * 工作流程删除
	 * {process} 流程主键
	 */
	public void delete() {
		Long processId = getParaToLong();

		dataMap.put("success", false);
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
			WfProcess process = snakerService.getProcess(processId);
			if (notBlank(process)) {
				setAttr("content", process.getStr("content"));
			}
		}
		render("designer.html");
	}
	
}
