package engine.model;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class DbEnterprise extends Model<DbEnterprise> {
	public static final DbEnterprise dao = new DbEnterprise();
}
