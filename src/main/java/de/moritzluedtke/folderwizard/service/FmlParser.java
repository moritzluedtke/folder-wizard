package de.moritzluedtke.folderwizard.service;

import de.moritzluedtke.folderwizard.service.exception.FMLSyntaxException;
import de.moritzluedtke.folderwizard.service.model.FolderTreeItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FmlParser {
	
	private static final FmlParser instance = new FmlParser();
	
	private static final CharSequence FML_KEYWORD = "+";
	private final FmlSyntaxChecker syntaxChecker = FmlSyntaxChecker.getInstance();
	
	
	private FmlParser() {
	
	}
	
	public static FmlParser getInstance() {
		return instance;
	}
	
	public FolderTreeItem parseFml(String fmlPath, String rootPath)
			throws FMLSyntaxException, IOException {
		
		List<String> fml = extractFmlFromFile(fmlPath);
		
		return extractFolderStructureFromFMLList(fml, rootPath);
	}
	
	private List<String> extractFmlFromFile(String fmlPath) throws IOException, FMLSyntaxException {
		List<String> fml = Files.readAllLines(Paths.get(fmlPath));
		fml.removeIf(line -> !line.startsWith(FML_KEYWORD.toString()));
		
		if (syntaxChecker.isFmlSyntaxValid(fml)) {
			return fml;
		} else {
			throw new FMLSyntaxException();
		}
	}
	
	private FolderTreeItem extractFolderStructureFromFMLList(List<String> filteredFml, String rootPath) {
		FolderTreeItem root = new FolderTreeItem(getFolderNameFromPath(rootPath), rootPath, null);
		FolderTreeItem currentRoot = root;
		
		int currentTreeLevel = 1;
		
		for (int lineIndex = 0; lineIndex < filteredFml.size(); lineIndex++) {
			String currentLine = filteredFml.get(lineIndex);
			
			String folderName = getFolderNameFromFmlLine(currentLine);
			FolderTreeItem newFolder = new FolderTreeItem(folderName,
					currentRoot.getPath() + "\\" + folderName,
					currentRoot);
			
			currentRoot.addChildren(newFolder);
			
			if (lineIndex < filteredFml.size() - 1) {
				String nextLine = filteredFml.get(lineIndex + 1);
				int nextTreeLevel = getLevelFromKeyword(nextLine);
				
				if (nextTreeLevel > currentTreeLevel) {
					currentRoot = newFolder;
					currentTreeLevel = nextTreeLevel;
					
				} else if (nextTreeLevel < currentTreeLevel) {
					currentRoot = getParentFromNLevelsAbove(newFolder, currentTreeLevel - nextTreeLevel);
					currentTreeLevel = nextTreeLevel;
				}
			}
		}
		
		return root;
	}
	
	private FolderTreeItem getParentFromNLevelsAbove(FolderTreeItem folder, int numberOfTreeLevelsToMoveUpTheTree) {
		int i = 0;
		
		while (i <= numberOfTreeLevelsToMoveUpTheTree) {
			folder = folder.getParent();
			
			i++;
		}
		
		return folder;
	}
	
	private int getLevelFromKeyword(String fmlLine) {
		int indexOfLastKeyword = fmlLine.lastIndexOf(FML_KEYWORD.toString()) + 1;
		String keyword = fmlLine.substring(0, indexOfLastKeyword);
		
		return keyword.length();
	}
	
	private String getFolderNameFromFmlLine(String line) {
		return line.substring(line.lastIndexOf("+") + 1, line.length());
	}
	
	private String getFolderNameFromPath(String path) {
		return path.substring(path.lastIndexOf("\\") + 1, path.length());
	}
}
