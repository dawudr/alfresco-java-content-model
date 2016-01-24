package org.amnesty.aidoc.webscript;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.amnesty.aidoc.Constants;
import org.amnesty.aidoc.Util;
import org.amnesty.aidoc.service.AidocServiceClientRemoteImpl;
import org.apache.commons.httpclient.NameValuePair;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.SubmitMethod;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

/**
 * Tests the create asset web script
 * 
 * @author chatch
 */
public class CreateAssetWebScriptTest extends WebscriptTestBase {
    AidocServiceClientRemoteImpl aidoc = new AidocServiceClientRemoteImpl();

    String currentYear = null;

    public CreateAssetWebScriptTest() throws MalformedURLException {
        super(Constants.CREATE_ASSET_URI);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // cleanup - tests work on the current year
        currentYear = Util.getCurrentYear();
        aidoc.deleteYearFolder(currentYear);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // cleanup - tests work on the current year
        // aidoc.deleteYearFolder(currentYear);
    }
   
    /**
     * Creation when year and class folders don't exist yet.
     */

    public void testCreateAssetWhenYearAndClassFoldersNotExist()
            throws Exception {
        /*
         * Create 2007 folder and AFR01 folder. Check ok when no category posted
         * too.
         */
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);
        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("year", currentYear));
        requestParams.add(new NameValuePair("class", "AFR01"));
        requestParams.add(new NameValuePair("docnum", "002"));
        requestParams.add(new NameValuePair("title", "Test create asset"));
        
        // Also test withdrawn
        requestParams.add(new NameValuePair("withdrawn", "true"));

        req.setRequestParameters(requestParams);
        XmlPage page = (XmlPage) webClient.getPage(req);
        assertEquals(200, page.getWebResponse().getStatusCode());

        String expectedXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
                + "<AiIndex>AFR 01/002/"+currentYear+"</AiIndex>\r\n";
        assertEquals(expectedXml, page.getContent());

        /*
         * Create AFR02 folder when year already exists and create a category
         * from each classification group.
         */
        requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("year", currentYear));
        requestParams.add(new NameValuePair("class", "AFR01"));
        requestParams.add(new NameValuePair("docnum", "003"));
        requestParams.add(new NameValuePair("title", "Test create asset"));
        requestParams.add(new NameValuePair("category", "FORCED EVICTION"));
        requestParams.add(new NameValuePair("category", "Irrepressible.info"));
        requestParams.add(new NameValuePair("category", "USA"));
        requestParams.add(new NameValuePair("category",
                "Religious groups - Hindu"));
        requestParams.add(new NameValuePair("category",
                "Refugees, Displaced People And Migrants"));

        req.setRequestParameters(requestParams);
        page = (XmlPage) webClient.getPage(req);
        assertEquals(200, page.getWebResponse().getStatusCode());

        expectedXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
                + "<AiIndex>AFR 01/003/"+currentYear+"</AiIndex>\r\n";
        assertEquals(expectedXml, page.getContent());
    }

    /**
     * Creation when given full explicit ai index and it already exists.
     * Expected behaviour is to return success.
     */
    public void testCreateAssetAlreadyExists() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);
        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("year", currentYear));
        requestParams.add(new NameValuePair("class", "AFR01"));
        requestParams.add(new NameValuePair("docnum", "001"));
        requestParams.add(new NameValuePair("title", "Test create asset"));

        req.setRequestParameters(requestParams);
        XmlPage page = (XmlPage) webClient.getPage(req);
        assertEquals(200, page.getWebResponse().getStatusCode());

        String expectedXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
                + "<AiIndex>AFR 01/001/"+currentYear+"</AiIndex>\r\n";
        assertEquals(expectedXml, page.getContent());

        /*
         * Create again and expect the same result
         */
        page = (XmlPage) webClient.getPage(req);
        assertEquals(200, page.getWebResponse().getStatusCode());
        assertEquals(expectedXml, page.getContent());
    }

    /**
     * Test error handling when an invalid category is passed
     */
    public void testCreateAssetInvalidCategory() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        /* One invalid category */
        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("year", currentYear));
        requestParams.add(new NameValuePair("class", "AFR19"));
        requestParams.add(new NameValuePair("docnum", "015"));
        requestParams.add(new NameValuePair("title", "Test create asset"));
        requestParams.add(new NameValuePair("category", "HATCH")); // invalid
        req.setRequestParameters(requestParams);

        XmlPage page = (XmlPage)webClient.getPage(req);
        assertEquals(200, page.getWebResponse().getStatusCode());
        String expectedXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
                + "<AiIndex>AFR 19/015/"+currentYear+"</AiIndex>\r\n";
        assertEquals(expectedXml, page.getContent());

        /* one invalid category, 2 valid categories */
        requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("year", currentYear));
        requestParams.add(new NameValuePair("class", "AFR19"));
        requestParams.add(new NameValuePair("docnum", "016"));
        requestParams.add(new NameValuePair("title", "Test create asset"));
        requestParams.add(new NameValuePair("category", "USA")); // valid
        requestParams.add(new NameValuePair("category", "WHATS THIS")); // invalid
        requestParams.add(new NameValuePair("category", "Armed Conflict")); // valid
        req.setRequestParameters(requestParams);

        page = (XmlPage)webClient.getPage(req);
        assertEquals(200, page.getWebResponse().getStatusCode());
        expectedXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
                + "<AiIndex>AFR 19/016/"+currentYear+"</AiIndex>\r\n";
        assertEquals(expectedXml, page.getContent());
    }

    /**
     * Test error handling when no type or class given. At least one must be
     * passed
     */
    public void testCreateAssetNoTypeOrClass() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("title", "Test create asset"));
        req.setRequestParameters(requestParams);

        boolean testPassed = false;
        try {
            @SuppressWarnings("unused")
            Page page = webClient.getPage(req);
        } catch (FailingHttpStatusCodeException e) {
            assertEquals(400, e.getStatusCode());
            WebResponse rsp = e.getResponse();
            final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>400</code>\r\n  <message>Must provide one of the parameters type or class</message>\r\n</response>";
            assertEquals(EXPECTED, rsp.getContentAsString().trim());
            assertEquals(400, rsp.getStatusCode());
            testPassed = true;
        }
        if (testPassed == false) {
            fail("expected response code 400 not returned");
        }
    }

    /**
     * Test error handling invalid type
     */
    public void testCreateAssetInvalidType() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("title", "Test create asset"));
        requestParams.add(new NameValuePair("type", "NotaType"));
        req.setRequestParameters(requestParams);

        boolean testPassed = false;
        try {
            @SuppressWarnings("unused")
            Page page = webClient.getPage(req);
        } catch (FailingHttpStatusCodeException e) {
            assertEquals(400, e.getStatusCode());
            WebResponse rsp = e.getResponse();
            final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>400</code>\r\n  <message>Invalid type NotaType. Must be one of [MediaBriefing, MediaAdvisory, PressRelease]</message>\r\n</response>";
            assertEquals(EXPECTED, rsp.getContentAsString().trim());
            assertEquals(400, rsp.getStatusCode());
            testPassed = true;
        }
        if (testPassed == false) {
            fail("expected response code 400 not returned");
        }
    }

    /**
     * Test error handling invalid security class
     */
    public void testCreateAssetInvalidSecurityClass() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("title", "Test create asset"));
        requestParams.add(new NameValuePair("year", currentYear));
        requestParams.add(new NameValuePair("class", "AFR19"));
        requestParams.add(new NameValuePair("docnum", "016"));
        requestParams.add(new NameValuePair("securityClass", "Gatecrasher"));
        req.setRequestParameters(requestParams);

        boolean testPassed = false;
        try {
            @SuppressWarnings("unused")
            Page page = webClient.getPage(req);
        } catch (FailingHttpStatusCodeException e) {
            assertEquals(400, e.getStatusCode());
            WebResponse rsp = e.getResponse();
            final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>400</code>\r\n  <message>Invalid security class Gatecrasher. Must be one of [Public, Internal]</message>\r\n</response>";
            assertEquals(EXPECTED, rsp.getContentAsString().trim());
            assertEquals(400, rsp.getStatusCode());
            testPassed = true;
        }
        if (testPassed == false) {
            fail("expected response code 400 not returned");
        }
    }

    /**
     * Creation passing just a type and no ai index parts.
     */
    public void testCreateAssetByType() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);
        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("type", "PressRelease"));
        requestParams.add(new NameValuePair("title", "Test create asset"));

        req.setRequestParameters(requestParams);
        XmlPage page = (XmlPage) webClient.getPage(req);
        assertEquals(200, page.getWebResponse().getStatusCode());

        String expectedXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
                + "<AiIndex>PRE 01/001/" + Util.getCurrentYear()
                + "</AiIndex>\r\n";
        assertEquals(expectedXml, page.getContent());
    }

    /**
     * Creation passing just a class
     */
    public void testCreateAssetByClass() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);
        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("class", "AFR46"));
        requestParams.add(new NameValuePair("title", "Test create asset"));
        requestParams.add(new NameValuePair("publishDate",
                "2008-01-01T12:00:00.000Z"));

        req.setRequestParameters(requestParams);
        XmlPage page = (XmlPage) webClient.getPage(req);
        assertEquals(200, page.getWebResponse().getStatusCode());

        String expectedXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
                + "<AiIndex>AFR 46/001/" + Util.getCurrentYear()
                + "</AiIndex>\r\n";
        assertEquals(expectedXml, page.getContent());
    }

    /**
     * Creation with no doc num so next available number is generated
     */
    public void testCreateAssetGetNextDocNum() throws Exception {
        /*
         * Create when year/class doesn't exists - should create 001
         */
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);
        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("year", currentYear));
        requestParams.add(new NameValuePair("class", "AFR01"));
        requestParams
                .add(new NameValuePair("title", "Test create asset 中国的网页"));
        requestParams.add(new NameValuePair("category", "Irrepressible.info"));
        requestParams.add(new NameValuePair("category",
                "DETENTION WITHOUT TRIAL")); // test different case works too
        // (migrator needs this)

        req.setRequestParameters(requestParams);
        req.setCharset("UTF-8");
        XmlPage page = (XmlPage) webClient.getPage(req);
        assertEquals(200, page.getWebResponse().getStatusCode());

        String expectedXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
                + "<AiIndex>AFR 01/001/"+currentYear+"</AiIndex>\r\n";
        assertEquals(expectedXml, page.getContent());

        /*
         * Call for same class again should create 002
         */
        requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("year", currentYear));
        requestParams.add(new NameValuePair("class", "AFR01"));
        requestParams.add(new NameValuePair("title", "Test create asset"));
        requestParams.add(new NameValuePair("category", "Irrepressible.info"));

        req.setRequestParameters(requestParams);
        page = (XmlPage) webClient.getPage(req);
        assertEquals(200, page.getWebResponse().getStatusCode());

        expectedXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
                + "<AiIndex>AFR 01/002/"+currentYear+"</AiIndex>\r\n";
        assertEquals(expectedXml, page.getContent());
    }

}
