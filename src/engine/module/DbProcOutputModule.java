package engine.module;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;

import engine.ModuleData;

/*
 * 流程模型 - 输出到数据库
 * 数据库存储过程
 */
public class DbProcOutputModule extends Module {

	private String procName;
	private List<Map<String, Object>> procFields;

	/**
	 * 模型执行方法体
	 * 
	 * @param inputs
	 *            输入的数据
	 * @return 输出的数据
	 */

	public ModuleData execute(ModuleData inputs) {
		final ModuleData finalInputs = inputs;
		
			Db.use("zcdh").execute(new ICallback() {

				public Object call(Connection conn) throws SQLException {
					try {
					StringBuffer sql = new StringBuffer("call " + procName + "(");
					int num = procFields.size();
					if (num > 0) {
						for (int i = 0; i < num; i++) {
							if (i != 0) {
								sql.append(",?");
							} else {
								sql.append("?");
							}
						}
						sql.append(",");
					}
					sql.append("?,?)");
					for (int j = 0; j < finalInputs.getRows().size(); j++) {
						Map<String, Object> map = finalInputs.getRows().get(j);
						CallableStatement proc = conn.prepareCall(sql.toString());
						for (int k = 0; k < num; k++) {
							Object o=procFields.get(k).get("from");
							Object param = (Object) map.get(o);
							if(param==null||param.toString().equals("")){
								param=procFields.get(k).get("default");
							}
							proc.setObject((k + 1), param);
						}
						proc.registerOutParameter(num + 1, Types.LONGNVARCHAR);// 设置返回值类型
						proc.registerOutParameter(num + 2, Types.INTEGER);
						proc.execute();
						String result = proc.getString(num + 1);// 得到返回值
						Integer isSuccess = proc.getInt(num + 2);
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