package controller;

import static util.Validator.blank;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.UtArea;
import model.UtIndustry;
import model.ViEnterprise;
import service.DataService;
import test.Area;
import test.Major;
import util.DateKit;
import util.Growth;

import com.alibaba.fastjson.JSON;
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
		String source = getPara("source");
		Integer status = getParaToInt("status");
		String name = getPara("name", "");
		if (page < 1) page = 1;
		if (rows < 1) rows = 1;

		Page<ViEnterprise> pager = blank(source) ?
									(blank(status) ?
										ViEnterprise.dao.paginate(page, rows, "select id,name,data_src,data_key,syn_status,syn_message", "from vi_enterprise where name like ?", "%" + name + "%") :
										ViEnterprise.dao.paginate(page, rows, "select id,name,data_src,data_key,syn_status,syn_message", "from vi_enterprise where syn_status=? and name like ?", status, "%" + name + "%")
									) :
									(blank(status) ?
										ViEnterprise.dao.paginate(page, rows, "select id,name,data_src,data_key,syn_status,syn_message", "from vi_enterprise where data_src=? and name like ?", source, "%" + name + "%") :
										ViEnterprise.dao.paginate(page, rows, "select id,name,data_src,data_key,syn_status,syn_message", "from vi_enterprise where data_src=? and syn_status=? and name like ?", source, status, "%" + name + "%")
									);
		dataMap.put("total", pager.getTotalRow());
		dataMap.put("rows", pager.getList());
		renderJson(dataMap);
	}
	
	/**
	 * 展示指定的企业列表
	 * categoryName 页面传入的行业名称
	 * field 当日新增、已上传、当日上传、所有
	 * beginTime 选定的开始时间
	 * endTime 选定的结束时间
	 */
	public void display() {
		String date = DateKit.toString(new Date());
		String categoryName = getPara("category");
		String field = getPara("field");
		Integer page = getParaToInt("page", 1);
		Integer rows = getParaToInt("rows", 20);
		String beginTime = getPara("beginTime", date + " 00:00:00");
		String endTime = getPara("endTime", date + " 23:59:59");

		Page<ViEnterprise> ents = null;
		if ("day_new".equals(field)) {
			if ("全部".equals(categoryName)) {
				ents = ViEnterprise.dao.paginate(page, rows, "select id, name, account, data_src, data_key", "from vi_enterprise where category_code in (select code from ut_industry) and create_date between ? and ?", beginTime, endTime);
			} else {
				ents = ViEnterprise.dao.paginate(page, rows, "select id, name, account, data_src, data_key", "from vi_enterprise where category_code=? and create_date between ? and ?", getCategoryCode(categoryName), beginTime, endTime);
			}
		} else if("syn".equals(field)) {
			if ("全部".equals(categoryName)) {
				ents = ViEnterprise.dao.paginate(page, rows, "select id, name, account, data_src, data_key", "from vi_enterprise where category_code in (select code from ut_industry) and create_date<? and syn_status=1", endTime);
			} else {
				ents = ViEnterprise.dao.paginate(page, rows, "select id, name, account, data_src, data_key", "from vi_enterprise where category_code=? and create_date<? and syn_status=1", getCategoryCode(categoryName), endTime);
			}
		} else if("day_syn".equals(field)) {
			if ("全部".equals(categoryName)) {
				ents = ViEnterprise.dao.paginate(page, rows, "select id, name, account, data_src, data_key", "from vi_enterprise where category_code in (select code from ut_industry) and create_date between ? and ? and syn_status=1", beginTime, endTime);
			} else {
				ents = ViEnterprise.dao.paginate(page, rows, "select id, name, account, data_src, data_key", "from vi_enterprise where category_code=? and create_date between ? and ? and syn_status=1", getCategoryCode(categoryName), beginTime, endTime);
			}
		} else {
			if ("全部".equals(categoryName)) {
				ents = ViEnterprise.dao.paginate(page, rows, "select id, name, account, data_src, data_key", "from vi_enterprise where category_code in (select code from ut_industry) and create_date<?", endTime);
			} else {
				ents = ViEnterprise.dao.paginate(page, rows, "select id, name, account, data_src, data_key", "from vi_enterprise where category_code=? and create_date<?", getCategoryCode(categoryName), endTime);
			}
		}
		dataMap.put("total", ents.getTotalRow());
		dataMap.put("rows", ents.getList());
		renderJson(dataMap);
	}
	
	/**
	 * 根据企业行业获取行业编码
	 * @param categoryName
	 * @return code
	 */
	private String getCategoryCode(String categoryName) {
		return UtIndustry.dao.findFirst("select code from ut_industry where name=?", categoryName).getStr("code");
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
		String date = DateKit.toString(new Date());
		String beginTime = getPara("beginTime", date + " 00:00:00");
		String endTime = getPara("endTime", date + " 23:59:59");
		List<Record> records = Db.find("select i.name, "
				+ "(select count(*) from vi_enterprise where category_code=i.code and create_date<?) total, "
				+ "(select count(*) from vi_enterprise where category_code=i.code and create_date between ? and ?) day_new, "
				+ "(select count(*) from vi_enterprise where category_code=i.code and create_date<? and syn_status=1) syn, "
				+ "(select count(*) from vi_enterprise where category_code=i.code and create_date between ? and ? and syn_status=1) day_syn "
				+ "from ut_industry i", endTime, beginTime, endTime, endTime, beginTime, endTime);
		renderJson(records);
	}
	public void getEnt(){
		String id = getPara("id");
		Record record = Db.findFirst("SELECT id,name,account,category,category_code,nature,nature_code,scale,scale_code,tag,establish"
				+ ",introduction,area,area_code,address,lbs_lon,lbs_lat,website ,orgains,license,contacter,public_contact,phone,"
				+ "fax,mobile,email,qq,legalize,syn_status FROM vi_enterprise  WHERE id ="+id);
		renderJson(record);
	}
	public void saveEnt(){
		String id =getPara("id");
		String name =getPara("name");
		String account =getPara("account");
		String category =getPara("category");
		String category_code =getPara("category_code");
		String nature =getPara("nature");
		String nature_code =getPara("nature_code");
		String scale =getPara("scale");
		String scale_code =getPara("scale_code");
		String tag =getPara("tag");
		Date establish =getParaToDate("establish");
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
		String website =getPara("website");
		String orgains =getPara("orgains");
		String license =getPara("license");
		String contacter =getPara("contacter");
		int public_contact =getParaToInt("public_contact") ==null?0:getParaToInt("public_contact");
		String phone =getPara("phone");
		String fax =getPara("fax");
		String mobile =getPara("mobile");
		String email =getPara("email");
		String qq =getPara("qq");
		
		
		Date update_date = getPara("update_date")==null|| getPara("update_date").equals("")?new Date():DateKit.toDate(getPara("update_date"), DateKit.YMDHM);
		Date create_date =getPara("create_date")==null|| getPara("create_date").equals("")?new Date():DateKit.toDate(getPara("create_date"), DateKit.YMDHM);
		
		String legalize =getPara("legalize").equals("")?"1":getPara("legalize"); 
		boolean isSuccess=true;
		ViEnterprise ve = null;
		if(id!=null && !id.equals("")){
			ve = ViEnterprise.dao.findById(id);
			if (ViEnterprise.dao.findFirst("select name from vi_enterprise where account=?", account).get("name").equals(name)) {
				isSuccess=ve.set("name",name)
					.set("account",account)
					.set("category",category)
					.set("category_code",category_code)
					.set("nature",nature)
					.set("nature_code",nature_code)
					.set("scale",scale)
					.set("scale_code",scale_code)
					.set("tag",tag)
					.set("establish",establish)
					.set("introduction",introduction)
					.set("area",area)
					.set("area_code",area_code)
					.set("address",address)
					.set("lbs_lon",lbs_lon)
					.set("lbs_lat",lbs_lat)
					.set("website",website)
					.set("orgains",orgains)
					.set("license",license)
					.set("contacter",contacter)
					.set("public_contact",public_contact)
					.set("phone",phone)
					.set("fax",fax)
					.set("mobile",mobile)
					.set("email",email)
					.set("qq",qq)
					.set("update_date",update_date)
					.set("create_date",create_date)
					.set("syn_status", 2)
					.set("legalize",legalize).update();
			} else {
				isSuccess = false;
				dataMap.put("msg", "保存失败！账号已存在！");
			}
		}else{
			ve = new ViEnterprise();
			if(ViEnterprise.dao.findFirst("select name, account from vi_enterprise where name=? or account=?", name, account) == null) {
				isSuccess=ve.set("name",name)
					.set("account",account)
					.set("category",category)
					.set("category_code",category_code)
					.set("nature",nature)
					.set("nature_code",nature_code)
					.set("scale",scale)
					.set("scale_code",scale_code)
					.set("tag",tag)
					.set("establish",establish)
					.set("introduction",introduction)
					.set("area",area)
					.set("area_code",area_code)
					.set("address",address)
					.set("lbs_lon",lbs_lon)
					.set("lbs_lat",lbs_lat)
					.set("website",website)
					.set("orgains",orgains)
					.set("license",license)
					.set("contacter",contacter)
					.set("public_contact",public_contact)
					.set("phone",phone)
					.set("fax",fax)
					.set("mobile",mobile)
					.set("email",email)
					.set("qq",qq)
					.set("data_src", "snaker")
					.set("data_key",name)
					.set("update_date",update_date)
					.set("create_date",create_date)
					.set("role","1")
					.set("legalize",legalize).save();
			} else {
				isSuccess = false;
				dataMap.put("msg", "保存失败！企业或账号已存在！");
			}
		}
		dataMap.put("id", ve.getLong("id"));
		dataMap.put("success", isSuccess);
		renderJson(dataMap);
	}
	/**
	 * 删除企业
	 */
	public void deleteEnt(){
		String id=getPara("id");
		boolean isSuccess=ViEnterprise.dao.findById(id).delete();
		dataMap.put("success", isSuccess);
		renderJson(dataMap);
	}
	
	/**
	 * 调用存储过程获取自动生成的企业账号
	 * prefix 账号前缀
	 */
	public void getAccount() {
		String prefix = getPara("prefix", "job");
		setAttr("account", Growth.get(prefix));
		renderJson();
	}
	
	public List<Area> area(){
		
		List<UtArea> list =UtArea.dao.find("SELECT * FROM ut_area ");
		List<Area> areas=new ArrayList<Area>();
		
		List<Area> out= new ArrayList<Area>();
		for(int i=0;i<list.size();i++){
			Area area=new Area();
			area.setId(list.get(i).getStr("code"));
			area.setText(list.get(i).getStr("name"));
			areas.add(area);
		}
		for(int i=0;i<areas.size();i++){
			List<Area> children =new ArrayList<Area>();
			for (int j = 0; j < list.size(); j++) {
				if(areas.get(i).getId().equals(list.get(j).get("parent"))){
					children.add(areas.get(j));
				}
			}
			areas.get(i).setChildren(children);
			
		}
		for(int i=0;i<areas.size();i++){
			if(areas.get(i).getId().length()==3){
				out.add(areas.get(i));
			}
		}
		System.out.println(out);
		//renderJson(out);
		dataMap.put("success", out);
		renderJson(JSON.toJSON(dataMap));
		return areas;
	}
	
	public void major(){
		List<Record> recordlist=Db.use("zcdh").find("SELECT CODE,major_name,parent_code FROM  zcdh_jobhunte_major");
		List<Major> majors=new ArrayList<Major>();
		for(int i=0;i<recordlist.size();i++){
			Major major=new Major();
			major.setCode(recordlist.get(i).get("CODE").toString());
			major.setName(recordlist.get(i).get("major_name").toString());
			major.setParent(recordlist.get(i).get("parent_code").toString());
			majors.add(major);
		}
		
		for(int i=0;i<majors.size();i++){
			List<Major> children =new ArrayList<Major>();
			for (int j = 0; j < recordlist.size(); j++) {
				if(majors.get(i).getCode().equals(recordlist.get(j).get("parent_code"))){
					children.add(majors.get(j));
				}
			}
			majors.get(i).setChildren(children);
		}
		List<Major> out= new ArrayList<Major>();
		for(int i=0;i<majors.size();i++){
			if(majors.get(i).getCode().length()==3){
				out.add(majors.get(i));
			}
		}
		System.out.println(out);
		//renderJson(out);
		dataMap.put("success", out);
		renderJson(JSON.toJSON(dataMap));
		//return areas;
	}
}
