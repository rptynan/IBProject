package uk.ac.cam.quebec.trends;

import uk.ac.cam.quebec.wikiwrapper.WikiArticle;

import winterwell.jtwitter.Status;
import winterwell.jtwitter.User;

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
private class DatabaseInternal implements Database {
    private static final DatabaseInternal INSTANCE = new DatabaseInternal();

    public static Database getInstance() {
        return INSTANCE;
    }

    public void putTrend(Trend trend) {
        return;
    }

    public List<Trend> getTrends() {
        ArrayList<Trend> result = new ArrayList<Trend>(3);
        result[0] = new Trend("POTUS with the mostest", "USA", 10);
        result[1] = new Trend("Larry", "UK", 10);
        result[2] = new Trend("Flat-Earth BOB", "World", 10);
        return result;
    }

    public void putTweets(List<Status> tweets, Trend trend) {
        return;
    }

    public List<Status> getTweets(Trend trend) {
        ArrayList<Status> result = new ArrayList<Status>(3);
        result[0] = new Status(new User("DTrump"),
                "Blah Blash BLasdfhlsafhkdlsf", null, new Date());
        result[1] = new Status(new User("DTrump"),
                "Blah Bloh Bleh", null, new Date());
        result[2] = new Status(new User("DTrump"),
                "Blaaaaaaaaaaaaaaah", null, new Date());
        return result;
    }

    public void putWikiArticles(List<WikiArticles> articles, Trend trend) {
        return;
    }

    public List<WikiArticle> getWikiArticles(Trend trend) {
        ArrayList<WikiArticle> result = new ArrayList<WikiArticle>(3);
        result[0] = new WikiArticle("Standard ML");
        result[1] = new WikiArticle("The Salmon of Knowledge");
        result[2] = new WikiArticle("The Trout of No-Craic");
        return result;
    }

}
