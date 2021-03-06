package engine.module;

import engine.ModuleData;

/*
 * 流程模型 - 开始
 * 一个工作流程有且只有一个开始模型
 */
public class BeginModule extends Module {

	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	public ModuleData execute(ModuleData inputs) {
		log.info("开始执行工作流程：" + workflow.getProcessName() + "." + name);
		return inputs;
	}
	
}
