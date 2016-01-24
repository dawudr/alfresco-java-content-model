package org.amnesty.aidoc.webscript;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.SubmitMethod;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

import junit.framework.Test;
import junit.framework.TestSuite;

/*
 * Class to compare Asset Index, Keywords, Title, Decription and Language properties of AI document
 * against search results returned from Google Search Applicance
 * 
 * Existing webscripts utilised:
 * Sample List of AI document URLS from: /service/aidoc/asset_feed/2009/
 * http://localhost:8888/amp-aicore-webapp/service/aidoc/asset_feed/2009/
 * Asset properties obtained from: /service/aidoc/asset_all/2009/AMR51/001
 * http://localhost:8888/amp-aicore-webapp/service/aidoc/asset_all/2010/PRE01/001
 * Google Search Appliance API: /service/intranet/search
 * OR 
 * Amnesty website search.
 * 
 * https://cmsqa.amnesty.org/service/aidoc/list_indexes/2009/AMR51
 */
public class GoogleSearchApplianceRegressionTest extends SeleneseTestCase {
	protected static Selenium selenium;
	protected static WebClient webClient;
	protected final String aiDocumentWebscriptURLStr = "https://cmsqa.amnesty.org/service/aidoc/asset_feed/2010/mde31";
	protected final String aiDocumentPropertiesWebScriptPrefix = "https://cmsqa.amnesty.org/service/aidoc/asset_all/";
	protected int passes = 0;
	protected int failures = 0;
	private static SimpleDateFormat sourceDateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
	private static SimpleDateFormat targetDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	private static ArrayList<String> aiDocumentUrlArray;
	protected String language = null;
	protected String index = null;
	protected String securityClass = null;
	protected String publishDateStr = null;
	protected String type = null;
	protected ArrayList<String> categoryArray = null;
	protected ArrayList<String> secCategoryArray = null;
	protected String title = null;
	protected String titlefull = null;	
	protected String description = null;
	protected String searchFormType = null;

	public GoogleSearchApplianceRegressionTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		String serverLogin = "admin";
		String serverPassword = "Wega82*k";
		DefaultCredentialsProvider credentialsProvider = new DefaultCredentialsProvider();
		credentialsProvider.addCredentials(serverLogin, serverPassword);
		webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_6_0);
		webClient.setCredentialsProvider(credentialsProvider);
		webClient.setThrowExceptionOnFailingStatusCode(false);
		System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "error");
		/*
		 * System.setProperty("org.apache.commons.logging.Log",
		 * "org.apache.commons.logging.impl.SimpleLog");
		 * System.setProperty("org.apache.commons.logging.simplelog.showdatetime"
		 * , "true");System.setProperty(
		 * "org.apache.commons.logging.simplelog.log.httpclient.wire.header",
		 * "error");System.setProperty(
		 * "org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient"
		 * , "error");
		 */

		selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://www.amnesty.org/");
		selenium.start();
/*		selenium.open("http://www.amnesty.org/");
		selenium.click("op");
		selenium.waitForPageToLoad("200000");
		selenium.click("link=Advanced Search");*/

		aiDocumentUrlArray = getaiDocumentUrlArray();
	}

	public void tearDown() {
		selenium.stop();
	}

	/*
	 * returns xml Document containing output of ASSET_ALL webscript
	 */
	private Document getaiDocument(String aiDocumentUrlString) {
		// construct aiDocumentPropertiesWebScriptURL
		String restrictedPatternStr = "service/library/restricted/";
		String publicPatternStr = "service/library/index/";
		String aiDocumentPropertiesWebScriptString = (aiDocumentUrlString.contains("restricted")) ? aiDocumentUrlString.substring(aiDocumentUrlString.lastIndexOf(restrictedPatternStr) + restrictedPatternStr.length(), aiDocumentUrlString.lastIndexOf("/")) : aiDocumentUrlString.substring(aiDocumentUrlString.lastIndexOf(publicPatternStr) + publicPatternStr.length(), aiDocumentUrlString.lastIndexOf("/"));

		XmlPage page2 = null;
		Document doc = null;
		URL aiDocumentPropertiesWebScriptURL;
		try {
			aiDocumentPropertiesWebScriptURL = new URL(aiDocumentPropertiesWebScriptPrefix + aiDocumentPropertiesWebScriptString);
			WebRequestSettings req = new WebRequestSettings(aiDocumentPropertiesWebScriptURL, SubmitMethod.GET);
			req = new WebRequestSettings(aiDocumentPropertiesWebScriptURL, SubmitMethod.GET);
			page2 = (XmlPage) webClient.getPage(req);
			// System.out.println(page2.getContent());

			InputSource inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(page2.getContent()));
			// Test file
			// File file = new File("c:\\amr510012009.xml");
			// Parse asset_all webscript xml for search keywords
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			// Document doc = db.parse(file);
			doc = db.parse(inStream);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/*
	 * parses ASSET_ALL webscript xml output and sets aiDocumentProperties we
	 * can test against
	 */
	private void getaiDocumentProperties(String aiDocumentUrlString) {
		this.language = aiDocumentUrlString.substring(aiDocumentUrlString.lastIndexOf("/") + 1);
		Document doc = getaiDocument(aiDocumentUrlString);
		doc.getDocumentElement().normalize();
		// get Asset node elements
		NodeList assetNodeList = doc.getElementsByTagName("Asset");
		Element assetElement = (Element) assetNodeList.item(0);
		// get Index node
		NodeList indexNodeList = assetElement.getElementsByTagName("index");
		if (indexNodeList != null && indexNodeList.getLength() > 0) {
			Element indexElement = (Element) indexNodeList.item(0);
			this.index = indexElement.getFirstChild().getNodeValue();
		}
		// get securityClass node
		NodeList securityClassNodeList = assetElement.getElementsByTagName("securityClass");
		if (securityClassNodeList != null && securityClassNodeList.getLength() > 0) {
			Element securityClassElement = (Element) securityClassNodeList.item(0);
			this.securityClass = securityClassElement.getFirstChild().getNodeValue();
		}
		// get publishDate node
		NodeList publishDateNodeList = assetElement.getElementsByTagName("publishDate");
		if (publishDateNodeList != null && publishDateNodeList.getLength() > 0) {
			Element typeElement = (Element) publishDateNodeList.item(0);
			Date publishDate = new Date();
			try {
				publishDate = sourceDateFormatter.parse(typeElement.getFirstChild().getNodeValue());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.publishDateStr = targetDateFormatter.format(publishDate);
		}
		// get categories node
		this.categoryArray = new ArrayList<String>();
		NodeList categoriesNodeList = assetElement.getElementsByTagName("categories");
		if (categoriesNodeList != null && categoriesNodeList.getLength() > 0) {
			Element categoriesElement = (Element) categoriesNodeList.item(0);

			if (categoriesNodeList != null && categoriesNodeList.getLength() > 0) {
				NodeList categoryNodeList = categoriesElement.getElementsByTagName("category");
				if (categoryNodeList != null && categoryNodeList.getLength() > 0) {
					for (int s = 0; s < categoryNodeList.getLength(); s++) {
						Element categoryElement = (Element) categoryNodeList.item(s);
						this.categoryArray.add(categoryElement.getFirstChild().getNodeValue());
					}
				}
			}
		}
		// get secCategories node
		this.secCategoryArray = new ArrayList<String>();
		NodeList secCategoriesNodeList = assetElement.getElementsByTagName("secCategories");
		if (secCategoriesNodeList != null && secCategoriesNodeList.getLength() > 0) {
			Element categoriesElement = (Element) secCategoriesNodeList.item(0);

			if (categoriesNodeList != null && categoriesNodeList.getLength() > 0) {
				NodeList categoryNodeList = categoriesElement.getElementsByTagName("category");
				if (categoryNodeList != null && categoryNodeList.getLength() > 0) {
					for (int s = 0; s < categoryNodeList.getLength(); s++) {
						Element categoryElement = (Element) categoryNodeList.item(s);
						this.secCategoryArray.add(categoryElement.getFirstChild().getNodeValue());
					}
				}
			}
		}
		// get rendition node
		NodeList renditionsNodeList = assetElement.getElementsByTagName("renditions");
		if (renditionsNodeList != null && renditionsNodeList.getLength() > 0) {

			NodeList renditionNodeList = assetElement.getElementsByTagName("rendition");
			if (renditionNodeList != null && renditionNodeList.getLength() > 0) {
				for (int s = 0; s < renditionNodeList.getLength(); s++) {
					this.title = "";
					this.description = "";
					Element renditionElement = (Element) renditionNodeList.item(s);
					// filter by language
					if (renditionElement.getAttribute("language").equals(this.language)) {
						// System.out.println("language:" +
						// renditionNodeList.getLength() + language);
						NodeList masterDocumentNodeList = renditionElement.getElementsByTagName("masterDocument");
						Element masterDocumentElement = (Element) masterDocumentNodeList.item(0);
						NodeList documentNodeList = masterDocumentElement.getElementsByTagName("document");

						if (documentNodeList != null && documentNodeList.getLength() > 0) {
							Element documentElement = (Element) documentNodeList.item(0);
							NodeList titleNodeList = documentElement.getElementsByTagName("title");
							if (titleNodeList != null && titleNodeList.getLength() > 0) {
								Element titleElement = (Element) titleNodeList.item(0);
								if (titleElement.hasChildNodes()) {
									String titleStr = titleElement.getFirstChild().getNodeValue();
									// fudge title text
									this.titlefull = titleStr;
									this.title = (titleStr.length() >= 50) ? titleStr.substring(0, 50) : titleStr;	
								}
							}
							NodeList descriptionNodeList = documentElement.getElementsByTagName("description");
							if (descriptionNodeList != null && descriptionNodeList.getLength() > 0) {
								Element descriptionElement = (Element) descriptionNodeList.item(0);
								if (descriptionElement.hasChildNodes())
									this.description = descriptionElement.getFirstChild().getNodeValue();
							}
							NodeList typeNodeList = documentElement.getElementsByTagName("type");
							if (typeNodeList != null && typeNodeList.getLength() > 0) {
								Element typeElement = (Element) typeNodeList.item(0);
								if (typeElement.hasChildNodes()) {
									String typeStr = typeElement.getFirstChild().getNodeValue();
									// fudge for UrgentActions text
									this.type = (typeStr.contains("UrgentAction")) ? "Urgent Action" : typeStr;
								}
							}
						}
						break;
					}
				}
			}
		}
	}

	/*
	 * return list of aiDocuments from ASSET_FEEDS webscript
	 */
	private ArrayList<String> getaiDocumentUrlArray() throws Exception {
		ArrayList<String> aiDocumentUrlArray = new ArrayList<String>();

		// Initialise urls of webscripts and Search site
		URL aiDocumentWebscriptURL = new URL(aiDocumentWebscriptURLStr);

		// Get list of test sample document urls
		WebRequestSettings req = new WebRequestSettings(aiDocumentWebscriptURL, SubmitMethod.GET);
		TextPage page = (TextPage) webClient.getPage(req);
		int retry = 0;

		if (page.getWebResponse().getStatusCode() == 401) {
			retry++;
			if (retry == 2) {
				throw new Exception("Invalid Server Credentials");
			}
		} else if (page.getWebResponse().getStatusCode() == 503) {
			throw new Exception("Service Temporarily Unavailable");
		} else if (page.getWebResponse().getStatusCode() == 200) {

			String aiDocumentUrls = page.getContent();
			StringTokenizer st = new StringTokenizer(aiDocumentUrls);
			while (st.hasMoreTokens()) {
				aiDocumentUrlArray.add(st.nextToken());
			}
		}
		return aiDocumentUrlArray;
	}

	/*
	 * sets language checkboxes in Amnesty Search form
	 */
	private void setSearchFormLanguage(String language) {
		if (language.equals("en")) {
			selenium.click("edit-language-en");
			selenium.uncheck("edit-language-ar");
			selenium.uncheck("edit-language-fr");
			selenium.uncheck("edit-language-es");
			selenium.uncheck("edit-language-**ALL**");
		} else if (language.equals("ar")) {
			selenium.click("edit-language-ar");
			selenium.uncheck("edit-language-en");
			selenium.uncheck("edit-language-fr");
			selenium.uncheck("edit-language-es");
			selenium.uncheck("edit-language-**ALL**");
		} else if (language.equals("fr")) {
			selenium.click("edit-language-fr");
			selenium.uncheck("edit-language-ar");
			selenium.uncheck("edit-language-en");
			selenium.uncheck("edit-language-es");
			selenium.uncheck("edit-language-**ALL**");
		} else if (language.equals("es")) {
			selenium.click("edit-language-es");
			selenium.uncheck("edit-language-ar");
			selenium.uncheck("edit-language-fr");
			selenium.uncheck("edit-language-en");
			selenium.uncheck("edit-language-**ALL**");
		} else {
			selenium.click("edit-language-**ALL**");
			selenium.uncheck("edit-language-ar");
			selenium.uncheck("edit-language-fr");
			selenium.uncheck("edit-language-en");
			selenium.uncheck("edit-language-es");
		}
	}

	/*
	 * sets language checkboxes in Amnesty Search form
	 */
	private void setSearchFormDocumentType(String type) {
		if (type.toUpperCase().contains(("URGENT ACTION"))) {
			selenium.click("edit-document-types-urgent-actions");			
			selenium.uncheck("edit-document-types-reports");
			selenium.uncheck("edit-document-types-press-materials");
			selenium.uncheck("edit-document-types-audio-video");
			selenium.uncheck("edit-document-types-other");
			this.searchFormType = "&document_types[urgent_actions]=urgent_actions";
		} else if (type.toUpperCase().contains("REPORT")) {
			selenium.click("edit-document-types-reports");			
			selenium.uncheck("edit-document-types-urgent-actions");
			selenium.uncheck("edit-document-types-press-materials");
			selenium.uncheck("edit-document-types-audio-video");
			selenium.uncheck("edit-document-types-other");
			this.searchFormType = "&document_types[reports]=reports";			
		} else if (type.equalsIgnoreCase("Document")) {
			selenium.click("edit-document-types-other");			
			selenium.uncheck("edit-document-types-press-materials");			
			selenium.uncheck("edit-document-types-urgent-actions");
			selenium.uncheck("edit-document-types-reports");
			selenium.uncheck("edit-document-types-audio-video");
			this.searchFormType = "&document_types[other]=other";			
		} else if (type.toUpperCase().contains("PRESS RELEASE")) {
			selenium.click("edit-document-types-press-materials");			
			selenium.uncheck("edit-document-types-urgent-actions");
			selenium.uncheck("edit-document-types-reports");
			selenium.uncheck("edit-document-types-audio-video");
			selenium.uncheck("edit-document-types-other");
			this.searchFormType = "&document_types[press_materials]=press_materials";			
		} else {
			selenium.click("edit-document-types-other");			
			selenium.uncheck("edit-document-types-press-materials");			
			selenium.uncheck("edit-document-types-urgent-actions");
			selenium.uncheck("edit-document-types-reports");
			selenium.uncheck("edit-document-types-audio-video");
			this.searchFormType = "&document_types[other]=other";			
		}
	}	
	
	/*
	 * TESTS:-
	 */

	/*
	 * search form inputs: AIINDEX, LANGUAGE
	 */
	public void testSearchByIndex() throws InterruptedException, UnsupportedEncodingException {
		for (String aiDocumentUrlString : aiDocumentUrlArray) {
			getaiDocumentProperties(aiDocumentUrlString);
			StringBuilder result = new StringBuilder();
			// use properties value to run test searches
/*			selenium.type("edit-ai-index", index);
			selenium.type("edit-title", "");
			setSearchFormLanguage(language);
			setSearchFormDocumentType(type);
			selenium.click("edit-submit");*/
			String searchFormPostUrl = "http://www.amnesty.org/en/ai_search?keywords=&show_advanced=true&title=&ai_index="+URLEncoder.encode(index,"UTF-8")+"&sort=date&start_date[date]=&end_date[date]=&language["+language+"]="+language + searchFormType + "&form_build_id=form-31a7e3b066dfe3fafb0f958f2d4f71f2&form_id=amnestysearch_filters_form&op=Search";
			selenium.open(searchFormPostUrl);
			selenium.waitForPageToLoad("25000");

			if (securityClass.equalsIgnoreCase("Internal")) {
				result.append((!selenium.isTextPresent(title) && !selenium.isTextPresent(index)) ? "" : "Restricted document found! [" + index + "] [" + title + "] ");
			} else {
				if (selenium.isTextPresent("No results were found")) {
					result.append("Document missing - No results were found");
				} else if ((!selenium.isTextPresent(title) && !language.equals("ar")) || !selenium.isTextPresent(index) || !selenium.isTextPresent(type)) {
					result.append((selenium.isTextPresent(title)) ? "" : (language.equals("ar")) ? "Arabic document" : "Title missing [" + title + "] ");
					result.append((selenium.isTextPresent(index)) ? "" : "Index missing [" + index + "] ");
					result.append((selenium.isTextPresent(type)) ? "" : " Type missing [" + type + "] ");
					result.append((description.length() > 0 && !language.equals("ar")) ? (selenium.isTextPresent(description.substring(0, Math.min(description.indexOf("."), 100)))) ? "" : "Descripton not present [" + description.substring(0, Math.min(description.indexOf("."), 100)) + "] " : "");
				}
			}

			if (result.length() == 0) {
				// System.out.println(title + ", " + index + ", " + type);
				System.out.println("testSearchByIndex: INPUT[index=" + index + "] EXPECTED[url=.." + aiDocumentUrlString.substring(23) + ", title=" + title.substring(0, 30) + ".., type=" + type + "] - PASS");
				passes++;
			} else {
				System.out.println("testSearchByIndex: INPUT[index=" + index + "] EXPECTED[url=.." + aiDocumentUrlString.substring(23) + ", title=" + title.substring(0, 30) + ".., type=" + type + "] - FAIL: " + result);
				failures++;
			}
		}
		System.out.println("** End of test: testSearchByIndex - Passes:" + passes + " Failures:" + failures + " **");
		assertFalse(failures > 0);
	}

	/*
	 * search form inputs: TITLE, LANGUAGE
	 */
	public void testSearchByTitle() throws InterruptedException {
		for (String aiDocumentUrlString : aiDocumentUrlArray) {
			getaiDocumentProperties(aiDocumentUrlString);
			StringBuilder result = new StringBuilder();
			// use properties value to run test searches
			selenium.type("edit-ai-index", "");
			selenium.type("edit-title", titlefull);
			setSearchFormLanguage(language);
			setSearchFormDocumentType(type);
			selenium.click("edit-submit");
			selenium.waitForPageToLoad("25000");

			if (securityClass.equalsIgnoreCase("Internal")) {
				result.append((!selenium.isTextPresent(title) && !selenium.isTextPresent(index)) ? "" : "Restricted document found! [" + index + "] [" + title + "] ");
			} else {
				if (selenium.isTextPresent("No results were found")) {
					result.append("Document missing - No results were found");
				} else if ((!selenium.isTextPresent(title) && !language.equals("ar")) || !selenium.isTextPresent(index) || !selenium.isTextPresent(type)) {
					result.append((selenium.isTextPresent(title)) ? "" : (language.equals("ar")) ? "Arabic document" : "Title missing [" + title + "] ");
					result.append((selenium.isTextPresent(index)) ? "" : "Index missing [" + index + "] ");
					result.append((selenium.isTextPresent(type)) ? "" : " Type missing [" + type + "] ");
					result.append((description.length() > 0 && !language.equals("ar")) ? (selenium.isTextPresent(description.substring(0, Math.min(description.indexOf("."), 100)))) ? "" : "Descripton not present [" + description.substring(0, Math.min(description.indexOf("."), 100)) + "] " : "");
				}
			}

			if (result.length() == 0) {
				// System.out.println(title + ", " + index + ", " + type);
				System.out.println("testSearchByTitle: INPUT[title=" + title.substring(0, 20) + "] EXPECTED[url=.." + aiDocumentUrlString.substring(23) + ", type=" + type + "] - PASS");
				passes++;
			} else {
				System.out.println("testSearchByTitle: INPUT[title=" + title.substring(0, 20) + "] EXPECTED[url=.." + aiDocumentUrlString.substring(23) + ", type=" + type + "] - FAIL: " + result);
				failures++;
			}
		}
		System.out.println("** End of test: testSearchByTitle - Passes:" + passes + " Failures:" + failures + " **");
		assertFalse(failures > 0);
	}

	/*
	 * search form inputs: CATEGORY, SECCATEGORY, PUBLISHDATE, LANGUAGE
	 */
	public void testSearchByCountryIssueDate() throws InterruptedException {
		for (String aiDocumentUrlString : aiDocumentUrlArray) {
			getaiDocumentProperties(aiDocumentUrlString);
			StringBuilder result = new StringBuilder();
			// use properties value to run test searches
			selenium.type("edit-ai-index", "");
			selenium.type("edit-title", "");
			selenium.click("edit-sort-date");
			for (String category : categoryArray) {
				try {
					selenium.addSelection("region", "label=" + category);
				} catch (SeleniumException e) {
					continue;
				}
			}
			for (String secCategory : secCategoryArray) {
				try {
					selenium.addSelection("issue", "label=" + secCategory);
				} catch (SeleniumException e) {
					continue;
				}
			}
			selenium.click("date-from-datepicker-popup-0");
			selenium.type("date-from-datepicker-popup-0", publishDateStr);
			selenium.click("date-to-datepicker-popup-0");
			selenium.type("date-to-datepicker-popup-0", publishDateStr);

			setSearchFormLanguage(language);
			setSearchFormDocumentType(type);
			selenium.click("edit-submit");
			selenium.waitForPageToLoad("25000");

			if (securityClass.equalsIgnoreCase("Internal")) {
				result.append((!selenium.isTextPresent(title) && !selenium.isTextPresent(index)) ? "" : "Restricted document found! [" + index + "] [" + title + "] ");
			} else {
				if (selenium.isTextPresent("No results were found")) {
					result.append("Document missing - No results were found");
				} else if ((!selenium.isTextPresent(title) && !language.equals("ar")) || !selenium.isTextPresent(index) || !selenium.isTextPresent(type)) {
					result.append((selenium.isTextPresent(title) ? "" : (language.equals("ar")) ? "Arabic document" : "Title missing [" + title + "] "));
					result.append((selenium.isTextPresent(index)) ? "" : "Index missing [" + index + "] ");
					result.append((selenium.isTextPresent(type)) ? "" : " Type missing [" + type + "] ");
					result.append((description.length() > 0 && !language.equals("ar")) ? (selenium.isTextPresent(description.substring(0, Math.min(description.indexOf("."), 100)))) ? "" : "Descripton not present [" + description.substring(0, Math.min(description.indexOf("."), 100)) + "] " : "");
				}
			}

			if (result.length() == 0) {
				System.out.println("testSearchByCountryIssueDate: INPUT[categoryArray=" + categoryArray + ", secCategory=" + secCategoryArray + ", publishdateStr=" + publishDateStr + "] EXPECTED[url=.." + aiDocumentUrlString.substring(23) + ", title=" + title.substring(0, 20) + ".., type=" + type + "] - PASS");
				passes++;
			} else {
				System.out.println("testSearchByCountryIssueDate: INPUT[categoryArray=" + categoryArray + ", secCategory=" + secCategoryArray + ", publishdateStr=" + publishDateStr + "] EXPECTED[url=.." + aiDocumentUrlString.substring(23) + ", title=" + title.substring(0, 20) + ".., type=" + type + "] - FAIL: " + result);
				failures++;
			}
		}
		System.out.println("** End of test: testSearchByCountryIssueDate - Passes:" + passes + " Failures:" + failures + " **");
		assertFalse(failures > 0);
	}

	public static Test suite() {
		return new TestSuite(GoogleSearchApplianceRegressionTest.class);
	}

	public static void main(String[] args) {
		try {
			junit.textui.TestRunner.run(suite());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
