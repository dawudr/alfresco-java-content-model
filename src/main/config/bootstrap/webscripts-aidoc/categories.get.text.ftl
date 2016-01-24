<#if found == true >
	<#if true>
<!-- We want to extend this function to return alternate formats/languages. See AIDOC-81 -->
<?xml version="1.0" encoding="utf-8"?>
<vocabulary>
	<vid>${baseCategory.properties["sys:node-uuid"]}</vid>
	<name>${baseCategory.name?html}</name>
	<description/>
	<help/>
	<relations>0</relations>
	<hierarchy>${hierarchy}</hierarchy>
	<multiple>1</multiple>
	<required>0</required>
	<tags>0</tags>
	<module>taxonomy</module>
	<weight>0</weight>
	<language>en</language>
	<nodes>appeal_for_action,audio_clip,campaign,country,event,home_page_feature,issue,job_opening,news,page,people,press_release,region,report_abstract,story,subsection,video_clip</nodes>
	<#list baseCategory.children as i>
	<term>
		<tid>${i.properties["sys:node-uuid"]}</tid>
		<vid>${baseCategory.properties["sys:node-uuid"]}</vid>
		<name>${i.name?html}</name>
		<description/>
		<weight>0</weight>
		<language>en</language>
		<trid>0</trid>
		<depth>0</depth>
		<parent>0</parent>
	</term>
		<#if i.children?size gt 0 >            
			<#list i.children as j >
	<term>
		<tid>${j.properties["sys:node-uuid"]}</tid>
		<vid>${baseCategory.properties["sys:node-uuid"]}</vid>
		<name>${j.name?html}</name>
		<description/>
		<weight>0</weight>
		<language>en</language>
		<trid>0</trid>
		<depth>1</depth>
		<parent>${i.properties["sys:node-uuid"]}</parent>
	</term>          
				<#if j.children?size gt 0 >                                          
					<#list j.children as k>
	<term>
		<tid>${k.properties["sys:node-uuid"]}</tid>
		<vid>${baseCategory.properties["sys:node-uuid"]}</vid>
		<name>${k.name?html}</name>
		<description/>
		<weight>0</weight>
		<language>en</language>
		<trid>0</trid>
		<depth>2</depth>
		<parent>${j.properties["sys:node-uuid"]}</parent>
	</term>          
					</#list> 
				</#if>
			</#list>
		</#if>
	</#list>  
</vocabulary>
	</#if>
	<#if false>
		<#list baseCategory.children as i>
${i.properties["sys:node-uuid"]}, ${i.name?html}
			<#if i.children?size gt 0 >            
				<#list i.children as j >
${j.properties["sys:node-uuid"]}, ${j.name?html}
					<#if j.children?size gt 0 >                                          
						<#list j.children as k>
${k.properties["sys:node-uuid"]}, ${k.name?html}
						</#list> 
					</#if>
				</#list>
			</#if>
		</#list>  
	</#if>

</#if>
