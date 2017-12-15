package de.moritzluedtke;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

public class Starter extends Application {
	private static final int MAIN_WINDOW_WIDTH = 900;
	private static final int MAIN_WINDOW_HEIGHT = 570;
	private static final String PATH_TO_WINDOW_ICON = "gui/icon/black/ic_b_new_folder.png";
	
	private static final Logger log = LogManager.getLogger();
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * JavaFX start method. Creates a {@link URL} to the FXML file
	 * and gives that to the method which creates the primary stage.
	 * <p>
	 * Also enables font smoothing.
	 *
	 * @param primaryStage	takes the primary stage from the JavaFX framework
	 * @throws Exception	generic exception when something goes wrong
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		URL mainWindowUrl = ClassLoader.getSystemResource("gui/layout/MainWindow.fxml");
		
		enableFontSmoothing();
		
		if (mainWindowUrl == null) {
			log.error("URL to FXML file is null! Check if the URL in the code is valid.");
		} else {
			createMainWindowGUI(primaryStage, mainWindowUrl).show();
		}
	}
	
	/**
	 * Creates the primary stage based upon the given {@link URL} to the FXML file.
	 * <p>
	 * Also sets the window logo, centers the stage & sets resizable to false.
	 *
	 * @param primaryStage	the primary stage
	 * @param resourceURL	takes the path to the FXML file
	 * @return				the created primary stage
	 */
	private Stage createMainWindowGUI(Stage primaryStage, URL resourceURL) {
		try {
			Parent root = FXMLLoader.load(resourceURL);
			primaryStage.setTitle("FileOrganizer - by Moritz Lüdtke");
			primaryStage.setScene(new Scene(root, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT));
			primaryStage.setResizable(false);
			
			setWindowIcon(primaryStage);
		} catch (IOException e) {
			log.error("Can't create primary stage! Might be an invalid URL.");
		}
		
		centerStage(primaryStage, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
		
		return primaryStage;
	}
	
	private void setWindowIcon(Stage primaryStage) {
		primaryStage.getIcons().add(new Image(PATH_TO_WINDOW_ICON));
	}
	
	private void enableFontSmoothing() {
		System.setProperty("prism.lcdtext", "false");
	}
	
	private void centerStage(Stage stage, int width, int height) {
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		
		stage.setX((screenBounds.getWidth() - width) / 2);
		stage.setY((screenBounds.getHeight() - height) / 2);
	}
	
}
