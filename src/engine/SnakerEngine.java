package engine;

import static util.Validator.notBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;
import engine.model.WfProcess;

/*
 * 工作流引擎
 */
public class SnakerEngine {

	private static Map<Long, Workflow> processWorkflowIdMap = new HashMap<Long, Workflow>();
	private static Map<Long, Workflow> instanceWorkflowIdMap = new HashMap<Long, Workflow>();
	
	static {
		initProcesses();
	}
	
	/**
	 * 加载已经存在的工作流程
	 * @return 是否加载成功
	 */
	public static boolean initProcesses() {
		List<WfProcess> processes = WfProcess.dao.find("select id,name,content,update_date from wf_process");
		for (WfProcess process : processes) {
			Workflow workflow = Workflow.create(process.getStr("content"));
			if (notBlank(workflow)) {
				workflow.setProcess(process);
				processWorkflowIdMap.put(workflow.getProcessId(), workflow);
			}
		}
		return true;
	}
	
	/**
	 * 运行工作流程
	 * @param processId 流程主键
	 * @param param 实例参数
	 * @param daemon 后台运行
	 * @return 流程实例
	 */
	public Workflow startProcess(Long processId, String params, boolean daemon) {
		Workflow prototype = processWorkflowIdMap.get(processId);
		if (notBlank(prototype)) {
			Workflow workflow = Workflow.create(prototype.getProcessContent());
			workflow.setProcess(prototype.getProcess());
			workflow.setInstanceId(System.currentTimeMillis());
			workflow.setInstanceParams(params);
			workflow.setInstanceCreateDate(new Date());
			workflow.setDaemon(daemon);
			if (workflow.init() && workflow.start()) {
				instanceWorkflowIdMap.put(workflow.getInstanceId(), workflow);
				return workflow;
			}
		}
		return null;
	}
	
	/**
	 * 停止工作流程
	 * @param instanceId 实例主键
	 * @return 停止是否成功
	 */
	public boolean stopProcess(Long instanceId) {
		Workflow workflow = instanceWorkflowIdMap.get(instanceId);
		if (notBlank(workflow))
			return workflow.stop();
		return false;
	}
	
	/**
	 * 新增工作流程
	 * @param content 工作流程内容
	 * @return 新增是否成功
	 */
	public Workflow addProcess(String content) {
		Workflow workflow = Workflow.create(content);
		String name = JSONObject.fromObject(content).getString("name");
		Date current = new Date();
		WfProcess process = new WfProcess()
							.set("name", name)
							.set("content", content)
							.set("update_date", current)
							.set("create_date", current);
		if (notBlank(workflow) && process.save()) {
			workflow.setProcess(process);
			processWorkflowIdMap.put(workflow.getProcessId(), workflow);
			return workflow;
		}
		return null;
	}
	
	/**
	 * 更新工作流程
	 * @param processId 流程主键
	 * @param content 流程内容
	 * @return 更新是否成功
	 */
	public Workflow updateProcess(Long processId, String content) {
		Workflow workflow = Workflow.create(content);
		String name = JSONObject.fromObject(content).getString("name");
		WfProcess process = new WfProcess()
							.set("id", processId)
							.set("name", name)
							.set("content", content)
							.set("update_date", new Date());
		if(notBlank(workflow) && process.update()) {
			workflow.setProcess(process);
			processWorkflowIdMap.put(workflow.getProcessId(), workflow);
			return workflow;
		}
		return null;
	}
	
	/**
	 * 删除工作流程
	 * @param processId 流程主键
	 * @return 删除是否成功
	 */
	public boolean deleteProcess(Long processId) {
		if (WfProcess.dao.deleteById(processId)) {
			processWorkflowIdMap.remove(processId);
			Iterator<Entry<Long, Workflow>> iterator = instanceWorkflowIdMap.entrySet().iterator(); 
			while (iterator.hasNext())
				if (processId.equals(iterator.next().getValue().getProcessId()))
					iterator.remove();
			return true;
		}
		return false;
	}
	
	/**
	 * 获取工作流程
	 * @param processId 流程主键
	 * @return 工作流程
	 */
	public Workflow getProcess(Long processId) {
		return processWorkflowIdMap.get(processId);
	}
	
	/**
	 * 获取所有工作流程
	 * @return 工作流程列表
	 */
	public List<Workflow> getProcesses() {
		List<Workflow> list = new ArrayList<Workflow>(processWorkflowIdMap.values());
		Collections.sort(list, new Comparator<Workflow>() {
			public int compare(Workflow wf1, Workflow wf2) {
				return wf1.getProcessId() > wf2.getProcessId() ? 1 : -1;
			}
		});
		return list;
	}
	
	/**
	 * 是否存在实例
	 * @param processId 流程主键
	 * @return 是否存在
	 */
	public boolean hasInstance(Long processId) {
		for (Workflow instance : instanceWorkflowIdMap.values())
			if (processId.equals(instance.getProcessId()))
				return true;
		return false;
	}
	
	/**
	 * 获取流程实例
	 * @param instanceId 实例主键
	 * @return 流程实例
	 */
	public Workflow getInstance(Long instanceId) {
		return instanceWorkflowIdMap.get(instanceId);
	}

	/**
	 * 获取所有流程实例
	 * @return 流程实例列表
	 */
	public List<Workflow> getInstances() {
		List<Workflow> list = new ArrayList<Workflow>(instanceWorkflowIdMap.values());
		Collections.sort(list, new Comparator<Workflow>() {
			public int compare(Workflow wf1, Workflow wf2) {
				return wf1.getInstanceId() > wf2.getInstanceId() ? -1 : 1;
			}
		});
		return list;
	}

	/**
	 * 实例活动状态
	 * @param instanceId 实例主键
	 * @return 是否正在活动
	 */
	public boolean instanceIsAlive(Long instanceId) {
		Workflow instance = instanceWorkflowIdMap.get(instanceId);
		if (notBlank(instance))
			return instance.isAlive();
		return false;
	}
	
}
