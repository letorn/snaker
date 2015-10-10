package engine;

import java.util.List;

import engine.model.WfProcess;

public class SnakerEngine {

	public WfProcess getWfProcess(Long processId) {
		return WfProcess.dao.findById(processId);
	}
	
	public List<WfProcess> findWfProcess(String name) {
		return WfProcess.dao.find("select * from sys_process where name like ?", name);
	}
	
	public void addWfProcess(String content) {
		Workflow workflow = Workflow.create(content);
		if (workflow != null)
			WfProcess.dao.set("name", workflow.getName()).set("content", content).save();
	}
	
	public void updateWfProcess(Long id, String content) {
		Workflow workflow = Workflow.create(content);
		if (id !=null && workflow != null)
			WfProcess.dao.set("id", id).set("name", workflow.getName()).set("content", content).update();
	}
	
}
