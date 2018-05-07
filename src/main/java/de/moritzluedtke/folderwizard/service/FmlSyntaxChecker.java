package de.moritzluedtke.folderwizard.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FmlSyntaxChecker {
	
	private static final Logger log = LogManager.getLogger();
	private static FmlSyntaxChecker instance = new FmlSyntaxChecker();
	
	private static final String FML_KEYWORD = "+";
	private List<Character> legalSpecialCharacters = new ArrayList<>();
	
	private FmlSyntaxChecker() {
		legalSpecialCharacters.add('_');
		legalSpecialCharacters.add('-');
		legalSpecialCharacters.add(' ');
		legalSpecialCharacters.add('+');
	}
	
	public static FmlSyntaxChecker getInstance() {
		return instance;
	}
	
	public boolean isFmlSyntaxValid(List<String> fmlAsList) {
		String fmlAsString = buildStringFromList(fmlAsList);
		
		return checksIfAllCharsAreLegal(fmlAsString)
				&& checkForNoDuplicateNames(fmlAsList)
				&& checkForNoKeywordInFolderName(fmlAsList)
				&& checkForNoForwardJump(fmlAsList);
	}
	
	private boolean checksIfAllCharsAreLegal(String fml) {
		for (Character currentCharFromFml : fml.toCharArray()) {
			if (!legalSpecialCharacters.contains(currentCharFromFml)
					&& !Character.isLetter(currentCharFromFml)
					&& !Character.isDigit(currentCharFromFml)) {
				log.error("FML contains illegal character: " + currentCharFromFml);
				return false;
			}
		}
		
		return true;
	}
	
	private boolean checkForNoDuplicateNames(List<String> fmlAsList) {
		// There can be duplicate folder names if they are not in the same scope/level under one folder.
		
		for (int i = 0; i < fmlAsList.size(); i++) {
			String currentLine = fmlAsList.get(i).toLowerCase();
			int currentLevel = getTreeLevelFromFmlLine(currentLine);
			
			for (int j = i + 1; j < fmlAsList.size(); j++) {
				String nextLine = fmlAsList.get(j).toLowerCase();
				int nextLevel = getTreeLevelFromFmlLine(nextLine);
				
				// If the next level is higher, than nothing happens
				
				// If the next line is the same as the current one, an error occurs
				if (currentLine.equals(nextLine)) {
					log.error("Found duplicate folder names in the same tree level");
					return false;
					
					// If the next level is higher in the tree (lower number), then the loop will break. After this
					// there can be the same folder name because it will have a different parent than the current one.
				} else if (nextLevel < currentLevel) {
					break;
				}
			}
		}
		
		return true;
	}
	
	private boolean checkForNoKeywordInFolderName(List<String> fmlAsList) {
		for (String line : fmlAsList) {
			int lastIndexOfKeyword = getIndexOfLastKeyword(line);
			
			if (lastIndexOfKeyword != line.lastIndexOf(FML_KEYWORD)) {
				log.error("Found keyword in folder name in count: " + line);
				return false;
			}
		}
		
		return true;
	}
	
	private boolean checkForNoForwardJump(List<String> fmlAsList) {
		int numberOfKeywords;
		int oldNumberOfKeywords = 0;
		
		for (String line : fmlAsList) {
			numberOfKeywords = line.lastIndexOf("+") + 1;
			
			if (numberOfKeywords > oldNumberOfKeywords + 1) {
				log.error("Illegal forword jump in line: " + line);
				return false;
			}
			
			oldNumberOfKeywords = numberOfKeywords;
		}
		
		return true;
	}
	
	private int getTreeLevelFromFmlLine(String fmlLine) {
		return getIndexOfLastKeyword(fmlLine) + 1;
	}
	
	private int getIndexOfLastKeyword(String fmlLine) {
		int index;
		
		for (index = 0; index < fmlLine.length(); index++) {
			char currentChar = fmlLine.charAt(index);
			char nextChar = fmlLine.charAt(index + 1);
			
			if (currentChar == '+' && nextChar != '+') {
				break;
			}
		}
		
		return index;
	}
	
	private String buildStringFromList(List<String> list) {
		StringBuilder sb = new StringBuilder();
		
		for (String string : list) {
			sb.append(string);
		}
		
		return sb.toString();
	}
	
}
