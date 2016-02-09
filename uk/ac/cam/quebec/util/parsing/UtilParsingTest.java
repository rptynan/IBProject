package uk.ac.cam.quebec.util.parsing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UtilParsingTest {

    @Test
    public void parseTest() {
	assertEquals(UtilParsing.parseTrendName("#DigitalMarketingForLife"),
		"Digital Marketing For Life");
	assertEquals(UtilParsing.parseTrendName("Federer"), "Federer");
	assertEquals(UtilParsing.parseTrendName("#BaeIn3Words"), "Bae In 3 Words");
	assertEquals(UtilParsing.parseTrendName("@JPMorgan"), "JP Morgan");
    }

}
