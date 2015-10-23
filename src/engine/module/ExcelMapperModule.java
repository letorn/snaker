package engine.module;

import engine.ModuleData;

/*
 * 流程模型 - Excel映射
 * 通过Excel文件映射数据
 */
public class ExcelMapperModule extends Module {

	private String excelFile;
	
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	public ModuleData execute(ModuleData inputs) {
		return inputs;
	}

}
