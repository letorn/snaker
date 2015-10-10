package engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import util.File;
import engine.module.Module;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Workflow {

	private String name;
	private List<Module> modules;

	public Workflow(String name, List<Module> modules) {
		this.name = name;
		this.modules = modules;
	}

	public void run() {
		for (Module module : modules) {
			if (module.getPrevModules().size() <= 0)
				module.run(null);
		}
	}

	public String getName() {
		return name;
	}

	public List<Module> getModules() {
		return modules;
	}

	public static Workflow create(String content) {
		try {
			JSONObject contentObject = JSONObject.fromObject(content);

			String workname = catchString(contentObject, "name");
			List<Module> modules = new ArrayList<Module>();

			Map<String, Module> moduleMap = new HashMap<String, Module>();
			Map<String, String[]> linkMap = new HashMap<String, String[]>();

			ModuleContext context = new ModuleContext();

			JSONArray moduleArray = catchJSONArray(contentObject, "modules");
			Iterator<JSONObject> moduleIterator = moduleArray.iterator();
			while (moduleIterator.hasNext()) {
				JSONObject moduleObject = moduleIterator.next();
				String name = catchString(moduleObject, "name");
				String clazz = catchString(moduleObject, "clazz");
				String[] tos = catchStrings(moduleObject, "tos");
				if (name != null && clazz != null) {
					Class classType = Class.forName(clazz);
					Object object = classType.newInstance();
					if (object instanceof Module) {
						Field[] fields = classType.getDeclaredFields();
						for (Field field : fields) {
							Object value = moduleObject.get(field.getName());
							if (!(value instanceof JSONNull)) {
								field.setAccessible(true);
								field.set(object, value);
							}
						}
						Module module = (Module) object;
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
			return new Workflow(workname, modules);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static JSONObject catchJSONObject(JSONObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			Object value = jsonObject.get(key);
			if (value instanceof JSONObject) {
				return (JSONObject) value;
			}
		}
		return null;
	}

	private static JSONArray catchJSONArray(JSONObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			Object value = jsonObject.get(key);
			if (value instanceof JSONArray) {
				return (JSONArray) value;
			}
		}
		return null;
	}

	private static String catchString(JSONObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			Object value = jsonObject.get(key);
			if (value instanceof String) {
				return (String) value;
			}
		}
		return null;
	}

	private static String[] catchStrings(JSONObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			Object value = jsonObject.get(key);
			if (value instanceof JSONArray) {
				Object[] objects = ((JSONArray) value).toArray();
				List<String> values = new ArrayList<String>();
				for (Object object : objects) {
					values.add(object.toString());
				}
				return values.toArray(new String[] {});
			}
		}
		return null;
	}

	private static Integer catchInt(JSONObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			Object value = jsonObject.get(key);
			if (value instanceof Number) {
				return (Integer) value;
			}
		}
		return null;
	}

	private static Long catchLong(JSONObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			Object value = jsonObject.get(key);
			if (value instanceof Number) {
				return (Long) value;
			}
		}
		return null;
	}

	private static String filename(String filename) {
		return System.class.getResource(filename).getFile();
	}

	public static void main(String[] args) {
		String content = File.read(filename("/flows/mytest2.snaker"));
		Workflow workflow = Workflow.create(content);
		workflow.run();
	}
}
