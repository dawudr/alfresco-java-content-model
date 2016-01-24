package org.amnesty.aidoc.webscript;

import java.io.File;
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

import junit.framework.TestCase;

/**
 * Tests the create type web script
 * 
 * @author chatch
 */
public class CreateTypeWebScriptTest extends WebscriptTestBase {
    AidocServiceClientRemoteImpl aidoc = new AidocServiceClientRemoteImpl();

    public CreateTypeWebScriptTest() throws MalformedURLException {
        super(Constants.CREATE_TYPE_URI);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        aidoc.deleteYearFolder(Util.getCurrentYear());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // aidoc.deleteYearFolder(getCurrentYear());
    }

    public void testCreateTypeMissingMandatoryParam() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("edition",
                "Report Standard Edition"));
        requestParams.add(new NameValuePair("type", "Report"));
        req.setRequestParameters(requestParams);

        try {
            @SuppressWarnings("unused")
            Page page = webClient.getPage(req);
        } catch (FailingHttpStatusCodeException e) {
            assertEquals("Unexpected error code: " + e, 400, e.getStatusCode());
            WebResponse rsp = e.getResponse();
            final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>400</code>\r\n  <message>Missing mandatory parameter aiIndex</message>\r\n</response>";
            assertEquals(EXPECTED, rsp.getContentAsString().trim());
            assertEquals(400, rsp.getStatusCode());
            return;
        }

        fail("expected response code 400 not returned");
    }

    public void testCreateTypeIndexFolderNotExists() throws Exception {

        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", "AFR 19/015/2000"));
        requestParams.add(new NameValuePair("type", "Report"));
        requestParams.add(new NameValuePair("title", "Test Report"));
        requestParams.add(new NameValuePair("mimetype", "application/rtf"));
        requestParams.add(new NameValuePair("lang", "fr"));
        requestParams
                .add(new NameValuePair(
                        "contenturl",
                        "contenturl=store://2007/9/5/11/31/25e5fc33-5b9b-11dc-b48c-bb81272ce4f6.bin|mimetype=application-rtf"));
        req.setRequestParameters(requestParams);

        try {
            @SuppressWarnings("unused")
            Page page = webClient.getPage(req);
        } catch (FailingHttpStatusCodeException e) {
            assertEquals("Unexpected error code: " + e, 400, e.getStatusCode());
            WebResponse rsp = e.getResponse();
            final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>400</code>\r\n  <message>Asset does not exist. Call the createasset service first.</message>\r\n</response>";
            assertEquals(EXPECTED, rsp.getContentAsString().trim());
            assertEquals(400, rsp.getStatusCode());
            return;
        }

        fail("expected response code 400 not returned");
    }

    public void testCreateTypeInvalidType() throws Exception {

        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", "AFR 19/015/2000"));
        requestParams.add(new NameValuePair("type", "Notatype"));
        requestParams.add(new NameValuePair("title", "Test Report"));
        requestParams.add(new NameValuePair("mimetype", "application/rtf"));
        requestParams.add(new NameValuePair("lang", "fr"));
        requestParams
                .add(new NameValuePair(
                        "contenturl",
                        "contenturl=store://2007/9/5/11/31/25e5fc33-5b9b-11dc-b48c-bb81272ce4f6.bin|mimetype=application-rtf"));
        req.setRequestParameters(requestParams);

        try {
            @SuppressWarnings("unused")
            Page page = webClient.getPage(req);
        } catch (FailingHttpStatusCodeException e) {
            assertEquals("Unexpected error code: " + e, 400, e.getStatusCode());
            WebResponse rsp = e.getResponse();
            final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>400</code>\r\n  <message>Type 'Notatype' is invalid</message>\r\n</response>";
            assertEquals(EXPECTED, rsp.getContentAsString().trim());
            assertEquals(400, rsp.getStatusCode());
            return;
        }

        fail("expected response code 400 not returned");
    }

    /**
     * Create a simple report type with core metadata
     */
    public void testCreateTypeReport() throws Exception {
        /* first create asset */
        String aiIndex = aidoc.createAsset("AFR19", null, "test title", null,
                null, null, null, null, null);
        File file = new File("test/resources/createtypesimple.rtf");
        String contentUrl = aidoc.uploadFile(file);

        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams.add(new NameValuePair("edition", "Report Web Edition"));
        requestParams.add(new NameValuePair("type", "Report"));
        requestParams.add(new NameValuePair("title",
                "Test Report for createtype service"));
        requestParams.add(new NameValuePair("lang", "en"));
        requestParams.add(new NameValuePair("mimetype", "application/rtf"));
        requestParams
                .add(new NameValuePair("from", "2006-09-01T00:00:00.000Z"));
        requestParams.add(new NameValuePair("to", "2008-09-05T12:29:00.000Z"));
        requestParams.add(new NameValuePair("contenturl", contentUrl));

        requestParams.add(new NameValuePair("description",
                "Test Report for createtype service\n"
                        + "this is the description which should go over\n"
                        + "3 lines ... Contains double byte chars: ä¸­å›½çš„ç½‘é¡µ"));

        req.setRequestParameters(requestParams);
        req.setCharset("UTF-8");
        XmlPage page = (XmlPage) webClient.getPage(req);

        final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>200</code>\r\n  <message>SUCCESS</message>\r\n</response>";
        WebResponse rsp = page.getWebResponse();
        assertEquals(EXPECTED, rsp.getContentAsString().trim());
        assertEquals(200, rsp.getStatusCode());
    }

    /**
     * Create a simple pinksheet type with core metadata
     */
    public void testCreateTypePinksheet() throws Exception {
        /* first create asset */
        String aiIndex = aidoc.createAsset("AFR19", null, "test title", null,
                null, null, null, null, null);

        File file = new File("test/resources/createtypesimple.rtf");
        String contentUrl = aidoc.uploadFile(file);

        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams.add(new NameValuePair("edition", "Web Edition"));
        requestParams.add(new NameValuePair("type", "Pinksheet"));
        requestParams.add(new NameValuePair("title",
                "Test Report for createtype service"));
        requestParams.add(new NameValuePair("lang", "en"));
        requestParams.add(new NameValuePair("mimetype", "application/rtf"));
        requestParams
                .add(new NameValuePair("from", "2006-09-01T00:00:00.000Z"));
        requestParams.add(new NameValuePair("to", "2008-09-05T12:29:00.000Z"));
        requestParams.add(new NameValuePair("contenturl", contentUrl));
        requestParams.add(new NameValuePair("description",
                "pink sheet description"));

        req.setRequestParameters(requestParams);
        req.setCharset("UTF-8");
        XmlPage page = (XmlPage) webClient.getPage(req);

        final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>200</code>\r\n  <message>SUCCESS</message>\r\n</response>";
        WebResponse rsp = page.getWebResponse();
        assertEquals(EXPECTED, rsp.getContentAsString().trim());
        assertEquals(200, rsp.getStatusCode());
    }

    /**
     * Create a press release and upload an html doc for it
     */
    public void testCreateTypePressRelease() throws Exception {
        /* first create asset */
        String aiIndex = aidoc.createAsset(null, "PressRelease", "test title",
                null, null, null, null, null, null);

        File file = new File("test/resources/pressrelease.html");
        String contentUrl = aidoc.uploadFile(file);

        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams.add(new NameValuePair("type", "PressRelease"));
        requestParams.add(new NameValuePair("title",
                "Test Report for createtype service"));
        requestParams.add(new NameValuePair("lang", "en"));
        requestParams.add(new NameValuePair("mimetype", "text/html"));
        requestParams
                .add(new NameValuePair("from", "2006-09-01T00:00:00.000Z"));
        requestParams.add(new NameValuePair("contenturl", contentUrl));

        req.setRequestParameters(requestParams);
        req.setCharset("UTF-8");
        XmlPage page = (XmlPage) webClient.getPage(req);

        final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>200</code>\r\n  <message>SUCCESS</message>\r\n</response>";
        WebResponse rsp = page.getWebResponse();
        assertEquals(EXPECTED, rsp.getContentAsString().trim());
        assertEquals(200, rsp.getStatusCode());
    }

    /**
     * Tests case for AIDOC-273 where summary and report translations were not
     * seperated for types. So this test inserts an english cover and an english
     * report and each should 'make' a new translation. Previoulsly the second
     * call would fail because it tried to add another english translation to
     * one that already exists.
     */
    public void testCreateTypeReportAndCover() throws Exception {
        /* first create asset */
        String aiIndex = aidoc.createAsset("AFR19", null, "test title", null,
                null, null, null, null, null);

        /*
         * Add the report
         */
        File file = new File("test/resources/createtypesimple.rtf");
        String contentUrl = aidoc.uploadFile(file);

        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams.add(new NameValuePair("edition", "Web Edition"));
        requestParams.add(new NameValuePair("type", "Report"));
        requestParams.add(new NameValuePair("title",
                "Test Report for createtype service"));
        requestParams.add(new NameValuePair("lang", "en"));
        requestParams.add(new NameValuePair("mimetype", "application/rtf"));
        requestParams
                .add(new NameValuePair("from", "2006-09-01T00:00:00.000Z"));
        requestParams.add(new NameValuePair("contenturl", contentUrl));

        req.setRequestParameters(requestParams);
        req.setCharset("UTF-8");
        XmlPage page = (XmlPage) webClient.getPage(req);

        final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>200</code>\r\n  <message>SUCCESS</message>\r\n</response>";
        WebResponse rsp = page.getWebResponse();
        assertEquals(EXPECTED, rsp.getContentAsString().trim());
        assertEquals(200, rsp.getStatusCode());

        /*
         * Add the cover
         */
        requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams.add(new NameValuePair("edition", "Web Edition"));
        requestParams.add(new NameValuePair("type", "Cover"));
        requestParams.add(new NameValuePair("title",
                "Test Summary for createtype service"));
        requestParams.add(new NameValuePair("lang", "en"));
        requestParams.add(new NameValuePair("mimetype", "application/rtf"));
        requestParams
                .add(new NameValuePair("from", "2006-09-01T00:00:00.000Z"));
        requestParams.add(new NameValuePair("contenturl", contentUrl));

        req.setRequestParameters(requestParams);
        req.setCharset("UTF-8");
        page = (XmlPage) webClient.getPage(req);

        rsp = page.getWebResponse();
        assertEquals(EXPECTED, rsp.getContentAsString().trim());
        assertEquals(200, rsp.getStatusCode());

        /*
         * Add a cover translation
         */
        requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams.add(new NameValuePair("edition", "Web Edition"));
        requestParams.add(new NameValuePair("type", "Cover"));
        requestParams.add(new NameValuePair("title",
                "Test Summary for createtype service"));
        requestParams.add(new NameValuePair("lang", "ar"));
        requestParams.add(new NameValuePair("mimetype", "application/rtf"));
        requestParams
                .add(new NameValuePair("from", "2006-09-01T00:00:00.000Z"));
        requestParams.add(new NameValuePair("contenturl", contentUrl));

        req.setRequestParameters(requestParams);
        req.setCharset("UTF-8");
        page = (XmlPage) webClient.getPage(req);

        rsp = page.getWebResponse();
        assertEquals(EXPECTED, rsp.getContentAsString().trim());
        assertEquals(200, rsp.getStatusCode());
    }

    /**
     * Create type with formatted edition. Should create an empty formatted
     * edition folder.
     */
    public void testCreateTypeFormattedEdition() throws Exception {
        /* first create asset */
        String aiIndex = aidoc.createAsset(null, "PressRelease", "test title",
                null, null, null, null, null, null);

        File file = new File("test/resources/pressrelease.html");
        String contentUrl = aidoc.uploadFile(file);

        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams.add(new NameValuePair("edition", "Formatted Edition"));
        requestParams.add(new NameValuePair("type", "PressRelease"));
        requestParams.add(new NameValuePair("title",
                "Test Report for createtype service"));
        requestParams.add(new NameValuePair("lang", "en"));
        requestParams.add(new NameValuePair("mimetype", "text/html"));
        requestParams
                .add(new NameValuePair("from", "2006-09-01T00:00:00.000Z"));
        requestParams.add(new NameValuePair("contenturl", contentUrl));

        req.setRequestParameters(requestParams);
        req.setCharset("UTF-8");
        XmlPage page = (XmlPage) webClient.getPage(req);

        final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>200</code>\r\n  <message>SUCCESS</message>\r\n</response>";
        WebResponse rsp = page.getWebResponse();
        assertEquals(EXPECTED, rsp.getContentAsString().trim());
        assertEquals(200, rsp.getStatusCode());
    }
    
    
    /**
     * Create a simple report type with core metadata
     */
    public void testCreateTypeReportFeed() throws Exception {
        /* first create asset */
    	
    	String title = "USA: Judge orders Mohammed Jawad’s release from Guantánamo; administration still mulling trial";
    	String description = "On 30 July 2009, US District Court Judge Ellen Segal Huvelle ordered the release, “beginning on August 21, 2009”, of Afghan national Mohammed Jawad from the US Naval Base at Guantánamo Bay in Cuba, where he has been held since early 2003. Although Amnesty International is concerned by the three-week delay in the release of a detainee unlawfully held, it nevertheless welcomes the beginning of remedy for Mohammed Jawad. ";
    	
        String aiIndex = aidoc.createAsset("AMR51", "Report", title, "2009",
                "088", null, null, null, null);
        File file = new File("/amr510882009eng.htm");
        String contentUrl = aidoc.uploadFile(file);

        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams.add(new NameValuePair("edition", "Standard Edition"));
        requestParams.add(new NameValuePair("lang", "en"));
        requestParams.add(new NameValuePair("mimetype", "application/rtf"));
        requestParams
                .add(new NameValuePair("from", "2006-09-01T00:00:00.000Z"));
        requestParams.add(new NameValuePair("to", "2010-09-05T12:29:00.000Z"));
        requestParams.add(new NameValuePair("contenturl", contentUrl));

        requestParams.add(new NameValuePair("description",description);

        req.setRequestParameters(requestParams);
        req.setCharset("UTF-8");
        XmlPage page = (XmlPage) webClient.getPage(req);

        final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>200</code>\r\n  <message>SUCCESS</message>\r\n</response>";
        WebResponse rsp = page.getWebResponse();
        assertEquals(EXPECTED, rsp.getContentAsString().trim());
        assertEquals(200, rsp.getStatusCode());
    }    

}
