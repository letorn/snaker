package controller;

import static util.Validator.notBlank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.SkAdmin;

import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;

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
	 * 注销
	 */
	@Clear
	public void test() {
		// areas
		// List<Record> records = Db.use("zcdh").find("select area_code id, area_name name from zcdh_area where is_delete=1 and parent_code='-1' and area_code!='008.014'");
		// for (Record record : records)
		// 	record.set("children", Db.use("zcdh").find("select area_code id, area_name name from zcdh_area where is_delete=1 and parent_code=?", record.getStr("id")));
		
		// majors
		// List<Record> records = Db.use("zcdh").find("select code id, major_name name from zcdh_jobhunte_major where parent_code regexp '^[0-9]{3}$'");
		// for (Record record : records)
		// 	record.set("children", Db.use("zcdh").find("select code id, major_name name from zcdh_jobhunte_major where parent_code=?", record.getStr("id")));
		// renderJson(Db.use("zcdh").find("select area_code code, area_name name, if(parent_code=-1,0,parent_code) parent, if(parent_code=-1,'true','false') nocheck from zcdh_area where is_delete=1 and (parent_code = -1 or parent_code regexp '^[0-9]{3}$')"));
		// renderJson(Db.use("zcdh").find("select industry_code code, industry_name name, if(parent_code=-1,0,parent_code) parent from zcdh_industry where is_delete=1 and (parent_code = -1 or parent_code regexp '^[0-9]{3}$')"));
		// renderJson(Db.use("zcdh").find("select post_category_code code, post_category_name name, if(parent_code=-1,0,parent_code) parent, 'true' nocheck from zcdh_category_post where is_delete=1 union select post_code code, post_name name, post_category_code parent, 'false' nocheck from zcdh_post where is_delete=1 and post_name !=''"));
		Db.execute(new ICallback() {
			public Object call(Connection conn) throws SQLException {
				PreparedStatement ps = conn.prepareStatement("select id from ? where parent=?");
				ps.setString(1, "ut_area");
				ps.setString(2, "001");
				ResultSet rs = ps.executeQuery();
				System.out.println(rs);
				return null;
			}
		});
		renderJson("{success: true}");
	}
	
}
