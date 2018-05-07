package de.moritzluedtke.folderwizard.service.model;

import javafx.scene.control.TreeItem;


public class CustomTreeItem<T> extends TreeItem {
	
	public CustomTreeItem(Object value) {
		super(value);
	}
	
	
	public void expandTree() {
		expandTree(this);
	}
	
	
	private void expandTree(TreeItem<?> item) {
		if (item != null && !item.isLeaf()) {
			item.setExpanded(true);
			
			for (TreeItem<?> child : item.getChildren()) {
				expandTree(child);
			}
		}
	}

}
