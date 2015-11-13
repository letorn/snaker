package init;

import static util.Validator.notBlank;
import model.SkAdmin;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

/*
 * 请求拦截器
 */
public class WebInterceptor implements Interceptor {

	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		SkAdmin admin = controller.getSessionAttr("admin");
		if (PropKit.getBoolean("devMode") || notBlank(admin))
			inv.invoke();
		else
			controller.redirect("/login.html");
	}

}
