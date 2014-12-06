# Abracadabra #

library for working with CITE Indices

Given input comprised of two URNs:

- <strong concordion:set="#urn1">urn:cite:hmt:reusedtext.4</strong> and
-  <strong concordion:set="#urn2">urn:cts:greekLit:tlg4036.tlg023.msA_tokens:Homer.11@Ἡσίοδος-Homer.11@Ὅμηρον</strong> 


and two complementary verbs, 

- <strong concordion:set="#verb1">hmt:reuses</strong> and
- <strong concordion:set="#verb2">hmt:isReusedBy</strong>

we generate the following four RDF statements in TTL format:



<pre concordion:assertTrue="tordf(#urn1, #urn2, #verb1, #verb2, #TEXT)">&lt;urn:cts
:greekLit:tlg4036.tlg023.msA_tokens:Homer.11@Ἡσίοδος-Homer.11@Ὅμηρον> cite:isExtendedRef &lt;urn:cts:greekLit:tlg4036.tlg023:Homer.11@Ἡσίοδος-Homer.11@Ὅμηρον> .
&lt;urn:cts:greekLit:tlg4036.tlg023:Homer.11@Ἡσίοδος-Homer.11@Ὅμηρον> cite:hasExtendedRef &lt;urn:cts:greekLit:tlg4036.tlg023.msA_tokens:Homer.11@Ἡσίοδος-Homer.11@Ὅμηρον> .
&lt;urn:cite:hmt:reusedtext.4> hmt:reuses &lt;urn:cts:greekLit:tlg4036.tlg023.msA_tokens:Homer.11@Ἡσίοδος-Homer.11@Ὅμηρον> .
&lt;urn:cts:greekLit:tlg4036.tlg023.msA_tokens:Homer.11@Ἡσίοδος-Homer.11@Ὅμηρον> hmt:isReusedBy &lt;urn:cite:hmt:reusedtext.4> .</pre>