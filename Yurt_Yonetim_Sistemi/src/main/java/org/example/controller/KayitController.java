package org.example.controller;

import com.yurt.model.Student;
import com.yurt.security.PasswordHasher;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class KayitController extends AbstractBaseController {

    @FXML private TextField adField;
    @FXML private TextField soyadField;
    @FXML private TextField tcField;
    @FXML private TextField emailField;
    @FXML private TextField telefonField;
    @FXML private TextArea adresArea;
    @FXML private PasswordField sifreField;
    @FXML private PasswordField sifreTekrarField;

    @FXML
    private void kayitOl() {
        String ad = adField.getText();
        String soyad = soyadField.getText();
        String tc = tcField.getText();
        String email = emailField.getText();
        String telefon = telefonField.getText();
        String adres = adresArea.getText();
        String sifre = sifreField.getText();
        String sifreTekrar = sifreTekrarField.getText();
        String rol = "Ogrenci";

        if (ad.isEmpty() || soyad.isEmpty() || tc.isEmpty() || email.isEmpty() || sifre.isEmpty()) {
            showErrorAlert("Lütfen tüm zorunlu alanları doldurun.");
            return;
        }

        if (tc.length() != 11 || !tc.matches("\\d+")) {
            showErrorAlert("T.C. Kimlik No tam 11 rakamdan oluşmalıdır.");
            return;
        }

        if (sifre.length() < 8) {
            showErrorAlert("Şifre minimum 8 karakter olmalıdır.");
            return;
        }
        if (!sifre.equals(sifreTekrar)) {
            showErrorAlert("Şifreler eşleşmiyor.");
            return;
        }

        String hashedPassword = PasswordHasher.hashPassword(sifre);

        try {
            Student yeniOgrenci = new Student.StudentBuilder()
                    .firstName(ad)
                    .lastName(soyad)
                    .tcNumber(tc)
                    .email(email)
                    .phone(telefon)
                    .address(adres)
                    .role(rol)
                    .build();

            boolean success = studentRepository.kayitEkle(
                    yeniOgrenci.getFirstName(), yeniOgrenci.getLastName(), yeniOgrenci.getTcNumber(),
                    yeniOgrenci.getEmail(), yeniOgrenci.getPhone(), yeniOgrenci.getAddress(),
                    hashedPassword,
                    yeniOgrenci.getRole()
            );

            if (success) {
                showSuccessAlert("Hesabınız başarıyla oluşturuldu. Giriş yapabilirsiniz.");
                Stage stage = (Stage) adField.getScene().getWindow();
                stage.close();
            } else {
                showErrorAlert("Kayıt işlemi başarısız. (T.C. veya E-posta zaten kayıtlı olabilir.)");
            }
        } catch (Exception e) {
            showErrorAlert("Beklenmedik bir hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}