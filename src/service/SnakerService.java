package service;

import static com.jfinal.aop.Enhancer.enhance;
import static util.Validator.notBlank;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.entity.Order;
import org.snaker.engine.entity.Task;

import util.File;
import engine.SnakerEngine;
import engine.model.WfProcess;

public class SnakerService {
	
	private SnakerEngine engine = enhance(SnakerEngine.class);;
	
	/**
	 * 工作流程初始化
	 * @return
	 */
	public boolean initFlows() {
		try {
			engine.addWfProcess(File.readFromClasspath("flows/leave.snaker"));
			engine.addWfProcess(File.readFromClasspath("flows/leave.snaker"));
			engine.addWfProcess(File.readFromClasspath("flows/mytest.xml"));
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
	public List<WfProcess> findWfProcess(String name) {
		if (notBlank(name))
			return engine.findWfProcess(name);
		return new ArrayList<WfProcess>();
	}
	
	/**
	 * 获取工作流程
	 * @param processId 流程主键
	 * @return
	 */
	public WfProcess getWfProcess(Long processId) {
		return engine.getWfProcess(processId);
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
	
}
