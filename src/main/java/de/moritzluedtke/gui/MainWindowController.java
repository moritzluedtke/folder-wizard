package de.moritzluedtke.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;


public class MainWindowController {
	
	private static final Logger log = LogManager.getLogger();
	private static final String JAVA_FX_CSS_TEXT_FILL = "-fx-text-fill: ";
	private static final String ROOT_PATH_MESSAGE_TEXT_SUCCESS = "This is a directory :)";
	private static final String ROOT_PATH_MESSAGE_TEXT_ERROR = "Please specify a valid path to a directory!";
	
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
	
	private static final String DIRECTORY_CHOOSER_WINDOW_TITLE = "Choose a root directory";
	private static final String DIRECTORY_CHOOSER_DEFAULT_DIRECTORY = "c:/";
	private static final String MESSAGE_TYPE_SUCCESS_COLOR = "#00AA00";
	private static final String MESSAGE_TYPE_ERROR_COLOR = "#AA0000";
	
	private static final double LARGE_ANIMATION_DURATION_IN_MS = 400;
	private static final int DETAIL_AREA_ANIMATION_TRAVEL_DISTANCE_Y_AXIS = 550;
	private static final String ABOUT_DIALOG_CONTENT_TEXT =
			"File Wizard wurde im Rahmen von der AE-Hausaufgabe in Block 4 geschrieben." +
			"Es dient dazu, die eigene Ordnerstruktur zu organisieren.\n" +
			"\n\n" +
			"Die Hauptfunktionen sind:\n" +
			"- Erstellen/Löschen von Dateien und Ordner nach bestimmten, frei definierbaren Namensmustern.\n" +
			"- Erstellen von Ordnerstrukturen anhand von FML (Folder Modelling Language) Dateien.\n" +
			"  Diese Dateien definieren die Ordnerstruktur anhand einer eigenen Syntax.\n" +
			"\n\n" +
			"Benutzte Technologien:\n" +
			"- Java 8 + JavaFX 2\n" +
			"- JFoenix (Material Design Library für JavaFX)\n" +
			"- Logfj2 (Logging Framework)\n" +
			"- Gradle (Build-Management-Automatisierungs-Tool, wie Maven)\n" +
			"- IntelliJ 2017 Community & Professional\n" +
			"- Bitbucket (Kostenloses Git Repository)\n" +
			"\n" +
			"© 2018 Moritz Lüdtke";
	
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
	public Label labelDetailAreaRootPathMessage;
	
	@FXML
	public JFXButton buttonCloseDetailArea;
	
	@FXML
	public VBox aboutDialogContent;
	
	@FXML
	public JFXTextField textFieldDetailAreaRootPath;
	
	
	// TODO: Alle Sachen, die nicht mit der UI zu tun haben in extra Services (FileService, FolderService auslagern? Bsp: directory validation in einem Folder Service.
	// TODO: So wäre die Controller Klasse schöne schlank und alle Logik ist in einer extra Klasse.
	
	// TODO: Services planen, Vererbung muss rein wegen Aufgabenstellung. Lohnt sich eine eigene File Klasse, die die java.io.File erweitert?
	
	/**
	 * Handles the onAction Event, when the button "Create File/Folder" on the main menu is clicked.
	 *
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleMainAreaButtonCreateClicked(ActionEvent actionEvent) {
		showDetailArea(Animate.IN);
	}
	
	/**
	 * Handles the onAction Event, when the button "Delete File/Folder" on the main menu is clicked.
	 *
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleMainAreaButtonDeleteClicked(ActionEvent actionEvent) {
		showDetailArea(Animate.IN);
	}
	
	/**
	 * Handles the onAction Event, when the button "Create Folder by FML" on the main menu is clicked.
	 *
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleMainAreaButtonCreateByFMLClicked(ActionEvent actionEvent) {
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
		dirChooser.setInitialDirectory(new File(DIRECTORY_CHOOSER_DEFAULT_DIRECTORY));
		
		Window mainWindow = rootStackPane.getScene().getWindow();
		File selectedDirectory = dirChooser.showDialog(mainWindow);
		
		if (selectedDirectory != null) {
			if (selectedDirectory.isDirectory()) {
				textFieldDetailAreaRootPath.setText(selectedDirectory.getAbsolutePath());
				setLabelDetailAreaRootPathMessageText(ROOT_PATH_MESSAGE_TEXT_SUCCESS, MessageType.SUCCESS);
			}
		}
	}
	
	/**
	 * Handles the onAction event which gets activated when the button "create" in the detail area
	 * is clicked
	 *
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleDetailAreaButtonCreateClicked(ActionEvent actionEvent) {
		log.info("Detail Area: \"Create\" clicked");
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
	 * Handles the event in that the user types something into the root path textfield.
	 * Also validates
	 *
	 * @param inputMethodEvent the keyevent provided by the textfield
	 */
	@FXML
	public void handleTextFieldDetailAreaRootPathUserInput(KeyEvent inputMethodEvent) {
		String currentText = textFieldDetailAreaRootPath.getText();
		
		boolean pathIsDir = validateThatUserInputIsADirectory(currentText);
		
		if (pathIsDir) {
			setLabelDetailAreaRootPathMessageText(ROOT_PATH_MESSAGE_TEXT_SUCCESS, MessageType.SUCCESS);
		} else {
			setLabelDetailAreaRootPathMessageText(ROOT_PATH_MESSAGE_TEXT_ERROR, MessageType.ERROR);
		}
	}
	
	/**
	 * Validates that the path typed in by the user is a directory.
	 *
	 * @param 	userInput	the text that is currently stored in the text field
	 * @return	return if the path points to a directory
	 */
	private boolean validateThatUserInputIsADirectory(String userInput) {
		return new File(userInput).isDirectory();
	}
	
	/**
	 * Sets the text of the label in the detail area for the root path message and changes the color according to
	 * the MessageType.
	 *
	 * @param text			the text of the label
	 * @param messageType	SUCCESS = text color green | ERROR = text color red
	 */
	private void setLabelDetailAreaRootPathMessageText(String text, MessageType messageType) {
		labelDetailAreaRootPathMessage.setOpacity(1);
		labelDetailAreaRootPathMessage.setText(text);
		
		switch (messageType) {
			case SUCCESS:
				labelDetailAreaRootPathMessage.setStyle(JAVA_FX_CSS_TEXT_FILL + MESSAGE_TYPE_SUCCESS_COLOR);
				break;
			case ERROR:
				labelDetailAreaRootPathMessage.setStyle(JAVA_FX_CSS_TEXT_FILL + MESSAGE_TYPE_ERROR_COLOR);
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
			fadeMainAreaHeader(Fade.OUT);
			animateDetailArea(Animate.IN);
		} else {
			fadeMainAreaHeader(Fade.IN);
			animateDetailArea(Animate.OUT);
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
			translateTransition.setByY(-DETAIL_AREA_ANIMATION_TRAVEL_DISTANCE_Y_AXIS);
			translateTransition.setCycleCount(1);
			translateTransition.setAutoReverse(false);
			
			translateTransition.play();
		} else {
			translateTransition.setByY(DETAIL_AREA_ANIMATION_TRAVEL_DISTANCE_Y_AXIS);
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
