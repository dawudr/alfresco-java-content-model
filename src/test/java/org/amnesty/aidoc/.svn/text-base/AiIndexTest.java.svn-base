package org.amnesty.aidoc;

import junit.framework.TestCase;

public class AiIndexTest extends TestCase {

    public void testParseWhenInvalidFormat() {
        boolean caughtException = false;
        try {
            @SuppressWarnings("unused")
            AiIndex index = AiIndex.parse("AFR312/001/2006");
        } catch (IllegalArgumentException iae) {
            caughtException = true;
        } finally {
            assertTrue(caughtException);
        }

    }

    public void testParse() {
        AiIndex expectedIndex = new AiIndex("AFR12", "001", "2006");
        
        AiIndex index = AiIndex.parse("AFR 12/001/2006");
        assertTrue(expectedIndex.equals(index));
        
        index = AiIndex.parse("AFR12/001/2006");
        assertTrue(expectedIndex.equals(index));
    }

    public void testToString() {
        AiIndex index = new AiIndex("AFR12", "001", "2006");
        assertEquals("AFR 12/001/2006", index.toString());
    }

}
