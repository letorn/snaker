package test;

import java.util.List;

public class Area {
	private String id;
	private String text;
	private List<Area> children;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<Area> getChildren() {
		return children;
	}
	public void setChildren(List<Area> children) {
		this.children = children;
	}
	
}
