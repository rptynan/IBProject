package uk.ac.cam.quebec.twitterwrapper.test;

import java.util.List;

import uk.ac.cam.quebec.twitterwrapper.TwitException;
import uk.ac.cam.quebec.twitterwrapper.TwitterLink;
import winterwell.jtwitter.Status;
import winterwell.jtwitter.User;

/**
 * A bunch of test cases for the Twitter Wrapper
 * 
 * @author Stuart
 *
 */
public class Test {

    /**
     * Need to supply the oAuth stuff at the command line.
     * 
     * @param args
     *            [String oAuthKey, String oAuthSecret, String accessToken,
     *            String accessTokenSecret, String username]
     * @throws TwitException
     */
    public static void main(String[] args) {
        TwitterLink link;
        List<String> trends;
        Status tweet;
        User user;
        System.out.println("Should give 'No static login performed.':");
        try {
            link = new TwitterLink();
        } catch (TwitException e) {
            System.out.println(e.toString());
        }
        try {
            TwitterLink.login(args[0], args[1], args[2], args[3], args[4]);
            link = new TwitterLink();
            System.out.println("Should give some Irish-looking trends:");
            trends = link.getTrends("Ireland");
            System.out.println(trends);
            tweet = link.getTweets(trends.get(0)).get(0);
            System.out
                    .println("Should give a tweet related to the first trend:");
            System.out.println(tweet);
            user = tweet.getUser();
            link.fillUser(user);
            System.out
                    .println("Should give a username and a number of followers:");
            System.out.println(user + ": " + user.followersCount);
            System.out.println("Should give a list of places:");
            System.out.println(TwitterLink.getLocations());

        } catch (TwitException e) {
            System.err.println(e.toString());

        }

    }

}
