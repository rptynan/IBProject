package uk.ac.cam.quebec.twitterproc;

import java.util.List;

import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.twitterwrapper.TwitException;
import uk.ac.cam.quebec.twitterwrapper.TwitterLink;
import uk.ac.cam.quebec.util.parsing.UtilParsing;
import winterwell.jtwitter.Status;

public class ExtractConceptsTest {

    public static void main(String[] args) {
	TwitterLink link;

	try {
	    Trend trend = new Trend("SpaceX", "USA", 1);
	    trend.setParsedName(UtilParsing.parseTrendName(trend.getName()));

	    List<Status> tweets;
	    TwitterLink.login(args[0], args[1], args[2], args[3], args[4]);
	    link = new TwitterLink();
	    tweets = link.getTweets(trend.getName());

	    System.out.println("Number of tweets: " + tweets.size());
//	    System.out.println("Print tweets");
//	    for (Status tweet : tweets) {
//		System.out.println(tweet.getDisplayText());
//	    }

	    TwitterProcessor.extractConcepts(trend, tweets);
	    List<String> concepts = trend.getConcepts();
	    List<String> hashTags = trend.getRelatedHashTags();
	    System.out.println("--- Concepts ---");
	    for (String concept : concepts) {
		System.out.println(concept);
	    }
	    System.out.println("--- Hash Tags ---");
	    for (String hashTag : hashTags) {
		System.out.println(hashTag);
	    }
	} catch (TwitException e) {
	    System.err.println(e.toString());
	}
    }

}
