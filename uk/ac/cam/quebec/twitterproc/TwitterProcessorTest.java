package uk.ac.cam.quebec.twitterproc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TwitterProcessorTest {

    @Test
    public void parseTest() {
	TwitterProcessor tp = new TwitterProcessor();
	assertEquals(tp.parse("#DigitalMarketingForLife"), "Digital Marketing For Life");
	assertEquals(tp.parse("Federer"), "Federer");
	assertEquals(tp.parse("#BaeIn3Words"), "Bae In 3 Words");
	assertEquals(tp.parse("@JPMorgan"), "JP Morgan");
    }

}
