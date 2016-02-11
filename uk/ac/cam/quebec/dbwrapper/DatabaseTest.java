package uk.ac.cam.quebec.dbwrapper;

import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;
import uk.ac.cam.quebec.wikiwrapper.WikiException;

import winterwell.jtwitter.Status;
import winterwell.jtwitter.User;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Test class for the database, not much to see here.
 *
 * @author Richard
 *
 */
public class DatabaseTest {

    /**
     * Just a test.
     *
     */
    public static void main(String[] args) {
    Database.setCredentials("ibproject", null, "jdbc:mysql://localhost:3306/ibprojectdb", true);
    //By seperating the set credentials call from the tests we can invoke the 
    //tests from elsewhere
    test();
    }
    /**
     * This is the function where the tests are run
     * 
     * SuprressWarnings is for Status constructor which is to only be used for
     * testing.
     */
    @SuppressWarnings("deprecation")
    public static void test() {// getInstance
        // Get two databases to check they're the same (or acting the same)
        System.out.println("==> Initialising Database(s)");
        
        Database db1 = Database.getInstance();
        Database db2 = Database.getInstance();

        // putTrend(), getTrends(), check ID has been set, and that replacing
        // trend works (check processCount of t1)
        System.out.println("\n==> Testing Trends");
        Trend t1 = new Trend("TestTrend1", "USA", 42);
        Trend t2 = new Trend("TestTrend2", "UK", 43);
        List<Trend> trendList = null;
        try {
            db1.putTrend(t1);
            db1.putTrend(t2);
            t1.incrementProcessCount();
            db1.putTrend(t1);
            trendList = db2.getTrends();
        } catch (DatabaseException exp) {
            exp.printStackTrace();
        }
        for (Trend t : trendList) {
            System.out.println(t.getName() + " " + t.getLocation()
                    + " " + t.getPriority() + " " + t.getProcessCount()
                    + " " + t.getId());
        }
        System.out.println("Check ID of trends inserted: " + t1.getId() + " " + t2.getId());
        System.out.println("Check processCount of trend " + t1.getId() + " is 1");

        // putTweets(), getTweets()
        System.out.println("\n==> Testing Tweets");
        ArrayList<Status> tweets = new ArrayList<Status>(3);
        List<Status> tweetList = null;
        tweets.add(0, new Status(new User("User1"), "Test Tweet 1 @Me #Beans",
                new BigInteger("123"), new Date()));
        tweets.add(1, new Status(new User("User2"), "Test Tweet 2 @Me #Beans",
                new BigInteger("18446744073709551615"), new Date()));
        tweets.add(2, new Status(new User("User3"), "Test Tweet 3 @Me #Beans",
                new BigInteger("123"), new Date()));
        try {
            db1.putTweets(tweets, t1);
            tweetList = db2.getTweets(t1);
        } catch (DatabaseException exp) {
            exp.printStackTrace();
        }
        for (Status tw : tweetList) {
            System.out.println(tw.getUser() + " " + tw.getLocation()
                    + " " + tw.getText() + " " + tw.getId());
        }
        System.out.println("Tweet 3 should have overwritten Tweet 1 (because same id)");

        // putWikiArticles(), getWikiArticles() (by object and id)
        System.out.println("\n==> Testing WikiArticles");
        ArrayList<WikiArticle> wikis1 = new ArrayList<WikiArticle>(2);
        ArrayList<WikiArticle> wikis2 = new ArrayList<WikiArticle>(2);
        List<WikiArticle> wikiList1 = null;
        List<WikiArticle> wikiList2 = null;
        try {
            wikis1.add(0, new WikiArticle("Standard ML"));
            wikis1.add(1, new WikiArticle("Lawrence Paulson"));
            wikis2.add(0, new WikiArticle("Conway's Game of Life"));
            wikis2.add(1, new WikiArticle("John Horton Conway"));
        } catch (WikiException exp) {
            exp.printStackTrace();
        }
        try {
            db1.putWikiArticles(wikis1, t1);
            db1.putWikiArticles(wikis1, t2);
            db1.putWikiArticles(wikis2, t2);
            wikiList1 = db2.getWikiArticles(t1.getId());
            wikiList2 = db2.getWikiArticles(t2);
        } catch (DatabaseException exp) {
            exp.printStackTrace();
        }
        System.out.println("Articles for Trend " + t1.getId());
        for (WikiArticle wk : wikiList1) {
            System.out.println(wk.getTitle() + " " + wk.getId());
        }
        System.out.println("Articles for Trend " + t2.getId());
        for (WikiArticle wk : wikiList2) {
            System.out.println(wk.getTitle() + " " + wk.getId());
        }
        System.out.println("Trend " + t1.getId() + " should have ML articles");
        System.out.println("Trend " + t2.getId() + " should have ML and GOL articles");
    }
}
