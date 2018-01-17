package de.moritzluedtke.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
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
				&& checkForNoDuplicateNames(fmlAsList)
				&& checkIfAnyKeywordIsPresent(fmlAsList)
				&& checkForForwardJump(fmlAsList);
	}
	
	private boolean checkForForwardJump(List<String> fmlAsList) {
		
		
		return true;
	}
	
	private boolean checkForLegalChars(String fml) {
		for (Character currentCharFromFml : fml.toCharArray()) {
			if (!validSpecialCharacters.contains(currentCharFromFml)
					&& !Character.isLetter(currentCharFromFml)
					&& !Character.isDigit(currentCharFromFml)) {
				log.error("FML contains illegal character: " + currentCharFromFml);
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
		
		log.error("FML contains no keyword at the start of a line");
		return false;
	}
	
	private boolean checkForNoDuplicateNames(List<String> fmlAsList) {
		HashMap<String, Integer> numberOfDuplicateFolderNames = aggregateDuplicateList(fmlAsList);
		
		for (String currentFolderName : numberOfDuplicateFolderNames.keySet()) {
			if (numberOfDuplicateFolderNames.get(currentFolderName) > 1) {
				log.error("Found a duplicate Foldername: " + currentFolderName);
				return false;
			}
		}
		
		return true;
	}
	
	private HashMap<String, Integer> aggregateDuplicateList(List<String> fml) {
		HashMap<String, Integer> map = new HashMap<>();
		
		for (String line : fml) {
			if (map.containsKey(line)) {
				int oldValue = map.get(line);
				map.replace(line, oldValue + 1);
			} else {
				map.put(line, 1);
			}
		}
		
		return map;
	}
	
}
