package util;

import static util.Validator.blank;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.Version;

/**
 * @author kyz 生成html页面
 */
public class CreateHtml {

	private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	public static String create(String fileName, Map<String, Object> paramMap, String path) {
		try {
			if (blank(fileName))
				fileName = dateFormat.format(new Date()) + "_" + new Random().nextInt(1000) + ".html";

			// 创建一个合适的Configration对象 ：jquery mobile
			Configuration mobileConfiguration = new Configuration(new Version("2.3.23"));
			// 模板文件的路径
			String tempPath = path + "/" + "mobile/";
			mobileConfiguration.setDirectoryForTemplateLoading(new File(tempPath));
			mobileConfiguration.setObjectWrapper(new DefaultObjectWrapper(new Version("2.3.23")));
			mobileConfiguration.setDefaultEncoding("UTF-8");// 这个一定要设置，不然在生成的页面中
			// 获取或创建一个模版。
			Template mobileTemplate = blank(paramMap.get("coverImg")) ? mobileConfiguration.getTemplate("mobile_no_cover.html") : mobileConfiguration.getTemplate("mobile.html");
			Writer writer = new OutputStreamWriter(new FileOutputStream(path + "/" + "mobile_html/" + fileName), "UTF-8");
			mobileTemplate.process(paramMap, writer);

			// 创建一个合适的Configration对象 ：html
			Configuration htmlConfiguration = new Configuration(new Version("2.3.23"));
			// 模板文件的路径
			htmlConfiguration.setDirectoryForTemplateLoading(new File(tempPath));
			htmlConfiguration.setObjectWrapper(new DefaultObjectWrapper(new Version("2.3.23")));
			htmlConfiguration.setDefaultEncoding("UTF-8");// 这个一定要设置，不然在生成的页面中
			// 获取或创建一个模版。
			Template htmlTemplate = mobileConfiguration.getTemplate("static.html");

			Writer htmlWriter = new OutputStreamWriter(new FileOutputStream(path + "/" + "native_html/" + fileName), "UTF-8");
			htmlTemplate.process(paramMap, htmlWriter);
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
