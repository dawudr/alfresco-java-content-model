<?xml version="1.0" encoding="utf-8"?>
<Asset>
  <id>${asset.node.properties["sys:node-uuid"]}</id>
  <index>${asset.aiIndex}</index>
  <latinTitle>${asset.latinTitle?xml?j_string}</latinTitle>
  <creationDate>${asset.node.properties.created?date}</creationDate>
  <#if asset.node.properties['aicore:publishDate']??>
    <publishDate>${asset.node.properties['aicore:publishDate']?date}</publishDate>
  </#if>
  <type>report</type>
    
  <#if editionFolder??>
      <#if editionFolder.properties.categories??>
        <#assign categories=editionFolder.properties.categories>
      </#if>
  </#if>
    <categories>
  <#if asset.node.properties.categories??>
        <#list getCategoriesForType(asset.node.properties.categories, "Regions") as region>
    <category type="region">Amnesty Core AMP Project</category>
    </#list>
    <#list getCategoriesForType(asset.node.properties.categories, "Keywords") as keyword>
    <category type="keyword">Amnesty Core AMP Project</category>
    </#list>
    <#list getCategoriesForType(asset.node.properties.categories, "Campaigns") as campaign>
    <category type="campaign">Amnesty Core AMP Project</category>
    </#list>
    <#list getCategoriesForType(asset.node.properties.categories, "Issues") as issue>
    <category type="issue">Amnesty Core AMP Project</category>
    </#list>
  </#if>
  </categories>

  <!--Summary>
        <document lang="en" title="The Summary" inlineHtml="path">
            <format master="true" mimetype="application/msword" content=""/>
            <format mimetype="text/html" content=""/>
            <format mimetype="application/PDF" content=""/>
        </document>
    </Summary-->
    
    <!--PinkSheet>
        <document lang="en" title="Internal instructions" inlineHtml="path">
            <format master="true" mimetype="application/msword" content=""/>
            <format mimetype="text/html" content=""/>
            <format mimetype="application/PDF" content=""/>
        </document>
    </PinkSheet-->

  <!-- Which nodes to display by default -->
  <renditions>
  <#list renditions as rendition>
    <rendition language="${rendition.language}">
            <#if rendition.masterDocument??>
                <masterDocument><@renderDocument document=rendition.masterDocument/></masterDocument>
            </#if>

            <#if rendition.inlineDocument??>
                <inlineRendition><@renderDocument document=rendition.inlineDocument/></inlineRendition>
            </#if>

            <#if rendition.attachmentDocument?? && rendition.attachmentDocument.node??>
                 <#if (rendition.attachmentDocument.node.content?j_string?length gt 0) || inAidoc>
                 <attachmentRendition><@renderDocument document=rendition.attachmentDocument/></attachmentRendition>
                 <#else>
								 <document nil="true" />
     	 					 </#if>
            </#if>
    </rendition>
    </#list>
  </renditions>
  
  <documents>
    <#list documents as child>
        <@renderDocument document=child/>
    </#list>
  </documents>
</Asset>

<#--  Macros and Functions  -->

<#macro renderDocument document>
  <#if document.node??>
    <#assign lang=document.node.name?substring(document.node.name?index_of("_")+1, document.node.name?index_of("."))>
    <#assign ref=document.node.properties['sys:node-uuid']>
    <#assign mimetype=document.node.mimetype>
    <#assign format=document.format>
    
     <document type="${document.type}">
       <#if document.node.content?j_string?length gt 0>
			        <id>${ref}</id>
			 <#else>
			          <#if inAidoc>
			               <id>dom-${asset.aiIndex?replace("/","")?replace(" ","")}</id>
			          <#else>
			               <id>${asset.aiIndex?replace("/","")?replace(" ","")}</id>
			          </#if>
     	 </#if>
       <#if document.inline??>
            <disposition>inline</disposition>
       <#else>
            <disposition>attachment</disposition>
       </#if>
       <mimetype>${mimetype}</mimetype>
       <language>${document.language}</language>
       <format>${format}</format>
       <title>${document.title?xml?j_string}</title>
       <#if document.description??>
            <description>${document.description?xml?j_string}</description>
       </#if>
       <#if document.effectiveFrom??>
            <effectiveDateFrom>${document.effectiveFrom?date}</effectiveDateFrom>
       </#if>
       <#if document.effectiveTo??>
            <effectiveDateTo>${document.effectiveTo?date}</effectiveDateTo>
       </#if>
       <#if document.node.content?j_string?length gt 0>
			        <url>${url.context}${document.url}</url>
			        <#else>
								<#if inAidoc>
       			        <url>dom-${asset.aiIndex?replace("/","")?replace(" ","")}${document.language}.${format?lower_case}</url>
								<#else>
      			        <url>${asset.aiIndex?replace("/","")?replace(" ","")}${document.language}.${format?lower_case}</url>
			          </#if>
			        
     	 </#if>
       <#list document.hasParts as part>
             <part type="${part.type}">   
                    ${part.node.properties['sys:node-uuid']}
             </part>      
                </#list>  
       <edition>${document.edition}</edition>
       
     </document>
  <#else>
    <document nil="true" />
  </#if>     
</#macro> 

<#function getCategoriesForType categories type>
  <#assign matchingCategories=[]>
  <#list categories as category>
    <#if category.displayPath?contains("/" + type)>
      <#assign matchingCategories=matchingCategories + [category]>
    </#if>
  </#list>
  <#return matchingCategories>
</#function>

