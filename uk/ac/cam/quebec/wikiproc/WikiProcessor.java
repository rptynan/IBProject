package uk.ac.cam.quebec.wikiproc;

import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.dbwrapper.DatabaseException;
import uk.ac.cam.quebec.kgsearchwrapper.KGConcept;
import uk.ac.cam.quebec.kgsearchwrapper.KGConceptGenerator;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;
import uk.ac.cam.quebec.wikiwrapper.WikiEdit;
import uk.ac.cam.quebec.wikiwrapper.WikiException;
import uk.ac.cam.quebec.wikiwrapper.WikiFetch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javafx.util.Pair;

/**
 * Class responsible for Wikipedia processing.
 *
 * So far it is called by the Twitter processor on process(Trend trend), also getting the trend
 * there. It then gets the concepts identified by the Twitter processor, runs them through the
 * Knowledge graph wrapper and augments them, creating the augmentedConcepts.
 *
 * Using the augmentedConcepts as search terms, it fetches WikiArticles from wikipedia, processes
 * them and then stores them in the database.
 *
 * As for what processing is being done on the articles, first a relevance measure is calculated
 * based on the score of the concepts (both from Twitter and KGSearch) used to find it on Wikipedia,
 * then it also sets the popularity based on the number of views, the recency based on the timestamp
 * of the latest edit and the controversy based on the number of edits that have happened in the
 * 24 hours before the timestamp of the Trend.
 *
 * @author Tudor
 *
 */
public class WikiProcessor {

    private static final int CONCEPTS_LIMIT = 10;
    private static final int WIKI_EDITS_AT_ONCE = 10;
    private static final int WIKI_EDITS_LIMIT = 30;

    private Trend trend;
    private List<WikiArticle> articleList;
    private List<Pair<String, Double>> augmentedConcepts;
    private List<Pair<String, Integer>> trendConcepts;
    private List<KGConcept> kgConcepts;
    private KGConceptGenerator kgConceptGenerator;

    public List<WikiArticle> getArticleList() {
        return articleList;
    }

    public List<Pair<String, Double>> getAugmentedConcepts() {
        return augmentedConcepts;
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

        augmentConcepts();
        fetchArticles();
        removeDuplicateArticles();
        processArticles();
        storeArticles();
    }

    /**
     * Builds a list of ordered concepts. The simplest way is just use the concepts in the Trend
     * object, but we can improve those using the Knowledge Graph API
     */
    private void augmentConcepts() {
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
        int limit = CONCEPTS_LIMIT;
        for (Pair<String, Integer> trendConcept : trendConcepts) {
            --limit;
            if (limit < 0) {
                break;
            }
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

        augmentedConcepts = new LinkedList<>();
        for (KGConcept kgConcept : kgConcepts) {
            augmentedConcepts.add(new Pair<>(kgConcept.getName(), kgConcept.getScore()));
        }
    }

    /**
     * Removes duplicate Articles.
     */
    public void removeDuplicateArticles() {
        HashMap<Integer, Integer> position = new HashMap<>();

        List<WikiArticle> copy = articleList;
        articleList = new ArrayList<>();

        for (WikiArticle article : copy) {
            Integer id = article.getId();
            if (position.containsKey(id)) {
                int pos = position.get(id);
                articleList.get(pos).increaseRelevance(article.getRelevance());
            } else {
                articleList.add(article);
                position.put(id, articleList.size() - 1);
            }
        }
    }

    /**
     * Fetch the relevant Wiki articles.
     */
    private void fetchArticles() {
        articleList = new LinkedList<>();

        double averageScore = 0;
        for (Pair<String, Double> concept : augmentedConcepts) {
            averageScore += concept.getValue();
        }
        if (!augmentedConcepts.isEmpty()) {
            averageScore /= augmentedConcepts.size();
        }

        List<WikiArticle> conceptArticles;
        for (Pair<String, Double> concept : augmentedConcepts) {
            try {
                conceptArticles = WikiFetch.search(concept.getKey(),
                        concept.getValue() >= averageScore ? 2 : 1, 0);

                double relevance = concept.getValue();
                if (conceptArticles != null) {
                    for (WikiArticle article : conceptArticles) {
                        article.setRelevance(relevance);
                        relevance *= 0.9;
                        articleList.add(article);
                    }
                }
            } catch (WikiException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Do some processing on the Articles (relevance, recency, popularity, controversy)
     */
    private void processArticles() {
        for (WikiArticle article : articleList) {
            int views = 0;
            try {
                views = article.getViews();
            } catch (WikiException ex) {
                ex.printStackTrace();
            }
            article.setPopularity(1.0 * views);


            int count = 0;
            int afterTrend = 0;
            boolean more = true;
            while (more && count < WIKI_EDITS_LIMIT) {
                more = false;
                try {
                    List<WikiEdit> edits = article.getEdits(count + WIKI_EDITS_AT_ONCE);
                    if (edits.size() < count + WIKI_EDITS_AT_ONCE) {
                        break;
                    }

                    count = edits.size();
                    int newAfterTrend = 0;
                    for (WikiEdit edit : edits) {
                        if (edit.getTimeStamp().after(trend.getTimestamp())) {
                            newAfterTrend++;
                        }
                    }

                    if (newAfterTrend > afterTrend) {
                        afterTrend = newAfterTrend;
                        if (newAfterTrend - afterTrend >= WIKI_EDITS_AT_ONCE) {
                            more = true;
                        }
                    }

                } catch (WikiException ex) {
                    ex.printStackTrace();
                }
            }

            article.setControversy(1.0 * afterTrend);
            List<WikiEdit> edits = article.getCachedEdits();
            if (!edits.isEmpty()) {
                article.setRecency(1.0 * edits.get(0).getTimeStamp().getTime());
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
