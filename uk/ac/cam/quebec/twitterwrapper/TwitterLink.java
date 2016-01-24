package uk.ac.cam.quebec.twitterwrapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Status;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.User;

/**
 * Class providing methods to get Twitter stuff. Is build as a wrapper for the
 * JTwitter Twitter class that is not thread safe so you need to make multiple
 * instances for concurrency.
 * 
 * @author Stuart
 * 
 */
/**
 * @author Stuart
 *
 */
public abstract class TwitterLink {

	/**
	 * OAuth client needed for creating Twitter Links
	 */
	private static OAuthSignpostClient oAuthClient;

	/**
	 * Username of the Twitter account we are using
	 */
	private static String username;

	/**
	 * Map to lookup the woeids for the location strings. Is initialised in
	 * login the shouldn't be changed.
	 */
	private static Map<String, Long> locationLookup;
	/**
	 * Underlying JTwitter twitter object.
	 */
	private Twitter twitter;

	/**
	 * Constructor for a single Twitter link. Not to be used concurrently - make
	 * lots instead!
	 * 
	 * @throws TwitterException
	 *             Must perform a SINGLE STATIC LOGIN else this is thrown
	 */

	public TwitterLink() throws TwitterException {
	}

	/**
	 * Call one to perform login and then build multiple TwitterLink objects
	 * from the single login
	 * 
	 * @param AccessToken
	 *            Token for application
	 * @param AccessTokenSecret
	 *            Secret for Application
	 * @param OAuthKey
	 *            Key for user
	 * @param OAuthSecret
	 *            Secret for user
	 * @param username
	 *            Name of the user
	 * @throws TwitterException
	 *             If the login fails
	 */
	public static void login(String AccessToken, String AccessTokenSecret,
			String OAuthKey, String OAuthSecret, String username)
			throws TwitterException {
	}

	/**
	 * Get the trends by specifying a location.
	 * 
	 * @param location
	 *            A VALID location - these are specified in the location set.
	 * @return The trends as unparsed strings.
	 * @throws TwitterException
	 *             It connection fails or place invalid
	 */
	public abstract List<String> getTrends(String location)
			throws TwitterException;

	/**
	 * Perform a search to get a page of tweets relating to a term. The Users
	 * associated with the tweet are dummies and need to be filled with the
	 * fillUser method.
	 * 
	 * @param phrase
	 *            The string to search with
	 * @return A list of JTwitter status objects (each representing a tweet) -
	 *         the Users are dummies though.
	 * @throws TwitterException
	 *             If connection fails.
	 */
	public abstract List<Status> getTweets(String phrase)
			throws TwitterException;

	/**
	 * Use to find out the valid locations to get trends from.
	 * 
	 * @return The set of valid locations.
	 */
	public static Set<String> getLocations() {
		return null;
	}

	/**
	 * Adds the actual details to a dummy user. External call so has a cost
	 * 
	 * @param user
	 *            User to be filled
	 * @throws TwitterException
	 *             If connection fails.
	 */
	public abstract void fillUser(User user) throws TwitterException;

}
