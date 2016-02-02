package uk.ac.cam.quebec.util.parsing;

/**
 * Given a long sentence, split it into words, i.e. should get a list of words.
 *
 * Will remove punctuation except for "#" and "@", assumed to be useful for Twitter
 *
 * @author tudor
 */
public class SplitIntoWords {

    public static String[] getWords(String sentence) {
        if (sentence == null || sentence.isEmpty()) {
            return new String[0];
        }

        sentence = sentence.replaceAll("(?<=(^|[.])[\\S&&\\D])[.](?=[\\S&&\\D]([.]|$))", "");
        sentence = sentence.replaceAll("[^\\w\\d\\#\\@]", " ");

        return sentence.split("\\s+");
    }

}
