package init;

import org.snaker.jfinal.plugin.SnakerPlugin;

import plugin.DBScriptPlugin;

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

import controller.FlowController;
import controller.IndexController;
import controller.ModuleController;
import controller.ProcessController;
import engine.model.WfProcess;

public class AppConfig extends JFinalConfig {

	public void configConstant(Constants me) {
		// 加载jfinal配置文件
		PropKit.use("jfinal.properties");

		// 开发模式
		me.setDevMode(PropKit.getBoolean("devMode"));
	}

	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("ctx"));
	}

	public void configInterceptor(Interceptors me) {

	}

	public void configPlugin(Plugins me) {
		// 配置C3P0数据库连接池插件
		C3p0Plugin c3p0Plugin = new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
		me.add(c3p0Plugin);

		DBScriptPlugin dbScriptPlugin = new DBScriptPlugin(c3p0Plugin);
		me.add(dbScriptPlugin);
		
		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		// arp.addMapping("user", User.class);
		arp.addMapping("wf_process", WfProcess.class);
		me.add(arp);

		// 配置Snaker插件
	    SnakerPlugin snakerPlugin = new SnakerPlugin(c3p0Plugin);
	    me.add(snakerPlugin);
	}

	public void configRoute(Routes me) {
		// 主页相关
		me.add("/", IndexController.class);
		
		// snaker流程相关
		me.add("/process", ProcessController.class);
		me.add("/flow", FlowController.class);
		
		// module组件相关
		me.add("/module", ModuleController.class);
	}

}
