package com.sorokaandriy.cafesimulation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class CafeApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        java.util.logging.Logger.getLogger("javafx.scene.control.skin.VirtualFlow")
                .setLevel(java.util.logging.Level.WARNING);

        FXMLLoader fxmlLoader = new FXMLLoader(
                CafeApplication.class.getResource("settings-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 960, 700);
        stage.setTitle("Cafe Simulation — Налаштування");   
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        try (InputStream is = CafeApplication.class.getResourceAsStream("/logging.properties")) {
            if (is != null) {
                LogManager.getLogManager().readConfiguration(is);
            }
        } catch (IOException ignored) {
        }
        launch();
    }
}