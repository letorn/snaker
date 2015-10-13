package engine.module;

import static util.Validator.blank;
import static util.Validator.notBlank;

import java.util.List;

import util.Json;

import com.jfinal.kit.JsonKit;

import engine.ModuleData;
import engine.WorkflowContext;
import engine.model.WfRecord;

/*
 * 流程模型
 */
public abstract class Module {

	protected WorkflowContext context;// 模型上下文
	private List<Module> prevModules;// 前一步的流程模型
	private List<Module> nextModules;// 后一步的流程模型
	private ModuleData inputs;// 输入的数据
	private ModuleData outputs;// 输出的数据
	
	private boolean autoRun = true;// 自动执行
	
	private String mtype;// 模型类型
	private String name;// 模型名称
	private String controller;// 模型界面
	private String service;// 模型服务
	private String view;// 模型视图
	
	/**
	 * 模型执行方法体
	 * @param inputs 输入的数据
	 * @return 输出的数据
	 */
	public abstract ModuleData execute(ModuleData inputs);

	/**
	 * 运行模型
	 */
	public void run() {
		if (blank(inputs))
			inputs = new ModuleData();
		ModuleData outputs = execute(inputs);
		if (notBlank(view)) {
			new WfRecord().set("process_id", context.getProcessId())
							.set("instance_id", context.getInstanceId())
							.set("module", name)
							.set("headers", JsonKit.toJson(outputs.getHeaders()))
							.set("rows", JsonKit.toJson(outputs.getRows()))
							.save();
		}
		for (Module module : nextModules) {
			module.autoRun(outputs);
		}
	}
	
	/**
	 * 自动运行模型
	 * @param inputs 输入的数据
	 */
	private void autoRun(ModuleData inputs) {
		this.inputs = inputs;
		if (autoRun) {
			ModuleData outputs = execute(inputs);
			if (notBlank(view)) {
				new WfRecord().set("process_id", context.getProcessId())
								.set("instance_id", context.getInstanceId())
								.set("module", name)
								.set("headers", Json.toString(outputs.getHeaders()))
								.set("rows", Json.toString(outputs.getRows()))
								.save();
			}
			for (Module module : nextModules) {
				module.autoRun(outputs);
			}
		}
	}

	public WorkflowContext getContext() {
		return context;
	}

	public void setContext(WorkflowContext context) {
		this.context = context;
	}

	public List<Module> getPrevModules() {
		return prevModules;
	}

	public void setPrevModules(List<Module> prevModules) {
		this.prevModules = prevModules;
	}

	public List<Module> getNextModules() {
		return nextModules;
	}

	public void setNextModules(List<Module> nextModules) {
		this.nextModules = nextModules;
	}

	public ModuleData getOutputs() {
		return outputs;
	}

	public void setOutputs(ModuleData outputs) {
		this.outputs = outputs;
	}

	public String getMtype() {
		return mtype;
	}

	public void setMtype(String mtype) {
		this.mtype = mtype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getController() {
		return controller;
	}

	public void setController(String controller) {
		this.controller = controller;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

}