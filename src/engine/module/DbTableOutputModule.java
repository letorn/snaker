package engine.module;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import engine.ModuleData;

/*
 * 流程模型 - 输出到数据库
 * 数据库存储过程
 */
@SuppressWarnings("serial")
public class DbTableOutputModule extends Module {

	private String tableName;

	private List<Map<String, Object>> tableFields;

	/**
	 * 模型执行方法体
	 * 
	 * @param inputs
	 *            输入的数据
	 * @return 输出的数据
	 */

	public ModuleData execute(ModuleData inputs) {
		final ModuleData finalInputs = inputs;
		Db.execute(new ICallback() {
			public Object call(Connection conn) throws SQLException {
				try {
					int num = tableFields.size();
					if (num > 0) {
						StringBuffer sql = new StringBuffer("insert into " + tableName + "(");
						for (int i = 0; i < num; i++) {
							if (i == 0) {
								sql.append(tableFields.get(i).get("name"));
							} else {
								sql.append(",");
								sql.append(tableFields.get(i).get("name"));
							}
						}
						sql.append(") values (");
						for (int k = 0; k < num; k++) {
							if (k == 0) {
								sql.append("?");
							} else {
								sql.append(",");
								sql.append("?");
							}
						}
						sql.append(")");
						PreparedStatement ps = conn.prepareStatement(sql.toString());
						for (int j = 0; j < finalInputs.getRows().size(); j++) {
							Map<String, Object> map = finalInputs.getRows().get(j);
							for (int i = 0; i < num; i++) {
								Object col = map.get(tableFields.get(i).get("from"));
								if (col instanceof String && col.toString().equals("")) {
									col = null;
								}
								ps.setObject(i + 1, col);
							}
							ps.addBatch();
						}
						ps.executeBatch();
					}
				} catch (BatchUpdateException e) {
					if (!e.getMessage().startsWith("Duplicate entry")) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				conn.close();
				return null;
			}
		});
		return inputs;
	}
}