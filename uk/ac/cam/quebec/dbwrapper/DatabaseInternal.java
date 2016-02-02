package uk.ac.cam.quebec.dbwrapper;

import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;

import winterwell.jtwitter.Status;
import winterwell.jtwitter.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.String;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class which implements Database.
 * This is the internal singleton class which implements the functions in the
 * Database abstract class. For descriptions of what the functions do see
 * there. If you're not modifying the Database Wrapper, you shouldn't be here!
 *
 * @author Richard
 *
 */
class DatabaseInternal extends Database {
    private static final DatabaseInternal INSTANCE = new DatabaseInternal();
    private static final String username = "ibproject";
    private static final String dbserver = "jdbc:mysql://localhost:3306/ibprojectdb";
    private static String password;
    Connection connection;

    private DatabaseInternal() {
        // Get password
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
            System.out.println(">Database Connected");
        }
        catch (SQLException e) {
            System.out.println(">Failed to connect to database");
        }
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    public void putTrend(Trend trend) {
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
