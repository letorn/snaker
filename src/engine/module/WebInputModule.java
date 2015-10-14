package engine.module;

import java.util.List;
import java.util.Map;

import engine.ModuleData;

/*
 * 流程模型 - 页面爬取
 * 爬虫
 */
public class WebInputModule extends Module {

	private List<Map<String, Object>> dataPaths;
	
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	
	public ModuleData execute(ModuleData inputs) {
		return inputs;
	}

}