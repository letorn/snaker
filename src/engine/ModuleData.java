package engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/*
 * 模型数据
 * 用于输入输出
 */
public class ModuleData {

	private List<DataHeader> headers = new ArrayList<DataHeader>();
	private List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

	public List<DataHeader> getHeaders() {
		return this.headers;
	}

	public List<Map<String, Object>> getRows() {
		return rows;
	}

	public void add(Map<String, Object> row) {
		if (row.size() > headers.size())
			headers = catchHeaders(row);
		rows.add(row);
	}

	public void addAll(List<Map<String, Object>> rows) {
		if (rows.size() > 0) {
			Map<String, Object> row = rows.get(0);
			if (row.size() > headers.size())
				headers = catchHeaders(row);
		}
		rows.addAll(rows);
	}

	private List<DataHeader> catchHeaders (Map<String, Object> row) {
		List<DataHeader> headers = new ArrayList<DataHeader>();
		for (String key : row.keySet()) {
			Object value = row.get(key);
			DataHeader header = new DataHeader();
			header.setText(key);
			header.setType(value.getClass());
			headers.add(header);
		}
		return headers;
	}
	
	@SuppressWarnings("rawtypes")
	public static class DataHeader {
		private String text;
		private Class type;
		private String format;

		public DataHeader() {

		}

		public DataHeader(String text, Class type, String format) {
			this.text = text;
			this.type = type;
			this.format = format;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public Class getType() {
			return type;
		}

		public void setType(Class type) {
			this.type = type;
		}

		public String getFormat() {
			return format;
		}

		public void setFormat(String format) {
			this.format = format;
		}

	}

}
