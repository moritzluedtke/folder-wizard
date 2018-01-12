package de.moritzluedtke.service;

import de.moritzluedtke.service.model.FolderTreeItem;

public class FolderWriter {
	
	private static FolderWriter instance = new FolderWriter();
	
	private FolderWriter() {
	
	}
	
	public static FolderWriter getInstance() {
		return instance;
	}
	
	public boolean writeFoldersToDisk(FolderTreeItem rootItem) {
		
		return false;
	}
}
