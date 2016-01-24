<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<#include "theme-meta.inc" parse="true">
</head>
<body text="#000000" bgcolor="#FFFFFF" lang="en" xml:lang="en">

<div align="center">
<table width="2%" border="0" cellspacing="0" cellpadding="1">
<tr>
	<td bgcolor="#000000">
	<table width="100%" border="0" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
	<tr>
		<td>
		<!--v6.0LibraryHeaderTable: START-->
		<#include "theme-header.inc" parse="true">
		<!--v6.0LibraryHeaderTable: END-->
		<table width="100%" border="0" cellspacing="0" cellpadding="19" dir="ltr">
		<tr align="left" valign="top">
			<td><br />

			<!--v6.0LeftHeaderTable: START-->
					<#include "theme-left.inc" parse="true">
		  <!--v6.0LeftHeaderTable: END-->
		
		<p class="navigator">
		View this list in<br/>
			<#list renditions as rendition>
				<a href="?lang=${rendition.language}&format=html">${rendition.languageString}</a><br/>
			</#list>
		</p>
			</td>
			<td width="74%">
			
			<table width="100%" border="0"
        cellpadding="0" cellspacing="0">
          <tr valign="top">
            <td width="50%">
            
            <#if !guest>
            <a href="${absurl(url.context)}/navigate/browse/workspace/SpacesStore/${asset.node.properties["sys:node-uuid"]}">
						Edit</a>
						</#if>
</td>


            <!-- PDF Link here -->
            <td align="right" width="50%">
						&#160;&#160;
			<#if selectedRendition?? && selectedRendition.attachmentDocument?? && selectedRendition.attachmentDocument.node??>
            <a class="navigator" href="${selectedRendition.attachmentDocument.url}">
              <img src="http://www.amnesty.org/images/resources/pdf-icon.gif" hspace="2" border="0" alt="" />
            </a>
            </td>
            <td nowrap="nowrap" align="left">
            <a class="navigator" href="${selectedRendition.attachmentDocument.url}">
              PDF
            </a>

			</#if>
            </td>
			
          </tr>
        </table>
        
        			<table width="100%" border="0"
        cellpadding="0" cellspacing="0">
          <tr valign="top">
            <td width="100%">
            
            
            </td>
          </tr>
        </table>

      <h2>${selectedRendition.masterDocument.title}</h2>  
      <p> </p>  
			<table cellpadding="0" cellspacing="0" width="100%">
			  <tr>
			    <td class="navigator" style="font-size: larger;">AI Index:
			     ${asset.aiIndex}</td>
			    <td>&#160;&#160;&#160;&#160;&#160;&#160;</td>
			    <td class="navigator" style="font-size: larger;align:left">
			    <#if asset.node.properties['aicore:publishDate']??>
					  	${asset.node.properties['aicore:publishDate']?date}
  				</#if>
			    </td>
			    </tr>
			</table>
			
			<div id="content" align="left">

			<hr/>
			<table>
		  	<#list documentsByLanguage as child>
					<tr>
						<td width="25%">${child.type}</td>
						<td width="25%">${child.edition}</td>
						<td width="25%">${child.node.mimetype}</td>
						<td width="25%" align="right"><a href="${child.url}">Display</a></td>
					</tr>
		  	</#list>
			</table>	
			<hr/>

			<#if selectedRendition?? && selectedRendition.inlineDocument?? && selectedRendition.inlineDocument.node??>
				${selectedRendition.inlineDocument.node.content}
			<#else>
			No html version available
			</#if>

					
			</div>
			
			</td>
		</tr>
		</table>

<!--v6.0FooterLinks: START-->
		<#include "theme-footer.inc" parse="false">
<!--v6.0FooterLinks: END-->

		</td>
	</tr>

	</table>
	</td>
</tr>
</table>
</div>
<!-- error =  --></body>
</html>
