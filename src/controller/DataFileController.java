package controller;

import static util.Validator.blank;
import static util.Validator.notBlank;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.SkFile;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

/*
 * 控制类 - 文件相关
 */
public class DataFileController extends Controller {

	/*
	 * 返回到页面的json数据
	 */
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	private List<Object> dataList = new ArrayList<Object>();
	
	/**
	 * 列表
	 * page 页码
	 * rows 每页多少条记录
	 * name 文件名
	 * ftype 文件类型
	 */
	public void index() {
		Integer page = getParaToInt("page", 1);
		Integer rows = getParaToInt("rows", 30);
		String name = getPara("name", "");
		String ftype = getPara("ftype", "");
		
		Page<SkFile> pager = SkFile.dao.paginate(page, rows, "select id,name,suffix,ftype,update_date", "from sk_file where name like ? and ftype like ?", "%" + name + "%", "%" + ftype + "%");
		dataMap.put("total", pager.getTotalRow());
		dataMap.put("rows", pager.getList());
		renderJson(dataMap);
	}

	/**
	 * 列表
	 * page 页码
	 * rows 每页多少条记录
	 * name 文件名
	 * ftype 文件类型
	 */
	public void list() {
		String ftype = getPara("ftype", "");
		
		renderJson(SkFile.dao.find("select id,name from sk_file where ftype=?", ftype));
	}
	
	/**
	 * 上传或更新文件
	 * file 文件
	 * file 文件主键
	 * ftype 文件类型
	 */
	public void save() {
		UploadFile uploadFile = getFile("file");
		Long fileId = getParaToLong("file");
		String ftype = getPara("ftype");

		if (notBlank(uploadFile)) {
			if (blank(fileId)) {
				String name = uploadFile.getOriginalFileName();
				String suffix = name.substring(name.lastIndexOf(".") + 1);
				SkFile skFile =  new SkFile().set("name", name).set("suffix", suffix).set("ftype", ftype).set("update_date", new Date());
				try (FileInputStream  fis = new FileInputStream(uploadFile.getFile())) {
					skFile.set("content", fis);
					dataMap.put("success", skFile.save());
					dataMap.put("file", name);
				} catch (Exception e) {}
			} else {
				SkFile skFile = SkFile.dao.findById(fileId);
				if (notBlank(skFile)) {
					try (FileInputStream  fis = new FileInputStream(uploadFile.getFile())) {
						skFile.set("content", fis).set("update_date", new Date());
						dataMap.put("success", skFile.update());
					} catch (Exception e) {}
				}
			}
		}
		renderJson(dataMap);
	}
	
	/**
	 * 删除文件
	 * {fileId} 文件主键
	 */
	public void delete() {
		Long fileId = getParaToLong();

		if (notBlank(fileId))
			if (SkFile.dao.deleteById(fileId))
				dataMap.put("success", true);
		renderJson(dataMap);
	}
	
	/**
	 * 下载文件
	 * {fileId} 文件主键
	 */
	public void download() {
		Long fileId = getParaToLong();

		SkFile skFile = SkFile.dao.findById(fileId);
		File file = new File(skFile.getStr("name"));
		byte[] content = skFile.getBytes("content");
		try (FileOutputStream out = new FileOutputStream(file);) {
			out.write(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderFile(file);
	}
	
}
