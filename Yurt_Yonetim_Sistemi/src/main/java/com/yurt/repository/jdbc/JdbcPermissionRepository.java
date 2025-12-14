package com.yurt.repository.jdbc;

import com.yurt.model.Izin;
import com.yurt.repository.PermissionRepository;
import com.yurt.design.singleton.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcPermissionRepository implements PermissionRepository {

    private static JdbcPermissionRepository instance;

    private JdbcPermissionRepository() {}

    public static synchronized JdbcPermissionRepository getInstance() {
        if (instance == null) {
            instance = new JdbcPermissionRepository();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    @Override public List<Izin> findAll() { return getAllPermissions(); }
    @Override public Izin findById(int izinId) { return getIzinById(izinId); }
    @Override public List<Izin> findByOgrenciId(int ogrenciId) { return getPermissionsByStudentId(ogrenciId); }
    @Override public Izin save(Izin izin) { return savePermission(izin); }
    @Override public Izin updateStatus(int izinId, String newStatus, int personelId) { return updatePermissionStatus(izinId, newStatus, personelId); }
    @Override public List<Izin> findPendingPermissions() { return findPermissionsByStatus("Beklemede"); }
    @Override public List<Izin> findApprovedPermissions() { return findPermissionsByStatus("Onaylandı"); }
    @Override public List<Izin> findRejectedPermissions() { return findPermissionsByStatus("Reddedildi"); }

    private List<Izin> getAllPermissions() {
        List<Izin> izinler = new ArrayList<>();

        String sql = "SELECT i.*, k.ad, k.soyad, o.oda_numarasi " +
                "FROM Izinler i " +
                "LEFT JOIN Kullanicilar k ON i.izin_alan_id = k.kullanici_id " +
                "LEFT JOIN Ogrenci_odalari oo ON k.kullanici_id = oo.ogrenci_id AND oo.bitis_tarihi IS NULL " +
                "LEFT JOIN Odalar o ON oo.oda_id = o.oda_id " +
                "ORDER BY i.baslangic_tarihi DESC";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                izinler.add(createIzinFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("DB HATA (PERMISSION FIND ALL): " + e.getMessage());
            e.printStackTrace();
        }
        return izinler;
    }

    private Izin getIzinById(int izinId) {
        String sql = "SELECT i.*, k.ad, k.soyad, o.oda_numarasi FROM Izinler i LEFT JOIN Kullanicilar k ON i.izin_alan_id = k.kullanici_id LEFT JOIN Ogrenci_odalari oo ON k.kullanici_id = oo.ogrenci_id AND oo.bitis_tarihi IS NULL LEFT JOIN Odalar o ON oo.oda_id = o.oda_id WHERE i.izin_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, izinId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) { return createIzinFromResultSet(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private List<Izin> getPermissionsByStudentId(int ogrenciId) {
        String sql = "SELECT i.*, k.ad, k.soyad, o.oda_numarasi FROM Izinler i LEFT JOIN Kullanicilar k ON i.izin_alan_id = k.kullanici_id LEFT JOIN Ogrenci_odalari oo ON k.kullanici_id = oo.ogrenci_id AND oo.bitis_tarihi IS NULL LEFT JOIN Odalar o ON oo.oda_id = o.oda_id WHERE i.izin_alan_id = ? ORDER BY i.baslangic_tarihi DESC";
        List<Izin> izinler = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ogrenciId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                izinler.add(createIzinFromResultSet(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return izinler;
    }

    private Izin savePermission(Izin izin) {
        String sql = "INSERT INTO Izinler (izin_alan_id, baslangic_tarihi, bitis_tarihi, neden, durum) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, izin.getIzinAlanId());
            pstmt.setDate(2, Date.valueOf(izin.getBaslangicTarihi()));
            pstmt.setDate(3, Date.valueOf(izin.getBitisTarihi()));
            pstmt.setString(4, izin.getNeden());
            pstmt.setString(5, izin.getDurum());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        izin.setIzinId(rs.getInt(1));
                        return izin;
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private Izin updatePermissionStatus(int izinId, String newStatus, int personelId) {
        String sql = "UPDATE Izinler SET durum = ?, izin_veren_id = ?, islem_tarihi = ? WHERE izin_id = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, personelId);
            pstmt.setDate(3, Date.valueOf(LocalDate.now()));
            pstmt.setInt(4, izinId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) { return getIzinById(izinId); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private List<Izin> findPermissionsByStatus(String status) {
        String sql = "SELECT i.*, k.ad, k.soyad, o.oda_numarasi FROM Izinler i LEFT JOIN Kullanicilar k ON i.izin_alan_id = k.kullanici_id LEFT JOIN Ogrenci_odalari oo ON k.kullanici_id = oo.ogrenci_id AND oo.bitis_tarihi IS NULL LEFT JOIN Odalar o ON oo.oda_id = o.oda_id WHERE i.durum = ? ORDER BY i.baslangic_tarihi DESC";
        List<Izin> izinler = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                izinler.add(createIzinFromResultSet(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return izinler;
    }

    private Izin createIzinFromResultSet(ResultSet rs) throws SQLException {
        Izin izin = new Izin();
        izin.setIzinId(rs.getInt("izin_id"));
        izin.setIzinAlanId(rs.getInt("izin_alan_id"));
        izin.setBaslangicTarihi(rs.getDate("baslangic_tarihi").toLocalDate());
        izin.setBitisTarihi(rs.getDate("bitis_tarihi").toLocalDate());
        izin.setNeden(rs.getString("neden"));
        izin.setDurum(rs.getString("durum"));
        izin.setOgrenciAdi(rs.getString("ad") + " " + rs.getString("soyad"));
        izin.setOdaNumarasi(rs.getString("oda_numarasi"));

        return izin;
    }
}