package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * Excel解析工具
 */
public class Excel {
	
	private final static String XLS = "xls";
	private final static String XLSX = "xlsx";
	
	public static Map<String, String> exportFromExcel(InputStream is, String suffix, String sheetName) throws IOException {
		Map<String, String> result = new HashMap<String, String>();
		if (suffix.toLowerCase().equals(XLS)) {
			HSSFWorkbook workbook = new HSSFWorkbook(is);
			result = exportFromExcel(workbook, sheetName);
		} else if (suffix.toLowerCase().equals(XLSX)) {
			XSSFWorkbook workbook = new XSSFWorkbook(is);
			result = exportFromExcel(workbook, sheetName);
		}
		return result;
	}
	
	private static Map<String, String> exportFromExcel(Workbook workbook, String sheetName) throws IOException{
		Map<String, String> result = new HashMap<String, String>();
		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet == null) {
			return null;
		}
		for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if (row != null) {
				String code = getValue(row.getCell(0));
				String value = getValue(row.getCell(1));
//				String keyWord = getValue(row.getCell(2));
				result.put(code, value);
			}
		}
		return result;
	}
	
	private static String getValue(Cell cell) {
		if(cell == null){
			return null;
		}
		if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue());
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return String.valueOf(cell.getNumericCellValue());
		} else {
			return String.valueOf(cell.getStringCellValue());
		}
	}
}
