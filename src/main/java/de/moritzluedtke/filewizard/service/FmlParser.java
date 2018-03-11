package de.moritzluedtke.filewizard.service;

import de.moritzluedtke.filewizard.service.exception.FMLSyntaxException;
import de.moritzluedtke.filewizard.service.model.FolderTreeItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Parser which creates a folder structure out of an FML file. <p>
 * The singleton pattern is used as there only needs to be one FMLParser class at any given time during runtime.
 */
public class FmlParser {
	
	private static FmlParser instance = new FmlParser();
	
	private static final CharSequence FML_KEYWORD = "+";
	private FmlSyntaxChecker syntaxChecker = FmlSyntaxChecker.getInstance();
	
	
	private FmlParser() {
	
	}
	
	/**
	 * Returns the only/one existing object.
	 *
	 * @return the singelton object
	 */
	public static FmlParser getInstance() {
		return instance;
	}
	
	/**
	 * Validates and parses the FML file by calling the corresponding methods.
	 *
	 * @param fmlPath  path to a fml file
	 * @param rootPath path to a root folder, where the folder structure will be created later on
	 * @return the folder structure in form of the root {@link FolderTreeItem}
	 * @throws FMLSyntaxException gets thrown if there is a syntax error within the FML file
	 * @throws IOException        gets thrown it there is a problem with accessing a file/directory
	 */
	public FolderTreeItem parseFml(String fmlPath, String rootPath)
			throws FMLSyntaxException, IOException {
		
		List<String> fml = extractFmlFromFile(fmlPath);
		
		return extractFolderStructureFromFMLList(fml, rootPath);
	}
	
	/**
	 * Extracts the FML out of the file. Removes any comments and validates that there is no syntax error wihin the FML.
	 *
	 * @param fmlPath the path to the fml file
	 * @return the valid fml file in list form without any comments
	 * @throws IOException        gets thrown if there is a syntax error within the FML file
	 * @throws FMLSyntaxException gets thrown it there is a problem with accessing a file/directory
	 */
	private List<String> extractFmlFromFile(String fmlPath) throws IOException, FMLSyntaxException {
		List<String> fml = Files.readAllLines(Paths.get(fmlPath));
		fml.removeIf(line -> !line.startsWith(FML_KEYWORD.toString()));
		
		if (syntaxChecker.isFmlSyntaxValid(fml)) {
			return fml;
		} else {
			throw new FMLSyntaxException();
		}
	}
	
	/**
	 * Converts/extracts the folder tree out of the list of FML commands.
	 *
	 * @param filteredFml valid fml with no comments
	 * @param rootPath    path to the root folder
	 * @return the folder tree in form of the root {@link FolderTreeItem}
	 */
	private FolderTreeItem extractFolderStructureFromFMLList(List<String> filteredFml, String rootPath) {
		FolderTreeItem root = new FolderTreeItem(getFolderNameFromPath(rootPath), rootPath, null);
		FolderTreeItem currentRoot = root;
		
		// Level 1 equals one "+" in the FML file
		int currentTreeLevel = 1;
		
		for (int lineIndex = 0; lineIndex < filteredFml.size(); lineIndex++) {
			String currentLine = filteredFml.get(lineIndex);
			
			String folderName = getFolderNameFromFmlLine(currentLine);
			FolderTreeItem newFolder = new FolderTreeItem(folderName,
					currentRoot.getPath() + "\\" + folderName,
					currentRoot);
			
			// Adds the new folder as a child to the current root folder.
			currentRoot.addChildren(newFolder);
			
			// Checks the next line if it is:
			if (lineIndex < filteredFml.size() - 1) {
				String nextLine = filteredFml.get(lineIndex + 1);
				int nextTreeLevel = getLevelFromKeyword(nextLine);
				
				// - One level deeper into the tree
				if (nextTreeLevel > currentTreeLevel) {
					currentRoot = newFolder;
					currentTreeLevel = nextTreeLevel;
					
					// - One or more level higher in the tree
				} else if (nextTreeLevel < currentTreeLevel) {
					currentRoot = getParentFromNLevelsAbove(newFolder, currentTreeLevel - nextTreeLevel);
					currentTreeLevel = nextTreeLevel;
				}
			}
		}
		
		return root;
	}
	
	/**
	 * Moves n levels up the tree and returns that {@link FolderTreeItem}.
	 *
	 * @param folder                            the folder from which to move n levels up
	 * @param numberOfTreeLevelsToMoveUpTheTree the number of tree levels to move up
	 * @return the parent {@link FolderTreeItem} which is n levels above the current folder
	 */
	private FolderTreeItem getParentFromNLevelsAbove(FolderTreeItem folder, int numberOfTreeLevelsToMoveUpTheTree) {
		int i = 0;
		
		while (i <= numberOfTreeLevelsToMoveUpTheTree) {
			folder = folder.getParent();
			
			i++;
		}
		
		return folder;
	}
	
	/**
	 * Extracts the number of keywords (= tree level) in the given FML line.
	 *
	 * @param fmlLine FML line with keyword(s)
	 * @return the number of keywords (= tree level) in the FML line
	 */
	private int getLevelFromKeyword(String fmlLine) {
		int indexOfLastKeyword = fmlLine.lastIndexOf(FML_KEYWORD.toString()) + 1;
		String keyword = fmlLine.substring(0, indexOfLastKeyword);
		
		return keyword.length();
	}
	
	/**
	 * Extracts the folder name from an FML line with keywords.
	 *
	 * @param line FML line with keywords
	 * @return folder name (FML line without keywords)
	 */
	private String getFolderNameFromFmlLine(String line) {
		return line.substring(line.lastIndexOf("+") + 1, line.length());
	}
	
	/**
	 * Extracts the folder name out of the path to it.
	 *
	 * @param path the path to the folder
	 * @return the folder name
	 */
	private String getFolderNameFromPath(String path) {
		return path.substring(path.lastIndexOf("\\") + 1, path.length());
	}
}
