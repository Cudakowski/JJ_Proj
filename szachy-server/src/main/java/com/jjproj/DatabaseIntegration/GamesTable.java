package com.jjproj.DatabaseIntegration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class GamesTable {
    public static Integer createNewGame(int whiteId, int blackId, String startFen) {
        String sql = "INSERT INTO games (game_fen, white_player_id, black_player_id) VALUES (?, ?, ?)";
        
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, startFen);
            stmt.setInt(2, whiteId);
            stmt.setInt(3, blackId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Błąd tworzenia gry w bazie: " + e.getMessage());
        }
        return null;
    }

    public static boolean updateGameFen(int gameId, String newFen) {
        String sql = "UPDATE games SET game_fen = ? WHERE game_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newFen);
            stmt.setInt(2, gameId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Błąd aktualizacji gry: " + e.getMessage());
            return false;
        }
    }

    //  isBlackWinner = false (Wygrał Biały), true (Wygrał Czarny), null (Remis)
    public static boolean setGameOver(int gameId, Boolean isBlackWinner) {
        String sql = "UPDATE games SET is_over = TRUE, winner_white_black = ? WHERE game_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Obsługa wartości NULL w przypadku remisu
            if (isBlackWinner == null) {
                stmt.setNull(1, Types.BOOLEAN);
            } else {
                stmt.setBoolean(1, isBlackWinner);
            }
            
            stmt.setInt(2, gameId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Błąd kończenia gry: " + e.getMessage());
            return false;
        }
    }

    public static String getPausedGamesForUser(int userId) {
        String sql = "SELECT g.game_id, g.white_player_id, g.black_player_id, " +
                     "uw.user_login AS white_name, ub.user_login AS black_name " +
                     "FROM games g " +
                     "JOIN users uw ON g.white_player_id = uw.user_id " +
                     "JOIN users ub ON g.black_player_id = ub.user_id " +
                     "WHERE g.is_over = FALSE AND (g.white_player_id = ? OR g.black_player_id = ?)";
                     
        StringBuilder result = new StringBuilder();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int gameId = rs.getInt("game_id");
                int whiteId = rs.getInt("white_player_id");
                
                String opponentName;
                String myColor;
                
                if (userId == whiteId) {
                    opponentName = rs.getString("black_name");
                    myColor = "Bialy";
                } else {
                    opponentName = rs.getString("white_name");
                    myColor = "Czarny";
                }
                
                if (result.length() > 0) result.append(",");
                result.append(gameId).append(":").append(opponentName).append(":").append(myColor);
            }
        } catch (SQLException e) {
            System.out.println("Błąd pobierania przerwanych gier: " + e.getMessage());
        }
        
        return result.toString();
    }

    public static String getGameFen(int gameId) {
        String sql = "SELECT game_fen FROM games WHERE game_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gameId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("game_fen");
            }
        } catch (SQLException e) {
            System.out.println("Błąd pobierania FEN: " + e.getMessage());
        }
        return null;
    }

    public static boolean isUserWhiteInGame(int gameId, int userId) {
        String sql = "SELECT white_player_id FROM games WHERE game_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gameId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int whitePlayerId = rs.getInt("white_player_id");
                return whitePlayerId == userId;
            }
        } catch (SQLException e) {
            System.out.println("Błąd weryfikacji koloru gracza: " + e.getMessage());
        }
        return false;
    }
}
