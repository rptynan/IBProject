package uk.ac.cam.quebec.wikiwrapper.test;

import java.util.List;

import uk.ac.cam.quebec.wikiwrapper.WikiArticle;
import uk.ac.cam.quebec.wikiwrapper.WikiException;
import uk.ac.cam.quebec.wikiwrapper.WikiFetch;

/**
 * Test cases for the WikiWrapper.
 * 
 * @author Stuart
 * 
 */
public class Test {

    /**
     * @param args
     *            None
     * @throws WikiException
     *             If error.
     */
    public static void main(String[] args) throws WikiException {
        long time = System.nanoTime();
        List<WikiArticle> l = WikiFetch.search("Trengune", 3, 3);

        double result = ((double) (System.nanoTime() - time)) / 1000000000.0;
        System.out.println("Expect a list of artiles relating to Trengune:");
        System.out.println(l);
        System.out.println("\n\n\nExpect the extract for Trengune:");
        System.out.println(l.get(0).getExtract());
        System.out.println("\n\n\nExpect the id for Trengune:");
        System.out.println(l.get(0).getId());
        System.out
                .println("\n\n\nExpect a list of edit comments for Trengune:");
        System.out.println(l.get(0).getEdits(3));
        System.out
                .println("\n\n\nExpect the timestamp of the last edit for Trengune:");
        System.out.println(l.get(0).getEdits(3).get(0).getTimeStamp());
        System.out
                .println("\n\n\nExpect the content of the last edit for Trengune:");
        System.out.println(l.get(0).getEdits(3).get(0).getContent());
        System.out
                .println("\n\n\nExpect the diff of the last edit for Trengune:");
        System.out.println(l.get(0).getEdits(3).get(0).getDiff());

        System.out
                .println("\n\n\nExpect a time in seconds for the above to be gathered:");
        System.out.println(result);
        System.out
        .println("\n\n\nExpect the number of views of the Trengune article.");
        System.out.println(l.get(0).getViews());

    }

}
