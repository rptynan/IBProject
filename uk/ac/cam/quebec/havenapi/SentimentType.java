package uk.ac.cam.quebec.havenapi;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing a Sentiment type.
 *
 * @author tudor
 */
public enum SentimentType {

    POSITIVE("positive"),
    NEGATIVE("negative"),
    NEUTRAL("neutral");
    private final String sentimentType;
    private final static Map<String, SentimentType> CONSTANTS =
            new HashMap<>();

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

    /**
     * Builds the enum from a string.
     * The accepted values are "positive", "negative" and "neutral"
     * @param sentimentType the sentiment type as a string
     * @return an enum instance representing the given string
     * @throws HavenException if the string is not a correct sentiment type
     */
    public static SentimentType fromString(String sentimentType) throws HavenException {
        SentimentType ret = CONSTANTS.get(sentimentType);
        if (ret == null) {
            throw new HavenException("Not a valid string representation for the SentimentType");
        }
        return ret;
    }

}