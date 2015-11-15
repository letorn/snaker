package controller;

import static util.Validator.blank;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.UtArea;
import model.UtIndustry;
import model.ViEnterprise;
import model.ViEntpost;
import service.DataService;
import test.Area;

import com.alibaba.fastjson.JSON;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/*
 * 控制类 - 企业信息相关
 */
@SuppressWarnings("unchecked")
public class DataEntpostController extends Controller {

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
	public void list() {
		Integer page = getParaToInt("page", 1);
		Integer rows = getParaToInt("rows", 30);
		String data_key = getPara("data_key");
		if (page < 1) page = 1;
		if (rows < 1) rows = 1;
		
		//String name = getPara("name", "");
				Page<ViEntpost> pager =ViEntpost.dao.paginate(page, rows, "select id,name,headcount,nature,address,education", "from vi_entpost where data_ent_key ='"+data_key+"'") ; 
									
		dataMap.put("total", pager.getTotalRow());
		dataMap.put("rows", pager.getList());
		renderJson(dataMap);
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
	
	
	public void getEntpost(){
		String id = getPara("id");
		Record record = Db.findFirst("SELECT id,name,category,category_code,nature,nature_code,headcount,age,gender,salary,experience,experience_code,"
				+ "education,education_code,tag,introduction,area,area_code,address,lbs_lon,lbs_lat,data_src,data_key,data_ent_key,update_date,"
				+ "create_date,syn_status,syn_date,syn_message FROM   vi_entpost   WHERE id ="+id);
		renderJson(record);
	}
	public void saveEntpost(){
		String id =getPara("id");
		String name =getPara("name");
		String category =getPara("category");
		String category_code =getPara("category_code");
		String nature =getPara("nature");
		String nature_code =getPara("nature_code");
		Integer headcount =getParaToInt("headcount");
		String age =getPara("age");
		String tag =getPara("tag");
		String gender = getPara("gender").equals("")?null:getPara("gender");
		String introduction =getPara("introduction");
		String area =getPara("area");
		String area_code =getPara("area_code");
		String address =getPara("address");
		String lons=getPara("lbs_lon");
		String lats=getPara("lbs_lat");
		Object lbs_lon=null;
		Object lbs_lat=null;
		if(lons!=null && lats!=null){
		lbs_lon =Double.parseDouble(getPara("lbs_lon"));
		lbs_lat =Double.parseDouble(getPara("lbs_lat"));
		}
		String salary =getPara("salary");
		String experience =getPara("experience");
		String experience_code =getPara("experience_code");
		String education =getPara("education");
		String education_code =getPara("education_code");
		String data_ent_key =getPara("data_ent_key");
		String data_src =getPara("data_src");
		String data_key =getPara("data_key");
		Date update_date =getParaToDate("update_date")==null?new Date():getParaToDate("update_date");
		Date create_date =getParaToDate("create_date")==null?new Date():getParaToDate("create_date");
	
		boolean isSuccess=true;
		if(id!=null && !id.equals("")){
			isSuccess=ViEntpost.dao.findById(id).set("name",name)
				.set("category",category)
				.set("category_code",category_code)
				.set("nature",nature)
				.set("nature_code",nature_code)
				.set("headcount", headcount)
				.set("age",age)
				.set("gender",gender)
				.set("tag",tag)
				.set("introduction",introduction)
				.set("area",area)
				.set("area_code",area_code)
				.set("address",address)
				.set("lbs_lon",lbs_lon)
				.set("lbs_lat",lbs_lat)
				.set("salary",salary)
				.set("experience",experience)
				.set("experience_code",experience_code)
				.set("education",education)
				.set("education_code",education_code)
				.set("data_ent_key",data_ent_key)
				.set("data_src",data_src)
				.set("data_key",data_key)
				.set("update_date",update_date)
				.set("create_date",create_date).update();
		}else{
			isSuccess=new ViEntpost().set("name",name)
					.set("category",category)
					.set("category_code",category_code)
					.set("nature",nature)
					.set("nature_code",nature_code)
					.set("headcount", headcount)
					.set("age",age)
					.set("gender",gender)
					.set("tag",tag)
					.set("introduction",introduction)
					.set("area",area)
					.set("area_code",area_code)
					.set("address",address)
					.set("lbs_lon",lbs_lon)
					.set("lbs_lat",lbs_lat)
					.set("salary",salary)
					.set("experience",experience)
					.set("experience_code",experience_code)
					.set("education",education)
					.set("education_code",education_code)
					.set("data_ent_key",data_ent_key)
					.set("data_src",data_src)
					.set("data_key",data_key)
					.set("update_date",update_date)
					.set("create_date",create_date).save();
		}
		dataMap.put("success", isSuccess);
		renderJson(dataMap);
	}
	/**
	 * 删除企业
	 */
	public void deletePost(){
		String id=getPara("id");
		boolean isSuccess=ViEntpost.dao.findById(id).delete();
		dataMap.put("success", isSuccess);
		renderJson(dataMap);
	}

}
