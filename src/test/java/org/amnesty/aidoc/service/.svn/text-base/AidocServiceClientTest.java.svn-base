package org.amnesty.aidoc.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.Util;
import org.w3c.dom.Document;

@Deprecated
public class AidocServiceClientTest extends TestCase {

    AidocServiceClientRemoteImpl aidoc = new AidocServiceClientRemoteImpl();

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

    public void testEmptyTest(){
    	
    	
    }
    
    public void disabletestGetAssetMetadata() throws Exception {
        /*
         * Create an asset
         */
        final String TITLE = "test title";
        final String LANG = "ar";
        String aiIndexStr = aidoc.createAsset("AFR19", null, TITLE, null, null,
                null, null, null, null);
        File file = new File("src/test/resources/createtypesimple.rtf");
        String contentUrl = aidoc.uploadFile(file);

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("title", TITLE);
        properties.put("mimetype", "application/rtf");
        properties.put("lang", LANG);
        properties.put("from", "2007-01-01T00:00:00.000Z");
        properties.put("contenturl", contentUrl);

        aidoc.createType(aiIndexStr, "Web Edition", "Report", properties);

        /*
         * Get and check metadata
         */
        AiIndex aiIndex = AiIndex.parse(aiIndexStr);
        Document xmlDoc = aidoc.getAssetMetadata(aiIndex.getYear(), aiIndex
                .getAiClass(), aiIndex.getDocnum(), LANG);

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
        List<String> categories = new ArrayList<String>();
        categories.add("Irrepressible.info");
        categories.add("USA");
        categories.add("Religious groups - Hindu");
        categories.add("Refugees, Displaced People And Migrants");

        String aiIndex = aidoc.createAsset("AFR19", null, "Test create asset",
                Util.getCurrentYear(), "010", null, "Public", categories, null);

        assertNotNull(aiIndex);

        AiIndex index = AiIndex.parse(aiIndex);
        assertEquals(Util.getCurrentYear(), index.getYear());
        assertEquals("010", index.getDocnum());
        assertEquals("AFR19", index.getAiClass());
    }

    public void disabletestCreateType() throws Exception {
        // first create an asset
        String aiIndex = aidoc.createAsset("AFR19", null, "test title", null,
                null, null, null, null, null);
        File file = new File("src/test/resources/createtypesimple.rtf");
        String contentUrl = aidoc.uploadFile(file);

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("title", "Test Report for createtype service");
        properties.put("mimetype", "application/rtf");
        properties.put("lang", "ar");
        properties.put("contenturl", contentUrl);

        aidoc.createType(aiIndex, "Web Edition", "Report", properties);
    }

    public void testUpdateAsset() throws Exception {
        /*
         * Create an asset
         */
        List<String> categories = new ArrayList<String>();
        categories.add("Irrepressible.info");
        String aiIndex = aidoc.createAsset("AFR19", null, "test title", null,
                null, null, null, null, null);
        assertNotNull(aiIndex);

        /*
         * Update simple
         */
        categories = new ArrayList<String>();
        categories.add("Religious groups - Hindu");
        categories.add("Refugees, Displaced People And Migrants");
        aidoc.updateAsset(aiIndex, "New title", categories);

        /*
         * Update full
         */
        categories = new ArrayList<String>();
        categories.add("Religious groups - Hindu");
        categories.add("Refugees, Displaced People And Migrants");
        aidoc.updateAsset(aiIndex, "New title", null, categories, "true",
                "my validity notes", null);

    }

    public void disabletestUpdateType() throws Exception {
        /*
         * Create type
         */
        String aiIndex = aidoc.createAsset("AFR19", null, "test title", null,
                null, null, null, null, null);
        File file = new File("src/test/resources/createtypesimple.rtf");
        String contentUrl = aidoc.uploadFile(file);

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("title", "Test Report for createtype service");
        properties.put("mimetype", "application/rtf");
        properties.put("lang", "en");
        properties.put("contenturl", contentUrl);

        aidoc.createType(aiIndex, "Web Edition", "Report", properties);

        /*
         * Update type with non standard edition
         */
        properties = new HashMap<String, String>();
        properties.put("description",
                "New description from update type service call");
        properties.put("to", "2008-09-05T12:29:00.000Z");
        properties.put("edition", "Web Edition");
        aidoc
                .updateType(aiIndex, "Report", "en", "application/rtf",
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

    public void testDeleteAiIndexFolder() throws Exception {
        aidoc.createAsset("AFR19", null, "test title", Util.getCurrentYear(),
                "004", null, null, null, null);
        assertTrue(aidoc.deleteAiIndexFolder(Util.getCurrentYear(), "AFR19",
                "004"));
    }

    public void testDeleteYearFolder() throws Exception {
        aidoc.createAsset("AFR19", null, "test title", Util.getCurrentYear(),
                "004", null, null, null, null);
        assertTrue(aidoc.deleteYearFolder(Util.getCurrentYear()));
    }

}
