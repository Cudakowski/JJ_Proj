package com.jjproj.DatabaseIntegration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class MovesTable {
    public static boolean saveMove(int gameId, String from, String to, int movedPieceId, Integer capturedPieceId) {
        String sql = "INSERT INTO moves (game_id, move_from, move_to, moved_piece_id, captured_piece_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gameId);
            stmt.setString(2, from);
            stmt.setString(3, to);
            stmt.setInt(4, movedPieceId);
            
            if (capturedPieceId == null) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setInt(5, capturedPieceId);
            }
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Błąd zapisu ruchu do bazy: " + e.getMessage());
            return false;
        }
    }
}
