package de.moritzluedtke.gui;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MainWindowController {
	
	private static final Logger log = LogManager.getLogger();
	private static final double LARGE_ANIMATION_DURATION_IN_MS = 400;
	private static final int DETAIL_AREA_ANIMATION_TRAVEL_DISTANCE_Y_AXIS = 550;
	
	private enum Animate {
		IN,
		OUT
	}
	
	private enum Fade {
		IN,
		OUT
	}
	
	@FXML
	public Pane paneDetailArea;
	
	@FXML
	private Label labelHeaderTitle;
	
	@FXML
	private Label labelHeaderSubtitle;
	
	@FXML
	public JFXButton buttonDetailAreaClose;
	
	@FXML
	private Group borderPaneDetailAreaLayout;
	
	
	/**
	 * Handles the onAction Event, when the button "Create File/Folder" on the main menu is clicked.
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleButtonCreateClicked(ActionEvent actionEvent) {
		log.info("buttonCreate clicked");
		showDetailArea(Animate.IN);
	}
	
	/**
	 * Handles the onAction Event, when the button "Delete File/Folder" on the main menu is clicked.
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleButtonDeleteClicked(ActionEvent actionEvent) {
		log.info("buttonDelete clicked");
		showDetailArea(Animate.IN);
	}
	
	/**
	 * Handles the onAction Event, when the button "Create Folder by FML" on the main menu is clicked.
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleButtonCreateByFMLClicked(ActionEvent actionEvent) {
		log.info("buttonCreateByStructure clicked");
		showDetailArea(Animate.IN);
	}
	
	/**
	 * Handles the onAction event when the close button in the details area is clicked.
	 * @param actionEvent the action event provided by the button
	 */
	@FXML
	public void handleDetailAreaCloseButtonClicked(ActionEvent actionEvent) {
		log.info("detail area close BUTTON clicked");
		showDetailArea(Animate.OUT);
	}
	
	/**
	 * Handles the mouse clicked event when the "created by" label in the main menu is clicked.
	 * @param mouseEvent the action event provided by the button
	 */
	@FXML
	public void handleLabelCreatedByMainMenuClicked(MouseEvent mouseEvent) {
		log.info("label created by clicked");
		//TODO: Make Pop up for "Thank you" Message + Used Frameworks
		//TODO: Or make that into an Info button in the lower right?
	}
	
	/**
	 * Shows the detail Area by animating it into the scene.
	 * <p>
	 * Also hides the main title.
	 * @param animationDirection Animate.IN = Animating nodes into the scene;
	 *                           Animate.OUT = Animating nodes out of the scene;
	 */
	private void showDetailArea(Animate animationDirection) {
		if (animationDirection == Animate.IN) {
			fadeHeaderTitle(Fade.OUT);
			animateDetailArea(Animate.IN);
		} else {
			fadeHeaderTitle(Fade.IN);
			animateDetailArea(Animate.OUT);
		}
	}
	
	/**
	 * Animates the detail area into the front by bringing it up from the bottom or animating it down.
	 * @param animationDirection Animate.IN = Animating nodes into the scene;
	 *                           Animate.OUT = Animating nodes out of the scene
	 */
	private void animateDetailArea(Animate animationDirection) {
		TranslateTransition translateTransition
				= new TranslateTransition(Duration.millis(LARGE_ANIMATION_DURATION_IN_MS), paneDetailArea);
		
		if (animationDirection == Animate.IN) {
			translateTransition.setByY(- DETAIL_AREA_ANIMATION_TRAVEL_DISTANCE_Y_AXIS);
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
	 * Fades in/out the whole header title (incl. the subtitle).
	 * @param fade Fade.IN = Animating opacity from 0 - 1; Fade.OUT = Animating opacity from 1 - 0
	 */
	private void fadeHeaderTitle(Fade fade) {
		FadeTransition fadeTransTitle
				= new FadeTransition(Duration.millis(LARGE_ANIMATION_DURATION_IN_MS), labelHeaderTitle);
		FadeTransition fadeTransSubtitle
				= new FadeTransition(Duration.millis(LARGE_ANIMATION_DURATION_IN_MS), labelHeaderSubtitle);
		
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
