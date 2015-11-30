package controller;

import static util.Validator.notBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.SkAdmin;

import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;

/*
 * 控制类 - 主页相关
 */
public class IndexController extends Controller {

	/*
	 * 返回到页面的json数据
	 */
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	private List<Object> dataList = new ArrayList<Object>();

	/**
	 * 主页
	 */
	public void index() {
		render("/index.html");
	}

	/**
	 * 登录
	 */
	@Clear
	public void login() {
		String account = getPara("account");
		String password = getPara("password");
		SkAdmin admin = SkAdmin.dao.findFirst("select * from sk_admin where account=? and password=?", account, password);
		if (notBlank(admin)) {
			setSessionAttr("admin", admin);
			redirect("/index.html");
		} else {
			setAttr("error", "用户名或密码错误！");
			render("/login.html");
		}
	}

}
