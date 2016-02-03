package uk.ac.cam.quebec.util;

import javafx.util.Pair;

/**
 * Interface for finding concepts in text. These can be single words, a bi-gram or a tri-gram
 *
 * @author tudor
 */
public interface ConceptFinder {

    /**
     * Add some text to be analysed for existing concepts.
     * This will be aggregated with all the previously added texts for the confidence scores.
     * @param text to analyse
     */
    void addText(String text);
    Pair<String, Integer>[] getConcepts();

}
