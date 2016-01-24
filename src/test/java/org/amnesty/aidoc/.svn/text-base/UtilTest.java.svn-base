package org.amnesty.aidoc;


import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.alfresco.service.cmr.search.SearchService;
import org.amnesty.aidoc.service.AidocRestServiceClient;
import org.amnesty.aidoc.service.AidocRestServiceClientImpl;
import org.amnesty.aidoc.service.ServiceCallResult;

public class UtilTest extends TestCase {
	
	protected SearchService searchService;

    public void testEncodeAiXPath() throws Exception {
        assertEquals("/cm:_x0032_000", Util.encodeAiXPath("/2000"));
        assertEquals("/cm:some_x0020_spaces", Util
                .encodeAiXPath("/some spaces"));
        assertEquals(
                "/cm:_x0032_000/cm:AFR19/cm:_x0030_04/cm:Report_x0020_Web_x0020_Edition",
                Util.encodeAiXPath("/2000/AFR19/004/Report Web Edition"));
    }

    public void testBuildFilename() throws Exception {
        AiIndex index = AiIndex.parse("AFR 12/002/2004");
/*        assertEquals("afr120022004fr_cover.rtf", Util.buildFilename(index, "fr", "Cover", "application/rtf"));
        assertEquals("afr120022004fr.xhtml", Util.buildFilename(index, "fr", null, "application/xhtml+xml"));
        assertEquals("afr120022004en.doc", Util.buildFilename(index, "en", "", "application/msword"));
        assertEquals("afr120022004ar.html", Util.buildFilename(index, "ar", null, "text/html"));    
        assertEquals("afr120022004fr.rtf", Util.buildFilename(index, "fr", "Report", "application/rtf"));
*/    }
    
    public void testFindCategory() throws Exception {
    	
    	AidocRestServiceClient aidoc = new AidocRestServiceClientImpl();
    	aidoc.configureFromProperties();
        Set<String> categories = new HashSet<String>();
        categories.add("Irrepressible.info");
        categories.add("USA");
        categories.add("Religious groups - Hindu");
        categories.add("Refugees, Displaced People And Migrants");
      	
        ServiceCallResult serviceCallResult	= aidoc.createAsset("AFR19", null, "Test create asset",
                Integer.valueOf(Util.getCurrentYear()), 010, null, null, "Public", categories, categories);
        
        
    	assertTrue(serviceCallResult != null);   	
    	assertEquals(200, serviceCallResult.getHttpStatusCode()); 	
    }
}
