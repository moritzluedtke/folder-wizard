package de.moritzluedtke;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Starter extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL mainWindowUrl = ClassLoader.getSystemResource("layout/MainWindow.fxml");
//        URL mainWindowUrl = getClass().getResource("/layout/MainWindow.fxml");

        createMainWindowGUI(primaryStage, mainWindowUrl).show();
    }

    private Stage createMainWindowGUI(Stage primaryStage, URL resource) {
        final int MAIN_WINDOW_WIDTH = 400;
        final int MAIN_WINDOW_HEIGHT = 250;

        try {
            Parent root = FXMLLoader.load(resource);
            primaryStage.setTitle("FileOrganizer - by Moritz Lüdtke");
            primaryStage.setScene(new Scene(root, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT));
            primaryStage.setResizable(false);
        } catch (IOException e) {
            System.out.println("Can't load the FXML File");
        }

        return primaryStage;
    }

}
