package com.jjproj.DatabaseIntegration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PiecesTable {
    public static Integer getPieceId(char symbol, boolean isBlack) {
        String sql = "SELECT piece_id FROM pieces WHERE piece_symbol = ? AND is_white_black = ?";
        
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, String.valueOf(symbol));
            stmt.setInt(2, isBlack ? 1 : 0);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("piece_id");
            }
        } catch (SQLException e) {
            System.out.println("Błąd pobierania figury: " + e.getMessage());
        }
        return null;
    }
}
