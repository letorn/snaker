/**
 * @author wh
 */
package engine.module;

import static util.Validator.notBlank;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import engine.ModuleData;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/*
 * 流程模型 - 页面爬取
 * 爬虫
 */
public class WebInputModule extends Module {

	private List<Map<String, String>> startUrls;
	private String charset;
	private List<Map<String, String>> headers;
	private List<Map<String, String>> cookies;
	private String threadNum;
	private String helpRegion;
	private String helpUrl;
	private String targetNum;
	private String targetUrl;
	private String skipJugment;
	private String cookieDomain;
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
			if (notBlank(charset)) {
				snakerProcessor.initCharset(charset);
			}
			if (notBlank(headers)) {
				snakerProcessor.initHeaders(headers);
			}
			if (notBlank(cookies)) {
				snakerProcessor.initCookies(cookies);
			}
			for (Module pre : this.prevModules) {
				if (pre instanceof HttpClientLoginModule) {
					Map<String, String> cookie = ((HttpClientLoginModule)pre).getCookie();
					snakerProcessor.initCookies(cookie);
				}
				if (pre instanceof AddFieldModule) {
					Map<String, String> cookie = ((AddFieldModule) pre).getCookie();
					snakerProcessor.initCookies(cookie);
				}
			}
			for (Map<String, String> url : startUrls) {
				spider = spider.addUrl(url.get("startUrl"));
			}
			spider = spider.thread(Integer.parseInt(threadNum));
			spider = spider.addPipeline(new OutputPipeline(inputs, this));
			if (workflow.isDaemon()) {
				spider.start();
			} else {
				spider.run();
			}
		}
	}
	
	/**
	 * 内部类，抓取页面内容
	 */
	private class SnakerProcessor implements PageProcessor {

		private Site site = Site.me().setRetryTimes(5).setTimeOut(30000);
		
		public void initCharset(String charset) {
			site = site.setCharset(charset);
		}
		
		public void initHeaders(List<Map<String, String>> headers) {
			for (Map<String, String> header : headers) {
				site = site.addHeader(header.get("headerName"), header.get("headerValue"));
			}
		}
		
		public void initCookies(List<Map<String, String>> cookies){
			for (Map<String, String> cookie : cookies) {
				if (notBlank(cookieDomain)) {
					site = site.addCookie(cookieDomain, cookie.get("cookieName"), cookie.get("cookieValue"));
				} else {
					site = site.addCookie(cookie.get("cookieName"), cookie.get("cookieValue"));
				}
			}
		}
		
		public void initCookies(Map<String, String> cookies){
			for (String cookieName : cookies.keySet()) {
				if (notBlank(cookieDomain)) {
					site = site.addCookie(cookieDomain, cookieName, cookies.get(cookieName));
				} else {
					site = site.addCookie(cookieName, cookies.get(cookieName));
				}
			}
		}

		public void process(Page page) {
			if (page.getUrl().toString().matches(targetUrl)) {
				if (notBlank(skipJugment)) {
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
				if (!notBlank(attr)) {
					continue;
				}
				if ("url".equals(attr.toLowerCase())) {
					result = page.getUrl();
				}
				if (notBlank(xpath)) {
					result = result.xpath(xpath);
				}
				if (notBlank(regex)) {
					result = result.regex(regex);
				}
				if (notBlank(isAll) && "true".equals(isAll.toLowerCase())) {
					List<String> out = new ArrayList<String>();
					for (String output : result.all()) {
						out.add(output.replaceAll(" ", ""));
					}
					page.putField(attr, out);
				} else {
					String out = result.toString();
					if (!notBlank(out)) {
						page.putField(attr, out);
					} else {
						page.putField(attr, out.replaceAll(" ", ""));
					}
				}
			}
		}
		
		private void helpProcess(Page page) {
			if (notBlank(targetNum)) {
				int num = Integer.parseInt(targetNum);
				for (String url : page.getHtml().links().regex(targetUrl).all()) {
					page.addTargetRequest(url);
					num--;
					if (num == 0)
						break;
				}
			} else {
				page.addTargetRequests(page.getHtml().links().regex(targetUrl).all());
			}
			if (notBlank(helpRegion)) {				
				page.addTargetRequests(page.getHtml().xpath(helpRegion).links().regex(helpUrl).all());
			} else {
				page.addTargetRequests(page.getHtml().links().regex(helpUrl).all());
			}
		}

		public Site getSite() {
			return site;
		}
		
	}
	
	/**
	 * 输出类
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
			}
		}

		public void close() throws IOException {
			output();
		}
		
		private void output() {
			module.setInputs(inputs);
			outputs = execute(inputs);
			if (doRecord) {
				records.addAll(outputs.getRows());
			}
			for (Module module : nextModules) {
				module.setInputs(outputs);
				if (workflow.isDaemon()) {
					module.start();
				} else {
					module.run();
				}
			}
			inputs = new ModuleData();
		}
		
	}
	
}