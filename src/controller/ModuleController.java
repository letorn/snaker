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
		Long processId = getParaToLong("process");
		
		setAttr("process", processId);
		
		render("/module/begin.html");
	}

	/**
	 * 结束
	 */
	public void end() {
		Long processId = getParaToLong("process");
		
		setAttr("process", processId);
		render("/module/end.html");
	}
	
	/**
	 * 表数据录入
	 */
	public void tableinput() {
		Long processId = getParaToLong("process");
		
		setAttr("process", processId);
		render("/module/tableinput.html");
	}
	
	/**
	 * 表数据录入
	 */
	public void basiclog() {
		Long processId = getParaToLong("process");
		
		setAttr("process", processId);
		render("/module/basiclog.html");
	}
	
}
