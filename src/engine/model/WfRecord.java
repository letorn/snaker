package engine.model;

import com.jfinal.plugin.activerecord.Model;

/*
 * 流程实例
 * id 实例主键
 * process_id 流程主键
 * param 实例参数
 * update_date 实例更新时间
 * create_date 实例创建时间
 */
@SuppressWarnings("serial")
public class WfRecord extends Model<WfRecord> {
	public static final WfRecord dao = new WfRecord();
}
