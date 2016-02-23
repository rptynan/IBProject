package uk.ac.cam.quebec.twitterwrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Status;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import winterwell.jtwitter.Twitter_Users;
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
public class TwitterLink {

    /**
     * OAuth client needed for creating Twitter Links
     */
    private static OAuthSignpostClient oAuthClient = null;

    /**
     * Username of the Twitter account we are using
     */
    private static String username = null;

    /**
     * Map to lookup the woeids for the location strings. Is initialised in
     * login the shouldn't be changed.
     */
    private static Map<String, Integer> locationLookup = new HashMap<String, Integer>(
            8);
    /**
     * Underlying JTwitter twitter object.
     */
    private Twitter twitter;

    /**
     * Extra object needed to fill in dummy users.
     */
    private Twitter_Users twitterUsers;

    /**
     * Call one to perform login and then build multiple TwitterLink objects
     * from the single login. It also initialises the location map.
     * 
     * @param AccessToken
     *            Token for user
     * @param AccessTokenSecret
     *            Secret for user.
     * @param OAuthKey
     *            Key for app.
     * @param OAuthSecret
     *            Secret for app.
     * @param username
     *            Name of the user.
     * @throws TwitException
     *             If the login fails.
     */
    public static void login(String oAuthKey, String oAuthSecret,
            String accessToken, String accessTokenSecret, String username)
            throws TwitException {
        locationLookup.put("World", 1);
        locationLookup.put("UK", 23424975);
        locationLookup.put("USA", 23424977);
        locationLookup.put("Australia", 23424748);
        locationLookup.put("Ireland", 23424803);
        locationLookup.put("India", 23424848);
        locationLookup.put("Seattle", 2490383);
        locationLookup.put("London", 44418);
        try {
            oAuthClient = new OAuthSignpostClient(oAuthKey, oAuthSecret,
                    accessToken, accessTokenSecret);
            TwitterLink.username = username;
        } catch (TwitterException e) {
            throw new TwitException("Login Failed.",e);
        }

    }

    /**
     * Constructor for a single Twitter link. Not to be used concurrently - make
     * lots instead!
     * 
     * @throws TwitException
     *             Must perform a SINGLE STATIC LOGIN else this is thrown
     */

    public TwitterLink() throws TwitException {
        try {
            if (oAuthClient != null && username != null) {
                twitter = new Twitter(username, oAuthClient);
                twitterUsers = twitter.users();
            } else
                throw new TwitException("No static login performed.");
        } catch (TwitterException e) {
            throw new TwitException("Connection to Twitter failed.",e);
        }
    }

    /**
     * Get the trends by specifying a location.
     * 
     * @param location
     *            A VALID location - these are specified in the location set.
     * @return The trends as unparsed strings.
     * @throws TwitException
     *             It connection fails or place invalid
     */
    public List<String> getTrends(String location) throws TwitException {
        try {
            List<String> x = twitter.getTrends(locationLookup.get(location));
            // For testing System.out.println(x);
            return x;
        } catch (TwitterException e) {
            throw new TwitException("Connection to Twitter failed.",e);
        }
    }

    /**
     * Perform a search to get a page of tweets relating to a term. The Users
     * associated with the tweet are dummies and need to be filled with the
     * fillUser method.
     * 
     * @param phrase
     *            The string to search with
     * @return A list of JTwitter status objects (each representing a tweet) -
     *         the Users are dummies though.
     * @throws TwitException
     *             If connection fails.
     */
    public List<Status> getTweets(String phrase) throws TwitException {
        try {
            return twitter.search(phrase);
        } catch (TwitterException e) {
            throw new TwitException("Connection to Twitter failed.",e);
        }
    }

    /**
     * Use to find out the valid locations to get trends from.
     * 
     * @return The set of valid locations.
     */
    public static Set<String> getLocations() {
        return locationLookup.keySet();
    }

    /**
     * Adds the actual details to a dummy user. External call so has a cost
     * 
     * @param user
     *            User to be filled
     * @throws TwitException
     *             If connection fails.
     */
    public void fillUser(User user) throws TwitException {
        try {
            user = twitterUsers.getUser(user.screenName);
        } catch (TwitterException e) {
            throw new TwitException("Connection to Twitter failed.",e);
        }

    }

}
