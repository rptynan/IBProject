package uk.ac.cam.quebec.havenapi;

import winterwell.json.JSONObject;

public class Sentiment {

    private SentimentType sentimentType;
    private double normalizedLength;
    private String normalizedText;
    private double originalLength;
    private String originalText;
    private double score;
    private String sentiment;
    private String topic;

    public Sentiment(String sentimentType, JSONObject object) {
        this.sentimentType = SentimentType.fromString(sentimentType);

        normalizedLength = object.getDouble("normalized_length");
        normalizedText = object.getString("normalized_text");
        originalLength = object.getDouble("original_length");
        originalText = object.getString("original_text");

        score = object.getDouble("score");
        sentiment = object.getString("sentiment");
        topic = object.getString("topic");
    }

    public Sentiment() {
    }

    /**
     * 
     * @param topic
     * @param sentiment
     * @param normalizedLength
     * @param score
     * @param originalText
     * @param normalizedText
     * @param originalLength
     */
    public Sentiment(SentimentType sentimentType, double normalizedLength, String normalizedText,
                     double originalLength, String originalText, double score,
                     String sentiment, String topic) {
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
     * @param sentimentType The sentimentType
     */
    public void setSentimentType(SentimentType sentimentType) {
        this.sentimentType = sentimentType;
    }

    /**
     * @return The normalizedLength
     */
    public double getNormalizedLength() {
        return normalizedLength;
    }

    /**
     * @param normalizedLength The normalized_length
     */
    public void setNormalizedLength(double normalizedLength) {
        this.normalizedLength = normalizedLength;
    }

    /**
     * @return The normalizedText
     */
    public String getNormalizedText() {
        return normalizedText;
    }

    /**
     * @param normalizedText The normalized_text
     */
    public void setNormalizedText(String normalizedText) {
        this.normalizedText = normalizedText;
    }

    /**
     * @return The originalLength
     */
    public double getOriginalLength() {
        return originalLength;
    }

    /**
     * @param originalLength The original_length
     */
    public void setOriginalLength(double originalLength) {
        this.originalLength = originalLength;
    }

    /**
     * @return The originalText
     */
    public String getOriginalText() {
        return originalText;
    }

    /**
     * @param originalText The original_text
     */
    public void setOriginalText(String originalText) {
        this.originalText = originalText;
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
     * @return The sentiment
     */
    public String getSentiment() {
        return sentiment;
    }

    /**
     * @param sentiment The sentiment
     */
    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    /**
     * @return The topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * @param topic The topic
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

}
