package uk.ac.cam.quebec.twitterproc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TwitterProcessorTest {

    @Test
    public void parseTest() {
	assertEquals(TwitterProcessor.parse("#DigitalMarketingForLife"),
		"Digital Marketing For Life");
	assertEquals(TwitterProcessor.parse("Federer"), "Federer");
	assertEquals(TwitterProcessor.parse("#BaeIn3Words"), "Bae In 3 Words");
	assertEquals(TwitterProcessor.parse("@JPMorgan"), "JP Morgan");
    }

}
