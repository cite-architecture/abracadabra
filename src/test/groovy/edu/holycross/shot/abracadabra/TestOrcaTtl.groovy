package edu.holycross.shot.abracadabra

import static org.junit.Assert.*
import org.junit.Test

class TestOrcaTtl {


    File inv = new File("testdata/indexinventory.xml")
    File srcDir = new File("testdata/textfiles")
    CiteIndex idx = new CiteIndex(inv, srcDir)

    @Test void idSubref() {
        String simpleCite1 = "urn:cite2:fufolio:PlutPericles_SyntaxTokens_ORCA.v1:0"
        assert idx.getRefType(simpleCite1) == CiteIndex.RefType.CITE2

        String simpleCite2 = "urn:cite2:fufolio:tokenTypes.v1:word"
        assert idx.getRefType(simpleCite2) == CiteIndex.RefType.CITE2


        String ctsUrn = "urn:cts:greekLit:tlg0007.tlg012.ziegler:1.1@Ξένους[1]"
        assert idx.getRefType(ctsUrn) == CiteIndex.RefType.CTS_EXTENDED

        String simpleCts = "urn:cts:greekLit:tlg0007.tlg012.ziegler.syntaxToken:1.1.2"
        assert idx.getRefType(simpleCts) == CiteIndex.RefType.CTS

        String bogusUrn = "not.really.a.urn"
        assert idx.getRefType(bogusUrn) == CiteIndex.RefType.ERROR
    }


    @Test void testTtl() {
        File outFile = new File("testdata/testoutput/testOrca.ttl")
        idx.ttl(outFile, true)
				assert true
    }


}
