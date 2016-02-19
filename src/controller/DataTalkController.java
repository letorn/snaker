package controller;

import static util.Validator.blank;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.ViTalk;
import service.DataService;
import util.DateKit;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/*
 * 控制类 - 宣讲会信息相关
 */
public class DataTalkController extends Controller {

	/*
	 * 数据服务类
	 */
	private DataService dataService = enhance(DataService.class);
	
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
		String source = getPara("source");
		Integer status = getParaToInt("status");
		String title = getPara("title", "");
		if (page < 1) page = 1;
		if (rows < 1) rows = 1;

		Page<ViTalk> pager = blank(source) ?
								(blank(status) ?
									ViTalk.dao.paginate(page, rows, "select id,title,data_src,data_key,syn_status,syn_message", "from vi_talk where title like ?", "%" + title + "%") :
									ViTalk.dao.paginate(page, rows, "select id,title,data_src,data_key,syn_status,syn_message", "from vi_talk where syn_status=? and title like ?", status, "%" + title + "%")) :
								(blank(status) ?
									ViTalk.dao.paginate(page, rows, "select id,title,data_src,data_key,syn_status,syn_message", "from vi_talk where data_src=? and title like ?", source, "%" + title + "%") :
									ViTalk.dao.paginate(page, rows, "select id,title,data_src,data_key,syn_status,syn_message", "from vi_talk where data_src=? and syn_status=? and title like ?", source, status, "%" + title + "%")
								);
		dataMap.put("total", pager.getTotalRow());
		dataMap.put("rows", pager.getList());
		renderJson(dataMap);
	}
	
	/**
	 * 获取数据来源
	 */
	public void sources() {
		List<Record> records = Db.find("select distinct data_src value from vi_talk");
		for (Record record : records)
			record.set("text", record.getStr("value"));
		records.add(0, new Record().set("text", "所有").set("value", ""));
		renderJson(records);
	}
	
	/**
	 * 展示指定学校宣讲会列表
	 * source 学校名称
	 * field 当日新增、已上传、当日上传、所有
	 * beginTime 选定的开始时间
	 * endTime 选定的结束时间
	 */
	public void display() {
		String date = DateKit.toString(new Date());
		String source = getPara("source");
		String field = getPara("field");
		Integer page = getParaToInt("page", 1);
		Integer rows = getParaToInt("rows", 20);
		String beginTime = getPara("beginTime", date + " 00:00:00");
		String endTime = getPara("endTime", date + " 23:59:59");

		Page<ViTalk> talks = null;
		if ("day_new".equals(field)) {
			if ("全部".equals(source)) {
				talks = ViTalk.dao.paginate(page, rows, "select id, title, data_src, data_key", "from vi_talk where create_date between ? and ?", beginTime, endTime);
			} else {
				talks = ViTalk.dao.paginate(page, rows, "select id, title, data_src, data_key", "from vi_talk where source=? and create_date between ? and ?", source, beginTime, endTime);
			}
		} else if("syn".equals(field)) {
			if ("全部".equals(source)) {
				talks = ViTalk.dao.paginate(page, rows, "select id, title, data_src, data_key", "from vi_talk where create_date<? and syn_status=1", endTime);
			} else {
				talks = ViTalk.dao.paginate(page, rows, "select id, title, data_src, data_key", "from vi_talk where source=? and create_date<? and syn_status=1", source, endTime);
			}
		} else if("day_syn".equals(field)) {
			if ("全部".equals(source)) {
				talks = ViTalk.dao.paginate(page, rows, "select id, title, data_src, data_key", "from vi_talk where create_date between ? and ? and syn_status=1", beginTime, endTime);
			} else {
				talks = ViTalk.dao.paginate(page, rows, "select id, title, data_src, data_key", "from vi_talk where source=? and create_date between ? and ? and syn_status=1", source, beginTime, endTime);
			}
		} else {
			if ("全部".equals(source)) {
				talks = ViTalk.dao.paginate(page, rows, "select id, title, data_src, data_key", "from vi_talk where create_date<?", endTime);
			} else {
				talks = ViTalk.dao.paginate(page, rows, "select id, title, data_src, data_key", "from vi_talk where source=? and create_date<?", source, endTime);
			}
		}
		dataMap.put("total", talks.getTotalRow());
		dataMap.put("rows", talks.getList());
		renderJson(dataMap);
	}
	
	/**
	 * 获取所有岗位类别
	 */
	public void categories() {
		List<ViTalk> talks = ViTalk.dao.find("select distinct source from vi_talk");
		for (ViTalk talk : talks) {
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
		String date = DateKit.toString(new Date());
		String beginTime = getPara("beginTime", date + " 00:00:00");
		String endTime = getPara("endTime", date + " 23:59:59");
		List<Record> records = Db.find("select t.source name, "
				+ "(select count(*) from vi_talk where source=t.source and create_date<?) total, "
				+ "(select count(*) from vi_talk where source=t.source and create_date between ? and ?) day_new, "
				+ "(select count(*) from vi_talk where source=t.source and create_date<? and syn_status=1) syn, "
				+ "(select count(*) from vi_talk where source=t.source and create_date between ? and ? and syn_status=1) day_syn "
				+ "from (select distinct source from vi_talk) t", endTime, beginTime, endTime, endTime, beginTime, endTime);
		renderJson(records);
	}
	/**
	 * 获取宣讲会
	 */
	public void getTalk(){
		String id = getPara("id");
		List<Record> records =Db.find("SELECT id,title,content,source FROM vi_talk where id ="+id +" limit 1");
		renderJson(records);
	}
	
	public void saveTalk(){
		String id=getPara("id");
		String title=getPara("title");
		String content=getPara("content");
		String source=getPara("source");
		Date update_time=getParaToDate("update_time");
		Date create_time=getParaToDate("create_time");
		boolean isSuccess=false;
		ViTalk vt = null;
		if(id==null ||id.equals("")){
			vt = new ViTalk();
			isSuccess=vt
					.set("title", title)
					.set("content", content)
					.set("source", source)
					.set("data_src", "snaker")
					.set("data_key", source + title)
					.set("update_date", update_time)
					.set("create_date", create_time).save();
		}else{
			vt = ViTalk.dao.findById(id);
			isSuccess=vt
				.set("title", title)
				.set("content", content)
				.set("source", source)
				.set("syn_status", 2)
				.set("update_date", update_time)
				.set("create_date", create_time).update();
		}
		dataMap.put("id", vt.getLong("id"));
		dataMap.put("success", isSuccess);
		renderJson(dataMap);
	}
	
	/**
	 * 删除宣讲会 
	 */
	public void deleteTalk(){
		String id=getPara("id");
		boolean isSuccess=ViTalk.dao.findById(id).delete();
		dataMap.put("success", isSuccess);
		renderJson(dataMap);
	}
	
}
