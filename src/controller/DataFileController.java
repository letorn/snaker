package controller;

import static util.Validator.blank;
import static util.Validator.notBlank;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.SkFile;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;

/*
 * 控制类 - 文件相关
 */
public class DataFileController extends Controller {

	/*
	 * 返回到页面的json数据
	 */
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	
	/**
	 * 列表
	 */
	public void index() {
		String name = getPara("name");
		String ftype = getPara("ftype");
		if (blank(name))
			name = "";
		if (blank(ftype))
			ftype = "";
		List<SkFile> skFiles = SkFile.dao.find("select id,name,suffix,ftype from sk_file where name like ? and ftype like ?", "%" + name + "%", "%" + ftype + "%");
		renderJson(skFiles);
	}

	/**
	 * 上传或更新文件
	 * file 文件
	 */
	public void save() {
		UploadFile uploadFile = getFile("file");
		Long fileId = getParaToLong("file");
		String ftype = getPara("ftype");
		if (notBlank(uploadFile)) {
			if (blank(fileId)) {
				String name = uploadFile.getOriginalFileName();
				String suffix = name.substring(name.lastIndexOf(".") + 1);
				SkFile skFile =  new SkFile().set("name", name).set("suffix", suffix).set("ftype", ftype);
				try (FileInputStream  fis = new FileInputStream(uploadFile.getFile())) {
					skFile.set("content", fis);
					dataMap.put("success", skFile.save());
				} catch (Exception e) {}
			} else {
				SkFile skFile = SkFile.dao.findById(fileId);
				if (notBlank(skFile)) {
					try (FileInputStream  fis = new FileInputStream(uploadFile.getFile())) {
						skFile.set("content", fis);
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
