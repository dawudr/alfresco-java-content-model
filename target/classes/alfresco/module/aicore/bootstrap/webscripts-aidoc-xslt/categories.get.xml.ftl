<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>

    <!-- XSLT to transform XML returned by /aidoc categories.get.xml.ftl call. -->

    <xsl:output method="html" encoding="iso-8859-1" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" />
    <xsl:variable name="userLang" select="//form/input/user-language" />
    <xsl:template match="query">
    
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <meta name="author" content="Amnesty International, International Secretariat" />
  <link href="http://www.amnesty.org/amnesty.css" rel="stylesheet" type="text/css" />
  <meta name="copyright" content="The copyright for this material rests with Amnesty International. You may not alter this information, repost or sell it without prior permission." />
  <title>Amnesty International Documents by <xsl:value-of select="@title"/></title>
</head>

<body text="#000000" bgcolor="#FFFFFF" lang="en" xml:lang="en">
  <a name="top" id="top"></a>

  <div align="center">
    <table width="2%" border="0" cellspacing="0" cellpadding="1">
      <tr>
        <td bgcolor="#000000">
          <table width="100%" border="0" cellspacing="0"
          cellpadding="0" bgcolor="#FFFFFF">
            <tr>
              <td>
                
                		<!--v6.0LibraryHeaderTable: START-->
										<#include "../theme-header.inc" parse="true">
		<!--v6.0LibraryHeaderTable: END-->

                <table width="100%" border="0" cellspacing="0" cellpadding="0" dir="ltr">
                  <tr align="left" valign="top">


                    <td width="74%">
                      <div id="content" align="left" dir="ltr">
                        <a name="content" id="content"></a>

                        <div dir="ltr">




                          <table border="0" cellpadding="0" cellspacing="10" width="100%" >
                            <tr>
                            			<td valign="top" ><br />

							

        <!--v6.0LeftHeaderTable: START-->
        <#include "../theme-left.inc" parse="true">
        <!--v6.0LeftHeaderTable: END-->
			
			</td>

                            
                              <td >
                              
                              
                                 <table border="0" cellpadding="5" cellspacing="5" width="100%" >
                                 <xsl:for-each select="category">
                                 
                                  <tr><td>
                                  <h2><xsl:value-of select="name"/></h2>
                                  
                                    <table border="0" cellpadding="5" cellspacing="5" width="100%" >
                                    <xsl:for-each select="categories/category">
                                 
                                    <tr>
                                    <td>
                                    <xsl:if test="@subcount!='0'"><h3><a href="../search?q=cat:%22{link}%22" ><xsl:value-of select="name"/></a></h3></xsl:if>
                                    <xsl:if test="@subcount='0'"><a href="../search?q=cat:%22{link}%22" ><xsl:value-of select="name"/></a></xsl:if>
                                  
                                    <table border="0" cellpadding="5" cellspacing="10" width="100%" > 
                                    <xsl:for-each select="categories/category">
                                    
                                    <tr> 
                                 
                                      <td>
                                  
                                    <xsl:if test="@subcount!='0'"><h4><a href="../search?q=cat:%22{link}%22" ><xsl:value-of select="name"/></a></h4></xsl:if>
                                    <xsl:if test="@subcount='0'"><a href="../search?q=cat:%22{link}%22" ><xsl:value-of select="name"/></a></xsl:if>
                                  
                                      <table border="0" cellpadding="2" cellspacing="2" width="100%" >
                                      <xsl:for-each select="categories/category">
                                 
                                      <tr>
                                      <td>


                                           <a href="../search?q=cat:%22{link}%22" ><xsl:value-of select="name"/></a>

                                      </td>
                                      </tr>
                                 
                                      </xsl:for-each >
                                      </table>
                                  
                                      </td>

                                      </tr>

                                 
                                  </xsl:for-each >
                                  </table>
                                  
                                  
                                  </td>
                                  </tr>
                                 
                                  </xsl:for-each >
                                  </table>
                                  
                                  
                                  </td>
                                  </tr>
                                 
                                 </xsl:for-each >
                                  </table>
                              
                              </td>
                            </tr>
                          </table>
                          
                        </div>
                        <p class="navigator">
                        <a href="#top">Back to Top</a> 
                        <a href="#top">
                        <img src="http://www.amnesty.org/images/double_caret.gif" alt="" width="18" height="10" border="0" /></a>
                        </p>
                      </div>
                    </td>
                  </tr>
                </table>

                <div align="center">
                  <br />
                  <!--v6.0FooterLinks: START-->
											<#include "../theme-footer.inc" parse="false">
									<!--v6.0FooterLinks: END-->
                </div>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </div><!-- error =  -->
</body>
</html>

    </xsl:template>

</xsl:stylesheet>
