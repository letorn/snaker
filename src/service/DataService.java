package service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;
import com.jfinal.plugin.activerecord.Record;


/*
 * 服务类 - 数据相关
 */
public class DataService {
	
	private static List<Thread> postEnterpriseThreads = new ArrayList<Thread>();
	
	public boolean postEnterprise(Long[] ids, String[] codes) {
		List<String> sqls = new ArrayList<String>();
		if (ids.length > 0)
			sqls.add("(select * from ut_industry where id in(" + StringUtils.join(ids, ",") + "))");
		for (String code : codes)
			sqls.add("(select * from ut_industry where code=? limit 10)");
		
		List<Record> enterprises = Db.find(StringUtils.join(sqls, " union "), codes);
		for (int i = 0; i < enterprises.size(); i += 500) {
			final List<Record> list = enterprises.subList(i, i + 500 < enterprises.size() ? i + 500 : enterprises.size());
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Db.use("zcdh").execute(new ICallback() {
						public Object call(Connection conn) throws SQLException {
							Set<Long> succeedSet = new HashSet<Long>();
							Set<Long> failedSet = new HashSet<Long>();
							CallableStatement proc = conn.prepareCall("");
							for (Record row : list) {
								proc.setObject(1, "1");
								proc.setObject(1, "1");
								proc.registerOutParameter(1, Types.LONGNVARCHAR);// 设置返回值类型
								proc.registerOutParameter(2, Types.BOOLEAN);
								proc.execute();
								boolean success = proc.getBoolean(1);
								String message = proc.getString(2);
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
	
}
