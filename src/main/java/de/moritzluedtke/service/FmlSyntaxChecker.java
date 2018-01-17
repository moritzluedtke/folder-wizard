package de.moritzluedtke.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FmlSyntaxChecker {
	
	private static final Logger log = LogManager.getLogger();
	private static FmlSyntaxChecker instance = new FmlSyntaxChecker();
	
	private List<Character> validSpecialCharacters = new ArrayList<>();
	
	
	private FmlSyntaxChecker() {
		validSpecialCharacters.add('_');
		validSpecialCharacters.add('-');
		validSpecialCharacters.add(' ');
		validSpecialCharacters.add('+');
	}
	
	public static FmlSyntaxChecker getInstance() {
		return instance;
	}
	
	public boolean isFmlSyntaxValid(List<String> fmlAsList) {
		StringBuilder sb = new StringBuilder();
		
		for (String string : fmlAsList) {
			sb.append(string);
		}
		
		String fmlAsString = sb.toString();
		
		return checkForLegalChars(fmlAsString)
//				&& checkForForwardJump(fmlAsString)
				&& checkIfAnyKeywordIsPresent(fmlAsList)
				&& checkForKeywordAsName(fmlAsString);
	}
	
	private boolean checkForLegalChars(String fml) {
		for (Character currentCharFromFml : fml.toCharArray()) {
			if (!validSpecialCharacters.contains(currentCharFromFml)
					&& !Character.isLetter(currentCharFromFml)
					&& !Character.isDigit(currentCharFromFml)) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean checkIfAnyKeywordIsPresent(List<String> fmlAsList) {
		for (String line : fmlAsList) {
		    if (line.startsWith("+")) {
				return true;
		    }
		}
		
		return false;
	}
	
	private boolean checkForKeywordAsName(String fml) {
		return true;
	}

//	private boolean checkForForwardJump(String fml) {
//		return true;
//	}

}
