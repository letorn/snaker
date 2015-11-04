package controller;

import static util.Validator.blank;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.UtIndustry;
import model.ViEnterprise;
import service.DataService;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/*
 * 控制类 - 企业信息相关
 */
@SuppressWarnings("unchecked")
public class DataEnterpriseController extends Controller {

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
	 * name 企业名称
	 */
	public void index() {
		Integer page = getParaToInt("page", 1);
		Integer rows = getParaToInt("rows", 30);
		String source = getPara("source", "");
		String name = getPara("name", "");
		
		Page<ViEnterprise> pager = blank(source) ?
									ViEnterprise.dao.paginate(page, rows, "select id,name,data_src,data_key", "from vi_enterprise where name like ?", "%" + name + "%") : 
									ViEnterprise.dao.paginate(page, rows, "select id,name,data_src,data_key", "from vi_enterprise where data_src=? and name like ?", source, "%" + name + "%");
		dataMap.put("total", pager.getTotalRow());
		dataMap.put("rows", pager.getList());
		renderJson(dataMap);
	}
	
	/**
	 * 获取数据来源
	 */
	public void sources() {
		List<Record> records = Db.find("select distinct data_src value from vi_enterprise");
		for (Record record : records)
			record.set("text", record.getStr("value"));
		records.add(0, new Record().set("text", "所有").set("value", ""));
		renderJson(records);
	}
	
	/**
	 * 获取所有企业所属行业
	 */
	public void categories() {
		List<UtIndustry> industries = UtIndustry.dao.find("select i.name,i.code,ii.name parent_name,ii.code parent_code from ut_industry i join ut_industry ii on ii.code=i.parent");
		
		Map<String, Object> parentMap = new HashMap<String, Object>();
		for (UtIndustry industry : industries) {
			if (!industry.getStr("parent_name").equals(parentMap.get("text"))) {
				parentMap = new HashMap<String, Object>();
				parentMap.put("text", industry.getStr("parent_name"));
				parentMap.put("state", "closed");
				parentMap.put("children", new ArrayList<Map<String, Object>>());
				dataList.add(parentMap);
			}
			Map<String, Object> nodeMap = new HashMap<String, Object>();
			nodeMap.put("text", industry.getStr("name"));
			nodeMap.put("code", industry.getStr("code"));
			List<Map<String, Object>> children = (List<Map<String, Object>>) parentMap.get("children");
			children.add(nodeMap);
		}
		renderJson(dataList);
	}
	
	/**
	 * 上传企业信息
	 */
	public void upload() {
		Long[] ids = getParaValuesToLong("ids[]");
		String[] codes = getParaValues("codes[]");
		if (blank(ids))
			ids = new Long[0];
		if (blank(codes))
			codes = new String[0];
		if (dataService.isPostEnterpriseFinished()) {
			dataMap.put("success", dataService.postEnterprise(ids, codes));
		} else {
			dataMap.put("success", false);
			dataMap.put("finished", false);
		}
		renderJson(dataMap);
	}
	
	/**
	 * 企业数据汇总
	 */
	public void sum() {
		String date = dateFormat.format(new Date());
		String beginTime = getPara("beginTime", date + " 00:00:00");
		String endTime = getPara("endTime", date + " 23:59:59");
		List<Record> records = Db.find("select i.name, "
				+ "(select count(*) from db_enterprise where category_code=i.code and create_date<?) total, "
				+ "(select count(*) from db_enterprise where category_code=i.code and create_date between ? and ?) day_new, "
				+ "(select count(*) from db_enterprise where category_code=i.code and create_date<? and syn_status=1) syn, "
				+ "(select count(*) from db_enterprise where category_code=i.code and create_date between ? and ? and syn_status=1) day_syn "
				+ "from ut_industry i", endTime, beginTime, endTime, endTime, beginTime, endTime);
		renderJson(records);
	}
	
}
