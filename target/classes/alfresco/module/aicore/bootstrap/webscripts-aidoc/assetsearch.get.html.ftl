<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head profile="http://a9.com/-/spec/opensearch/1.1/"> 
    <title>AI Doc Search</title>

    <meta name="totalResults" content="${search.totalResults}"/>
    <meta name="startIndex" content="${search.startIndex}"/>
    <meta name="itemsPerPage" content="${search.itemsPerPage}"/>
    <meta name="author" content="Amnesty International, International Secretariat" />
    <meta name="copyright" content="The copyright for this material rests with Amnesty International. You may not alter this information, repost or sell it without prior permission." />
    <meta name="Description" content="Amnesty International is deeply concerned that the Government of Zimbabwe is using provisions of national legislation to silence dissent, perpetrate human rights violations and effectively place the rights of Zimbabweans under siege." />
    <meta name="Keywords" content="AFRICA, IMPUNITY, ZIMBABWE, HARASSMENT, LEGISLATION, SOUTHERN AFRICA, FREEDOM OF EXPRESSION, TORTURE/ILL-TREATMENT, FREEDOM OF ASSOCIATION, ILO, MEC, LAWYERS, TEACHERS, ELECTIONS, JOURNALISTS, PHOTOGRAPHS, BROADCASTERS, LAND PROBLEMS, DEMONSTRATIONS, TRADE UNIONISTS, CONSTITUTIONAL CHANGE, HUMAN RIGHTS DEFENDERS, USE OF EXCESSIVE FORCE, POLITICALLY MOTIVATED CRIMINAL CHARGES, ZIMBABWEhuman rights, amnesty international, AI, international secretariat, justice, death penalty, armed conflict, violence against women, activists, human rights defenders, refugees, war on terror, torture, universal declaration of human rights, take action, campaigns" />

    <link href="http://www.amnesty.org/amnesty.css" rel="stylesheet" type="text/css" />
    <link rel="search" type="application/opensearchdescription+xml" href="${url.serviceContext}/api/search/keyword/description.xml" title="Alfresco Keyword Search"/>
 
    <script language="JavaScript" type="text/javascript">
        //<![CDATA[
        <!-- 
        function popEmailAFriend(lang) {
            window.open("/automailer/app.nsf/MailToFriend?ReadForm&language_isl=" + lang + "&page_title=" + escape(document.title) + "&page_url=" + document.location, "email_a_friend", "fullscreen=no,toolbar=no,status=no,menubar=no,scrollbars=no,resizable=yes,directories=no,location=no,width=350,height=490")
        }
        // -->
        //]]>
    </script>

    <script language="JavaScript" type="text/javascript">
        //<![CDATA[
        <!-- 
        function popEmailAFriend(lang) {
            window.open("/automailer/app.nsf/MailToFriend?ReadForm&language_isl=" + lang + "&page_title=" + escape(document.title) + "&page_url=" + document.location, "email_a_friend", "fullscreen=no,toolbar=no,status=no,menubar=no,scrollbars=no,resizable=yes,directories=no,location=no,width=350,height=490")
        }
        // -->
        //]]>
    </script>
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
							 		<#include "theme-header.inc" parse="true">
							 <!--v6.0LibraryHeaderTable: END-->

                <table width="100%" border="0" cellspacing="0"
                cellpadding="19" dir="ltr">
                  <tr align="left" valign="top">
                    <td width="26%">
                      <br />

											<!--v6.0LeftHeaderTable: START-->
													<#include "theme-left.inc" parse="true">
											<!--v6.0LeftHeaderTable: END-->
		  
                      <span class="navigator"><br />
                      <br /></span>

                    <td width="74%">
                      <div id="content" align="left" dir="ltr">


<!-- Main body of the search results BEGIN -->
    <table>
      <tr>
        <td>Results <b>${search.startIndex}</b> - <b><#if search.startIndex == 0>0<#else>${search.startIndex + search.totalPageItems - 1}</#if></b> of <b>${search.totalResults}</b> for <b>${search.searchTerms?replace('!', ' ')}</b> visible to user <b><#if person??>${person.properties.userName}<#else>unknown</#if>.</b></td>
     </tr>
    </table>
    <table>
		      <tr>
		        <td><a href="${scripturl("?q=${search.searchTerms?url}&p=1&c=${search.itemsPerPage}&lang=${search.localeId}")}">first</a></td>
		<#if search.startPage &gt; 1>
		        <td><a href="${scripturl("?q=${search.searchTerms?url}&p=${search.startPage - 1}&c=${search.itemsPerPage}&lang=${search.localeId}")}">previous</a></td>
		</#if>
		        <td><a href="${scripturl("?q=${search.searchTerms?url}&p=${search.startPage}&c=${search.itemsPerPage}&lang=${search.localeId}")}">${search.startPage}</a></td>
		<#if search.startPage &lt; search.totalPages>
		        <td><a href="${scripturl("?q=${search.searchTerms?url}&p=${search.startPage + 1}&c=${search.itemsPerPage}&lang=${search.localeId}")}">next</a></td>
		        <td><a href="../${search.startPage + 1}/">google next</a></td>
		</#if>
		        <td><a href="${scripturl("?q=${search.searchTerms?url}&p=${search.totalPages}&c=${search.itemsPerPage}&lang=${search.localeId}")}">last</a></td>
		      </tr>
    </table>
    <br>
    <table>
<#list search.results as row>

			<#assign query="">
			<#if row.properties["sys:locale"]??>
					 <#assign query="lang=${row.properties['sys:locale']}">
			</#if>

			<#assign guestAccess="">
     	<#if guest??>
	      	<#assign guestAccess="guest=true">
     	</#if>
			      
      <#assign aiIndex="NO INDEX">
      <#assign docPageUrl="">
      <#if row.properties["aicore:aiIndex"]??>
        <#assign aiIndex=row.properties["aicore:aiIndex"]>
      	
      	<#assign year=aiIndex?substring(11,15)>
      	<#assign classSubclass=aiIndex?substring(0,3)+aiIndex?substring(4,6)>
      	<#assign docnum=aiIndex?substring(7,10)>
      	
      	<#assign docPageUrl="${url.serviceContext}/aidoc/asset/${year}/${classSubclass}/${docnum}?${query}">
      	<#assign libraryPageUrl="/en/library/asset/${year}/${classSubclass}/${classSubclass?lower_case}${docnum}${year}${row.properties['sys:locale']}.html">
      </#if>
      <tr>
					<td style="vertical-align: top;width:85px">${row.properties['from']?date}</td>
					<td style="vertical-align: top;">
					<#if row.properties["aicore:aiIndex"]??>
					<a href="${docPageUrl}">${row.properties["cm:title"]} <small>(${aiIndex})</small></a>
					<a href="${libraryPageUrl}">Google Link</small></a>
					<#else>
					${row.properties["cm:title"]} <small>(${aiIndex})</small>
					</#if>
					</td>
			</tr>
			<#if row.properties.description?? == true>
			<tr>
					<td></td>
					<td>${row.properties.description}</td>
			</tr>
			</#if>
</#list>
    </table>
    <br>
    <table>
		      <tr>
		        <td><a href="${scripturl("?q=${search.searchTerms?url}&p=1&c=${search.itemsPerPage}&lang=${search.localeId}")}">first</a></td>
		<#if search.startPage &gt; 1>
		        <td><a href="${scripturl("?q=${search.searchTerms?url}&p=${search.startPage - 1}&c=${search.itemsPerPage}&lang=${search.localeId}")}">previous</a></td>
		</#if>
		        <td><a href="${scripturl("?q=${search.searchTerms?url}&p=${search.startPage}&c=${search.itemsPerPage}&lang=${search.localeId}")}">${search.startPage}</a></td>
		<#if search.startPage &lt; search.totalPages>
		        <td><a href="${scripturl("?q=${search.searchTerms?url}&p=${search.startPage + 1}&c=${search.itemsPerPage}&lang=${search.localeId}")}">next</a></td>
		</#if>
		        <td><a href="${scripturl("?q=${search.searchTerms?url}&p=${search.totalPages}&c=${search.itemsPerPage}&lang=${search.localeId}")}">last</a></td>
		      </tr>
    </table>
<!-- Main body of the search results END -->



                      </div>
                    </td>
                  </tr>
                </table>

                <div align="center">
                  <br />
                  <!--v6.0FooterLinks: START-->
                  <br />

                  <p class="small"><a href=
                  "http://www.amnesty.org/aboutai">ABOUT AI</a>
                  <img src=
                  "http://www.amnesty.org/images/vertical_bar.gif"
                  width="3" height="7" alt="" border="0" />
                  <a href="http://www.amnesty.org/news">NEWS</a>
                  <img src=
                  "http://www.amnesty.org/images/vertical_bar.gif"
                  width="3" height="7" alt="" border="0" />
                  <a href="http://www.amnesty.org/library">LIBRARY</a>
                  <img src=
                  "http://www.amnesty.org/images/vertical_bar.gif"
                  width="3" height="7" alt="" border="0" />
                  <a href="http://www.amnesty.org/actnow">ACT
                  NOW</a> <img src=
                  "http://www.amnesty.org/images/vertical_bar.gif"
                  width="3" height="7" alt="" border="0" />
                  <a href="http://www.amnesty.org/campaigns">CAMPAIGNS</a>
                  <img src=
                  "http://www.amnesty.org/images/vertical_bar.gif"
                  width="3" height="7" alt="" border="0" />
                  <a href="http://www.amnesty.org/resources">RESOURCES
                  &amp; LINKS</a> <img src=
                  "http://www.amnesty.org/images/vertical_bar.gif"
                  width="3" height="7" alt="" border="0" />
                  <a href="http://www.amnesty.org/contact">CONTACT
                  US</a> <img src=
                  "http://www.amnesty.org/images/vertical_bar.gif"
                  width="3" height="7" alt="" border="0" />
                  <a href="http://www.amnesty.org/sitemap">SITEMAP</a></p>

                  <p class="copyright">&#169; Copyright Amnesty
                  International</p><br />
                  <br/>
                </div>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </div>
</body>
</html>


