package uk.ac.cam.quebec.wikiwrapper.test;

import java.io.IOException;

import uk.ac.cam.quebec.wikiwrapper.WikiArticle;
import uk.ac.cam.quebec.wikiwrapper.WikiException;
import uk.ac.cam.quebec.wikiwrapper.WikiFetch;

public class Test {

    public static void main(String[] args) throws IOException, WikiException {
        long time = System.nanoTime();
        WikiFetch.search("Cambridge", 10, 3);
        System.out.println(((double)(System.nanoTime() - time))/1000000000.0);
        

    }

}
