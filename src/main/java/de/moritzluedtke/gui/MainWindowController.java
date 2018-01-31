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

/**
 * Controller class which is connected to the GUI (FXML file).
 * Controls everything which is related to the GUI.
 */
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
	
	private static final String DEFAULT_DIRECTORY_FOR_DIR_CHOOSER = System.getProperty("user.dir");
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
			"- Log4j2\n" +
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
			= "Can not create folder structure.\n\n" +
			"Please check that there is no folder in the specified directory\n" +
			"that is also in the FML file.";
	public static final String IO_EXCEPTION_DURING_FML_LOADING_ERROR_MESSAGE
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
	
	
	/**
	 * Gets called before the GUI window launches. <p>
	 * Calls the {@link MainWindowController#addTextFieldChangeListeners()} method.
	 */
	public void initialize() {
		addTextFieldChangeListeners();
	}
	
	/**
	 * Animates the Detail Area in and sets the global variable activeDetailAreaSection according to the pressed button.
	 * <p/>
	 * Note: This approach ({@link #activeDetailAreaSection}) is kept for future expandability.
	 */
	@FXML
	public void handleMainAreaButtonCreateByFMLClicked() {
		activeDetailAreaSection = DetailAreaSection.CREATE_BY_FILE;
		showDetailArea(Animate.IN);
	}
	
	/**
	 * Handles the onAction event when the close button in the details area is clicked.
	 * Animates the detail area out. <p/>
	 * Note: This approach ({@link #activeDetailAreaSection}) is kept for future expandability.
	 */
	@FXML
	public void handleDetailAreaButtonCloseClicked() {
		showDetailArea(Animate.OUT);
		activeDetailAreaSection = DetailAreaSection.NONE;
	}
	
	/**
	 * Shows a {@link DirectoryChooser} by calling {@link #getDirectoryFromDirChooser(String)}. <p>
	 * Also calls {@link #putSelectedRootPathIntoTextField(File)} and gives it the selected directory.
	 */
	@FXML
	public void handleDetailAreaButtonOpenRootFolderClicked() {
		if (selectedRootPath.isEmpty()) {
			File selectedDirectory = getDirectoryFromDirChooser(DEFAULT_DIRECTORY_FOR_DIR_CHOOSER);
			
			putSelectedRootPathIntoTextField(selectedDirectory);
		} else {
			File selectedDirectory = getDirectoryFromDirChooser(selectedRootPath);
			
			putSelectedRootPathIntoTextField(selectedDirectory);
		}
	}
	
	/**
	 * Shows a {@link FileChooser} by calling {@link #getFileFromFileChooser(String)}. <p>
	 * Also calls {@link #putSelectedFmlPathIntoTextField(File)} and gives it the selected file.
	 */
	@FXML
	public void handleDetailAreaButtonOpenFmlFileClicked() {
		if (selectedFmlPath.isEmpty()) {
			File selectedFmlFile = getFileFromFileChooser(DEFAULT_DIRECTORY_FOR_DIR_CHOOSER);
			
			putSelectedFmlPathIntoTextField(selectedFmlFile);
		} else {
			File selectedFmlFile = getFileFromFileChooser(utils.getDirectoryFromFilePath(selectedFmlPath));
			
			putSelectedFmlPathIntoTextField(selectedFmlFile);
		}
	}
	
	/**
	 * Handles the onAction event which gets activated when the button "execute" in the detail area is clicked.
	 * Writes the Folder Tree into actual folders on the hard drive
	 * by calling {@link FolderWriter#writeFoldersToDisk(FolderTreeItem)}.<p/>
	 * Shows a "Success" label on success and an error dialog in case of an error.<p>
	 * Calls {@link MainWindowController#clearFMLPath()}.
	 */
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
	
	/**
	 * Handles the mouse click event when the "created by" label in the main menu is clicked.
	 * Shows an about dialog (calls {@link MainWindowController#showDialog(String, String)}).
	 */
	@FXML
	public void handleMainMenuLabelCreatedByClicked() {
		showDialog(ABOUT_DIALOG_CONTENT_TEXT, ABOUT_DIALOG_TITLE);
	}
	
	/**
	 * Handles the mouse click event when the header in the main menu is clicked.
	 * Shows an about dialog (calls {@link MainWindowController#showDialog(String, String)}).
	 */
	@FXML
	public void handleHeaderMainMenuClick() {
		showDialog(ABOUT_DIALOG_CONTENT_TEXT, ABOUT_DIALOG_TITLE);
	}
	
	/**
	 * Sets the text of {@link #textFieldDetailAreaFmlFilePath} after basic validation of the input.
	 *
	 * @param file the selected fml file from the file chooser
	 */
	private void putSelectedFmlPathIntoTextField(File file) {
		if (file != null && !file.getPath().isEmpty()) {
			textFieldDetailAreaFmlFilePath.setText(file.getPath());
		}
	}
	
	/**
	 * Creates and shows a file chooser and returns the selected {@link File}.
	 *
	 * @param initialDirectory the default directory the file chooser shows
	 * @return the selected file from the file chooser
	 */
	private File getFileFromFileChooser(String initialDirectory) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(FILE_CHOOSER_WINDOW_TITLE);
		fileChooser.setInitialDirectory(new File(initialDirectory));
		
		Window mainWindow = rootStackPane.getScene().getWindow();
		
		return fileChooser.showOpenDialog(mainWindow);
	}
	
	/**
	 * Sets the text of {@link #textFieldDetailAreaRootPath} after basic validation of the input.
	 *
	 * @param dir the selected dir from the dir chooser
	 */
	private void putSelectedRootPathIntoTextField(File dir) {
		if (dir != null) {
			if (utils.isUserInputADirectory(dir.getPath())) {
				textFieldDetailAreaRootPath.setText(dir.getPath());
			}
		}
	}
	
	/**
	 * Creates and shows a directory chooser and returns the selected {@link File}.
	 *
	 * @param initialDirectory the default directory the directory chooser shows
	 * @return the selected directory from the directory chooser
	 */
	private File getDirectoryFromDirChooser(String initialDirectory) {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle(DIRECTORY_CHOOSER_WINDOW_TITLE);
		dirChooser.setInitialDirectory(new File(initialDirectory));
		
		Window mainWindow = rootStackPane.getScene().getWindow();
		
		return dirChooser.showDialog(mainWindow);
	}
	
	/**
	 * Adds two text field change listeners. These listenes get activated when the values they are listening to
	 * (text value) get changed.
	 * <p/>
	 * Both Listeners = Whenever the text inside the text field
	 * changes the new value will be check if it's valid.
	 * If it is valid a preview of the folder structure from the FML file will be generated
	 * by calling {@link #makeFolderStructureFromFML()}. <p>
	 * Also the message underneath the text field will be updated.
	 */
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
	
	/**
	 * Calls {@link FmlParser} and gives him {@link #selectedFmlPath} + {@link #selectedRootPath}. <p>
	 * When no exception occur during the validation of the FML, the execute button gets activated
	 * and the folder preview will be made by calling {@link #makeFolderTreePreviewVisible()}. <p>
	 * If an exception occurs, it will be catched and an error dialog will be shown.
	 */
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
	
	/**
	 * Converts the whole folder tree from {@link FolderTreeItem} into {@link CustomTreeItem} and makes the preview.
	 */
	private void makeFolderTreePreviewVisible() {
		if (rootFolderTreeItem != null) {
			CustomTreeItem<String> root = new CustomTreeItem<>(rootFolderTreeItem.getName() + " (root)");
			
			root = utils.convertFolderTreeIntoTreeItem(root, rootFolderTreeItem);
			
			root.expandTree();
			
			treeViewDetailArea.setRoot(root);
		}
	}
	
	/**
	 * Clears {@link #textFieldDetailAreaFmlFilePath}, {@link #labelDetailAreaFmlFilePathMessage},
	 * {@link #selectedFmlPath} and also calls {@link #emptyFolderPreview()}.
	 */
	private void clearFMLPath() {
		textFieldDetailAreaFmlFilePath.setText("");
		labelDetailAreaFmlFilePathMessage.setOpacity(0);
		selectedFmlPath = "";
		
		emptyFolderPreview();
	}
	
	/**
	 * Sets the root tree element in {@link #treeViewDetailArea} to null, effectivly clearing the preview.
	 */
	private void emptyFolderPreview() {
		treeViewDetailArea.setRoot(null);
	}
	
	/**
	 * Sets the text of the label in the detail area for the root path message and changes the color according to
	 * the MessageType. Also sets the opcatiy to 1 (full opacity).
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
	 * Uses the layout specified in the FXML file.
	 * Goes on top of the content defined in the root StackPane.
	 * <p>
	 * Sets the opacity of the dialog content to 1 (full opacity). The Content needs to remain hidden before the dialog
	 * appears. Otherwise it would be visible in the layout.
	 *
	 * @param message the message for the dialog body
	 * @param title   the title for the header of the dialog
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
	 * Customizes the detail area and then animates it into the scene.
	 * <p>
	 * Also hides the main title.
	 *
	 * @param animationDirection Animate.IN = Animating nodes into the scene |
	 *                           Animate.OUT = Animating nodes out of the scene
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
	 * <p/>
	 * Note: This approach ({@link #activeDetailAreaSection}) is kept for future expandability.
	 *
	 * @param section = the section to which the detail area should be customized (e.g. Create by File)
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
	 * Animates the detail area in/out by bringing it up from the bottom or animating it down.
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
	
	/**
	 * Fades in/out the component
	 *
	 * @param fadeDirection the direction of the fade
	 * @param component     the component which should be faded in/out
	 * @param duration      the time the fade should take
	 */
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
