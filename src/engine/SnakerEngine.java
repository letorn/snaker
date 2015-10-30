package engine;

import static util.Validator.notBlank;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

import com.jfinal.plugin.activerecord.Db;

import engine.model.WfInstance;
import engine.model.WfProcess;

/*
 * 工作流引擎
 */
public class SnakerEngine {

	private static Map<Long, Workflow> processIdMap = new HashMap<Long, Workflow>();
	private static Map<Long, Workflow> instanceIdMap = new HashMap<Long, Workflow>();
	
	static {
		/*
		 * 加载已经存在的流程实例
		 */
		List<WfProcess> processes = WfProcess.dao.find("select * from wf_process");
		for (WfProcess process : processes) {
			Workflow workflow = Workflow.create(process);
			if (notBlank(workflow))
				processIdMap.put(workflow.getProcessId(), workflow);
		}
		
		List<WfInstance> instances = WfInstance.dao.find("select * from wf_instance");
		for (WfInstance instance : instances) {
			Long processId = instance.getLong("process_id");
			// Workflow workflow = processIdMap.get(processId);
			Workflow workflow = Workflow.create(WfProcess.dao.findById(processId));
			if (notBlank(workflow)) {
				workflow = workflow.clone();
				workflow.setInstance(instance);
				instanceIdMap.put(workflow.getInstanceId(), workflow);
			}
		}
	}
	
	/**
	 * 运行工作流程
	 * @param processId 流程主键
	 * @param param 实例参数
	 * @param daemon 后台运行
	 * @return 流程实例
	 */
	public Workflow startProcess(Long processId, String params, boolean daemon) {
		// Workflow workflow = processIdMap.get(processId);
		Workflow workflow = Workflow.create(WfProcess.dao.findById(processId));
		if (notBlank(workflow)) {
			WfInstance instance = new WfInstance();
			instance.set("process_id", processId)
					.set("params", params)
					.set("create_date", new Date());
			if (instance.save()) {
				// workflow = workflow.clone();
				workflow.setInstance(instance);
				workflow.setDaemon(daemon);
				if (workflow.start(params)) {
					instanceIdMap.put(workflow.getInstanceId(), workflow);
					return workflow;
				}
			}
		}
		return null;
	}
	
	/**
	 * 新增工作流程
	 * @param content 工作流程内容
	 * @return 新增是否成功
	 */
	public boolean addProcess(String content) {
		String name = JSONObject.fromObject(content).getString("name");
		WfProcess process = new WfProcess();
		process.set("name", name)
				.set("content", content)
				.set("update_date", new Date())
				.set("create_date", new Date());
		if (process.save()) {
			Workflow workflow = Workflow.create(process);
			processIdMap.put(workflow.getProcessId(), workflow);
			return true;
		}
		return false;
	}
	
	/**
	 * 更新工作流程
	 * @param processId 流程主键
	 * @param content 流程内容
	 * @return 更新是否成功
	 */
	public boolean updateProcess(Long processId, String content) {
		String name = JSONObject.fromObject(content).getString("name");
		WfProcess process = new WfProcess();
		process.set("id", processId)
				.set("name", name)
				.set("content", content)
				.set("update_date", new Date());
		if(process.update()) {
			Workflow workflow = Workflow.create(process);
			processIdMap.put(workflow.getProcessId(), workflow);
			return true;
		}
		return false;
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
			processIdMap.remove(processId);
			Iterator<Entry<Long, Workflow>> iterator = instanceIdMap.entrySet().iterator(); 
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
		return processIdMap.get(processId);
	}
	
	/**
	 * 查询工作流程
	 * @return 工作流程列表
	 */
	public List<Workflow> findProcess() {
		List<Workflow> list = new ArrayList<Workflow>(processIdMap.values());
		Collections.sort(list, new Comparator<Workflow>() {
			public int compare(Workflow wf1, Workflow wf2) {
				return wf1.getProcessId() > wf2.getProcessId() ? 1 : -1;
			}
		});
		return list;
	}
	
	/**
	 * 获取流程实例
	 * @param instanceId 实例主键
	 * @return 流程实例
	 */
	public Workflow getInstance(Long instanceId) {
		return instanceIdMap.get(instanceId);
	}
	
	/**
	 * 查询流程实例
	 * @return 流程实例列表
	 */
	public List<Workflow> findInstance() {
		List<Workflow> list = new ArrayList<Workflow>(instanceIdMap.values());
		Collections.sort(list, new Comparator<Workflow>() {
			public int compare(Workflow wf1, Workflow wf2) {
				return wf1.getProcessId() > wf2.getProcessId() ? 1 : -1;
			}
		});
		return list;
	}
	
	/**
	 * 深层复制工作流程
	 * @param workflow 旧工作流程
	 * @return 新工作流程
	 */
	public static Workflow copy(Workflow workflow) {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(workflow);
			bais = new ByteArrayInputStream(baos.toByteArray());
			ois = new ObjectInputStream(bais);
			return (Workflow) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				baos.close();
				oos.close();
				bais.close();
				ois.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
}
