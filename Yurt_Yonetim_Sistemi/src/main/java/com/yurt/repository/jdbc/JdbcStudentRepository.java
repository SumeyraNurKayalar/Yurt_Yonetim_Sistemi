package com.yurt.repository.jdbc;

import com.yurt.repository.StudentRepository;
import com.yurt.model.User;
import com.yurt.DTO.OdaBilgisiDTO;
import com.yurt.design.singleton.DatabaseConnection;
import com.yurt.security.PasswordHasher;
import com.yurt.design.factory.UserFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcStudentRepository implements StudentRepository {

    private static JdbcStudentRepository instance;
    private JdbcStudentRepository() {}
    public static JdbcStudentRepository getInstance() {
        if (instance == null) {
            instance = new JdbcStudentRepository();
        }
        return instance;
    }
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    private User mapUser(ResultSet rs) throws SQLException {
        String role = rs.getString("rol");

        String roomNumber = null;
        int roomId = 0;

        try {

            rs.findColumn("oda_numarasi");
            roomNumber = rs.getString("oda_numarasi");
            rs.findColumn("oda_id");
            roomId = rs.getInt("oda_id");
        } catch (SQLException ignored) {
        }

        return UserFactory.createUserFromResultSet(rs, role, roomNumber, roomId);
    }

    @Override
    public User kullaniciGirisYap(String giris, String sifre) {
        String sql = "SELECT * FROM Kullanicilar WHERE email = ? OR tc = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, giris);
            pstmt.setString(2, giris);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapUser(rs);
                    String hashedPassword = user.getPasswordHash();

                    if (PasswordHasher.checkPassword(sifre, hashedPassword)) {
                        return user;
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT K.*, O.oda_numarasi, O.oda_id FROM Kullanicilar K " +
                "LEFT JOIN Ogrenci_odalari OO ON K.kullanici_id = OO.ogrenci_id AND OO.bitis_tarihi IS NULL " +
                "LEFT JOIN Odalar O ON OO.oda_id = O.oda_id WHERE K.kullanici_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean profilGuncelle(int kullaniciId, String ad, String soyad, String email,
                                  String telefon, String adres, String yeniSifre) {
        String yeniSifreHash = (yeniSifre != null && !yeniSifre.isEmpty()) ? yeniSifre : null;
        String sql;
        if (yeniSifreHash != null) {
            sql = "UPDATE Kullanicilar SET ad = ?, soyad = ?, email = ?, telefon = ?, adres = ?, sifre_hash = ? WHERE kullanici_id = ?";
        } else {
            sql = "UPDATE Kullanicilar SET ad = ?, soyad = ?, email = ?, telefon = ?, adres = ? WHERE kullanici_id = ?";
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ad);
            pstmt.setString(2, soyad);
            pstmt.setString(3, email);
            pstmt.setString(4, telefon);
            pstmt.setString(5, adres);

            if (yeniSifreHash != null) {
                pstmt.setString(6, yeniSifreHash);
                pstmt.setInt(7, kullaniciId);
            } else {
                pstmt.setInt(6, kullaniciId);
            }

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Profil güncellenirken SQL hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean kayitEkle(String ad, String soyad, String tc, String email, String telefon,
                             String adres, String sifre, String rol) {
        String kullaniciAdi_DB = email;
        String passwordHash = sifre;

        try {
            createUser(ad, soyad, tc, kullaniciAdi_DB, telefon, adres, rol, email, passwordHash);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void createUser(String ad, String soyad, String tc, String kullaniciAdi, String telefon,
                           String adres, String rol, String email, String passwordHash) {

        String sql = "INSERT INTO Kullanicilar (ad, soyad, tc, kullanici_adi, telefon, adres, rol, email, sifre_hash) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ad);
            pstmt.setString(2, soyad);
            pstmt.setString(3, tc);
            pstmt.setString(4, kullaniciAdi);
            pstmt.setString(5, telefon);
            pstmt.setString(6, adres);
            pstmt.setString(7, rol);
            pstmt.setString(8, email);
            pstmt.setString(9, passwordHash);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Kullanıcı oluşturulurken SQL hatası: " + e.getMessage());
            throw new RuntimeException("Kullanıcı ekleme başarısız.", e);
        }
    }

    @Override
    public boolean isStudentCurrentlyPlaced(long userId) {
        String sql = "SELECT COUNT(*) FROM Ogrenci_odalari WHERE ogrenci_id = ? AND bitis_tarihi IS NULL";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Yerleşim kontrolü sırasında SQL hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public OdaBilgisiDTO getOdaVeArkadasBilgileri(int ogrenciId) {
        int odaId = -1;
        String odaNumarasi = null;
        List<User> odaArkadaslari = new ArrayList<>();

        String sqlOda = "SELECT O.oda_id, O.oda_numarasi FROM Odalar O JOIN Ogrenci_odalari OO ON O.oda_id = OO.oda_id WHERE OO.ogrenci_id = ? AND OO.bitis_tarihi IS NULL";

        try (Connection conn = getConnection(); PreparedStatement pstmtOda = conn.prepareStatement(sqlOda)) {
            pstmtOda.setInt(1, ogrenciId);
            try (ResultSet rsOda = pstmtOda.executeQuery()) {
                if (rsOda.next()) {
                    odaId = rsOda.getInt("oda_id");
                    odaNumarasi = rsOda.getString("oda_numarasi");
                } else { return null; }
            }
        } catch (SQLException e) { System.err.println("Oda bilgisi çekilirken SQL hatası: " + e.getMessage()); return null; }

        if (odaId != -1) {
            String sqlArkadaslar = "SELECT K.* FROM Kullanicilar K JOIN Ogrenci_odalari OO ON K.kullanici_id = OO.ogrenci_id WHERE OO.oda_id = ? AND K.kullanici_id != ? AND OO.bitis_tarihi IS NULL";

            try (Connection conn = getConnection(); PreparedStatement pstmtArkadaslar = conn.prepareStatement(sqlArkadaslar)) {
                pstmtArkadaslar.setInt(1, odaId);
                pstmtArkadaslar.setInt(2, ogrenciId);
                try (ResultSet rsArkadaslar = pstmtArkadaslar.executeQuery()) {
                    while (rsArkadaslar.next()) {
                        User arkadas = mapUser(rsArkadaslar);
                        odaArkadaslari.add(arkadas);
                    }
                }
            } catch (SQLException e) { System.err.println("Oda arkadaşları çekilirken SQL hatası: " + e.getMessage()); }
        }
        return new OdaBilgisiDTO(odaNumarasi, odaArkadaslari);
    }

    @Override public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT K.*, O.oda_numarasi, O.oda_id " +
                "FROM Kullanicilar K " +
                "LEFT JOIN Ogrenci_odalari OO ON K.kullanici_id = OO.ogrenci_id AND OO.bitis_tarihi IS NULL " +
                "LEFT JOIN Odalar O ON OO.oda_id = O.oda_id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    @Override public User findByEmail(String email) {
        String sql = "SELECT K.*, O.oda_numarasi, O.oda_id FROM Kullanicilar K " +
                "LEFT JOIN Ogrenci_odalari OO ON K.kullanici_id = OO.ogrenci_id AND OO.bitis_tarihi IS NULL " +
                "LEFT JOIN Odalar O ON OO.oda_id = O.oda_id WHERE K.email = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { return mapUser(rs); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    @Override public User findByTc(String tc) {
        String sql = "SELECT K.*, O.oda_numarasi, O.oda_id FROM Kullanicilar K " +
                "LEFT JOIN Ogrenci_odalari OO ON K.kullanici_id = OO.ogrenci_id AND OO.bitis_tarihi IS NULL " +
                "LEFT JOIN Odalar O ON OO.oda_id = O.oda_id WHERE K.tc = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tc);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { return mapUser(rs); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    @Override public List<User> findByOdaId(int odaId) { return new ArrayList<>(); }
}