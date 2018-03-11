package de.moritzluedtke.filewizard.service.model;

import java.util.ArrayList;

/**
 * Custom tree item class for representing a folder structure.
 */
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
	
	/**
	 * Adds the given children to {@link #children}. If {@link #children} is null then a new list will be instantiated.
	 *
	 * @param children the children which should be added to the list
	 */
	public void addChildren(FolderTreeItem children) {
		createChildrenListIfChildrenNull();
		
		this.children.add(children);
	}
	
	/**
	 * Checks if there are any children present.
	 *
	 * @return true if {@link #children} is not null
	 */
	public boolean hasChildren() {
		return children != null;
	}
	
	/**
	 * Instantiates a new ArrayList if {@link #children} is null (no children present).
	 */
	private void createChildrenListIfChildrenNull() {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
	}
}
