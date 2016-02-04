package uk.ac.cam.quebec.wikiproc;

import uk.ac.cam.quebec.kgsearchwrapper.KGConcept;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;

/**
 * Testing for the wiki processor.
 *
 * @author tudor
 */
public class WikiProcessorTest {

    public static void main(String[] args) {
        Trend trend = new Trend("president", "us", 0);
        trend.getConcepts().add("barack");
        trend.getConcepts().add("president of us");

        WikiProcessor wikiProcessor = new WikiProcessor();
        wikiProcessor.process(trend);

        System.out.println("\n>>>>> CONCEPTS: <<<<<\n");
        for (String concept : wikiProcessor.getWikiConcepts()) {
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
