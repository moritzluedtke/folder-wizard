package de.moritzluedtke.folderwizard;

import javafx.application.Application;
import javafx.application.Platform;
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

/**
 * "Main" class. Creates and shows the GUI based on the FXML file.
 */
public class Starter extends Application {
	private static final int MAIN_WINDOW_WIDTH = 900;
	private static final int MAIN_WINDOW_HEIGHT = 570;
	private static final String PATH_TO_WINDOW_ICON = "gui/icon/window/ic_window_v7.png";
	
	private static final Logger log = LogManager.getLogger();
	private static final String GUI_MAIN_LAYOUT_URL = "gui/layout/MainWindow.fxml";
	
	/**
	 * Main method used to start the GUI.
	 * @param args contains the arguments from the command line
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * JavaFX start method. Loads the FXML file for the main window.
	 * <p>
	 * Also enables font smoothing and calls {@link #createMainWindowGUI(Stage, URL) createMainWindowGUI()}
	 *
	 * @param primaryStage	takes the primary stage from the JavaFX framework
	 * @throws Exception	gets thrown when something goes wrong
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		URL mainWindowUrl = ClassLoader.getSystemResource(GUI_MAIN_LAYOUT_URL);
		
		enableFontSmoothing();
		
		if (mainWindowUrl == null) {
			log.error("URL to FXML file is null! Check if the URL in the java code is valid.");
		} else {
			createMainWindowGUI(primaryStage, mainWindowUrl).show();
		}
	}
	
	/**
	 * Closes all GUI windows so that the program can really stop. Without the two method calls it is possible
	 * that the programm still runs in the background although all GUI windows are closed.
	 *
	 * @throws Exception gets thrown when something goes wrong
	 */
	@Override
	public void stop() throws Exception {
		Platform.exit();
		System.exit(0);
	}
	
	/**
	 * Creates the primary stage based upon the given {@link URL} to the FXML file.
	 * <p>
	 * Also sets the window logo, centers the stage & sets resizable to false.
	 *
	 * @param primaryStage	takes the primary stage from the JavaFX framework
	 * @param resourceURL	takes the path to the FXML file
	 * @return				the created primary stage
	 */
	private Stage createMainWindowGUI(Stage primaryStage, URL resourceURL) {
		try {
			Parent root = FXMLLoader.load(resourceURL);
			primaryStage.setTitle("Folder Wizard - by Moritz Lüdtke");
			primaryStage.setScene(new Scene(root, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT));
			primaryStage.setResizable(false);
			
			setWindowIcon(primaryStage);
		} catch (IOException e) {
			log.error("Can't create primary stage! Might be an invalid URL.");
		}
		
		centerStage(primaryStage, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
		
		return primaryStage;
	}
	
	/**
	 * Sets the Window Icon.
	 *
	 * @param primaryStage	takes the primary stage from the JavaFX framework
	 */
	private void setWindowIcon(Stage primaryStage) {
		primaryStage.getIcons().add(new Image(PATH_TO_WINDOW_ICON));
	}
	
	/**
	 * Enables font smoothing for the application.
	 */
	private void enableFontSmoothing() {
		System.setProperty("prism.lcdtext", "false");
	}
	
	/**
	 * Centers the window to the screen.
	 *
	 * @param stage				the stage that should be centered
	 * @param windowWidth		the windowWidth of the GUI window
	 * @param windowHeight		the windowHeight of the GUI window
	 */
	private void centerStage(Stage stage, int windowWidth, int windowHeight) {
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		
		stage.setX((screenBounds.getWidth() - windowWidth) / 2);
		stage.setY((screenBounds.getHeight() - windowHeight) / 2);
	}
	
}
