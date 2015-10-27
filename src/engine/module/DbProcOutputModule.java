package engine.module;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.impl.NewProxyCallableStatement;

import engine.ModuleData;

/*
 * 流程模型 - 输出到数据库
 * 数据库存储过程
 */
public class DbProcOutputModule extends Module implements ICallback {

	private String jdbcUrl;
	private String jdbcUser;
	private String jdbcPwd;
	private String procName;
	private List<Map<String, Object>> procFields;
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
		StringBuffer sql=new StringBuffer("call "+procName+"(");
		int num = procFields.size();
		if(num>0 ){
			for(int i=0;i<num;i++){
				if(i!=0){
					sql.append(",?");
				}else{
					sql.append("?");
				}
			}
			sql.append(",");
		}
		sql.append("?,?)");
		System.out.println(sql);
		
		
		/*
		 * inputs.rows
		 * aaa: let
		 * bbb: 12
		 */
		
		/*
		 * test
		 * name:
		 * age:
		 * 
		 */
		
		/*
		 * procFields
		 * {
		 * 	a2: {
		 * 	from: '',
		 * 	
		 * },
		 * a1: {
		 *  from : ''
		 * }
		 * }
		 * [{
		 * 		name: 'a2',
		 * 		from: 'aaa',
		 * 		type: '',
		 * 		format: ''
		 * },
		 * {
		 * 		name: 'a1',
		 * 		from: 'bbb',
		 * },
		 * {
		 * 		name: 'address',
		 * 		from: 'a1',
		 * }]
		 */
		
		try (Connection conn = dataSource.getConnection()) {
			for(int j=0;j<inputs.getRows().size();j++){
				Map<String, Object> map= inputs.getRows().get(j);
				NewProxyCallableStatement proc = null;
				proc=(NewProxyCallableStatement) conn.prepareCall(sql.toString());
				for(int k=0;k<num;k++){
					String param=(String)map.get(procFields.get(k).get("from"));;
					proc.setObject((k+1), param);
				}
				//proc.setString(1, "title");//设置参数值
	            // proc.setString(2, "content");
				proc.registerOutParameter(num+1, java.sql.Types.LONGNVARCHAR);//设置返回值类型
	            proc.registerOutParameter(num+2, java.sql.Types.INTEGER);
	            proc.execute();
	            String result =  proc.getString(num+1);//得到返回值
	            Integer isSuccess =proc.getInt(num+2);
	
	            System.out.println(result);
	            System.out.println(isSuccess);
			}
	        dataSource.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return inputs;
	}
	

	@Override
	public Object call(java.sql.Connection conn) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}