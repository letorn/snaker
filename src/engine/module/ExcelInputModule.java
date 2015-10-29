package engine.module;

import java.io.File;

import engine.ModuleData;

/*
 * 流程模型 - Excel输入
 * 通过Excel文件输入数据
 */
public class ExcelInputModule extends Module {

	private File excelFile;
	
	
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	
	public ModuleData execute(ModuleData inputs) {
		System.out.println(excelFile);
		return inputs;
	}

}