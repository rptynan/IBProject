package uk.ac.cam.quebec.wikiproc;

import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.kgsearchwrapper.KGConcept;
import uk.ac.cam.quebec.kgsearchwrapper.KGConceptGenerator;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;
import uk.ac.cam.quebec.wikiwrapper.WikiException;
import uk.ac.cam.quebec.wikiwrapper.WikiFetch;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

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
    private List<WikiArticle> articleList;
    private List<String> wikiConcepts;
    private List<String> trendConcepts;
    private List<KGConcept> kgConcepts;
    private KGConceptGenerator kgConceptGenerator;

    public List<WikiArticle> getArticleList() {
        return articleList;
    }

    public List<String> getWikiConcepts() {
        return wikiConcepts;
    }

    public List<KGConcept> getKgConcepts() {
        return kgConcepts;
    }

    /**
     * WikiProcessor constructor.
     */
    public WikiProcessor() {
    }

    /**
     * Starts the wiki processing for a particular trend
     * @param trend The trend that is currently being processed.
     */
    public void process(Trend trend) {
        this.trend = trend;

        buildConcepts();
        fetchArticles();
        storeArticles();
    }

    /**
     * Builds a list of ordered concepts. The simplest way is just use the concepts in the Trend
     * object, but we can improve those using the Knowledge Graph API
     */
    private void buildConcepts() {
        trendConcepts = trend.getConcepts();
        wikiConcepts = trendConcepts; // return after this for simplest functionality

        kgConceptGenerator = new KGConceptGenerator();

        kgConcepts = new LinkedList<>();

        // get KGConcepts for each trendConcept and put them all together
        for (String trendConcept : trendConcepts) {
            for (KGConcept kgConcept : kgConceptGenerator.getKGConcepts(trendConcept, 3)) {
                if (kgConcept != null) {
                    kgConcepts.add(kgConcept);
                }
            }
        }

        // sort the KGconcepts decreasingly by confidence score
        kgConcepts.sort(new Comparator<KGConcept>() {
            @Override
            public int compare(KGConcept o1, KGConcept o2) {
                return Double.compare(o2.getScore(), o1.getScore()); // swap them if o2 is bigger
            }
        });

        wikiConcepts = new LinkedList<>();
        for (KGConcept kgConcept : kgConcepts) {
            wikiConcepts.add(kgConcept.getName());
        }
    }

    /**
     * Fetch the relevant Wiki articles.
     */
    private void fetchArticles() {
        articleList = new LinkedList<WikiArticle>();

        List<WikiArticle> conceptArticles;
        for (String concept : wikiConcepts) {
            try {
                conceptArticles = WikiFetch.search(concept, 2, 0);
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
     * Store the fetched WikiArticle in the database.
     */
    private void storeArticles() {
        Database.getInstance().putWikiArticles(articleList, trend);
    }

}
