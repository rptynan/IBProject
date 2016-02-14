package uk.ac.cam.quebec.kgsearchwrapper;

/**
 * Constants used for the Google Knowledge Graph API
 *
 * @author Tudor
 */
public final class APIConstants {

    // TODO: this key will expire in 24 hours and we will use a new one, set from the config file
    // through the setCredentials
    private static String API_KEY = "AIzaSyBEAaUAzNW4eVxhg--sD5IndQP3ZoCX2go";

    public static String getApiKey() {
        return API_KEY;
    }

    public static void setCredentials(String credentials) {
        API_KEY = credentials;
    }

}
