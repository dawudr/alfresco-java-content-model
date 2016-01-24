package org.amnesty.aidoc.webscript;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.amnesty.aidoc.Constants;
import org.amnesty.aidoc.service.AidocRestServiceClientImpl;
import org.amnesty.aidoc.service.ServiceCallResult;

public class UpdateRenditionWebScriptTest extends WebscriptTestBase {
    AidocRestServiceClientImpl aidoc = new AidocRestServiceClientImpl();

    public UpdateRenditionWebScriptTest() throws MalformedURLException {
        super(Constants.UPDATE_RENDITION_SERVICE_URI);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testUpdateRendition()
    {
    	String aiIndex = "AMR 51/001/2009";
    	String lang = "en";
    	Map <String, String> properties = new HashMap<String, String>();
    	properties.put("title", "Test update rendition title");
    	properties.put("description", "Test update rendition description");
        
        try {
			ServiceCallResult result = aidoc.updateRendition(aiIndex, lang, properties);
			
			assertEquals(200, result.getHttpStatusCode());
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		
    }
}
