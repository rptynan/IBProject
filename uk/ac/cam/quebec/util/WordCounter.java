package uk.ac.cam.quebec.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javafx.util.Pair;

/**
 * Given sentences as an array of words (Strings) will keep a word count and an n-gram count,
 * based on (n <= 10). By default n is 2, and that will be used if incorrect n is given
 *
 *
 * @author tudor
 */
public class WordCounter {

    private int n;

    private HashMap<String, Integer> wordCount;
    private HashMap<List<String>, Integer>[] nGramCount;

    private boolean updatedWordCount;
    private boolean[] updatedNGramCount;

    private Pair<String, Integer>[] wordsAndCount;
    private Pair<List<String>, Integer>[][] nGramsAndCount;


    private void init(int n)
    {
        this.n = n;
        wordCount = new HashMap<>();
        updatedWordCount = true;

        nGramCount = new HashMap[n + 1];
        updatedNGramCount = new boolean[n + 1];
        nGramsAndCount = new Pair[n + 1][];
        for (int i = 2; i <= n; i++) {
            nGramCount[i] = new HashMap<>();
            updatedNGramCount[i] = true;
        }
    }

    /**
     * Default word counter which counts words and bigrams
     */
    public WordCounter() {
        init(2);
    }

    /**
     * word counter which also counts n-grams of length up to given n
     * @param n - length of max n-gram, between 1 and 10, otherwise default value of 2 used
     */
    public WordCounter(int n) {
        if (n >= 1 && n <= 10) {
            init(n);
        } else {
            init(2);
        }
    }

    /**
     *
     * @return the maximum length of n-grams counted
     */
    public int getNValue() {
        return n;
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
        updatedWordCount = true;
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
                    updatedNGramCount[len] = true;
                    Integer prev = nGramCount[len].get(subseq);
                    if (prev != null) {
                        nGramCount[len].put(new LinkedList<>(subseq), prev + 1);
                    } else {
                        nGramCount[len].put(new LinkedList<>(subseq), 1);
                    }
                }
            }
        }
    }

    private void populateOrderedPairsDecrByValue(HashMap<Object, Integer> map,
                                                 Pair<Object, Integer>[] ret) {
        if (map == null || ret == null || map.size() != ret.length) {
            return;
        }

        int index = 0;
        for (Iterator<Map.Entry<Object, Integer>> it = map.entrySet().iterator();
                it.hasNext(); ++index) {
            Map.Entry<Object, Integer> entry = it.next();
            ret[index] = new Pair<>(entry.getKey(), entry.getValue());
        }

        Arrays.sort(ret, new Comparator<Pair<Object, Integer>>() {
            @Override
            public int compare(Pair<Object, Integer> o1, Pair<Object, Integer> o2) {
                // sort them such that o2 comes before o1 if o2 > o1, otherwise sort
                return o2.getValue().compareTo(o1.getValue());
            }
        });
    }

    /**
     * Get all the words encountered with the most common at the top
     * @return array of pairs<string, integer> ordered in decreasing order by value
     */
    public Pair<String, Integer>[] getOrderedWordsAndCount() {
        if (!updatedWordCount) {
            return wordsAndCount;
        }

        wordsAndCount = new Pair[wordCount.size()];

        populateOrderedPairsDecrByValue((HashMap<Object, Integer>) ((HashMap<?, ?>) wordCount),
                (Pair<Object, Integer>[]) ((Pair<?, ?>[]) wordsAndCount));

        return wordsAndCount;
    }

    /**
     * Given an n, get all the n-grams encountered with the most common at the top
     * @return array of pairs<list<string>, integer> ordered in decreasing order by value
     *         where each list has size n, if n is within the range 2 and getNValue, otherwise null
     */
    public Pair<List<String>, Integer>[] getOrderedNGramsAndCount(int n) {
        if (n < 2 || n > this.n) {
            return null;
        }
        if (!updatedNGramCount[n]) {
            return nGramsAndCount[n];
        }

        nGramsAndCount[n] = new Pair[nGramCount[n].size()];

        populateOrderedPairsDecrByValue((HashMap<Object, Integer>) ((HashMap<?, ?>) nGramCount[n]),
                (Pair<Object, Integer>[]) ((Pair<?, ?>[]) nGramsAndCount[n]));

        return nGramsAndCount[n];
    }

}
