package engine;

import static util.Validator.notBlank;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

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

	private String name;// 工作流程名称
	private Module beginModule;// 开始模型
	private List<Module> modules;// 所有模型
	private WorkflowContext context;// 流程上下文
	private WfInstance instance;// 流程实例

	/**
	 * 构造方法
	 * @param name 流程名称
	 * @param modules 所有模型
	 */
	public Workflow(WorkflowContext context, String name, Module beginModule, List<Module> modules) {
		this.context = context;
		this.name = name;
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
			context.setParam(null);
			beginModule.run();
			WfInstance instance = new WfInstance().set("process_id", context.getProcessId())
													.set("param", param)
													.set("update_date", new Date())
													.set("create_date", new Date());
			if (instance.save()) {
				context.setInstanceId(instance.getLong("id"));
				return true;
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public List<Module> getModules() {
		return modules;
	}

	public void setInstance(WfInstance instance) {
		context.setParam(null);
		this.instance = instance;
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
			JSONObject contentObject = JSONObject.fromObject(process.getStr("content"));

			String workname = Json.catchString(contentObject, "name");
			Module beginModule = null;
			List<Module> modules = new ArrayList<Module>();

			Map<String, Module> moduleMap = new HashMap<String, Module>();
			Map<String, String[]> linkMap = new HashMap<String, String[]>();

			WorkflowContext context = new WorkflowContext();
			context.setProcessId(processId);
			
			JSONArray moduleArray = Json.catchJSONArray(contentObject, "modules");
			Iterator<JSONObject> moduleIterator = moduleArray.iterator();
			while (moduleIterator.hasNext()) {
				JSONObject moduleObject = moduleIterator.next();
				String name = Json.catchString(moduleObject, "name");
				String clazz = Json.catchString(moduleObject, "clazz");
				String[] tos = Json.catchStrings(moduleObject, "tos");
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
							Object value = moduleObject.get(field.getName());
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

						moduleMap.put(name, module);
						if (tos != null)
							linkMap.put(name, tos);
					}
				}
			}
			for (String moduleName : linkMap.keySet()) {
				Module module = moduleMap.get(moduleName);
				String[] tos = linkMap.get(moduleName);
				List<Module> nexts = new ArrayList<Module>();
				for (String to : tos) {
					Module next = moduleMap.get(to);
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
				return new Workflow(context, workname, beginModule, modules);
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
	
}
