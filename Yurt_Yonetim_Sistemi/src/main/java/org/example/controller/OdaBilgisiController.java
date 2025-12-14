package org.example.controller;

import com.yurt.model.User;
import com.yurt.DTO.OdaBilgisiDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.util.List;

public class OdaBilgisiController extends AbstractBaseController {

    @FXML private Label odaNumarasiLabel;
    @FXML private ListView<String> odaArkadaslariListView;
    @FXML private Label durumMesajiLabel;

    public void initData(int ogrenciId) {
        bilgileriYukle(ogrenciId);
    }

    private void bilgileriYukle(int ogrenciId) {
        OdaBilgisiDTO odaData = studentRepository.getOdaVeArkadasBilgileri(ogrenciId);

        if (odaData != null && odaData.getOdaNumarasi() != null) {
            odaNumarasiLabel.setText(odaData.getOdaNumarasi());
            durumMesajiLabel.setText("Odanızda kalan diğer öğrencilerin iletişim bilgileri:");

            List<User> arkadaslar = odaData.getOdaArkadaslari();
            if (arkadaslar.isEmpty()) {
                odaArkadaslariListView.setItems(FXCollections.observableArrayList("Odanızda sizden başka aktif kalan öğrenci yok. (Tek Kişilik Oda olabilir.)"));
            } else {
                ObservableList<String> arkadasListesi = FXCollections.observableArrayList();
                for (User k : arkadaslar) {
                    arkadasListesi.add(String.format("Adı Soyadı: %s %s | Tel: %s | E-posta: %s",
                            k.getFirstName(), k.getLastName(),
                            k.getPhone() != null ? k.getPhone() : "Bilgi Yok",
                            k.getEmail()));
                }
                odaArkadaslariListView.setItems(arkadasListesi);
            }
        } else {
            odaNumarasiLabel.setText("Bulunamadı");
            durumMesajiLabel.setText("⚠ Henüz aktif bir yurt odasına atanmadınız.");
            odaArkadaslariListView.setItems(FXCollections.observableArrayList());
            showWarningAlert("Oda bilgisi bulunamadı. Lütfen yönetici ile iletişime geçin.");
        }
    }
}