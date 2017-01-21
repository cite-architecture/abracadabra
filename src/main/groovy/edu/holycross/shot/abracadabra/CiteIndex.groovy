package edu.holycross.shot.abracadabra

import au.com.bytecode.opencsv.CSVReader

import edu.harvard.chs.cite.CiteUrn
import edu.harvard.chs.cite.Cite2Urn
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


  static Integer WARN = 1
  static Integer DEBUG = 2
  static Integer SCREAM = 3
  Integer debug = 1

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

    /** Enumeration distinguishing CITE from CTS URNs,
     * and distinguishing simple URNs from those with
     * @-extended notation.
     */
    enum RefType {
        CITE,CITE2,CITE_EXTENDED,CITE2_EXTENDED,CTS,CTS_EXTENDED,ERROR
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
	  //if (debug > WARN) { System.err.println "CiteIndex:initFromFile, index " + i }
            def idxStruct = [:]
            idxStruct['verb'] = i.'@verb'
            idxStruct['inverse'] = i.'@inverse'
            i[idx.source].each { src ->
                idxStruct['sourceType'] = src.'@type'
                idxStruct['source'] = src.'@value'
            }

            indices.add(idxStruct)
        }
	//if (debug > WARN) { System.err.println "CiteIndex: indices initialized to " + indices }
    }



    /** Determines type of URN represented by a String value.
    * @param urnString String value of URN to check.
    * @returns a RefType
    */
    static RefType getRefType(String urnString ) {
        RefType reply = RefType.ERROR
				if (urnString.contains(":cts:")){
	        try {
	            CtsUrn urn = new CtsUrn(urnString)
	            if (urn.hasSubref()) {
	                reply =  RefType.CTS_EXTENDED
	            } else {
	                reply = RefType.CTS
	            }
	        } catch (Exception ctse) {
						throw new Exception("CiteIndex.getRefType error trying to make Cts Urn: ${ctse}")
	        }
				}

				if (urnString.contains(":cite:")){
	        try {
	            CiteUrn urn = new CiteUrn(urnString)
	            if (urn.getExtendedRef() != null) {
	                reply = RefType.CITE_EXTENDED
	            }  else {
	                reply = RefType.CITE
	            }
	        } catch (Exception c1obje) {
						throw new Exception("CiteIndex.getRefType error trying to make Cite Urn: ${c1obje}")
	        }
				}

				if (urnString.contains(":cite2:")){
	        try {
	            Cite2Urn urn = new Cite2Urn(urnString)
	            if (urn.getExtendedRef() != null) {
	                reply = RefType.CITE2_EXTENDED
	            }  else {
	                reply = RefType.CITE2
	            }
	        } catch (Exception c2obje) {
						throw new Exception("CiteIndex.getRefType error trying to make Cite2 urn: ${c2obje}")
	        }
				}
				//System.err.println("${urnString} = ${reply}")
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
  static String formatPair(String urn1, String urn2, String verb, String inverse) {
    return formatPair(urn1, urn2, verb, inverse, 0)
  }

  static String formatPair(String urn1, String urn2, String verb, String inverse, Integer debug) {

    StringBuffer reply = new StringBuffer()
    String urn1encoded = ""
    String urn2encoded = ""

    if (debug > CiteIndex.WARN) {
      //System.err.println "CiteIndex:formatPair: formatting ${urn1} based on type " + getRefType(urn1)
    }


    // format urn1 relations:
    boolean urn1ok = false
    switch(CiteIndex.getRefType(urn1)) {

	      // NS: this is where we need to encode urn1 for RDF output
	    case (RefType.CTS_EXTENDED):

			    CtsUrn ctsUrn = new CtsUrn(urn1)
			    CtsUrn parentUrn

			    String workLevel = ctsUrn.labelForWorkLevel()
			    if (workLevel == "version") {
			      parentUrn = new CtsUrn(ctsUrn.reduceToWork())

			    } else if (workLevel == "exemplar") {
			      parentUrn = new CtsUrn(ctsUrn.reduceToVersion())
			    }


			    reply.append("<${ctsUrn.encodeSubref()}> cite:isExtendedRef <${parentUrn.toString()}> .\n")
			    reply.append("<${parentUrn.toString()}> cite:hasExtendedRef <${ctsUrn.encodeSubref()}> .\n")
					urn1encoded = ctsUrn.encodeSubref()
			    urn1ok = true

			    break

	    case (RefType.CITE_EXTENDED):

					// NS: this is where we need to encode urn1 for RDF output
					CiteUrn citeUrn = new CiteUrn(urn1)
					Cite2Urn cite2Urn = new Cite2Urn(citeUrn)
					reply.append("<${cite2Urn.encodeSubref()}> cite:isExtendedRef <${cite2Urn.reduceToObject()}> .\n")
					reply.append("<${cite2Urn.reduceToObject()}> cite:hasExtendedRef <${cite2Urn.encodeSubref()}> .\n")
					urn1ok = true
					urn1encoded = cite2Urn.encodeSubref()
					break


					case (RefType.CITE):
						CiteUrn tempCite = new CiteUrn(urn1)
						Cite2Urn tempCite2 = new Cite2Urn(tempCite)
						urn1encoded = tempCite2.encodeSubref()
						urn1ok = true
						break
					case (RefType.CTS):
						urn1encoded = new CtsUrn(urn1).encodeSubref()
						urn1ok = true
						break

			default:

					break
    }


    if (debug > CiteIndex.WARN) {
      System.err.println "CiteIndex:formatPair: formatting ${urn2} based on type " + getRefType(urn2)
    }

    // format urn2 relations:
    boolean urn2ok = false


    switch(CiteIndex.getRefType(urn2)) {

		    case (RefType.CTS_EXTENDED):
			    CtsUrn ctsUrn = new CtsUrn(urn2)
			    CtsUrn parentUrn

			    String workLevel = ctsUrn.labelForWorkLevel()
			    if (workLevel == "version") {
			      parentUrn = new CtsUrn(ctsUrn.reduceToWork())

			    } else if (workLevel == "exemplar") {
			      parentUrn = new CtsUrn(ctsUrn.reduceToVersion())
			    }

			    reply.append("<${ctsUrn.encodeSubref()}> cite:isExtendedRef <${parentUrn.toString()}> .\n")
			    reply.append("<${parentUrn.toString()}> cite:hasExtendedRef <${ctsUrn.encodeSubref()}> .\n")

					urn2encoded = ctsUrn.encodeSubref()
			    urn2ok = true
		    break

	    case (RefType.CITE_EXTENDED):
		    CiteUrn citeUrn = new CiteUrn(urn2)
				Cite2Urn cite2Urn = new Cite2Urn(citeUrn)


		    //CiteUrn parentUrn = new CiteUrn()

		    reply.append("<${cite2Urn.encodeSubref()}> cite:isExtendedRef <${cite2Urn.reduceToObject()}> .\n")
		    reply.append("<${cite2Urn.reduceToObject()}> cite:hasExtendedRef <${cite2Urn.encodeSubref()}> .\n")
				urn2encoded = cite2Urn.encodeSubref()
		    urn2ok = true
		    break

					case (RefType.CITE):
						CiteUrn tempCite = new CiteUrn(urn2)
						Cite2Urn tempCite2 = new Cite2Urn(tempCite)
						urn2encoded = tempCite2.encodeSubref()
						urn2ok = true
						break
					case (RefType.CTS):
						urn2encoded = new CtsUrn(urn2).encodeSubref()
						urn2ok = true
						break


	    default:
		    break
    }
    if (debug > WARN) { System.err.println "urn2 ok? " + urn2ok }

    if (urn1ok && urn2ok) {
      // FORMAT HERE FOR EXTENDED

      reply.append("<${urn1encoded}> ${verb} <${urn2encoded}> .\n")
      reply.append("<${urn2encoded}> ${inverse} <${urn1encoded}> .\n")
      //if (debug > WARN) { System.err.println "BOTH urns OK so added lines: " + reply.toString()}

    } else {
      //if (debug > WARN)  { System.err.println "CiteIndex:formatPair: Emptying buffer" }
      return reply = new StringBuffer("")
    }
    //if (debug > WARN) { System.err.println "CiteIndex:formatPair: formatted " + reply.toString() }
    return reply.toString()
  }

  /**
   */
  String ttlFromFile(String fileName, String verb, String inverse) {
    if (debug >= WARN) {System.err.println "CiteIndex:ttlFromFile ${fileName}"}
    File indexFile
    if (this.sourceDirectory) {
      indexFile = new File(this.sourceDirectory, fileName)
    } else {
      indexFile = new File(fileName)
    }

    StringBuffer reply = new StringBuffer()
    if (fileName ==~ /.+csv/) {
      CSVReader reader = new CSVReader(new FileReader(indexFile))
      Integer no = 0
      reader.readAll().each { ln ->
	no++;
	if (ln.size() > 1) {
	  reply.append(CiteIndex.formatPair(ln[0], ln[1], verb, inverse ))
	} else {
	  System.err.println "CiteIndex: in file ${fileName}, empty row, line ${no}."
	}
      }
    } else if (fileName ==~ /.+tsv/) {
      if (debug >= WARN) {
	System.err.println "CiteIndex: indexing " + fileName
      }
      Integer no = 0
      indexFile.eachLine { ln ->
	no++
	def cols = ln.split(/\t/)
	if (cols.size() > 1) {

	  String formatted = CiteIndex.formatPair(cols[0], cols[1], verb, inverse)
	  if (debug > WARN) {
	    System.err.println "CiteIndex: add row .. " + ln
	    System.err.println "Formatted as " + formatted
	  }
	  reply.append(formatted)
	} else {
	  System.err.println "CiteIndex: in file ${fileName} unable to process tsv columns in line ${no}."
	}

      }
    } else {
      System.err.println "CiteIndex:ttlFromFile : NO MATCH for name " + fileName
    }
    if (debug > WARN) { System.err.println "CiteIndex: returning " + reply.toString()}
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

	if (debug > WARN) {
	  println "CiteIndex:ttl:  indices ${indices}"
	}
        this.indices.each {idx ->
	if (debug > WARN) {
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
