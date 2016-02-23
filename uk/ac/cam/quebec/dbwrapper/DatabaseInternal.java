package uk.ac.cam.quebec.dbwrapper;

import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;

import winterwell.jtwitter.Status;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which implements Database.
 * This is the internal singleton class which implements the functions in the
 * Database abstract class. For descriptions of what the functions do see
 * there. If you're not modifying the Database Wrapper, you shouldn't be here!
 *
 * <p>Notes on concurrency: It appears to suggest here
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

    private static Object conMutex = new Object();
    Connection connection;

    private DatabaseInternal() {
        // Get credentials (if needed), open connection
        if (username == null) {
            System.out.println("> Enter Username for Database:");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                username = br.readLine();
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }
        if (password == null) {
            System.out.println("> Enter Password for Database:");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                password = br.readLine();
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }
        if (dbserver == null) {
            System.out.println("> Enter Location for Database (in form "
                    + "jdbc:mysql://localhost:3306/ibprojectdb):");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                dbserver = br.readLine();
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }

        try {
            connection = DriverManager.getConnection(dbserver, username, password);
            connection.setClientInfo("useUnicode", "true");
            connection.setClientInfo("characterEncoding", "utf8mb4");
            connection.setClientInfo("connectionCollation", "utf8mb4_general_ci");
            connection.setClientInfo("characterSetResults", "utf8mb4");
            // Might be able to lower this later
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(true);
            System.out.println("> Database Connected");
        } catch (SQLException exp) {
            exp.printStackTrace();
            System.out.println("> Failed to connect to database");
            return;
        }

        // Create database tables if they don't exist
        Statement stmt = null;
        String s;
        try {
            stmt = connection.createStatement();
            // Drop the tables on startup, clears any data in db!
            if (dropTables) {
                stmt.execute(s = "DROP tables IF EXISTS trends, wikiarticles, tweets, "
                        + "trends_wikiarticles_junction");
            }
            
            // trends
            stmt.execute(s = "CREATE TABLE IF NOT EXISTS trends ("
                    + "name VARCHAR(60) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL,"
                    + "location VARCHAR(60) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci',"
                    + "updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "object MEDIUMBLOB NOT NULL,"
                    + "trend_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY) CHARACTER SET 'utf8mb4'");
            
            // tweets
            stmt.execute(s = "CREATE TABLE IF NOT EXISTS tweets ("
                    + "content VARCHAR(200) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL,"
                    + "location VARCHAR(60) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci',"
                    + "updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "object MEDIUMBLOB NOT NULL,"
                    + "trend_id INT UNSIGNED NOT NULL,"
                    + "tweet_id BIGINT UNSIGNED NOT NULL PRIMARY KEY)CHARACTER SET 'utf8mb4'");
            // wikiarticles
            stmt.execute(s = "CREATE TABLE IF NOT EXISTS wikiarticles ("
                    + "title VARCHAR(300) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL,"
                    + "updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "relevance DOUBLE NOT NULL,"
                    + "popularity DOUBLE NOT NULL,"
                    + "controversy DOUBLE NOT NULL,"
                    + "recency DOUBLE NOT NULL,"
                    + "object MEDIUMBLOB NOT NULL,"
                    + "wikiarticle_id INT UNSIGNED NOT NULL PRIMARY KEY) CHARACTER SET 'utf8mb4'");
            // trends_wikiarticles_junction
            stmt.execute(s = "CREATE TABLE IF NOT EXISTS trends_wikiarticles_junction ("
                    + "trend_id INT UNSIGNED NOT NULL,"
                    + "wikiarticle_id INT UNSIGNED NOT NULL,"
                    + "PRIMARY KEY(trend_id, wikiarticle_id)) CHARACTER SET 'utf8mb4'");
        } catch (SQLException exp) {
            exp.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    @Override
    public void putTrend(Trend trend) throws DatabaseException {
        PreparedStatement stmt = null;

        try {
            stmt = connection.prepareStatement("INSERT INTO trends"
                    + "(name, location, object, trend_id) VALUES (?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE name = VALUES(name), "
                    + "location = VALUES(location), object = VALUES(object), "
                    + "trend_id = VALUES(trend_id)");
            stmt.setString(1, trend.getName());
            stmt.setString(2, trend.getLocation());
            stmt.setObject(3, trend);
            stmt.setInt(4, trend.getId());

            synchronized (conMutex) {
                // If ID hasn't been set before, we need to set it
                if (trend.getId() == 0) {
                    Statement ss = connection.createStatement();
                    ResultSet rs = ss.executeQuery("SELECT MAX(trend_id) FROM trends");
                    rs.first();
                    trend.setId(rs.getInt(1) + 1, accessId);
                    rs.close();
                    ss.close();
                    stmt.setObject(3, trend);
                    stmt.setInt(4, trend.getId());
                }
                stmt.executeUpdate();
            }
        } catch (SQLException exp) {
            throw new DatabaseException("SQL failed to insert Trend into the database", exp);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        return;
    }

    @Override
    public List<Trend> getTrends() throws DatabaseException {
        // Pass wildcard as location to get all trends
        return getTrends("%");
    }

    @Override
    public List<Trend> getTrends(String location) throws DatabaseException {
        ArrayList<Trend> result = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Creates forward & read only ResultSet by default
            stmt = connection.createStatement();
            synchronized (conMutex) {
                rs = stmt.executeQuery("SELECT object FROM trends "
                        + "WHERE location LIKE \"" + location + "\"");
            }

            while (rs.next()) {
                byte[] buffer = rs.getBytes(1);
                ObjectInputStream obj = new ObjectInputStream(new ByteArrayInputStream(buffer));
                result.add((Trend) obj.readObject());
            }

        } catch (SQLException | IOException | ClassNotFoundException exp) {
            throw new DatabaseException("SQL failed to get Trends from database", exp);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        return result;
    }

    @Override
    public void putTweets(List<Status> tweets, Trend trend) throws DatabaseException {
        // In case this trend hasn't been put in the db before
        if (trend.getId() == 0) {
            putTrend(trend);
        }

        PreparedStatement stmt = null;

        try {
            synchronized (conMutex) {
                // require for batch update
                connection.setAutoCommit(false);
                stmt = connection.prepareStatement("INSERT INTO tweets (content,"
                        + "location, object, trend_id, tweet_id) VALUES (?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE content = VALUES(content), "
                        + "location = VALUES(location), object = VALUES(object),"
                        + "trend_id = VALUES(trend_id), tweet_id = VALUES(tweet_id)");

                int num = 0;
                for (Status tw : tweets) {
                    stmt.setString(1, tw.getText());
                    stmt.setString(2, tw.getLocation());
                    stmt.setObject(3, tw);
                    stmt.setInt(4, trend.getId());
                    stmt.setString(5, tw.getId().toString());
                    stmt.addBatch();
                    num++;

                    // Some DB drivers don't like big batches
                    if (num % 1000 == 0) {
                        stmt.executeBatch();
                    }
                }
                stmt.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        } catch (SQLException exp) {
            throw new DatabaseException("SQL failed to insert all Tweets into the database", exp);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        return;
    }

    @Override
    public List<Status> getTweets(Trend trend) throws DatabaseException {
        return getTweets(trend.getId());
    }

    @Override
    public List<Status> getTweets(int trend_id) throws DatabaseException {
        ArrayList<Status> result = new ArrayList<Status>();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Creates forward & read only ResultSet by default
            stmt = connection.createStatement();
            synchronized (conMutex) {
                rs = stmt.executeQuery("SELECT object FROM tweets WHERE "
                        + "trend_id = " + trend_id);
            }

            while (rs.next()) {
                byte[] buffer = rs.getBytes(1);
                ObjectInputStream obj = new ObjectInputStream(new ByteArrayInputStream(buffer));
                result.add((Status) obj.readObject());
            }

        } catch (SQLException | IOException | ClassNotFoundException exp) {
            throw new DatabaseException("SQL failed to get Tweets from database", exp);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        return result;
    }

    /*
     * WikiArticle put method
    */
    private static final String WIKI_ARTICLE_INSERT = "INSERT INTO wikiarticles "
        + "(title, relevance, popularity, controversy, recency, object, wikiarticle_id) "
        + "  VALUES (?, ?, ?, ?, ?, ?, ?) "
        + "ON DUPLICATE KEY UPDATE "
        + "  title = VALUES(title), relevance = VALUES(relevance), "
        + "  popularity = VALUES(popularity), controversy = VALUES(controversy), "
        + "  recency = VALUES(recency), object = VALUES(object), "
        + "  wikiarticle_id = VALUES(wikiarticle_id)";

    @Override
    public void putWikiArticles(List<WikiArticle> articles, Trend trend) throws DatabaseException {
        // In case this trend hasn't been put in the db before
        if (trend.getId() == 0) {
            putTrend(trend);
        }

        PreparedStatement stmt = null;

        try {
            synchronized (conMutex) {
                // require for batch update
                connection.setAutoCommit(false);

                // Insert articles
                stmt = connection.prepareStatement(WIKI_ARTICLE_INSERT);

                int num = 0;
                for (WikiArticle wk : articles) {
                    stmt.setString(1, wk.getTitle());
                    stmt.setDouble(2, wk.getRelevance());
                    stmt.setDouble(3, wk.getPopularity());
                    stmt.setDouble(4, wk.getControversy());
                    stmt.setDouble(5, wk.getRecency());
                    stmt.setObject(6, wk);
                    stmt.setInt(7, wk.getId());
                    stmt.addBatch();
                    num++;
                    // Some DB drivers don't like big batches
                    if (num % 1000 == 0) {
                        stmt.executeBatch();
                    }
                }
                stmt.executeBatch();
                connection.commit();
                try { if (stmt != null) stmt.close(); } catch (Exception e) {};

                // Insert which trend they relate to (update clause is just to
                // ignore, duplicate entries)
                stmt = connection.prepareStatement("INSERT INTO "
                        + "trends_wikiarticles_junction (trend_id, wikiarticle_id) "
                        + "VALUES (?, ?) ON DUPLICATE KEY UPDATE trend_id = VALUES(trend_id)");

                num = 0;
                for (WikiArticle wk : articles) {
                    stmt.setInt(1, trend.getId());
                    stmt.setInt(2, wk.getId());
                    stmt.addBatch();
                    num++;
                    // Some DB drivers don't like big batches
                    if (num % 1000 == 0) {
                        stmt.executeBatch();
                    }
                }
                stmt.executeBatch();

                connection.commit();
                connection.setAutoCommit(true);
            }
        } catch (SQLException exp) {
            throw new DatabaseException("SQL failed to insert all WikiArticles "
                    + "into the database", exp);
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }
        return;
    }

    /*
     * WikiArticle get methods
    */
    private static final String WIKI_ARTICLE_SELECT = "SELECT object "
        + "FROM wikiarticles INNER JOIN trends_wikiarticles_junction "
        + "ON wikiarticles.wikiarticle_id = trends_wikiarticles_junction.wikiarticle_id "
        + "WHERE trend_id = ";
    private static final String WIKI_ARTICLE_SORT_RELEVANCE =
        " ORDER BY wikiarticles.relevance DESC";
    private static final String WIKI_ARTICLE_SORT_POPULARITY =
        " ORDER BY wikiarticles.popularity DESC";
    private static final String WIKI_ARTICLE_SORT_CONTROVERSY =
        " ORDER BY wikiarticles.controversy DESC";
    private static final String WIKI_ARTICLE_SORT_RECENCY =
        " ORDER BY wikiarticles.recency DESC";

    //Private method
    private List<WikiArticle> getWikiArticlesByQuery(String query) throws DatabaseException {
        ArrayList<WikiArticle> result = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Creates forward & read only ResultSet by default
            stmt = connection.createStatement();

            synchronized (conMutex) {
                rs = stmt.executeQuery(query);
            }

            while (rs.next()) {
                byte[] buffer = rs.getBytes(1);
                ObjectInputStream obj = new ObjectInputStream(new ByteArrayInputStream(buffer));
                result.add((WikiArticle) obj.readObject());
            }

        } catch (SQLException | IOException | ClassNotFoundException exp) {
            throw new DatabaseException("SQL failed to get WikiArticles from database", exp);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {};
            try { if (stmt != null) stmt.close(); } catch (Exception e) {};
        }

        return result;
    }

    // Public-facing methods
    @Override
    public List<WikiArticle> getWikiArticles(int trend_id) throws DatabaseException {
        return getWikiArticlesByQuery(WIKI_ARTICLE_SELECT + trend_id
                + WIKI_ARTICLE_SORT_RELEVANCE);
    }

    @Override
    public List<WikiArticle> getWikiArticles(Trend trend) throws DatabaseException {
        return getWikiArticles(trend.getId());
    }

    @Override
    public List<WikiArticle> getWikiArticlesByPopularity(int trend_id) throws DatabaseException {
        return getWikiArticlesByQuery(WIKI_ARTICLE_SELECT + trend_id
                + WIKI_ARTICLE_SORT_POPULARITY);
    }

    @Override
    public List<WikiArticle> getWikiArticlesByPopularity(Trend trend) throws DatabaseException {
        return getWikiArticlesByPopularity(trend.getId());
    }

    @Override
    public List<WikiArticle> getWikiArticlesByControversy(int trend_id) throws DatabaseException {
        return getWikiArticlesByQuery(WIKI_ARTICLE_SELECT + trend_id
                + WIKI_ARTICLE_SORT_CONTROVERSY);
    }

    @Override
    public List<WikiArticle> getWikiArticlesByControversy(Trend trend) throws DatabaseException {
        return getWikiArticlesByControversy(trend.getId());
    }

    @Override
    public List<WikiArticle> getWikiArticlesByRecency(int trend_id) throws DatabaseException {
        return getWikiArticlesByQuery(WIKI_ARTICLE_SELECT + trend_id
                + WIKI_ARTICLE_SORT_RECENCY);
    }

    @Override
    public List<WikiArticle> getWikiArticlesByRecency(Trend trend) throws DatabaseException {
        return getWikiArticlesByRecency(trend.getId());
    }
}
