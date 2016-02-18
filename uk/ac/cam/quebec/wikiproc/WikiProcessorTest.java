package uk.ac.cam.quebec.wikiproc;

import uk.ac.cam.quebec.kgsearchwrapper.KGConcept;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;

import javafx.util.Pair;

/**
 * Testing for the wiki processor.
 *
 * @author tudor
 */
public class WikiProcessorTest {

    public static void main(String[] args) {
        Trend trend = new Trend("president", "us", 0);
        trend.getConcepts().add(new Pair<String, Integer>("barack", 10));
        trend.getConcepts().add(new Pair<String, Integer>("president of us", 10));

        WikiProcessor wikiProcessor = new WikiProcessor();
        wikiProcessor.process(trend);

        System.out.println("\n>>>>> CONCEPTS: <<<<<\n");
        for (Pair<String, Double> concept : wikiProcessor.getAugmentedConcepts()) {
            System.out.println(concept);
        }

        System.out.println("\n>>>>> WIKI ARTICLES: <<<<<\n");
        for (WikiArticle article : wikiProcessor.getArticleList()) {
            System.out.println(article.getTitle());
        }

        System.out.println("\n>>>>> KG Concepts: <<<<<\n");
        for (KGConcept concept : wikiProcessor.getKgConcepts()) {
            System.out.println(concept.toString());
        }
    }
}
