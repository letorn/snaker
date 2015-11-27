package engine.module;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import engine.ModuleData;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import util.File;

public class PostWebInputModule extends Module {

	private String startUrl;
	private List<Map<String, String>> headers;
	private List<Map<String, String>> cookies;
	private List<Map<String, String>> formDatas;
	private String method;
	private String threadNum;
	private String helpUrl;
	private String pageFlag;
	private String cursorXpath;
	private String targetUrl;
	private String skipJugment;
	private List<Map<String, String>> dataPaths;
	private SnakerProcessor snakerProcessor = new SnakerProcessor();
	private Spider spider = Spider.create(snakerProcessor);
	
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	public ModuleData execute(ModuleData inputs) {
		return inputs;
	}
	
	@Override
	public boolean isAlive() {
		return spider.getStatus() == Spider.Status.Running;
	}
	
	@Override
	public void stop() {
		spider.stop();
	}
	
	@Override
	public void run() {
		if (spider.getStatus() == Spider.Status.Init) {
			if (headers != null && headers.size() > 0) {
				snakerProcessor.initHeaders(headers);
			}
			if (cookies != null && cookies.size() > 0) {
				snakerProcessor.initCookies(cookies);
			}
			Map<String, Object> params = new HashMap<String, Object>();
			if (formDatas != null && formDatas.size() > 0) {
				int length = formDatas.size();
				int count = 0;
				NameValuePair[] nameValuePair = new NameValuePair[length];
				for (Map<String, String> formData : formDatas) {
					nameValuePair[count] = new BasicNameValuePair(formData.get("name"), formData.get("value"));
					count++;
				}
				params.put("nameValuePair", nameValuePair);
			}
			Request request = new Request();
			request.setMethod(method);
			request.setExtras(params);
			request.setUrl(startUrl);
			spider = spider.addRequest(request);
			spider = spider.thread(Integer.parseInt(threadNum));
			spider = spider.addPipeline(new OutputPipeline(inputs, this));
			if (workflow.isDaemon()) {
				spider.start();
			} else {
				spider.run();
			}
		}
		if (spider.getStatus() == Spider.Status.Running || spider.getStatus() == Spider.Status.Stopped) {
			super.run();
		}
	}

	/**
	 * 内部类，抓取页面内容
	 * @author wanghao
	 *
	 */
	private class SnakerProcessor implements PageProcessor {

		private Site site = Site.me().setRetryTimes(5).setTimeOut(30000);
		
		public void initHeaders(List<Map<String, String>> headers) {
			for (Map<String, String> header : headers) {
				site = site.addHeader(header.get("headerName"), header.get("headerValue"));
			}
		}
		
		public void initCookies(List<Map<String, String>> cookies) {
			for (Map<String, String> cookie : cookies) {
				site = site.addCookie(cookie.get("cookieName"), cookie.get("cookieValue"));
			}
		}
		
		@Override
		public void process(Page page) {
			if (page.getUrl().toString().matches(targetUrl)) {
				if (skipJugment != null && skipJugment.length() > 0) {
					if (page.getHtml().xpath(skipJugment).toString() != null && page.getHtml().xpath(skipJugment).toString().length() > 0) {
						page.setSkip(true);
					}
				}
				targetProcess(page);
			} else if (page.getUrl().toString().matches(helpUrl)) {
				helpProcess(page);
			} else {
				page.setSkip(true);
			}
		}
		
		private void targetProcess(Page page) {
			for (Map<String, String> dataPath : dataPaths) {
				String attr = dataPath.get("name");
				String xpath = dataPath.get("xpath");
				String regex = dataPath.get("regex");
				String isAll = dataPath.get("isAll");
				Selectable result = page.getHtml();
				if (attr == null || attr.length() == 0) {
					continue;
				}
				if ("url".equals(attr.toLowerCase())) {
					result = page.getUrl();
				}
				if (xpath != null && xpath.length() > 0) {
					result = result.xpath(xpath);
				}
				if (regex != null && regex.length() > 0) {
					result = result.regex(regex);
				}
				if (isAll != null && isAll.length() > 0 && "true".equals(isAll.toLowerCase())) {
					page.putField(attr, result.all());
				} else {
					String out = result.toString();
					if (out == null || out.length() == 0) {
						page.putField(attr, out);
					} else {
						page.putField(attr, out.replaceAll(" ", ""));
					}
				}
			}
		}
		
		private void helpProcess(Page page) {
			page.addTargetRequests(page.getHtml().links().regex(targetUrl).all());
			List<String> cursors = page.getHtml().xpath(cursorXpath).all();
			for (String cursor : cursors) {
				if(cursor.matches("^\\d*$")) {
					Map<String, Object> params = new HashMap<String, Object>();
					if (formDatas != null && formDatas.size() > 0) {
						int length = formDatas.size();
						int count = 0;
						NameValuePair[] nameValuePair = new NameValuePair[length];
						for (Map<String, String> formData : formDatas) {
							if (pageFlag.equals(formData.get("name"))) {
								nameValuePair[count] = new BasicNameValuePair(pageFlag, cursor);
							} else {
								nameValuePair[count] = new BasicNameValuePair(formData.get("name"), formData.get("value"));
							}
							count++;
						}
						params.put("nameValuePair", nameValuePair);
					}
					Request request = new Request();
					request.setMethod(method);
					request.setExtras(params);
					request.setUrl(startUrl + "?" +  cursor);
					page.addTargetRequest(request);
				}
			}
		}

		@Override
		public Site getSite() {
			return site;
		}
		
	}
	
	/**
	 * 输出类
	 * @author wanghao
	 *
	 */
	private class OutputPipeline implements Pipeline, Closeable {

		private ModuleData inputs;
		private Module module;
		public OutputPipeline(ModuleData inputs, Module module){
			this.inputs = inputs;
			this.module = module;
		}
		
		public synchronized void process(ResultItems resultItems, Task task) {
			if (resultItems.getRequest().getUrl().matches(targetUrl)){
				inputs.add(resultItems.getAll());
			}
			if (inputs.getRows().size() >= 30) {
				output();
				inputs = new ModuleData();
			}
		}

		public void close() throws IOException {
			output();
		}
		
		private void output() {
			module.setInputs(inputs);
			if (workflow.isDaemon()) {
				module.start();
			} else {
				module.run();
			}
		}
		
	}
	
}
