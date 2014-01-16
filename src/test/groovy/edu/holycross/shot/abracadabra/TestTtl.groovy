package edu.holycross.shot.abracadabra

import static org.junit.Assert.*
import org.junit.Test

class TestTtl {


    File inv = new File("testdata/testsubsindex.xml")
    File srcDir = new File("testdata/indices")
    CiteIndex idx = new CiteIndex(inv, srcDir)

    @Test void idSubref() {
        String imgUrn = "urn:cite:hmt:vaimg.VA012RN-0013@0.056,0.2279,0.112,0.084"
        assert idx.getRefType(imgUrn) == CiteIndex.RefType.CITE_EXTENDED

        String simpleCite = "urn:cite:hmt:msA.12r"
        assert idx.getRefType(simpleCite) == CiteIndex.RefType.CITE

        String ctsUrn = "urn:cts:greekLit:tlg0012.tlg001.msA:1.1@μῆνιν"
        assert idx.getRefType(ctsUrn) == CiteIndex.RefType.CTS_EXTENDED

        String simpleCts = "urn:cts:greekLit:tlg0012.tlg001.msA:1.1"
        assert idx.getRefType(simpleCts) == CiteIndex.RefType.CTS
        

        String bogusUrn = "not.really.a.urn"
        assert idx.getRefType(bogusUrn) == CiteIndex.RefType.ERROR
    }

    @Test void testTtl() {
        File outFile = new File("testdata/testoutput/testSubs.ttl")
        idx.ttl(outFile, true)
    }


}
