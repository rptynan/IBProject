package uk.ac.cam.quebec.trends;

/**
 * Class which fetches and schedules trends to be processsed.
 * This class provides a thread-safe way for other classes to queue a trend to
 * be run. It also is responsible for updating the queue with fresh trends from
 * the TwitterWrapper.
 *
 * @author Richard
 *
 */
public interface TrendsQueue {

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
