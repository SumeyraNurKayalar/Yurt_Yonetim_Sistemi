package org.example.controller;

import com.yurt.model.Room;
import com.yurt.repository.jdbc.JdbcRoomRepository;
import com.yurt.repository.jdbc.JdbcStudentRepository;
import com.yurt.service.RoomService;
import com.yurt.design.state.IRoomState;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class RoomController extends AbstractBaseController {

    @FXML private TextField roomNumberCreateField;
    @FXML private TextField capacityField;
    @FXML private TextField roomNumberPlacementField;
    @FXML private TextField studentNameField;
    @FXML private TextField studentSurnameField;
    @FXML private TextField studentTcField;
    @FXML private Label statusLabel;

    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, String> roomNumberCol;
    @FXML private TableColumn<Room, Integer> capacityCol;
    @FXML private TableColumn<Room, Integer> occupancyCol;
    @FXML private TableColumn<Room, String> stateCol;
    private final ObservableList<Room> roomData = FXCollections.observableArrayList();

    private final RoomService roomService =
            new RoomService(JdbcRoomRepository.getInstance(), JdbcStudentRepository.getInstance());

    @FXML
    public void initialize() {
        roomNumberCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        occupancyCol.setCellValueFactory(new PropertyValueFactory<>("currentOccupancy"));

        stateCol.setCellValueFactory(cellData -> {
            IRoomState state = cellData.getValue().getCurrentState();
            String stateName = state.getClass().getSimpleName().replace("State", "");
            String turkishState = ("Available".equals(stateName)) ? "Müsait" : ("Full".equals(stateName) ? "Dolu" : stateName);
            return new javafx.beans.property.SimpleStringProperty(turkishState);
        });
        roomTable.setItems(roomData);
        refreshRoomList();

    }

    @FXML
    private void handleCreateRoom() {
        String roomNumber = roomNumberCreateField.getText();
        String capacityText = capacityField.getText();

        try {
            int capacity = Integer.parseInt(capacityText);
            if (roomNumber.isEmpty() || capacity <= 0) {
                showErrorAlert("Oda numarası ve geçerli kapasite giriniz.");
                return;
            }

            Room newRoom = new Room.RoomBuilder()
                    .roomNumber(roomNumber)
                    .capacity(capacity)
                    .currentOccupancy(0)
                    .build();

            roomService.createRoom(newRoom.getRoomNumber(), newRoom.getCapacity());

            statusLabel.setText("Başarılı: Oda " + roomNumber + " oluşturuldu ve kaydedildi.");

            roomNumberCreateField.clear();
            capacityField.clear();
            refreshRoomList();

        } catch (NumberFormatException e) {
            showErrorAlert("Kapasite geçerli bir sayı olmalıdır.");
        } catch (IllegalStateException e) {
            showErrorAlert(e.getMessage());
        } catch (Exception e) {
            showErrorAlert("Oda oluşturma sırasında bir hata oluştu: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddOccupantByDetails() {
        String roomNumber = roomNumberPlacementField.getText();
        String name = studentNameField.getText();
        String surname = studentSurnameField.getText();
        String tcNumber = studentTcField.getText();

        if (roomNumber.isEmpty() || name.isEmpty() || surname.isEmpty() || tcNumber.isEmpty()) {
            showWarningAlert("Lütfen tüm Ad, Soyad, TC No ve Oda Numarası alanlarını doldurun.");
            return;
        }

        if (tcNumber.length() != 11 || !tcNumber.matches("\\d+")) {
            showErrorAlert("TC Kimlik Numarası 11 haneli sayı olmalıdır.");
            return;
        }

        try {
            String result = roomService.addOccupantByDetails(roomNumber, name, surname, tcNumber);

            if (result.startsWith("Hata") || result.startsWith("Uyarı")) {
                showErrorAlert(result);
            } else {
                statusLabel.setText(result);
                roomNumberPlacementField.clear();
                studentNameField.clear();
                studentSurnameField.clear();
                studentTcField.clear();
                refreshRoomList();
            }

        } catch (Exception e) {
            showErrorAlert("Yerleştirme sırasında beklenmeyen bir hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void refreshRoomList() {
        roomData.clear();
        roomData.addAll(roomService.listAllRooms());
    }
}