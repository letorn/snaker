package engine.module;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.axis.encoding.Base64;

import test.DuXMLDoc;
import util.HbClient;

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
public class HbInputModule extends Module  {

	private String dxUrl="http://10.128.10.141:9090/dxservice/svcs/DataService";
	private String dxUser="admin";
	private String dxPwd="1";
	private String interfaceName;
	String appName = "职场导航网络科技有限公司";
	String taskOId = "hbjyweb_webservice_cb20_xml_task";

	private String p=
			"<maps>"+
			"<map><key><![CDATA[ACB200]]></key><value><![CDATA[E5AF7B1AB654C27AE040800A9A083FA2]]></value></map> "+
			"<map><key><![CDATA[AAB004]]></key><value><![CDATA[%]]></value></map>"+
			"<map><key><![CDATA[AAB003]]></key><value><![CDATA[%]]></value></map>"+ 
			"<map><key><![CDATA[AAE022]]></key><value><![CDATA[%]]></value></map>"+
			"<map><key><![CDATA[AAF036_1]]></key><value><![CDATA[20150701]]></value></map>"+
			"<map><key><![CDATA[AAF036_2]]></key><value><![CDATA[20150702]]></value></map>"+ 
			"</maps>";
	
	
	private List<Map<String, Object>> tableFields;
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */

	public ModuleData execute(ModuleData inputs) {
		HbClient client = new HbClient(dxUrl, dxUser, dxPwd, "get");
		Object[] resps = client.execute(appName, taskOId, p);
		System.out.println(resps[1]);
		DuXMLDoc doc = new DuXMLDoc();
		inputs= doc.xmlElements(resps[1].toString());
		return inputs;
	}
	
	
	
}