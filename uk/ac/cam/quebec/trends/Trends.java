package uk.ac.cam.quebec.trends;

import java.lang.String;

public interface Trends {

    /**
     * Puts a custom trend on the processing queue.
     *
     * <p>This allows the User API to request "custom trends" to be processed.
     * It also lets the Twitter and Wikipedia Processing to request new trends
     * that they have found to be related to the current trend they are
     * processing.
     *
     * @param trend the trend to be processed, with proper priority and name
     */
    public void putTrend(Trend trend);

}
