package test;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class Carte {

	// private static String url = "http://localhost:8080/snaker/process/start/";
	private static String url = "http://10.128.10.151:8080/snaker/process/start/";
	// private static String username = "zcdhjob";
	// private static String password = "zcdhjob";
	private static int timeout = 180000;

	private static CloseableHttpClient httpClient = HttpClients.createDefault();
	private static RequestConfig requestConfig;
	private static HttpPost httpPost;
	// private static Credentials credentials = new UsernamePasswordCredentials(username, password);

	static {
		requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).build();
	}

	public static boolean syncEnterpriseAndEntpostFromHBToWHDH(String enterpriseId) {
		return startProcess(1l, "{\"hb_input_ent\":{\"ACB200\":\"" + enterpriseId + "\"},\"hb_input_post\":{\"ACB200\":\"" + enterpriseId + "\"}}");
	}

	public static boolean syncEnterpriseAndEntpostFromWHDHToHB(String enterpriseId) {
		return false;
	}

	public static boolean syncJobhunterAndJobresumeFromHBToWHDH(String jobhunterId) {
		return startProcess(2l, "{\"hb_input_user\":{\"AAC001\":\"" + jobhunterId + "\"}}");
	}

	public static boolean syncJobhunterAndJobresumeFromWHDHToHB(String jobhunterId) {
		return false;
	}

	private static boolean startProcess(Long process, String params) {
		try {
			httpPost = new HttpPost(url + process);
			httpPost.setConfig(requestConfig);
			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair("daemon", "false"));
			nameValuePair.add(new BasicNameValuePair("params", params));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
			// System.out.println(EntityUtils.toString(httpResponse.getEntity(), "UTF-8"));
			JSONObject response = JSONObject.fromObject(EntityUtils.toString(httpResponse.getEntity(), "UTF-8"));
			return response.getBoolean("success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
		long begin = System.currentTimeMillis();
		// boolean synStatus = syncEnterpriseAndEntpostFromHBToWHDH("15D9A79A8A7A7C90E050800A8C0A6878");
		boolean synStatus = syncJobhunterAndJobresumeFromHBToWHDH("8a008a8e4148633c0141a20c15f61bf4");
		long end = System.currentTimeMillis();
		System.out.println("spent: " + (end - begin));
		System.out.println("synStatus: " + synStatus);
		
	}

}
