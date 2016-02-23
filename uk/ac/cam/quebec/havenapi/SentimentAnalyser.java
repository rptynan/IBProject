package uk.ac.cam.quebec.havenapi;

import winterwell.json.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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
     * @param textQuery the text to analyse
     * @return a SentimentAnalysis object for the text query, if it was successful, or null if not
     */
    public static SentimentAnalysis getAnalysis(String textQuery) throws HavenException {
        if (textQuery == null || textQuery.isEmpty()) {
            throw new HavenException("The text passed for analysis is empty");
        }
        try {
            String urlString = URL_BASE + URLEncoder.encode(textQuery, "UTF-8");

            String jsonString = getJSONStringFromURL(urlString);
            JSONObject obj = new JSONObject(jsonString);

            return new SentimentAnalysis(obj);
        } catch (UnsupportedEncodingException ex) {
            throw new HavenException("Cannot encode query to URL", ex);
        } catch (MalformedURLException ex) {
            throw new HavenException("URL is incorrect", ex);
        } catch (IOException ex) {
            throw new HavenException("Can't read from the API response", ex);
        } catch (JSONException ex) {
            throw new HavenException("Could not parse from JSON", ex);
        }
    }

    private static String getJSONStringFromURL(String urlString) throws IOException {
        StringBuilder jsonString = new StringBuilder();
        URL url = new URL(urlString);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        int cp;
        while ((cp = reader.read()) != -1) {
            jsonString.append((char) cp);
        }

        return jsonString.toString();
    }

}
