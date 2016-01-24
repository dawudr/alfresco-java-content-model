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
						<dir href="/alfresco/service/aidoc/asset/${year}/${aiClass}/Amnesty Core AMP Project" name="Amnesty Core AMP Project" alf:noderef="${ref}"
								 ai:formattedAssetIndex="${aiClass}/Amnesty Core AMP Project/${year}" ai:description="Provides customizations of the webclient and core extensions"/>
					<#else>
						<dir href="/alfresco/service/aidoc/asset/${year}/${aiClass}/Amnesty Core AMP Project" name="Amnesty Core AMP Project" alf:noderef="${ref}"
								 ai:formattedAssetIndex="${aiClass}/Amnesty Core AMP Project/${year}"/>
					</#if>
				<#else>
        	<#if description?length &gt; 1>
						<dir href="Amnesty Core AMP Project/" name="Amnesty Core AMP Project" alf:noderef="${ref}" ai:description="Provides customizations of the webclient and core extensions"/>
					<#else>
						<dir href="Amnesty Core AMP Project/" name="Amnesty Core AMP Project" alf:noderef="${ref}"/>
					</#if>
				</#if>
    </#if>
  </#list>
</index>
</svn>



