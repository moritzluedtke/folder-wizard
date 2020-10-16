package de.moritzluedtke.folderwizard.service;

import de.moritzluedtke.folderwizard.service.model.FmlPreset;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FmlPresetProvider {
	
	private static final FmlPresetProvider instance = new FmlPresetProvider();

	private static final String DOT = ".";
	private static final String FML_FILE_ENDING = ".fml";
	private static final String PRESET_FOLDER = "/_presets";
	
	private List<FmlPreset> presetList = new ArrayList<>();
	
	private FmlPresetProvider() {
	}

	public static FmlPresetProvider getInstance() {
		return instance;
	}
	
	public List<FmlPreset> readAllPresetsFromDisk(String pathToInstallationFolder) {
		File presetFolder = new File(pathToInstallationFolder + PRESET_FOLDER);
		
		//TODO: Weniger ifs, isDirectory() nötig?
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
//				log.info("No presets found!");
			}
		} else {
//			log.error("This is not a folder (Installation Folder)!");
		}
		
		return presetList;
	}
}
