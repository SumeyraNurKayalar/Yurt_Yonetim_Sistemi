package org.example.controller;

import com.yurt.model.User;
import com.yurt.security.PasswordHasher;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class OgrenciProfilController extends AbstractBaseController {

    @FXML private TextField adField;
    @FXML private TextField soyadField;
    @FXML private TextField tcField;
    @FXML private TextField emailField;
    @FXML private TextField telefonField;
    @FXML private TextArea adresArea;
    @FXML private PasswordField sifreField;
    @FXML private PasswordField sifreTekrarField;

    private int kullaniciId;
    private User currentUser;

    public void initData(int id) {
        this.kullaniciId = id;
        bilgileriYukle();
    }

    private void bilgileriYukle() {
        currentUser = studentRepository.getUserById(this.kullaniciId);

        if (currentUser != null) {
            populateFields(currentUser);
        } else {
            showErrorAlert("Profil bilgileri yüklenirken bir sorun oluştu.");
        }
    }

    private void populateFields(User user) {
        adField.setText(user.getFirstName());
        soyadField.setText(user.getLastName());
        tcField.setText(user.getTcNumber());
        emailField.setText(user.getEmail());
        telefonField.setText(user.getPhone());
        adresArea.setText(user.getAddress());
        tcField.setEditable(false);
    }

    @FXML
    private void profiliGuncelle() {
        String ad = adField.getText();
        String soyad = soyadField.getText();
        String email = emailField.getText();
        String telefon = telefonField.getText();
        String adres = adresArea.getText();
        String yeniSifre = sifreField.getText();
        String yeniSifreTekrar = sifreTekrarField.getText();

        String guncelSifreHash = null;

        if (!yeniSifre.isEmpty() || !yeniSifreTekrar.isEmpty()) {
            if (yeniSifre.length() > 0 && yeniSifre.length() < 8) {
                showErrorAlert("Yeni şifre minimum 8 karakter olmalıdır.");
                return;
            }
            if (!yeniSifre.equals(yeniSifreTekrar)) {
                showErrorAlert("Yeni şifreler eşleşmiyor.");
                return;
            }

            guncelSifreHash = PasswordHasher.hashPassword(yeniSifre);
        }

        currentUser.setFirstName(ad);
        currentUser.setLastName(soyad);
        currentUser.setEmail(email);
        currentUser.setPhone(telefon);
        currentUser.setAddress(adres);

        if (guncelSifreHash != null) {
            currentUser.setPasswordHash(guncelSifreHash);
        }

        boolean success = studentRepository.profilGuncelle(
                this.kullaniciId, ad, soyad, email, telefon, adres, guncelSifreHash
        );

        if (success) {
            showSuccessAlert("Profiliniz başarıyla güncellendi.");
            sifreField.clear();
            sifreTekrarField.clear();
        } else {
            showErrorAlert("Güncelleme başarısız oldu. E-posta adresi zaten kayıtlı olabilir.");
        }
    }
}