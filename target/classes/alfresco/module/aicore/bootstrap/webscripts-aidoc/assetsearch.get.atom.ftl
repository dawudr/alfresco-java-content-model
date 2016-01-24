<?xml version="1.0" encoding="UTF-8"?>
<feed xmlns="http://www.w3.org/2005/Atom" 
      xmlns:ai="http://www.amnesty.org/search/1.0/" 
      xmlns:opensearch="http://a9.com/-/spec/opensearch/1.1/" 
      xmlns:relevance="http://a9.com/-/opensearch/extensions/relevance/1.0/" 
      xmlns:alf="http://www.alfresco.org/opensearchds/1.0/">
  <generator version="1.0.9-SNAPSHOT">Alfresco (${server.edition})</generator>
  <title>Alfresco Keyword Search: ${search.searchTerms}</title> 
  <updated>${xmldate(date)}</updated>
  <icon>${absurl(url.context)}/images/logo/AlfrescoLogo16.ico</icon>
  <author> 
    <name><#if person??>${person.properties.userName}<#else>unknown</#if></name>
  </author> 
  <id>urn:uuid:org.amnesty:amp-aicore:amp:1.0.9-SNAPSHOT</id>
  <opensearch:totalResults>${search.totalResults}</opensearch:totalResults>
  <opensearch:startIndex>${search.startIndex}</opensearch:startIndex>
  <opensearch:itemsPerPage>${search.itemsPerPage}</opensearch:itemsPerPage>
  <#assign searchString=search.searchTerms?replace("\"", "&quot;")>
  <opensearch:Query role="request" searchTerms="${searchString}" startPage="${search.startPage}" count="${search.itemsPerPage}" language="${search.localeId}"/>
  <link rel="alternate" href="${absurl(scripturl("?q=${search.searchTerms?url}&p=${search.startPage}&c=${search.itemsPerPage}&l=${search.localeId}")?xml)}" type="text/html"/>
  <link rel="self" href="${absurl(scripturl("?q=${search.searchTerms?url}&p=${search.startPage}&c=${search.itemsPerPage}&l=${search.localeId}")?xml)}" type="application/atom+xml"/>
<#if search.startPage &gt; 1>
  <link rel="first" href="${absurl(scripturl("?q=${search.searchTerms?url}&p=1&c=${search.itemsPerPage}&l=${search.localeId}")?xml)}" type="application/atom+xml"/>
  <link rel="previous" href="${absurl(scripturl("?q=${search.searchTerms?url}&p=${search.startPage - 1}&c=${search.itemsPerPage}&l=${search.localeId}")?xml)}" type="application/atom+xml"/>
</#if>
<#if search.startPage &lt; search.totalPages>
  <link rel="next" href="${absurl(scripturl("?q=${search.searchTerms?url}&p=${search.startPage + 1}&c=${search.itemsPerPage}&l=${search.localeId}")?xml)}" type="application/atom+xml"/> 
  <link rel="last" href="${absurl(scripturl("?q=${search.searchTerms?url}&p=${search.totalPages}&c=${search.itemsPerPage}&l=${search.localeId}")?xml)}" type="application/atom+xml"/>
</#if>
  <link rel="search" type="application/opensearchdescription+xml" href="${absurl(url.serviceContext)}/api/search/keyword/description.xml"/>

<#list search.results as row>
  <#comment>Only show result if language and effective from date are set</#comment>
  <#if row.properties["sys:locale"]?? && row.properties['from']??>
  <entry>
    <alf:noderef>${row.nodeRef}</alf:noderef>

		<#assign aiIndex="NO INDEX">
		<#assign docPageUrl="">
		<#if row.properties["aicore:aiIndex"]??>
			<#assign aiIndex=row.properties["aicore:aiIndex"]>

			<#assign year=aiIndex?substring(11,15)>
			<#assign classSubclass=aiIndex?substring(0,3)+aiIndex?substring(4,6)>
			<#assign docnum=aiIndex?substring(7,10)>

			<#assign docPageUrl="${absurl(url.serviceContext)}/aidoc/asset/${year}/${classSubclass}/${docnum}?lang=${row.properties['sys:locale']}">

			<ai:assetIndex>
				<ai:year>${year}</ai:year>
				<ai:aiclasssubclass>${classSubclass}</ai:aiclasssubclass>
				<ai:documentno>${docnum}</ai:documentno>
			</ai:assetIndex>

	    <ai:formattedAssetIndex>${row.properties["aicore:aiIndex"]}</ai:formattedAssetIndex>

		</#if>

		<#if row.properties["sys:locale"]??>
	    <ai:lang>${row.properties["sys:locale"]}</ai:lang>
		</#if>
			
    <ai:additionalLanguages>
    </ai:additionalLanguages>

	<#if row.properties["cm:title"]?exists & (row.properties["cm:title"]?length > 1)>
    <title>${row.properties["cm:title"]?html}</title>
    <#else>
    <title>${row.name?html}</title>
    </#if>

    <link rel="alternate" href="${docPageUrl}"/>
    <icon>${absurl(url.context)}${row.icon16}</icon> <#comment>TODO: What's the standard for entry icons?</#comment>
    <id>urn:uuid:org.amnesty:amp-aicore:amp:1.0.9-SNAPSHOT</id>
    <updated>${xmldate(row.properties['from'])}</updated>
    <summary>${(row.properties.description?html)!""}</summary>
    <author> 
      <name>${row.properties.creator}</name>
    </author> 

    <relevance:score>${row.score}</relevance:score>
  </entry>
  </#if>
</#list>
</feed>

<#--  Macros and Functions  -->
