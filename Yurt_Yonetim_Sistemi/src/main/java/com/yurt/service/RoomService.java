package com.yurt.service;

import com.yurt.design.strategy.SearchContext;
import com.yurt.model.Room;
import com.yurt.model.User;
import com.yurt.model.Student;
import com.yurt.repository.RoomRepository;
import com.yurt.repository.StudentRepository;
import com.yurt.repository.jdbc.JdbcRoomRepository;
import com.yurt.repository.jdbc.JdbcStudentRepository;
import com.yurt.security.PasswordHasher;
import com.yurt.design.strategy.SearchStrategy;

import java.util.List;

public class RoomService {

    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;
    private final SearchContext searchContext;

    public RoomService(RoomRepository roomRepository, StudentRepository studentRepository) {
        this.roomRepository = roomRepository;
        this.studentRepository = studentRepository;
        this.searchContext = new SearchContext();
    }

    public RoomService() {
        this.roomRepository = JdbcRoomRepository.getInstance();
        this.studentRepository = JdbcStudentRepository.getInstance();
        this.searchContext = new SearchContext();
    }


    public void updateStudent(User user, String newPassword) {
        if (user == null) { throw new IllegalArgumentException("Hata: Güncellenecek kullanıcı nesnesi boş."); }

        if (newPassword != null && !newPassword.isEmpty()) {
            if (newPassword.length() < 8) { throw new IllegalArgumentException("Hata: Yeni şifre en az 8 karakter uzunluğunda olmalıdır."); }
            String hashedPassword = PasswordHasher.hashPassword(newPassword);
            user.setPasswordHash(hashedPassword);
        }

        boolean success = studentRepository.profilGuncelle(
                (int) user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getPasswordHash()
        );

        if (!success) { throw new RuntimeException("Veritabanı hatası: Profil güncellenemedi."); }
    }

    public void updateStudentWithFactory(User updatedUser, String newPassword) {
        updateStudent(updatedUser, newPassword);
    }


    public String addNewUser(String ad, String soyad, String tc, String kullaniciAdi, String telefon, String adres, String rol, String email, String plainPassword) {
        if (plainPassword.length() < 8) { return "Hata: Şifre en az 8 karakter olmalıdır."; }
        if (tc.length() != 11) { return "Hata: TC Kimlik Numarası 11 hane olmalıdır."; }

        String passwordHash = PasswordHasher.hashPassword(plainPassword);
        try {
            studentRepository.createUser(ad, soyad, tc, tc, telefon, adres, rol, email, passwordHash);
            return "Başarılı: " + rol + " (" + ad + " " + soyad + ") sisteme eklendi.";
        } catch (Exception e) {
            return "Hata: Kullanıcı eklenirken bir hata oluştu: " + e.getMessage();
        }
    }

    public String addNewUserWithFactory(User user, String plainPassword) {
        if (plainPassword.length() < 8) { return "Hata: Şifre en az 8 karakter olmalıdır."; }

        String passwordHash = PasswordHasher.hashPassword(plainPassword);

        try {
            studentRepository.createUser(
                    user.getFirstName(), user.getLastName(), user.getTcNumber(), user.getTcNumber(),
                    user.getPhone(), user.getAddress(), user.getRole(), user.getEmail(), passwordHash
            );
            return "Başarılı: " + user.getRole() + " (" + user.getFirstName() + " " + user.getLastName() + ") sisteme eklendi.";

        } catch (Exception e) { return "Hata: Kullanıcı eklenirken bir hata oluştu: " + e.getMessage(); }
    }


    public Room createRoom(String roomNumber, int capacity) {
        Room newRoom = new Room.RoomBuilder()
                .roomNumber(roomNumber)
                .capacity(capacity)
                .currentOccupancy(0)
                .build();

        return roomRepository.save(newRoom);
    }

    public String addOccupantByDetails(String roomNumber, String name, String surname, String tcNumber) {

        User user = studentRepository.findByTc(tcNumber);

        if (user == null) { return "Hata: TC Kimlik Numarası (" + tcNumber + ") ile eşleşen kayıtlı öğrenci bulunamadı."; }

        if (!(user instanceof Student student)) { return "Hata: Bulunan kayıt bir Öğrenci değil (" + user.getRole() + ")."; }

        if (studentRepository.isStudentCurrentlyPlaced(student.getUserId())) {
            return "Hata: " + student.getFirstName() + " " + student.getLastName() + " zaten mevcut bir odada kalmaktadır. Yerleştirme yapılamaz.";
        }

        if (!student.getFirstName().equalsIgnoreCase(name) || !student.getLastName().equalsIgnoreCase(surname)) {
            return "Hata: Girilen Ad/Soyad, TC Kimlik Numarası ile eşleşmiyor. Lütfen bilgileri kontrol edin.";
        }

        Room room = roomRepository.findByRoomNumber(roomNumber);
        if (room == null) { return "Hata: Belirtilen Oda Numarası (" + roomNumber + ") bulunamadı."; }

        int occupancyBefore = room.getCurrentOccupancy();
        room.addOccupant();
        int occupancyAfter = room.getCurrentOccupancy();

        if (occupancyAfter > occupancyBefore) {
            roomRepository.recordStudentPlacement((int) student.getUserId(), roomNumber);
            return "Başarılı: " + student.getFirstName() + " " + student.getLastName() + " odaya yerleştirildi. Oda doluluk: " + occupancyAfter + "/" + room.getCapacity();
        } else {
            return "Hata: Oda (" + roomNumber + ") dolu veya kapasiteye ulaşıldı. Yerleştirme yapılamadı. Oda durumu: " + room.getCurrentState().getClass().getSimpleName();
        }
    }

    public List<Room> listAllRooms() { return roomRepository.findAll(); }

    public List<User> getAllStudents() { return studentRepository.findAll(); }

    public Room getRoomDetails(String roomNumber) { return roomRepository.findByRoomNumber(roomNumber); }

    public List<User> searchUsers(SearchStrategy strategy, String searchTerm) {

        List<User> allUsers = studentRepository.findAll();
        searchContext.setStrategy(strategy);
        return searchContext.executeSearch(allUsers, searchTerm);
    }
}