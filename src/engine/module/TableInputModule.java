package engine.module;

import engine.ModuleData;

/*
 * 流程模型 - 表格录入
 * 通过表格的方式录入数据
 */
public class TableInputModule extends Module {

	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	public ModuleData execute(ModuleData inputs) {
		System.out.println("tableinput...");
		return inputs;
	}

}
