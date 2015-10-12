package controller;

import com.jfinal.core.Controller;

/*
 * 控制类 - 模型相关
 */
public class ModuleController extends Controller{

	/**
	 * 开始
	 */
	public void begin() {
		render("/module/begin.html");
	}

	/**
	 * 结束
	 */
	public void end() {
		render("/module/end.html");
	}
	
	/**
	 * 表数据录入
	 */
	public void tableinput() {
		render("/module/tableinput.html");
	}
	
}
