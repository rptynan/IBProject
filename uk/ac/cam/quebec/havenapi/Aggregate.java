package uk.ac.cam.quebec.havenapi;

import winterwell.json.JSONObject;

public class Aggregate {

    private double score;
    private SentimentType sentimentType;

    public Aggregate(JSONObject object) {
        score = object.getDouble("score");
        sentimentType = SentimentType.fromString(object.getString("sentiment"));
    }

    public Aggregate() {
    }

    /**
     *
     * @param sentimentType the sentiment type
     * @param score the score
     */
    public Aggregate(double score, SentimentType sentimentType) {
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
     * @param score The score
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * @return The sentiment type
     */
    public SentimentType getSentimentType() {
        return sentimentType;
    }

    /**
     * @param sentiment The sentiment type
     */
    public void setSentiment(SentimentType sentiment) {
        this.sentimentType = sentiment;
    }

}
