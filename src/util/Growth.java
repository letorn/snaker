package util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;

/**
 * @author Administrator
 * 自增长工具类
 *
 */
public class Growth {
	static String result="";
	public static String get(final String prifix) {
		Db.execute(new ICallback() {
			public Object call(Connection conn) throws SQLException {
				try {
					StringBuffer sql = new StringBuffer("call growth_proc(?,?)");
					CallableStatement proc = conn.prepareCall(sql.toString());
					proc.setObject(1, prifix);
					proc.registerOutParameter(2, Types.VARCHAR);// 设置返回值类型
					proc.execute();
					result = proc.getString(2);// 得到返回值
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		});

		return result;
	}
}
