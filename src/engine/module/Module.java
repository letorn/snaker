package engine.module;

import java.util.List;

import engine.ModuleContext;
import engine.ModuleData;

public abstract class Module {

	private ModuleContext context;
	private List<Module> prevModules;
	private List<Module> nextModules;
	private ModuleData outputs;

	public abstract ModuleData execute(ModuleData inputs);

	public void run(ModuleData inputs) {
		ModuleData outputs = execute(inputs);
		for (Module module : nextModules) {
			module.run(outputs);
		}
	}

	public ModuleContext getContext() {
		return context;
	}

	public void setContext(ModuleContext context) {
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

}
