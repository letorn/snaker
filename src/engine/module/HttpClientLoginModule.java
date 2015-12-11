/**
 * @author wh
 */
package engine.module;

import static util.Validator.notBlank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import engine.ModuleData;

public class HttpClientLoginModule extends Module{

//	private List<Map<String, String>> nameValuePairs;
	private String account;
	private String password;
	private String accountName;
	private String passwordName;
	private String loginUrl;
	private String verifyHeader;
	private String verifyString;
	private String errorMsg;
	private boolean success = false;
	private Map<String, String> cookie = new HashMap<String, String>();
	
	/**
	 * 模拟登录方法执行体
	 */
	@Override
	public ModuleData execute(ModuleData inputs) {
		List<NameValuePair> loginParames = new ArrayList<NameValuePair>();
//		for (Map<String, String> nameValuePair : nameValuePairs) {
//			loginParames.add(new BasicNameValuePair(nameValuePair.get("name"), nameValuePair.get("value")));
//		}
		loginParames.add(new BasicNameValuePair(accountName, account));
		loginParames.add(new BasicNameValuePair(passwordName, password));
		
		BasicCookieStore cookieStore = new BasicCookieStore();
		CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		
		HttpPost login = new HttpPost(loginUrl);
		
		try {
			HttpEntity he = new UrlEncodedFormEntity(loginParames);
			login.setEntity(he);
			HttpResponse response = client.execute(login);
			List<Cookie> cookies = cookieStore.getCookies();
			for (Cookie cookie: cookies) {
				this.cookie.put(cookie.getName(), cookie.getValue());
			}
			if (notBlank(verifyHeader)) {
				if ("Set-Cookie".equals(verifyHeader)) {
					for (String cookieName: this.cookie.keySet()) {
						if (cookieName.equals(verifyString)) {
							success = true;
						}
					}
					if (!success) {
						this.workflow.setMessage(errorMsg);
					}
				} else {
					String verifyValue = response.getFirstHeader(verifyHeader).getValue();
					if (verifyString.equals(verifyValue)) {
						this.workflow.setMessage(errorMsg);
					} else {
						success = true;
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException();
		} finally {							//关闭资源
			login.abort();
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return inputs;
	}
	
	@Override
	public void run(ModuleData inputs) {
		outputs = execute(inputs);
		if (doRecord) {
			records.addAll(outputs.getRows());
		}
		if (success) {
			for (Module module : nextModules) {
				module.setInputs(outputs);
				if (workflow.isDaemon()) {
					module.start();
				} else {
					module.run();
				}
			}
		} else {
			logger.error("模拟登录失败！");
		}
	}
	
	public Map<String, String> getCookie() {
		return this.cookie;
	}

}
