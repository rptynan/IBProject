package uk.ac.cam.quebec.util.parsing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Momchil
 */
public class UtilParsingTest {

    @Test
    public void parseTest() {
	assertEquals(UtilParsing.parseTrendName("#DigitalMarketingForLife"),
		"Digital Marketing For Life");
	assertEquals(UtilParsing.parseTrendName("Federer"), "Federer");
	assertEquals(UtilParsing.parseTrendName("#BaeIn3Words"), "Bae In 3 Words");
	assertEquals(UtilParsing.parseTrendName("@JPMorgan"), "JP Morgan");
	assertEquals(UtilParsing.parseTrendName("#NFU16"), "NFU 16");
    }

    @Test
    public void removeLinksTest() {
	assertEquals(UtilParsing.removeLinks("http://www.cl.cam.ac.uk/"), "");
	assertEquals(UtilParsing.removeLinks("This is http://www.cl.cam.ac.uk/ the CL web page"),
		"This is the CL web page");
	assertEquals(UtilParsing.removeLinks("  www.cl.cam.ac.uk/ Link in the start "),
		"Link in the start");
	assertEquals(UtilParsing.removeLinks("http://www.cl.cam.ac.uk/ Link in the start"),
		"Link in the start");
	assertEquals(UtilParsing.removeLinks("  Link in the end www.cl.cam.ac.uk/"),
		"Link in the end");
    }

}
