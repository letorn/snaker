package engine.module;

import static util.Validator.blank;

import java.util.List;
import java.util.Map;

import com.jfinal.log.Logger;

import engine.ModuleData;
import engine.Workflow;

/*
 * 流程模型
 */
public abstract class Module implements Runnable {

	protected static final Logger logger = Logger.getLogger(Module.class);
	
	protected Workflow workflow;// 工作流
	protected List<Module> prevModules;// 前一步的流程模型
	protected List<Module> nextModules;// 后一步的流程模型
	protected ModuleData inputs;// 输入的数据
	protected ModuleData outputs;// 输出的数据
	
	protected String mtype;// 模型类型
	protected String name;// 模型名称
	protected String controller;// 模型界面
	protected boolean doRecord = false;
	protected ModuleData records = new ModuleData();
	protected String recordView;// 模型视图
	protected List<Map<String, Object>> params;

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
		run(inputs);
	}
	
	/**
	 * 运行模型，后台线程
	 */
	public void start() {
		new Thread(this).start();
	}
	
	/**
	 * 停止运行
	 */
	public void stop() {}
	
	/**
	 * 组件活动状态
	 * @return 是否正在活动
	 */
	public boolean isAlive() {
		return false;
	}
	
	/**
	 * 运行模型
	 */
	public void run(ModuleData inputs) {
		outputs = execute(inputs);
		if (doRecord) {
			records.addAll(inputs.getRows());
			/*new WfRecord().set("process_id", workflow.getProcessId())
							.set("instance_id", workflow.getInstanceId())
							.set("module", name)
							.set("headers", Json.toString(outputs.getHeaders()))
							.set("rows", Json.toString(outputs.getRows()))
							.set("create_date", new Date())
							.save();*/
		}
		for (Module module : nextModules) {
			module.setInputs(outputs);
			if (workflow.isDaemon()) {
				module.start();
			} else {
				module.run();
			}
		}
	}
	
	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
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

	public ModuleData getInputs() {
		return inputs;
	}

	public void setInputs(ModuleData inputs) {
		this.inputs = inputs;
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

	public boolean isDoRecord() {
		return doRecord;
	}

	public void setDoRecord(boolean doRecord) {
		this.doRecord = doRecord;
	}

	public ModuleData getRecords() {
		return records;
	}

	public String getRecordView() {
		return recordView;
	}

	public void setRecordView(String recordView) {
		this.recordView = recordView;
	}

	public List<Map<String, Object>> getParams() {
		return params;
	}

	public void setParams(List<Map<String, Object>> params) {
		this.params = params;
	}

}
