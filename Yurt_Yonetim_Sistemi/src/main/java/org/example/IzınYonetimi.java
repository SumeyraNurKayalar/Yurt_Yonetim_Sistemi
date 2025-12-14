package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class IzınYonetimi extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(IzınYonetimi.class.getResource("/StaffMainView.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 900, 700);
        stage.setTitle("Yurt Yönetim Sistemi - Personel Yönetim Ekranı");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
