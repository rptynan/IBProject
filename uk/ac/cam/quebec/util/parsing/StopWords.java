package uk.ac.cam.quebec.util.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import uk.ac.cam.quebec.core.Configuration;


/**
 * Manage a dataset of words to ignore (so called "stop words"). Similar to Singleton Design Patter
 * as we want to read the file only once.
 *
 * @author Momchil
 */
public class StopWords {
    private static final String STOP_WORDS_FILE = Configuration.getValue("StopWordsPath");

    private static Set<String> stopWords = null;

    private static void readStopWords() {
	stopWords = new HashSet<String>();
	File file = new File(STOP_WORDS_FILE);
	try {
	    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	    String line = null;
	    while ((line = br.readLine()) != null) {
		stopWords.add(line);
	    }
	} catch (FileNotFoundException e) {
	    System.err.println("Can't open StopWrods file");
	    e.printStackTrace();
	} catch (IOException e) {
	    System.err.println("Error reading StopWords file");
	    e.printStackTrace();
	}
    }

    public static boolean isStopWord(String word) {
	if (stopWords == null) {
	    readStopWords();
	}

	return stopWords.contains(word.toLowerCase());
    }
}
