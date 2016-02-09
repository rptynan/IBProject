package uk.ac.cam.quebec.dbwrapper;

import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;

import winterwell.jtwitter.Status;

import java.util.List;

/**
 * Class which wraps all interactions with the database.
 * Everything here should be thread-safe.
 *
 * @author Richard
 *
 */
public abstract class Database {

    protected static String username = null;
    protected static String dbserver = null;
    protected static String password = null;

    /**
     * Used for accessing setId() methods on Trends, WikiArticles and Tweets.
     */
    public static final class AccessId {
        private AccessId() {}
    }

    protected static final AccessId accessId = new AccessId();

    /**
     * Gets an instance of the database wrapper.
     *
     * @return  an object which implements the Database interface which should
     *          be used for calls
     */
    public static Database getInstance() {
        return DatabaseInternal.getInstance();
    }

    /**
     * Sets the username, password and database location of all instance(s) to
     * be created.
     *
     * <p><b>Only to be called once by the startup code!</b>
     *
     * @param usernm    a string containing the username
     * @param passwd    a string containing the password
     * @param loctn     a string containing the database location, e.g.
     *                  jdbc:mysql://localhost:3306/ibprojectdb
     */
    public static void setCredentials(String usernm, String passwd, String loctn) {
        username = usernm;
        password = passwd;
        dbserver = loctn;
    }

    /**
     * Puts a single trend into the database.
     *
     * <p>This should be called before trying to store any tweets or articles
     * under this trend, although (hopefully) if you don't do this, the other
     * functions should cause this to be called..
     *
     * @param trend     single Trend to put in the database
     */
    public abstract void putTrend(Trend trend) throws DatabaseException;

    /**
     * Gets a list of all the trends in the Database.
     *
     * @return a list of Trend objects from the database
     */
    public abstract List<Trend> getTrends() throws DatabaseException;

    /**
     * Stores a list of tweets with reference to the given Trend.
     *
     * @param tweets    a list of jtwitter Status objects, representing the
     *                  tweets to put in the database
     * @param trend     the trend to store these tweets under
     */
    public abstract void putTweets(List<Status> tweets, Trend trend) throws DatabaseException;

    /**
     * Gets a list of tweets with reference to the given Trend.
     *
     * @param trend     the trend the tweets are stored under
     *
     * @return a list of jtwitter Status Objects representing the tweets
     */
    public abstract List<Status> getTweets(Trend trend) throws DatabaseException;

    /**
     * Stores a list of wikipedia articles with reference to the given Trend.
     *
     * @param articles  a list of WikiArticles, to put in the database
     * @param trend     the trend to store these articles under
     */
    public abstract void putWikiArticles(List<WikiArticle> articles, Trend trend)
        throws DatabaseException;

    /**
     * Gets a list of wikipedia articles with reference to the given Trend.
     *
     * @param trend     the trend the articles are stored under
     *
     * @return a list of the WikiArticles
     */
    public abstract List<WikiArticle> getWikiArticles(Trend trend) throws DatabaseException;

}
