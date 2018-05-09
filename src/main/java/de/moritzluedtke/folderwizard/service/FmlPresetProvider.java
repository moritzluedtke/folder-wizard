package de.moritzluedtke.folderwizard.service;

import de.moritzluedtke.folderwizard.service.model.FmlPreset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.Optional.of;

public class FmlPresetProvider {
	
	private static FmlPresetProvider instance = new FmlPresetProvider();
	private static final Logger log = LogManager.getLogger();
	
	private static final String DOT = ".";
	private static final String FML_FILE_ENDING = ".fml";
	private static final String PRESET_FOLDER = "/_presets";
	
	private List<FmlPreset> presetList = new ArrayList<>();
	
	
	private FmlPresetProvider() {
	}
	
	public List<FmlPreset> readAllPresetsFromDisk(String pathToInstallationFolder) {
		File presetFolder = new File(pathToInstallationFolder + PRESET_FOLDER);
		
		if (presetFolder.isDirectory()) {
			Optional<File[]> allFilesList = Optional.ofNullable(presetFolder.listFiles());
			
			if (allFilesList.isPresent()) {
				List<FmlPreset> presetList = new ArrayList<>();
				List<File> allFilesArray = Arrays.asList(allFilesList.get());
				
				for (File file : allFilesArray) {
					String fileName = file.getName();
					String filePath = file.getAbsolutePath();
					
					if (fileName.endsWith(FML_FILE_ENDING)) {
						String shortFileName = fileName.substring(0, fileName.lastIndexOf(DOT));
						
						presetList.add(new FmlPreset()
								.withName(shortFileName)
								.andPathToFml(filePath));
					}
				}
				
				this.presetList = presetList;
				
			} else {
				log.info("No presets found!");
			}
		} else {
			log.error("This is not a folder (Installation Folder)!");
		}
		
		return presetList;
	}
	
	public static FmlPresetProvider getInstance() {
		return instance;
	}
	
	public List<FmlPreset> getPresetList() {
		return presetList;
	}
}
