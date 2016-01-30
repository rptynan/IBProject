package uk.ac.cam.quebec.wikiwrapper.test;

import java.io.IOException;

import uk.ac.cam.quebec.wikiwrapper.WikiArticle;
import uk.ac.cam.quebec.wikiwrapper.WikiException;
import uk.ac.cam.quebec.wikiwrapper.WikiFetch;

public class Test {

    public static void main(String[] args) throws IOException, WikiException {
        long time = System.nanoTime();
        String s = WikiFetch.search("Donald Trump", 1, 1).get(0).getEdits(1).get(0).getDiff();
        
        System.out.println(((double)(System.nanoTime() - time))/1000000000.0);
        System.out.println(s);

    }

}
