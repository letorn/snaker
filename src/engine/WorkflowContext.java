package engine;


/*
 * 工作流上下文
 */
public class WorkflowContext {

	private Long processId;// 流程主键
	private String processName;// 流程名称
	private String processContent;// 流程内容

	private Long instanceId;// 实例主键
	private String instanceParam;// 实例参数

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

	public String getProcessContent() {
		return processContent;
	}

	public void setProcessContent(String processContent) {
		this.processContent = processContent;
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public String getInstanceParam() {
		return instanceParam;
	}

	public void setInstanceParam(String instanceParam) {
		this.instanceParam = instanceParam;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

}
