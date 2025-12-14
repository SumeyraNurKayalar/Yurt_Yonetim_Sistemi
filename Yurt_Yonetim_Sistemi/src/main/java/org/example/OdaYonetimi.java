package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class OdaYonetimi extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            URL fxmlUrl = getClass().getResource("/room_manager_view.fxml");
            if (fxmlUrl == null) {
                System.err.println("HATA: FXML dosyası bulunamadı! Lütfen 'src/main/resources/room_manager_view.fxml' yolunu kontrol edin.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            VBox root = loader.load();

            Scene scene = new Scene(root, 750, 550);

            primaryStage.setTitle("Yurt Odası Yönetimi - JavaFX & JDBC");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Arayüz yüklenemedi: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}