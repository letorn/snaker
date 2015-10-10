package controller;

import static util.Validator.blank;
import static util.Validator.notBlank;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.snaker.engine.entity.Order;
import org.snaker.engine.entity.Process;
import org.snaker.engine.entity.Task;
import org.snaker.engine.model.TaskModel;

import service.SnakerService2;

import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;

public class FlowController extends Controller {

	/*
	 * snaker工作流程服务类
	 */
	private SnakerService2 snakerService = enhance(SnakerService2.class);

	/*
	 * 返回到页面的json数据
	 */
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	
	/**
	 * 工作流程处理：
	 * process 流程主键
	 * order 实例主键
	 * task 任务主键
	 * 表单数据类型：S字符型，I整形，L常整形，B布尔型，D日期型，F浮点型
	 */
	public void process() {
		String processId = getPara("process");
		String orderId = getPara("order");
		String taskId = getPara("task");
		Map<String, Object> params = new HashMap<String, Object>();
		Enumeration<String> paraNames = getRequest().getParameterNames();
		while (paraNames.hasMoreElements()) {
			String element = paraNames.nextElement();
			int index = element.indexOf("_");
			if (index == -1) {
				params.put(element, getPara(element));
			} else {
				char type = element.charAt(0);
				String name = element.substring(index + 1);
				Object value = null;
				switch (type) {
				case 'S':
					value = getPara(element);
					break;
				case 'I':
					value = getParaToInt(element);
					break;
				case 'L':
					value = getParaToLong(element);
					break;
				case 'B':
					value = getParaToBoolean(element);
					break;
				case 'D':
					value = getParaToDate(element);
					break;
				case 'F':
					value = Double.parseDouble(getPara(element));
					break;
				default:
					value = getPara(element);
					break;
				}
				params.put(name, value);
			}
		}

		if (blank(orderId) && blank(taskId)) {
			Order order = snakerService.startAndExecute(processId, null, params);
			dataMap.put("success", order != null ? true : false);
		} else {
			List<Task> tasks = snakerService.execute(taskId, null, params);
			dataMap.put("success", tasks != null ? true : false);
		}

		renderJson(dataMap);
	}

	/**
	 * 获取工作实例
	 * @param processId 工作流程主键
	 * @param orderId 工作实例主键
	 * @param taskId 活动任务主键
	 */
    public void all() {
    	String processId = getPara("process");
		String orderId = getPara("order");
		String taskId = getPara("task");

    	if (notBlank(processId)) {
    		Process process = snakerService.getProcessById(processId);
    		dataMap.put("process", process);
    		
    		List<TaskModel> taskModels = process.getModel().getModels(TaskModel.class);
    		List<Map<String, Object>> views = new ArrayList<Map<String,Object>>();
    		for (TaskModel taskModel : taskModels) {
    			Map<String, Object> view = new HashMap<String, Object>();
    			view.put("name", taskModel.getName());
    			view.put("displayName", taskModel.getDisplayName());
    			view.put("form", taskModel.getForm());
    			views.add(view);
    		}
    		dataMap.put("views", views);
    	}
    	if (notBlank(processId)) 
    		dataMap.put("order", snakerService.getOrderById(orderId));
    	if (notBlank(processId)) 
    		dataMap.put("task", snakerService.getTaskById(taskId));

    	setAttr("dataMap", JsonKit.toJson(dataMap));
    	render("/flow/all.html");
    }

}
