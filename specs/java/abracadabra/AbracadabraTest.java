package abracadabra;

import edu.holycross.shot.abracadabra.CiteIndex;

import org.concordion.integration.junit3.ConcordionTestCase;

public class AbracadabraTest extends ConcordionTestCase {

    //    public String tordf(String u1, String u2, String v1, String v2, String expected) {
        public boolean tordf(String u1, String u2, String v1, String v2, String expected) {
	String rdf = CiteIndex.formatPair(u1, u2, v1, v2);
	//String mod = rdf.replaceAll("<","&lt;");

	System.err.println ("Expected: " + expected.length());
	System.err.println ("Actual: " + rdf.length());
	
	return (expected.length() == rdf.length());
    }
}
