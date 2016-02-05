package uk.ac.cam.quebec.dbwrapper;

import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;

import winterwell.jtwitter.Status;
import winterwell.jtwitter.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.String;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class which implements Database.
 * This is the internal singleton class which implements the functions in the
 * Database abstract class. For descriptions of what the functions do see
 * there. If you're not modifying the Database Wrapper, you shouldn't be here!
 *
 * Notes on concurrency: It appears to suggest here
 * (http://stackoverflow.com/q/1209693/1205923) that we are responsible for
 * ensuring only one thread accesses one Connection object at a time. For now,
 * I will be wrapping the one connection in a mutex.  When/If speed become an
 * issue, switching to a connection pool might be a good idea.
 *
 * @author Richard
 *
 */
class DatabaseInternal extends Database {

    private static final DatabaseInternal INSTANCE = new DatabaseInternal();

    private static final String username = "ibproject";
    private static final String dbserver = "jdbc:mysql://localhost:3306/ibprojectdb";
    private static String password;
    private static Object conMutex = new Object();
    Connection connection;

    private DatabaseInternal() {
        // Get password, open connection
        System.out.println(">Enter Password for Database:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            password = br.readLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(dbserver, username, password);
            // Might be able to lower this later
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(true);
            System.out.println(">Database Connected");
        }
        catch (SQLException e) {
            System.out.println(">Failed to connect to database");
        }

        // Create database tables if they don't exist
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS trends (" +
                    "name VARCHAR(60) NOT NULL," +
                    "location VARCHAR(60) NOT NULL," +
                    "updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "object MEDIUMBLOB NOT NULL," +
                    "trend_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY" +
                    ")");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    public void putTrend(Trend trend) {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(
                    "INSERT INTO trends(name, location, object) VALUES (?, ?, ?)");
            stmt.setString(1, trend.getName());
            stmt.setString(2, trend.getLocation());
            stmt.setObject(3, trend);
            synchronized(conMutex) {
                    stmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        return;
    }

    public List<Trend> getTrends() {
        ArrayList<Trend> result = new ArrayList<Trend>(3);
        return result;
    }

    public void putTweets(List<Status> tweets, Trend trend) {
        return;
    }

    public List<Status> getTweets(Trend trend) {
        ArrayList<Status> result = new ArrayList<Status>(3);
        return result;
    }

    public void putWikiArticles(List<WikiArticle> articles, Trend trend) {
        return;
    }

    public List<WikiArticle> getWikiArticles(Trend trend) {
        ArrayList<WikiArticle> result = new ArrayList<WikiArticle>(3);
        return result;
    }

}
