package de.moritzluedtke.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MainWindowController {
	
	private enum Animate {
		IN,
		OUT
	}
	
	private enum Fade {
		IN,
		OUT
	}
	
	private static final Logger log = LogManager.getLogger();
	
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
	private Label labelHeaderTitleMainArea;
	
	@FXML
	private Label labelHeaderSubtitleMainArea;
	
	@FXML
	public Label labelAboutDialogContentMainArea;
	
	@FXML
	public JFXButton buttonCloseDetailArea;
	
	@FXML
	public VBox aboutDialogContent;
	
	@FXML
	public JFXTextField rootPathTextFieldDetailArea;
	
	
	/**
	 * Handles the onAction Event, when the button "Create File/Folder" on the main menu is clicked.
	 *
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleMainAreaButtonCreateClicked(ActionEvent actionEvent) {
		log.info("buttonCreate clicked");
		showDetailArea(Animate.IN);
	}
	
	/**
	 * Handles the onAction Event, when the button "Delete File/Folder" on the main menu is clicked.
	 *
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleMainAreaButtonDeleteClicked(ActionEvent actionEvent) {
		log.info("buttonDelete clicked");
	}
	
	/**
	 * Handles the onAction Event, when the button "Create Folder by FML" on the main menu is clicked.
	 *
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleMainAreaButtonCreateByFMLClicked(ActionEvent actionEvent) {
		log.info("buttonCreateByStructure clicked");
		showDetailArea(Animate.IN);
	}
	
	/**
	 * Handles the onAction event when the close button in the details area is clicked.
	 *
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleDetailAreaButtonCloseClicked(ActionEvent actionEvent) {
		log.info("detail area close BUTTON clicked");
		showDetailArea(Animate.OUT);
	}
	
	/**
	 * Handles the onAction event which gets activated when the button "open root folder" in the detail area
	 * is clicked
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleDetailAreaButtonOpenRootFolderClicked(ActionEvent actionEvent) {
	
	}
	
	@FXML
	public void handleDetailAreaButtonCreateClicked(ActionEvent actionEvent) {
	
	}
	
	/**
	 * Handles the mouse click event when the "created by" label in the main menu is clicked.
	 *
	 * @param mouseEvent the action event provided by the button
	 */
	@FXML
	public void handleLabelCreatedByMainMenuClicked(MouseEvent mouseEvent) {
		log.info("label created by clicked");
		
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
		labelAboutDialogContentMainArea.setText(ABOUT_DIALOG_CONTENT_TEXT);
		
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
				= new FadeTransition(Duration.millis(LARGE_ANIMATION_DURATION_IN_MS), labelHeaderTitleMainArea);
		FadeTransition fadeTransSubtitle
				= new FadeTransition(Duration.millis(LARGE_ANIMATION_DURATION_IN_MS), labelHeaderSubtitleMainArea);
		
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
