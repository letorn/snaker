package engine.module;

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

	private String dataSrc;
	private String startUrl;
	private String helpRegion;
	private String helpUrl;
	private String targetUrl;
	private String skipJugment;
	private List<Map<String, String>> dataPaths;
	private Spider spider = Spider.create(new SnakerProcessor()).thread(16);
	
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	
	public ModuleData execute(ModuleData inputs) {
		// spider.addUrl(startUrl).addPipeline(new OutputPipeline(inputs)).run();
		return inputs;
	}

	protected void autoRun(ModuleData inputs) {
		spider.addUrl(startUrl).addPipeline(new OutputPipeline(inputs, this)).run();
//		spider.addUrl(startUrl).addPipeline(new ConsolePipeline()).run();
		
		super.autoRun(inputs);
	}
	
	/**
	 * 内部类，抓取页面内容
	 * @author wanghao
	 *
	 */
	private class SnakerProcessor implements PageProcessor {

		private Site site = Site.me().setRetryTimes(3).setTimeOut(5000);

		public void process(Page page) {
			if (page.getUrl().toString().matches(targetUrl)) {
				if (page.getHtml().xpath(skipJugment).toString() != null && page.getHtml().xpath(skipJugment).toString().length() > 0) {
					page.setSkip(true);
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
				page.putField("dataSrc", dataSrc);
				if ("url".equals(attr)) {
					result = page.getUrl();
				}
				if (xpath != null && xpath.length() > 0) {
					result = result.xpath(xpath);
				}
				if (regex != null && regex.length() > 0) {
					result = result.regex(regex);
				}
				if ("true".equals(isAll) || "True".equals(isAll) || "TRUE".equals(isAll)) {
					page.putField(attr, result.all());
				} else {
					String out = result.toString();
					if (out == null || out.length() == 0) {
						page.putField(attr, result.toString());
					} else {
						page.putField(attr, result.toString().trim());
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
	private class OutputPipeline implements Pipeline {

		private ModuleData inputs;
		private Module module;
		private boolean stop = false;
		private List<Map<String, Object>> tmp = new ArrayList<Map<String, Object>>();
		public OutputPipeline(ModuleData inputs, Module module){
			this.inputs = inputs;
			this.module = module;
		}
		
		public synchronized void process(ResultItems resultItems, Task task) {
			if (resultItems.getRequest().getUrl().matches(targetUrl)){
				tmp.add(resultItems.getAll());
			}
			if (tmp.size() >= 20) {
				inputs.addAll(tmp);
				tmp.clear();
				module.run(inputs);
				inputs = new ModuleData();
			}
			if ((!stop) && spider.getStatus() == Spider.Status.Stopped) {
				inputs.addAll(tmp);
				tmp.clear();
				module.run(inputs);
				stop = true;
			}
		}
		
	}
	
}