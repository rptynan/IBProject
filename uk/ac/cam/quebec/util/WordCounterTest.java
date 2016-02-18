package uk.ac.cam.quebec.util;

import java.util.LinkedList;
import java.util.List;

import javafx.util.Pair;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.cam.quebec.util.parsing.UtilParsing;

/**
 * Testing unit for WordCounter class
 *
 * @author tudor
 */

public class WordCounterTest {

    static String text1 = "hello and welcome to ibgroup project";
    static String text2 = "hi and welcome to ibgroup project";
    static String text3 = "hi welcome to ibgroup";
    static String text4 = "ibgroup";

    @Test
    public static void test1() {
        WordCounter wordCounter = new WordCounter(10);

        wordCounter.addSentence(UtilParsing.splitIntoWords(text1));
        wordCounter.addSentence(UtilParsing.splitIntoWords(text2));
        wordCounter.addSentence(UtilParsing.splitIntoWords(text3));
        wordCounter.addSentence(UtilParsing.splitIntoWords(text4));
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

        wordCounter = new WordCounter(2);
        LinkedList<String> words = new LinkedList<>();
        words.add("DONALD");
        words.add("TRUMP");
        wordCounter.addSentence(words);
        for (Pair<String, Integer> pair : wordCounter.getOrderedWordsAndCount()) {
            System.out.println(pair.getKey());
        }
        for (int n = 2; n <= 2; ++n) {
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

        wordCounter = new WordCounter(2);
        words = new LinkedList<>();
        words.add("DONALD");
        words.add(WordCounter.IGNORED_WORD);
        words.add("TRUMP");
        wordCounter.addSentence(words);
        for (Pair<String, Integer> pair : wordCounter.getOrderedWordsAndCount()) {
            System.out.println(pair.getKey());
        }
        for (int n = 2; n <= 2; ++n) {
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
