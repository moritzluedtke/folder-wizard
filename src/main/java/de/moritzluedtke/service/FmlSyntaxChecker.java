package de.moritzluedtke.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which checks that there is no syntax error in a given FML file.<p>
 * Uses the singelton pattern as there is no need for more than one FolderWriter at any given time during runtime.
 */
public class FmlSyntaxChecker {
	
	private static final Logger log = LogManager.getLogger();
	private static FmlSyntaxChecker instance = new FmlSyntaxChecker();
	
	public static final String FML_KEYWORD = "+";
	private List<Character> validSpecialCharacters = new ArrayList<>();
	
	
	private FmlSyntaxChecker() {
		// Loads all valid Special Characters into the corresponding ArrayList
		validSpecialCharacters.add('_');
		validSpecialCharacters.add('-');
		validSpecialCharacters.add(' ');
		validSpecialCharacters.add('+');
	}
	
	public static FmlSyntaxChecker getInstance() {
		return instance;
	}
	
	/**
	 * Checks the FML for syntax errors.
	 *
	 * @param fmlAsList the FML in List form
	 * @return true if the FML has no syntax errors in it
	 */
	public boolean isFmlSyntaxValid(List<String> fmlAsList) {
		String fmlAsString = buildStringFromList(fmlAsList);
		
		return checkForLegalChars(fmlAsString)
				&& checkForNoDuplicateNames(fmlAsList)
				&& checkForNoKeywordInFolderName(fmlAsList)
				&& checkForNoForwardJump(fmlAsList);
	}
	
	/**
	 * Iterates through every character in the FML and checks if it is a valid character.
	 *
	 * @param fml the whole fml in one String
	 * @return
	 */
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
	
	/**
	 * Checks that for every folder name there is no duplicate name in the same scope/tree level.
	 *
	 * @param fmlAsList the FML in form of a list
	 * @return true if the FML is free of duplicate names in the same scope
	 */
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
					
					// If the next level is higher in the tree (lower number), then the lopp will break. After this
					// there can be the same folder name because it will have a different parent than the current one.
				} else if (nextLevel < currentLevel) {
					break;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if the keyword is in any folder name.
	 *
	 * @param fmlAsList the FML in form of a list
	 * @return true if every folder name is free of keywords
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
	
	/**
	 * Checks if there is any forword jump (e.g. jumping from tree level 2 to 4) in the FML.
	 *
	 * @param fmlAsList the FML in form of a list
	 * @return true if no forward jump was found
	 */
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
	
	/**
	 * Gets the tree level/scope from one FML line.
	 *
	 * @param fmlLine one FML line with keywords
	 * @return tree level from that line
	 */
	private int getTreeLevelFromFmlLine(String fmlLine) {
		return getIndexOfLastKeyword(fmlLine) + 1;
	}
	
	/**
	 * Gets the index of the last keyword in a given line.
	 *
	 * @param fmlLine one FML line
	 * @return index of the last keyword
	 */
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
	
	/**
	 * Builds one string out of a List of Strings.
	 *
	 * @param list list of strings
	 * @return concatenated string
	 */
	private String buildStringFromList(List<String> list) {
		StringBuilder sb = new StringBuilder();
		
		for (String string : list) {
			sb.append(string);
		}
		
		return sb.toString();
	}
	
}
