package engine.module;

import engine.ModuleData;

public class StopModule extends Module {

	public ModuleData execute(ModuleData inputs) {
		System.out.println("stop");
		return null;
	}

}
