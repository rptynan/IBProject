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
 * Parsing Util functions.
 *
 * @author Tudor & Momchil
 */
public class UtilParsing {
    private static final boolean CHECK_STOP_WORDS = true; // used for testing.
    private static final String STOP_WORDS_FILE = Configuration.getValue("StopWordsPath");
    private static Set<String> stopWords = null;

    /**
     * Given a long sentence, split it into words, i.e. should get a list of words.
     *
     * Will remove punctuation except for "#" and "@", assumed to be useful for Twitter
     */
    public static String[] splitIntoWords(String sentence) {
        if (sentence == null || sentence.isEmpty()) {
            return new String[0];
        }

        sentence = sentence.replaceAll("(?<=(^|[.])[\\S&&\\D])[.](?=[\\S&&\\D]([.]|$))", "");
        sentence = sentence.replaceAll("[^\\w\\d\\#\\@]", " ");

//        System.out.println(sentence);
        return sentence.split("\\s+");
    }

    /**
     * Removes usernames (starting with '@') and hashtags (starting with '#') from a tweet.
     *
     * @param text
     * @return The tweet without usernames and hashtags
     */
    public static String removeUsersAndHashTags(String text) {
	StringBuilder sb = new StringBuilder();
	boolean removeMode = false;
	for (int i = 0; i < text.length(); i++) {
	    if (text.charAt(i) == '@' || text.charAt(i) == '#') {
		removeMode = true;
		continue;
	    }

	    if (removeMode) {
		removeMode = (Character.isAlphabetic(text.codePointAt(i))
			   || Character.isDigit(text.codePointAt(i)));
	    }

	    if (!removeMode) {
		sb.append(text.charAt(i));
	    }
	}
	return sb.toString().trim().replaceAll(" +", " ");
    }

    /**
     * <p> Parse the name of the trend.
     *
     * <p> This includes removal of special symbols such as '@' and '#' and trying to put spaces
     * in the right positions.
     *
     * @param trendName The original trend name that we have to parse.
     * @return Parsed trend.
     */
    public static String parseTrendName(String trendName) {
	String newTrendName = trendName.replaceAll("[^a-zA-Z0-9-]", " ").trim();
	StringBuilder parsedResult = new StringBuilder();
	for (int i = 0; i < newTrendName.length(); i++) {
	    char currentCharacter = newTrendName.charAt(i);
	    parsedResult.append(currentCharacter);
	    if (i + 1 < newTrendName.length()) {
		if (Character.isLowerCase(currentCharacter)) {
		    if (Character.isUpperCase(newTrendName.charAt(i + 1))
			|| Character.isDigit(newTrendName.charAt(i + 1))) {
			parsedResult.append(" ");
		    }
		} else if (Character.isDigit(currentCharacter)) {
		    if (Character.isUpperCase(newTrendName.charAt(i + 1))
			|| Character.isLowerCase(newTrendName.charAt(i + 1))) {
			parsedResult.append(" ");
		    }
		} else if (Character.isUpperCase(currentCharacter)) {
		    if (Character.isDigit(newTrendName.charAt(i + 1))
			|| (i + 2 < newTrendName.length()
			    && Character.isUpperCase(newTrendName.charAt(i + 1))
			    && Character.isLowerCase(newTrendName.charAt(i + 2)))) {
			parsedResult.append(" ");
		    }
		}
	    }
	}
	return parsedResult.toString();
    }

    /**
     * Remove links from a text.
     *
     * @param text
     * @return The text without any links.
     */
    public static String removeLinks(String text) {
	StringBuilder sb = new StringBuilder();
	int index = 0;
	while (index < text.length()) {
	    String tillEnd = text.substring(index);
	    if (tillEnd.startsWith("http") || tillEnd.startsWith("www")) {
		index = text.indexOf(" ", index);
		if (index == -1) {
		    index = text.length();
		}
	    } else {
		sb.append(text.charAt(index));
		index++;
	    }
	}
	return sb.toString().trim().replaceAll(" +", " ");
    }

    /**
     * Reads the stop words file.
     */
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

    /**
     * @param word
     * @return true if the word is a stop word, false otherwise.
     */
    public static boolean isStopWord(String word) {
	if (!CHECK_STOP_WORDS) {
	    return false;
	}

	if (word == null) {
	    return true;
	}

	if (stopWords == null) {
	    readStopWords();
	}

	return stopWords.contains(word.toLowerCase());
    }
}
