package com.yurt.design.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=YurtYonetimDB;encrypt=false;trustServerCertificate=true";

    private static final String USER = "Vincenza";
    private static final String PASSWORD = "123456";

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("📌 SQL Server bağlantısı başarılı!");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("❌ Bağlantı hatası: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Bağlantı kapatıldı.");
            }
        } catch (SQLException e) {
            System.out.println("❗ Kapatma hatası: " + e.getMessage());
        }
    }
}