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

        // putTrend & getTrends
        System.out.println("==> Testing Trends");
        db1.putTrend(new Trend("TestTrend1", "USA", 42));
        List<Trend> trendList = db2.getTrends();
        for (Trend t : trendList) {
            System.out.println(t.getName() + " " + t.getLocation()
                    + " " + t.getPriority() + " " + t.getProcessCount());
        }
    }
}
