package engine.module;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import engine.ModuleData;
import model.SkFile;
import util.Excel;

/*
 * 流程模型 - Excel映射
 * 通过Excel文件映射数据
 */
public class ExcelMapperModule extends Module {

	private String excelFile;
	private static boolean initFlag = true;
	private Map<String, Map<String, String>> excelMaps = new HashMap<String, Map<String, String>>();
	
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	public ModuleData execute(ModuleData inputs) {
//		System.out.println(inputs.getRows());
		if (initFlag) {
			initMapper(inputs);
		}
		for (Map<String, Object> row : inputs.getRows()) {
			for (String attr : row.keySet()) {
				Map<String, String> tmp = excelMaps.get(attr);
				if (row.get(attr) == null || tmp == null || tmp.size() == 0) {
					continue;
				} else {
					if (row.get(attr) instanceof List) {
						StringBuffer sb = new StringBuffer();
						for (String value : (List<String>) row.get(attr)) {
							if (tmp.get(value) != null && tmp.get(value).length() > 0) {
								sb.append("system&" + tmp.get(value) + "&" + value + "$$");
							} else {
								sb.append("self&" + value + "$$");
							}
						}
						if (sb.length() > 2) {
							sb.delete(sb.length() - 2, sb.length());
						}
						row.put(attr, sb.toString());
					} else {
						row.put(attr, tmp.get(row.get(attr)));
					}
				}
			}
		}
		return inputs;
	}
	
	private void initMapper(ModuleData inputs) {
		SkFile skFile = SkFile.dao.findFirst("select * from sk_file where name='" + excelFile + "'");
		byte[] content = skFile.getBytes("content");
		ByteArrayInputStream bais = new ByteArrayInputStream(content);
		try {
			excelMaps = Excel.exportFromExcel(bais, FilenameUtils.getExtension(excelFile), inputs);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bais != null)
					bais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		initFlag = false;
	}

}
