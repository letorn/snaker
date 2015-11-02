package init;

import model.SkFile;
import model.UtDate;
import model.UtIndustry;
import model.UtPost;
import model.ViEnterprise;
import model.ViEntpost;
import model.ViJobhunter;
import plugin.SQLPlugin;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;

import controller.DataEnterpriseController;
import controller.DataFileController;
import controller.DataJobhunterController;
import controller.IndexController;
import controller.InstanceController;
import controller.ModuleController;
import controller.ProcessController;
import engine.model.DbEnterprise;
import engine.model.DbEntpost;
import engine.model.DbJobhunter;
import engine.model.WfInstance;
import engine.model.WfProcess;
import engine.model.WfRecord;

/*
 * jfinal配置类
 */
public class AppConfig extends JFinalConfig {

	/**
	 * 配置常量值
	 */
	public void configConstant(Constants me) {
		// 加载jfinal配置文件
		PropKit.use("jfinal.properties");

		// 开发模式
		me.setDevMode(PropKit.getBoolean("devMode"));
		// 文件保存目录
		me.setUploadedFileSaveDirectory("temp");
	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		//上下文
		me.add(new ContextPathHandler("ctx"));
	}

	/**
	 * 配置拦截器
	 */
	public void configInterceptor(Interceptors me) {

	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 配置数据库自动脚本
		if (PropKit.getBoolean("c3p0.init")) {
			SQLPlugin sqlPlugin = new SQLPlugin(PropKit.get("c3p0.url"), PropKit.get("c3p0.user"), PropKit.get("c3p0.pwd").trim());
			me.add(sqlPlugin);
		}

		// 配置C3P0数据库连接池插件
		C3p0Plugin c3p0Plugin = new C3p0Plugin(PropKit.get("c3p0.url"), PropKit.get("c3p0.user"), PropKit.get("c3p0.pwd").trim());
		me.add(c3p0Plugin);

		// 配置ActiveRecord插件
		ActiveRecordPlugin c3p0Arp = new ActiveRecordPlugin(c3p0Plugin);
		// 工作流
		c3p0Arp.addMapping("wf_process", WfProcess.class);
		c3p0Arp.addMapping("wf_instance", WfInstance.class);
		c3p0Arp.addMapping("wf_record", WfRecord.class);
		// 自动采集
		c3p0Arp.addMapping("db_enterprise", DbEnterprise.class);
		c3p0Arp.addMapping("db_entpost", DbEntpost.class);
		c3p0Arp.addMapping("db_jobhunter", DbJobhunter.class);
		// 视图
		c3p0Arp.addMapping("vi_enterprise", ViEnterprise.class);
		c3p0Arp.addMapping("vi_entpost", ViEntpost.class);
		c3p0Arp.addMapping("vi_jobhunter", ViJobhunter.class);
		// 项目
		c3p0Arp.addMapping("sk_file", SkFile.class);
		// 工具
		c3p0Arp.addMapping("ut_date", UtDate.class);
		c3p0Arp.addMapping("ut_industry", UtIndustry.class);
		c3p0Arp.addMapping("ut_post", UtPost.class);
		me.add(c3p0Arp);
		
		// 配置正式环境数据库连接池插件
		C3p0Plugin zcdhPlugin = new C3p0Plugin(PropKit.get("zcdh.url"), PropKit.get("zcdh.user"), PropKit.get("zcdh.pwd").trim());
		me.add(zcdhPlugin);
		ActiveRecordPlugin zcdhArp = new ActiveRecordPlugin("zcdh", zcdhPlugin);
		me.add(zcdhArp);
	}

	/**
	 * 配置访问路由
	 */
	public void configRoute(Routes me) {
		// 主页相关
		me.add("/", IndexController.class);
		
		// 数据相关
		me.add("/data/file", DataFileController.class);// 文件
		me.add("/data/enterprise", DataEnterpriseController.class);// 企业信息
		me.add("/data/jobhunter", DataJobhunterController.class);// 求职者信息
				
		// snaker流程相关
		me.add("/process", ProcessController.class);// 工作流程
		me.add("/instance", InstanceController.class);// 流程实例
		
		// module组件相关
		me.add("/module", ModuleController.class);
	}

}
