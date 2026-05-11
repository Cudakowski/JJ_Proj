package com.jjproj.DatabaseIntegration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    
    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://localhost:3306/szachy");
            config.setUsername("root");
            config.setPassword("javaprojekt");

            // ustawienia
            config.setMaximumPoolSize(10); // Max otwartych połączeń naraz
            config.setMinimumIdle(2);      // Min otwartych połączeń naraz
            config.setConnectionTimeout(3000); // Zrezygnuj, jeśli baza nie odpowie w 3 sekundy

            dataSource = new HikariDataSource(config);
            System.out.println("Uruchomiono pule polaczen");
        } catch (Exception e) {
            System.out.println("Nie uruchomiono puli polaczen");
            e.printStackTrace();
        }
        
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Pula polaczen z baza danych zostala zamknieta");
        }
    }
}
