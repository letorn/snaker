package controller;

import com.jfinal.core.Controller;

/*
 * 控制类 - 模型相关
 */
public class ModuleController extends Controller{

	/**
	 * 表数据录入
	 */
	public void tableinput() {
		render("/module/tableinput.html");
	}
	
}
