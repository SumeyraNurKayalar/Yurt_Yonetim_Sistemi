package org.example.controller;

import com.yurt.repository.StudentRepository;
import com.yurt.repository.RoomRepository;
import com.yurt.repository.PermissionRepository;
import com.yurt.repository.jdbc.JdbcStudentRepository;
import com.yurt.repository.jdbc.JdbcRoomRepository;
import com.yurt.repository.jdbc.JdbcPermissionRepository;
import javafx.scene.control.Alert;

public abstract class AbstractBaseController {

    protected final StudentRepository studentRepository = JdbcStudentRepository.getInstance();
    protected final RoomRepository roomRepository = JdbcRoomRepository.getInstance();
    protected final PermissionRepository permissionRepository = JdbcPermissionRepository.getInstance();

    protected void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected void showSuccessAlert(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Başarılı", message);
    }

    protected void showErrorAlert(String message) {
        showAlert(Alert.AlertType.ERROR, "Hata", message);
    }

    protected void showWarningAlert(String message) {
        showAlert(Alert.AlertType.WARNING, "Uyarı", message);
    }
}