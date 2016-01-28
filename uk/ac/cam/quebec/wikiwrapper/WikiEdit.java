package uk.ac.cam.quebec.wikiwrapper;

import java.time.Instant;
import java.util.Date;

/**
 * Class representing a single edit to a Wikipedia article. It is content heavy
 * so costly to build completely.
 * 
 * @author Stuart
 *
 */
public abstract class WikiEdit {

    private String comment;
    private String diff;
    private String content;
    private String id;
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
    public WikiEdit(String id, String comment, String wikiTime,
            WikiArticle article) throws WikiException {
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
    public abstract String getDiff() throws WikiException;

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
    public abstract String getContent() throws WikiException;

    public String getComment() {
        return comment;
    }

    public String getId() {
        return id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public WikiArticle getArticle() {
        return article;
    }

}
