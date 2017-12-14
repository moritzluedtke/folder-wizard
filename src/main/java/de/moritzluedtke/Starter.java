package de.moritzluedtke;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

public class Starter extends Application {
	public final int MAIN_WINDOW_WIDTH = 900;
	public final int MAIN_WINDOW_HEIGHT = 570;
	
	private static final Logger log = LogManager.getLogger();
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * JavaFX start method. Loads the FXML layout file into a URL
	 * and gives that to the method which creates the primary stage.
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
	 * This centers the stage too.
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
		} catch (IOException e) {
			log.error("Can't create primary stage! Might be an invalid URL.");
		}
		
		centerStage(primaryStage, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
		
		return primaryStage;
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
