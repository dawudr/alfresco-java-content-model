<xsl:stylesheet version='1.0'
    xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
    xmlns:h="http://www.w3.org/1999/xhtml">

    <!-- XSLT to transform XML returned by /aidoc aiiindex.get.xml.ftl call. -->

    <xsl:output method="html" encoding="UTF-8"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" />

    <xsl:variable name="userLang" select="//form/input/user-language" />

    <xsl:template match="form">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
  <#include "../theme-meta.inc" parse="true">
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
               
          

                <table width="100%" border="0" cellspacing="0"
                cellpadding="19" dir="ltr">
                  <tr align="left" valign="top">
                    <td width="26%">
                      <br />

        <!--v6.0LeftHeaderTable: START-->
        <#include "../theme-left.inc" parse="true">
        <!--v6.0LeftHeaderTable: END-->

                      <xsl:variable name="htmlParts" select="IndexedAsset/editions/ReportEdition/parts/part[@mimetype='application/xhtml+xml' and @lang!=$userLang]"/>
                      <xsl:if test="count($htmlParts) > 0">
                      <!--translation list: START-->
                      <p class="navigator" align="left">View this page in<br/>
                        <xsl:for-each select="$htmlParts">
                          <a>
                            <xsl:attribute name="href">?lang=<xsl:value-of select="@lang" /></xsl:attribute>
                            <xsl:choose>
                              <xsl:when test="@lang='ar'">
                                <img src="http://www.amnesty.org/images/library/ara.gif" border="0" alt="Arabic" />
                              </xsl:when>
                              <xsl:when test="@lang='en'">
                                <img src="http://www.amnesty.org/images/library/eng.gif" border="0" alt="English" />
                              </xsl:when>
                              <xsl:when test="@lang='es'">
                                <img src="http://www.amnesty.org/images/library/esl.gif" border="0" alt="EspaÃÂÃÂ±ol" />
                              </xsl:when>
                              <xsl:when test="@lang='fr'">
                                <img src="http://www.amnesty.org/images/library/fra.gif" border="0" alt="Fran&#195;&#167;ais" />
                              </xsl:when>
                              <xsl:otherwise>
                                Unknown language: (<xsl:value-of select="@lang"/>)
                              </xsl:otherwise>
                            </xsl:choose>
                          </a>
                          <br />
                        </xsl:for-each>
                      </p>
                      <!--translation list: END-->
                      </xsl:if>
                    </td>

                    <td width="74%">
                      <div id="content" align="left" dir="ltr">
                        <a name="content" id="content"></a>

                        <div dir="ltr">
                          <table width="100%" border="0"
                          cellpadding="0" cellspacing="0">
                            <tr valign="top">
                              <td width="100%">&#160;</td>

                              <!-- PDF Link here -->
                              <td align="center">
                              <a class="navigator">
                                <xsl:attribute name="href">
                                  <xsl:value-of select="IndexedAsset/editions/ReportEdition/parts/part[@format='PDF' and @lang=$userLang]/url" />
                                </xsl:attribute>
                                <img src="http://www.amnesty.org/images/resources/pdf-icon.gif" hspace="2" border="0" alt="" />
                              </a>
                              </td>
                              <td nowrap="nowrap" align="left">
                              <a class="navigator">
                                <xsl:attribute name="href">
                                  <xsl:value-of select="IndexedAsset/editions/ReportEdition/parts/part[@format='PDF' and @lang=$userLang]/url" />
                                </xsl:attribute>
                                PDF
                              </a>
                              </td>
                            </tr>
                          </table>

                          <div>
                            <xsl:call-template name="renderCategories">
                              <xsl:with-param name="categories" select="IndexedAsset/categories/*[@type='region']"/>
                              <xsl:with-param name="cat_title" select="'Regions'"/>
                            </xsl:call-template>
                            <xsl:call-template name="renderCategories">
                              <xsl:with-param name="categories" select="IndexedAsset/categories/*[@type='keyword']"/>
                              <xsl:with-param name="cat_title" select="'Keywords'"/>
                            </xsl:call-template>
                            <xsl:call-template name="renderCategories">
                              <xsl:with-param name="categories" select="IndexedAsset/categories/*[@type='campaign']"/>
                              <xsl:with-param name="cat_title" select="'Campaigns'"/>
                            </xsl:call-template>
                            <xsl:call-template name="renderCategories">
                              <xsl:with-param name="categories" select="IndexedAsset/categories/*[@type='issue']"/>
                              <xsl:with-param name="cat_title" select="'Issues'"/>
                            </xsl:call-template><br/><br/>
                          </div>

                        <table cellpadding="0" cellspacing="0">
                          <tr>
                            <td class="navigator" style="font-size: larger;">AI Index:
                              <xsl:value-of select="substring(input/assetIndex/aiclasssubclass,1,3)"/>&#160;<xsl:value-of select="substring(input/assetIndex/aiclasssubclass,4,5)"/>/<xsl:value-of select="input/assetIndex/documentno"/>/<xsl:value-of select="input/assetIndex/year"/>
                            </td>
                            <td>&#160;&#160;&#160;&#160;&#160;&#160;</td>
                            <td class="navigator" style="font-size: larger;">
                              <xsl:value-of select="IndexedAsset/editions/ReportEdition/parts/part[@master='true' and @lang=$userLang]/effectiveDate"/>
                            </td>
                          </tr>
                        </table>
                        <br/>
                        </div>

<!-- Main body of the document BEGIN -->
<div id="documentBody">
  <iframe width="100%" height="1000" frameborder="0">
    <xsl:attribute name="src">
      <xsl:value-of select="IndexedAsset/editions/ReportEdition/parts/part[@format='XHTML' and @lang=$userLang]/url" />
    </xsl:attribute>
  </iframe>
</div>
<!-- Main body of the document END -->

   <#-- Originally implemented it like this the following however it fails in Internet Explorer (see AIDOC-96)
        Copy the styles from the body element to this div.
        NOTE: there are styles defined in head/style that are not copied over here ... --
    <xsl:variable name="htmldoc" select="document(IndexedAsset/editions/ReportEdition/parts/part[@format='XHTML' and @lang=$userLang]/url)"/>
    <xsl:attribute name="style">
        <xsl:value-of select="$htmldoc/h:html/h:body/@style"/>
    </xsl:attribute>
    <xsl:copy-of select="$htmldoc/h:html/h:body/*"/ -->

                        <table cellpadding="0" cellspacing="0">
                          <tr>
                            <td class="navigator" style="font-size: larger;">AI Index:
                              <xsl:value-of select="substring(input/assetIndex/aiclasssubclass,1,3)"/>&#160;<xsl:value-of select="substring(input/assetIndex/aiclasssubclass,4,5)"/>/<xsl:value-of select="input/assetIndex/documentno"/>/<xsl:value-of select="input/assetIndex/year"/>
                            </td>
                            <td>&#160;&#160;&#160;&#160;&#160;&#160;</td>
                            <td class="navigator" style="font-size: larger;">
                              <xsl:value-of select="IndexedAsset/editions/ReportEdition/parts/part[@master='true' and @lang=$userLang]/effectiveDate"/>
                            </td>
                          </tr>
                        </table><br />

                        <table width="100%" border="0" cellpadding=
                        "0" cellspacing="0">
                          <tr valign="top">
                            <td width="100%">&#160;</td>                            

                            <!-- PDF Link here -->
                            <td>
                              <a class="navigator">
                                <xsl:attribute name="href">
                                <xsl:value-of select="IndexedAsset/editions/ReportEdition/parts/part[@format='PDF' and @lang=$userLang]/url" />
                                </xsl:attribute>
                                <img src="http://www.amnesty.org/images/resources/pdf-icon.gif" hspace="2" border="0" alt="" />
                              </a>
                            </td>

                            <td nowrap="nowrap" align="left">
                              <a class="navigator">
                                <xsl:attribute name="href">
                                <xsl:value-of select="IndexedAsset/editions/ReportEdition/parts/part[@format='PDF' and @lang=$userLang]/url" />
                                </xsl:attribute>
                                PDF
                              </a>
                            </td>
                            <!-- PDF Link here -->
                          </tr>
                        </table><br />

                        <table border="0" cellpadding="4"
                        cellspacing="1" bgcolor="#666666" width=
                        "100%">
                          <tr align="center" bgcolor="#B3C2FB">
                            <td align="center"><strong>Further
                            information</strong></td>
                          </tr>

                          <tr bgcolor="#ECF3FA">
                            <td class="navigator">
                              <p class="navigator"><a href=
                              "http://web.amnesty.org/report2005/zwe-summary-eng">
                              AI Report 2005 entry</a></p>
                            </td>
                          </tr>
                        </table><br />

                        <p class="navigator"><a href="#top">Back to
                        Top</a> <a href="#top"><img src=
                        "http://www.amnesty.org/images/double_caret.gif"
                        alt="" width="18" height="10" border=
                        "0" /></a></p>
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
</body>
</html>

    </xsl:template>

    <xsl:template name="renderCategories">
      <xsl:param name="categories"/>
      <xsl:param name="cat_title"/>
      <xsl:if test="count($categories) > 0">
         <b><xsl:value-of select="$cat_title"/>:</b>&#160;
        <xsl:for-each select="$categories">
	      <a>
            <xsl:attribute name="href">/alfresco/service/aidoc/search?q=cat:%22<xsl:value-of select="." />%22</xsl:attribute>
            <xsl:value-of select="."/>
          </a>&#160;
        </xsl:for-each><br/>
      </xsl:if>
    </xsl:template>

</xsl:stylesheet>
