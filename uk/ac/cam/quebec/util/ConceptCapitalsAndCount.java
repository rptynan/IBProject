package uk.ac.cam.quebec.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javafx.util.Pair;
import uk.ac.cam.quebec.util.parsing.UtilParsing;

/**
 * Class that implements the ConceptFinder interface by identifying words that start with capital
 * letters or non-letters (e.g. digits, @, #) and then do a word count on those.
 *
 * Should be thread safe.
 *
 * @author tudor
 */
public class ConceptCapitalsAndCount implements ConceptFinder {

    private WordCounter wordCounter;
    private boolean updatedCounter;
    private Pair<String, Integer>[] concepts;

    public ConceptCapitalsAndCount() {
        wordCounter = new WordCounter(3);
        updatedCounter = true;
        concepts = null;
    }

    @Override
    public synchronized void addText(String text) {
        String[] words = UtilParsing.splitIntoWords(UtilParsing.removeLinks(text));
        LinkedList<String> capitalWords = new LinkedList<>();

        boolean added_prev = true;
        for (String word : words) {
            if (word != null && !word.isEmpty() && (Character.isUpperCase(word.charAt(0))
        	|| !Character.isLetter(word.charAt(0))) && !word.startsWith("@") &&
                    !word.startsWith("#") && word.length() > 3) {
                if (!added_prev) {
                    capitalWords.add(WordCounter.IGNORED_WORD);
                }
                capitalWords.add(word);
                added_prev = true;
            } else {
                added_prev = false;
            }
        }

        updatedCounter = true;
        wordCounter.addSentence(capitalWords);
    }

    @Override
    public synchronized Pair<String, Integer>[] getConcepts() {
        if (!updatedCounter) {
            return concepts;
        }

        Pair<String, Integer>[] singleWords = wordCounter.getOrderedWordsAndCount();
        Pair<List<String>, Integer>[] bigrams = wordCounter.getOrderedNGramsAndCount(2);
        Pair<List<String>, Integer>[] trigrams = wordCounter.getOrderedNGramsAndCount(3);
        concepts = new Pair[singleWords.length + bigrams.length + trigrams.length];

        for (int i = 0; i < singleWords.length; i++)
            concepts[i] = singleWords[i];
        for (int i = 0; i < bigrams.length; i++)
            concepts[i + singleWords.length] =
                    new Pair<>(String.join(" ", bigrams[i].getKey()), bigrams[i].getValue());
        for (int i = 0; i < trigrams.length; i++)
            concepts[i + singleWords.length + bigrams.length] =
                    new Pair<>(String.join(" ", trigrams[i].getKey()), trigrams[i].getValue());

        Arrays.sort(concepts, new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                // sort them such that o2 comes before o1 if o2 > o1, otherwise sort
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        updatedCounter = false;
        return concepts;
    }

}
