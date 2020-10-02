package de.moritzluedtke.folderwizard.gui;

import com.jfoenix.controls.*;
import de.moritzluedtke.folderwizard.service.FmlParser;
import de.moritzluedtke.folderwizard.service.FmlPresetProvider;
import de.moritzluedtke.folderwizard.service.FolderWriter;
import de.moritzluedtke.folderwizard.service.Utils;
import de.moritzluedtke.folderwizard.service.exception.FMLSyntaxException;
import de.moritzluedtke.folderwizard.service.model.CustomTreeItem;
import de.moritzluedtke.folderwizard.service.model.FmlPreset;
import de.moritzluedtke.folderwizard.service.model.FolderTreeItem;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("Duplicates")
public class MainWindowController {
	
	private static final Logger log = LogManager.getLogger();
	
	private static final String JAVA_FX_CSS_TEXT_FILL = "-fx-text-fill: ";
	private static final String MESSAGE_TYPE_SUCCESS_COLOR = "#00AA00";
	private static final String MESSAGE_TYPE_ERROR_COLOR = "#AA0000";
	private static final String MESSAGE_TEXT_DIRECTORY_VALID = "This is a directory :)";
	private static final String MESSAGE_TEXT_DIRECTORY_INVALID = "Please specify a valid path to a directory!";
	private static final String MESSAGE_TEXT_FML_VALID = "This is an FML file :)";
	private static final String MESSAGE_TEXT_FML_INVALID = "Please choose a valid FML file!";
	
	private static final String LABEL_DETAIL_AREA_TITLE_TEXT_CREATE_BY_FILE = "Create Folder by File";
	
	private static final String BUTTON_DETIAL_AREA_EXECUTE_TEXT_CREATE_BY_FILE = "Create!";
	
	private static final String DIRECTORY_CHOOSER_WINDOW_TITLE = "Choose a root directory:";
	private static final String FILE_CHOOSER_WINDOW_TITLE = "Choose a FML file:";
	
	private static final String INSTALLATION_DIR = System.getProperty("user.dir");
	private static final double LARGE_ANIMATION_DURATION_IN_MS = 400;
	private static final double VERY_LARGE_ANIMATION_DURATION_IN_MS = 2000;
	private static final int DETAIL_AREA_ANIMATION_PANE_TRAVEL_DISTANCE_Y_AXIS = 560;
	
	private static final String ABOUT_DIALOG_TITLE = "About Folder Wizard";
	private static final String ABOUT_DIALOG_CONTENT_TEXT
			= "Folder Wizard was created as part of an homework assignment.\n\n" +
			"It's purpose is to automatically create folder structures based upon\n" +
			"a so called FML (Folder Modelling Language) file.\n" +
			"In these files, the folder structure can be described using a special syntax.\n" +
			"\n\n" +
			"Used technologies:\n" +
			"- Java 11 + JavaFX 14\n" +
			"- JFoenix\n" +
			"- Log4j2\n" +
			"- Maven\n" +
			"- IntelliJ Ultimate\n" +
			"\n" +
			"© 2020 Moritz Lüdtke";
	private static final String INCORRECT_FML_SYNTAX_ERROR_MESSAGE
			= "Incorrect FML Syntax!\n\n" +
			"Please check your file, it must not contain:\n" +
			"- Any characters other than A-Z, a-z, 0-9, blank space, _ , - as a folder name\n" +
			"- A forward jump (e.g. jumping from \"++\" level to \"++++\")\n" +
			"- No \"+\" in a folder name\n" +
			"- Duplicate folder names in the same scope/level\n\n" +
			"Check if it contains:\n" +
			"- Any keyword (+). If there is no keyword at the start of a line,\n" +
			"this program can't create a folder structure\n";
	private static final String ERROR_DIALOG_TITLE = "ERROR";
	private static final String ERROR_MESSAGE_CANT_CREATE_FOLDER_STRUCTURE
			= "Can not create folder structure.\n\n" +
			"Please check that there is no folder in the specified directory\n" +
			"that is also in the FML file.";
	private static final String IO_EXCEPTION_DURING_FML_LOADING_ERROR_MESSAGE
			= "There was a problem with loading the FML file!";
	
	private enum Animate {
		IN,
		OUT
	}
	
	private enum Fade {
		IN,
		OUT
	}
	
	private enum MessageType {
		ERROR,
		SUCCESS
	}
	
	private enum DetailAreaSection {
		CREATE_BY_FILE,
		NONE
	}
	
	private DetailAreaSection activeDetailAreaSection;
	private String selectedRootPath = "";
	private String selectedFmlPath = "";
	private FolderTreeItem rootFolderTreeItem = null;
	
	private FmlParser fmlParser = FmlParser.getInstance();
	private FolderWriter folderWriter = FolderWriter.getInstance();
	private Utils utils = Utils.getInstance();
	private FmlPresetProvider fmlPresetProvider = FmlPresetProvider.getInstance();
	
	@FXML
	private StackPane rootStackPane;
	@FXML
	private Pane paneDetailArea;
	@FXML
	private Label labelMainAreaHeaderTitle;
	@FXML
	private Label labelMainAreaHeaderSubtitle;
	@FXML
	private Label labelDialogTitle;
	@FXML
	private Label labelDialogContent;
	@FXML
	private Label labelDetailAreaTitle;
	@FXML
	private Label labelDetailAreaRootPathMessage;
	@FXML
	private Label labelDetailAreaFmlFilePathMessage;
	@FXML
	private Label labelDetailAreaExecuteSuccess;
	@FXML
	private JFXButton buttonDetailAreaExecute;
	@FXML
	private VBox dialogVBox;
	@FXML
	private JFXTextField textFieldDetailAreaFmlFilePath;
	@FXML
	private JFXTextField textFieldDetailAreaRootPath;
	@FXML
	private JFXTreeView treeViewDetailArea;
	@FXML
	private JFXButton buttonDetailAreaPreset;
	
	private JFXPopup presetPopup = new JFXPopup();
	private JFXListView<FmlPreset> popUpListView = new JFXListView<>();
	
	
	public void initialize() {
		addTextFieldChangeListeners();
		createPresetPopup();
	}
	
	@FXML
	public void handleMainAreaButtonCreateByFMLClicked() {
		activeDetailAreaSection = DetailAreaSection.CREATE_BY_FILE;
		showDetailArea(Animate.IN);
	}
	
	@FXML
	public void handleDetailAreaButtonCloseClicked() {
		showDetailArea(Animate.OUT);
		activeDetailAreaSection = DetailAreaSection.NONE;
	}
	
	@FXML
	public void handleDetailAreaButtonOpenRootFolderClicked() {
		if (selectedRootPath.isEmpty()) {
			File selectedDirectory = getDirectoryFromDirChooser(INSTALLATION_DIR);
			
			putSelectedRootPathIntoTextField(selectedDirectory);
		} else {
			File selectedDirectory = getDirectoryFromDirChooser(selectedRootPath);
			
			putSelectedRootPathIntoTextField(selectedDirectory);
		}
	}
	
	@FXML
	public void handleDetailAreaButtonOpenFmlFileClicked() {
		if (selectedFmlPath.isEmpty()) {
			File selectedFmlFile = getFileFromFileChooser(INSTALLATION_DIR);
			
			putSelectedFmlPathIntoTextField(selectedFmlFile);
		} else {
			File selectedFmlFile = getFileFromFileChooser(utils.getDirectoryFromFilePath(selectedFmlPath));
			
			putSelectedFmlPathIntoTextField(selectedFmlFile);
		}
	}
	
	@FXML
	public void handleDetailAreaButtonChoosePresetClicked(MouseEvent mouseEvent) {
		List<FmlPreset> presetList = fmlPresetProvider.readAllPresetsFromDisk(INSTALLATION_DIR);
		if (!presetList.isEmpty()) {
			updatePresetPopup(presetList);

			presetPopup.show(buttonDetailAreaPreset,
							 JFXPopup.PopupVPosition.TOP,
							 JFXPopup.PopupHPosition.LEFT,
							 mouseEvent.getX(),
							 mouseEvent.getY());
		}
	}
	
	@FXML
	public void handleDetailAreaButtonExecuteClicked() {
		if (folderWriter.writeFoldersToDisk(rootFolderTreeItem)) {
			activateLabel(labelDetailAreaExecuteSuccess, "Success", MessageType.SUCCESS);
			fadeGUIComponent(Fade.OUT,
					labelDetailAreaExecuteSuccess,
					VERY_LARGE_ANIMATION_DURATION_IN_MS);
			
			clearFMLPath();
		} else {
			showDialog(ERROR_MESSAGE_CANT_CREATE_FOLDER_STRUCTURE, ERROR_DIALOG_TITLE);
		}
	}
	
	@FXML
	public void handleMainMenuLabelCreatedByClicked() {
		showDialog(ABOUT_DIALOG_CONTENT_TEXT, ABOUT_DIALOG_TITLE);
	}
	
	@FXML
	public void handleHeaderMainMenuClick() {
		showDialog(ABOUT_DIALOG_CONTENT_TEXT, ABOUT_DIALOG_TITLE);
	}
	
	private void updatePresetPopup(List<FmlPreset> presetList) {
		int currentListViewSize = popUpListView.getItems().size();
		popUpListView.getItems().remove(0, currentListViewSize);
		popUpListView.getItems().addAll(presetList);
		
		resizePresetPopupListView(presetList);
		
		popUpListView.getSelectionModel().clearSelection();
	}
	
	private void resizePresetPopupListView(List<FmlPreset> presetList) {
		int listWidth = calculateListWidthBasedOn(presetList);
		popUpListView.setStyle("-fx-min-width: " + listWidth);
	}
	
	private int calculateListWidthBasedOn(List<FmlPreset> presetList) {
		return (int) Math.round(utils.getLengthOfLongestNameFromList(presetList) * 6.7) + 30;
	}
	
	private void createPresetPopup() {
		presetPopup.setPopupContent(popUpListView);
		
		popUpListView.setOnMouseClicked(event -> {
			String selectedPresetPath = popUpListView.getSelectionModel().getSelectedItem().getPathToFml();
			
			textFieldDetailAreaFmlFilePath.setText(selectedPresetPath);
			
			presetPopup.hide();
		});
	}
	
	private void putSelectedFmlPathIntoTextField(File file) {
		if (file != null && !file.getPath().isEmpty()) {
			textFieldDetailAreaFmlFilePath.setText(file.getPath());
		}
	}
	
	private File getFileFromFileChooser(String initialDirectory) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(FILE_CHOOSER_WINDOW_TITLE);
		fileChooser.setInitialDirectory(new File(initialDirectory));
		
		Window mainWindow = rootStackPane.getScene().getWindow();
		
		return fileChooser.showOpenDialog(mainWindow);
	}
	
	private void putSelectedRootPathIntoTextField(File dir) {
		if (dir != null) {
			if (utils.isUserInputADirectory(dir.getPath())) {
				textFieldDetailAreaRootPath.setText(dir.getPath());
			}
		}
	}
	
	private File getDirectoryFromDirChooser(String initialDirectory) {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle(DIRECTORY_CHOOSER_WINDOW_TITLE);
		dirChooser.setInitialDirectory(new File(initialDirectory));
		
		Window mainWindow = rootStackPane.getScene().getWindow();
		
		return dirChooser.showDialog(mainWindow);
	}
	
	private void addTextFieldChangeListeners() {
		textFieldDetailAreaRootPath.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (utils.isUserInputADirectory(newValue)) {
						activateLabel(labelDetailAreaRootPathMessage,
								MESSAGE_TEXT_DIRECTORY_VALID,
								MessageType.SUCCESS);
						
						selectedRootPath = newValue;
						
						if (!selectedFmlPath.isEmpty() && utils.isUserInputAFmlFile(selectedFmlPath)) {
							makeFolderStructureFromFML();
						}
					} else {
						activateLabel(labelDetailAreaRootPathMessage,
								MESSAGE_TEXT_DIRECTORY_INVALID, MessageType.ERROR);
						selectedRootPath = "";
						
						buttonDetailAreaExecute.setDisable(true);
					}
				});
		
		textFieldDetailAreaFmlFilePath.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (utils.isUserInputAFmlFile(newValue)) {
						activateLabel(labelDetailAreaFmlFilePathMessage,
								MESSAGE_TEXT_FML_VALID,
								MessageType.SUCCESS);
						
						selectedFmlPath = newValue;
						
						if (!selectedRootPath.isEmpty() && utils.isUserInputADirectory(selectedRootPath)) {
							makeFolderStructureFromFML();
						}
					} else {
						activateLabel(labelDetailAreaFmlFilePathMessage,
								MESSAGE_TEXT_FML_INVALID, MessageType.ERROR);
						
						selectedFmlPath = "";
						
						emptyFolderPreview();
						buttonDetailAreaExecute.setDisable(true);
					}
				});
	}
	
	private void makeFolderStructureFromFML() {
		try {
			rootFolderTreeItem = fmlParser.parseFml(selectedFmlPath,
					selectedRootPath);
			
			buttonDetailAreaExecute.setDisable(false);
			makeFolderTreePreviewVisible();
		} catch (FMLSyntaxException e) {
			log.error(e.getMessage());
			showDialog(INCORRECT_FML_SYNTAX_ERROR_MESSAGE, ERROR_DIALOG_TITLE);
			
			textFieldDetailAreaFmlFilePath.setText("");
		} catch (IOException e) {
			log.error(e.getMessage());
			showDialog(IO_EXCEPTION_DURING_FML_LOADING_ERROR_MESSAGE, ERROR_DIALOG_TITLE);
			
			textFieldDetailAreaFmlFilePath.setText("");
		}
	}
	
	private void makeFolderTreePreviewVisible() {
		if (rootFolderTreeItem != null) {
			CustomTreeItem<String> root = new CustomTreeItem<>(rootFolderTreeItem.getName() + " (root)");
			
			root = utils.convertFolderTreeIntoTreeItem(root, rootFolderTreeItem);
			
			root.expandTree();
			
			treeViewDetailArea.setRoot(root);
		}
	}
	
	private void clearFMLPath() {
		textFieldDetailAreaFmlFilePath.setText("");
		labelDetailAreaFmlFilePathMessage.setOpacity(0);
		selectedFmlPath = "";
		
		emptyFolderPreview();
	}
	
	private void emptyFolderPreview() {
		treeViewDetailArea.setRoot(null);
	}
	
	private void activateLabel(Label label, String text, MessageType messageType) {
		label.setOpacity(1);
		label.setText(text);
		
		switch (messageType) {
			case SUCCESS:
				label.setStyle(JAVA_FX_CSS_TEXT_FILL + MESSAGE_TYPE_SUCCESS_COLOR);
				break;
			case ERROR:
				label.setStyle(JAVA_FX_CSS_TEXT_FILL + MESSAGE_TYPE_ERROR_COLOR);
				break;
		}
	}
	
	private void showDialog(String message, String title) {
		JFXDialog dialog = new JFXDialog();
		
		dialogVBox.setOpacity(1);
		
		labelDialogContent.setText(message);
		labelDialogTitle.setText(title);
		
		dialog.setContent(dialogVBox);
		dialog.setDialogContainer(rootStackPane);
		dialog.show();
	}
	
	private void showDetailArea(Animate animationDirection) {
		if (animationDirection == Animate.IN) {
			customizeDetailAreaForSection(activeDetailAreaSection);
			
			fadeMainAreaHeader(Fade.OUT);
			animateDetailArea(Animate.IN);
		} else {
			fadeMainAreaHeader(Fade.IN);
			animateDetailArea(Animate.OUT);
		}
	}
	
	private void customizeDetailAreaForSection(DetailAreaSection section) {
		switch (section) {
			case CREATE_BY_FILE:
				labelDetailAreaTitle.setText(LABEL_DETAIL_AREA_TITLE_TEXT_CREATE_BY_FILE);
				buttonDetailAreaExecute.setText(BUTTON_DETIAL_AREA_EXECUTE_TEXT_CREATE_BY_FILE);
				break;
		}
	}
	
	private void animateDetailArea(Animate animationDirection) {
		TranslateTransition translateTransition
				= new TranslateTransition(Duration.millis(LARGE_ANIMATION_DURATION_IN_MS), paneDetailArea);
		
		if (animationDirection == Animate.IN) {
			translateTransition.setByY(-DETAIL_AREA_ANIMATION_PANE_TRAVEL_DISTANCE_Y_AXIS);
			translateTransition.setCycleCount(1);
			translateTransition.setAutoReverse(false);
			
			translateTransition.play();
		} else {
			translateTransition.setByY(DETAIL_AREA_ANIMATION_PANE_TRAVEL_DISTANCE_Y_AXIS);
			translateTransition.setCycleCount(1);
			translateTransition.setAutoReverse(false);
			
			translateTransition.play();
		}
	}
	
	private void fadeMainAreaHeader(Fade fade) {
		fadeGUIComponent(fade, labelMainAreaHeaderTitle, LARGE_ANIMATION_DURATION_IN_MS);
		fadeGUIComponent(fade, labelMainAreaHeaderSubtitle, LARGE_ANIMATION_DURATION_IN_MS);
	}
	
	private void fadeGUIComponent(Fade fadeDirection, Node component, double duration) {
		FadeTransition fadeTransitionComponent = new FadeTransition(Duration.millis(duration), component);
		
		if (fadeDirection == Fade.IN) {
			fadeTransitionComponent.setFromValue(0.0);
			fadeTransitionComponent.setToValue(1.0);
			
			fadeTransitionComponent.play();
		} else {
			fadeTransitionComponent.setFromValue(1.0);
			fadeTransitionComponent.setToValue(0.0);
			
			fadeTransitionComponent.play();
		}
	}
	
}
