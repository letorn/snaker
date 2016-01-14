package service;

import static util.Validator.empty;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.ViEnterprise;
import model.ViEntpost;
import model.ViJobhunter;
import model.ViTalk;

import org.apache.commons.lang.StringUtils;

import util.CreateHtml;
import util.VarKit;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;

/*
 * 服务类 - 数据相关
 */
/**
 * @author Administrator
 */
public class DataService {

	private static List<Thread> postEnterpriseThreads = new ArrayList<Thread>();
	private static List<Thread> postJobhunterThreads = new ArrayList<Thread>();
	private static List<Thread> postTalkThreads = new ArrayList<Thread>();

	/**
	 * 上传企业
	 * @param ids 企业id
	 * @param codes 行业code
	 * @return
	 */
	public boolean postEnterprise(Long[] ids, String[] codes) {
		// 单code上传上限
		String singleLimit = VarKit.get("enterprise_upload_single_limit");

		// 查询企业
		List<String> sqls = new ArrayList<String>();
		if (ids.length > 0)
			sqls.add("(select id,name,category_code,nature_code,scale_code,tag,establish,introduction,address,website,area_code,lbs_lon,lbs_lat,orgains,license,contacter,public_contact,phone,fax,mobile,email,qq,data_src,data_key,update_date,create_date,account,role,legalize from vi_enterprise where id in(" + StringUtils.join(ids, ",") + "))");
		
		for (String code : codes)
			sqls.add("(select id,name,category_code,nature_code,scale_code,tag,establish,introduction,address,website,area_code,lbs_lon,lbs_lat,orgains,license,contacter,public_contact,phone,fax,mobile,email,qq,data_src,data_key,update_date,create_date,account,role,legalize from vi_enterprise where syn_status in(-1,2) and category_code='" + code + "' limit " + singleLimit + ")");
	
		List<ViEnterprise> enterprises = sqls.size() > 0 ? ViEnterprise.dao.find(StringUtils.join(sqls, " union ")) : new ArrayList<ViEnterprise>();

		// 上传企业
		final long curr = System.currentTimeMillis();
		for (int i = 0; i < enterprises.size(); i += 500) {
			final List<ViEnterprise> list = enterprises.subList(i, i + 500 < enterprises.size() ? i + 500 : enterprises.size());
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Db.use("zcdh").execute(new ICallback() {
						public Object call(Connection conn) throws SQLException {
							Map<String, Set<String>> dataSrcMap = new HashMap<String, Set<String>>();
							CallableStatement proc = conn.prepareCall("call save_or_update_ent(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
								proc.setObject(24, row.get("update_date"));
								proc.setObject(25, row.get("create_date"));
								proc.setObject(26, row.get("account"));
								proc.setObject(27, row.get("role"));
								proc.setObject(28, row.get("legalize"));
								proc.registerOutParameter(29, Types.LONGNVARCHAR);
								proc.registerOutParameter(30, Types.BOOLEAN);
								proc.execute();

								int synStatus = proc.getInt(30);
								if (synStatus == 1) {// 上传成功
									// 统计需要上传岗位的企业的data_src,data_key
									Set<String> dataEntKeys = dataSrcMap.get(row.getStr("data_src"));
									if (empty(dataEntKeys)) {
										dataEntKeys = new HashSet<String>();
										dataSrcMap.put(row.getStr("data_src"), dataEntKeys);
									}
									dataEntKeys.add("'" + row.get("data_key") + "'");
								}
								Db.update("update vi_enterprise set syn_status=?, syn_date=?, syn_message=? where id=?", synStatus, new java.sql.Date(curr), proc.getString(29), row.getLong("id"));
							}

							// 查询岗位
							List<String> entpostSqls = new ArrayList<String>();
							for (String dataSrc : dataSrcMap.keySet())
								entpostSqls.add("(select id,name,category_code,nature_code,headcount,age,gender,salary,salary_type,experience_code,education_code,tag,introduction,area_code,address,lbs_lon,lbs_lat,data_src,data_key,data_ent_key,update_date,create_date from vi_entpost where syn_status in(-1,2) and data_src='" + dataSrc + "' and data_ent_key in(" + StringUtils.join(dataSrcMap.get(dataSrc), ",") + "))");
							List<ViEntpost> entposts = entpostSqls.size() > 0 ? ViEntpost.dao.find(StringUtils.join(entpostSqls, " union ")) : new ArrayList<ViEntpost>();

							// 上传岗位
							for (int i = 0; i < entposts.size(); i += 500) {
								final List<ViEntpost> list = entposts.subList(i, i + 500 < entposts.size() ? i + 500 : entposts.size());
								Thread thread = new Thread(new Runnable() {
									public void run() {
										Db.use("zcdh").execute(new ICallback() {
											public Object call(Connection conn) throws SQLException {
												long curr = System.currentTimeMillis();
												CallableStatement proc = conn.prepareCall("call save_or_update_post(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
												for (ViEntpost row : list) {
													proc.setObject(1, row.get("name"));
													proc.setObject(2, row.get("category_code"));
													proc.setObject(3, row.get("nature_code"));
													proc.setObject(4, row.get("headcount"));
													proc.setObject(5, row.get("age"));
													proc.setObject(6, row.get("gender"));
													proc.setObject(7, row.get("salary"));
													proc.setObject(8, row.get("tag"));
													proc.setObject(9, row.get("introduction"));
													proc.setObject(10, row.get("area_code"));
													proc.setObject(11, row.get("address"));
													proc.setObject(12, row.get("lbs_lon"));
													proc.setObject(13, row.get("lbs_lat"));
													proc.setObject(14, row.get("data_src"));
													proc.setObject(15, row.get("data_key"));
													proc.setObject(16, row.get("data_ent_key"));
													proc.setObject(17, row.get("update_date"));
													proc.setObject(18, row.get("create_date"));
													proc.setObject(19, row.get("experience_code"));
													proc.setObject(20, row.get("education_code"));
													proc.setObject(21, row.get("salary_type"));
													proc.registerOutParameter(22, Types.LONGNVARCHAR);
													proc.registerOutParameter(23, Types.BOOLEAN);
													proc.execute();

													Db.update("update vi_entpost set syn_status=?, syn_date=?, syn_message=? where id=?", proc.getInt(23), new java.sql.Date(curr), proc.getString(22), row.getLong("id"));
												}
												return null;
											}
										});
									}
								});
								thread.start();
								postEnterpriseThreads.add(thread);
							}
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

	/**
	 * 上传求职者
	 * @param ids 求职者id
	 * @param codes 当前岗位类别code
	 * @return
	 */
	public boolean postJobhunter(Long[] ids, String[] codes) {
		// 单code上传上限
		String singleLimit = VarKit.get("jobhunter_upload_single_limit");

		// 查询求职者
		List<String> sqls = new ArrayList<String>();
		if (ids.length > 0)
			sqls.add("(select id,name,gender,nation,mobile,email,experience_code,education_code,household,category_code,hunter_status_code,marriage,cert_type,cert_id,birth,self_comment,location_code,data_src,data_key,update_date,create_date,account,curr_ent,curr_ent_phone,curr_post_code,curr_post,lbs_lon,lbs_lat,account_status from vi_jobhunter where id in(" + StringUtils.join(ids, ",") + "))");
		for (String code : codes)
			sqls.add("(select id,name,gender,nation,mobile,email,experience_code,education_code,household,category_code,hunter_status_code,marriage,cert_type,cert_id,birth,self_comment,location_code,data_src,data_key,update_date,create_date,account,curr_ent,curr_ent_phone,curr_post_code,curr_post,lbs_lon,lbs_lat,account_status from vi_jobhunter where syn_status in(-1,2) and curr_post_code='" + code + "' limit " + singleLimit + ")");
		List<ViJobhunter> jobhunters = sqls.size() > 0 ? ViJobhunter.dao.find(StringUtils.join(sqls, " union ")) : new ArrayList<ViJobhunter>();

		// 上传求职者
		final long curr = System.currentTimeMillis();
		for (int i = 0; i < jobhunters.size(); i += 500) {
			final List<ViJobhunter> list = jobhunters.subList(i, i + 500 < jobhunters.size() ? i + 500 : jobhunters.size());
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Db.use("zcdh").execute(new ICallback() {
						public Object call(Connection conn) throws SQLException {
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

	/**
	 * 上传宣讲会
	 * @param ids 宣讲会id
	 * @param codes 宣讲会来源
	 * @return
	 */
	public boolean postTalk(Long[] ids, String[] codes) {
		// 单code上传上限
		String singleLimit = VarKit.get("talk_upload_single_limit");

		// 查询宣讲会
		List<String> sqls = new ArrayList<String>();
		if (ids.length > 0)
			sqls.add("(select id,title,content,source,data_src,data_key,update_date,create_date from vi_talk where id in(" + StringUtils.join(ids, ",") + "))");
		for (String code : codes)
			sqls.add("(select id,title,content,source,data_src,data_key,update_date,create_date from vi_talk where syn_status in(-1,2) and source='" + code + "' limit " + singleLimit + ")");
		List<ViTalk> talks = sqls.size() > 0 ? ViTalk.dao.find(StringUtils.join(sqls, " union ")) : new ArrayList<ViTalk>();

		// 上传宣传会
		final long curr = System.currentTimeMillis();
		for (int i = 0; i < talks.size(); i += 500) {
			final List<ViTalk> list = talks.subList(i, i + 500 < talks.size() ? i + 500 : talks.size());
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Db.use("zcdh").execute(new ICallback() {
						public Object call(Connection conn) throws SQLException {
							CallableStatement proc = conn.prepareCall("call save_or_update_information(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
							for (ViTalk row : list) {
								Map<String, Object> paramMap = new HashMap<String, Object>();
								paramMap.put("title", row.get("title"));
								paramMap.put("date", row.get("create_date"));
								paramMap.put("source", row.get("source"));
								paramMap.put("content", row.get("content"));

								proc.setObject(1, "005");
								proc.setObject(2, row.get("title"));
								proc.setObject(3, row.get("source"));
								proc.setObject(4, "html");
								proc.setObject(5, row.get("content"));
								proc.setObject(6, CreateHtml.create(null, paramMap, PropKit.get("fileserver.path")));
								proc.setObject(7, 1);
								proc.setObject(8, row.get("create_date"));
								proc.setObject(9, 0);
								proc.setObject(10, "");
								proc.setObject(11, "");
								proc.setObject(12, 0);
								proc.setObject(13, "");
								proc.setObject(14, "");
								proc.setObject(15, new Date(curr));
								proc.setObject(16, 0);
								proc.setObject(17, 0);
								proc.setObject(18, "");
								proc.setObject(19, row.get("data_src"));
								proc.setObject(20, row.get("data_key"));
								proc.registerOutParameter(21, Types.LONGNVARCHAR);
								proc.registerOutParameter(22, Types.BOOLEAN);
								proc.execute();

								Db.update("update vi_talk set syn_status=?, syn_date=?, syn_message=? where id=?", proc.getInt(22), new java.sql.Date(curr), proc.getString(21), row.getLong("id"));
							}
							return null;
						}
					});
				}
			});
			thread.start();
			postTalkThreads.add(thread);
		}
		return true;
	}

	public boolean isPostTalkFinished() {
		boolean finished = true;
		Iterator<Thread> iterator = postTalkThreads.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().isAlive())
				finished = false;
			else
				iterator.remove();
		}
		return finished;
	}

}
