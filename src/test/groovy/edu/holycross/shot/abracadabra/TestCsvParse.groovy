package edu.holycross.shot.abracadabra

import static org.junit.Assert.*
import org.junit.Test

class TestCsvParse {


    File inv = new File("testdata/thermometer/hmtindices.xml")
    File srcDir = new File("testdata/thermometer")


    @Test void testInv() {
      CiteIndex idx = new CiteIndex(inv, srcDir)

      File outFile = new File("testdata/testoutput/testthermo.ttl")
      idx.ttl(outFile, true)
    }


}
