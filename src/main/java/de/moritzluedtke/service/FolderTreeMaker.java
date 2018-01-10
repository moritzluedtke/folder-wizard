package de.moritzluedtke.service;

public class FolderTreeMaker {
	
	private static FolderTreeMaker instance = new FolderTreeMaker();
	
	private FolderTreeMaker() {
	
	}
	
	public static FolderTreeMaker getInstance() {
		return instance;
	}
}
