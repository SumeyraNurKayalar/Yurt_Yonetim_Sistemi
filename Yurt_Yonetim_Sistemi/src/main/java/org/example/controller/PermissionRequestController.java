package org.example.controller;

import com.yurt.model.Izin;
import com.yurt.service.PermissionService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class PermissionRequestController extends AbstractBaseController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextArea reasonTextArea;
    @FXML private Label errorLabel;

    private final PermissionService permissionService = new PermissionService();
    private StudentController studentController;
    private int currentUserId;

    public void setStudentController(StudentController controller) {
        this.studentController = controller;
    }

    public void initData(int userId) {
        this.currentUserId = userId;
    }

    @FXML
    private void handleSubmitRequest() {
        errorLabel.setText("");

        try {
            LocalDate baslangic = startDatePicker.getValue();
            LocalDate bitis = endDatePicker.getValue();
            String neden = reasonTextArea.getText();

            if (baslangic == null || bitis == null || neden == null || neden.trim().isEmpty()) {
                errorLabel.setText("Lütfen tüm alanları doldurun.");
                return;
            }

            if (baslangic.isAfter(bitis)) {
                errorLabel.setText("Başlangıç tarihi bitiş tarihinden sonra olamaz.");
                return;
            }
            Izin newRequest = new Izin.IzinBuilder()
                    .izinAlanId(currentUserId)
                    .baslangicTarihi(baslangic)
                    .bitisTarihi(bitis)
                    .neden(neden)
                    .durum("Beklemede")
                    .build();

            Izin saved = permissionService.savePermissionRequest(newRequest);

            if (saved == null) {
                errorLabel.setText("İzin kaydedilemedi.");
                return;
            }

            if (studentController != null) {
                studentController.addPermissionToTable(saved);
            }

            ((Stage) startDatePicker.getScene().getWindow()).close();

        } catch (Exception e) {
            errorLabel.setText("Hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) startDatePicker.getScene().getWindow()).close();
    }
}