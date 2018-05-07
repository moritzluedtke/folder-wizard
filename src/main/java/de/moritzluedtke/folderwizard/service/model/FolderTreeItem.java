package de.moritzluedtke.folderwizard.service.model;

import java.util.ArrayList;

public class FolderTreeItem {
	
	private String name;
	private String path;
	private FolderTreeItem parent;
	private ArrayList<FolderTreeItem> children = null;
	
	
	public FolderTreeItem(String name, String path, FolderTreeItem parent) {
		this.name = name;
		this.path = path;
		this.parent = parent;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public FolderTreeItem getParent() {
		return parent;
	}
	
	public ArrayList<FolderTreeItem> getChildren() {
		return children;
	}
	
	public void addChildren(FolderTreeItem children) {
		createChildrenListIfChildrenNull();
		
		this.children.add(children);
	}
	
	public boolean hasChildren() {
		return children != null;
	}
	
	private void createChildrenListIfChildrenNull() {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
	}
}
