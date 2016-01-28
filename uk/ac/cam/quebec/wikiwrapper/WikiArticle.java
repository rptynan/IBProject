package uk.ac.cam.quebec.wikiwrapper;

import java.util.LinkedList;
import java.util.List;

/**
 * Class to represent a single Wikipedia article
 * 
 * @author Stuart
 * 
 */
public class WikiArticle {

    private String title;
    private String extract;

    public String getTitle() {
        return title;
    }

    public String getExtract() {
        return extract;
    }

    private int views;
    private List<WikiEdit> edits = new LinkedList<WikiEdit>();

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
        extract = "Lawrence Charles Paulson (born 1955) is a professor at the University of Cambridge Computer Laboratory and a fellow of Clare College, Cambridge. \n\n\n== Education ==\nPaulson graduated from the California Institute of Technology in 1977, and obtained his PhD in Computer Science from Stanford University under the supervision of John L. Hennessy.\n\n\n== Research ==\nPaulson came to the University of Cambridge in 1983 and became a Fellow of Clare College, Cambridge in 1987. He is best known for the cornerstone text on the programming language ML, ML for the Working Programmer. His research is based around the interactive theorem prover Isabelle, which he introduced in 1986. He has worked on the verification of cryptographic protocols using inductive definitions, and he has also formalized the constructible universe of Kurt G\u00f6del. Recently he has built a new theorem prover, MetiTarski, for real-valued special functions.\n\n\n== Teaching ==\nPaulson teaches two undergraduate lecture courses on the Computer Science Tripos, entitled Foundations of Computer Science (which introduces functional programming) and Logic and Proof(which covers automated theorem proving and related methods).\n\n\n== Personal life ==\nPaulson has two children by his first wife, Dr Susan Mary Paulson, who died in 2010. He is now married to Dr Elena Tchougounova.\n\n\n== Awards and honours ==\nPaulson is a Fellow of the Association for Computing Machinery (2008).\n\n\n== References ==";
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
    public int getViews() throws WikiException{
        views = 69;
        return views;
    }
  

    /**
     * Forms the URL of the page.
     * 
     * @return URL as a string
     */
    public String getURL(){
        return "https://en.wikipedia.org/wiki/Lawrence_Paulson";
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

    public  List<WikiEdit> getEdits(int editCount) throws WikiException{
        return edits;
    }

    /**
     * Method that return the list of edits already gathered. Will never make a
     * web query.
     * 
     * @return List of the edits.
     */
    public List<WikiEdit> getCachedEdits(){
        return edits;
    }

    /**
     * Removes all cached edits
     */
    public void clearCachedEdits(){
        edits = new LinkedList<WikiEdit>();
    };

}
