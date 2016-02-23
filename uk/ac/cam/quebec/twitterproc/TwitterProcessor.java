package uk.ac.cam.quebec.twitterproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javafx.util.Pair;
import uk.ac.cam.quebec.common.VisibleForTesting;
import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.dbwrapper.DatabaseException;
import uk.ac.cam.quebec.havenapi.HavenException;
import uk.ac.cam.quebec.havenapi.SentimentAnalyser;
import uk.ac.cam.quebec.havenapi.SentimentAnalysis;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.twitterwrapper.TwitException;
import uk.ac.cam.quebec.twitterwrapper.TwitterLink;
import uk.ac.cam.quebec.util.WordCounter;
import uk.ac.cam.quebec.util.parsing.UtilParsing;
import uk.ac.cam.quebec.wikiproc.WikiProcessor;
import winterwell.jtwitter.Status;

/**
 * <p>
 * Class responsible for processing trends. It should extract relevant tweets,
 * and send them to the database. After that analyse them and use the data to
 * create a list of concepts that are passed to the WikiProcessor.
 *
 * <p>
 * Don't care about concurrency issues.
 *
 * @author Momchil
 */
public class TwitterProcessor {

    private static final boolean DEBUG = false;

    private static final int CONCEPT_THRESHOLD_PERCENTAGE = 30;
    private static final int MERGE_BI_GRAMS_PERCENTAGE = 40;

    /**
     * Process a trend and in the end pass a list of concepts to the Wikipedia
     * Processor.
     *
     * @param trend The trend that should be processed.
     */
    public static void process(Trend trend) {
        if (doProcess(trend)) {
            WikiProcessor wp = new WikiProcessor();
            wp.process(trend);
        }
    }

    /**
     * Do the actual processing of the trend
     *
     * @param trend The trend that should be processed.
     * @return true if trend successfully processed
     */
    public static boolean doProcess(Trend trend) {
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
            }

            calculatePopularity(trend, tweets);
            calculateTimestamp(trend, tweets);
            calculateControversy(trend, tweets);
            extractConcepts(trend, tweets);
            return true;
        } catch (TwitException e) {
            // TODO Auto-generated catch block
            System.err.println("Could not create a TwitterLink object");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * <p>
     * Ad-hoc evaluation of the popularity of a trend based on the tweets
     * related to it. For each such tweet we consider its retweet count and the
     * "popularity" of its author (measured in number of followers).
     *
     * @param tweets The tweets for the trend.
     * @return Integer evaluation of the popularity of the trend based on these
     * tweets.
     */
    private static void calculatePopularity(Trend trend, List<Status> tweets) {
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
        trend.setPopularity(popularity);
    }

    /**
     * <p>
     * Calculates a timestamp for the trend.
     *
     * <p>
     * Currently considers it as the earliest tweet for the trend
     *
     * @param trend The trend we are processing.
     * @param tweets The tweets related to this trend.
     */
    private static void calculateTimestamp(Trend trend, List<Status> tweets) {
	boolean set = false;
	for (Status tweet : tweets) {
	    if (tweet.getCreatedAt() != null &&
		    (!set || tweet.getCreatedAt().before(trend.getTimestamp()))) {
		trend.setTimestamp(tweet.getCreatedAt());
		set = true;
	    }
	}
    }

    /**
     * Calculates controversy of a trend on the basis of the sentiment analyses of the tweets.
     *
     * @param trend
     * @param tweets
     */
    private static void calculateControversy(Trend trend, List<Status> tweets) {
	List<String> uniqueTweets = removeDuplicates(tweets);
	for (int i = 0; i < uniqueTweets.size(); i++) {
	    uniqueTweets.set(i, UtilParsing.removeLinks(uniqueTweets.get(i)));
	}

	double mx = -1e6;
	double mn =  1e6;
	boolean set = false;
	for (String tweet : uniqueTweets) {
	    String text = UtilParsing.removeUsersAndHashTags(tweet);
	    if (text != null && !text.isEmpty()) {
		SentimentAnalysis sa;
		try {
		    sa = SentimentAnalyser.getAnalysis(text);
		    if (sa != null) {
			mx = Double.max(mx, sa.getAggregate().getScore());
			mn = Double.min(mn, sa.getAggregate().getScore());
			set = true;
		    }
		} catch (HavenException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}

	if (set) {
	    trend.setControversy(mx - mn);
	}
    }

    /**
     * <p>
     * Tries to extract the concepts from the tweets and add them to the trend.
     *
     * <p>
     * Currently relies on simple word count.
     *
     * @param trend The trend we are processing.
     * @param tweetsBatch The tweets related to this trend.
     */
    @VisibleForTesting
    static void extractConcepts(Trend trend, List<Status> tweetsBatch) {
        trend.addConcept(new Pair<String, Integer>(trend.getParsedName(),
        	tweetsBatch == null ? 1 : 1 + 2 * tweetsBatch.size()));

        if (tweetsBatch == null) {
            return;
        }

        List<List<String>> tweetsSplitted = filter(removeDuplicates(tweetsBatch));
        tweetsSplitted = hashTagConcepts(trend, tweetsSplitted);
        wordConcepts(trend, tweetsSplitted);
    }

    /**
     * Process the hash tags in the tweets (in search for related trends) and eventually remove
     * them.
     *
     * @param trend
     * @param tweets
     */
    private static List<List<String>> hashTagConcepts(Trend trend, List<List<String>> tweets) {
	WordCounter hashTagCounter = new WordCounter();
	List<List<String>> tweetsWithNoHashTags = new ArrayList<List<String>>();
	for (List<String> tweet : tweets) {
	    List<String> tweetWithNoHashTags = new ArrayList<String>();
	    for (String word : tweet) {
		if (word.startsWith("#")) {
                    // Certain hash tag.
                    if (!trend.getParsedName().toLowerCase().equals(
                            UtilParsing.parseTrendName(word).toLowerCase())) {
			// If the hash tag is different from the current trend, add it as a
                        // relevant trend.
                        hashTagCounter.addWord(word);
                    }
		} else {
		    tweetWithNoHashTags.add(word);
		}
	    }
	    tweetsWithNoHashTags.add(tweetWithNoHashTags);
	}

	Pair<String, Integer>[] orderedHashTags = hashTagCounter.getOrderedWordsAndCount();
	if (orderedHashTags != null) {
	    // Add top 5 hash tags + all those which pass the threshold.
	    for (int i = 0; i < orderedHashTags.length; i++) {
		if (i < 5
		|| 100 * orderedHashTags[i].getValue() >=
			CONCEPT_THRESHOLD_PERCENTAGE * tweets.size()) {
		    trend.addRelatedHashTag(orderedHashTags[i]);
		}
	    }
	}

	return tweetsWithNoHashTags;
    }

    /**
     * Looks for concepts in the tweets combining the popular bi-grams in a single concept.
     *
     * @param trend
     * @param tweets
     */
    private static void wordConcepts(Trend trend, List<List<String>> tweets) {
	if (DEBUG) {
	    System.out.println(" ~~~~ Tweets on which we run the concepts extractor ~~~~ ");
	    for (List<String> tweet : tweets) {
		for (String word : tweet) {
		    System.out.print(word + " ");
		}
		System.out.println();
	    }
	}

	WordCounter wordCounter = new WordCounter();
	for (List<String> tweet : tweets) {
	    wordCounter.addSentence(tweet);
	}

	Pair<String, Integer>[] orderedWords = wordCounter.getOrderedWordsAndCount();
	Pair<List<String>, Integer>[] orderedBiGrams = wordCounter.getOrderedNGramsAndCount(2);
	HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
	List<Pair<String, Integer>> concepts = new ArrayList<Pair<String, Integer>>();
	HashSet<String> removeFromSingleWordConcepts = new HashSet<String>();

	if (orderedBiGrams == null && orderedWords == null) {
	    return;
	}

	if (orderedBiGrams != null) {
	    for (int i = 0; i < orderedWords.length; i++) {
		wordCount.put(orderedWords[i].getKey(), orderedWords[i].getValue());
	    }
	    for (int i = 0; i < orderedBiGrams.length; i++) {
		String word1 = orderedBiGrams[i].getKey().get(0);
		String word2 = orderedBiGrams[i].getKey().get(1);
		if (100 * orderedBiGrams[i].getValue() >= MERGE_BI_GRAMS_PERCENTAGE *
			Math.max(wordCount.get(word1), wordCount.get(word2))) {
		    concepts.add(new Pair<String, Integer>(word1 + " " + word2,
			    wordCount.get(word1) + wordCount.get(word2)
			    - orderedBiGrams[i].getValue() / 2));
		    removeFromSingleWordConcepts.add(word1);
		    removeFromSingleWordConcepts.add(word2);
		} else {
		    concepts.add(new Pair<String, Integer>(word1 + " " + word2,
			    orderedBiGrams[i].getValue()));
		}
	    }
	}

	for (int i = 0; i < orderedWords.length; i++) {
	    if (!removeFromSingleWordConcepts.contains(orderedWords[i].getKey())) {
		concepts.add(orderedWords[i]);
	    }
	}

	Collections.sort(concepts, new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                // sort them such that o2 comes before o1 if o2 > o1, otherwise sort
                return o2.getValue().compareTo(o1.getValue());
            }
        });

	for (int i = 0; i < concepts.size(); i++) {
	    Pair<String, Integer> concept = concepts.get(i);
	    if (i < 5 || 100 * concept.getValue() >=
		    CONCEPT_THRESHOLD_PERCENTAGE * tweets.size()) {
		trend.addConcept(concept);
	    }
	}
    }

    /**
     * The Twitter API and the Database can give us two or more same tweets. We
     * want to remove the duplicates.
     *
     * @param tweets
     * @return List of tweets (represented just as strings that must be unique).
     */
    private static List<String> removeDuplicates(List<Status> tweets) {
        HashSet<String> cache = new HashSet<String>();
        for (Status tweet : tweets) {
            cache.add(tweet.getDisplayText());
        }
        return new ArrayList<String>(cache);
    }

    /**
     * Remove stop words and Twitter usernames; split into words.
     *
     * @param tweets
     * @return List of filtered tweets
     */
    private static List<List<String>> filter(List<String> tweets) {
	List<List<String>> tweetsSplitted = new ArrayList<List<String>>();
	for (String tweet : tweets) {
	    String[] individualWords = UtilParsing.splitIntoWords(UtilParsing.removeLinks(tweet));
	    List<String> finalSetOfWords = new ArrayList<String>();
	    for (int i = 0; i < individualWords.length; i++) {
		if (individualWords[i].length() > 2
		    && !UtilParsing.isStopWord(individualWords[i])
		    && !individualWords[i].startsWith("@")) {
		    finalSetOfWords.add(individualWords[i]);
		}
	    }
	    tweetsSplitted.add(finalSetOfWords);
	}
	return tweetsSplitted;
    }
}
