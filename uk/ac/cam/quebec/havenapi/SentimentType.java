package uk.ac.cam.quebec.havenapi;

import java.util.HashMap;
import java.util.Map;

public enum SentimentType {

    POSITIVE("positive"),
    NEGATIVE("negative"),
    NEUTRAL("neutral");
    private final String sentimentType;
    private final static Map<String, SentimentType> CONSTANTS = new HashMap<String, SentimentType>();

    static {
        for (SentimentType c : values()) {
            CONSTANTS.put(c.sentimentType, c);
        }
    }

    SentimentType(String sentimentType) {
        this.sentimentType = sentimentType;
    }

    @Override
    public String toString() {
        return this.sentimentType;
    }

    public static SentimentType fromString(String sentimentType) {
        return CONSTANTS.get(sentimentType);
    }

}