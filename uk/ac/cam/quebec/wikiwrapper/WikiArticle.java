package uk.ac.cam.quebec.wikiwrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import winterwell.json.JSONArray;
import winterwell.json.JSONObject;

/**
 * Class to represent a single Wikipedia article
 * 
 * @author Stuart
 * 
 */
public class WikiArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String extract;
    private int id;

    public String getTitle() {
        return title;
    }

    public String getExtract() {
        return extract;
    }
    public int getId() {
        return id;
    }

    private int views = -1;
    private List<WikiEdit> edits = new LinkedList<WikiEdit>();

    /**
     * Processing stuff
     */
    private Double relevance;

    public Double getRelevance() {
        return relevance;
    }

    public void setRelevance(Double relevance) {
        this.relevance = relevance;
    }

    public void increaseRelevance(Double increase) {
        this.relevance += increase;
    }

    /**
     * Constructor for an article. For performance reasons only the extract is
     * gathered on initialisation.
     * 
     * @param title
     *            The correct title for the article
     * @throws WikiException
     *             Throws exception if connection fails
     */
    public WikiArticle(String title) throws WikiException {
        this.title = title;
        try {
            title = title.replace(" ", "%20");
            JSONObject json = WikiFetch
                    .getJSONfromAddress("https://en.wikipedia.org/w/api.php?"
                            + "action=query&prop=extracts&"
                            + "format=json&explaintext=&titles=" + title);
            
            json = json.getJSONObject("query").getJSONObject("pages");
            JSONArray names = json.names();
            extract = json.getJSONObject(names.getString(0)).getString(
                    "extract");
            id = json.getJSONObject(names.getString(0)).getInt(
                    "pageid");
            // For testing System.out.println("Article: " + this.title);
 
        } catch (IOException e) {
            throw new WikiException("Connection to Wikipedia failed.");
        }

    }

    /**
     * Method to get usage data for a page. Is gathered on first call so there
     * is likely to be a performance hit then. The data is monthly not daily so
     * it may not be that useful and perhaps better to rely on search.
     * 
     * @throws WikiException
     *             Throws exception if connection fails
     * @return The number of views of the page in the past 30 days.
     */
    public int getViews() throws WikiException {
        if (views >= 0)
            return views;
        else {
            try {

                JSONObject json = WikiFetch
                        .getJSONfromAddress("http://stats.grok.se/json/en/latest30/"
                                + title.replace(" ", "%20"));
                json = json.getJSONObject("daily_views");
                Iterator keys = json.keys();
                views = 0;
                while (keys.hasNext()) {
                    views += json.getInt((String) keys.next());
                }
                return views;

            } catch (IOException e) {
                throw new WikiException("Connection to stats.grok.se failed.");
            }
        }
    }

    /**
     * Forms the URL of the page.
     * 
     * @return URL as a string
     */
    public String getURL() {

        return "https://en.wikipedia.org/wiki/" + title.replace(" ", "_");
    }

    /**
     * Method to get the edits list. If the editCount is less than or equal to
     * the current length of the edit list then there is no call to Wikipeda and
     * the cached list is returned. However if it is greater than then the
     * entire edit list will be rebuilt (costly).
     * 
     * @param editCount
     *            Number of edits wanted
     * @return List of the edits
     * @throws WikiException
     *             Throws exception if connection fails
     */

    public List<WikiEdit> getEdits(int editCount) throws WikiException {
        if (editCount <= edits.size()) {
            List<WikiEdit> ret = new LinkedList<WikiEdit>();
            for (WikiEdit e : edits) {
                if (editCount == 0)
                    break;
                ret.add(e);
                editCount--;
            }
            return ret;
        } else {
            edits = new LinkedList<WikiEdit>();
            try {
                JSONObject json = WikiFetch
                        .getJSONfromAddress("https://en.wikipedia.org/w/api.php?"
                                + "action=query&prop=revisions&format=json&rvprop=ids%"
                                + "7Ctimestamp%7Ccomment&"
                                + "rvlimit="
                                + editCount
                                + "&titles="
                                + title.replace(" ", "%20"));
                json = json.getJSONObject("query").getJSONObject("pages");
                JSONArray array = json.names();
                array = json.getJSONObject(array.getString(0)).getJSONArray(
                        "revisions");
                int len = array.length();
                for (int i = 0; i < len; i++) {
                    json = array.getJSONObject(i);
                    edits.add(new WikiEdit(json.getInt("revid"), json
                            .getString("comment"), json.getString("timestamp"),
                            this));
                }
                return edits;

            } catch (IOException e) {
                throw new WikiException("Connection to Wikipedia failed.");
            }
        }

    }

    /**
     * Method that return the list of edits already gathered. Will never make a
     * web query.
     * 
     * @return List of the edits.
     */
    public List<WikiEdit> getCachedEdits() {
        return edits;
    }

    /**
     * Removes all cached edits
     */
    public void clearCachedEdits() {
        edits = new LinkedList<WikiEdit>();
    };

    @Override
    public String toString() {
        return title;
    }

}
