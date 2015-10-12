package controller;

import static util.Validator.blank;
import static util.Validator.notBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.SnakerService;

import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;

import engine.Workflow;
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
			dataMap.put("name", workflow.getName());
			List<Map<String, Object>> views = new ArrayList<Map<String, Object>>();
			for (Module module : workflow.getModules()) {
				Map<String, Object> view = new HashMap<String, Object>();
				view.put("mtype", module.getMtype());
				view.put("name", module.getName());
				view.put("controller", module.getController());
				views.add(view);
			}
			dataMap.put("views", views);
		}

		setAttr("workflow", JsonKit.toJson(dataMap));
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
		if (blank(processId))
			dataMap.put("success", snakerService.runWorkflow(processId, param));

		renderJson(dataMap);
	}

}
