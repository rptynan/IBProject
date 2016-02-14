
package uk.ac.cam.quebec.havenapi;

import winterwell.json.JSONArray;
import winterwell.json.JSONException;
import winterwell.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;


/**
 *
 * Class representing the response the API gives for one query. For a detailed description of the
 * fields and what they mean, check https://dev.havenondemand.com/apis/analyzesentiment#response
 *
 * ALL THE FIELDS MUST BE NON-NULL!!
 *
 * @author tudor
 */
@Immutable
public class SentimentAnalysis {

    private Aggregate aggregate;
    private List<Sentiment> negative = new ArrayList<>();
    private List<Sentiment> positive = new ArrayList<>();


    /**
     * Builds the response from a JSONObject
     * @param object the JSONObject representing the response
     */
    public SentimentAnalysis(JSONObject object) throws HavenException {
        try {
            aggregate = new Aggregate(object.getJSONObject("aggregate"));

            JSONArray array = object.getJSONArray("negative");
            for (int i = 0; i < array.length(); ++i) {
                negative.add(new Sentiment("negative", array.getJSONObject(i)));
            }

            array = object.getJSONArray("positive");
            for (int i = 0; i < array.length(); ++i) {
                positive.add(new Sentiment("positive", array.getJSONObject(i)));
            }
        } catch (JSONException ex) {
            throw new HavenException("Could not parse from JSON", ex);
        }
    }

    /**
     * @param aggregate The aggregate, must be non null
     * @param negative The negative sentiments, must be non null
     * @param positive The positive sentiments, must be non null
     */
    public SentimentAnalysis(Aggregate aggregate, List<Sentiment> negative,
                             List<Sentiment> positive) throws HavenException {
        if (aggregate == null || negative == null || positive == null) {
            throw new HavenException("Cannot have SentimentAnalysis with null fields");
        }
        for (Sentiment sent : negative) {
            if (sent == null) {
                throw new HavenException("Cannot have SentimentAnalysis with null fields");
            }
            if (sent.getSentimentType() != SentimentType.NEGATIVE) {
                throw new HavenException("Sentiment in the positive list is not positive");
            }
        }
        for (Sentiment sent : positive) {
            if (sent == null) {
                throw new HavenException("Cannot have SentimentAnalysis with null fields");
            }
            if (sent.getSentimentType() != SentimentType.POSITIVE) {
                throw new HavenException("Sentiment in the positive list is not positive");
            }
        }
        this.aggregate = aggregate;
        this.negative = negative;
        this.positive = positive;
    }

    public Aggregate getAggregate() {
        return aggregate;
    }

    /**
     * @return The negative sentiments
     */
    public List<Sentiment> getNegative() {
        return negative;
    }

    /**
     * @return The positive sentiments
     */
    public List<Sentiment> getPositive() {
        return positive;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("sentiment_analysis {\n");
        sb.append("\n").append(aggregate.toString()).append("\n");
        sb.append("NEGATIVE_SENTIMENTS [\n");
        for (Sentiment sentiment : negative) {
            sb.append(sentiment.toString());
        }
        sb.append("]\n\n");
        sb.append("POSITIVE_SENTIMENTS [\n");
        for (Sentiment sentiment : positive) {
            sb.append(sentiment.toString());
        }
        sb.append("]\n\n");
        sb.append("}\n");
        return sb.toString();
    }
}
