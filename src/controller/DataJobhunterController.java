package controller;

import static util.Validator.blank;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import engine.model.DbJobhunter;

/*
 * 控制类 - 求职者信息相关
 */
public class DataJobhunterController extends Controller {

	/*
	 * 返回到页面的json数据
	 */
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	
	/**
	 * 列表
	 */
	public void index() {
		Integer page = getParaToInt("page");
		if (blank(page))
			page = 1;
		Integer rows = getParaToInt("rows");
		if (blank(rows))
			rows = 30;
		String name = getPara("name");
		if (blank(name))
			name = "";
		Page<DbJobhunter> pager = DbJobhunter.dao.paginate(page, rows, "select id,name,data_src,data_key", "from db_jobhunter where name like ?", "%" + name + "%");
		dataMap.put("total", pager.getTotalRow());
		dataMap.put("rows", pager.getList());
		renderJson(dataMap);
	}
	
}
