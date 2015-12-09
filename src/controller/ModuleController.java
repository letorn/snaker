package controller;

import static util.Validator.notBlank;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import service.SnakerService;
import util.Json;

import com.jfinal.core.Controller;

import engine.Workflow;
import engine.module.HttpClientLoginModule;
import engine.module.Module;

/*
 * 控制类 - 模型相关
 */
public class ModuleController extends Controller{

	/*
	 * snaker工作流程服务类
	 */
	private SnakerService snakerService = enhance(SnakerService.class);
	
	/**
	 * 开始
	 */
	public void begin() {
		Long processId = getParaToLong("process");
		Long instanceId = getParaToLong("instance");
		String moduleName = getPara("module");

		setAttr("process", processId);
		setAttr("instance", instanceId);
		setAttr("module", moduleName);

		render("/module/begin.html");
	}

	/**
	 * 结束
	 */
	public void end() {
		Long processId = getParaToLong("process");
		Long instanceId = getParaToLong("instance");
		String moduleName = getPara("module");

		setAttr("process", processId);
		setAttr("instance", instanceId);
		setAttr("module", moduleName);

		render("/module/end.html");
	}
	
	/**
	 * 表数据录入
	 */
	public void tableinput() {
		Long processId = getParaToLong("process");
		Long instanceId = getParaToLong("instance");
		String moduleName = getPara("module");

		setAttr("process", processId);
		setAttr("instance", instanceId);
		setAttr("module", moduleName);

		render("/module/tableinput.html");
	}
	
	/**
	 * 表数据录入
	 */
	public void basiclog() {
		Long processId = getParaToLong("process");
		Long instanceId = getParaToLong("instance");
		String moduleName = getPara("module");

		setAttr("process", processId);
		setAttr("instance", instanceId);
		setAttr("module", moduleName);

		render("/module/basiclog.html");
	}
	
	/**
	 * 登录
	 */
	public void login() {
		Long processId = getParaToLong();
		if (notBlank(processId)) {
			setAttr("process", processId);
			Workflow process = snakerService.getProcess(processId);
			for (Module module : process.getModules()) {
				if (module instanceof HttpClientLoginModule) {
					setAttr("module", module.getName());
					break;
				}
			}
		}
		Map<String, Object> params = new HashMap<String, Object>();
		Enumeration<String> paraNames = getParaNames();
		while (paraNames.hasMoreElements()) {
			String paraName = paraNames.nextElement();
			params.put(paraName, getPara(paraName));
		}
		setAttr("params", Json.toString(params));
		render("/module/login.html");
	}
	
}
