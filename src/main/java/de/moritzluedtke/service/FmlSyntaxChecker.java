package de.moritzluedtke.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FmlSyntaxChecker {
	
	private static final Logger log = LogManager.getLogger();
	private static FmlSyntaxChecker instance = new FmlSyntaxChecker();
	
	public static final String FML_KEYWORD = "+";
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
		String fmlAsString = buildStringFromList(fmlAsList);
		
		return checkForLegalChars(fmlAsString)
				&& checkForNoDuplicateNames(fmlAsList)
				&& checkForNoKeywordInFolderName(fmlAsList)
				&& checkForNoForwardJump(fmlAsList);
	}
	
	/**
	 * @param fmlAsList
	 * @return
	 */
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
	
	private boolean checkForNoDuplicateNames(List<String> fmlAsList) {
		for (int i = 0; i < fmlAsList.size(); i++) {
			String currentLine = fmlAsList.get(i).toLowerCase();
			int currentLevel = getTreeLevelFromFmlLine(currentLine);
			
			for (int j = i + 1; j < fmlAsList.size(); j++) {
				String nextLine = fmlAsList.get(j).toLowerCase();
				int nextLevel = getTreeLevelFromFmlLine(nextLine);
				
				if (currentLine.equals(nextLine)) {
					log.error("Found duplicate folder names in the same tree level");
					return false;
				} else if (nextLevel < currentLevel) {
					break; //there can be duplicate folder names if they are not in the same scope/level under one folder.
					//if the next level is a higher tree level, under the next (higher) folder there can be the same name at the same tree level
				}
			}
		}
		
		return true;
	}
	
	private int getTreeLevelFromFmlLine(String fmlLine) {
		return getIndexOfLastKeyword(fmlLine) + 1;
	}
	
	private int getIndexOfLastKeyword(String line) {
		int index;
		
		for (index = 0; index < line.length(); index++) {
			char currentChar = line.charAt(index);
			char nextChar = line.charAt(index + 1);
			
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
