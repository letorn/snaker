package init;

import model.SkAdmin;
import model.SkConfig;
import model.SkFile;
import model.UtArea;
import model.UtDate;
import model.UtGrowth;
import model.UtIndustry;
import model.UtPost;
import model.ViEnterprise;
import model.ViEntpost;
import model.ViJobhunter;
import model.ViTalk;
import plugin.SQLPlugin;
import util.FileKit;
import util.VarKit;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;

import controller.DataEnterpriseController;
import controller.DataEntpostController;
import controller.DataFileController;
import controller.DataJobhunterController;
import controller.DataTalkController;
import controller.IndexController;
import controller.InstanceController;
import controller.ModuleController;
import controller.ProcessController;
import controller.SystemController;
import engine.SnakerEngine;
import engine.model.DbEnterprise;
import engine.model.DbEntpost;
import engine.model.DbJobhunter;
import engine.model.DbTalk;
import engine.model.WfProcess;

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
		me.setDevMode(true);

		// 智联招聘证书路径
		System.setProperty("javax.net.ssl.trustStore", FileKit.classpathBy("jssecacerts"));
		
		// 上传文件大小限制100M
		me.setMaxPostSize(104857600);
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
		// 拦截器
		me.addGlobalActionInterceptor(new WebInterceptor());
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
		// 自动采集
		c3p0Arp.addMapping("db_enterprise", DbEnterprise.class);
		c3p0Arp.addMapping("db_entpost", DbEntpost.class);
		c3p0Arp.addMapping("db_jobhunter", DbJobhunter.class);
		c3p0Arp.addMapping("db_talk", DbTalk.class);
		// 视图
		c3p0Arp.addMapping("vi_enterprise", ViEnterprise.class);
		c3p0Arp.addMapping("vi_entpost", ViEntpost.class);
		c3p0Arp.addMapping("vi_jobhunter", ViJobhunter.class);
		c3p0Arp.addMapping("vi_talk", ViTalk.class);
		// 项目
		c3p0Arp.addMapping("sk_admin", SkAdmin.class);
		c3p0Arp.addMapping("sk_file", SkFile.class);
		c3p0Arp.addMapping("sk_config", SkConfig.class);
		// 工具
		c3p0Arp.addMapping("ut_date", UtDate.class);
		c3p0Arp.addMapping("ut_industry", UtIndustry.class);
		c3p0Arp.addMapping("ut_post", UtPost.class);
		c3p0Arp.addMapping("ut_growth", UtGrowth.class);
		c3p0Arp.addMapping("ut_area", UtArea.class);
		me.add(c3p0Arp);
		
		// 配置正式环境数据库连接池插件
		C3p0Plugin zcdhPlugin = new C3p0Plugin(PropKit.get("zcdh.url"), PropKit.get("zcdh.user"), PropKit.get("zcdh.pwd").trim());
		me.add(zcdhPlugin);
		ActiveRecordPlugin zcdhArp = new ActiveRecordPlugin("zcdh", zcdhPlugin);
		me.add(zcdhArp);
		
		// 定时器
		// SchedulePlugin schedulePlugin = new SchedulePlugin();
		// me.add(schedulePlugin);
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
		me.add("/data/entpost", DataEntpostController.class);// 岗位信息
		me.add("/data/jobhunter", DataJobhunterController.class);// 求职者信息
		me.add("/data/talk", DataTalkController.class);// 宣讲会信息
				
		// snaker流程相关
		me.add("/process", ProcessController.class);// 工作流程
		me.add("/instance", InstanceController.class);// 流程实例
		
		// module组件相关
		me.add("/module", ModuleController.class);
		
		// 系统相关
		me.add("/system", SystemController.class);
	}

	/**
	 * 启动后的事件
	 */
	public void afterJFinalStart() {
		// 初始化全局系统参数
		VarKit.init();
		// 初始化流程引擎
		SnakerEngine.init(PropKit.get("store.path"));
	}
	
	public static void main(String[] args) {
		JFinal.start("WebContent", 8080, "/snaker", 5);
	}
	
}
