package de.moritzluedtke.service.model;

import java.util.ArrayList;

public class Folder {
	
	private String name;
	private String path;
	private ArrayList<Folder> children;
	
	public Folder(String name, String path) {
		this.name = name;
		this.path = path;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public ArrayList<Folder> getChildren() {
		return children;
	}
	
	public void setChildren(ArrayList<Folder> children) {
		this.children = children;
	}
	
	public void addChildren(ArrayList<Folder> children) {
		this.children.addAll(children);
	}
}
