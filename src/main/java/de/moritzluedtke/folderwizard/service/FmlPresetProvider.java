package de.moritzluedtke.folderwizard.service;

import java.util.Map;

public class FmlPresetProvider {
	
	private static FmlPresetProvider instance = new FmlPresetProvider();
	
	private Map<String, String> presetList;
	
	
	private FmlPresetProvider() {
	}
	
	public static FmlPresetProvider getInstance() {
		return instance;
	}
}
