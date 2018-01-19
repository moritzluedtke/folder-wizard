package de.moritzluedtke.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeView;
import de.moritzluedtke.service.FmlParser;
import de.moritzluedtke.service.FolderWriter;
import de.moritzluedtke.service.Utils;
import de.moritzluedtke.service.exception.FMLSyntaxException;
import de.moritzluedtke.service.model.CustomTreeItem;
import de.moritzluedtke.service.model.FolderTreeItem;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
	
	private static final String DEFAULT_DIRECTORY = System.getProperty("user.dir");
	private static final double LARGE_ANIMATION_DURATION_IN_MS = 400;
	private static final double VERY_LARGE_ANIMATION_DURATION_IN_MS = 2000;
	private static final int DETAIL_AREA_ANIMATION_PANE_TRAVEL_DISTANCE_Y_AXIS = 560;
	
	private static final String ABOUT_DIALOG_TITLE = "About File Wizard";
	private static final String ABOUT_DIALOG_CONTENT_TEXT
			= "File Wizard wurde im Rahmen einer AE-Hausaufgabe in Block 4 geschrieben. " +
			"Es dient dazu, die eigene Ordnerstruktur zu organisieren.\n" +
			"\n\n" +
			"Die Hauptfunktionen ist das erstellen von Ordnerstrukturen " +
			"anhand von FML (Folder Modelling Language) Dateien.\n" +
			"\n\n" +
			"Benutzte Technologien:\n" +
			"- Java 8 + JavaFX 2\n" +
			"- JFoenix\n" +
			"- Logfj2\n" +
			"- Gradle\n" +
			"- IntelliJ 2017 Community & Professional\n" +
			"- Bitbucket\n" +
			"\n" +
			"© 2018 Moritz Lüdtke";
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
	public static final String ERROR_DIALOG_TITLE = "ERROR";
	public static final String ERROR_MESSAGE_CANT_CREATE_FOLDER_STRUCTURE
			= "Can not create folder structure.\n" +
			"Please check that there is no folder in the specified directory\n" +
			"that is also in the FML file.";
	
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
		CREATE_BY_FILE
	}
	
	private DetailAreaSection activeDetailAreaSection;
	private String selectedRootPath = "";
	private String selectedFmlPath = "";
	private FolderTreeItem rootFolderTreeItem = null;
	
	private FmlParser fmlParser = FmlParser.getInstance();
	private FolderWriter folderWriter = FolderWriter.getInstance();
	private Utils utils = Utils.getInstance();
	
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
	
	
	// TODO: Java Doc
	
	/**
	 * Gets called before the GUI launches. <p>
	 * Adds the GUI change listeners.
	 */
	public void initialize() {
		addGUIChangeListeners();
	}
	
	/**
	 * Animates the Detail Area in and sets the global variable activeDetailAreaSection according to the pressed button.
	 */
	@FXML
	public void handleMainAreaButtonCreateByFMLClicked() {
		activeDetailAreaSection = DetailAreaSection.CREATE_BY_FILE;
		showDetailArea(Animate.IN);
	}
	
	/**
	 * Handles the onAction event when the close button in the details area is clicked.
	 */
	@FXML
	public void handleDetailAreaButtonCloseClicked() {
		showDetailArea(Animate.OUT);
	}
	
	/**
	 * Creates and shows a directory chooser. Writes the path of the selected directory into the text field.
	 */
	@FXML
	public void handleDetailAreaButtonOpenRootFolderClicked() {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle(DIRECTORY_CHOOSER_WINDOW_TITLE);
		dirChooser.setInitialDirectory(new File(DEFAULT_DIRECTORY));
		
		Window mainWindow = rootStackPane.getScene().getWindow();
		File selectedDirectory = dirChooser.showDialog(mainWindow);
		
		if (selectedDirectory != null) {
			if (utils.isUserInputADirectory(selectedDirectory.getAbsolutePath())) {
				try {
					textFieldDetailAreaRootPath.setText(selectedDirectory.getCanonicalPath());
				} catch (IOException e) {
					log.error(String.format(
							"IO Exception \"%s\" occured while choosing a root folder",
							e.getMessage()));
				}
			}
		}
	}
	
	//JAVA DOC
	@FXML
	public void handleDetailAreaButtonOpenFmlFileClicked() {
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
	 */
	@FXML
	public void handleDetailAreaButtonExecuteClicked() {
		if (folderWriter.writeFoldersToDisk(rootFolderTreeItem)) {
			activateLabel(labelDetailAreaExecuteSuccess, "Success", MessageType.SUCCESS);
			fadeGUIComponent(Fade.OUT,
					labelDetailAreaExecuteSuccess,
					VERY_LARGE_ANIMATION_DURATION_IN_MS);
			
			clearUserInputFile();
		} else {
			showDialog(ERROR_MESSAGE_CANT_CREATE_FOLDER_STRUCTURE, ERROR_DIALOG_TITLE);
		}
	}
	
	/**
	 * Handles the mouse click event when the "created by" label in the main menu is clicked.
	 */
	@FXML
	public void handleMainMenuLabelCreatedByClicked() {
		showDialog(ABOUT_DIALOG_CONTENT_TEXT, ABOUT_DIALOG_TITLE);
	}
	
	/**
	 * Handles the mouse click event when the header in the main menu is clicked.
	 */
	@FXML
	public void handleHeaderMainMenuClick() {
		showDialog(ABOUT_DIALOG_CONTENT_TEXT, ABOUT_DIALOG_TITLE);
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
			showDialog("There was a problem with loading the FML file!", ERROR_DIALOG_TITLE);
			
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
	
	private void clearUserInputFile() {
		textFieldDetailAreaFmlFilePath.setText("");
		labelDetailAreaFmlFilePathMessage.setOpacity(0);
		
		treeViewDetailArea.setRoot(null);
	}
	
	/**
	 * Sets the text of the label in the detail area for the root path message and changes the color according to
	 * the MessageType.
	 *
	 * @param text        the text of the label
	 * @param messageType SUCCESS = text color green | ERROR = text color red
	 */
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
	
	/**
	 * Creates a dialog and show is on the screen.
	 * Uses the content specified in the FXML file.
	 * Goes on top of the content defined in the root StackPane.
	 * <p>
	 * Sets the opacity of the dialog content to 1 (full opacity). The Content needs to remain hidden before the dialog
	 * appears. Otherwise it would be visible in the layout.
	 */
	private void showDialog(String message, String title) {
		JFXDialog dialog = new JFXDialog();
		
		dialogVBox.setOpacity(1);
		
		labelDialogContent.setText(message);
		labelDialogTitle.setText(title);
		
		dialog.setContent(dialogVBox);
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
