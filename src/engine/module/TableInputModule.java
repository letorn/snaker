package engine.module;

import java.util.List;
import java.util.Map;

import engine.ModuleData;

/*
 * 流程模型 - 表格输入
 * 通过表格的方式输入数据
 */
@SuppressWarnings("serial")
public class TableInputModule extends Module {

	private List<Map<String, Object>> dataHeaders;
	private List<Map<String, Object>> dataRows;
	
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	
	public ModuleData execute(ModuleData inputs) {
		inputs.addAll(dataRows);
		return inputs;
	}

}