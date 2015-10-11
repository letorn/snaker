package engine.model;

import com.jfinal.plugin.activerecord.Model;

/*
 * 工作流程
 * id 流程主键
 * name 流程名称
 * content 流程内容
 * update_date 流程更新时间
 * create_date 流程创建时间
 */
@SuppressWarnings("serial")
public class WfProcess extends Model<WfProcess> {
	public static final WfProcess dao = new WfProcess();
}
