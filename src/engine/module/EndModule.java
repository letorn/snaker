package engine.module;

import engine.ModuleData;

/*
 * 流程模型 - 结束
 * 一个工作流程可以有多个结束模型
 */
@SuppressWarnings("serial")
public class EndModule extends Module {

	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	public ModuleData execute(ModuleData inputs) {
		System.out.println("stop...");
		return inputs;
	}

}
