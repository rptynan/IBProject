package uk.ac.cam.quebec.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Given sentences as an array of words (Strings) will keep a word count and an n-gram count,
 * based on (n <= 10). By default n is 2, and that will be used if incorrect n is given
 *
 *
 * @author tudor
 */
public class WordCounter {

    private int n;

    HashMap<String, Integer> wordCount;
    HashMap<List<String>, Integer>[] nGramCount;


    private void init(int n)
    {
        this.n = n;
        wordCount = new HashMap<>();

        nGramCount = new HashMap[n + 1];
        for (int i = 2; i <= n; i++) {
            nGramCount[i] = new HashMap<>();
        }
    }

    public WordCounter() {
        init(2);
    }

    public WordCounter(int n) {
        if (n >= 1 && n <= 10) {
            init(n);
        } else {
            init(2);
        }
    }

    /**
     * Add a single word
     * @param word to be added
     */
    public void addWord(String word) {
        if (word == null) return;
        addSentence(new String[] {word});
    }

    /**
     * Add a sentence, make sure we do not change the given array
     * @param words to be added
     */
    public void addSentence(String[] words) {
        if (words == null) return;
        addSentence(Arrays.asList(words));
    }

    /**
     * Add a sentence, make sure we do not change the given array
     * @param words to be added
     */
    public void addSentence(List<String> words) {
        if (words == null) return;
        // Increment single word count
        for (String copy : words) {
            String word = copy.toLowerCase();

            Integer prev = wordCount.get(word);
            if (prev != null) {
                wordCount.put(word, prev + 1);
            } else {
                wordCount.put(word, 1);
            }
        }

        if (n < 2) return;
        for (int i = 0; i < words.size(); ++i) {
            LinkedList<String> subseq = new LinkedList<>();

            for (ListIterator<String> iterator = words.listIterator(i);
                 iterator.hasNext() && iterator.nextIndex() - i < n; ) {
                subseq.add(iterator.next().toLowerCase());

                int len = subseq.size();
                if (len > 1) {
                    Integer prev = nGramCount[len].get(subseq);
                    if (prev != null) {
                        nGramCount[len].put(subseq, prev + 1);
                    } else {
                        nGramCount[len].put(subseq, 1);
                    }
                }
            }
        }
    }


}
