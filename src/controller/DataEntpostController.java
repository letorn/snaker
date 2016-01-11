package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.ViEntpost;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/*
 * 控制类 - 企业信息相关
 */
public class DataEntpostController extends Controller {

	/*
	 * 数据服务类
	 */
	// private DataService dataService = enhance(DataService.class);
	// private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	/*
	 * 返回到页面的json数据
	 */
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	// private List<Object> dataList = new ArrayList<Object>();
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
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


	
	public void getEntpost(){
		String id = getPara("id");
		Record record = Db.findFirst("SELECT id,name,category,category_code,nature,nature_code,headcount,age,gender,salary,salary_type,experience,experience_code,"
				+ "education,education_code,tag,introduction,area,area_code,address,lbs_lon,lbs_lat FROM   vi_entpost   WHERE id ="+id);
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
		Integer salary_type = getParaToInt("salary_type");
		String experience =getPara("experience");
		String experience_code =getPara("experience_code");
		String education =getPara("education");
		String education_code =getPara("education_code");
		String ent_name = getPara("ent_name");
		Date update_date = null;
		Date create_date = null;
		try {
			update_date = getPara("update_date")==null|| getPara("update_date").equals("")?new Date():sdf.parse(getPara("update_date"));
			create_date =getPara("create_date")==null|| getPara("create_date").equals("")?new Date():sdf.parse(getPara("create_date"));
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		boolean isSuccess=true;
		ViEntpost vep = null;
		if(id!=null && !id.equals("")){
			vep = ViEntpost.dao.findById(id);
			isSuccess=vep.set("name",name)
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
				.set("salary_type", salary_type)
				.set("experience",experience)
				.set("experience_code",experience_code)
				.set("education",education)
				.set("education_code",education_code)
				.set("syn_status", 2)
				.set("update_date",update_date)
				.set("create_date",create_date).update();
		}else{
			vep = new ViEntpost();
			isSuccess=vep.set("name",name)
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
					.set("salary_type", salary_type)
					.set("experience",experience)
					.set("experience_code",experience_code)
					.set("education",education)
					.set("education_code",education_code)
					.set("data_ent_key",ent_name)
					.set("data_src","snaker")
					.set("data_key",ent_name + "-" + name + "-" + area)
					.set("update_date",update_date)
					.set("create_date",create_date).save();
		}
		dataMap.put("id", vep.getLong("id"));
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
