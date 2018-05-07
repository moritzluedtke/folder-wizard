package de.moritzluedtke.folderwizard.service;

import de.moritzluedtke.folderwizard.service.model.CustomTreeItem;
import de.moritzluedtke.folderwizard.service.model.FolderTreeItem;

import java.io.File;


public class Utils {
	
	private static Utils instance = new Utils();
	
	
	private Utils() {
	
	}
	
	
	public static Utils getInstance() {
		return instance;
	}
	
	
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
	
	
	public boolean isUserInputAFmlFile(String userInputPath) {
		File fmlFile = new File(userInputPath);
		return fmlFile.isFile() && fmlFile.getPath().endsWith(".fml");
	}
	
	
	public boolean isUserInputADirectory(String userInputPath) {
		return new File(userInputPath).isDirectory() && !userInputPath.endsWith("\\");
	}
	
	
	public String getDirectoryFromFilePath(String filePath) {
		int lastSlash = filePath.lastIndexOf("\\") + 1;
		
		return filePath.substring(0, lastSlash);
	}
}
