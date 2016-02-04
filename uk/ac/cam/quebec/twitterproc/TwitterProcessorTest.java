package uk.ac.cam.quebec.twitterproc;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

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
    
   
    public static void main(String[] args) {
	System.out.println("login start");
	try {
	    TwitterLink.login(args[0],
	    	args[1],
	    	args[2],
	    	args[3],
	    	"IBProjectQuebec");
	    TwitterLink tl = new TwitterLink();
	    TwitterProcessor tp = new TwitterProcessor();
	    List<String> trends = tl.getTrends("World");
	    List<String> parsedTrends = new LinkedList<String>();
	    System.out.println(trends);
	    for(String t:trends){
	        
	         parsedTrends.add(tp.parse(t));
	    }
	    System.out.println(parsedTrends);
	} catch (TwitException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	System.out.println("login successful");
    }

}
