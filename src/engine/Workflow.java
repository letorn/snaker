package engine;

import static util.Validator.notBlank;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONNull;

import org.apache.commons.lang.ArrayUtils;

import util.Json;
import engine.model.WfInstance;
import engine.model.WfProcess;
import engine.module.BeginModule;
import engine.module.Module;

/*
 * 工作流
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Workflow {

	private WfProcess process;// 工作流程
	private Long processId;// 流程主键
	private String processName;// 流程名称
	private String processContent;// 流程内容

	private WfInstance instance;// 流程实例
	private Long instanceId;// 实例主键
	private String instanceParam;// 实例参数

	private WorkflowContext context;// 流程上下文

	private Module beginModule;// 开始模型
	private List<Module> modules;// 所有模型

	/**
	 * 构造方法
	 * @param name 流程名称
	 * @param modules 所有模型
	 */
	public Workflow(WorkflowContext context, Module beginModule, List<Module> modules) {
		this.context = context;
		this.beginModule = beginModule;
		this.modules = modules;
	}

	/**
	 * 运行一个实例
	 * @param param 实例参数
	 * @return
	 */
	public boolean run(String param) {
		if (notBlank(beginModule)) {
			WfInstance instance = new WfInstance().set("process_id", context.getProcessId())
													.set("param", param)
													.set("update_date", new Date())
													.set("create_date", new Date());
			if (instance.save()) {
				setInstance(instance);
				beginModule.run();
				return true;
			}
		}
		return false;
	}
	
	public static Workflow get(Long processId, Long instanceId) {
		Workflow workflow = Workflow.create(processId);
		workflow.setInstance(WfInstance.dao.findById(instanceId));
		return workflow;
	}
	
	/**
	 * 
	 * @param processId 流程主键
	 * @return 工作流
	 */
	public static Workflow create(Long processId) {
		try {
			WfProcess process = WfProcess.dao.findById(processId);
			Map<String, Object> contentMap = Json.parseToMap(process.getStr("content"));

			String workname = (String) contentMap.get("name");
			Module beginModule = null;
			List<Module> modules = new ArrayList<Module>();

			Map<String, Module> moduleNameMap = new HashMap<String, Module>();
			Map<String, List<String>> relatedNameMap = new HashMap<String, List<String>>();

			WorkflowContext context = new WorkflowContext();
			context.setProcessId(processId);
			
			List<Map<String, Object>> moduleMapList = (List<Map<String, Object>>) contentMap.get("modules");
			for (Map<String, Object> moduleMap : moduleMapList) {
				String name = (String)moduleMap.get("name");
				String clazz = (String)moduleMap.get("clazz");
				List<String> tos = (List<String>)moduleMap.get("tos");
				if (name != null && clazz != null) {
					Class classType = Class.forName(clazz);
					Object object = classType.newInstance();
					if (object instanceof Module) {
						Field[] fields = new Field[0];
						while (classType != Object.class) {
							fields = (Field[]) ArrayUtils.addAll(fields, classType.getDeclaredFields());
							classType = classType.getSuperclass();
						}
						for (Field field : fields) {
							Object value = moduleMap.get(field.getName());
							if (value != null && !(value instanceof JSONNull)) {
								field.setAccessible(true);
								field.set(object, value);
							}
						}
						Module module = (Module) object;
						if (module instanceof BeginModule)
							beginModule = module;
						modules.add(module);
						
						module.setContext(context);
						module.setPrevModules(new ArrayList<Module>());
						module.setNextModules(new ArrayList<Module>());

						moduleNameMap.put(name, module);
						if (tos != null)
							relatedNameMap.put(name, tos);
					}
				}
			}
			for (String moduleName : relatedNameMap.keySet()) {
				Module module = moduleNameMap.get(moduleName);
				List<String> tos = relatedNameMap.get(moduleName);
				List<Module> nexts = new ArrayList<Module>();
				for (String to : tos) {
					Module next = moduleNameMap.get(to);
					if (next != null) {
						nexts.add(next);
						next.getPrevModules().add(module);
					}
				}
				module.getNextModules().addAll(nexts);
			}
			if (notBlank(workname) && notBlank(beginModule) && notBlank(modules)) {
				Collections.sort(modules, new Comparator<Module>() {
					public int compare(Module m1, Module m2) {
						if (prevExist(m1, m2)) 
							return -1;
						return 0;
					}
				});
				Workflow workflow = new Workflow(context, beginModule, modules);
				workflow.setProcess(process);
				context.setWorkflow(workflow);
				return workflow;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 判断模型m1是否是m2之前
	 * @param m1 模型1
	 * @param m2 模式2
	 * @return 是否在m2之前
	 */
	private static boolean prevExist(Module m1, Module m2) {
		if (m2.getPrevModules().size() > 0)
			for (Module prev : m2.getPrevModules())
				return prev == m1 ? true : prevExist(m1, prev);
		return false;
	}

	public WfProcess getProcess() {
		return process;
	}

	public void setProcess(WfProcess process) {
		this.process = process;
		processId = process.getLong("id");
		processName = process.getStr("name");
		processContent = process.getStr("content");
		context.setProcessId(processId);
		context.setProcessName(processName);
		context.setProcessContent(processContent);
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessContent() {
		return processContent;
	}

	public void setProcessContent(String processContent) {
		this.processContent = processContent;
	}

	public WfInstance getInstance() {
		return instance;
	}

	public void setInstance(WfInstance instance) {
		this.instance = instance;
		instanceId = instance.getLong("id");
		instanceParam = instance.getStr("param");
		context.setInstanceId(instanceId);
		context.setInstanceParam(instanceParam);
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public String getInstanceParam() {
		return instanceParam;
	}

	public void setInstanceParam(String instanceParam) {
		this.instanceParam = instanceParam;
	}

	public WorkflowContext getContext() {
		return context;
	}

	public void setContext(WorkflowContext context) {
		this.context = context;
	}

	public Module getBeginModule() {
		return beginModule;
	}

	public void setBeginModule(Module beginModule) {
		this.beginModule = beginModule;
	}

	public List<Module> getModules() {
		return modules;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}
	
}
