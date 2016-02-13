package uk.ac.cam.quebec.havenapi;

import javax.annotation.concurrent.Immutable;

import winterwell.json.JSONException;
import winterwell.json.JSONObject;

/**
 * Class representing the aggregate
 *
 * ALL THE FIELDS MUST BE NON-NULL!!
 *
 * @author tudor
 */
@Immutable
public class Aggregate {

    private double score;
    private SentimentType sentimentType;

    /**
     * Builds the aggregate from a JSONObject
     * @param object the JSONObject representing the aggregate
     */
    public Aggregate(JSONObject object) throws HavenException {
        try {
            score = object.getDouble("score");
            sentimentType = SentimentType.fromString(object.getString("sentiment"));
        } catch (JSONException ex) {
            throw new HavenException("Could not parse from JSON", ex);
        }
    }

    /**
     *
     * @param sentimentType the sentiment type
     * @param score the score
     */
    public Aggregate(double score, SentimentType sentimentType) throws HavenException {
        if (sentimentType == null) {
            throw new HavenException("Cannot have Aggregate with null fields");
        }
        this.score = score;
        this.sentimentType = sentimentType;
    }

    /**
     * @return The score
     */
    public double getScore() {
        return score;
    }

    /**
     * @return The sentiment type
     */
    public SentimentType getSentimentType() {
        return sentimentType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("aggregate {\n");
        sb.append("    score: ").append(score).append("\n");
        sb.append("    sentiment_type: ").append(sentimentType.toString()).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
