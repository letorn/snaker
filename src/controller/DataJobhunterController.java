package controller;

import static util.Validator.blank;
import static util.Validator.notBlank;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import model.UtPost;
import model.ViJobhunter;
import service.DataService;
import util.Growth;

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
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
	/**
	 * 列表
	 * page 页码
	 * rows 每页多少条记录
	 * name 求职者名称
	 */
	public void index() {
		Integer page = getParaToInt("page", 1);
		Integer rows = getParaToInt("rows", 30);
		String source = getPara("source", "");
		String name = getPara("name", "");
		if (page < 1) page = 1;
		if (rows < 1) rows = 1;

		Page<ViJobhunter> pager = blank(source) ?
				ViJobhunter.dao.paginate(page, rows, "select id,name,data_src,data_key,syn_status", "from vi_jobhunter where name like ?", "%" + name + "%") :
				ViJobhunter.dao.paginate(page, rows, "select id,name,data_src,data_key,syn_status", "from vi_jobhunter where data_src=? and name like ?", source, "%" + name + "%");
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
		Integer page = getParaToInt("page", 1);
		Integer rows = getParaToInt("rows", 20);
		String beginTime = getPara("beginTime", date + " 00:00:00");
		String endTime = getPara("endTime", date + " 23:59:59");

		Page<ViJobhunter> jobhunters = null;
		if ("day_new".equals(field)) {
			if ("全部".equals(categoryName)) {
				jobhunters = ViJobhunter.dao.paginate(page, rows, "select id, name, data_src, data_key", "from vi_jobhunter where curr_post_code in (select code from ut_post) and create_date between ? and ?", beginTime, endTime);
			} else {
				jobhunters = ViJobhunter.dao.paginate(page, rows, "select id, name, data_src, data_key", "from vi_jobhunter where curr_post_code=? and create_date between ? and ?", getCategoryCode(categoryName), beginTime, endTime);
			}
		} else if("syn".equals(field)) {
			if ("全部".equals(categoryName)) {
				jobhunters = ViJobhunter.dao.paginate(page, rows, "select id, name, data_src, data_key", "from vi_jobhunter where curr_post_code in (select code from ut_post) and create_date<? and syn_status=1", endTime);
			} else {
				jobhunters = ViJobhunter.dao.paginate(page, rows, "select id, name, data_src, data_key", "from vi_jobhunter where curr_post_code=? and create_date<? and syn_status=1", getCategoryCode(categoryName), endTime);
			}
		} else if("day_syn".equals(field)) {
			if ("全部".equals(categoryName)) {
				jobhunters = ViJobhunter.dao.paginate(page, rows, "select id, name, data_src, data_key", "from vi_jobhunter where curr_post_code in (select code from ut_post) and create_date between ? and ? and syn_status=1", beginTime, endTime);
			} else {
				jobhunters = ViJobhunter.dao.paginate(page, rows, "select id, name, data_src, data_key", "from vi_jobhunter where curr_post_code=? and create_date between ? and ? and syn_status=1", getCategoryCode(categoryName), beginTime, endTime);
			}
		} else {
			if ("全部".equals(categoryName)) {
				jobhunters = ViJobhunter.dao.paginate(page, rows, "select id, name, data_src, data_key", "from vi_jobhunter where curr_post_code in (select code from ut_post) and create_date<?", endTime);
			} else {
				jobhunters = ViJobhunter.dao.paginate(page, rows, "select id, name, data_src, data_key", "from vi_jobhunter where curr_post_code=? and create_date<?", getCategoryCode(categoryName), endTime);
			}
		}
		dataMap.put("total", jobhunters.getTotalRow());
		dataMap.put("rows", jobhunters.getList());
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
	public void getUser(){
		String id = getPara("id");
		Record record = Db.findFirst("SELECT id,name,account,gender,nation,mobile,email,qq,experience,experience_code,education,education_code,"
				+ "major,household,polity,category,category_code,hunter_status,hunter_status_code,marriage,cert_type,cert_id,birth,height,weight,location,"
				+ "location_code,address,lbs_lon,lbs_lat,curr_ent,curr_ent_phone,curr_post,curr_post_code,self_comment,syn_status FROM vi_jobhunter where id="+id);
		renderJson(record);
	}
	
	public void saveUser(){
		String id =getPara("id");
		String name =getPara("name");
		String account =getPara("account");
		String gender =getPara("gender");
		String nation =getPara("nation");
		String mobile =getPara("mobile");
		String email =getPara("email");
		String qq =getPara("qq");
		String experience = getPara("experience");
		String experience_code =getPara("experience_code");
		String education =getPara("education");
		String education_code =getPara("education_code");
		String major =getPara("major");
		String lons=getPara("lbs_lon");
		String lats=getPara("lbs_lat");
		Object lbs_lon=null;
		Object lbs_lat=null;
		if(notBlank(lons) && notBlank(lats)){
			lbs_lon =Double.parseDouble(getPara("lbs_lon"));
			lbs_lat =Double.parseDouble(getPara("lbs_lat"));
		}
		String household =getPara("household");
		String category =getPara("category");
		String category_code =getPara("category_code");
		String hunter_status =getPara("hunter_status");
		String hunter_status_code =getPara("hunter_status_code");
		String marriage =getPara("marriage");
		String cert_type =getPara("cert_type");
		String cert_id =getPara("cert_id");
		Date update_date = null;
		Date create_date = null;
		Date birth= null;
		try {
			update_date = getPara("update_date")==null|| getPara("update_date").equals("")?new Date():sdf.parse(getPara("update_date"));
			create_date =getPara("create_date")==null|| getPara("create_date").equals("")?new Date():sdf.parse(getPara("create_date"));
			birth =getPara("birth")==null|| getPara("birth").equals("")?new Date():getParaToDate("birth");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String location =getPara("location");
		String location_code =getPara("location_code");
		String address =getPara("address");
		String curr_ent =getPara("curr_ent");
		String curr_ent_phone =getPara("curr_ent_phone");
		String curr_post =getPara("curr_post");
		String curr_post_code =getPara("curr_post_code");
		String self_comment =getPara("self_comment");
		
		boolean isSuccess=true;
		ViJobhunter vj = null;
		if(id!=null && !id.equals("")){
			vj = ViJobhunter.dao.findById(id);
			if (ViJobhunter.dao.findFirst("select mobile from vi_jobhunter where account=?", account).get("mobile").equals(mobile)) {
				isSuccess=vj.set("name",name)
						.set("account",account)
						.set("gender",gender)
						.set("nation",nation)
						.set("mobile",mobile)
						.set("account",account)
						.set("email",email)
						.set("qq",qq)
						.set("experience",experience)
						.set("experience_code",experience_code)
						.set("education",education)
						.set("education_code",education_code)
						.set("major",major)
						.set("lbs_lon",lbs_lon)
						.set("lbs_lat",lbs_lat)
						.set("household",household)
						.set("category",category)
						.set("category_code",category_code)
						.set("hunter_status",hunter_status)
						.set("hunter_status_code",hunter_status_code)
						.set("marriage",marriage)
						.set("cert_type",cert_type)
						.set("cert_id",cert_id)
						.set("update_date",update_date)
						.set("create_date",create_date)
						.set("birth",birth)
						.set("location",location)
						.set("location_code",location_code)
						.set("address",address)
						.set("curr_ent",curr_ent)
						.set("curr_ent_phone",curr_ent_phone)
						.set("curr_post",curr_post)
						.set("curr_post_code",curr_post_code)
						.set("syn_status", 2)
						.set("self_comment",self_comment).update();
			} else {
				isSuccess = false;
				dataMap.put("msg", "保存失败！账号已存在！");
			}
		}else{
			vj = new ViJobhunter();
			if (ViJobhunter.dao.findFirst("select account from vi_jobhunter where account=?", account) == null) {
				isSuccess=vj.set("name",name)
					.set("account",account)
					.set("account_status",2)
					.set("gender",gender)
					.set("nation",nation)
					.set("mobile",mobile)
					.set("account",account)
					.set("email",email)
					.set("qq",qq)
					.set("experience",experience)
					.set("experience_code",experience_code)
					.set("education",education)
					.set("education_code",education_code)
					.set("major",major)
					.set("lbs_lon",lbs_lon)
					.set("lbs_lat",lbs_lat)
					.set("household",household)
					.set("category",category)
					.set("category_code",category_code)
					.set("hunter_status",hunter_status)
					.set("hunter_status_code",hunter_status_code)
					.set("marriage",marriage)
					.set("cert_type",cert_type)
					.set("cert_id",cert_id)
					.set("update_date",update_date)
					.set("create_date",create_date)
					.set("birth",birth)
					.set("location",location)
					.set("location_code",location_code)
					.set("address",address)
					.set("curr_ent",curr_ent)
					.set("curr_ent_phone",curr_ent_phone)
					.set("curr_post",curr_post)
					.set("curr_post_code",curr_post_code)
					.set("data_src","snaker")
					.set("data_key",mobile)
					.set("self_comment",self_comment).save();
			} else {
				isSuccess = false;
				dataMap.put("msg", "保存失败！账号已存在！");
			}
		}
		dataMap.put("id", vj.getLong("id"));
		dataMap.put("success", isSuccess);
		renderJson(dataMap);
	}
	public void deleteUser(){
		String id=getPara("id");
		boolean isSuccess=ViJobhunter.dao.findById(id).delete();
		dataMap.put("success", isSuccess);
		renderJson(dataMap);
	}
}
