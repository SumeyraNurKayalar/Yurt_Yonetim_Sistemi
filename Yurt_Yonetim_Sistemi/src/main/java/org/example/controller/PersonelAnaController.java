package org.example.controller;

import com.yurt.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class PersonelAnaController extends AbstractBaseController {

    private User personelUser;

    public void initData(User kullanici) {
        this.personelUser = kullanici;
    }

    @FXML
    private void kullaniciYonetimiAc() {
        if (personelUser == null) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Personel bilgisi yüklenemedi.");
            return;
        }

        String fxmlPath = "/OgrenciKullaniciYonetimiView.fxml";
        URL resource = getClass().getResource(fxmlPath);

        if (resource == null) {
            showAlert(Alert.AlertType.ERROR, "Yol Hatası", "Kullanıcı Yönetimi ekranı yüklenemedi. FXML dosya yolunu kontrol edin: " + fxmlPath);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Kullanıcı Yönetimi (Öğrenci Listesi & Ekleme)");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", "Kullanıcı Yönetimi ekranı yüklenirken IO hatası oluştu.");
        }
    }
    @FXML
    private void izinYonetimiAc() {
        if (personelUser == null) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Personel bilgisi yüklenemedi. Oturumu kontrol edin.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StaffMainView.fxml"));
            Parent root = loader.load();

            StaffController controller = loader.getController();
            controller.initData(personelUser);

            Stage stage = new Stage();
            stage.setTitle("İzin Yönetimi");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", "İzin Yönetimi ekranı yüklenemedi. FXML dosya yolunu kontrol edin: /view/StaffMainView.fxml");
        }
    }

    @FXML
    private void odaYonetimiAc() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/room_manager_view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Oda Yönetimi");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", "Oda Yönetimi ekranı yüklenemedi. FXML dosya yolunu kontrol edin.");
        }
    }

}