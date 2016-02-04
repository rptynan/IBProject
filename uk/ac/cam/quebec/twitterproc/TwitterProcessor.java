package uk.ac.cam.quebec.twitterproc;

import java.util.List;

import uk.ac.cam.quebec.common.VisibleForTesting;
import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.twitterwrapper.TwitException;
import uk.ac.cam.quebec.twitterwrapper.TwitterLink;
import winterwell.jtwitter.Status;

/**
 * Class responsible for processing trends. It should extract relevant tweets,
 * and send them to the database. After that analyse them and use the data to
 * create a list of concepts that are passed to the WikiProcessor.
 * 
 * @author Momchil
 *
 */
public class TwitterProcessor {

    @VisibleForTesting
    static String parse(String trendName) {
	String newTrendName = trendName.replaceAll("[^a-zA-Z0-9-]", " ").trim();
	StringBuilder parsedResult = new StringBuilder();
	for (int i = 0; i < newTrendName.length(); i++) {
	    char currentCharacter = newTrendName.charAt(i);
	    parsedResult.append(currentCharacter);
	    if (i + 1 < newTrendName.length()) {
		if (Character.isLowerCase(currentCharacter)) {
		    if (Character.isUpperCase(newTrendName.charAt(i + 1))
			|| Character.isDigit(newTrendName.charAt(i + 1))) {
			parsedResult.append(" ");
		    }
		} else if (Character.isDigit(currentCharacter)) {
		    if (Character.isUpperCase(newTrendName.charAt(i + 1))
			|| Character.isLowerCase(newTrendName.charAt(i + 1))) {
			parsedResult.append(" ");
		    }
		} else if (Character.isUpperCase(currentCharacter)) {
		    if (Character.isDigit(newTrendName.charAt(i + 1))
			|| (i + 2 < newTrendName.length()
			    && Character.isUpperCase(newTrendName.charAt(i + 1))
			    && Character.isLowerCase(newTrendName.charAt(i + 2)))) {
			parsedResult.append(" ");
		    }
		}
	    }
	}
	return parsedResult.toString();
    }

    private static int calculatePopularity(List<Status> tweets) {
	int popularity = 0;
	for (Status tweet : tweets) {
	    if (tweet.retweetCount != -1) {
		// retweetCount is not unknown
		popularity += 10 * tweet.retweetCount;
	    }
	    if (tweet.getUser() != null) {
		// add the "popularity" (i.e. number of followers) of the user who made the tweet
		popularity += tweet.getUser().getFollowersCount();
	    }
	}
	return popularity;
    }

    /**
     * Process a trend and in the end pass a list of concepts to the Wikipedia
     * Processor.
     * 
     * @param trend The trend that should be processed.
     */
    public static void process(Trend trend) {
	trend.setParsedName(parse(trend.getName()));
	TwitterLink twitter;
	try {
	    twitter = new TwitterLink();
	    List<Status> tweets = twitter.getTweets(trend.getName());
	    Database db = Database.getInstance();
	    db.putTweets(tweets, trend);
	    tweets = db.getTweets(trend);
	    trend.setPopularity(calculatePopularity(tweets));
	} catch (TwitException e) {
	    // TODO Auto-generated catch block
	    System.err.println("Could not create a TwitterLink object");
	    e.printStackTrace();
	}
    }

}
