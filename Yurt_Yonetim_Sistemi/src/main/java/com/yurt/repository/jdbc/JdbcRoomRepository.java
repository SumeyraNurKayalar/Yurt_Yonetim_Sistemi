package com.yurt.repository.jdbc;

import com.yurt.design.singleton.DatabaseConnection;
import com.yurt.repository.RoomRepository;
import com.yurt.model.Room;
import com.yurt.design.state.AvailableState;
import com.yurt.design.state.FullState;

import java.sql.*;
import java.util.*;

public class JdbcRoomRepository implements RoomRepository {

    private static JdbcRoomRepository instance;
    private JdbcRoomRepository() {}
    public static JdbcRoomRepository getInstance() {
        if (instance == null) {
            instance = new JdbcRoomRepository();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    @Override
    public Room save(Room room) {
        String sql = "INSERT INTO Odalar (oda_numarasi, kapasite) VALUES (?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, room.getRoomNumber());
            pstmt.setInt(2, room.getCapacity());
            pstmt.executeUpdate();
            return room;

        } catch (SQLException e) {
            System.err.println("DB HATA (SAVE): Oda kaydedilemedi: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Room findByRoomNumber(String roomNumber) {
        String sql = "SELECT oda_id, oda_numarasi, kapasite FROM Odalar WHERE oda_numarasi = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setString(1, roomNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int capacity = rs.getInt("kapasite");
                    Room room = new Room(roomNumber, capacity);

                    int occupancy = countOccupantsByRoomNumber(roomNumber);
                    room.setCurrentOccupancy(occupancy);

                    if (occupancy == capacity) {
                        room.setState(new FullState(room));
                    } else {
                        room.setState(new AvailableState(room));
                    }
                    return room;
                }
            }
        } catch (SQLException e) {
            System.err.println("DB HATA (FIND): Oda bulunamadı: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Room> findAll() {
        String sql = "SELECT oda_numarasi, kapasite FROM Odalar";
        List<Room> rooms = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String roomNumber = rs.getString("oda_numarasi");
                int capacity = rs.getInt("kapasite");
                Room room = new Room(roomNumber, capacity);

                int occupancy = countOccupantsByRoomNumber(roomNumber);
                room.setCurrentOccupancy(occupancy);

                if (occupancy == capacity) {
                    room.setState(new FullState(room));
                } else {
                    room.setState(new AvailableState(room));
                }
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("DB HATA (FIND ALL): Tüm odaları listeleme hatası: " + e.getMessage());
        }
        return rooms;
    }

    @Override
    public int countOccupantsByRoomNumber(String roomNumber) {
        String sql = "SELECT COUNT(oa.ogrenci_id) FROM Ogrenci_odalari oa " +
                "JOIN Odalar o ON oa.oda_id = o.oda_id " +
                "WHERE o.oda_numarasi = ? AND oa.bitis_tarihi IS NULL";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setString(1, roomNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("DB HATA (COUNT): Doluluk hesaplanamadı: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public void update(Room room) {
        String sql = "UPDATE Odalar SET kapasite = ? WHERE oda_numarasi = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setInt(1, room.getCapacity());
            pstmt.setString(2, room.getRoomNumber());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DB HATA (UPDATE): " + e.getMessage());
        }
    }

    @Override
    public void recordStudentPlacement(int studentId, String roomNumber) {
        String sql = "INSERT INTO Ogrenci_odalari (ogrenci_id, oda_id, baslangic_tarihi) " +
                "SELECT ?, oda_id, GETDATE() FROM Odalar WHERE oda_numarasi = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setString(2, roomNumber);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("DB HATA (PLACE): Öğrenci yerleştirme kaydı oluşturulamadı: " + e.getMessage());
        }
    }
}