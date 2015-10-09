package controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.snaker.engine.SnakerEngine;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.entity.Process;
import org.snaker.engine.helper.StreamHelper;
import org.snaker.engine.helper.StringHelper;
import org.snaker.jfinal.plugin.SnakerPlugin;

import com.jfinal.core.Controller;

public class ProcessController extends Controller{

	private SnakerEngine snakerEngine = SnakerPlugin.getEngine();
	private Map<String, Object> dataMap = new HashMap<String, Object>();
	
	public void init() {
		snakerEngine.process().deploy(StreamHelper.getStreamFromClasspath("flows/leave.snaker"));
		snakerEngine.process().deploy(StreamHelper.getStreamFromClasspath("flows/borrow.snaker"));
		dataMap.put("success", true);
		renderJson(dataMap);
	}
	
	public void list() {
		String displayName = getPara("displayName");
		
		QueryFilter filter = new QueryFilter();
		filter.setState(1);
		if(StringHelper.isNotEmpty(displayName)) {
			filter.setDisplayName(displayName);
		}
		List<Process> processes = snakerEngine.process().getProcesss( filter);
		renderJson(processes);
	}
	
	public void edit() {
		String processId = getPara();
		
		if(StringHelper.isNotEmpty(processId)) {
			Process process = snakerEngine.process().getProcessById(processId);
			if (process != null) {
				dataMap.put("id", process.getId());
				if(process.getDBContent() != null) {
		            try {
		            	dataMap.put("content", new String(process.getDBContent(), "UTF-8"));
		            } catch (UnsupportedEncodingException e) {
		                e.printStackTrace();
		            }
		        }
			}
		}
		dataMap.put("success", true);
		renderJson(dataMap);
	}
	
	public void deploy() {
		String processId = getPara("id");
		String content = getPara("content");
		
		InputStream contentStream = null;
		if(StringHelper.isNotEmpty(content)) {
			try {
				contentStream = new ByteArrayInputStream(content.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (contentStream != null) {
			if (StringHelper.isNotEmpty(processId)) {
				snakerEngine.process().redeploy(processId, contentStream);
			} else {
				snakerEngine.process().deploy(contentStream);
			}
			try {
				contentStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		dataMap.put("success", true);
		renderJson(dataMap);
	}
	
	public void delete() {
		String processId = getPara();
		
		if(StringHelper.isNotEmpty(processId)) {
			snakerEngine.process().undeploy(processId);
		}
		dataMap.put("success", true);
		renderJson(dataMap);
	}
	
	public void designer() {
		render("designer.html");
	}
	
}
