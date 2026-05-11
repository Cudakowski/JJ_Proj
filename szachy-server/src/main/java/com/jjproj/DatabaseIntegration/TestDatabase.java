package com.jjproj.DatabaseIntegration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

public class TestDatabase {

    public static void test(){
        
        try (Connection connection =  DatabaseManager.getConnection();) {
            
            System.out.println("connection.isValid(0) = " + connection.isValid(0));


            // select

            PreparedStatement selectPreparedStatement = connection.prepareStatement("select * from users where name = ?");
            selectPreparedStatement.setString(1, "Emilia");

            ResultSet resultSet = selectPreparedStatement.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getInt("user_id") + " - " + resultSet.getString("user_login") + " - " + resultSet.getString("user_password") );
            }

            // inserts

            PreparedStatement insertPreparedStatement = connection.prepareStatement("insert into users (user_login, user_password) values (?,?)");
            insertPreparedStatement.setString(1, "Mietek");
            insertPreparedStatement.setString(2, "abcd");
            int insertCount = insertPreparedStatement.executeUpdate();
            System.out.println("insert count = " + insertCount);

            // updates

            PreparedStatement updatePreparedStatement = connection.prepareStatement("update users set user_login = ? where name = ?");
            updatePreparedStatement.setString(1, "Ferdek");
            updatePreparedStatement.setString(2, "Mietek");
            int updateCount = updatePreparedStatement.executeUpdate();
            System.out.println("update count = " + updateCount);

            // deletes

            PreparedStatement deletePreparedStatement = connection.prepareStatement("delete from users where name = ?");
            deletePreparedStatement.setString(1, "Ferdek");
            int deleteCount = deletePreparedStatement.executeUpdate();
            System.out.println("delete count = " + deleteCount);


            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            AbandonedConnectionCleanupThread.checkedShutdown();
        }
    }
}
