package org.example.controller;

import com.yurt.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController extends AbstractBaseController {

    @FXML private TextField girisBilgisiField;
    @FXML private TextField sifreField;

    @FXML
    private void kayitEkraniniAc() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/KayitOl.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Yeni Hesap Oluştur");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Kayıt ekranı dosyası bulunamadı.");
        }
    }

    @FXML
    private void girisYap() {
        String giris = girisBilgisiField.getText();
        String sifre = sifreField.getText();

        User user = studentRepository.kullaniciGirisYap(giris, sifre);

        if (user != null) {
            String fxmlPath;
            String title;

            if ("Personel".equalsIgnoreCase(user.getRole())) {
                fxmlPath = "/PersonelAna.fxml";
                title = "Yurt Yönetim Sistemi - Personel Paneli";
            } else if ("Ogrenci".equalsIgnoreCase(user.getRole())) {
                fxmlPath = "/OgrenciAna.fxml";
                title = "Yurt Yönetim Sistemi - Öğrenci Paneli";
            } else {
                showErrorAlert("Geçersiz kullanıcı rolü!");
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();

                if ("Ogrenci".equalsIgnoreCase(user.getRole())) {
                    OgrenciAnaController controller = loader.getController();
                    controller.initData(user);
                }
                if ("Personel".equalsIgnoreCase(user.getRole())) {
                    PersonelAnaController controller = loader.getController();
                    controller.initData(user);
                }

                Stage stage = (Stage) girisBilgisiField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle(title);

            } catch (IOException e) {
                e.printStackTrace();
                showErrorAlert("Ana panel dosyası bulunamadı: " + fxmlPath);
            }
        } else {
            showErrorAlert("Giriş bilgisi veya şifre yanlış!");
        }
    }
}