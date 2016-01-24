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

/**
 * Tests the update asset web script
 * 
 * @author chatch
 */
public class UpdateAssetWebScriptTest extends WebscriptTestBase {
    AidocServiceClientRemoteImpl aidoc = new AidocServiceClientRemoteImpl();

    public UpdateAssetWebScriptTest() throws MalformedURLException {
        super(Constants.UPDATE_ASSET_SERVICE_URI);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        aidoc.deleteYearFolder(Util.getCurrentYear());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // aidoc.deleteYearFolder(Util.getCurrentYear());
    }

    /**
     * Test error handling when asset does not exist
     */
    public void testUpdateAssetAssetDoesNotExist() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", "AFR 19/099/2007"));
        requestParams.add(new NameValuePair("title", "Test update asset"));
        req.setRequestParameters(requestParams);

        boolean testPassed = false;
        try {
            @SuppressWarnings("unused")
            Page page = webClient.getPage(req);
        } catch (FailingHttpStatusCodeException e) {
            assertEquals(400, e.getStatusCode());
            WebResponse rsp = e.getResponse();
            final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>400</code>\r\n  <message>Asset does not exist.</message>\r\n</response>";
            assertEquals(EXPECTED, rsp.getContentAsString().trim());
            assertEquals(400, rsp.getStatusCode());
            testPassed = true;
        }
        if (testPassed == false) {
            fail("expected response code 400 not returned");
        }
    }

    /**
     * Test error handling when an invalid category is passed
     */
    public void testUpdateAssetInvalidCategory() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        String aiIndex = aidoc.createAsset("AFR19", null, "test title", null,
                null, null, null, null, null);

        /* One invalid category */
        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams.add(new NameValuePair("title", "Test update asset"));
        requestParams.add(new NameValuePair("category", "HATCH")); // invalid
        req.setRequestParameters(requestParams);

        Page page = webClient.getPage(req);
        WebResponse rsp = page.getWebResponse();
        assertEquals(200, rsp.getStatusCode());

        /* one invalid category, 2 valid categories */
        requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams.add(new NameValuePair("title", "Test update asset"));
        requestParams.add(new NameValuePair("category", "USA")); // valid
        requestParams.add(new NameValuePair("category", "WHATS THIS")); // invalid
        requestParams.add(new NameValuePair("category", "Armed Conflict")); // valid
        req.setRequestParameters(requestParams);

        page = webClient.getPage(req);
        rsp = page.getWebResponse();
        assertEquals(200, rsp.getStatusCode());
    }

    /**
     * Test error handling for invalid security class
     */
    public void testUpdateAssetInvalidSecurityClass() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);

        String aiIndex = aidoc.createAsset("AFR19", null, "test title", null,
                null, null, null, null, null);

        /* One invalid category */
        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams.add(new NameValuePair("title", "Test update asset"));
        requestParams.add(new NameValuePair("securityClass", "Munchip"));
        req.setRequestParameters(requestParams);

        boolean testPassed = false;
        try {
            @SuppressWarnings("unused")
            Page page = webClient.getPage(req);
        } catch (FailingHttpStatusCodeException e) {
            assertEquals(400, e.getStatusCode());
            WebResponse rsp = e.getResponse();
            final String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<response>\r\n  <code>400</code>\r\n  <message>Invalid security class Munchip. Must be one of [Public, Internal]</message>\r\n</response>";
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
    public void testUpdateAssetTitleAndCategory() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);
        String aiIndex = aidoc.createAsset("AFR19", null, "test title", null,
                null, null, null, null, null);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams
                .add(new NameValuePair("title", "Test update asset 中国的网页"));
        requestParams.add(new NameValuePair("category", "USA"));
        requestParams.add(new NameValuePair("publishDate",
                "2008-01-01T12:00:00.000Z"));
        requestParams.add(new NameValuePair("securityClass", "Public"));

        req.setCharset("UTF-8");
        req.setRequestParameters(requestParams);

        Page page = webClient.getPage(req);
        WebResponse rsp = page.getWebResponse();
        assertEquals(200, rsp.getStatusCode());
    }

    /**
     * Test update invalidated flag and notes
     */
    public void testUpdateAssetInvalidatedFlags() throws Exception {
        WebRequestSettings req = new WebRequestSettings(SERVICE_URL,
                SubmitMethod.POST);
        String aiIndex = aidoc.createAsset("AFR19", null, "test title", null,
                null, null, null, null, null);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams.add(new NameValuePair("invalidated", "true"));
        requestParams.add(new NameValuePair("validityNotes", "my notes"));

        req.setCharset("UTF-8");
        req.setRequestParameters(requestParams);

        Page page = webClient.getPage(req);
        WebResponse rsp = page.getWebResponse();
        assertEquals(200, rsp.getStatusCode());
    }

}
