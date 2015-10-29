package controller;

import com.jfinal.core.Controller;

import engine.model.WfRecord;

/*
 * 控制类 - 主页相关
 */
public class IndexController extends Controller {

	/**
	 * 主页
	 */
	public void index() {
		new WfRecord().set("headers", null).save();
		render("/index.html");
	}

}
