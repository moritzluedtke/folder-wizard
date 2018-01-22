package de.moritzluedtke.service.model;

import javafx.scene.control.TreeItem;

/**
 * Custom tree item which inherits from {@link TreeItem}.
 * Extends the original class by adding an {@link #expandTree()} method.
 *
 * @param <T>
 */
public class CustomTreeItem<T> extends TreeItem {
	
	public CustomTreeItem(Object value) {
		super(value);
	}
	
	/**
	 * Calls {@link #expandTree(TreeItem)}
	 */
	public void expandTree() {
		expandTree(this);
	}
	
	/**
	 * Sets {@link #setExpanded(boolean)} to true for itself and all of its children.
	 * @param item the tree item, which should be expanded
	 */
	private void expandTree(TreeItem<?> item) {
		if (item != null && !item.isLeaf()) {
			item.setExpanded(true);
			
			for (TreeItem<?> child : item.getChildren()) {
				expandTree(child);
			}
		}
	}

}
