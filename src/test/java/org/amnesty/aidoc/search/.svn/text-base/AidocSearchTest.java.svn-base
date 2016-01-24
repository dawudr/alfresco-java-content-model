package org.amnesty.aidoc.search;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.amnesty.aidoc.webscript.WebscriptTestBase;
import org.apache.commons.httpclient.NameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.gargoylesoftware.htmlunit.SubmitMethod;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

/**
 * Tests the search service /aidoc/search.atom
 *
 * @author chatch
 */
public class AidocSearchTest extends WebscriptTestBase {
    private static final String SERVICE_URI = "/service/aidoc/search.atom";

    public AidocSearchTest() throws MalformedURLException {
        super(SERVICE_URI);
    }

    public void tesSearchSimple() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.GET);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("q", "arms"));
        req.setRequestParameters(requestParams);

        XmlPage page = (XmlPage) webClient.getPage(req);
        assertEquals(200, page.getWebResponse().getStatusCode());

        Document doc = page.getXmlDocument();
        NodeList entries = doc.getElementsByTagName("entry");
        assertEquals(3, entries.getLength());

        assertEquals(1, doc.getElementsByTagName("feed").getLength());
    }

    public void tesSearchRestrictByLang() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.GET);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("q", "arms"));
        requestParams.add(new NameValuePair("l", "es"));
        req.setRequestParameters(requestParams);

        XmlPage page = (XmlPage) webClient.getPage(req);
        assertEquals(200, page.getWebResponse().getStatusCode());

        Document doc = page.getXmlDocument();
        NodeList entries = doc.getElementsByTagName("entry");
        assertEquals(1, entries.getLength());
        assertEquals(1, doc.getElementsByTagName("feed").getLength());
    }

    public void tesSearchRestrictByDateRange() throws Exception {
//        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
//                SubmitMethod.GET);
//
//        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
//        requestParams.add(new NameValuePair("q", "arms"));
//        requestParams.add(new NameValuePair("l", "es"));
//        req.setRequestParameters(requestParams);
//
//        XmlPage page = (XmlPage) webClient.getPage(req);
//        assertEquals(200, page.getWebResponse().getStatusCode());
//
//        Document doc = page.getXmlDocument();
//        NodeList entries = doc.getElementsByTagName("entry");
//        assertEquals(1, entries.getLength());
//        assertEquals(1, doc.getElementsByTagName("feed").getLength());
    }

    public void testSearchBadPageNumberSimple() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.GET);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("q", "arms"));
        requestParams.add(new NameValuePair("c", "10"));
        requestParams.add(new NameValuePair("p", "5")); // page doesn't exist
        req.setRequestParameters(requestParams);

        XmlPage page = (XmlPage) webClient.getPage(req);
        assertEquals(200, page.getWebResponse().getStatusCode());

        Document doc = page.getXmlDocument();
        NodeList entries = doc.getElementsByTagName("entry");
        assertEquals(0, entries.getLength());

        assertEquals(1, doc.getElementsByTagName("feed").getLength());
    }
}
