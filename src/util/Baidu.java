package util;
import static util.Validator.blank;

import java.net.URLEncoder;
import java.util.Arrays;

import net.sf.json.JSONObject;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/*
 * 百度工具类
 */
public class Baidu {
	
	private static String url = "http://api.map.baidu.com/geocoder/v2/?ak=YcnpFYovxoDCqPvnsL89VD8U&output=json";
	private static CloseableHttpClient httpClient = HttpClients.createDefault();
	private static RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
	/**
	 * 根据地址获取坐标点
	 * @param address 详细地址
	 * @param city 城市
	 * @return 坐标点
	 */
	public static Double[] getPoint(String address, String city) {
		if (blank(address))
			return null;
		if (blank(city))
			city = "";
		try {
			HttpGet httpGet = new HttpGet(String.format("%s&address=%s&city=%s", url, URLEncoder.encode(address, "UTF-8"), URLEncoder.encode(city, "UTF-8")));
			httpGet.setConfig(requestConfig);
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			if (content.contains("location")) {
				JSONObject location = JSONObject.fromObject(content).getJSONObject("result").getJSONObject("location");
				Double lon = location.getDouble("lng");
				Double lat = location.getDouble("lat");
				return new Double[] { lon, lat };
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		Double[] lbs = getPoint("珠海", null);
		System.out.println(Arrays.toString(lbs));
	}
	
}
