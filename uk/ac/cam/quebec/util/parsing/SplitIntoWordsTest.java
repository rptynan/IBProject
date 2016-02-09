package uk.ac.cam.quebec.util.parsing;

import org.junit.Assert;
import org.junit.Test;
/**
 * Testing for SplitIntoWords
 *
 * @author tudor
 */
class SplitIntoWordsTest {

    @Test
    public static void main(String[] args) {
        String[] testData = new String[] {
                "Hi! We're Team Quebec. Who are you?",
                "Hello.World",
                "This.Is.A.Test",
                "The.S.W.A.T.Team",
                "S.w.a.T.",
                "S.w.a.T.1",
                "2001.A.Space.Odyssey",
                "@DonaldTrump is the best #loveTrump"
        };

        String[][] okay = new String[][] {
                new String[] {"Hi", "We", "re", "Team", "Quebec", "Who", "are", "you"},
                new String[] {"Hello", "World"},
                new String[] {"This", "Is", "A", "Test"},
                new String[] {"The", "SWAT", "Team"},
                new String[] {"SwaT"},
                new String[] {"SwaT", "1"},
                new String[] {"2001", "A", "Space", "Odyssey"},
                new String[] {"@DonaldTrump", "is", "the", "best", "#loveTrump"}
        };

        for (int i = 0; i < testData.length; i++) {
            Assert.assertArrayEquals(okay[i], UtilParsing.splitIntoWords(testData[i]));
            System.out.println("Test " + i + " OKAY!");
        }
    }
}
