/**
 * @author wh
 */
package engine.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.ModuleData;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class PageAnalyserModule extends Module{

	private String analyserData;
	private String targetXpath;
	private List<Map<String, String>> analyserDataPaths;
	
	/**
	 * 单页分析模块
	 */
	public ModuleData execute(ModuleData inputs) {
		return new PageAnalyserProcessor(inputs).analyse();
	}

	private class PageAnalyserProcessor {
		
		private ModuleData inputs;
		private List<Map<String, Object>> rows;
		
		public PageAnalyserProcessor(ModuleData inputs) {
			this.inputs = new ModuleData();
			this.rows = inputs.getRows();
		}
		
		public ModuleData analyse() {
			for (Map<String, Object> row : rows) {
				for (String attr : row.keySet()) {
					if (attr.equals(analyserData)) {
						String content = (String)row.get(attr);
						List<String> result = new Html(content).xpath(targetXpath).all();
						for (String re : result) {
							Html page = new Html(re);
							Map<String, Object> toInputs = new HashMap<String, Object>();
							toInputs.putAll(row);
							for (Map<String, String> dataPath : analyserDataPaths) {
								String attribute = dataPath.get("name");
								String xpath = dataPath.get("xpath");
								String regex = dataPath.get("regex");
								String isAll = dataPath.get("isAll");
								Selectable r = page;
								if (attribute == null || attribute.length() == 0) {
									continue;
								}
								if (xpath != null && xpath.length() > 0) {
									r = r.xpath(xpath);
								}
								if (regex != null && regex.length() > 0) {
									r = r.regex(regex);
								}
								if (isAll != null && isAll.length() > 0 && "true".equals(isAll.toLowerCase())) {
									List<String> out = new ArrayList<String>();
									for (String output : r.all()) {
										out.add(output.replaceAll(" ", ""));
									}
									toInputs.put(attribute, out);
								} else {
									String out = r.toString();
									if (out == null || out.length() == 0) {
										toInputs.put(attribute, out);
									} else {
										toInputs.put(attribute, out.replaceAll(" ", ""));
									}
								}
							}
							inputs.add(toInputs);
						}
						break;
					}
				}
			}
			return inputs;
		}	
	}
}
