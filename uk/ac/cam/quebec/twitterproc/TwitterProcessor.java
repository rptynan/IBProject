package uk.ac.cam.quebec.twitterproc;

import uk.ac.cam.quebec.common.VisibleForTesting;
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

    @VisibleForTesting
    String parse(String trendName) {
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
