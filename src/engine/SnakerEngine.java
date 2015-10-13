package engine;

import static util.Validator.notBlank;

import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;
import engine.model.WfInstance;
import engine.model.WfProcess;

/*
 * 工作流引擎
 */
public class SnakerEngine {

	/**
	 * 获取工作流程
	 * @param processId 流程主键
	 * @return 工作流程
	 */
	public WfProcess getProcess(Long processId) {
		return WfProcess.dao.findById(processId);
	}
	
	/**
	 * 查询工作流程
	 * @param name 流程名称
	 * @return 工作流程列表
	 */
	public List<WfProcess> findProcess(String name) {
		return WfProcess.dao.find("select * from wf_process where name like ?", name);
	}
	
	/**
	 * 新增工作流程
	 * @param content 工作流程内容
	 * @return 新增是否成功
	 */
	public boolean addProcess(String content) {
		String name = JSONObject.fromObject(content).getString("name");
		WfProcess process = new WfProcess().set("name", name)
											.set("content", content)
											.set("update_date", new Date())
											.set("create_date", new Date());
		return process.save();
	}
	
	/**
	 * 更新工作流程
	 * @param id 流程主键
	 * @param content 流程内容
	 * @return 更新是否成功
	 */
	public boolean updateProcess(Long processId, String content) {
		String name = JSONObject.fromObject(content).getString("name");
		return WfProcess.dao.set("id", processId)
							.set("name", name)
							.set("content", content)
							.set("update_date", new Date())
							.update();
	}
	
	/**
	 * 删除工作流程
	 * @param processId 流程主键
	 * @return 删除是否成功
	 */
	public boolean deleteProcess(Long processId) {
		return WfProcess.dao.deleteById(processId);
	}
	
	/**
	 * 获取流程实例
	 * @param instanceId 实例主键
	 * @return 流程实例
	 */
	public WfInstance getInstance(Long instanceId) {
		return WfInstance.dao.findById(instanceId);
	}
	
	/**
	 * 运行工作流程
	 * @param processId 流程主键
	 * @param param 实例参数
	 * @return 是否运行成功
	 */
	public Workflow runWorkflow(Long processId, String param) {
		Workflow workflow = Workflow.create(processId);
		if (notBlank(workflow))
			if (workflow.run(param))
				return workflow;
		return null;
	}
	
	/**
	 * 获取工作流
	 * @param processId 流程主键
	 * @param instanceId 实例主键
	 * @return
	 */
	public Workflow getWorkflow(Long processId, Long instanceId) {
		if (notBlank(instanceId)) 
			return Workflow.get(processId, instanceId);
		else
			return Workflow.create(processId);
	}
	
}
