package controller;

import com.jfinal.core.Controller;

public class IndexController extends Controller {

	/**
	 * 主页
	 */
	public void index() {
		render("/index.html");
	}

}
