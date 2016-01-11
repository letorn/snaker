package controller;

import static util.Validator.notBlank;

import java.util.HashMap;
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
	// private List<Object> dataList = new ArrayList<Object>();

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
		SkAdmin admin = SkAdmin.dao.findFirst("select account from sk_admin where account=? and password=?", account, password);
		if (notBlank(admin)) {
			dataMap.put("success", true);
			setSessionAttr("admin", admin);
			setCookie("adminAccount", admin.getStr("account"), -1);
		}
		renderJson(dataMap);
	}
	
	/**
	 * 注销
	 */
	@Clear
	public void logout() {
		removeSessionAttr("admin");
		redirect("/login.html");
	}

	/**
	 * 测试
	 */
	public void test() {
		renderJson("{success: true}");
	}
	
}
