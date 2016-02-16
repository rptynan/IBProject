package uk.ac.cam.quebec.twitterproc;

import java.util.List;

import javafx.util.Pair;
import uk.ac.cam.quebec.common.VisibleForTesting;
import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.dbwrapper.DatabaseException;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.twitterwrapper.TwitException;
import uk.ac.cam.quebec.twitterwrapper.TwitterLink;
import uk.ac.cam.quebec.util.WordCounter;
import uk.ac.cam.quebec.util.parsing.UtilParsing;
import uk.ac.cam.quebec.wikiproc.WikiProcessor;
import winterwell.jtwitter.Status;

/**
 * <p> Class responsible for processing trends. It should extract relevant tweets, and send them to
 * the database. After that analyse them and use the data to create a list of concepts that are
 * passed to the WikiProcessor.
 *
 * <p> Don't care about concurrency issues.
 *
 * @author Momchil
 */
public class TwitterProcessor {

    /**
     * Process a trend and in the end pass a list of concepts to the Wikipedia Processor.
     *
     * @param trend The trend that should be processed.
     */
    public static void process(Trend trend) {
	trend.setParsedName(UtilParsing.parseTrendName(trend.getName()));
	TwitterLink twitter;
	try {
	    twitter = new TwitterLink();
	    List<Status> tweets = twitter.getTweets(trend.getName());

	    Database db = Database.getInstance();
	    try {
		db.putTweets(tweets, trend);
		tweets = db.getTweets(trend); // It is possible to have old tweets in the database.
	    } catch (DatabaseException e) {
            e.printStackTrace();
		// Throwing exception doesn't necessarily prevent us from continuing execution.
	    }

	    trend.setPopularity(calculatePopularity(tweets));
	    extractConcepts(trend, tweets);

	    WikiProcessor wp = new WikiProcessor();
	    wp.process(trend);
	} catch (TwitException e) {
	    // TODO Auto-generated catch block
	    System.err.println("Could not create a TwitterLink object");
	    e.printStackTrace();
	}
    }

    /**
     * <p> Ad-hoc evaluation of the popularity of a trend based on the tweets related to it. For
     * each such tweet we consider its retweet count and the "popularity" of its author (measured
     * in number of followers).
     *
     * @param tweets The tweets for the trend.
     * @return Integer evaluation of the popularity of the trend based on these tweets.
     */
    private static int calculatePopularity(List<Status> tweets) {
	int popularity = 0;
	if (tweets != null) {
	    for (Status tweet : tweets) {
		if (tweet.retweetCount != -1) {
		    // retweetCount is not unknown.
		    popularity += 10 * tweet.retweetCount;
		}
		if (tweet.getUser() != null) {
		    // Add the "popularity" (number of followers) of the user who made the tweet.
		    popularity += tweet.getUser().getFollowersCount();
        	}
	    }
	}
	return popularity;
    }

    /**
     * <p> Tries to extract the concepts from the tweets and add them to the trend.
     *
     * <p> Currently relies on simple word count.
     *
     * @param trend The trend we are processing.
     * @param tweets The tweets related to this trend.
     */
    @VisibleForTesting
    static void extractConcepts(Trend trend, List<Status> tweets) {
	// TODO (Momchil): Push parsed name with infinite priority.

	if (tweets == null) {
	    return;
	}

	WordCounter wordCounter = new WordCounter();
	WordCounter hashTagCounter = new WordCounter();
	for (Status tweet : tweets) {
	    String text = UtilParsing.removeLinks(tweet.getDisplayText());
	    String[] words = text.replaceAll("[^a-zA-Z0-9@#-]", " ")
		    		 .trim()
		    		 .split("\\s+");
	    for (String word : words) {
		if (word.startsWith("@")) {
		    // Discard usernames and links.
		    continue;
		} else if (word.startsWith("#")) {
		    // Certain hash tag.
		    if (!trend.getParsedName().equals(UtilParsing.parseTrendName(word))) {
			// If the hash tag is different from the current trend, add it as a
			// relevant trend.
			hashTagCounter.addWord(word);
		    }
		} else {
		    if (word.length() > 3) {
			// Discard short words (a.g. is, the, a, and, ...).
			wordCounter.addWord(word);
		    }
		}
	    }
	}

	Pair<String, Integer>[] orderedWords = wordCounter.getOrderedWordsAndCount();
	if (orderedWords != null) {
	    // Add at most 100 most common words among the tweets sorted by their frequencies.
	    for (int i = 0; i < 100 && i < orderedWords.length; i++) {
		trend.addConcept(orderedWords[i]);
	    }
	}

	Pair<String, Integer>[] orderedHashTags = hashTagCounter.getOrderedWordsAndCount();
	if (orderedHashTags != null) {
	    // Add at most 100 most common hash tags among the tweets sorted by their frequencies.
	    for (int i = 0; i < 100 && i < orderedHashTags.length; i++) {
		trend.addRelatedHashTag(orderedHashTags[i]);
	    }
	}
    }

}
