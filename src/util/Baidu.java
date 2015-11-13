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
			return new Double[2];
		if (blank(city))
			city = "";
		try {
			address = URLEncoder.encode(address, "utf-8");
			city = URLEncoder.encode(city, "utf-8");
			HttpGet httpGet = new HttpGet(String.format("%s&address=%s&city=%s", url, address, city));
			httpGet.setConfig(requestConfig);
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			JSONObject contentObject = JSONObject.fromObject(EntityUtils.toString(httpResponse.getEntity(), "UTF-8"));
			JSONObject resultObject = contentObject.getJSONObject("result");
			if (resultObject.isNullObject()) {
				return new Double[2];
			}
			JSONObject locationObject = resultObject.getJSONObject("location");
			Double lon = locationObject.getDouble("lng");
			Double lat = locationObject.getDouble("lat");
			return new Double[] { lon, lat };
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Double[2];
	}
	
	public static void main(String[] args) {
		Double[] lbs = getPoint("珠海", null);
		System.out.println(Arrays.toString(lbs));
	}
	
}
