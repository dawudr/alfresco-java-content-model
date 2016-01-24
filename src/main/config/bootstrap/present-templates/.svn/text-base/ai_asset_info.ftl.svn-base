<#-- Shows some general info about the Amnesty asset folder and files within its -->
<#if space?exists>
   <#if space.properties["{http://www.amnesty.org/model/aicore/1.0}aiTitle"]??>
   Asset title: ${space.properties["{http://www.amnesty.org/model/aicore/1.0}aiTitle"]}<br/>
   <#else>
   Asset title: ${space.properties["title"]}<br/>
   </#if>
   Asset publish date: 
   <#if space.properties["{http://www.amnesty.org/model/aicore/1.0}publishDate"]??> 
   ${space.properties["{http://www.amnesty.org/model/aicore/1.0}publishDate"]?date}
   </#if><br/>
   Asset Feed published: ${space.properties["{http://www.amnesty.org/model/aicore/1.0}feedPublishedStatus"]!""}<br/>
	 <#if hasAspect(space, "{http://www.amnesty.org/model/aicore/1.0}autovalidatable") = 1> 
   	Invalid: ${space.properties["{http://www.amnesty.org/model/aicore/1.0}invalidated"]?string}<br/>
 		Validation Notes: ${space.properties["{http://www.amnesty.org/model/aicore/1.0}validityNotes"]!"No notes"}<br/>
	 </#if>
<#else>
   Not an asset folder!
</#if>
