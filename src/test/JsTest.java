package test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JsTest {

	public static void main(String[] args) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("javascript");
		try {
			engine.put("salary", "100年");
			engine.eval("type = /年/.test(salary)");
			System.out.println(engine.get("type"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
