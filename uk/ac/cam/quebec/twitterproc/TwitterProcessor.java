package uk.ac.cam.quebec.twitterproc;

import uk.ac.cam.quebec.trends.Trend;

/**
 * Class responsible for processing trends. It should extract relevant tweets,
 * and send them to the database. After that analyse them and use the data to
 * create a list of concepts that are passed to the WikiProcessor.
 * 
 * @author Momchil
 *
 */
public class TwitterProcessor {

    /**
     * TwitterProcessor constructor. 
     */
    public TwitterProcessor() {
    }

    /**
     * Process a trend and in the end pass a list of concepts to the Wikipedia
     * Processor.
     * 
     * @param trend The trend that should be processed.
     */
    public void process(Trend trend) {
    }

}
