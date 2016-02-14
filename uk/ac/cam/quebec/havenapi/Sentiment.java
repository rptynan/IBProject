package uk.ac.cam.quebec.havenapi;

import javax.annotation.concurrent.Immutable;

import winterwell.json.JSONException;
import winterwell.json.JSONObject;

/**
 * Class representing a sentiment.
 *
 * ALL THE FIELDS MUST BE NON-NULL!!
 *
 * @author tudor
 */
@Immutable
public class Sentiment {

    private SentimentType sentimentType;
    private double normalizedLength;
    private String normalizedText;
    private double originalLength;
    private String originalText;
    private double score;
    private String sentiment;
    private String topic;

    /**
     * Builds a sentiment from a JSONObject and a Sentiment Type.
     * @param sentimentType the sentiment type
     * @param object the JSONObject representing the sentiment
     */
    public Sentiment(String sentimentType, JSONObject object) throws HavenException {
        try {
            this.sentimentType = SentimentType.fromString(sentimentType);

            normalizedLength = object.getDouble("normalized_length");
            normalizedText = object.getString("normalized_text");
            originalLength = object.getDouble("original_length");
            originalText = object.getString("original_text");

            score = object.getDouble("score");
            sentiment = object.getString("sentiment");
            topic = object.getString("topic");
        } catch (JSONException ex) {
            throw new HavenException("Could not parse from JSON", ex);
        }

        if (sentimentType == null || normalizedText == null || originalText == null
                || sentiment == null || topic == null) {
            throw new HavenException("Cannot have Sentiment with null fields");
        }
    }

    /**
     * 
     * @param topic the topic
     * @param sentiment the sentiment
     * @param normalizedLength the length
     * @param score the score
     * @param originalText the original text
     * @param normalizedText the normalized text
     * @param originalLength the original length
     */
    public Sentiment(SentimentType sentimentType, double normalizedLength, String normalizedText,
                     double originalLength, String originalText, double score,
                     String sentiment, String topic) throws HavenException {
        if (sentimentType == null || normalizedText == null || originalText == null
                || sentiment == null || topic == null) {
            throw new HavenException("Cannot have Sentiment with null fields");
        }
        this.sentimentType = sentimentType;
        this.normalizedLength = normalizedLength;
        this.normalizedText = normalizedText;
        this.originalLength = originalLength;
        this.originalText = originalText;
        this.score = score;
        this.sentiment = sentiment;
        this.topic = topic;
    }

    /**
     * @return The sentimentType
     */
    public SentimentType getSentimentType() {
        return sentimentType;
    }

    /**
     * @return The normalizedLength
     */
    public double getNormalizedLength() {
        return normalizedLength;
    }

    /**
     * @return The normalizedText
     */
    public String getNormalizedText() {
        return normalizedText;
    }

    /**
     * @return The originalLength
     */
    public double getOriginalLength() {
        return originalLength;
    }

    /**
     * @return The originalText
     */
    public String getOriginalText() {
        return originalText;
    }

    /**
     * @return The score
     */
    public double getScore() {
        return score;
    }

    /**
     * @return The sentiment
     */
    public String getSentiment() {
        return sentiment;
    }

    /**
     * @return The topic
     */
    public String getTopic() {
        return topic;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("sentiment_obj {\n");
        sb.append("    sentiment_type: ").append(sentimentType.toString()).append("\n");
        sb.append("    sentiment: ").append(sentiment).append("\n");
        sb.append("    topic: ").append(topic).append("\n");
        sb.append("    score: ").append(score).append("\n");
        sb.append("    normalized_text: ").append(normalizedText).append("\n");
        sb.append("    normalized_length: ").append(normalizedLength).append("\n");
        sb.append("    normalized_text: ").append(normalizedText).append("\n");
        sb.append("    original_length: ").append(originalLength).append("\n");
        sb.append("    original_text: ").append(originalText).append("\n");
        sb.append("}\n");

        return sb.toString();
    }
}
