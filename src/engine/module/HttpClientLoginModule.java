/**
 * @author wh
 */
package engine.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
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
	private Map<String, String> cookie = new HashMap<String, String>();

	public Map<String, String> getCookie() {
		return this.cookie;
	}
	
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
			client.execute(login);
			List<Cookie> cookies = cookieStore.getCookies();
			for (Cookie cookie : cookies) {
				this.cookie.put(cookie.getName(), cookie.getValue());
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

}
