package edu.holycross.shot.abracadabra

import au.com.bytecode.opencsv.CSVReader

import edu.harvard.chs.cite.CiteUrn
import edu.harvard.chs.cite.CtsUrn


/** A simple class modelling a CITE Index, associating
* pairs of URN values.  There are two components to the model:
* [1] an inventory, cataloging indices and defining RDF
* verbs for each direction of the paired relation, and
* identifying a data source; [2] a data source.
* This implementation uses local files * in either .tsv or 
* .csv format both for the inventory and for the index data source.
*/
class CiteIndex {


    int debug = 5

    /** Character encoding for i/o. */
    String charEnc = "UTF-8"

    /** Map of abbreviations to URIs for all RDF namespaces. */
    def nsMap = [:]

    /** A list of configuration settings, one per index. **/
    def indices = []

    /** Directory containing one or more .tsv or .csv files with
    * index data.
    */
    File sourceDirectory

    /** Namespace for CiteIndex inventory. */
    groovy.xml.Namespace idx = new groovy.xml.Namespace("http://chs.harvard.edu/xmlns/cite")
    
    enum RefType {
        CITE,CITE_EXTENDED,CTS,CTS_EXTENDED,ERROR
    }

    /** Constructor with a File source for the inventory.
    * Perhaps should really throw an Excepion if 
    * we can't parse the inventory.
    * @param inventory The inventory of indices, as a 
    * .tsv or .csv file.
    */
    CiteIndex (File inventory) {
        initFromFile(inventory)
    }



    /** Constructor with a File source for the inventory
    * and local directory for data source files.
    * Perhaps should really throw an Excepion if 
    * we can't parse the inventory.
    * @param inventory The inventory of indices, as a 
    * .tsv or .csv file.
    * @param baseDir Directory with one or more .tsv or .csv
    * files containing index data.
    */
    CiteIndex (File inventory, File baseDir) {
        this.sourceDirectory = baseDir
        initFromFile(inventory)
    }

    /** Populates the configuration list from an
    * an inventory in f.
    * @param f File with inventory.
    */
    void initFromFile(File f) {
        def root = new XmlParser().parse(f)

        root[idx.rdfNamespace].each { ns ->
            nsMap[ns.'@abbr']  = ns.'@fullValue'
        }

        root[idx.index].each { i ->
	  if (debug > 1) { System.err.println "Index " + i }
            def idxStruct = [:]
            idxStruct['verb'] = i.'@verb'
            idxStruct['inverse'] = i.'@inverse'
            i[idx.source].each { src ->
                idxStruct['sourceType'] = src.'@type'
                idxStruct['source'] = src.'@value'
            }

            indices.add(idxStruct)
        }
	if (debug > 1) { System.err.println "Indices initialized to " + indices }
    }



    /** Determines type of URN represented by a String value.
    * @param urnString String value of URN to check.
    * @returns a RefType
    */
    RefType getRefType(String urnString ) {
        RefType reply = RefType.ERROR
	if (debug > 0) { System.err.println "CiteIndex:getRefType: examine ${urnString}"}
        try {
            CtsUrn urn = new CtsUrn(urnString)
            if (urn.hasSubref()) {
                reply =  RefType.CTS_EXTENDED
            } else {
                reply = RefType.CTS
            }
        } catch (Exception ctse) {
	  if (debug > 1) {
	    System.err.println "getRefType: ${urnString} not a cts urn."
	  }
        }

        try {
            CiteUrn urn = new CiteUrn(urnString)
            if (debug > 0) { System.err.println "CITE URN: extendedref = " + urn.getExtendedRef() }
            if (urn.getExtendedRef() != null) {
                reply = RefType.CITE_EXTENDED
            }  else {
                reply = RefType.CITE
            }
        } catch (Exception obje) {
        }
        return reply
    }


    /** Creates RDF string for a pair of URNs.  If either URN
    * includes an extended reference, includes RDF statements
    * relating extended reference to unique object.
    * @param urn1 First URN, subject of main verb.
    * @param urn2 Second URN, object of main verb.
    * @param verb RDF verb for first relation.
    * @param inverse RDF for inverse relation.
    * @returns RDF TTL statements relating urn1 and urn2, or an
    * empty String if urn1 or urn2 cannot be parsed.
    */
    String formatPair(String urn1, String urn2, String verb, String inverse) {
        StringBuffer reply = new StringBuffer()
        
        if (debug > 0) { System.err.println "Formatting ${urn1} based on type " + getRefType(urn1)}
        boolean urn1ok = false
        switch(getRefType(urn1)) {
            case (RefType.CTS_EXTENDED):
                CtsUrn ctsUrn = new CtsUrn(urn1)
            String urn = "${ctsUrn.getUrnWithoutPassage()}:${ctsUrn.getPassageNode()}"
            reply.append("<${urn1}> cite:isExtendedRef <${urn}> .\n")
            reply.append("<${urn}> cite:hasExtendedRef <${urn1}> .\n")
            urn1ok = true
            break


            case (RefType.CITE_EXTENDED):
                CiteUrn citeUrn = new CiteUrn(urn1)
            String urn = "urn:cite:${citeUrn.getNs()}:${citeUrn.getCollection()}.${citeUrn.getObjectId()}"
            reply.append("<${urn1}> cite:isExtendedRef <${urn}> .\n")
            reply.append("<${urn}> cite:hasExtendedRef <${urn1}> .\n")
            urn1ok = true
            break

            case (RefType.CITE):
                case (RefType.CTS):
                urn1ok = true
            break
            default:
                break
        }


        boolean urn2ok = false
        if (debug > 0) {
            System.err.println "urn1 ok? " + urn1ok
            System.err.println "Formatting ${urn2} based on type " + getRefType(urn2)
        }
        switch(getRefType(urn2)) {
            case (RefType.CTS_EXTENDED):
                CtsUrn ctsUrn = new CtsUrn(urn2)
            String urn = "${ctsUrn.getUrnWithoutPassage()}:${ctsUrn.getPassageNode()}"
            reply.append("<${urn2}> cite:isExtendedRef <${urn}> .\n")
            reply.append("<${urn}> cite:hasExtendedRef <${urn2}> .\n")
            urn2ok = true
            break


            case (RefType.CITE_EXTENDED):
                CiteUrn citeUrn = new CiteUrn(urn2)
            String urn = "urn:cite:${citeUrn.getNs()}:${citeUrn.getCollection()}.${citeUrn.getObjectId()}"
            reply.append("<${urn2}> cite:isExtendedRef <${urn}> .\n")
            reply.append("<${urn}> cite:hasExtendedRef <${urn2}> .\n")
            urn2ok = true
            break

            case (RefType.CITE):
                case (RefType.CTS):
                urn2ok = true
            break
            default:
                break
        }
        if (debug > 0) { System.err.println "urn2 ok? " + urn2ok }
        if (urn1ok && urn2ok) {

            reply.append("<${urn1}> ${verb} <${urn2}> .\n")
            reply.append("<${urn2}> ${inverse} <${urn1}> .\n")
            if (debug > 0) { System.err.println "BOTH urns OK so added lines: " + reply.toString()}
        } else {
            if (debug > 0)  { System.err.println "Emptying buffer" }
            return reply = new StringBuffer("")
        }
        if (debug > 0) { System.err.println "Formatted " + reply.toString() }
        return reply.toString()
    }

    /**
    */
    String ttlFromFile(String fileName, String verb, String inverse) {
        if (debug > 0) {System.err.println "TTL OF FILE ${fileName}"}
        File indexFile
        if (this.sourceDirectory) {
            indexFile = new File(this.sourceDirectory, fileName)
        } else {
            indexFile = new File(fileName)
        }

        StringBuffer reply = new StringBuffer()
        if (fileName ==~ /.+csv/) {
            CSVReader reader = new CSVReader(new FileReader(indexFile))
            // make sure we have 2 cols!
            reader.readAll().each { ln ->
                if (ln.size() > 1) {
                    reply.append(formatPair(ln[0], ln[1], verb, inverse ))
                } else {
                    System.err.println "CiteIndex: unable to process row " + ln
                    System.err.println "Could not parse csv columns."
                }
            }
        } else if (fileName ==~ /.+tsv/) {
            System.err.println "Indexing " + fileName
            indexFile.eachLine { ln ->
                def cols = ln.split(/\t/)
                if (cols.size() > 1) {
                  
                    String formatted = formatPair(cols[0], cols[1], verb, inverse)
                    if (debug > 0) { 
                        System.err.println "Add row .. " + ln 
                        System.err.println "Formatted as " + formatted
                    }
                    reply.append(formatted)
                } else {
                    System.err.println "CiteIndex: unable to process row " + ln
                    System.err.println "Could not parse tsv columns."
                }

            }
        } else {
            System.err.println "ttlFromFile : NO MATCH for name " + fileName
        }
        if (debug > 0) { System.err.println "Returning " + reply.toString()}
        return reply.toString()
    }


    /** Serializes all indices as RDF in TTL format.
    * @param outputFile File where output will be written.
    */
    void ttl(File outputFile) 
    throws Exception {
        ttl(outputFile, false)
    }




    /** Serializes all indices as RDF in TTL format.
    * @param outputFile File where output will be written.
    * @param prefix True if TTL prefix statements should be
    * included.
    */
    void ttl(File outputFile, boolean prefix)  
    throws Exception {
        if (prefix) {
            nsMap.keySet().each {
                outputFile.append("@prefix ${it}:        <${nsMap[it]}> .\n", charEnc)
            }
        }

	if (debug > 0) {
	  println "CiteIndex:ttl:  indices ${indices}"
	}
        this.indices.each {idx ->
	if (debug > 0) {
	  println "\tindex ${idx}"
	}
            switch(idx['sourceType']) {
                case "file" :
                    String rdfData = ttlFromFile(idx['source'], idx['verb'], idx['inverse'])
                outputFile.append(rdfData, charEnc)

                break

                case "fusiontable":
                    break

                default:
                    throw new Exception("CiteIndex: unrecognized value for index source type ${idx['sourceType']}")
                break
            }
        }
    }

}
