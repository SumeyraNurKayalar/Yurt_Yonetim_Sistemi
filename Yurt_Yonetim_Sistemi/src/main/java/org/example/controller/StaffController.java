package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import com.yurt.model.Izin;
import com.yurt.model.User;
import com.yurt.design.observer.PermissionSubject;
import com.yurt.design.observer.StaffObserver;
import com.yurt.service.PermissionService;

import java.time.LocalDate;
import java.util.stream.Collectors;

public class StaffController extends AbstractBaseController implements StaffObserver {

    @FXML private TableView<Izin> permissionTable;
    @FXML private TableColumn<Izin, String> studentNameCol;
    @FXML private TableColumn<Izin, String> studentRoomCol;
    @FXML private TableColumn<Izin, LocalDate> startDateCol;
    @FXML private TableColumn<Izin, LocalDate> endDateCol;
    @FXML private TableColumn<Izin, String> reasonCol;
    @FXML private TableColumn<Izin, String> statusCol;
    @FXML private TableColumn<Izin, Void> actionsCol;

    @FXML private DatePicker filterStartDatePicker;
    @FXML private DatePicker filterEndDatePicker;

    @FXML private TextField searchTextField;
    @FXML private Label searchStatusLabel;

    private final ObservableList<Izin> allPermissions = FXCollections.observableArrayList();
    private final PermissionService permissionService = new PermissionService();
    private final PermissionSubject permissionSubject = PermissionSubject.getInstance();

    private int currentStaffId = -1;

    public void initData(User personel) {
        if (personel != null) {
            this.currentStaffId = (int) personel.getUserId();
        }
        initialize();
    }


    @FXML
    public void initialize() {
        if (currentStaffId == -1) {
            System.err.println("UYARI: Personel ID'si initData ile set edilmedi. Tablo yüklenemeyecek.");
            return;
        }

        permissionSubject.setStaffObserver(this);
        loadPermissions();
        setupColumns();
        permissionTable.setItems(allPermissions);
    }

    private void loadPermissions() {
        allPermissions.clear();
        allPermissions.addAll(
                permissionService.getAllPermissions().stream()
                        .filter(i -> i.getDurum().equals("Beklemede"))
                        .collect(Collectors.toList())
        );
        searchStatusLabel.setText(allPermissions.size() + " izin talebi listelendi.");
        permissionTable.setItems(allPermissions);
    }

    @Override
    public void refreshPermissions() {
        loadPermissions();
        permissionTable.refresh();
    }

    private void setupColumns() {

        studentNameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getOgrenciAdi()));

        studentRoomCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getOdaNumarasi()));

        startDateCol.setCellValueFactory(new PropertyValueFactory<>("baslangicTarihi"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("bitisTarihi"));
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("neden"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("durum"));

        actionsCol.setCellFactory(param -> new TableCell<>() {

            private final Button approveBtn = new Button("Onayla");
            private final Button rejectBtn = new Button("Reddet");
            private final HBox box = new HBox(10, approveBtn, rejectBtn);

            {
                approveBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
                rejectBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

                approveBtn.setOnAction(e -> changeStatus("Onaylandı", this.getIndex()));
                rejectBtn.setOnAction(e -> changeStatus("Reddedildi", this.getIndex()));
            }

            private void changeStatus(String newStatus, int rowIndex) {
                Izin izin = getTableView().getItems().get(rowIndex);

                if (!izin.getDurum().equals("Beklemede")) return;

                Izin updated = permissionService.updatePermissionStatus(
                        izin.getIzinId(), newStatus, currentStaffId);

                if (updated != null) {

                    allPermissions.remove(izin);
                    permissionTable.refresh();

                    permissionSubject.notifyObservers(updated);
                } else {
                    System.err.println("HATA: İzin durumu veritabanına kaydedilemedi: " + izin.getIzinId());
                    showAlert(Alert.AlertType.ERROR, "İşlem Başarısız", "İzin durumu veritabanına kaydedilemedi.");
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                Izin izin = (Izin) getTableRow().getItem();

                if (empty || izin == null || !izin.getDurum().equals("Beklemede")) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });
    }

    @FXML
    private void handleSearch() {
        String text = searchTextField.getText().trim().toLowerCase();
        if (text.isEmpty()) {
            loadPermissions();
            return;
        }

        var filtered = permissionService.getAllPermissions().stream()
                .filter(i -> i.getOgrenciAdi().toLowerCase().contains(text))
                .collect(Collectors.toList());

        permissionTable.setItems(FXCollections.observableArrayList(filtered));
        searchStatusLabel.setText(filtered.size() + " sonuç bulundu.");
    }

    @FXML
    private void handleFilterByDate() {
        LocalDate s = filterStartDatePicker.getValue();
        LocalDate e = filterEndDatePicker.getValue();

        if (s == null || e == null) {
            searchStatusLabel.setText("Başlangıç ve bitiş tarihini seçiniz.");
            return;
        }

        var filtered = permissionService.getAllPermissions().stream()
                .filter(i -> !i.getBaslangicTarihi().isBefore(s) && !i.getBitisTarihi().isAfter(e))
                .collect(Collectors.toList());

        permissionTable.setItems(FXCollections.observableArrayList(filtered));
        searchStatusLabel.setText(filtered.size() + " izin bulundu.");
    }
}