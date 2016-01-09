package controller;

import static util.Validator.notBlank;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import service.SnakerService;
import util.Json;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import engine.ModuleData;
import engine.ModuleData.DataHeader;
import engine.Workflow;
import engine.module.Module;

/*
 * 控制类 - 流程实例相关
 */
public class InstanceController extends Controller {

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/*
	 * snaker工作流程服务类
	 */
	private SnakerService snakerService = enhance(SnakerService.class);
	
	/*
	 * 返回到页面的json数据
	 */
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	private List<Object> dataList = new ArrayList<Object>();
	
	/*
	 * 下载Excel时使用的映射Map
	 */
	private Map<String, Integer> colNameMapper = new HashMap<String, Integer>();
	
	/**
	 * 列表
	 * page 页码
	 * rows 每页多少条记录
	 * name 工作流程名称
	 */
	public void index() {
		Integer page = getParaToInt("page", 1);
		Integer rows = getParaToInt("rows", 30);
		String name = getPara("name", "");
		if (page < 1) page = 1;
		if (rows < 1) rows = 1;

		Page<Workflow> pager = snakerService.findInstance(page, rows, name);
		for (Workflow instance : pager.getList()) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("processId", instance.getProcessId());
			data.put("processName", instance.getProcessName());
			data.put("instanceId", instance.getInstanceId());
			data.put("instanceParams", instance.getInstanceParams());
			data.put("instanceCreateDate", dateFormat.format(instance.getInstanceCreateDate()));
			data.put("instanceIsAlive", instance.isAlive());
			dataList.add(data);
		}
		dataMap.put("total", pager.getTotalRow());
		dataMap.put("rows", dataList);
		renderJson(dataMap);
	}
	
	/**
	 * 获取流程实例
	 * {instanceId} 流程实例主键
	 */
	public void all() {
		Long instanceId = getParaToLong();

		if (notBlank(instanceId)) {
			Workflow instance = snakerService.getInstance(instanceId);
			if (notBlank(instance)) {
				setAttr("message", instance.getMessage());
				setAttr("process", instance.getProcessId());
				setAttr("processName", instance.getProcessName());
				setAttr("instance", instance.getInstanceId());
				setAttr("instanceIsAlive", instance.isAlive());
				setAttr("instanceParams", instance.getInstanceParams());
				List<Map<String, Object>> views = new ArrayList<Map<String, Object>>();
				for (Module module : instance.getModules()) {
					if (module.isDoRecord() && notBlank(module.getRecordView())) {
						Map<String, Object> view = new HashMap<String, Object>();
						view.put("mtype", module.getMtype());
						view.put("name", module.getName());
						view.put("form", module.getRecordView());
						views.add(view);
					}
				}
				setAttr("views", Json.toString(views));
			}
		}

		render("/instance/all.html");
	}
	
	/**
	 * 运行输出日志
	 * instance 实例参数
	 * module 模型名称
	 * page 页码
	 * refresh 是否刷新表头
	 */
	public void record() {
		Long instanceId = getParaToLong("instance");
		String module = getPara("module");
		Integer page = getParaToInt("page", 1);
		Integer rows = getParaToInt("rows", 30);
		Boolean refresh = getParaToBoolean("refresh", true);
		if (page < 1) page = 1;
		if (rows < 1) rows = 1;

		if (notBlank(instanceId) && notBlank(module)) {
			ModuleData records = snakerService.getRecords(instanceId, module);
			if (notBlank(records)) {
				int fromIndex = (page - 1) * rows;
				int toIndex = fromIndex + rows;
				if (toIndex > records.getRows().size()) toIndex = records.getRows().size();

				List<Map<String, Object>> data = records.getRows().subList(fromIndex, toIndex);
				for (Map<String, Object> d : data) {
					for (String key : d.keySet()) {
						Object value = d.get(key);
						if (notBlank(value) && value instanceof String) {
							String str = (String) value;
							if (str.length() > 1000)
								d.put(key, str.substring(1000) + "...");
						}
					}
				}
				dataMap.put("rows", data);
				if (refresh) {
					dataMap.put("headers", records.getHeaders());
					dataMap.put("total", records.getRows().size());
				}
			}
		}

		renderJson(Json.toString(dataMap));
	}
	
	/**
	 * 下载数据到Excel表格
	 * instance 实例参数
	 * module 模型名称
	 * refresh 是否刷新表头
	 */
	public void download() {
		Long instanceId = getParaToLong("instance");
		String module = getPara("module");

		if (notBlank(instanceId) && notBlank(module)) {
			ModuleData records = snakerService.getRecords(instanceId, module);
			Workflow instance = snakerService.getInstance(instanceId);
			if (notBlank(records) && notBlank(instance)) {
				List<DataHeader> headers = records.getHeaders();
				List<Map<String, Object>> datas = records.getRows();
				Workbook workbook = new XSSFWorkbook();
				Sheet sheet = exportHeaders(workbook, headers);
				exportDatas(sheet, datas);
				File file = new File(instance.getProcessName() + "-" + module + ".xlsx");
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(file);
					workbook.write(fos);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
				renderFile(file);
			}
		}
	}
	
	/**
	 * 实例停止方法
	 * @instanceId 需要停止的实例ID
	 */
	public void stop() {
		Long instanceId = getParaToLong();
		if(notBlank(instanceId)) {
			snakerService.stopProcess(instanceId);
			dataMap.put("success", true);
		}
		renderJson(dataMap);
	}
	
	private Sheet exportHeaders(Workbook workbook, List<DataHeader> headers) {
		Sheet sheet = workbook.createSheet();
		Row row = sheet.createRow(0);
		int colIndex = 0;
		for (DataHeader header : headers) {
			createCell(row, colIndex, header.getName());
			colNameMapper.put(header.getName(), colIndex++);
		}
		return sheet;
	}
	
	private void exportDatas(Sheet sheet, List<Map<String, Object>> datas) {
		int rowIndex = 1;
		for (Map<String, Object> data : datas) {
			Row dataRow = sheet.createRow(rowIndex++);
			for (String key : data.keySet()) {
				createCell(dataRow, colNameMapper.get(key), data.get(key));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void createCell(Row row, int column, Object value) {
		Cell cell = row.createCell(column);
		String result = "";
		if (value instanceof List) {
			for (String v : (List<String>)value) {
				result = result + v + "\\";
			}
			result = result.substring(0, result.length() - 1);
		} else if (value instanceof Double) {
			result = ((Double)value).toString();
		} else if (value instanceof Integer) {
			result = ((Integer)value).toString();
		} else if (!notBlank(value)) {
			result = "";
		} else {
			result = (String)value;
		}
		cell.setCellValue(result);
	}
	
}
