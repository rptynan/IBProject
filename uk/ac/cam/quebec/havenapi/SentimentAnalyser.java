package uk.ac.cam.quebec.havenapi;

import winterwell.json.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Class for sending sentiment analysis requests to the Haven API.
 *
 * @author tudor
 */
public class SentimentAnalyser {

    private static String URL_BASE =
            "https://api.havenondemand.com/1/api/sync/analyzesentiment/v1?apikey="
                    + APIConstants.getApiKey()
                    + "&text=";

    /**
     *
     * @param textQuery
     * @return a SentimentAnalysis object for the text query, if it was successful, or null if not
     */
    public static SentimentAnalysis getAnalysis(String textQuery) {
        try {
            String urlString = URL_BASE + URLEncoder.encode(textQuery, "UTF-8");

            String jsonString = getJSONStringFromURL(urlString);
            JSONObject obj = new JSONObject(jsonString);

            return new SentimentAnalysis(obj);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static String getJSONStringFromURL(String urlString) {
        try {
            StringBuilder jsonString = new StringBuilder();
            URL url = new URL(urlString);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            int cp;
            while ((cp = reader.read()) != -1) {
                jsonString.append((char) cp);
            }

            return jsonString.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) throws Exception {
        String text = "I personally absolutely hate tacos";

        getAnalysis(text);
    }
}
