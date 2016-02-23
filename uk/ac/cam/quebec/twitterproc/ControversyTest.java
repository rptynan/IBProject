package uk.ac.cam.quebec.twitterproc;

import java.util.List;

import javafx.util.Pair;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.twitterwrapper.TwitException;
import uk.ac.cam.quebec.twitterwrapper.TwitterLink;
import uk.ac.cam.quebec.util.parsing.UtilParsing;
import winterwell.jtwitter.Status;

/**
 * @author Momchil
 */
public class ControversyTest {

    public static void main(String[] args) {
	TwitterLink link;

	try {
	    Trend trend = new Trend("Donald Trump", "USA", 1);
	    trend.setParsedName(UtilParsing.parseTrendName(trend.getName()));

	    List<Status> tweets;
	    TwitterLink.login(args[0], args[1], args[2], args[3], args[4]);
	    link = new TwitterLink();
	    tweets = link.getTweets(trend.getName());

	    TwitterProcessor.calculateControversy(trend, tweets);
	    System.out.println("Controversy measure: " + trend.getControversy());
	} catch (TwitException e) {
	    System.err.println(e.toString());
	}
    }

}
