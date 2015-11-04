package engine.module;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import org.apache.xmlbeans.impl.regex.REUtil;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

import util.Baidu;
import util.CreateHtml;
import util.Growth;
import engine.ModuleData;
import engine.ModuleData.DataHeader;

public class AddFieldModule extends Module {

	private List<Map<String, Object>> fieldTable;

	@Override
	public ModuleData execute(ModuleData inputs) {
		ScriptEngineManager manager = new ScriptEngineManager();
		List<DataHeader> header= inputs.getHeaders();
		for (int j = 0; j < fieldTable.size(); j++) {
			header.add(new DataHeader((String) fieldTable.get(j).get("name"), "string", ""));
		}
		
		for (int i = 0; i < inputs.getRows().size(); i++) {
			Map<String, Object> map =inputs.getRows().get(i);
			boolean initScript=false;
			
			ScriptEngine engine = manager.getEngineByName("javascript");
			for (int j = 0; j < fieldTable.size(); j++) {
				
				if( fieldTable.get(j).get("type").equals("variable")){
					//变量
					map.put((String) fieldTable.get(j).get("name") , inputs.getRows().get(i).get(fieldTable.get(j).get("value")));
				}else if(fieldTable.get(j).get("type").equals("constant")){
					//常量
					map.put((String) fieldTable.get(j).get("name") , fieldTable.get(j).get("value"));
				}else if (fieldTable.get(j).get("type").equals("currentdate")){
					//当前时间
					map.put((String) fieldTable.get(j).get("name") , new Date());
				}else if (fieldTable.get(j).get("type").equals("coordinate") ){
					//坐标
					try {
						String[] address = fieldTable.get(j).get("value").toString().split(",");
						Double[] coordinate = null;
						if (address.length == 2) {
							Object area = inputs.getRows().get(i).get(address[0]);
							Object addr = inputs.getRows().get(i).get(address[1]);
							if (area == null || area.toString().length() == 0)
								area = "";
							else if (addr == null || addr.toString().length() == 0)
								addr = "";
							coordinate = Baidu.getPoint(addr.toString(), area.toString());
						} else if (address.length == 1){
							Object addr = inputs.getRows().get(i).get(address[0]);
							if (addr == null || addr.toString().length() == 0)
								addr = "";
							coordinate = Baidu.getPoint(addr.toString(), null);
						}
						String[] name=fieldTable.get(j).get("name").toString().split(",");
						map.put(name[0],coordinate[0]);    //x
						map.put(name[1],coordinate[1]);	   //y
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if(fieldTable.get(j).get("type").equals("entaccount")){
					String account= Growth.get(1L);
					map.put(fieldTable.get(j).get("name").toString(),account);
					//企业账号
				}else if(fieldTable.get(j).get("type").equals("useraccount")){
					String account= Growth.get(2L);
					map.put(fieldTable.get(j).get("name").toString(),account);
					//求职者账号
				}else if(fieldTable.get(j).get("type").equals("script")){
					
					//脚本
					
					if(!initScript){
						initScript=true;
						for (Entry<String, Object> entry : map.entrySet()) {
							engine.put(entry.getKey(), entry.getValue());
						}
					}
					try {
						engine.eval(fieldTable.get(j).get("value").toString());
						Object o=engine.get(fieldTable.get(j).get("name").toString());
						map.put(fieldTable.get(j).get("name").toString(), o);
					} catch (ScriptException e) {
						e.printStackTrace();
					}
				}else if(fieldTable.get(j).get("type").equals("createhtmlpage")){
					
					String create_time=inputs.getRows().get(i).get("create_time").toString();
					String title =inputs.getRows().get(i).get("title").toString();
					String source=inputs.getRows().get(i).get("source").toString();
					String html_content=inputs.getRows().get(i).get("html_content").toString();
					//String path=fieldTable.get(j).get("value").toString();
					PropKit.get("path");
					 
					String path="";
					
					//生成html文件
					Date date = new Date();
					String time="";
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					if(create_time!=null && !"".equals(create_time)){
						time = sdf.format(create_time);
					}else{
						time = sdf.format(date);
					}
					String fileName = "";
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("title", title);
					paramMap.put("date", time);
					paramMap.put("source", source);
					paramMap.put("content", html_content);
			
					String htmlName="";
					try {
						htmlName = CreateHtml.create(fileName ,paramMap ,path);
						//information.setHtml_name(htmlName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return inputs;
	}

}
