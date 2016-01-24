package org.amnesty.aidoc.service;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.amnesty.aidoc.Util;

public class AidocRestServiceClientTest extends TestCase {

    AidocRestServiceClientImpl aidoc = new AidocRestServiceClientImpl();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        aidoc.configureFromProperties();
        //aidoc.deleteYearFolder(Util.getCurrentYear());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // aidoc.deleteYearFolder(Util.getCurrentYear());
    }

    public void testEmptyTest(){
    	
    	
    }
    
    public void disabletestGetAssetMetadata() throws Exception {
        /*
         * Create an asset
         */
        final String TITLE = "test title";
        final String LANG = "ar";
        ServiceCallResult callResult = aidoc.createAsset("AFR19", null, TITLE, null, null,
                null, null, null, null, null);
        
        assertEquals(200, callResult.getHttpStatusCode());
        
        String aiIndex = callResult.getOutputDocument().getRootElement().getValue();
        
        File file = new File("src/test/resources/createtypesimple.rtf");
        String contentUrl = aidoc.uploadFile(file);

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("title", TITLE);
        properties.put("mimetype", "application/rtf");
        properties.put("lang", LANG);
        properties.put("from", "2007-01-01T00:00:00.000Z");
        properties.put("contenturl", contentUrl);

        callResult = aidoc.createType(aiIndex, "Web Edition", "Report", properties);
        
        assertEquals(200, callResult.getHttpStatusCode());

        /*
         * Get and check metadata
         */
        //AiIndex aiIndex = AiIndex.parse(aiIndexStr);
        //Document xmlDoc = aidoc.getAssetMetadata(aiIndex.getYear(), aiIndex
        //        .getAiClass(), aiIndex.getDocnum(), LANG);

        /*
         * TODO: add assets - having trouble with the XML parsers and need to
         * move on - download JDOM or similar for easy parsing
         */
        // System.out.println(xmlDoc.getElementsByTagName("latinTitle").getLength());
        // System.out.println(xmlDoc.getElementsByTagName("asset").getLength());
        // System.out.println(xmlDoc.getElementsByTagName("document").getLength());
        // System.out.println(xmlDoc.getElementsByTagName("latinTitle"));
        // assertEquals(TITLE, xmlDoc.getElementsByTagName("latinTitle").item(1)
        // .getNodeValue());
    }

    public void testCreateAsset() throws Exception {
    	
        Set<String> categories = new HashSet<String>();
        categories.add("Irrepressible.info");
        categories.add("USA");
        categories.add("Religious groups - Hindu");
        categories.add("Refugees, Displaced People And Migrants");

        //String aiIndex = 
        	
        	ServiceCallResult callResult = aidoc.createAsset("AFR19", null, "Test create asset",
                Integer.valueOf(Util.getCurrentYear()), 010, null,  null, "Public", (Set<String>) categories, null);

        assertNotNull(callResult);
        assertEquals(200, callResult.getHttpStatusCode());
        //AiIndex index = AiIndex.parse(aiIndex);
        //assertEquals(Util.getCurrentYear(), index.getYear());
        //assertEquals("010", index.getDocnum());
        //assertEquals("AFR19", index.getAiClass());
    }

    public void disabletestCreateType() throws Exception {
        // first create an asset
    	ServiceCallResult callResult = aidoc.createAsset("AFR19", null, "test title", null,
                null, null, null, null, null, null);
        File file = new File("src/test/resources/createtypesimple.rtf");
        String contentUrl = aidoc.uploadFile(file);

        assertEquals(200, callResult.getHttpStatusCode());
        
        String aiIndex = callResult.getOutputDocument().getRootElement().getValue();
        
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("title", "Test Report for createtype service");
        properties.put("mimetype", "application/rtf");
        properties.put("lang", "ar");
        properties.put("contenturl", contentUrl);

        callResult = aidoc.createType(aiIndex, "Web Edition", "Report", properties);
        
        assertEquals(200, callResult.getHttpStatusCode());
    }

    public void testUpdateAsset() throws Exception {
        /*
         * Create an asset
         */
        Set<String> categories = new HashSet<String>();
        categories.add("Irrepressible.info");
        ServiceCallResult callResult = aidoc.createAsset("AFR19", null, "test title", null,
                null, null, null, null, null, null);

        assertEquals(200, callResult.getHttpStatusCode());
        
        /*
         * Update simple
         */
        
        String aiIndex = callResult.getOutputDocument().getRootElement().getValue();
        
        categories = new HashSet<String>();
        categories.add("Religious groups - Hindu");
        categories.add("Refugees, Displaced People And Migrants");
        callResult = aidoc.updateAsset(aiIndex, "New title", categories);

        assertEquals(200, callResult.getHttpStatusCode());
        
        /*
         * Update full
         */
        categories = new HashSet<String>();
        categories.add("Religious groups - Hindu");
        categories.add("Refugees, Displaced People And Migrants");
        callResult = aidoc.updateAsset(aiIndex, "New title", null, null, null, null, null, categories, "true",
                "my validity notes", null);
        
        assertEquals(200, callResult.getHttpStatusCode());

    }
    
    public void testUpdateAssetType() throws Exception {

        /*
         * Update simple
         */
        
        String aiIndex = "ORG 10/001/2007";
        
 
        /*
        String aiIndex, String title,
        String securityClass, String aiIndexType, String originator, String network,
        String networkNumber, Set<String> categories, String invalidated,
        String validityNotes, Set<String> secondaryCategories
         */
        
        ServiceCallResult callResult = aidoc.updateAsset(aiIndex, "title", null, "Report", "iramosbi", "1", "2", null, null, null, null);


        
        assertEquals(200, callResult.getHttpStatusCode());

    }

    public void testUpdateType2() throws Exception {

    	String aiIndex = "EUR12/008/2010";
        
        /*
         * Update type with non standard edition
         */
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("description",
                "New description test");
        properties.put("edition", "Standard Edition");
        aidoc.updateType(aiIndex, "Report", null, "en", "application/msword", null,
                        properties);
    }
    
    public void testUpdateType() throws Exception {
        /*
         * Create type
         */
  
    	
    	ServiceCallResult callResult = aidoc.createAsset("AFR19", null, "testUpdateType", null,
                null, null, null, null, null, null);
    	
    	assertEquals(200, callResult.getHttpStatusCode());
    	
    	String aiIndex = callResult.getOutputDocument().getRootElement().getValue();
    	 
        File file = new File("src/test/resources/createtypesimple.rtf");
        String contentUrl = aidoc.uploadFile(file);

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("title", "Test Report for createtype service");
        properties.put("mimetype", "application/rtf");
        properties.put("lang", "en");
        properties.put("contenturl", contentUrl);
       
        callResult = aidoc.createType(aiIndex, "Web Edition", "Report", properties);

        assertEquals(200, callResult.getHttpStatusCode());
        
        /*
         * Update type with non standard edition
         */
        properties = new HashMap<String, String>();
        properties.put("description",
                "New description from update type service call");
        properties.put("to", "2008-09-05T12:29:00.000Z");
        properties.put("edition", "Web Edition");
        aidoc.updateType(aiIndex, "Report", null, "en", "application/rtf", null,
                        properties);
    }

    public void testGetTicket() throws Exception {
        String ticket = aidoc.getTicket();
        System.out.println(ticket);
        assertTrue(ticket.startsWith("TICKET_"));
    }

    public void testUploadFile() throws Exception {
        File file = new File("src/test/resources/createtypesimple.rtf");
        String contentUrl = aidoc.uploadFile(file);
        assertTrue(contentUrl.startsWith("contentUrl=store://"));

        file = new File("src/test/resources/pressrelease.html");
        contentUrl = aidoc.uploadFile(file);
        assertTrue(contentUrl.startsWith("contentUrl=store://"));
    }

    public void disabletestDeleteAiIndexFolder() throws Exception {
        aidoc.createAsset("AFR19", null, "test title", Integer.valueOf(Util.getCurrentYear()),
                004, null, null, null, null, null);
        //assertTrue(aidoc.deleteAiIndexFolder(Util.getCurrentYear(), "AFR19","004"));
    }

    public void disabletestDeleteYearFolder() throws Exception {
        aidoc.createAsset("AFR19", null, "test title", Integer.valueOf(Util.getCurrentYear()),
                004, null, null, null, null, null);
        //assertTrue(aidoc.deleteYearFolder(Util.getCurrentYear()));
    }

}
