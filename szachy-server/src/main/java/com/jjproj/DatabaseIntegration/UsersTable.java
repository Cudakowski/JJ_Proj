package com.jjproj.DatabaseIntegration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersTable {

    public static boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (user_login, user_password) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; 

        } catch (SQLException e) {
            System.out.println("Błąd rejestracji " + e.getMessage());
            return false;
        }
    }

    public static boolean checkLogin(String username, String password) {
        String sql = "SELECT user_password FROM users WHERE user_login = ?";

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String dbPassword = rs.getString("user_password");
                return dbPassword.equals(password);
            }

        } catch (SQLException e) {
            System.out.println("Błąd bazy danych: " + e.getMessage());
        }
        return false;
    }
}
