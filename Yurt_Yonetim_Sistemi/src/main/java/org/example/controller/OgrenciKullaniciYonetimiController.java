package org.example.controller;

import com.yurt.service.RoomService;
import com.yurt.model.User;
import com.yurt.model.Student;
import com.yurt.model.Staff;
import com.yurt.design.strategy.NameSearch;
import com.yurt.design.strategy.TCSearch;
import com.yurt.design.strategy.RoomNumberSearch;
import com.yurt.design.strategy.SearchStrategy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Separator;
import java.util.List;


public class OgrenciKullaniciYonetimiController extends AbstractBaseController {

    @FXML private TableView<User> studentTable;
    @FXML private TableColumn<User, String> nameCol;
    @FXML private TableColumn<User, String> tcCol;
    @FXML private TableColumn<User, String> roomCol;
    @FXML private TableColumn<User, Void> studentDetailsCol;
    @FXML private ComboBox<String> searchCriteria;
    @FXML private TextField searchStudentField;
    @FXML private Button odasizlariFiltreButton;

    private final RoomService roomService = new RoomService();
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML private TextField newUserNameField;
    @FXML private TextField newUserSurnameField;
    @FXML private TextField newUserTcField;
    @FXML private PasswordField newUserPasswordField;
    @FXML private TextField newUserEmailField;
    @FXML private TextField newUserPhoneField;
    @FXML private TextArea newUserAddressField;
    @FXML private ComboBox<String> newUserRoleCombo;
    @FXML private Label newUserStatusLabel;

    @FXML
    public void initialize() {
        setupStudentTable();
        loadAllStudents();
        setupSearchCriteria();
        setupNewUserRoleCombo();
        addButtonToStudentTable();

        if (odasizlariFiltreButton != null) {
            odasizlariFiltreButton.setOnAction(e -> handleFilterOdasizlar());
        }
    }

    private void setupStudentTable() {
        nameCol.setCellValueFactory(data -> {
            User user = data.getValue();
            return new SimpleStringProperty(user.getFirstName() + " " + user.getLastName());
        });

        tcCol.setCellValueFactory(data -> {
            User user = data.getValue();
            if (user instanceof Student student) {
                return new SimpleStringProperty(student.getTcNumber());
            }
            return new SimpleStringProperty("");
        });

        roomCol.setCellValueFactory(data -> {
            User user = data.getValue();
            if (user instanceof Student student) {
                return new SimpleStringProperty(student.getCurrentRoomNumber());
            }
            return new SimpleStringProperty("");
        });

        studentTable.setItems(userList);
    }

    private void setupSearchCriteria() {

        searchCriteria.setItems(FXCollections.observableArrayList("İsim/Soyisim", "TC Numarası", "Oda Numarası"));
        searchCriteria.setValue("İsim/Soyisim");

        searchStudentField.textProperty().addListener((obs, oldV, newV) -> handleStudentSearch());
    }

    private void setupNewUserRoleCombo() {
        ObservableList<String> roles = FXCollections.observableArrayList("Ogrenci", "Personel");
        newUserRoleCombo.setItems(roles);
        newUserRoleCombo.getSelectionModel().selectFirst();
    }

    private void loadAllStudents() {
        Platform.runLater(() -> {
            userList.clear();
            userList.addAll(roomService.getAllStudents().stream()
                    .filter(u -> "Ogrenci".equalsIgnoreCase(u.getRole()))
                    .toList());
        });
    }

    @FXML
    private void handleStudentSearch() {
        String searchTerm = searchStudentField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAllStudents();
            return;
        }

        SearchStrategy strategy = switch (searchCriteria.getValue()) {
            case "TC Numarası" -> new TCSearch();
            case "Oda Numarası" -> new RoomNumberSearch();
            case "İsim/Soyisim" -> new NameSearch();
            default -> new NameSearch();
        };

        List<User> filteredList = roomService.searchUsers(strategy, searchTerm);

        userList.setAll(filteredList.stream()
                .filter(u -> "Ogrenci".equalsIgnoreCase(u.getRole()))
                .toList());
    }

    @FXML
    private void handleFilterOdasizlar() {
        List<User> filteredList = roomService.getAllStudents().stream()
                .filter(u -> u instanceof Student student && "Yok".equalsIgnoreCase(student.getCurrentRoomNumber()))
                .toList();

        userList.setAll(filteredList);
        showAlert(Alert.AlertType.INFORMATION, "Filtre", filteredList.size() + " odasız öğrenci listelendi.");
    }

    @FXML
    private void handleCreateUser() {
        String ad = newUserNameField.getText();
        String soyad = newUserSurnameField.getText();
        String tc = newUserTcField.getText();
        String email = newUserEmailField.getText();
        String telefon = newUserPhoneField.getText();
        String adres = newUserAddressField.getText();
        String sifre = newUserPasswordField.getText();
        String rol = newUserRoleCombo.getValue();

        if (ad.isEmpty() || soyad.isEmpty() || tc.isEmpty() || email.isEmpty() || sifre.isEmpty() || rol == null) {
            newUserStatusLabel.setText("Hata: Tüm zorunlu alanları doldurun.");
            return;
        }

        try {
            User newUser;
            if ("Ogrenci".equals(rol)) {
                newUser = new Student.StudentBuilder()
                        .firstName(ad).lastName(soyad).tcNumber(tc).email(email)
                        .phone(telefon).address(adres).role(rol).build();
            } else {
                newUser = new Staff.StaffBuilder()
                        .firstName(ad).lastName(soyad).tcNumber(tc).email(email)
                        .phone(telefon).address(adres).role(rol).department("Yonetim").build();
            }

            String result = roomService.addNewUserWithFactory(newUser, sifre);

            if (result.startsWith("Hata")) {
                newUserStatusLabel.setText(result);
            } else {
                newUserStatusLabel.setText(result);
                clearNewUserFields();
                loadAllStudents();
            }
        } catch (Exception e) {
            newUserStatusLabel.setText("Hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addButtonToStudentTable() {
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("Göster/Düzelt");

            {
                btn.setOnAction((event) -> {
                    User user = getTableView().getItems().get(getIndex());
                    if (user instanceof Student student) {
                        showStudentDetails(student);
                    }
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        };

        studentDetailsCol.setCellFactory(cellFactory);
    }

    private void showStudentDetails(Student student) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Öğrenci Bilgilerini Güncelle");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setStyle("-fx-padding: 20;");

        TextField nameField = new TextField(student.getFirstName());
        TextField surnameField = new TextField(student.getLastName());
        TextField phoneField = new TextField(student.getPhone());
        TextField emailField = new TextField(student.getEmail());
        TextArea addressArea = new TextArea(student.getAddress());
        addressArea.setPrefRowCount(3);

        TextField tcField = new TextField(student.getTcNumber());
        tcField.setEditable(false);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Yeni şifre (Boş bırakılırsa değişmez)");

        Label roomLabel = new Label("Mevcut Oda: " + student.getCurrentRoomNumber());
        roomLabel.setStyle("-fx-font-weight: bold;");

        Button saveButton = new Button("Bilgileri Kaydet");
        Button closeButton = new Button("Kapat");

        HBox buttonBox = new HBox(10, saveButton, closeButton);

        saveButton.setOnAction(e -> {
            String newPassword = passwordField.getText();

            if (nameField.getText().isEmpty() || surnameField.getText().isEmpty() || emailField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Ad, Soyad ve E-posta alanları boş bırakılamaz.");
                return;
            }

            try {
                Student updatedStudent = new Student.StudentBuilder()
                        .userId(student.getUserId())
                        .firstName(nameField.getText())
                        .lastName(surnameField.getText())
                        .tcNumber(tcField.getText())
                        .email(emailField.getText())
                        .phone(phoneField.getText())
                        .address(addressArea.getText())
                        .role(student.getRole())
                        .currentRoomNumber(student.getCurrentRoomNumber())
                        .roomId(student.getRoomId())
                        .build();

                roomService.updateStudentWithFactory(updatedStudent, newPassword);

                showSuccessAlert("Öğrenci bilgileri başarıyla güncellendi.");

                loadAllStudents();
                dialog.close();

            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Hata", ex.getMessage());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Güncelleme Hatası", "Beklenmedik bir hata oluştu: " + ex.getMessage());
            }
        });

        closeButton.setOnAction(e -> dialog.close());

        dialogVBox.getChildren().addAll(
                roomLabel,
                new Separator(),
                new Label("TC Kimlik No (Okunur):"), tcField,
                new Label("Adı:"), nameField,
                new Label("Soyadı:"), surnameField,
                new Separator(),
                new Label("E-mail:"), emailField,
                new Label("Telefon:"), phoneField,
                new Label("Adres:"), addressArea,
                new Separator(),
                new Label("Yeni Şifre:"), passwordField,
                new Separator(),
                buttonBox
        );

        Scene dialogScene = new Scene(dialogVBox);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private void clearNewUserFields() {
        newUserNameField.clear();
        newUserSurnameField.clear();
        newUserTcField.clear();
        newUserPasswordField.clear();
        newUserEmailField.clear();
        newUserPhoneField.clear();
        newUserAddressField.clear();
    }
}