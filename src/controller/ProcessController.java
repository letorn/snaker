package controller;

import static util.Validator.notBlank;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.snaker.engine.entity.Process;

import service.SnakerService2;

import com.jfinal.core.Controller;

public class ProcessController extends Controller{

	/*
	 * snaker工作流程服务类
	 */
	private SnakerService2 snakerService = enhance(SnakerService2.class);

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
	 * displayName 显示名称
	 * state 流程状态
	 */
	public void list() {
		String name = getPara("name");
		String displayName = getPara("displayName");
		Integer state = getParaToInt("state");
		String processType = getPara("processType");

		List<Process> processes = snakerService.findProcess(name, displayName, state, processType);

		renderJson(processes);
	}
	
	/**
	 * 工作流程编辑
	 * {process} 流程主键
	 */
	public void edit() {
		String processId = getPara();

		dataMap.put("success", false);
		if (notBlank(processId)) {
			Process process = snakerService.getProcessById(processId);
			if (notBlank(process)) {
				dataMap.put("success", true);
				dataMap.put("process", process.getId());
				if (notBlank(process.getDBContent())) {
					try {
						dataMap.put("content", new String(process.getDBContent(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
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
		String processId = getPara("process");
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
		String processId = getPara();

		dataMap.put("success", false);
		if (notBlank(processId))
			dataMap.put("success", snakerService.deleteProcess(processId));

		renderJson(dataMap);
	}
	
	/**
	 * 工作流程设计
	 */
	public void designer() {
		render("designer.html");
	}
	
}
