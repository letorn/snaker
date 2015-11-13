package engine.module;

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
	private String threadNum;
	private String helpRegion;
	private String helpUrl;
	private String targetUrl;
	private String skipJugment;
	private List<Map<String, String>> dataPaths;
	private Spider spider = Spider.create(new SnakerProcessor());
	
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	
	public ModuleData execute(ModuleData inputs) {
		return inputs;
	}
	
	@Override
	public void run() {
		if (spider.getStatus() == Spider.Status.Init) {
			List<String> urls = new ArrayList<String>();
			for (Map<String, String> url : startUrls) {
				urls.add(url.get("startUrl"));
			}
			spider = spider.startUrls(urls);
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

		private Site site = Site.me().setRetryTimes(3).setTimeOut(30000);

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
						page.putField(attr, out.trim());
					}
				}
			}
		}
		
		private void helpProcess(Page page) {
			page.addTargetRequests(page.getHtml().links().regex(targetUrl).all());
			if (helpRegion != null && helpRegion.length() > 0) {				
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