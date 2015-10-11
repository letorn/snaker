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
		rows.add(row);
	}

	public void addAll(List<Map<String, Object>> rows) {
		rows.addAll(rows);
	}

	static class DataHeader {
		private String text;
		private Class<Object> type;
		private String format;

		public DataHeader() {

		}

		public DataHeader(String text, Class<Object> type, String format) {
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

		public Class<Object> getType() {
			return type;
		}

		public void setType(Class<Object> type) {
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
