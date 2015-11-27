package test;

import java.util.List;

public class Major {
	private String id;
	private String name;
	private String code;
	private String parent;
	private List<Major> children;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Major> getChildren() {
		return children;
	}
	public void setChildren(List<Major> children) {
		this.children = children;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	
}
