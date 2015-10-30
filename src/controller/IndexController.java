package controller;

import com.jfinal.core.Controller;

/*
 * 控制类 - 主页相关
 */
public class IndexController extends Controller {

	/**
	 * 主页
	 */
	public void index() {
		render("/index.html");
	}

}
