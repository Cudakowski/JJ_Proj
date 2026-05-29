package com.jjproj.DatabaseIntegration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsRepository {

    // Pobiera historię zakończonych gier dla użytkownika
    public static String getGameHistory(int userId) {
        String sql = "SELECT g.game_id, g.white_player_id, g.black_player_id, g.winner_white_black, " +
                     "uw.user_login AS white_name, ub.user_login AS black_name " +
                     "FROM games g " +
                     "JOIN users uw ON g.white_player_id = uw.user_id " +
                     "JOIN users ub ON g.black_player_id = ub.user_id " +
                     "WHERE g.is_over = TRUE AND (g.white_player_id = ? OR g.black_player_id = ?) " +
                     "ORDER BY g.game_id DESC LIMIT 20"; // Pobieramy 20 ostatnich partii
                     
        StringBuilder result = new StringBuilder();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int gameId = rs.getInt("game_id");
                int whiteId = rs.getInt("white_player_id");
                
                String opponentName = (userId == whiteId) ? rs.getString("black_name") : rs.getString("white_name");
                Boolean isBlackWinner = rs.getObject("winner_white_black") != null ? rs.getBoolean("winner_white_black") : null;
                
                String outcome;
                if (isBlackWinner == null) {
                    outcome = "Remis";
                } else if ((userId == whiteId && !isBlackWinner) || (userId != whiteId && isBlackWinner)) {
                    outcome = "Wygrana";
                } else {
                    outcome = "Przegrana";
                }
                
                if (result.length() > 0) result.append(",");
                result.append(gameId).append(":").append(opponentName).append(":").append(outcome);
            }
        } catch (SQLException e) {
            System.out.println("Błąd historii gier: " + e.getMessage());
        }
        return result.toString();
    }

    // Pobiera zagregowane statystyki
    public static String getUserStats(int userId) {
        int totalGames = 0, wins = 0, draws = 0, capturedPieces = 0;

        // 1. Zliczanie gier i wyników
        String sqlGames = "SELECT white_player_id, winner_white_black FROM games " +
                          "WHERE is_over = TRUE AND (white_player_id = ? OR black_player_id = ?)";
                          
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlGames)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                totalGames++;
                int whiteId = rs.getInt("white_player_id");
                Boolean isBlackWinner = rs.getObject("winner_white_black") != null ? rs.getBoolean("winner_white_black") : null;
                
                if (isBlackWinner == null) draws++;
                else if ((userId == whiteId && !isBlackWinner) || (userId != whiteId && isBlackWinner)) wins++;
            }
        } catch (SQLException e) {
            System.out.println("Błąd statystyk gier: " + e.getMessage());
        }

        int losses = totalGames - wins - draws;
        int winrate = totalGames > 0 ? (int) Math.round((wins * 100.0) / totalGames) : 0;

        // 2. Zliczanie zbitych figur
        String sqlCaptures = "SELECT COUNT(m.captured_piece_id) AS total_captured FROM moves m " +
                             "JOIN games g ON m.game_id = g.game_id " +
                             "JOIN pieces p ON m.moved_piece_id = p.piece_id " +
                             "WHERE m.captured_piece_id IS NOT NULL AND " +
                             "((g.white_player_id = ? AND p.is_white_black = 0) OR " +
                             "(g.black_player_id = ? AND p.is_white_black = 1))";
                             
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCaptures)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) capturedPieces = rs.getInt("total_captured");
            
        } catch (SQLException e) {
            System.out.println("Błąd zliczania bicia: " + e.getMessage());
        }

        // Zwracamy paczkę danych rozdzieloną przecinkami
        return totalGames + "," + winrate + "%," + wins + "," + draws + "," + losses + "," + capturedPieces;
    }
}