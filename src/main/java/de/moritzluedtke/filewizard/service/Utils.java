package de.moritzluedtke.filewizard.service;

import de.moritzluedtke.filewizard.service.model.CustomTreeItem;
import de.moritzluedtke.filewizard.service.model.FolderTreeItem;

import java.io.File;

/**
 * Multi purpose class. Validates files or directories, converts folder trees from one class to
 * another and gets the directory from a path to a file.
 */
public class Utils {
	
	private static Utils instance = new Utils();
	
	/**
	 * Uses the singleton pattern as there is no need for more than one Utils class at any given time during runtime.
	 */
	private Utils() {
	
	}
	
	/**
	 * Returns the only/one existing object.
	 *
	 * @return the singelton object
	 */
	public static Utils getInstance() {
		return instance;
	}
	
	/**
	 * Converts the folder tree from {@link FolderTreeItem} to {@link CustomTreeItem}.
	 *
	 * @param currentTreeRoot   the current root {@link CustomTreeItem} item.
	 * @param currentFolderRoot the current root {@link FolderTreeItem} item.
	 * @return the converted {@link CustomTreeItem}
	 */
	public CustomTreeItem<String> convertFolderTreeIntoTreeItem(CustomTreeItem<String> currentTreeRoot,
																FolderTreeItem currentFolderRoot) {
		for (FolderTreeItem folderItem : currentFolderRoot.getChildren()) {
			CustomTreeItem<String> newTreeItem = new CustomTreeItem<>(folderItem.getName());
			currentTreeRoot.getChildren().add(newTreeItem);
			
			if (folderItem.hasChildren()) {
				convertFolderTreeIntoTreeItem(newTreeItem, folderItem);
			}
		}
		
		return currentTreeRoot;
	}
	
	/**
	 * Validates that the path typed in/selected by the user points to a ".fml" file.
	 *
	 * @param userInputPath the path pointing to a ".fml" file
	 * @return true if the path points to a file and the file ends with ".fml"
	 */
	public boolean isUserInputAFmlFile(String userInputPath) {
		File fmlFile = new File(userInputPath);
		return fmlFile.isFile() && fmlFile.getPath().endsWith(".fml");
	}
	
	/**
	 * Validates that the path typed in/selected by the user points to a directory.
	 *
	 * @param userInputPath the path pointing to a directory
	 * @return true if the path points to a directory and does not end with a backslash. Important for file parsing.
	 */
	public boolean isUserInputADirectory(String userInputPath) {
		return new File(userInputPath).isDirectory() && !userInputPath.endsWith("\\");
	}
	
	/**
	 * Removes the file from a path only leaving the path to the directory the file is in.
	 *
	 * @param filePath the path with a file at the end
	 * @return the path without a file at the end (e.g. no "Test.fml")
	 */
	public String getDirectoryFromFilePath(String filePath) {
		int lastSlash = filePath.lastIndexOf("\\") + 1;
		
		return filePath.substring(0, lastSlash);
	}
}
