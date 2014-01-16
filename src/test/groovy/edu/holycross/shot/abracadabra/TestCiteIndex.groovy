package edu.holycross.shot.abracadabra

import static org.junit.Assert.*
import org.junit.Test

class TestCiteIndex {


    File inv = new File("testdata/testindex.xml")
    File srcDir = new File("testdata/indices")


    @Test void testInv() {
        CiteIndex idx = new CiteIndex(inv)
        assert idx

        String expectedUri = "http://www.homermultitext.org/hmt/rdf/"

        assert idx.nsMap["hmt"] == expectedUri
        assert idx.indices.size() == 1
    }

    @Test void testTtl() {
        
        CiteIndex idx = new CiteIndex(inv, srcDir)
        
        File outFile = new File("testdata/testoutput/test.ttl")
        idx.ttl(outFile, true)
    }


}
