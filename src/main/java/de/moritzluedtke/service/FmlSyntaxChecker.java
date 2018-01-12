package de.moritzluedtke.service;

import java.util.List;

public class FmlSyntaxChecker {
	
	private static FmlSyntaxChecker instance = new FmlSyntaxChecker();
	
	private FmlSyntaxChecker() {
	
	}
	
	public static FmlSyntaxChecker getInstance() {
		return instance;
	}
	
	public boolean isFmlValid(List<String> fml) {
		
		return true;
	}
}
