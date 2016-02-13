
package uk.ac.cam.quebec.havenapi;

import winterwell.json.JSONArray;
import winterwell.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * @author tudor
 */
public class SentimentAnalysis {

    private Aggregate aggregate;
    private List<Sentiment> negative = new ArrayList<Sentiment>();
    private List<Sentiment> positive = new ArrayList<Sentiment>();


    public SentimentAnalysis(JSONObject object) {
        aggregate = new Aggregate(object.getJSONObject("aggregate"));

        JSONArray array = object.getJSONArray("negative");
        for (int i = 0; i < array.length(); ++i) {
            negative.add(new Sentiment("negative", array.getJSONObject(i)));
        }

        array = object.getJSONArray("positive");
        for (int i = 0; i < array.length(); ++i) {
            positive.add(new Sentiment("positive", array.getJSONObject(i)));
        }
    }

    public SentimentAnalysis() {
    }

    /**
     * @param aggregate
     * @param negative
     * @param positive
     */
    public SentimentAnalysis(Aggregate aggregate,
                             List<Sentiment> negative, List<Sentiment> positive) {
        this.aggregate = aggregate;
        this.negative = negative;
        this.positive = positive;
    }

    public Aggregate getAggregate() {
        return aggregate;
    }

    /**
     * @param aggregate The aggregate
     */
    public void setAggregate(Aggregate aggregate) {
        this.aggregate = aggregate;
    }

    /**
     * @return The negative sentiments
     */
    public List<Sentiment> getNegative() {
        return negative;
    }

    /**
     * @param negative The negative sentiments
     */
    public void setNegative(List<Sentiment> negative) {
        this.negative = negative;
    }

    /**
     * @return The positive sentiments
     */
    public List<Sentiment> getPositive() {
        return positive;
    }

    /**
     * @param positive The positive sentiments
     */
    public void setPositive(List<Sentiment> positive) {
        this.positive = positive;
    }

}
