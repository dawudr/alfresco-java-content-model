<xsl:stylesheet version='1.0'
    xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
    xmlns:h="http://www.w3.org/1999/xhtml">

    <xsl:output method="html" encoding="UTF-8"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" />

    <xsl:template match="response">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<#include "../theme-meta.inc" parse="true">
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
		<#include "../theme-header.inc" parse="true">
		<!--v6.0LibraryHeaderTable: END-->
		<table width="100%" border="0" cellspacing="0" cellpadding="19" dir="ltr">
		<tr align="left" valign="top">
			<td><br />

			<!--v6.0LeftHeaderTable: START-->
					<#include "../theme-left.inc" parse="true">
		  <!--v6.0LeftHeaderTable: END-->
		
			</td>
			<td width="74%">
			<div id="content" align="left">

<!--v6.0LibraryHome: START-->
<h3>Document not available</h3>
<br/>
<xsl:value-of select="message" />
<!--v6.0LibraryHome: END-->

			</div>
			</td>
		</tr>
		</table>

<!--v6.0FooterLinks: START-->
		<#include "../theme-footer.inc" parse="false">
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

    </xsl:template>

</xsl:stylesheet>
