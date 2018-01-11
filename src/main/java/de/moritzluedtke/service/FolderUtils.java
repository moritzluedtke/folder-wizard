package de.moritzluedtke.service;

import de.moritzluedtke.service.model.FolderTreeItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class FolderUtils {
	
	private static final Logger log = LogManager.getLogger();
	
	private static final CharSequence FML_KEYWORD = "+";
	private static FolderUtils instance = new FolderUtils();
	
	
	private FolderUtils() {
	
	}
	
	public static FolderUtils getInstance() {
		return instance;
	}
	
	public FolderTreeItem createFolderTreeFromFmlFile(String fmlPath, String rootPath) {
		try {
			List<String> fml = Files.readAllLines(Paths.get(fmlPath));
			fml = filterOutCommentsFromFml(fml);
			
			return loopOverFmlLinesToCreateFolders(fml, rootPath);
		} catch (IOException e) {
			log.error("Error with loading the FML file\n" + e.getMessage());
			
			return null;
		}
	}
	
	private FolderTreeItem loopOverFmlLinesToCreateFolders(List<String> filteredFml, String rootPath) {
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
				
				if (isFmlLineStartingWithKeywordForLevel(nextLine, currentTreeLevel + 1)) {
					currentRoot = newFolder;
					currentTreeLevel++;
				} else if (isFmlLineStartingWithKeywordForLevel(nextLine, currentTreeLevel - 1)) {
					currentRoot = currentRoot.getParent();
					currentTreeLevel--;
				}
			}
		}
		
		return root;
	}
	
	private boolean isFmlLineStartingWithKeywordForLevel(String fmlLine, int level) {
		int indexOfLastKeyword = fmlLine.lastIndexOf(FML_KEYWORD.toString()) + 1;
		String keyword = fmlLine.substring(0, indexOfLastKeyword);
		
		return getKeywordForLevel(level).equals(keyword);
	}
	
	public boolean writeFoldersToDisk(FolderTreeItem rootItem) {
		
		
		return false;
	}
	
	public boolean isUserInputAFolder(String path) {
		return false;
	}
	
	public boolean isUserInputAFmlFile() {
		return false;
	}
	
	private List<String> filterOutCommentsFromFml(List<String> fml) {
		fml.removeIf(line -> !line.startsWith(FML_KEYWORD.toString()));
		
		return fml;
	}
	
	private String getKeywordForLevel(int level) {
		return String.join("", Collections.nCopies(level, FML_KEYWORD));
	}
	
	private String getFolderNameFromFmlLine(String line) {
		return line.substring(line.lastIndexOf("+") + 1, line.length());
	}
	
	private String getFolderNameFromPath(String path) {
		return path.substring(path.lastIndexOf("\\") + 1, path.length());
	}
}
