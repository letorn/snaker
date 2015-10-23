package test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JsTest {

	public static void main(String[] args) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("javascript");
		try {
			Integer a = 1;
			Integer b = 2;
			String[] strs = new String[]{"a", "b"};
			engine.put("a", a);
			engine.put("b", b);
			engine.put("strs", strs);
			engine.eval("a = 2");
			engine.eval("c = a + b");
			engine.eval("d = String(strs[0])");
			engine.eval("e = new Packages.java.lang.Integer(2)");
			
			System.out.println(engine.get("e").getClass());
			Number a2 = (Number)engine.get("a");
			System.out.println(a2.intValue());
			Double c = (Double)engine.get("c");
			String d = (String)engine.get("d");
			System.out.println(a);
			System.out.println(a2);
			System.out.println(c);
			System.out.println(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
