package controller;

import com.jfinal.core.Controller;

public class ModuleController extends Controller{

	/**
	 * 表数据录入
	 */
	public void tableinput() {
		render("/module/tableinput.html");
	}
	
}
