<?xml version="1.0" encoding="iso-8859-1" standalone="no"?>
<?xml-stylesheet type="text/xsl" href="/alfresco/xslt/aiarchive.xsl"?>

<svn xmlns:ai="http://www.amnesty.org/search/1.0/" xmlns:alf="http://www.alfresco.org/opensearchds/1.0/">
<index path="${extn}">
	<#if level &gt; 0>
		<updir/>
	</#if>
	<#list selectedFolder.children as innerChild>
		<#if innerChild.isContainer>
        <#assign ref=innerChild.properties['sys:node-uuid']>
        <#assign name=innerChild.properties['cm:name']>
        <#assign description=innerChild.properties['cm:description']?if_exists?html>
        <#if level &gt; 1>
        	<#if description?length &gt; 1>
						<dir href="/alfresco/service/aidoc/asset/${year}/${aiClass}/${name}" name="${name}" alf:noderef="${ref}"
								 ai:formattedAssetIndex="${aiClass}/${name}/${year}" ai:description="${description}"/>
					<#else>
						<dir href="/alfresco/service/aidoc/asset/${year}/${aiClass}/${name}" name="${name}" alf:noderef="${ref}"
								 ai:formattedAssetIndex="${aiClass}/${name}/${year}"/>
					</#if>
				<#else>
        	<#if description?length &gt; 1>
						<dir href="${name}/" name="${name}" alf:noderef="${ref}" ai:description="${description}"/>
					<#else>
						<dir href="${name}/" name="${name}" alf:noderef="${ref}"/>
					</#if>
				</#if>
    </#if>
  </#list>
</index>
</svn>



