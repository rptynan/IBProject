package uk.ac.cam.quebec.util;

import javafx.util.Pair;

/**
 * Test unit for ConceptCapitalsAndCount.
 *
 * @author tudor
 */
public class ConceptCapitalsAndCountTest {

    public static void main(String[] args) {
        String[] texts = new String[] {
                "hello! I am Donald Trump and you are?",
                "we love #trump how about 1984",
                "how is @trump doing? we love Trump",
                "what about Clinton, do you think Bill will win"
        };

        ConceptCapitalsAndCount count = new ConceptCapitalsAndCount();
        for (String text : texts) {
            count.addText(text);
        }

        for (Pair<String, Integer> pair : count.getConcepts()) {
            System.out.println(pair.getKey() + " " + pair.getValue());
        }
    }

}
