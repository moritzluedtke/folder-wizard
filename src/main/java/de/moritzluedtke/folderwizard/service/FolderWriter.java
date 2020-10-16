package de.moritzluedtke.folderwizard.service;

import de.moritzluedtke.folderwizard.service.model.FolderTreeItem;

import java.io.File;

public class FolderWriter {
	
	private static FolderWriter instance = new FolderWriter();
	
	private FolderWriter() {
	
	}
	
	public static FolderWriter getInstance() {
		return instance;
	}
	
	public boolean writeFoldersToDisk(FolderTreeItem rootItem) {
		for (FolderTreeItem folderItem : rootItem.getChildren()) {
			File folder = new File(folderItem.getPath());
			
			if (folder.exists()) {
//				log.error("Folder \"" + folder.getName() + "\" already exists.");
				return false;
			} else {
				if (!folder.mkdir()) {
//					log.error("Can not create folder: " + folderItem.getName() + "@ " + folderItem.getPath());
					return false;
				}
				
				if (folderItem.hasChildren()) {
					writeFoldersToDisk(folderItem);
				}
			}
		}
		
		return true;
	}
}
