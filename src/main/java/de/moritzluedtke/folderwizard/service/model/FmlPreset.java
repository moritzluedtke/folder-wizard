package de.moritzluedtke.folderwizard.service.model;

public class FmlPreset {
	
	private String name;
	private String pathToFml;
	
	@Override
	public String toString() {
		return name.length() > 34 ? this.name.substring(0, 30) + " . . ." : name;
	}
	
	public FmlPreset withName(String name) {
		this.name = name;
		return this;
	}
	
	public FmlPreset andPathToFml(String path) {
		this.pathToFml = path;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPathToFml() {
		return pathToFml;
	}
	
}
