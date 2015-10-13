package test;

import java.util.List;
import java.util.Map;

import util.File;
import util.Json;

@SuppressWarnings("unchecked")
public class JsonTest {

	public static void main(String[] args) {
		String snaker = File.readFromClasspath("flows/test.snaker");
		Map<String, Object> map = Json.parseToMap(snaker);
		List<Map<String, Object>> moduleList = (List<Map<String, Object>>) map.get("modules");
		for (Object module : moduleList) {
			List<Object> tos = (List<Object>) ((Map<String, Object>) module).get("tos");
			if (tos.size() > 0)
				System.out.println(tos.get(0));
		}
	}

}
