package uk.ac.cam.quebec.wikiwrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import winterwell.json.JSONArray;
import winterwell.json.JSONException;
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
    private Double relevance = new Double(0.0);

    public Double getRelevance() {
        return relevance;
    }

    public void setRelevance(Double relevance) {
        this.relevance = relevance;
    }

    public void increaseRelevance(Double increase) {
        this.relevance += increase;
    }

    private Double popularity = new Double(0.0);

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    private Double controversy = new Double(0.0);

    public Double getControversy() {
        return controversy;
    }

    public void setControversy(Double controversy) {
        this.controversy = controversy;
    }

    private Double recency = new Double(0.0);

    public Double getRecency() {
        return recency;
    }

    public void setRecency(Double recency) {
        this.recency = recency;
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
                            + "format=json&explaintext=&titles=" + title
                            + "&exintro=1");

            json = json.getJSONObject("query").getJSONObject("pages");
            JSONArray names = json.names();
            extract = json.getJSONObject(names.getString(0)).getString(
                    "extract");
            id = json.getJSONObject(names.getString(0)).getInt("pageid");
            // For testing System.out.println("Article: " + this.title);

        } catch (IOException e) {
            throw new WikiException("Connection to Wikipedia failed.");
        }

    }

    /**
     * Method to get usage data for a page. Is gathered on first call so there
     * is likely to be a performance hit then. The data is daily so
     * it may not be that useful and perhaps better to rely on search.
     * 
     * @throws WikiException
     *             Throws exception if connection fails
     * @return The number of views of the page in the past day.
     */
    public int getViews() throws WikiException {
        if (views >= 0)
            return views;
        else {
            try {
                Date today = new Date();
                Date yesterday = new Date(today.getTime()-86400000L);
                SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd"); 
                JSONObject json = WikiFetch
                        .getJSONfromAddress("https://wikimedia.org/api/rest_v1/metrics/pageviews/per-article/en.wikipedia.org/all-access/all-agents/"
                                + title.replace(" ", "%20")+ "/daily/"+ dt.format(yesterday)+"/"+dt.format(today));
                return json.getJSONArray("items").getJSONObject(0).getInt("views");
               

            } catch (IOException e) {
                throw new WikiException("Connection to view api failed.");
            }
            catch (JSONException e){
                throw new WikiException("Invalid Article.");
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
            // edits = new LinkedList<WikiEdit>(); // We only get as many as we
            // need.
            try {
                JSONObject json = WikiFetch
                        .getJSONfromAddress("https://en.wikipedia.org/w/api.php?"
                                + "action=query&prop=revisions&format=json&rvprop=ids%"
                                + "7Ctimestamp%7Ccomment&"
                                + "rvlimit="
                                + (editCount - edits.size())
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
