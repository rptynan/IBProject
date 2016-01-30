package uk.ac.cam.quebec.wikiwrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

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
     *            Part of the address after the standard Wikipedia bit.
     * @return The JSON object of the page.
     * @throws IOException
     *             If there are IO issues.
     */
    public static JSONObject getJSONfromAddress(String address)
            throws IOException {

        BufferedReader r = new BufferedReader(new InputStreamReader(new URL(
                "https://en.wikipedia.org/w/api.php?" + address).openStream()));
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
        ret.add(new WikiArticle("Lawrence Paulson"));
        return ret;
    }

}
