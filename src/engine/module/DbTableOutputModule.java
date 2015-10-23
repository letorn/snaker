package engine.module;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;
import com.jfinal.plugin.druid.DruidPlugin;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.impl.NewProxyCallableStatement;

import engine.ModuleData;
import engine.model.DbEnterprise;

/*
 * 流程模型 - 输出到数据库
 * 数据库存储过程
 */
public class DbTableOutputModule extends Module implements ICallback {

	private String jdbcUrl;
	private String jdbcUser;
	private String jdbcPwd;
	private String tableName;
	
	private List<Map<String, Object>> tableFields;
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */

	public ModuleData execute(ModuleData inputs) {
		
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setJdbcUrl(jdbcUrl);
		dataSource.setUser(jdbcUser);
		dataSource.setPassword(jdbcPwd);
		int num = tableFields.size();
		StringBuffer sql=new StringBuffer("insert into "+tableName+"(");
		if(num>0 ){
			for(int i=0;i<num;i++){
				if(i==0){
					sql.append(tableFields.get(i).get("from"));
				}else{
					sql.append(",");
					sql.append(tableFields.get(i).get("from"));
				}
			}
			sql.append(") values (");
			for(int j=0;j<inputs.getRows().size();j++){
				Map<String, Object> map= inputs.getRows().get(j);
				if(j!=0){
					sql.append("),(");
				}
				for(int k=0;k<num;k++){
					
					if(k==0){
						sql.append("'"+map.get(tableFields.get(k).get("from"))+"'");
					}else{
						sql.append(",");
						sql.append("'"+map.get(tableFields.get(k).get("from"))+"'");
					}
				}
			}
			sql.append(")");
			System.out.println("sql====="+sql);
		try (Connection conn = dataSource.getConnection()) {
			
			NewProxyCallableStatement proc = null;
			proc=(NewProxyCallableStatement) conn.prepareCall(sql.toString());
				
			proc.execute();
	        dataSource.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return inputs;
	}
	

	@Override
	public Object call(java.sql.Connection conn) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}