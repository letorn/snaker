package engine.module;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import util.Baidu;
import util.CreateHtml;
import util.Growth;

import com.jfinal.kit.PropKit;

import engine.ModuleData;
import engine.ModuleData.DataHeader;

public class AddFieldModule extends Module {

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private List<Map<String, Object>> fieldTable;

	@Override
	public ModuleData execute(ModuleData inputs) {
		ScriptEngineManager manager = new ScriptEngineManager();
		List<DataHeader> header = inputs.getHeaders();
		for (int j = 0; j < fieldTable.size(); j++) {
			String[] name = fieldTable.get(j).get("name").toString().split(",");
			if (name.length == 2) {
				header.add(new DataHeader(name[0], "string", ""));
				header.add(new DataHeader(name[1], "string", ""));
			} else if (name.length == 1)
				header.add(new DataHeader((String) fieldTable.get(j).get("name"), "string", ""));
		}

		for (int i = 0; i < inputs.getRows().size(); i++) {
			Map<String, Object> map = inputs.getRows().get(i);
			boolean initScript = false;

			ScriptEngine engine = manager.getEngineByName("javascript");
			for (int j = 0; j < fieldTable.size(); j++) {
				if (fieldTable.get(j).get("type").equals("variable")) {
					// 变量
					map.put((String) fieldTable.get(j).get("name"), inputs.getRows().get(i).get(fieldTable.get(j).get("value")));
				} else if (fieldTable.get(j).get("type").equals("constant")) {
					// 常量
					map.put((String) fieldTable.get(j).get("name"), fieldTable.get(j).get("value"));
				} else if (fieldTable.get(j).get("type").equals("currentdate")) {
					// 当前时间
					Date date = new Date();
					map.put((String) fieldTable.get(j).get("name"), dateFormat.format(date));
				} else if (fieldTable.get(j).get("type").equals("coordinate")) {
					// 坐标
					try {
						String[] address = fieldTable.get(j).get("value").toString().split(",");
						Double[] coordinate = null;
						if (address.length == 2) {
							Object area = inputs.getRows().get(i).get(address[0]);
							Object addr = inputs.getRows().get(i).get(address[1]);
							coordinate = Baidu.getPoint((String) addr, (String) area);
							if (coordinate == null) {
								coordinate = Baidu.getPoint((String) area + (String) addr, null);
							}
						} else if (address.length == 1) {
							address = address[0].split(":");
							Object area = inputs.getRows().get(i).get(address[0]);
							Object addr = inputs.getRows().get(i).get(address[1]);
							coordinate = Baidu.getPoint((String) addr, null);
							if (coordinate == null) {
								coordinate = Baidu.getPoint((String) addr, (String) area);
							}
						}
						String[] name = fieldTable.get(j).get("name").toString().split(",");
						map.put(name[0], coordinate != null ? coordinate[0] : null); // x
						map.put(name[1], coordinate != null ? coordinate[1] : null); // y
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} else if (fieldTable.get(j).get("type").equals("entaccount")) {
					String account = Growth.get(fieldTable.get(j).get("value").toString());
					map.put(fieldTable.get(j).get("name").toString(), account);
					// 企业账号
				} else if (fieldTable.get(j).get("type").equals("useraccount")) {
					String account = Growth.get(fieldTable.get(j).get("value").toString());
					map.put(fieldTable.get(j).get("name").toString(), account);
					// 求职者账号
				} else if (fieldTable.get(j).get("type").equals("script")) {
					// 脚本
					if (!initScript) {
						initScript = true;
						for (Entry<String, Object> entry : map.entrySet()) {
							engine.put(entry.getKey(), entry.getValue());
						}
					}
					try {
						engine.eval(fieldTable.get(j).get("value").toString());
						Object o = engine.get(fieldTable.get(j).get("name").toString());
						map.put(fieldTable.get(j).get("name").toString(), o);
					} catch (ScriptException e) {
						throw new RuntimeException(e);
					}
				} else if (fieldTable.get(j).get("type").equals("createhtmlpage")) {

					String create_time = inputs.getRows().get(i).get("create_time").toString();
					String title = inputs.getRows().get(i).get("title").toString();
					String source = inputs.getRows().get(i).get("source").toString();
					String html_content = inputs.getRows().get(i).get("html_content").toString();
					String path = PropKit.get("fileserver.path");

					// 生成html文件
					String fileName = "";
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("title", title);
					paramMap.put("date", create_time);
					paramMap.put("source", source);
					paramMap.put("content", html_content);

					String htmlName = "";
					try {
						htmlName = CreateHtml.create(fileName, paramMap, path);
						map.put(fieldTable.get(j).get("name").toString(), htmlName);
						// information.setHtml_name(htmlName);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} else if (fieldTable.get(j).get("type").equals("parameter")) {
					// 系统参数
					map.put((String) fieldTable.get(j).get("name"), workflow.getParameters().get(fieldTable.get(j).get("value")));
				}
			}
		}
		return inputs;
	}

	public List<Map<String, Object>> getFieldTable() {
		return fieldTable;
	}

}
