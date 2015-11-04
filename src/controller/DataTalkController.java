package controller;

import static util.Validator.blank;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.DataService;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import engine.model.DbTalk;

/*
 * 控制类 - 宣讲会信息相关
 */
public class DataTalkController extends Controller {

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
		String title = getPara("title", "");

		Page<DbTalk> pager = DbTalk.dao.paginate(page, rows, "select id,title,data_src,data_key", "from db_talk where title like ?", "%" + title + "%");
		dataMap.put("total", pager.getTotalRow());
		dataMap.put("rows", pager.getList());
		renderJson(dataMap);
	}
	
	/**
	 * 获取数据来源
	 */
	public void sources() {
		List<Record> records = Db.find("select distinct data_src value from db_talk");
		for (Record record : records)
			record.set("text", record.getStr("value"));
		records.add(0, new Record().set("text", "所有").set("value", ""));
		renderJson(records);
	}
	
	/**
	 * 获取所有岗位类别
	 */
	public void categories() {
		List<DbTalk> talks = DbTalk.dao.find("select distinct source from db_talk");
		for (DbTalk talk : talks) {
			Map<String, Object> nodeMap = new HashMap<String, Object>();
			nodeMap.put("text", talk.getStr("source"));
			nodeMap.put("code", talk.getStr("source"));
			dataList.add(nodeMap);
		}
		renderJson(dataList);
	}
	
	/**
	 * 上传宣讲会信息
	 */
	public void upload() {
		Long[] ids = getParaValuesToLong("ids[]");
		String[] codes = getParaValues("codes[]");
		if (blank(ids))
			ids = new Long[0];
		if (blank(codes))
			codes = new String[0];
		if (dataService.isPostTalkFinished()) {
			dataMap.put("success", dataService.postTalk(ids, codes));
		} else {
			dataMap.put("success", false);
			dataMap.put("finished", false);
		}
		renderJson(dataMap);
	}
	
	/**
	 * 宣讲会数据汇总
	 */
	public void sum() {
		String date = dateFormat.format(new Date());
		String beginTime = getPara("beginTime", date + " 00:00:00");
		String endTime = getPara("endTime", date + " 23:59:59");
		List<Record> records = Db.find("select t.source name, "
				+ "(select count(*) from db_talk where source=t.source and create_date<?) total, "
				+ "(select count(*) from db_talk where source=t.source and create_date between ? and ?) day_new, "
				+ "(select count(*) from db_talk where source=t.source and create_date<? and syn_status=1) syn, "
				+ "(select count(*) from db_talk where source=t.source and create_date between ? and ? and syn_status=1) day_syn "
				+ "from (select distinct source from db_talk) t", endTime, beginTime, endTime, endTime, beginTime, endTime);
		renderJson(records);
	}
	
}
