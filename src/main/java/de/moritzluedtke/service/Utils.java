package de.moritzluedtke.service;

import de.moritzluedtke.service.model.FolderTreeItem;
import javafx.scene.control.TreeItem;

import java.io.File;

public class Utils {
	
	private static Utils instance = new Utils();
	
	private Utils() {
	
	}
	
	public static Utils getInstance() {
		return instance;
	}
	
	public boolean isUserInputAFmlFile(String userInputPath) {
		File fmlFile = new File(userInputPath);
		return fmlFile.isFile() && fmlFile.getPath().endsWith(".fml");
	}
	
	/**
	 * Validates that the path typed in/selected by the user is a directory.
	 *
	 * @param userInputPath the text that is currently stored in the text field
	 * @return returns true if the path points to a directory and not ends with a backslash. Important for file parsing.
	 */
	public boolean isUserInputADirectory(String userInputPath) {
		return new File(userInputPath).isDirectory() && !userInputPath.endsWith("\\");
	}
	
	public TreeItem<String> putFolderTreeIntoTreeItems(TreeItem<String> currentTreeRoot,
														FolderTreeItem currentFolderRoot) {
		if (currentFolderRoot.hasChildren()) {
			for (FolderTreeItem folderItem : currentFolderRoot.getChildren()) {
				TreeItem<String> newTreeItem = new TreeItem<>(folderItem.getName());
				currentTreeRoot.getChildren().add(newTreeItem);
				
				if (folderItem.hasChildren()) {
					putFolderTreeIntoTreeItems(newTreeItem, folderItem);
				}
			}
		}
		
		return currentTreeRoot;
	}
}
