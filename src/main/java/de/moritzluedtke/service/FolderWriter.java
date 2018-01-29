package de.moritzluedtke.service;

import de.moritzluedtke.service.model.FolderTreeItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Class which writes a folder structure to a hard drive. <p>
 * Uses the singelton pattern as there is no need for more than one FolderWriter at any given time during runtime.
 */
public class FolderWriter {
	
	private static final Logger log = LogManager.getLogger();
	private static FolderWriter instance = new FolderWriter();
	
	private FolderWriter() {
	
	}
	
	/**
	 * Returns the only/one existing object.
	 *
	 * @return the singelton object
	 */
	public static FolderWriter getInstance() {
		return instance;
	}
	
	/**
	 * Writes the {@link FolderTreeItem} to disk including all of its children.
	 *
	 * @param rootItem current root folder item
	 * @return true if no error occured
	 */
	public boolean writeFoldersToDisk(FolderTreeItem rootItem) {
		for (FolderTreeItem folderItem : rootItem.getChildren()) {
			File folder = new File(folderItem.getPath());
			
			if (folder.exists()) {
				log.error("Folder \"" + folder.getName() + "\" already exists.");
				return false;
			} else {
				// Writes the folder to disk an uses the return value to determine if the creation was successful
				if (!folder.mkdir()) {
					log.error("Can not create folder: " + folderItem.getName() + "@ " + folderItem.getPath());
					return false;
				}
				
				// Calls itself if the folder has children, but with the newly created folder as the new root.
				if (folderItem.hasChildren()) {
					writeFoldersToDisk(folderItem);
				}
			}
		}
		
		return true;
	}
}
