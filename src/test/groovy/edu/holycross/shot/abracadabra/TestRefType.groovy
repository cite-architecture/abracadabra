package edu.holycross.shot.abracadabra

import edu.harvard.chs.cite.CtsUrn

import static org.junit.Assert.*
import org.junit.Test

class TestRefType {


    File inv = new File("testdata/testindex.xml")
    File srcDir = new File("testdata/indices")

    String ctsNode = "urn:cts:greekLit:tlg0012.tlg001:1.1"
    String ctsSubref = "urn:cts:greekLit:tlg0012.tlg001.msA:1.1@θεά"


    @Test void testCtsUrns() {
        CiteIndex idx = new CiteIndex(inv)
        assert idx
	assert idx.getRefType(ctsNode) == CiteIndex.RefType.CTS
	assert idx.getRefType(ctsSubref) == CiteIndex.RefType.CTS_EXTENDED
    }



}
