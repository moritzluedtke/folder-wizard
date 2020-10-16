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

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Starter extends Application {
    private static final int MAIN_WINDOW_WIDTH = 900;
    private static final int MAIN_WINDOW_HEIGHT = 570;
    private static final String GUI_MAIN_LAYOUT_URL = "gui/layout/MainWindow.fxml";
    private static final String PATH_TO_WINDOW_ICON = "gui/icon/window/ic_window_v7.png";
    private static final String PATH_TO_CURRENT_DIRECTORY = System.getProperty("user.dir");
    private static final String PRESET_FOLDER = "/_presets";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        URL mainWindowUrl = ClassLoader.getSystemResource(GUI_MAIN_LAYOUT_URL);

        enableFontSmoothing();

        if (mainWindowUrl == null) {
            //            log.error("URL to FXML file is null! Check if the URL is valid.");
        } else {
            createPresetsFolder();
            createMainWindowGUI(primaryStage, mainWindowUrl).show();
        }
    }

    private void createPresetsFolder() {
        File presetFolder = new File(PATH_TO_CURRENT_DIRECTORY + PRESET_FOLDER);

        if (!presetFolder.exists()) {
            presetFolder.mkdir();
        }
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }

    private Stage createMainWindowGUI(Stage primaryStage, URL resourceURL) {
        try {
            Parent root = FXMLLoader.load(resourceURL);

            primaryStage.setTitle("Folder Wizard - by Moritz Lüdtke");
            primaryStage.setScene(new Scene(root, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT));
            primaryStage.setResizable(false);

            setWindowIcon(primaryStage);
        } catch (IOException e) {
            //            log.error("Can't create primary stage! Might be an invalid URL.\n" + e);
        }

        centerStage(primaryStage);

        return primaryStage;
    }

    private void setWindowIcon(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(PATH_TO_WINDOW_ICON));
    }

    private void enableFontSmoothing() {
        System.setProperty("prism.lcdtext", "false");
    }

    private void centerStage(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        stage.setX((screenBounds.getWidth() - MAIN_WINDOW_WIDTH) / 2);
        stage.setY((screenBounds.getHeight() - MAIN_WINDOW_HEIGHT) / 2);
    }

}
