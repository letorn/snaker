package service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.ViEnterprise;
import model.ViJobhunter;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;


/*
 * 服务类 - 数据相关
 */
public class DataService {
	
	private static List<Thread> postEnterpriseThreads = new ArrayList<Thread>();
	private static List<Thread> postJobhunterThreads = new ArrayList<Thread>();
	
	public boolean postEnterprise(Long[] ids, String[] codes) {
		List<String> sqls = new ArrayList<String>();
		if (ids.length > 0)
			sqls.add("(select id,name,category_code,nature_code,scale_code,tag,establish,introduction,address,website,area_code,lbs_lon,lbs_lat,orgains,license,contacter,public_contact,phone,fax,mobile,email,qq,data_src,data_key,data_url,update_date,create_date,account from vi_enterprise where syn_status=-1 and id in(" + StringUtils.join(ids, ",") + "))");
		for (String code : codes)
			sqls.add("(select id,name,category_code,nature_code,scale_code,tag,establish,introduction,address,website,area_code,lbs_lon,lbs_lat,orgains,license,contacter,public_contact,phone,fax,mobile,email,qq,data_src,data_key,data_url,update_date,create_date,account from vi_enterprise where syn_status=-1 and category_code=? limit 10)");
		
		List<ViEnterprise> enterprises = ViEnterprise.dao.find(StringUtils.join(sqls, " union "), codes);
		for (int i = 0; i < enterprises.size(); i += 500) {
			final List<ViEnterprise> list = enterprises.subList(i, i + 500 < enterprises.size() ? i + 500 : enterprises.size());
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Db.use("zcdh").execute(new ICallback() {
						public Object call(Connection conn) throws SQLException {
							long curr = System.currentTimeMillis();
							CallableStatement proc = conn.prepareCall("call save_or_update_ent(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
							for (ViEnterprise row : list) {
								proc.setObject(1, row.get("name"));
								proc.setObject(2, row.get("category_code"));
								proc.setObject(3, row.get("nature_code"));
								proc.setObject(4, row.get("scale_code"));
								proc.setObject(5, row.get("tag"));
								proc.setObject(6, row.get("establish"));
								proc.setObject(7, row.get("introduction"));
								proc.setObject(8, row.get("address"));
								proc.setObject(9, row.get("website"));
								proc.setObject(10, row.get("area_code"));
								proc.setObject(11, row.get("lbs_lon"));
								proc.setObject(12, row.get("lbs_lat"));
								proc.setObject(13, row.get("orgains"));
								proc.setObject(14, row.get("license"));
								proc.setObject(15, row.get("contacter"));
								proc.setObject(16, row.get("public_contact"));
								proc.setObject(17, row.get("phone"));
								proc.setObject(18, row.get("fax"));
								proc.setObject(19, row.get("mobile"));
								proc.setObject(20, row.get("email"));
								proc.setObject(21, row.get("qq"));
								proc.setObject(22, row.get("data_src"));
								proc.setObject(23, row.get("data_key"));
								proc.setObject(24, row.get("data_url"));
								proc.setObject(25, row.get("update_date"));
								proc.setObject(26, row.get("create_date"));
								proc.setObject(27, row.get("account"));
								proc.registerOutParameter(28, Types.LONGNVARCHAR);
								proc.registerOutParameter(29, Types.BOOLEAN);
								proc.execute();
								
								Db.update("update vi_enterprise set syn_status=?, syn_date=?, syn_message=? where id=?", proc.getInt(29), new java.sql.Date(curr), proc.getString(28), row.getLong("id"));
							}
							conn.close();
							return null;
						}
						
					});
				}
			});
			thread.start();
			postEnterpriseThreads.add(thread);
		}
		return true;
	}
	
	public boolean isPostEnterpriseFinished() {
		boolean finished = true;
		Iterator<Thread> iterator = postEnterpriseThreads.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().isAlive()) 
				finished = false;
			else
				iterator.remove();
		}
		return finished;
	}
	
	public boolean postJobhunter(Long[] ids, String[] codes) {
		List<String> sqls = new ArrayList<String>();
		if (ids.length > 0)
			sqls.add("(select id,name,gender,nation,mobile,email,experience_code,education_code,household,category_code,hunter_status_code,marriage,cert_type,cert_id,birth,self_comment,location_code,data_src,data_key,update_date,create_date,account,curr_ent,curr_ent_phone,curr_post_code,curr_post,lbs_lon,lbs_lat,account_status from vi_jobhunter where syn_status=-1 and id in(" + StringUtils.join(ids, ",") + "))");
		for (String code : codes)
			sqls.add("(select id,name,gender,nation,mobile,email,experience_code,education_code,household,category_code,hunter_status_code,marriage,cert_type,cert_id,birth,self_comment,location_code,data_src,data_key,update_date,create_date,account,curr_ent,curr_ent_phone,curr_post_code,curr_post,lbs_lon,lbs_lat,account_status from vi_jobhunter where syn_status=-1 and curr_post_code=? limit 10)");
		
		List<ViJobhunter> jobhunters = ViJobhunter.dao.find(StringUtils.join(sqls, " union "), codes);
		for (int i = 0; i < jobhunters.size(); i += 500) {
			final List<ViJobhunter> list = jobhunters.subList(i, i + 500 < jobhunters.size() ? i + 500 : jobhunters.size());
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Db.use("zcdh").execute(new ICallback() {
						public Object call(Connection conn) throws SQLException {
							long curr = System.currentTimeMillis();
							CallableStatement proc = conn.prepareCall("call save_or_update_user(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
							for (ViJobhunter row : list) {
								proc.setObject(1, row.get("name"));
								proc.setObject(2, row.get("gender"));
								proc.setObject(3, row.get("nation"));
								proc.setObject(4, row.get("mobile"));
								proc.setObject(5, row.get("email"));
								proc.setObject(6, row.get("experience_code"));
								proc.setObject(7, row.get("education_code"));
								proc.setObject(8, row.get("household"));
								proc.setObject(9, row.get("category_code"));
								proc.setObject(10, row.get("hunter_status_code"));
								proc.setObject(11, row.get("marriage"));
								proc.setObject(12, row.get("cert_type"));
								proc.setObject(13, row.get("cert_id"));
								proc.setObject(14, row.get("birth"));
								proc.setObject(15, row.get("self_comment"));
								proc.setObject(16, row.get("location_code"));
								proc.setObject(17, row.get("data_src"));
								proc.setObject(18, row.get("data_key"));
								proc.setObject(19, row.get("update_date"));
								proc.setObject(20, row.get("create_date"));
								proc.setObject(21, row.get("account"));
								proc.setObject(22, row.get("curr_ent"));
								proc.setObject(23, row.get("curr_ent_phone"));
								proc.setObject(24, row.get("curr_post_code"));
								proc.setObject(25, row.get("curr_post"));
								proc.setObject(26, row.get("lbs_lon"));
								proc.setObject(27, row.get("lbs_lat"));
								proc.setObject(28, row.get("account_status"));
								proc.registerOutParameter(29, Types.LONGNVARCHAR);
								proc.registerOutParameter(30, Types.BOOLEAN);
								proc.execute();
								
								Db.update("update vi_jobhunter set syn_status=?, syn_date=?, syn_message=? where id=?", proc.getInt(30), new java.sql.Date(curr), proc.getString(29), row.getLong("id"));
							}
							conn.close();
							return null;
						}
						
					});
				}
			});
			thread.start();
			postJobhunterThreads.add(thread);
		}
		return true;
	}
	
	public boolean isPostJobhunterFinished() {
		boolean finished = true;
		Iterator<Thread> iterator = postJobhunterThreads.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().isAlive()) 
				finished = false;
			else
				iterator.remove();
		}
		return finished;
	}
	
}
