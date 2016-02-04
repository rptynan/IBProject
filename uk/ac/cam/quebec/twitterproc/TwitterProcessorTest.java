package uk.ac.cam.quebec.twitterproc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.quebec.twitterwrapper.TwitException;
import uk.ac.cam.quebec.twitterwrapper.TwitterLink;

public class TwitterProcessorTest {

    @Test
    public void parseTest() {
	TwitterProcessor tp = new TwitterProcessor();
	assertEquals(tp.parse("#DigitalMarketingForLife"), "Digital Marketing For Life");
	assertEquals(tp.parse("Federer"), "Federer");
	assertEquals(tp.parse("#BaeIn3Words"), "Bae In 3 Words");
	assertEquals(tp.parse("@JPMorgan"), "JP Morgan");
    }
    
    @Test
    public void twitterLinkIntegrationTest() {
	System.out.println("login start");
	try {
	    TwitterLink.login("3qZZH5HFPZtKEPRNRvY3K6gu8",
	    	"7GhzY7PGsSJZ2bBm9jqdfcbaZJRKXK49Bi1jenUW95i6ZpI9Yx",
	    	"4818230530-66u4XPAkVML77f7jlirK825m2DyNOPTkx7s4hkm",
	    	"BzFPA6dtBzn4yX8F4u2bxmbXqRhCtAYs69XFtZOQcjDHC",
	    	"IBProjectQuebec");
	} catch (TwitException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	System.out.println("login successful");
    }

}
