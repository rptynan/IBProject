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
    protected static boolean dropTables = false;

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
     * @param dropTbls  a bool, if true the database will be cleared on
     *                  startup (dropped tables), else it won't be cleared
     */
    public static void setCredentials(String usernm, String passwd, String loctn,
            boolean dropTbls) {
        username = usernm;
        password = passwd;
        dbserver = loctn;
        dropTables = dropTbls;
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
     * Gets a list of all the trends in the Database for a specific location.
     *
     * @param location  a location string
     *
     * @return a list of Trend objects from the database
     */
    public abstract List<Trend> getTrends(String location) throws DatabaseException;

    /**
     * Stores a list of tweets with reference to the given Trend.
     *
     * @param tweets    a list of jtwitter Status objects, representing the
     *                  tweets to put in the database
     * @param trend     the trend to store these tweets under
     */
    public abstract void putTweets(List<Status> tweets, Trend trend) throws DatabaseException;

    /**
     * Gets a list of tweets associated with the Trend specified by the Trend
     * object.
     *
     * @param trend     the trend the tweets are stored under
     *
     * @return a list of jtwitter Status Objects representing the tweets
     */
    public abstract List<Status> getTweets(Trend trend) throws DatabaseException;

    /**
     * Gets a list of tweets associated with the Trend specified by the
     * trend_id.
     *
     * @param trend_id  the trend_id the tweets are stored under
     *
     * @return a list of jtwitter Status Objects representing the tweets
     */
    public abstract List<Status> getTweets(int trend_id) throws DatabaseException;

    /**
     * Stores a list of wikipedia articles with reference to the given Trend.
     *
     * @param articles  a list of WikiArticles, to put in the database
     * @param trend     the trend to store these articles under
     */
    public abstract void putWikiArticles(List<WikiArticle> articles, Trend trend)
        throws DatabaseException;

    /**
     * Gets a list of wikipedia articles associated with the Trend specified by
     * the Trend object, ordered by descending relevance.
     *
     * @param trend     the trend the articles are stored under
     *
     * @return a list of the WikiArticles
     */
    public abstract List<WikiArticle> getWikiArticles(Trend trend) throws DatabaseException;

    /**
     * Gets a list of wikipedia articles associated with the Trend specified by
     * the trend_id, ordered by descending relevance.
     *
     * @param trend_id  the trend_id that the articles are stored under
     *
     * @return a list of the WikiArticles
     */
    public abstract List<WikiArticle> getWikiArticles(int trend_id) throws DatabaseException;

    /**
     * Gets a list of wikipedia articles associated with the Trend specified by
     * the Trend object, ordered by descending popularity.
     *
     * @param trend     the trend the articles are stored under
     *
     * @return a list of the WikiArticles
     */
    public abstract List<WikiArticle> getWikiArticlesByPopularity(Trend trend)
        throws DatabaseException;

    /**
     * Gets a list of wikipedia articles associated with the Trend specified by
     * the trend_id, ordered by descending popularity.
     *
     * @param trend_id  the trend_id that the articles are stored under
     *
     * @return a list of the WikiArticles
     */
    public abstract List<WikiArticle> getWikiArticlesByPopularity(int trend_id)
        throws DatabaseException;

    /**
     * Gets a list of wikipedia articles associated with the Trend specified by
     * the Trend object, ordered by descending controversy.
     *
     * @param trend     the trend the articles are stored under
     *
     * @return a list of the WikiArticles
     */
    public abstract List<WikiArticle> getWikiArticlesByControversy(Trend trend)
        throws DatabaseException;

    /**
     * Gets a list of wikipedia articles associated with the Trend specified by
     * the trend_id, ordered by descending controversy.
     *
     * @param trend_id  the trend_id that the articles are stored under
     *
     * @return a list of the WikiArticles
     */
    public abstract List<WikiArticle> getWikiArticlesByControversy(int trend_id)
        throws DatabaseException;

    /**
     * Gets a list of wikipedia articles associated with the Trend specified by
     * the Trend object, ordered by descending recency.
     *
     * @param trend     the trend the articles are stored under
     *
     * @return a list of the WikiArticles
     */
    public abstract List<WikiArticle> getWikiArticlesByRecency(Trend trend)
        throws DatabaseException;

    /**
     * Gets a list of wikipedia articles associated with the Trend specified by
     * the trend_id, ordered by descending recency.
     *
     * @param trend_id  the trend_id that the articles are stored under
     *
     * @return a list of the WikiArticles
     */
    public abstract List<WikiArticle> getWikiArticlesByRecency(int trend_id)
        throws DatabaseException;
}
