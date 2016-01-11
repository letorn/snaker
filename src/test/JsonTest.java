package test;

import java.util.List;
import java.util.Map;

import engine.ModuleData;
import engine.module.HbInputModule;
import util.FileKit;
import util.Json;

@SuppressWarnings("unchecked")
public class JsonTest {

	public static void main2(String[] args) {
		String snaker = FileKit.readFromClasspath("flows/test.snaker");
		Map<String, Object> map = Json.parseToMap(snaker);
		List<Map<String, Object>> moduleList = (List<Map<String, Object>>) map.get("modules");
		for (Object module : moduleList) {
			List<Object> tos = (List<Object>) ((Map<String, Object>) module).get("tos");
			if (tos.size() > 0)
				System.out.println(tos.get(0));
		}
	}
	
	
	public static void main(String[] args) {
//		 String dxUrl = "http://59.175.218.200:9090/dxservice/svcs/DataService";
//		 String username = "admin";
//		 String password = "1";
		 ModuleData inputs=null;
		 HbInputModule d=new HbInputModule();
		 d.execute(inputs);
	}

}
