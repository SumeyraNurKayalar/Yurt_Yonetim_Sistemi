package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.yurt.model.Izin;
import com.yurt.design.observer.PermissionSubject;
import com.yurt.design.observer.StudentObserver;
import com.yurt.service.PermissionService;

import java.io.IOException;
import java.time.LocalDate;

public class StudentController extends AbstractBaseController implements StudentObserver {

    private int currentUserId = -1;

    @FXML private TableView<Izin> permissionTable;
    @FXML private TableColumn<Izin, LocalDate> startDateCol;
    @FXML private TableColumn<Izin, LocalDate> endDateCol;
    @FXML private TableColumn<Izin, String> reasonCol;
    @FXML private TableColumn<Izin, String> statusCol;
    @FXML private Label notificationLabel;

    private final ObservableList<Izin> izinListesi = FXCollections.observableArrayList();

    private final PermissionService permissionService = new PermissionService();
    private final PermissionSubject permissionSubject = PermissionSubject.getInstance();

    public void initData(int userId) {
        this.currentUserId = userId;
        initialize();
    }

    @FXML
    public void initialize() {

        startDateCol.setCellValueFactory(new PropertyValueFactory<>("baslangicTarihi"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("bitisTarihi"));
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("neden"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("durum"));

        permissionSubject.addStudentObserver(this);
        permissionTable.setItems(izinListesi);
        notificationLabel.setText("İzin durumlarınızı görüntüleyebilirsiniz.");

        if (currentUserId != -1) {
            loadPermissions();
        }
    }

    private void loadPermissions() {
        if (currentUserId == -1) return;

        izinListesi.clear();
        izinListesi.addAll(
                permissionService.getAllPermissions().stream()
                        .filter(izin -> izin.getIzinAlanId() == currentUserId)
                        .toList()
        );
    }


    @Override
    public void update(Izin updatedIzin) {
        if (updatedIzin.getIzinAlanId() != currentUserId) {
            return;
        }

        for (Izin izin : izinListesi) {
            if (izin.getIzinId() == updatedIzin.getIzinId()) {
                izin.setDurum(updatedIzin.getDurum());
                permissionTable.refresh();
                break;
            }
        }

        notificationLabel.setText("🔔 Güncelleme: İzin durumunuz -> " + updatedIzin.getDurum());
        System.out.println("StudentController güncellendi: " + updatedIzin.getDurum());
    }

    @FXML
    private void handleCreateNewPermissionRequest() {
        if (currentUserId == -1) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Kullanıcı ID'si belirlenemedi. Giriş yapmayı deneyin.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PermissionRequestView.fxml"));
            Parent root = loader.load();

            PermissionRequestController requestController = loader.getController();
            requestController.setStudentController(this);

            requestController.initData(currentUserId);

            Stage stage = new Stage();
            stage.setTitle("Yeni İzin Talebi");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "İzin talep formu yüklenemedi! /PermissionRequestView.fxml dosyasını kontrol edin.");
            e.printStackTrace();
        }
    }

    public void addPermissionToTable(Izin newIzin) {
        izinListesi.add(newIzin);
        permissionTable.refresh();
        notificationLabel.setText("✔ Yeni izin talebiniz oluşturuldu.");
    }
}