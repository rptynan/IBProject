package uk.ac.cam.quebec.trends;

import java.lang.String;

public abstract class Trend {

    private String name;
    private int priority;

    /**
     * Create a Trend object.
     *
     * <p>This class should be used for passing trends between classes. Care
     * should be taken to preserve the priority.
     *
     * @param name      a string which represents the trend
     * @param priority  the priority of the trend, 0 being the highest. This
     *                  should be 0 for user requests and requests made by
     *                  Twitter/Wikipedia Processing should half the priority
     *                  of the trend which causes this one to be created.
     */
    public Trend(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

}
