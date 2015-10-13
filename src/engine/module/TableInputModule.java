package engine.module;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import engine.ModuleData;

/*
 * 流程模型 - 表格输入
 * 通过表格的方式输入数据
 */
@SuppressWarnings("unchecked")
public class TableInputModule extends Module {

	private JSONArray dataHeaders;
	private JSONArray dataRows;
	
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	
	public ModuleData execute(ModuleData inputs) {
		Iterator<JSONObject> rowIterator = dataRows.iterator();
		while (rowIterator.hasNext()) {
			JSONObject dataRow = rowIterator.next();
			Map<String, Object> row = new HashMap<String, Object>();
			for (Object key : dataRow.keySet()) {
				row.put(key.toString(), dataRow.get(key));
			}
			inputs.add(row);
		}
		return inputs;
	}

}