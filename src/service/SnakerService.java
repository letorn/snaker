package service;

import static com.jfinal.aop.Enhancer.enhance;
import static util.Validator.blank;

import java.util.ArrayList;
import java.util.List;

import util.File;

import com.jfinal.plugin.activerecord.Page;

import engine.ModuleData;
import engine.SnakerEngine;
import engine.Workflow;
import engine.module.Module;

/*
 * 服务类 - 工作流程相关
 */
public class SnakerService {
	
	private SnakerEngine engine = enhance(SnakerEngine.class);;
	
	/**
	 * 添加指定工作流程
	 * @return 添加是否成功
	 */
	public boolean initFlows() {
		try {
			engine.addProcess(File.readFromClasspath("/flows/test.snaker"));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 工作流程初始化
	 * @return 初始化是否成功
	 */
	@SuppressWarnings("static-access")
	public boolean initProcesses() {
		return engine.initProcesses();
	}
	
	/**
	 * 获取工作流程
	 * @param processId 流程主键
	 * @return 工作流程
	 */
	public Workflow getProcess(Long processId) {
		return engine.getProcess(processId);
	}
	
	/**
	 * 查询工作流程
	 * @param name 流程名称
	 * @return 工作流程列表
	 */
	public Page<Workflow> findProcess(int page, int limit, String name) {
		List<Workflow> processes = engine.getProcesses();
		List<Workflow> list = new ArrayList<Workflow>();
		if (blank(name)) {
			list = processes;
		} else {
			for (Workflow process : processes)
				if (process.getProcessName().contains(name))
					list.add(process);
		}
		int total = list.size();
		int fromIndex = (page - 1) * limit;
		int toIndex = fromIndex + limit;
		if (toIndex > total) toIndex = total;
		return new Page<Workflow>(list.subList(fromIndex, toIndex), page, limit, total % limit == 0 ? total / limit : total / limit + 1, total);
	}
	
	/**
	 * 保存工作流程：有主键就更新，没我键就新增
	 * @param processId 流程主键
	 * @param content 流程XML内容
	 * @return 保存是否成功
	 */
	public Workflow saveProcesss(Long processId, String content) {
		return blank(processId) ? engine.addProcess(content) : engine.updateProcess(processId, content);
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
	 * 运行工作流
	 * @param processId 流程主键
	 * @param param 实例参数
	 * @param daemon 后台运行
	 * @return 是否运行成功
	 */
	public Workflow startProcess(Long processId, String params, boolean daemon) {
		try {
			return engine.startProcess(processId, params, daemon);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 是否存在实例
	 * @param processId 流程主键
	 * @return 是否存在
	 */
	public boolean hasInstance(Long processId) {
		return engine.hasInstance(processId);
	}
	
	/**
	 * 获取流程实例
	 * @param instanceId 实例主键
	 * @return 流利实例
	 */
	public Workflow getInstance(Long instanceId) {
		return engine.getInstance(instanceId);
	}
	
	/**
	 * 获取组件的所有记录
	 * @param instanceId 实例主键
	 * @param moduleName 组件名称
	 * @return 记录列表
	 */
	public ModuleData getRecords(Long instanceId, String moduleName) {
		Workflow workflow = engine.getInstance(instanceId);
		for (Module module : workflow.getModules())
			if (module.getName().equals(moduleName))
				return module.getRecords();
		return null;
	}
	
	/**
	 * 查询工作流实例
	 * @param page 页码
	 * @param limit 每页多少条记录
	 * @param name 流程名称
	 * @return 工作流实例列表
	 */
	public Page<Workflow> findInstance(int page, int limit, String name) {
		List<Workflow> instances = engine.getInstances();
		List<Workflow> list = new ArrayList<Workflow>();
		if (blank(name)) {
			list = instances;
		} else {
			for (Workflow instance : instances)
				if (instance.getProcessName().contains(name))
					list.add(instance);
		}
		int total = list.size();
		int fromIndex = (page - 1) * limit;
		int toIndex = fromIndex + limit;
		if (toIndex > total) toIndex = total;
		return new Page<Workflow>(list.subList(fromIndex, toIndex), page, limit, total % limit == 0 ? total / limit : total / limit + 1, total);
	}
	
	/**
	 * 实例活动状态
	 * @param instanceId 实例主键
	 * @return 是否正在活动
	 */
	public boolean instanceIsAlive(Long instanceId) {
		return engine.instanceIsAlive(instanceId);
	}
	
	/**
	 * 停止工作流实例
	 * @param 要停止的实例ID
	 * @return 是否停止
	 */
	public boolean stopProcess(Long instanceId) {
		return engine.stopProcess(instanceId);
	}
}
