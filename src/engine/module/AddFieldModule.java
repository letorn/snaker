package engine.module;

import java.util.List;
import java.util.Map;

import engine.ModuleData;

public class AddFieldModule extends Module {

	private List<Map<String, Object>> tableFields;
	
	@Override
	public ModuleData execute(ModuleData inputs) {
		for (int i = 0; i < inputs.getRows().size(); i++) {
			Map<String, Object> map =inputs.getRows().get(i);
			for (int j = 0; j < tableFields.size(); j++) {
				if( tableFields.get(j).get("type").equals("variable")){
					//变量
					map.put((String) tableFields.get(j).get("key") , inputs.getRows().get(i).get(tableFields.get(j).get("value")));
				}else if(tableFields.get(j).get("type").equals("constant")){
					//常量
					map.put((String) tableFields.get(j).get("key") , tableFields.get(j).get("value"));
				}else{
					//脚本
					
				}
			}
		}
		return inputs;
	}

}
