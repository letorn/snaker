package engine;

import java.util.Map;

/*
 * 工作流上下文
 */
public class WorkflowContext {

	private Long processId;// 工作流程主键
	private String processName;// 工作流程名称
	private Long instanceId;// 流程实例主键
	private Map<String, Object> param;// 实例参数
	private Workflow workflow;// 工作流

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public Map<String, Object> getParam() {
		return param;
	}

	public void setParam(Map<String, Object> param) {
		this.param = param;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

}
