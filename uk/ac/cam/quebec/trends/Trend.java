package uk.ac.cam.quebec.trends;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javafx.util.Pair;
import uk.ac.cam.quebec.dbwrapper.Database;

/**
 * Class to represent a single "trend".
 * This is not necessarily a trend from twitter, but a single concept or phrase
 * that we want to process.
 *
 * @author Richard
 *
 */
public class Trend implements Serializable, Comparable<Trend> {

    private static final long serialVersionUID = 1L;

    private String name;
    private String location;
    private int id;
    private int priority;
    private int processCount;

    private String parsedName;
    private double popularity;
    private Date timestamp;
    private double recency;
    private List<Pair<String, Integer>> concepts;
    private List<Pair<String, Integer>> relatedHashTags;

    public String getName() { return name; }
    public String getLocation() { return location; }
    public int getId() { return id; }
    public int getPriority() { return priority; }
    public int getProcessCount() { return processCount; }

    public String getParsedName() { return parsedName; }
    public double getPopularity() { return popularity; }
    public Date getTimestamp() { return timestamp; }
    public double getRecency() { return recency; }
    public List<Pair<String, Integer>> getConcepts() { return concepts; }
    public List<Pair<String, Integer>> getRelatedHashTags() { return relatedHashTags; }

    /**
     * Create a Trend object.
     *
     * <p>This class should be used for passing trends between classes. Care
     * should be taken to preserve the priority.
     *
     * @param name      a string which represents the trend
     * @param location  the string identifier for the location of the trend
     * @param priority  the priority of the trend, 0 being the highest. This
     *                  should be 0 for user requests and requests made by
     *                  Twitter/Wikipedia Processing should half the priority
     *                  of the trend which causes this one to be created.
     */
    public Trend(String name, String location, int priority) {
        this.name = name;
        this.location = location;
        id = 0;
        this.priority = priority;
        processCount = 0;
        parsedName = null;
        popularity = 0.0;
        timestamp = new Date(System.currentTimeMillis());
        recency = (double) timestamp.getTime();
        concepts = new LinkedList<>();
        relatedHashTags = new LinkedList<>();
    }

    public void setParsedName(String parsedName) {
        this.parsedName = parsedName;
    }

    public void setPopularity(int popularity) {
        this.popularity = (double) popularity;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        this.recency = (double) timestamp.getTime();
    }

    public void addConcept(Pair<String, Integer> concept) {
        concepts.add(concept);
    }

    public void addRelatedHashTag(Pair<String, Integer> hashTag) {
        relatedHashTags.add(hashTag);
    }

    /**
     * Set the ID of this trend.
     *
     * <p>This sets the ID of the trend which will be used to index the
     * database trends table. getId() can be relied on by other classes once
     * the Trend has been put in the database, but no other classes are allowed
     * to call this function, hence the parameter is an obscure private class
     * provided by the database to only allow this (see
     * http://stackoverflow.com/a/18634125/1205923)
     *
     * @param id        the id to set it to
     * @param access    must be provided to call the method, only the database
     *                  can construct it
     */
    public void setId(int id, Database.AccessId access) {
        if (access == null) {
            return;
        }
        this.id = id;
    }

    /**
     * Increment the amount of times this trend has been processed.
     *
     * <p>Used to keep track of how many times we've process this trend, the
     * Wikipedia Processing module should call this *once* after it is finished
     * and about to store this class (or its subclass).
     */
    public void incrementProcessCount() {
        processCount++;
    }
    /**
     * An implementation of the Comparator interface
     * @param o The trend to be compared to
     * @return The difference in their priorities
     */
     @Override
    public int compareTo(Trend o) {
        return this.getPriority()-o.getPriority();
    }

}
