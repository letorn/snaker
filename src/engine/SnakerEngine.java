package engine;

import static util.Validator.notBlank;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import com.jfinal.plugin.activerecord.Db;

import engine.model.WfInstance;
import engine.model.WfProcess;

/*
 * 工作流引擎
 */
public class SnakerEngine {

	private static List<Workflow> workflows = new ArrayList<Workflow>();
	
	static {
		/*
		 * 加载已经存在的流程实例
		 */
		List<WfInstance> instances = WfInstance.dao.find("select * from wf_instance");
		for (WfInstance instance : instances) {
			WfProcess process = WfProcess.dao.findById(instance.getLong("process_id"));
			if (notBlank(process)) {
				Workflow workflow = Workflow.create(process);
				workflow.setInstance(instance);
				workflows.add(workflow);
			}
		}
	}
	
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
		if (WfProcess.dao.deleteById(processId)) {
			Db.deleteById("wf_instance", "process_id", processId);
			Db.deleteById("wf_record", "process_id", processId);
			return true;
		}
		return false;
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
	public Workflow startWorkflow(Long processId, String params) {
		Workflow workflow = Workflow.create(processId);
		if (notBlank(workflow)) {
			if (workflow.start(params)) {
				workflows.add(workflow);
				return workflow;
			}
		}
		return null;
	}
	
	/**
	 * 创建工作流
	 * @param processId 流程主键
	 * @return 工作流
	 */
	public Workflow createWorkflow(Long processId) {
		return Workflow.create(processId);
	}
	
	/**
	 * 获取工作流
	 * @param processId 流程主键
	 * @param instanceId 实例主键
	 * @return 工作流
	 */
	public Workflow getWorkflow(Long processId, Long instanceId) {
		for (Workflow workflow : workflows) {
			if (workflow.getInstanceId().equals(instanceId))
				return workflow;
		}
		return null;
	}
	
	/**
	 * 查询工作流实例
	 * @return 工作流实例列表
	 */
	public List<Workflow> findWorkflow() {
		return workflows;
	}
	
}
