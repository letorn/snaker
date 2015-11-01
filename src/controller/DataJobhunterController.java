package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.UtPost;
import model.ViJobhunter;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/*
 * 控制类 - 求职者信息相关
 */
@SuppressWarnings("unchecked")
public class DataJobhunterController extends Controller {

	/*
	 * 返回到页面的json数据
	 */
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	private List<Object> dataList = new ArrayList<Object>();
	
	/**
	 * 列表
	 * page 页码
	 * rows 每页多少条记录
	 * name 求职者名称
	 */
	public void index() {
		Integer page = getParaToInt("page", 1);
		Integer rows = getParaToInt("rows", 30);
		String name = getPara("name", "");

		Page<ViJobhunter> pager = ViJobhunter.dao.paginate(page, rows, "select id,name,data_src,data_key", "from vi_jobhunter where name like ?", "%" + name + "%");
		dataMap.put("total", pager.getTotalRow());
		dataMap.put("rows", pager.getList());
		renderJson(dataMap);
	}
	
	/**
	 * 获取数据来源
	 */
	public void sources() {
		List<Record> records = Db.find("select distinct data_src value from vi_jobhunter");
		for (Record record : records)
			record.set("text", record.getStr("value"));
		records.add(0, new Record().set("text", "所有").set("value", ""));
		renderJson(records);
	}
	
	/**
	 * 获取所有岗位类别
	 */
	public void categories() {
		List<UtPost> posts = UtPost.dao.find("select p.name,p.code,pp.name parent_name,pp.code parent_code,ppp.name grand_name,ppp.code grand_code from ut_post p join ut_post pp on pp.code=p.parent join ut_post ppp on ppp.code=pp.parent");
		Map<String, Object> grandMap = new HashMap<String, Object>();
		Map<String, Object> parentMap = new HashMap<String, Object>();
		for (UtPost post : posts) {
			if (!post.getStr("grand_name").equals(grandMap.get("text"))) {
				grandMap = new HashMap<String, Object>();
				grandMap.put("text", post.getStr("grand_name"));
				grandMap.put("state", "closed");
				grandMap.put("children", new ArrayList<Map<String, Object>>());
				dataList.add(grandMap);
			}
			if (!post.getStr("parent_name").equals(parentMap.get("text"))) {
				parentMap = new HashMap<String, Object>();
				parentMap.put("text", post.getStr("parent_name"));
				parentMap.put("state", "closed");
				parentMap.put("children", new ArrayList<Map<String, Object>>());
				List<Map<String, Object>> children = (List<Map<String, Object>>) grandMap.get("children");
				children.add(parentMap);
			}
			Map<String, Object> postMap = new HashMap<String, Object>();
			postMap.put("text", post.getStr("name"));
			postMap.put("code", post.getStr("code"));
			List<Map<String, Object>> children = (List<Map<String, Object>>) parentMap.get("children");
			children.add(postMap);
		}
		renderJson(dataList);
	}
	
	/**
	 * 上传岗位信息
	 */
	public void upload() {
		String[] codes = getParaValues("codes[]");
		dataMap.put("success", true);
		renderJson(dataMap);
	}
	
}
