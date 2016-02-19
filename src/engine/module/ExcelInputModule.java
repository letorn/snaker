/**
 * @author wh
 */
package engine.module;

import static util.Validator.blank;
import static util.Validator.notBlank;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import engine.ModuleData;

/*
 * 流程模型 - Excel输入
 * 通过Excel文件输入数据
 */
public class ExcelInputModule extends Module {

	private File excelFile;
	private final static String XLS = "xls";
	private final static String XLSX = "xlsx";
	
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	
	public ModuleData execute(ModuleData inputs) {
		InputStream is = null;
		String suffix = FilenameUtils.getExtension(excelFile.getName());
		try {
			is = new FileInputStream(excelFile);
			inputs.addAll(excelParser(is, suffix));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return inputs;
	}
	
	private List<Map<String, Object>> excelParser(InputStream is, String suffix) throws IOException {
		Workbook workbook = null;
		if (suffix.toLowerCase().equals(XLS)) {
			workbook = new HSSFWorkbook(is);
		} else if (suffix.toLowerCase().equals(XLSX)) {
			workbook = new XSSFWorkbook(is);
		}
		return exportFromExcel(workbook);
	}
	
	
	/**
	 * 将数据从Excel表中导出
	 * @param workbook
	 * @return
	 * @throws IOException
	 */
	private List<Map<String, Object>> exportFromExcel(Workbook workbook) throws IOException {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Sheet sheet = workbook.getSheetAt(0);
		for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			Map<String, Object> rowResult = new HashMap<String, Object>();
			boolean isNullRow = true;
			if (row == null) {
				continue;
			}
			for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
				String header = getValue(sheet.getRow(0).getCell(colIndex));
				String value = getValue(row.getCell(colIndex));
				if (notBlank(header) && !"null".equalsIgnoreCase(header.trim())) {
					rowResult.put(header, value);
					isNullRow = isNullRow && (blank(value) || "null".equalsIgnoreCase(value.trim()));
				}
			}
			if (!isNullRow)
				result.add(rowResult);
		}
		return result;
	}
	
	/**
	 * 从Cell中获取值
	 * @param cell
	 * @return
	 */
	private static String getValue(Cell cell) {
		if(cell == null){
			return null;
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue());
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			return String.valueOf(cell.getNumericCellValue());
		} else {
			return String.valueOf(cell.getStringCellValue());
		}
	}

}