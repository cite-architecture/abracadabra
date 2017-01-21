package edu.holycross.shot.abracadabra

import static org.junit.Assert.*
import org.junit.Test

class TestTtl {


    File inv = new File("testdata/testsubsindex.xml")
    File srcDir = new File("testdata/indices")
    CiteIndex idx = new CiteIndex(inv, srcDir)

    @Test void idSubref() {
        String simpleCite1 = "urn:cite:hmt:msA.12r"
        assert idx.getRefType(simpleCite1) == CiteIndex.RefType.CITE

        String c1imgUrn = "urn:cite:hmt:vaimg.VA012RN_0013"
        assert idx.getRefType(c1imgUrn) == CiteIndex.RefType.CITE

        String c1imgUrnX = "urn:cite:hmt:vaimg.VA012RN_0013.v1@0.056,0.2279,0.112,0.084"
        assert idx.getRefType(c1imgUrnX) == CiteIndex.RefType.CITE_EXTENDED

        String c2imgUrn = "urn:cite2:hmt:vaimg.v1:VA012RN_0013@0.056,0.2279,0.112,0.084"
        assert idx.getRefType(c2imgUrn) == CiteIndex.RefType.CITE2_EXTENDED

        String simpleCite2 = "urn:cite2:hmt:msA:12r"
        assert idx.getRefType(simpleCite2) == CiteIndex.RefType.CITE2

        String ctsUrn = "urn:cts:greekLit:tlg0012.tlg001.msA:1.1@μῆνιν"
        assert idx.getRefType(ctsUrn) == CiteIndex.RefType.CTS_EXTENDED

        String simpleCts = "urn:cts:greekLit:tlg0012.tlg001.msA:1.1"
        assert idx.getRefType(simpleCts) == CiteIndex.RefType.CTS

        String bogusUrn = "not.really.a.urn"
        assert idx.getRefType(bogusUrn) == CiteIndex.RefType.ERROR
    }

		@Test void testCite2Conversion(){

        String simpleCite1 = "urn:cite:hmt:msA.12r"
        assert idx.getRefType(simpleCite1) == CiteIndex.RefType.CITE


        String c1imgUrn = "urn:cite:hmt:vaimg.VA012RN_0013"
        assert idx.getRefType(c1imgUrn) == CiteIndex.RefType.CITE

        String c1imgUrnX = "urn:cite:hmt:vaimg.VA012RN_0013.v1@0.056,0.2279,0.112,0.084"
        assert idx.getRefType(c1imgUrnX) == CiteIndex.RefType.CITE_EXTENDED

        String c2imgUrn = "urn:cite2:hmt:vaimg.v1:VA012RN_0013@0.056,0.2279,0.112,0.084"
        assert idx.getRefType(c2imgUrn) == CiteIndex.RefType.CITE2_EXTENDED

        String simpleCite2 = "urn:cite2:hmt:msA:12r"
        assert idx.getRefType(simpleCite2) == CiteIndex.RefType.CITE2
		}

    @Test void testTtl() {
        File outFile = new File("testdata/testoutput/testSubs.ttl")
        idx.ttl(outFile, true)
				assert true
    }


}
