package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;



import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * @author kyz
 *	生成html页面
 */
public class CreateHtml {
	public static String create(String fileName ,Map<String, Object> paramMap,String path) throws Exception {

		String htmlName = fileName.equals("")?"":fileName;
		if( htmlName.equals("") ){
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			htmlName = df.format(new Date()) + "_" + new Random().nextInt(1000) + ".html";
		}
		
		// 创建一个合适的Configration对象 ：jquery mobile
		Configuration mobileConfiguration = new Configuration();
		// 模板文件的路径
		String tempPath = path+"/" + "mobile/";
		mobileConfiguration.setDirectoryForTemplateLoading(new File(tempPath));
		mobileConfiguration.setObjectWrapper(new DefaultObjectWrapper());
		mobileConfiguration.setDefaultEncoding("UTF-8"); // 这个一定要设置，不然在生成的页面中
															// 会乱码
		// 获取或创建一个模版。
		Template mobileTemplate = null; 
		if( paramMap.get("coverImg") != null ){ 
			mobileTemplate	= mobileConfiguration.getTemplate("mobile.html");
		}else{
			mobileTemplate	= mobileConfiguration.getTemplate("mobile_no_cover.html");
		}

		// 文件保存目录路径
		String desPath = "";
		if (true) {
			desPath =  path+"/" + "mobile_html/";
		} 
		Writer writer = new OutputStreamWriter(new FileOutputStream(desPath + htmlName), "UTF-8");
		mobileTemplate.process(paramMap, writer);

		// 创建一个合适的Configration对象 ：html
		Configuration htmlConfiguration = new Configuration();
		// 模板文件的路径
		htmlConfiguration.setDirectoryForTemplateLoading(new File(tempPath));
		htmlConfiguration.setObjectWrapper(new DefaultObjectWrapper());
		htmlConfiguration.setDefaultEncoding("UTF-8"); // 这个一定要设置，不然在生成的页面中
															// 会乱码
		// 获取或创建一个模版。
		Template htmlTemplate = mobileConfiguration.getTemplate("static.html");

		// 文件保存目录路径
		if (true) {
			desPath =  path+"/"+ "native_html/";
		} 
		try {
			Writer htmlWriter = new OutputStreamWriter(new FileOutputStream(desPath + htmlName), "UTF-8");
			htmlTemplate.process(paramMap, htmlWriter);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return htmlName;
	
	}
}
