package util;

import java.sql.Connection;
import java.sql.SQLException;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;
import com.mchange.v2.c3p0.impl.NewProxyCallableStatement;

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
					NewProxyCallableStatement proc = null;
					proc = (NewProxyCallableStatement) conn.prepareCall(sql.toString());
					proc.setObject(1, prifix);
					proc.registerOutParameter(2, java.sql.Types.VARCHAR);// 设置返回值类型
					proc.execute();
					result = proc.getString(2);// 得到返回值
				} catch (Exception e) {
					e.printStackTrace();
				}
				conn.close();
				return null;
			}

		});

		return result;
	}
}
