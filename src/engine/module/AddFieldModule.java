package engine.module;

import java.util.List;
import java.util.Map;

import engine.ModuleData;

@SuppressWarnings("serial")
public class AddFieldModule extends Module {

	private List<Map<String, Object>> fieldTable;

	@Override
	public ModuleData execute(ModuleData inputs) {
		for (int i = 0; i < inputs.getRows().size(); i++) {
			Map<String, Object> map =inputs.getRows().get(i);
			for (int j = 0; j < fieldTable.size(); j++) {
				if( fieldTable.get(j).get("type").equals("variable")){
					//变量
					map.put((String) fieldTable.get(j).get("name") , inputs.getRows().get(i).get(fieldTable.get(j).get("value")));
				}else if(fieldTable.get(j).get("type").equals("constant")){
					//常量
					map.put((String) fieldTable.get(j).get("name") , fieldTable.get(j).get("value"));
				}else{
					//脚本
					
				}
			}
		}
		return inputs;
	}

}
