package servlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabasePlay {

    public static String bob() throws ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306/javabase";
        String username = "java";
        String password = "password";

        System.out.println("Connecting database...");

        try {
            Class.forName("com.mysql.jdbc.Driver"); 
            
            Connection connection = DriverManager.getConnection(url, username, password);
            
            connection.close();
            return "Database connected!";
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }

    }

}
