package com.yurt.design.factory;

import com.yurt.model.User;
import com.yurt.model.Student;
import com.yurt.model.Staff;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserFactory {

    public static User createUserFromResultSet(
            ResultSet rs, String role, String roomNumber, int roomId) throws SQLException {

        long userId = rs.getLong("kullanici_id");
        String firstName = rs.getString("ad");
        String lastName = rs.getString("soyad");
        String tcNumber = rs.getString("tc");
        String email = rs.getString("email");
        String phone = rs.getString("telefon");
        String address = rs.getString("adres");
        String passwordHash = rs.getString("sifre_hash");

        if ("Ogrenci".equalsIgnoreCase(role)) {
            Student s = new Student.StudentBuilder()
                    .userId(userId).firstName(firstName).lastName(lastName)
                    .tcNumber(tcNumber).email(email).phone(phone).address(address)
                    .role(role).passwordHash(passwordHash)
                    .currentRoomNumber(roomNumber != null ? roomNumber : "Yok")
                    .roomId(roomId)
                    .build();
            return s;

        } else if ("Personel".equalsIgnoreCase(role) || "Yonetici".equalsIgnoreCase(role)) {
            Staff s = new Staff.StaffBuilder()
                    .userId(userId).firstName(firstName).lastName(lastName)
                    .tcNumber(tcNumber).email(email).phone(phone).address(address)
                    .role(role).passwordHash(passwordHash)
                    .build();
            return s;
        }

        throw new IllegalArgumentException("Geçersiz kullanıcı rolü: " + role);
    }
}