package uk.ac.cam.quebec.twitterproc;

import java.util.List;

import javafx.util.Pair;
import uk.ac.cam.quebec.common.VisibleForTesting;
import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.twitterwrapper.TwitException;
import uk.ac.cam.quebec.twitterwrapper.TwitterLink;
import uk.ac.cam.quebec.util.WordCounter;
import uk.ac.cam.quebec.wikiproc.WikiProcessor;
import winterwell.jtwitter.Status;

/**
 * <p>Class responsible for processing trends. It should extract relevant tweets, and send them to
 * the database. After that analyse them and use the data to create a list of concepts that are
 * passed to the WikiProcessor.
 *
 * <p>Don't care about concurrency issues.
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
	trend.setParsedName(parse(trend.getName()));
	TwitterLink twitter;
	try {
	    twitter = new TwitterLink();
	    List<Status> tweets = twitter.getTweets(trend.getName());

	    Database db = Database.getInstance();
	    db.putTweets(tweets, trend);
	    tweets = db.getTweets(trend); // as it is possible to have old tweets in the database

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
     * <p> Parse the name of the trend.
     *
     * <p> This includes removal of special symbols such as '@' and '#' and trying to put spaces
     * in the right positions.
     *
     * @param trendName The original trend name that we have to parse.
     * @return Parsed trend.
     */
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
     * <p> Tries to extract the concepts from the tweets and add them to the trend.
     *
     * <p> Currently relies on simple word count.
     *
     * @param trend The trend we are processing.
     * @param tweets The tweets related to this trend.
     */
    private static void extractConcepts(Trend trend, List<Status> tweets) {
	WordCounter wordCounter = new WordCounter();
	WordCounter hashTagCounter = new WordCounter();
	for (Status tweet : tweets) {
	    String[] words = tweet.getDisplayText()
		    		  .replaceAll("[^a-zA-Z0-9@#-]", " ")
		    		  .trim()
		    		  .split("\\s+");
	    for (String word : words) {
		if (word.startsWith("@")) {
		    // Discard usernames.
		    continue;
		} else if (word.startsWith("#")) {
		    // Certain hash tag.
		    if (!trend.getParsedName().equals(parse(word))) {
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
	    // Add top 5 most common words among the tweets.
	    for (int i = 0; i < 5 && i < orderedWords.length; i++) {
		trend.addConcept(orderedWords[i].getKey());
	    }
	    // Add at most 5 other words given that they occur in at least 30% of the tweets.
	    for (int i = 5; i < 10 && i < orderedWords.length; i++) {
		if (10 * orderedWords[i].getValue() >= 3 * tweets.size()) {
		    trend.addConcept(orderedWords[i].getKey());
		} else {
		    break;
		}
	    }
	}

	Pair<String, Integer>[] orderedHashTags = hashTagCounter.getOrderedWordsAndCount();
	if (orderedHashTags != null) {
	    // Add top 5 most common hash tags among the tweets.
	    for (int i = 0; i < 5 && i < orderedHashTags.length; i++) {
		trend.addRelatedHashTag(orderedHashTags[i].getKey());
	    }
	    // Add at most 5 other hash tags given that they occur in at least 30% of the tweets.
	    for (int i = 5; i < 10 && i < orderedHashTags.length; i++) {
		if (10 * orderedHashTags[i].getValue() >= 3 * tweets.size()) {
		    trend.addRelatedHashTag(orderedHashTags[i].getKey());
		} else {
		    break;
		}
	    }
	}
    }

}
