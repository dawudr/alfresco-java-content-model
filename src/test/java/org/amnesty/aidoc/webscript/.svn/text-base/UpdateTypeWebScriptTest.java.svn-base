package org.amnesty.aidoc.webscript;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amnesty.aidoc.Constants;
import org.amnesty.aidoc.Util;
import org.amnesty.aidoc.service.AidocServiceClientRemoteImpl;
import org.apache.commons.httpclient.NameValuePair;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.SubmitMethod;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.WebResponse;

/**
 * Tests the update asset type web script
 * 
 * @author chatch
 */
public class UpdateTypeWebScriptTest extends WebscriptTestBase {
    AidocServiceClientRemoteImpl aidoc = new AidocServiceClientRemoteImpl();

    public UpdateTypeWebScriptTest() throws MalformedURLException {
        super(Constants.UPDATE_TYPE_SERVICE_URI);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        aidoc.deleteYearFolder(Util.getCurrentYear());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // AiWebscriptClient.deleteYearFolder(WebscriptTestBase.getCurrentYear());
    }

    /**
     * Test mandatory parameter validation
     */
    public void testUpdateTypeMandatoryParamHandling() throws Exception {
        /*
         * 1. Missing 3 mandatory parameters
         */
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);
        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", "AFR 19/099/2007"));
        req.setRequestParameters(requestParams);

        boolean testPassed = false;
        try {
            @SuppressWarnings("unused")
            Page page = webClient.getPage(req);
        } catch (FailingHttpStatusCodeException e) {
            assertEquals(400, e.getStatusCode());
            WebResponse rsp = e.getResponse();
            final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>400</code>\r\n  <message>Missing mandatory parameter type is a mandatory parameter</message>\r\n</response>";
            assertEquals(EXPECTED, rsp.getContentAsString().trim());
            assertEquals(400, rsp.getStatusCode());
            testPassed = true;
        }
        if (testPassed == false) {
            fail("expected response code 400 not returned");
        }

        /*
         * 2. Missing 1 mandatory parameter
         */
        requestParams.add(new NameValuePair("type", "Report"));
        requestParams.add(new NameValuePair("lang", "en"));
        req.setRequestParameters(requestParams);

        testPassed = false;
        try {
            @SuppressWarnings("unused")
            Page page = webClient.getPage(req);
        } catch (FailingHttpStatusCodeException e) {
            assertEquals(400, e.getStatusCode());
            WebResponse rsp = e.getResponse();
            final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>400</code>\r\n  <message>Missing mandatory parameter mimetype is a mandatory parameter</message>\r\n</response>";
            assertEquals(EXPECTED, rsp.getContentAsString().trim());
            assertEquals(400, rsp.getStatusCode());
            testPassed = true;
        }
        if (testPassed == false) {
            fail("expected response code 400 not returned");
        }
    }

    /**
     * Test error handling when asset type doesn not exist
     */
    public void testUpdateTypeTypeDoesNotExist() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);
        /*
         * 1. no asset at all
         */
        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", "AFR 19/099/2007"));
        requestParams.add(new NameValuePair("type", "Report"));
        requestParams.add(new NameValuePair("lang", "en"));
        requestParams.add(new NameValuePair("mimetype", "application/rtf"));

        req.setRequestParameters(requestParams);

        boolean testPassed = false;
        try {
            @SuppressWarnings("unused")
            Page page = webClient.getPage(req);
        } catch (FailingHttpStatusCodeException e) {
            assertEquals(400, e.getStatusCode());
            WebResponse rsp = e.getResponse();
            final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>400</code>\r\n  <message>Asset type does not exist. Address [/2007/AFR19/099/Standard Edition/afr190992007en.rtf]</message>\r\n</response>";
            assertEquals(EXPECTED, rsp.getContentAsString().trim());
            assertEquals(400, rsp.getStatusCode());
            testPassed = true;
        }
        if (testPassed == false) {
            fail("expected response code 400 not returned");
        }

        /*
         * 2. asset exists but not the type
         */
        String aiIndex = aidoc.createAsset("AFR19", null, "test title", null,
                null, null, null, null, null);
        requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams.add(new NameValuePair("type", "Report"));
        requestParams.add(new NameValuePair("lang", "en"));
        requestParams.add(new NameValuePair("mimetype", "application/rtf"));
        req.setRequestParameters(requestParams);

        testPassed = false;
        try {
            @SuppressWarnings("unused")
            Page page = webClient.getPage(req);
        } catch (FailingHttpStatusCodeException e) {
            assertEquals(400, e.getStatusCode());
            WebResponse rsp = e.getResponse();
            final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>400</code>\r\n  <message>Asset type does not exist. Address [/2007/AFR19/001/Standard Edition/afr190012007en.rtf]</message>\r\n</response>";
            assertEquals(EXPECTED, rsp.getContentAsString().trim());
            assertEquals(400, rsp.getStatusCode());
            testPassed = true;
        }
        if (testPassed == false) {
            fail("expected response code 400 not returned");
        }
    }

    /**
     * Test successful update
     */
    public void testUpdateTypeDescriptionAndFrom() throws Exception {
        /*
         * First create a type
         */
        String aiIndex = aidoc.createAsset("AFR19", null, "test title", null,
                null, null, null, null, null);

        File file = new File("test/resources/createtypesimple.rtf");
        String contentUrl = aidoc.uploadFile(file);

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("title", "Test Report");
        properties.put("mimetype", "application/rtf");
        properties.put("lang", "en");
        properties.put("contenturl", contentUrl);
        aidoc.createType(aiIndex, null, "Report", properties);

        /*
         * Now call the update type service
         */
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams.add(new NameValuePair("type", "Report"));
        requestParams.add(new NameValuePair("lang", "en"));
        requestParams.add(new NameValuePair("mimetype", "application/rtf"));
        requestParams
                .add(new NameValuePair("description",
                        "Test update asset type description with double byte chars 中国的网页"));
        requestParams
                .add(new NameValuePair("from", "2008-09-05T12:29:00.000Z"));

        req.setCharset("UTF-8");
        req.setRequestParameters(requestParams);

        Page page = webClient.getPage(req);
        WebResponse rsp = page.getWebResponse();
        assertEquals(200, rsp.getStatusCode());
    }

}
