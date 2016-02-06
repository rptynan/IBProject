package uk.ac.cam.quebec.dbwrapper;

import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;

import winterwell.jtwitter.Status;

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
     */
    public static void main(String[] args) {
        // getInstance
        // Get two databases to check they're the same (or acting the same)
        System.out.println("==> Initialising Database(s)");
        Database db1 = Database.getInstance();
        Database db2 = Database.getInstance();

        // putTrend(), getTrends() and check ID has been set
        System.out.println("==> Testing Trends");
        Trend t1 = new Trend("TestTrend1", "USA", 42);
        Trend t2 = new Trend("TestTrend2", "UK", 43);
        List<Trend> trendList = null;
        try {
            db1.putTrend(t1);
            db1.putTrend(t2);
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
    }
}
