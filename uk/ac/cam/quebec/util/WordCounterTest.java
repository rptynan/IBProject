package uk.ac.cam.quebec.util;

import uk.ac.cam.quebec.util.parsing.SplitIntoWords;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import javafx.util.Pair;

/**
 * Testing unit for WordCounter class
 *
 * @author tudor
 */

class WordCounterTest {

    static String text1 = "hello and welcome to ibgroup project";
    static String text2 = "hi and welcome to ibgroup project";
    static String text3 = "hi welcome to ibgroup";
    static String text4 = "ibgroup";

    @Test
    public static void test1() {
        WordCounter wordCounter = new WordCounter(10);

        wordCounter.addSentence(SplitIntoWords.getWords(text1));
        wordCounter.addSentence(SplitIntoWords.getWords(text2));
        wordCounter.addSentence(SplitIntoWords.getWords(text3));
        wordCounter.addSentence(SplitIntoWords.getWords(text4));
        Assert.assertArrayEquals(wordCounter.getOrderedWordsAndCount(), new Pair[] {
                new Pair("ibgroup", 4),
                new Pair("to", 3),
                new Pair("welcome", 3),
                new Pair("hi", 2),
                new Pair("and", 2),
                new Pair("project", 2),
                new Pair("hello", 1)
        });

        for (int n = 2; n <= 10; ++n) {
            System.out.println("\n\n >>> NGRAM LEN <<< " + n + " \n\n");
            for (Pair<List<String>, Integer> pair : wordCounter.getOrderedNGramsAndCount(n)) {
                Assert.assertTrue(pair.getKey().size() == n);
                System.out.print("<");
                for (String s : pair.getKey()) {
                    System.out.print(s + ", ");
                }
                System.out.println(" > " + pair.getValue());
            }
        }
    }

    @Test
    public static void main(String[] args) {
        test1();
    }

}
