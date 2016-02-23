package uk.ac.cam.quebec.wikiwrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import winterwell.json.JSONArray;
import winterwell.json.JSONException;
import winterwell.json.JSONObject;

/**
 * Class providing methods to get Wikipedia articles. Is uninstantiable.
 * 
 * @author Stuart
 * 
 */
public class WikiFetch {

    /**
     * Blocks construction.
     */
    private WikiFetch() {
    }

    /**
     * Gets the parsed JSON of a WIki API call. Public not default only for
     * testing reasons. Only the concern of the wikiwrapper.
     * 
     * @param address
     *            The web address.
     * @return The JSON object of the page.
     * @throws IOException
     *             If there are IO issues.
     */
    public static JSONObject getJSONfromAddress(String address)
            throws IOException {

        BufferedReader r = new BufferedReader(new InputStreamReader(new URL(
                address).openStream()));
        String str = null;
        StringBuilder sb = new StringBuilder(32768);
        try {
            while ((str = r.readLine()) != null) {
                sb.append(str);
            }
        } finally {
            r.close();
        }
        return new JSONObject(sb.toString());

    }

    /**
     * Search Wikipedia for articles simply by supplying a string. Can return an
     * empty list.
     * 
     * @param searchTerm
     *            String to search with.
     * @param max
     *            Maximum number of articles to return.
     * @param edits
     *            Number of edits to put with each article. Make zero to prevent
     *            an extra query for edits to be made.
     * @return List of fetched articles ordered in the order that WIkipedia
     *         gives them. Can be empty.
     * @throws WikiException
     *             Throws exception if connection fails.
     */

    public static List<WikiArticle> search(String searchTerm, int max, int edits)
            throws WikiException {
        List<WikiArticle> ret = new LinkedList<WikiArticle>();
        JSONArray array;
        try {
            JSONObject json = getJSONfromAddress("https://en.wikipedia.org/w/api.php?"
                    + "action=query&list=search&format=json&srsearch="
                    + searchTerm.replace(" ", "%20") + "&srlimit=" + max);
            try{
            array = json.getJSONObject("query")
                    .getJSONArray("search");
            }catch(JSONException e){
                return ret;
            }
           
            int len = array.length();
            WikiArticle wiki;
            for (int i = 0; i < len; i++) {
                try{
                wiki = new WikiArticle(array.getJSONObject(i)
                        .getString("title"));
                if (edits > 0)
                    wiki.getEdits(edits);
                ret.add(wiki);
                }catch(JSONException e){
                    continue;
                }
            }
            return ret;
        } catch (IOException e) {
            throw new WikiException("Connection to Wikipedia failed.",e);
        }

    }

}
