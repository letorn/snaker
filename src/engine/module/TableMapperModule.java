package engine.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import engine.ModuleData;
import engine.ModuleData.DataHeader;

/*
 * 流程模型 - 表格映射
 * 通过表格的方式映射数据
 */
@SuppressWarnings("unchecked")
public class TableMapperModule extends Module {

	private JSONArray dataHeaders;
	private JSONArray dataMappers;
	
	private Map<String, Map<Object, Object>> mapper = new HashMap<String, Map<Object,Object>>();
	
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	public ModuleData execute(ModuleData inputs) {
		Iterator<JSONObject> headerIterator = dataHeaders.iterator();
		Iterator<JSONArray> mapperIterator = dataMappers.iterator();
		while (headerIterator.hasNext() && mapperIterator.hasNext()) {
			JSONObject jsonHeader = headerIterator.next();
			JSONArray jsonMps = mapperIterator.next();
			Iterator<JSONObject> mpIterator = jsonMps.iterator();
			Map<Object, Object> mp = new HashMap<Object, Object>();
			while (mpIterator.hasNext()) {
				JSONObject jsonMp = mpIterator.next();
				mp.put(jsonMp.get("from"), jsonMp.get("to"));
			}
			mapper.put(jsonHeader.getString("name"), mp);
		}
		Set<String> needSet = new HashSet<String>();
		for (String key : mapper.keySet()) {
			for (DataHeader header : inputs.getHeaders()) {
				if (key.equals(header.getText()));
				needSet.add(key);
			}
		}
		for (Map<String, Object> input : inputs.getRows()) {
			for (String key : needSet) {
				Object oldValue = input.get(key);
				if (oldValue != null) {
					input.put(key, mapper.get(key).get(oldValue));
				}
			}
		}
		
		
		return inputs;
	}

}
