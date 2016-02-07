package uk.ac.cam.quebec.dbwrapper;

import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;

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
     * <p>SuprressWarnings is for Status constructor which is to only be used
     * for testing.
     */
    @SuppressWarnings( "deprecation" )
    public static void main(String[] args) {
        // getInstance
        // Get two databases to check they're the same (or acting the same)
        System.out.println("==> Initialising Database(s)");
        Database db1 = Database.getInstance();
        Database db2 = Database.getInstance();

        // putTrend(), getTrends(), check ID has been set, and that replacing
        // trend works (check processCount of t1)
        System.out.println("==> Testing Trends");
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
        System.out.println("Check processCount of " + t1.getId());

        // putTweets(), getTweets()
        System.out.println("==> Testing Tweets");
        ArrayList<Status> tweets = new ArrayList<Status>(3);
        List<Status> tweetList = null;
        tweets.add(0, new Status(new User("User1"), "Test Tweet 1 @Me #Beans",
                    new BigInteger("123"), new Date()));
        tweets.add(1, new Status(new User("User2"), "Test Tweet 2 @Me #Beans",
                    new BigInteger("18446744073709551615"), new Date()));
        tweets.add(2, new Status(new User("User3"), "Test Tweet 3 @Me #Beans",
                    new BigInteger("1"), new Date()));
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
    }
}
