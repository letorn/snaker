package engine.module;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;

import engine.ModuleData;

/*
 * 流程模型 - 输出到数据库
 * 数据库存储过程
 */
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
						sql.append(") on duplicate key update ");
						for (int i = 0; i < num; i++) {
							if (i == 0) {
								sql.append(tableFields.get(i).get("name") + "=?");
							} else {
								sql.append(",");
								sql.append(tableFields.get(i).get("name") + "=?");
							}
						}
						PreparedStatement ps = conn.prepareStatement(sql.toString());
						for (int j = 0; j < finalInputs.getRows().size(); j++) {
							Map<String, Object> map = finalInputs.getRows().get(j);
							boolean notnull=false;
							for (int i = 0; i < num; i++) {
								Object col = map.get(tableFields.get(i).get("from"));
								if (col instanceof String && col.toString().equals("")) {
									col = null;
								}
								if(col == null && tableFields.get(i).get("notnull").toString().equals("true")){
									notnull=true;
									break;
								}
								ps.setObject(i + 1, col);
								ps.setObject(num + i + 1, col);
							}
							if(!notnull){
								ps.addBatch();
							}
						}
						ps.executeBatch();
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return null;
			}
		});
		return inputs;
	}
}