package org.amnesty.aidoc;

import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.Util;

import junit.framework.TestCase;

public class UtilTest extends TestCase {

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
}
