package uk.ac.cam.quebec.havenapi;

/**
 * Testing for the Sentiment Analyser.
 *
 * Example response:
 *
 * QUERY: I absolutely love tacos
 *
 * sentiment_analysis {
 *     aggregate {
 *         score: 0.9107458225962982
 *         sentiment_type: positive
 *     }
 *     NEGATIVE_SENTIMENTS [
 *     ]
 *
 *     POSITIVE_SENTIMENTS [
 *         sentiment_obj {
 *             sentiment_type: positive
 *             sentiment: absolutely love
 *             topic: tacos
 *             score: 0.9107458225962982
 *             normalized_text: I absolutely love tacos
 *             normalized_length: 23.0
 *             normalized_text: I absolutely love tacos
 *             original_length: 23.0
 *             original_text: I absolutely love tacos
 *         }
 *     ]
 * }
 *
 * @author tudor
 */
public class SentimentAnalyserTest {

    public static void main(String[] args) throws HavenException {
        String[] queries = new String[] {
                "I love tacos",
                "I absolutely love tacos",
                "Trump is the worse",
                "Larry is something else"
        };

        for (String query : queries) {
            System.out.println("\nQUERY: " + query + "\n");
            SentimentAnalysis analysis = SentimentAnalyser.getAnalysis(query);
            System.out.println(analysis.toString());
        }
    }

}
