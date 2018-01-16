package de.moritzluedtke.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class FmlSyntaxChecker {
	
	private static final Logger log = LogManager.getLogger();
	private static FmlSyntaxChecker instance = new FmlSyntaxChecker();
	
	private String[] forbiddenCharacters = {"*", "\"", "/", "\\", ":", "|", "<", ">"};
	
	
	private FmlSyntaxChecker() {
	
	}
	
	public static FmlSyntaxChecker getInstance() {
		return instance;
	}
	
	public boolean isFmlSyntaxValid(List<String> fml) {
		StringBuilder sb = new StringBuilder();
		
		for (String string : fml) {
			sb.append(string);
		}
		
		String fmlAsString = sb.toString();
		
		return checkForLegalSymbols(fmlAsString)
				&& checkForForwardJump(fmlAsString)
				&& checkIfAnyKeywordIsPresent(fmlAsString)
				&& checkForKeywordAsName(fmlAsString);
	}
	
	private boolean checkIfAnyKeywordIsPresent(String fmlAsString) {
		return false;
	}
	
	private boolean checkForKeywordAsName(String fml) {
		return true;
	}
	
	private boolean checkForForwardJump(String fml) {
		return true;
	}
	
	private boolean checkForLegalSymbols(String fml) {
		boolean containsForbiddenSymbol = false;
		
		for (String character : forbiddenCharacters) {
		    if (fml.contains(character)) {
		        containsForbiddenSymbol = true;
		        
		        log.error("FML file contains forbidden symbol: " + character);
		    }
		}
		
		return containsForbiddenSymbol;
	}
}
