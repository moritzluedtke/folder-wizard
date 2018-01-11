package de.moritzluedtke.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeView;
import de.moritzluedtke.service.FolderUtils;
import de.moritzluedtke.service.model.FolderTreeItem;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
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


public class MainWindowController {
	
	private static final Logger log = LogManager.getLogger();
	
	private static final String JAVA_FX_CSS_TEXT_FILL = "-fx-text-fill: ";
	private static final String MESSAGE_TEXT_DIRECTORY_VALID = "This is a directory :)";
	private static final String MESSAGE_TEXT_DIRECTORY_INVALID = "Please specify a valid path to a directory!";
	public static final String MESSAGE_TEXT_FML_VALID = "This is an FML file :)";
	public static final String MESSAGE_TEXT_FML_INVALID = "Please choose a valid FML file!";
	
	public static final String LABEL_DETAIL_AREA_TITLE_TEXT_CREATE = "Create File/FolderTreeItem";
	public static final String LABEL_DETAIL_AREA_TITLE_TEXT_DELETE = "Delete File/FolderTreeItem";
	public static final String LABEL_DETAIL_AREA_TITLE_TEXT_CREATE_BY_FILE = "Create FolderTreeItem by File";
	
	public static final String BUTTON_DETAIL_AREA_EXECUTE_TEXT_CREATE = "Create!";
	public static final String BUTTON_DETAIL_AREA_EXECUTE_TEXT_DELETE = "Delete!";
	public static final String BUTTON_DETIAL_AREA_EXECUTE_TEXT_CREATE_BY_FILE = "Create!";
	private static final String DIRECTORY_CHOOSER_WINDOW_TITLE = "Choose a root directory:";
	public static final String FILE_CHOOSER_WINDOW_TITLE = "Choose a FML file:";
	
	private static final String DEFAULT_DIRECTORY = "c:/";
	private static final String MESSAGE_TYPE_SUCCESS_COLOR = "#00AA00";
	private static final String MESSAGE_TYPE_ERROR_COLOR = "#AA0000";
	private static final double LARGE_ANIMATION_DURATION_IN_MS = 400;
	private static final int DETAIL_AREA_ANIMATION_PANE_TRAVEL_DISTANCE_Y_AXIS = 550;
	private static final String ABOUT_DIALOG_CONTENT_TEXT =
			"File Wizard wurde im Rahmen von der AE-Hausaufgabe in Block 4 geschrieben." +
					"Es dient dazu, die eigene Ordnerstruktur zu organisieren.\n" +
					"\n\n" +
					"Die Hauptfunktionen sind:\n" +
					"- Erstellen/Löschen von Dateien und Ordner nach bestimmten, frei definierbaren Namensmustern.\n" +
					"- Erstellen von Ordnerstrukturen anhand von FML (FolderTreeItem Modelling Language) Dateien.\n" +
					"  Diese Dateien definieren die Ordnerstruktur anhand einer eigenen Syntax.\n" +
					"\n\n" +
					"Benutzte Technologien:\n" +
					"- Java 8 + JavaFX 2\n" +
					"- JFoenix (Material Design Library für JavaFX)\n" +
					"- Logfj2 (Logging Framework)\n" +
					"- Gradle (Build-Management-Automatisierungs-Tool)\n" +
					"- IntelliJ 2017 Community & Professional\n" +
					"- Bitbucket (Kostenloses Git Repository)\n" +
					"\n" +
					"© 2018 Moritz Lüdtke";
	
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
		CREATE,
		DELETE,
		CREATE_BY_FILE
	}
	
	private DetailAreaSection activeDetailAreaSection;
	private String selectedRootPath = "";
	private String selectedFmlPath = "";
	private FolderUtils folderUtils = FolderUtils.getInstance();
	private FolderTreeItem rootFolderTreeItem = null;
	
	@FXML
	public StackPane rootStackPane;
	@FXML
	public Pane paneDetailArea;
	@FXML
	private Label labelMainAreaHeaderTitle;
	@FXML
	private Label labelMainAreaHeaderSubtitle;
	@FXML
	public Label labelMainAreaAboutDialogContent;
	@FXML
	public Label labelDetailAreaTitle;
	@FXML
	public Label labelDetailAreaRootPathMessage;
	@FXML
	public Label labelDetailAreaFmlFilePathMessage;
	@FXML
	public JFXButton buttonDetailAreaExecute;
	@FXML
	public VBox aboutDialogContent;
	@FXML
	public JFXTextField textFieldDetailAreaFmlFilePath;
	@FXML
	public JFXTextField textFieldDetailAreaRootPath;
	@FXML
	public JFXTreeView treeViewDetailArea;
	
	
	// TODO: Alle Sachen, die nicht mit der UI zu tun haben in extra Services (FileService, FolderUtils auslagern? Bsp: directory validation in einem FolderTreeItem Service.
	
	// TODO: Detail Area Clear all inputs when switching sections? OR Keep inputs seperate for each section, save'em
	
	// TODO: Services planen, Vererbung muss rein wegen Aufgabenstellung. Lohnt sich eine eigene File Klasse, die die java.io.File erweitert?
	
	/**
	 * Gets called before the GUI launches. <p>
	 * Adds the GUI change listeners.
	 */
	public void initialize() {
		addGUIChangeListeners();
	}
	
	/**
	 * Animates the Detail Area in and sets the global variable activeDetailAreaSection according to the pressed button.
	 *
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleMainAreaButtonCreateByFMLClicked(ActionEvent actionEvent) {
		activeDetailAreaSection = DetailAreaSection.CREATE_BY_FILE;
		showDetailArea(Animate.IN);
	}
	
	/**
	 * Handles the onAction event when the close button in the details area is clicked.
	 *
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleDetailAreaButtonCloseClicked(ActionEvent actionEvent) {
		showDetailArea(Animate.OUT);
	}
	
	/**
	 * Creates and shows a directory chooser. Writes the path of the selected directory into the text field.
	 *
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleDetailAreaButtonOpenRootFolderClicked(ActionEvent actionEvent) {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle(DIRECTORY_CHOOSER_WINDOW_TITLE);
		dirChooser.setInitialDirectory(new File(DEFAULT_DIRECTORY));
		
		Window mainWindow = rootStackPane.getScene().getWindow();
		File selectedDirectory = dirChooser.showDialog(mainWindow);
		
		if (selectedDirectory != null) {
			if (isUserInputADirectory(selectedDirectory.getAbsolutePath())) {
				try {
					textFieldDetailAreaRootPath.setText(selectedDirectory.getCanonicalPath());
				} catch (IOException e) {
					log.error(String.format(
							"IO Exception \"%s\" occured while setting the textFieldDetailAreaRootPath",
							e.getMessage()));
				}
			}
		}
	}
	
	//JAVA DOC
	@FXML
	public void handleDetailAreaButtonOpenFmlFileClicked(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(FILE_CHOOSER_WINDOW_TITLE);
		fileChooser.setInitialDirectory(new File(DEFAULT_DIRECTORY));
		
		Window mainWindow = rootStackPane.getScene().getWindow();
		File selectedFmlFile = fileChooser.showOpenDialog(mainWindow);
		
		if (selectedFmlFile != null) {
			textFieldDetailAreaFmlFilePath.setText(selectedFmlFile.getAbsolutePath());
		}
	}
	
	/**
	 * Handles the onAction event which gets activated when the button "create" in the detail area
	 * is clicked
	 *
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleDetailAreaButtonExecuteClicked(ActionEvent actionEvent) {
		folderUtils.writeFoldersToDisk(rootFolderTreeItem);
	}
	
	/**
	 * Handles the mouse click event when the "created by" label in the main menu is clicked.
	 *
	 * @param mouseEvent the action event provided by the button
	 */
	@FXML
	public void handleMainMenuLabelCreatedByClicked(MouseEvent mouseEvent) {
		showAboutDialog();
	}
	
	/**
	 * Handles the mouse click event when the header in the main menu is clicked.
	 *
	 * @param mouseEvent the action event provided by the button
	 */
	@FXML
	public void handleHeaderMainMenuClick(MouseEvent mouseEvent) {
		showAboutDialog();
	}
	
	/**
	 * Adds the GUI change listeners. These listenes get activated when the values they are listening to get changed.
	 * <p/>
	 * {@link MainWindowController#textFieldDetailAreaRootPath} Listener = Whenever the text inside the Text Field
	 * changes the new value will be check if it is a valid directory.
	 * Also the message underneath the Text Field will be updated.
	 */
	private void addGUIChangeListeners() {
		textFieldDetailAreaRootPath.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					
					if (isUserInputADirectory(newValue)) {
						setLabelDetailAreaMessageText(labelDetailAreaRootPathMessage,
								MESSAGE_TEXT_DIRECTORY_VALID,
								MessageType.SUCCESS);
						selectedRootPath = textFieldDetailAreaRootPath.getText();
						
						if (!selectedRootPath.isEmpty() && !selectedFmlPath.isEmpty()) {
							buttonDetailAreaExecute.setDisable(false);
						}
					} else {
						setLabelDetailAreaMessageText(labelDetailAreaRootPathMessage,
								MESSAGE_TEXT_DIRECTORY_INVALID, MessageType.ERROR);
						selectedRootPath = "";
						
						buttonDetailAreaExecute.setDisable(true);
					}
				});
		
		textFieldDetailAreaFmlFilePath.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					
					if (isUserInputAFmlFile(newValue)) {
						setLabelDetailAreaMessageText(labelDetailAreaFmlFilePathMessage,
								MESSAGE_TEXT_FML_VALID,
								MessageType.SUCCESS);
						selectedFmlPath = textFieldDetailAreaFmlFilePath.getText();
						
						if (!selectedRootPath.isEmpty() && isUserInputADirectory(selectedRootPath)) {
							buttonDetailAreaExecute.setDisable(false);
							
							rootFolderTreeItem = folderUtils.createFolderTreeFromFmlFile(selectedFmlPath,
									selectedRootPath);
							
							makeFolderTreePreviewVisible();
						}
					} else {
						setLabelDetailAreaMessageText(labelDetailAreaFmlFilePathMessage,
								MESSAGE_TEXT_FML_INVALID, MessageType.ERROR);
						selectedFmlPath = "";
						
						buttonDetailAreaExecute.setDisable(true);
					}
				});
	}
	
	private void makeFolderTreePreviewVisible() {
		if (rootFolderTreeItem != null) {
			TreeItem<String> root = new TreeItem<>(rootFolderTreeItem.getName());
			
			root = putFolderTreeItemIntoTreeItem(root);
			
			root.setExpanded(true);
			treeViewDetailArea.setRoot(root);
//			treeViewDetailArea.setShowRoot(false);
		}
	}
	
	private TreeItem<String> putFolderTreeItemIntoTreeItem(TreeItem<String> root, FolderTreeItem current) {
		for (FolderTreeItem item : root.getChildren()) {
			if (item.hasChildren()) {
				putFolderTreeItemIntoTreeItem(new TreeItem<>(item.getName()));
			}
			
			root.getChildren().add(new TreeItem<>(item.getName()));
		}
		
		return root;
	}
	
	//JAVA DOC
	private boolean isUserInputAFmlFile(String userInputPath) {
		File fmlFile = new File(userInputPath);
		return fmlFile.isFile() && fmlFile.getPath().endsWith(".fml");
	}
	
	/**
	 * Validates that the path typed in/selected by the user is a directory.
	 *
	 * @param userInputPath the text that is currently stored in the text field
	 * @return return if the path points to a directory
	 */
	private boolean isUserInputADirectory(String userInputPath) {
		return new File(userInputPath).isDirectory() && !userInputPath.endsWith("\\");
	}
	
	/**
	 * Sets the text of the label in the detail area for the root path message and changes the color according to
	 * the MessageType.
	 *
	 * @param text        the text of the label
	 * @param messageType SUCCESS = text color green | ERROR = text color red
	 */
	private void setLabelDetailAreaMessageText(Label label, String text, MessageType messageType) {
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
	
	/**
	 * Creates a dialog and show is on the screen.
	 * Uses the content specified in the FXML file.
	 * Goes on top of the content defined in the root StackPane.
	 * <p>
	 * Sets the opacity of the dialog content to 1 (full opacity). The Content needs to remain hidden before the dialog
	 * appears. Otherwise it would be visible in the layout.
	 */
	private void showAboutDialog() {
		JFXDialog dialog = new JFXDialog();
		
		aboutDialogContent.setOpacity(1);
		labelMainAreaAboutDialogContent.setText(ABOUT_DIALOG_CONTENT_TEXT);
		
		dialog.setContent(aboutDialogContent);
		dialog.setDialogContainer(rootStackPane);
		dialog.show();
	}
	
	/**
	 * Shows the detail area by animating it into the scene.
	 * <p>
	 * Also hides the main title.
	 *
	 * @param animationDirection Animate.IN = Animating nodes into the scene;
	 *                           Animate.OUT = Animating nodes out of the scene;
	 */
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
	
	/**
	 * Customizes the detail area (e.g. the title) to fit the desired section.
	 *
	 * @param section = the section to which the detail area should be customized (e.g. Create, Delete, Create by File)
	 */
	private void customizeDetailAreaForSection(DetailAreaSection section) {
		switch (section) {
			case CREATE:
				labelDetailAreaTitle.setText(LABEL_DETAIL_AREA_TITLE_TEXT_CREATE);
				buttonDetailAreaExecute.setText(BUTTON_DETAIL_AREA_EXECUTE_TEXT_CREATE);
				break;
			case DELETE:
				labelDetailAreaTitle.setText(LABEL_DETAIL_AREA_TITLE_TEXT_DELETE);
				buttonDetailAreaExecute.setText(BUTTON_DETAIL_AREA_EXECUTE_TEXT_DELETE);
				break;
			case CREATE_BY_FILE:
				labelDetailAreaTitle.setText(LABEL_DETAIL_AREA_TITLE_TEXT_CREATE_BY_FILE);
				buttonDetailAreaExecute.setText(BUTTON_DETIAL_AREA_EXECUTE_TEXT_CREATE_BY_FILE);
				break;
		}
	}
	
	/**
	 * Animates the detail area into the front by bringing it up from the bottom or animating it down.
	 *
	 * @param animationDirection Animate.IN = Animating nodes into the scene;
	 *                           Animate.OUT = Animating nodes out of the scene
	 */
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
	
	/**
	 * Fades in/out the whole header (incl. the subtitle).
	 *
	 * @param fade Fade.IN = Animating opacity from 0 - 1; Fade.OUT = Animating opacity from 1 - 0
	 */
	private void fadeMainAreaHeader(Fade fade) {
		FadeTransition fadeTransTitle
				= new FadeTransition(Duration.millis(LARGE_ANIMATION_DURATION_IN_MS), labelMainAreaHeaderTitle);
		FadeTransition fadeTransSubtitle
				= new FadeTransition(Duration.millis(LARGE_ANIMATION_DURATION_IN_MS), labelMainAreaHeaderSubtitle);
		
		if (fade == Fade.IN) {
			fadeTransTitle.setFromValue(0.0);
			fadeTransTitle.setToValue(1.0);
			
			fadeTransSubtitle.setFromValue(0.0);
			fadeTransSubtitle.setToValue(1.0);
			
			fadeTransTitle.play();
			fadeTransSubtitle.play();
		} else {
			fadeTransTitle.setFromValue(1.0);
			fadeTransTitle.setToValue(0.0);
			
			fadeTransSubtitle.setFromValue(1.0);
			fadeTransSubtitle.setToValue(0.0);
			
			fadeTransTitle.play();
			fadeTransSubtitle.play();
		}
	}
	
}
