# `abracadabra`: a library for working with CITE indices #


## Planned functionality ##

- read a CITE Index inventory and load into an object model
- read configured indices and serialize as RDF

## Planned data sources ##

- tab-separated or comma-separated local files named either `FILE.tsv` or `FILE.csv`, repspectively
- Google Fusion Tables


## User manual ##

Overview:

- copy `conf.gradle-dist` to `conf.gradle`
- edit `conf.gradle`  to point to a configured CITE Index inventory 
- run `gradle ttl`

### Explanation ###

A CITE Index is an association of two URNs.  All indexes associating two URNs (of any type) are symmetrical.  In the CITE Index inventory, you identify the RDF verb to use for each of the two relations.
