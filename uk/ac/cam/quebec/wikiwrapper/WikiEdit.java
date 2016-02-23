package uk.ac.cam.quebec.wikiwrapper;

import info.bliki.wiki.model.WikiModel;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

import winterwell.json.JSONArray;
import winterwell.json.JSONObject;

/**
 * Class representing a single edit to a Wikipedia article. It is content heavy
 * so costly to build completely.
 * 
 * @author Stuart
 *
 */
public class WikiEdit implements Serializable {

    private String comment;
    private String diff = null;
    private String content = null;
    private int id;
    private Date timeStamp;
    private WikiArticle article;

    /**
     * Constructs all of the edit bar the diff and article as these are the most
     * costly pieces of data
     * 
     * @param id
     *            Id of the edit
     * @param comment
     *            Editors comment
     * @param wikiTime
     *            String from Wikipedia representing time
     * @param article
     *            The associated article
     * @throws WikiException
     *             Throws exception if connection fails
     */
    @SuppressWarnings("deprecation")
    public WikiEdit(int id, String comment, String wikiTime, WikiArticle article)
            throws WikiException {
        this.id = id;
        this.comment = comment;
        this.article = article;
        timeStamp = new Date(
                Integer.parseInt(wikiTime.substring(0, 4), 10) - 1900,
                Integer.parseInt(wikiTime.substring(5, 7), 10) - 1,
                Integer.parseInt(wikiTime.substring(8, 10), 10),
                Integer.parseInt(wikiTime.substring(11, 13), 10),
                Integer.parseInt(wikiTime.substring(14, 16), 10),
                Integer.parseInt(wikiTime.substring(17, 19), 10));
    }

    /**
     * On first call it will retrieve the diff through an external call
     * (costly), afterwards it will just give the cached value. The format of
     * the diff can be discussed - I can remove wiki text and some of the HTML
     * but not quite sure what the final format will be.
     * 
     * @throws WikiException
     *             Throws exception if connection fails
     * @return The content of the diff of this edit with the previous one.
     */
    public String getDiff() throws WikiException {
        if (diff == null) {
            try {
                JSONObject json = WikiFetch
                        .getJSONfromAddress("https://en.wikipedia.org/w/"
                                + "api.php?action=query&prop=revisions&format=json&"
                                + "rvdiffto=prev&revids=" + id);
                json = json.getJSONObject("query").getJSONObject("pages");
                JSONArray array = json.names();
                array = json.getJSONObject(array.getString(0)).getJSONArray(
                        "revisions");
                diff = WikiModel.toHtml(array.getJSONObject(0)
                        .getJSONObject("diff").getString("*"));
                diff = diff.replace("href=\"/",
                        "href=\"https://en.wikipedia.org/wiki/");
            } catch (IOException e) {
                throw new WikiException("Connection to Wikipedia failed.",e);
            }
        }
        return diff;
    }

    /**
     * 
     * On first call it will retrieve the content through an external call
     * (costly), afterwards it will just give the cached value. Will either be
     * in html format or if you want will have further parsing done to it.
     * 
     * @throws WikiException
     *             Throws exception if connection fails
     * @return The content of the article after this edit.
     */
    public String getContent() throws WikiException {
        if (content == null) {
            try {
                JSONObject json = WikiFetch
                        .getJSONfromAddress("https://en.wikipedia.org/w/"
                                + "api.php?action=query&prop=revisions&format=json&"
                                + "rvprop=content&revids=" + id);
                json = json.getJSONObject("query").getJSONObject("pages");
                JSONArray array = json.names();
                array = json.getJSONObject(array.getString(0)).getJSONArray(
                        "revisions");
                content = WikiModel.toHtml(array.getJSONObject(0)
                        .getString("*"));
                content = content.replace("href=\"/",
                        "href=\"https://en.wikipedia.org/wiki/");
            } catch (IOException e) {
                throw new WikiException("Connection to Wikipedia failed.",e);
            }
        }
        return content;
    }

    public String getComment() {
        return comment;
    }

    public int getId() {
        return id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public WikiArticle getArticle() {
        return article;
    }

    @Override
    public String toString() {
        return comment;
    }

}
