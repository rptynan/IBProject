package uk.ac.cam.quebec.wikiproc;

import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.dbwrapper.DatabaseException;
import uk.ac.cam.quebec.kgsearchwrapper.KGConcept;
import uk.ac.cam.quebec.kgsearchwrapper.KGConceptGenerator;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;
import uk.ac.cam.quebec.wikiwrapper.WikiException;
import uk.ac.cam.quebec.wikiwrapper.WikiFetch;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javafx.util.Pair;

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
    private List<Pair<String, Double>> wikiConcepts;
    private List<Pair<String, Integer>> trendConcepts;
    private List<KGConcept> kgConcepts;
    private KGConceptGenerator kgConceptGenerator;

    public List<WikiArticle> getArticleList() {
        return articleList;
    }

    public List<Pair<String, Double>> getWikiConcepts() {
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

        kgConceptGenerator = new KGConceptGenerator();
        kgConcepts = new LinkedList<>();

        int averageCount = 0;
        for (Pair<String, Integer> concept : trendConcepts) {
            averageCount += concept.getValue();
        }
        if (!trendConcepts.isEmpty()) {
            averageCount /= trendConcepts.size();
        }

        // get KGConcepts for each trendConcept and put them all together
        for (Pair<String, Integer> trendConcept : trendConcepts) {
            for (KGConcept kgConcept : kgConceptGenerator.getKGConcepts(trendConcept.getKey(),
                    trendConcept.getValue() >= averageCount ? 2 : 1)) {
                if (kgConcept != null) {
                    kgConcept.setScore(kgConcept.getScore() * trendConcept.getValue());
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
            wikiConcepts.add(new Pair<String, Double>(kgConcept.getName(), kgConcept.getScore()));
        }
    }

    /**
     * Fetch the relevant Wiki articles.
     */
    private void fetchArticles() {
        articleList = new LinkedList<WikiArticle>();

        double averageScore = 0;
        for (Pair<String, Double> concept : wikiConcepts) {
            averageScore += concept.getValue();
        }
        if (!wikiConcepts.isEmpty()) {
            averageScore /= wikiConcepts.size();
        }

        List<WikiArticle> conceptArticles;
        for (Pair<String, Double> concept : wikiConcepts) {
            try {
                conceptArticles = WikiFetch.search(concept.getKey(),
                        concept.getValue() >= averageScore ? 2 : 1, 0);
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
        try {
            Database.getInstance().putWikiArticles(articleList, trend);
        } catch (DatabaseException ex) {
            System.err.println("Could not store the Wiki articles due to the following error:");
            ex.printStackTrace(System.err);
        }
    }

}
