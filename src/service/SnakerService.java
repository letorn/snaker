package service;

import static util.Validator.notBlank;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.snaker.engine.SnakerEngine;
import org.snaker.engine.access.Page;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.entity.HistoryTask;
import org.snaker.engine.entity.Order;
import org.snaker.engine.entity.Process;
import org.snaker.engine.entity.Surrogate;
import org.snaker.engine.entity.Task;
import org.snaker.engine.helper.StreamHelper;
import org.snaker.engine.model.TaskModel.TaskType;
import org.snaker.jfinal.plugin.SnakerPlugin;

public class SnakerService {
	
	private SnakerEngine engine = SnakerPlugin.getEngine();
	
	/**
	 * 工作流程初始化
	 * @return
	 */
	public boolean initFlows() {
		try {
			engine.process().deploy(StreamHelper.getStreamFromClasspath("flows/leave.snaker"));
			engine.process().deploy(StreamHelper.getStreamFromClasspath("flows/borrow.snaker"));
			engine.process().deploy(StreamHelper.getStreamFromClasspath("flows/mytest.xml"));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 获取工作流程
	 * @param name 流程名称
	 * @param displayName 流程显示名称
	 * @param state 流程状态
	 * @param processType 流程类型
	 * @return
	 */
	public List<Process> findProcess(String name, String displayName, Integer state, String processType) {
		QueryFilter filter = new QueryFilter();
		if (notBlank(name))
			filter.setName(name);
		if (notBlank(displayName))
			filter.setDisplayName(displayName);
		if (notBlank(state))
			filter.setState(state);
		if (notBlank(processType))
			filter.setProcessType(processType);
		return engine.process().getProcesss(filter);
	}
	
	/**
	 * 获取工作流程
	 * @param processId 流程主键
	 * @return
	 */
	public Process getProcessById(String processId) {
		return engine.process().getProcessById(processId);
	}
	
	/**
	 * 保存工作流程：有主键就更新，没我键就新增
	 * @param processId 流程主键
	 * @param content 流程XML内容
	 * @return
	 */
	public boolean saveProcesss(String processId, String content) {
		try (InputStream contentStream = new ByteArrayInputStream(content.getBytes("UTF-8"))) {
			if (notBlank(processId))
				engine.process().redeploy(processId, contentStream);
			else
				engine.process().deploy(contentStream);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 删除工作流程：并不是真正的删除，只是状态改为禁止
	 * @param processId 流程主键
	 * @return
	 */
	public boolean deleteProcess(String processId) {
		try {
			engine.process().undeploy(processId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 获取流程实例
	 * @param orderId 实例主键
	 * @return
	 */
	public Order getOrderById(String orderId) {
		return engine.query().getOrder(orderId);
	}
	
	/**
	 * 获取活动任务
	 * @param taskId 任务主键
	 * @return
	 */
	public Task getTaskById(String taskId) {
		return engine.query().getTask(taskId);
	}
	
	/**
	 * 启程并执行工作流程
	 * @param processId 流程主键
	 * @param operator 启动者
	 * @param params 任务变量
	 * @return
	 */
	public Order startAndExecute(String processId, String operator, Map<String, Object> params) {
		try {
			Order order = engine.startInstanceById(processId, operator, params);
			List<Task> tasks = engine.query().getActiveTasks(new QueryFilter().setOrderId(order.getId()));
			List<Task> newTasks = new ArrayList<Task>();
			if(tasks != null && tasks.size() > 0) {
				Task task = tasks.get(0);
				newTasks.addAll(engine.executeTask(task.getId(), operator, params));
			}
			return order;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 执行工作流程
	 * @param taskId 任务主键
	 * @param operator 执行者
	 * @param params 任务变量
	 * @return
	 */
	public List<Task> execute(String taskId, String operator, Map<String, Object> params) {
		try {
			return engine.executeTask(taskId, operator, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	
	public List<String> getAllProcessNames() {
		List<Process> list = engine.process().getProcesss(new QueryFilter());
		List<String> names = new ArrayList<String>();
		for(Process entity : list) {
			if(names.contains(entity.getName())) {
				continue;
			} else {
				names.add(entity.getName());
			}
		}
		return names;
	}
	
	public Order startInstanceById(String processId, String operator, Map<String, Object> args) {
		return engine.startInstanceById(processId, operator, args);
	}
	
	public Order startInstanceByName(String name, Integer version, String operator, Map<String, Object> args) {
		return engine.startInstanceByName(name, version, operator, args);
	}
	
	public Order startAndExecute(String name, Integer version, String operator, Map<String, Object> args) {
		Order order = engine.startInstanceByName(name, version, operator, args);
		List<Task> tasks = engine.query().getActiveTasks(new QueryFilter().setOrderId(order.getId()));
		List<Task> newTasks = new ArrayList<Task>();
		if(tasks != null && tasks.size() > 0) {
			Task task = tasks.get(0);
			newTasks.addAll(engine.executeTask(task.getId(), operator, args));
		}
		return order;
	}
	
	public List<Task> executeAndJump(String taskId, String operator, Map<String, Object> args, String nodeName) {
		return engine.executeAndJumpTask(taskId, operator, args, nodeName);
	}

    public List<Task> transferMajor(String taskId, String operator, String... actors) {
        List<Task> tasks = engine.task().createNewTask(taskId, TaskType.Major.ordinal(), actors);
        engine.task().complete(taskId, operator);
        return tasks;
    }

    public List<Task> transferAidant(String taskId, String operator, String... actors) {
        List<Task> tasks = engine.task().createNewTask(taskId, TaskType.Aidant.ordinal(), actors);
        engine.task().complete(taskId, operator);
        return tasks;
    }
    
    public Map<String, Object> flowData(String orderId, String taskName) {
    	Map<String, Object> data = new HashMap<String, Object>();
		if (StringUtils.isNotEmpty(orderId) && StringUtils.isNotEmpty(taskName)) {
			List<HistoryTask> histTasks = engine.query()
					.getHistoryTasks(
							new QueryFilter().setOrderId(orderId).setName(
									taskName));
			List<Map<String, Object>> vars = new ArrayList<Map<String,Object>>();
			for(HistoryTask hist : histTasks) {
				vars.add(hist.getVariableMap());
			}
			data.put("vars", vars);
			data.put("histTasks", histTasks);
		}
		return data;
	}
	
	public void addSurrogate(Surrogate entity) {
		if(entity.getState() == null) {
			entity.setState(1);
		}
		engine.manager().saveOrUpdate(entity);
	}
	
	public void deleteSurrogate(String id) {
		engine.manager().deleteSurrogate(id);
	}
	
	public Surrogate getSurrogate(String id) {
		return engine.manager().getSurrogate(id);
	}
	
	public List<Surrogate> searchSurrogate(Page<Surrogate> page, QueryFilter filter) {
		return engine.manager().getSurrogate(page, filter);
	}
}
