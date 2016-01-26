package uk.ac.cam.quebec.trends;

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

    /**
     * Gets a list of all the trends in the Database.
     *
     * @return a list of Trend objects from the database
     */
    public abstract List<Trend> getTrends();

    /**
     * Stores a list of tweets with reference to the given Trend.
     *
     * @param tweets    a list of jtwitter Status objects, represting the
     *                  tweets to put in the database
     * @param trend     the trend to store these tweets under
     */
    public abstract void putTweets(List<Status> tweets, Trend trend);

}
