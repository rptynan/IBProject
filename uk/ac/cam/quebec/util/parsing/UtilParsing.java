package uk.ac.cam.quebec.util.parsing;

/**
 * Parsing Util functions.
 *
 * @author Tudor & Momchil
 */
public class UtilParsing {
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
}
