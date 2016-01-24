<?xml version="1.0" encoding="utf-8"?>
<?xml-stylesheet type="text/xsl" href="${url.context}/service/aidoc/xslt/categories" ?>
<form xmlns:xforms="http://www.w3.org/2002/xforms">

  <query value="${query}" found="${found?string}" title="${title}">
    
  <#if found == true >
  
  <category subcount="${baseCategory.children?size}" >

  <id>${baseCategory.properties["sys:node-uuid"]}</id>
  
  <name>${baseCategory.name?html}</name>
  <link>${baseCategory.name?html}</link>

    <categories>
      
      <#list baseCategory.children as i>
        
        <category subcount="${i.children?size}" >
           
          <id>${i.properties["sys:node-uuid"]}</id>

          <name>${i.name?html}</name>
          <link>${i.name?html}</link>
          
          <#if i.children?size gt 0 >
            
            <categories>
              
              <#list i.children as j >
                
                <category subcount="${j.children?size}">
                  
                  <id>${j.properties["sys:node-uuid"]}</id>
                  
                  <name>${j.name?html}</name>
                  <link>${j.name?html}</link>
                  
                  <#if j.children?size gt 0 >
                    
                    <categories>
                      
                      <#list j.children as k>
                        <category id="${k.properties["sys:node-uuid"]}" subcount="0" >
                          <id>${k.properties["sys:node-uuid"]}</id>
                          <name>${k.name?html}</name>
                          <link>${k.name?html}</link>
                        </category>
                      </#list> 
                      
                    </categories>
                    
                  </#if>
                  
                </category>
                
              </#list>
              
            </categories>
            
          </#if>
        
        </category>
        
      </#list>  
      
    </categories>
    
    </category>
  
  <#else>
  
      <message>Can't find ${query?html}</message>
  
  </#if>

</query>
  
</form>
