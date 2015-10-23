package service;

import static com.jfinal.aop.Enhancer.enhance;
import static util.Validator.notBlank;

import java.util.List;

import util.File;
import engine.SnakerEngine;
import engine.Workflow;
import engine.model.WfInstance;
import engine.model.WfProcess;

/*
 * 服务类 - 工作流程相关
 */
public class SnakerService {
	
	private SnakerEngine engine = enhance(SnakerEngine.class);;
	
	/**
	 * 工作流程初始化
	 * @return 初始化是否成功
	 */
	public boolean initFlows() {
		try {
			engine.addProcess(File.readFromClasspath("/flows/dbprocoutput-save_or_update_ent.snaker"));
			engine.addProcess(File.readFromClasspath("/flows/dbprocoutput-save_or_update_post.snaker"));
			engine.addProcess(File.readFromClasspath("/flows/dbtableoutput-db_enterprise.snaker"));
			engine.addProcess(File.readFromClasspath("/flows/dbtableoutput-db_entpost.snaker"));
			engine.addProcess(File.readFromClasspath("/flows/test.snaker"));
//			engine.addProcess(File.readFromClasspath("/flows/webinput.snaker"));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 查询工作流程
	 * @param name 流程名称
	 * @param displayName 流程显示名称
	 * @param state 流程状态
	 * @param processType 流程类型
	 * @return 工作流程列表
	 */
	public List<WfProcess> findProcess(String name) {
		return engine.findProcess(name);
	}
	
	/**
	 * 获取工作流程
	 * @param processId 流程主键
	 * @return 工作流程
	 */
	public WfProcess getProcess(Long processId) {
		return engine.getProcess(processId);
	}
	
	/**
	 * 保存工作流程：有主键就更新，没我键就新增
	 * @param processId 流程主键
	 * @param content 流程XML内容
	 * @return 保存是否成功
	 */
	public boolean saveProcesss(Long processId, String content) {
		if (notBlank(processId))
			return engine.updateProcess(processId, content);
		else
			return engine.addProcess(content);
	}
	
	/**
	 * 删除工作流程
	 * @param processId 流程主键
	 * @return 删除是否成功
	 */
	public boolean deleteProcess(Long processId) {
		return engine.deleteProcess(processId);
	}
	
	/**
	 * 获取流程实例
	 * @param orderId 实例主键
	 * @return 流利实例
	 */
	public WfInstance getInstance(Long instanceId) {
		return engine.getInstance(instanceId);
	}
	
	/**
	 * 创建工作流
	 * @param processId 流程主键
	 * @return 工作流
	 */
	public Workflow createWorkflow(Long processId) {
		return engine.createWorkflow(processId);
	}
	
	/**
	 * 获取工作流
	 * @param processId 流程主键
	 * @param instanceId 实例主键
	 * @return 工作流
	 */
	public Workflow getWorkflow(Long processId, Long instanceId) {
		return engine.getWorkflow(processId, instanceId);
	}
	
	/**
	 * 运行工作流
	 * @param processId 流程主键
	 * @param param 实例参数
	 * @return 是否运行成功
	 */
	public Workflow runWorkflow(Long processId, String param) {
		return engine.runWorkflow(processId, param);
	}
	
	/**
	 * 查询工作流实例
	 * @return 工作流实例列表
	 */
	public List<Workflow> findWorkflow() {
		return engine.findWorkflow();
	}
	
}
