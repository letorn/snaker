package engine.module;

import test.DuXMLDoc;
import util.HbClient;
import engine.ModuleData;


/*
 * 流程模型 - 输出到数据库
 * 数据库存储过程
 */
public class HbInputModule extends Module  {

	//private String dxUrl="http://10.128.10.141:9090/dxservice/svcs/DataService";
	private String dxUrl;
	//private String dxUser="admin";
	private String dxUser;
	//private String dxPwd="1";
	private String dxPwd;
	//private String interfaceName="hbjyweb_webservice_cb20_xml_task";
	private String interfaceName;
	String appName = "职场导航网络科技有限公司"; 
	
	private String ACB200; //经办机构区划代码
	private String AAB004; //单位名称
	private String AAB003; //组织机构代码
	private String AAE022; //经办机构区划代码
	private String AAF036_1;//最后一次修改经办日期起始
	private String AAF036_2; //最后一次修改经办日期终止
	private String ACB217; //职位名称
	private String ACA111;	//职业（工种）
	private String AAC002; //公民身份号码
	private String p=null;  // 参数 xml字符串
	private String AAC001;  //AAC001
	
	
	
	//private List<Map<String, Object>> tableFields;
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */

	public ModuleData execute(ModuleData inputs) {
		
		
		if(interfaceName.equals("hbjyweb_webservice_cb20_xml_task")){
			//用人单位信息查询
			p = "<maps>"+
				"<map><key><![CDATA[ACB200]]></key><value><![CDATA["+(ACB200==null||ACB200.equals("")?"%":ACB200)+"]]></value></map> "+
				"<map><key><![CDATA[AAB004]]></key><value><![CDATA["+(AAB004==null||AAB004.equals("")?"%":AAB004)+"]]></value></map>"+
				"<map><key><![CDATA[AAB003]]></key><value><![CDATA["+(AAB003==null||AAB003.equals("")?"%":AAB003)+"]]></value></map>"+ 
				"<map><key><![CDATA[AAE022]]></key><value><![CDATA["+(AAE022==null||AAE022.equals("")?"%":AAE022)+"]]></value></map>"+
				"<map><key><![CDATA[AAF036_1]]></key><value><![CDATA["+(AAF036_1==null||AAF036_1.equals("")?"%":AAF036_1)+"]]></value></map>"+
				"<map><key><![CDATA[AAF036_2]]></key><value><![CDATA["+(AAF036_2==null||AAF036_2.equals("")?"%":AAF036_2)+"]]></value></map>"+ 
				"</maps>";
		}else if(interfaceName.equals("hbjyweb_webservice_cb21_xml_task")){
			//招聘岗位信息查询
			p = "<maps>"+
					"<map><key><![CDATA[ACB200]]></key><value><![CDATA["+(ACB200==null||ACB200.equals("")?"%":ACB200)+"]]></value></map> "+
					"<map><key><![CDATA[ACB217]]></key><value><![CDATA["+(ACB217==null||ACB217.equals("")?"%":ACB217)+"]]></value></map> "+
					"<map><key><![CDATA[ACA111]]></key><value><![CDATA["+(ACA111==null||ACA111.equals("")?"%":ACA111)+"]]></value></map>"+
					"<map><key><![CDATA[AAF036_1]]></key><value><![CDATA["+(AAF036_1==null||AAF036_1.equals("")?"%":AAF036_1)+"]]></value></map>"+
					"<map><key><![CDATA[AAF036_2]]></key><value><![CDATA["+(AAF036_2==null||AAF036_2.equals("")?"%":AAF036_2)+"]]></value></map>"+ 
					"<map><key><![CDATA[AAE022]]></key><value><![CDATA["+(AAE022==null||AAE022.equals("")?"%":AAE022)+"]]></value></map>"+
					"</maps>";
		}else if(interfaceName.equals("hbjyweb_webservice_cc20_xml_task")){
			//个人信息查询
			p = "<maps>"+
					"<map><key><![CDATA[AAC001]]></key><value><![CDATA["+(AAC001==null||AAC001.equals("")?"%":AAC001)+"]]></value></map>" +
				"</maps>";
		} 
		HbClient client = new HbClient(dxUrl, dxUser, dxPwd, "get");
		Object[] resps = client.execute(appName, interfaceName, p);
		System.out.println(resps[1]);
		DuXMLDoc doc = new DuXMLDoc();
		inputs= doc.xmlElements(resps[1].toString());
		return inputs;
	}
	
	
	
}