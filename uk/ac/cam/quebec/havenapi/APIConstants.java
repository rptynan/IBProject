package uk.ac.cam.quebec.havenapi;

/**
 * Constants used for the Haven Sentiment Analysis API
 *
 * @author Tudor
 */
public final class APIConstants {

    // TODO: this key will expire in 24 hours and we will use a new one, set from the config file
    // through the setCredentials method
    private static String API_KEY = "b8f620e6-c509-4828-a53a-12679351abe5";

    public static String getApiKey() {
        return API_KEY;
    }

    public static void setCredentials(String credentials) {
        API_KEY = credentials;
    }

}
