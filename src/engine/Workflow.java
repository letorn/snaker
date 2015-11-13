package engine;

import static util.Validator.notBlank;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

	private boolean daemon = true;// 后台运行
	
	private WfProcess process;// 工作流程
	private Long processId;// 流程主键
	private String processName;// 流程名称
	private String processContent;// 流程内容

	private WfInstance instance;// 流程实例
	private Long instanceId;// 实例主键
	private String instanceParams;// 实例参数

	private Module beginModule;// 开始模型
	private List<Module> modules;// 所有模型

	/**
	 * 初始化参数
	 * @param params 实例参数
	 * @return 是否初始化成功
	 */
	public boolean init(String params) {
		Map<String, Map<String, Object>> allModuleParamMap = Json.parseToMap(params);
		for (Module module : modules) {
			String moduleName = module.getName();
			Map<String, Object> moduleParamMap = allModuleParamMap.get(moduleName);
			List<Map<String, Object>> moduleParams = module.getParams();
			if (notBlank(moduleParamMap) && notBlank(moduleParams) && moduleParams.size() > 0) {
				Class classType = module.getClass();
				for (Map<String, Object> moduleParam : moduleParams) {
					String paramName = (String) moduleParam.get("name");
					Object paramValue = moduleParamMap.get(paramName);
					try {
						Field field = classType.getDeclaredField(paramName);
						field.setAccessible(true);
						if (field.getGenericType() == File.class) {
							paramValue = new File(paramValue.toString());
						}
						field.set(module, paramValue);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 运行一个实例
	 * @param params 实例参数
	 * @return 是否运行成功
	 */
	public boolean start(String params) {
		if (notBlank(beginModule) && init(params)) {
			if (daemon) {
				new Thread(beginModule).start();
			} else {
				beginModule.run();
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 停止一个实例
	 * @param params 实例参数
	 * @return 是否停止成功
	 */
	public boolean stop() {
		for (Module module : modules)
			module.stop();
		return true;
	}
	
	/**
	 * 创建工作流
	 * @param process 流程流程
	 * @return 工作流
	 */
	public static Workflow create(String content) {
		try {
			Map<String, Object> contentMap = Json.parseToMap(content);

			String workname = (String) contentMap.get("name");
			Module beginModule = null;
			List<Module> modules = new ArrayList<Module>();

			Map<String, Module> moduleNameMap = new HashMap<String, Module>();
			Map<String, List<String>> relatedNameMap = new HashMap<String, List<String>>();

			Workflow workflow = new Workflow();
			
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
						
						module.setWorkflow(workflow);
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
				workflow.setBeginModule(beginModule);
				workflow.setModules(modules);
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

	public boolean isDaemon() {
		return daemon;
	}

	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	public void setProcess(WfProcess process) {
		this.process = process;
		processId = process.getLong("id");
		processName = process.getStr("name");
		processContent = process.getStr("content");
	}

	public WfProcess getProcess() {
		return process;
	}
	
	public Long getProcessId() {
		return processId;
	}

	public String getProcessName() {
		return processName;
	}

	public String getProcessContent() {
		return processContent;
	}

	public void setInstance(WfInstance instance) {
		this.instance = instance;
		instanceId = instance.getLong("id");
		instanceParams = instance.getStr("params");
	}

	public WfInstance getInstance() {
		return instance;
	}
	
	public Long getInstanceId() {
		return instanceId;
	}

	public String getInstanceParams() {
		return instanceParams;
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
