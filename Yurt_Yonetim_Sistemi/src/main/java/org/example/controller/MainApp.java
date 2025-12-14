package org.example.controller;

import com.yurt.design.singleton.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private static final String LOGIN_VIEW_PATH = "/Login.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseConnection.getConnection();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOGIN_VIEW_PATH));
            Parent root = loader.load();

            primaryStage.setTitle("Yurt Yönetim Sistemi - Giriş");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Giriş FXML dosyası yüklenemedi: " + LOGIN_VIEW_PATH);
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        DatabaseConnection.closeConnection();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}