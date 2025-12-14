package org.example.controller;

import com.yurt.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;

public class OgrenciAnaController extends AbstractBaseController {

    private User ogrenci;

    public void initData(User kullanici) {
        this.ogrenci = kullanici;
    }

    @FXML
    private void profilEkraniniAc() {
        if (ogrenci == null) {
            showErrorAlert("Kullanıcı bilgisi yüklenemedi.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OgrenciProfil.fxml"));
            Parent root = loader.load();

            OgrenciProfilController controller = loader.getController();
            controller.initData((int) ogrenci.getUserId());

            Stage stage = new Stage();
            stage.setTitle("Profil Bilgilerim");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Profil ekranı yüklenemedi. OgrenciProfil.fxml dosyasını kontrol edin.");
        }
    }

    @FXML
    private void odaBilgisiEkraniniAc() {
        if (ogrenci == null) {
            showErrorAlert("Kullanıcı bilgisi yüklenemedi.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OdaBilgisi.fxml"));
            Parent root = loader.load();

            OdaBilgisiController controller = loader.getController();
            controller.initData((int) ogrenci.getUserId());

            Stage stage = new Stage();
            stage.setTitle("Oda ve Arkadaş Bilgileri");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Oda bilgisi ekranı yüklenemedi. OdaBilgisi.fxml dosyasını kontrol edin.");
        }
    }

    @FXML
    private void izinAlmaEkraniniAc() {
        if (ogrenci == null || !"Ogrenci".equalsIgnoreCase(ogrenci.getRole())) {
            showErrorAlert("Sadece öğrenciler izin talebinde bulunabilir.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StudentMainView.fxml"));
            Parent root = loader.load();

            StudentController controller = loader.getController();
            controller.initData((int) ogrenci.getUserId());

            Stage stage = new Stage();
            stage.setTitle("İzin Talebi ve Durumu");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", "İzin Yönetimi ekranı yüklenemedi. FXML dosya yolunu kontrol edin: /StudentMainView.fxml");
        }
    }
}