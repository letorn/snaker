package init;

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

import controller.IndexController;
import controller.ModuleController;
import controller.ProcessController;
import controller.WorkflowController;
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
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		arp.addMapping("wf_process", WfProcess.class);
		arp.addMapping("wf_instance", WfInstance.class);
		arp.addMapping("wf_record", WfRecord.class);
		me.add(arp);
	}

	/**
	 * 配置访问路由
	 */
	public void configRoute(Routes me) {
		// 主页相关
		me.add("/", IndexController.class);
		
		// snaker流程相关
		me.add("/process", ProcessController.class);
		me.add("/workflow", WorkflowController.class);
		
		// module组件相关
		me.add("/module", ModuleController.class);
	}

}
