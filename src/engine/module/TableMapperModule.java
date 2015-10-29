package engine.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import engine.ModuleData;
import engine.ModuleData.DataHeader;

/*
 * 流程模型 - 表格映射
 * 通过表格的方式映射数据
 */
@SuppressWarnings("serial")
public class TableMapperModule extends Module {

	private List<Map<String, Object>> dataHeaders;
	private List<List<Map<String, Object>>> dataMappers;
	
	private Map<String, Map<Object, Object>> mapper = new HashMap<String, Map<Object,Object>>();
	
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	public ModuleData execute(ModuleData inputs) {
		for (int i = 0; i < dataHeaders.size(); i++) {
			Map<String, Object> dataHeader = dataHeaders.get(i);
			List<Map<String, Object>> dataMapper = dataMappers.get(i);
			Map<Object, Object> mp = new HashMap<Object, Object>();
			for (Map<String, Object> dataMp : dataMapper)
				mp.put(dataMp.get("from"), dataMp.get("to"));
			mapper.put((String) dataHeader.get("name"), mp);
		}
		Set<String> needSet = new HashSet<String>();
		for (String key : mapper.keySet()) {
			for (DataHeader header : inputs.getHeaders()) {
				if (key.equals(header.getName()))
					;
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
