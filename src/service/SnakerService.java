package service;

import static com.jfinal.aop.Enhancer.enhance;
import static util.Validator.blank;

import java.util.ArrayList;
import java.util.List;

import util.File;

import com.jfinal.plugin.activerecord.Page;

import engine.SnakerEngine;
import engine.Workflow;

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
			engine.addProcess(File.readFromClasspath("/flows/test.snaker"));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
	public List<Workflow> findProcess(String name) {
		List<Workflow> processes = engine.findProcess();
		if (blank(name))
			return processes;
		List<Workflow> list = new ArrayList<Workflow>();
		for (Workflow process : processes) {
			if (process.getProcessName().contains(name))
				list.add(process);
		}
		return list;
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
	 * 获取流程实例
	 * @param instanceId 实例主键
	 * @return 流利实例
	 */
	public Workflow getInstance(Long instanceId) {
		return engine.getInstance(instanceId);
	}
	
	/**
	 * 查询工作流实例
	 * @param page 页码
	 * @param limit 每页多少条记录
	 * @param name 流程名称
	 * @return 工作流实例列表
	 */
	public Page<Workflow> findInstance(int page, int limit, String name) {
		List<Workflow> instances = engine.findInstance();
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
		if (toIndex > total)
			toIndex = total;
		return new Page<Workflow>(list.subList(fromIndex, toIndex), page, limit, total % limit == 0 ? total / limit : total / limit + 1, total);
	}
	
}
