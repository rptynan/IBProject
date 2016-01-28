package uk.ac.cam.quebec.wikiproc;

import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.twitterproc.Concept;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;
import uk.ac.cam.quebec.wikiwrapper.WikiException;
import uk.ac.cam.quebec.wikiwrapper.WikiFetch;

import java.util.LinkedList;

/**
 * Class responsible for Wikipedia processing. For the minimum viable product,
 * it should have the basic functionality that given a Trend and a list of
 * relevant Concepts it should identify relevant Wikipedia articles and
 * return a list of them on demand.
 *
 * @author Tudor
 *
 */
public class WikiProcessor {

    private Trend trend;
    private List<Concept> conceptList;
    private List<WikiArticle> articleList;

    /**
     * WikiProcessor constructor.
     */
    public WikiProcessor() {
    }

    /**
     * Starts the wiki processing for a particular trend
     * @param trend The trend that is currently being processed.
     * @param conceptList List of Concepts that are relevant to the processed
     *                    trend and can be used to identify relevant Wiki
     *                    articles.
     */
    public void process(Trend trend, List<Concept> conceptList) {
        this.trend = trend;
        this.conceptList = conceptList;

        fetchArticles();
        storeArticles();
    }

    /**
     * Fetch the relevant Wiki articles.
     */
    private void fetchArticles() {
        articleList = new LinkedList<WikiArticle>();

        List<WikiArticle> conceptArticles;
        for (Concept concept : conceptList) {
            try {
                conceptArticles = WikiFetch.search(concept.getString(), 100, 0);
                if (conceptArticles != null) {
                    for (WikiArticle article : conceptArticles) {
                        articleList.add(article);
                    }
                }
            } catch (WikiException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Store a processed WikiArticle in the database.
     * @param article The article to be stored in the database.
     */
    private void storeArticles() {
        Database.getInstance().putWikiArticles(articleList, trend);
    }

}
