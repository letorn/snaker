package controller;

import static util.Validator.blank;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.UtPost;
import model.ViJobhunter;
import service.DataService;

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
	 * 数据服务类
	 */
	private DataService dataService = enhance(DataService.class);
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
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
	 * 展示指定的求职者列表
	 * categoryName 页面传入的岗位类别
	 * field 当日新增、已上传、当日上传、所有
	 * beginTime 选定的开始时间
	 * endTime 选定的结束时间
	 */
	public void display() {
		String date = dateFormat.format(new Date());
		String categoryName = getPara("category");
		String field = getPara("field");
		String beginTime = getPara("beginTime", date + " 00:00:00");
		String endTime = getPara("endTime", date + " 23:59:59");

		List<ViJobhunter> jobhunters = null;
		if ("day_new".equals(field)) {
			if ("全部".equals(categoryName)) {
				jobhunters = ViJobhunter.dao.find("select id, name, data_src, data_key from vi_jobhunter where curr_post_code in (select code from ut_post) and create_date between ? and ?", beginTime, endTime);
			} else {
				jobhunters = ViJobhunter.dao.find("select id, name, data_src, data_key from vi_jobhunter where curr_post_code=? and create_date between ? and ?", getCategoryCode(categoryName), beginTime, endTime);
			}
		} else if("syn".equals(field)) {
			if ("全部".equals(categoryName)) {
				jobhunters = ViJobhunter.dao.find("select id, name, data_src, data_key from vi_jobhunter where curr_post_code in (select code from ut_post) and create_date<? and syn_status=1", endTime);
			} else {
				jobhunters = ViJobhunter.dao.find("select id, name, data_src, data_key from vi_jobhunter where curr_post_code=? and create_date<? and syn_status=1", getCategoryCode(categoryName), endTime);
			}
		} else if("day_syn".equals(field)) {
			if ("全部".equals(categoryName)) {
				jobhunters = ViJobhunter.dao.find("select id, name, data_src, data_key from vi_jobhunter where curr_post_code in (select code from ut_post) and create_date between ? and ? and syn_status=1", beginTime, endTime);
			} else {
				jobhunters = ViJobhunter.dao.find("select id, name, data_src, data_key from vi_jobhunter where curr_post_code=? and create_date between ? and ? and syn_status=1", getCategoryCode(categoryName), beginTime, endTime);
			}
		} else {
			if ("全部".equals(categoryName)) {
				jobhunters = ViJobhunter.dao.find("select id, name, data_src, data_key from vi_jobhunter where curr_post_code in (select code from ut_post) and create_date<?", endTime);
			} else {
				jobhunters = ViJobhunter.dao.find("select id, name, data_src, data_key from vi_jobhunter where curr_post_code=? and create_date<?", getCategoryCode(categoryName), endTime);
			}
		}
		dataMap.put("total", jobhunters.size());
		dataMap.put("rows", jobhunters);
		renderJson(dataMap);
	}
	
	/**
	 * 根据岗位类别获取岗位类别编码
	 * @param categoryName
	 * @return
	 */
	private String getCategoryCode(String categoryName) {
		return UtPost.dao.findFirst("select code from ut_post where name=?", categoryName).getStr("code");
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
			Map<String, Object> nodeMap = new HashMap<String, Object>();
			nodeMap.put("text", post.getStr("name"));
			nodeMap.put("code", post.getStr("code"));
			List<Map<String, Object>> children = (List<Map<String, Object>>) parentMap.get("children");
			children.add(nodeMap);
		}
		renderJson(dataList);
	}
	
	/**
	 * 上传岗位信息
	 */
	public void upload() {
		Long[] ids = getParaValuesToLong("ids[]");
		String[] codes = getParaValues("codes[]");
		if (blank(ids))
			ids = new Long[0];
		if (blank(codes))
			codes = new String[0];
		if (dataService.isPostJobhunterFinished()) {
			dataMap.put("success", dataService.postJobhunter(ids, codes));
		} else {
			dataMap.put("success", false);
			dataMap.put("finished", false);
		}
		renderJson(dataMap);
	}
	
	/**
	 * 求职者数据汇总
	 */
	public void sum() {
		String date = dateFormat.format(new Date());
		String beginTime = getPara("beginTime", date + " 00:00:00");
		String endTime = getPara("endTime", date + " 23:59:59");
		List<Record> records = Db.find("select p.name, "
				+ "(select count(*) from vi_jobhunter where curr_post_code=p.code and create_date<?) total, "
				+ "(select count(*) from vi_jobhunter where curr_post_code=p.code and create_date between ? and ?) day_new, "
				+ "(select count(*) from vi_jobhunter where curr_post_code=p.code and create_date<? and syn_status=1) syn, "
				+ "(select count(*) from vi_jobhunter where curr_post_code=p.code and create_date between ? and ? and syn_status=1) day_syn "
				+ "from ut_post p", endTime, beginTime, endTime, endTime, beginTime, endTime);
		renderJson(records);
	}
	
}
