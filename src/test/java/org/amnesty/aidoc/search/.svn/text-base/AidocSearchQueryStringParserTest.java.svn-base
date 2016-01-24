package org.amnesty.aidoc.search;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class AidocSearchQueryStringParserTest extends TestCase {

    public void testGetDateRangeValues() {
        String dateRange;
        Map<String, String> dateValues;

        dateRange = "2006-01-01T00:00:00.000-05:00";
        dateValues = AidocSearchQueryStringParser.getDateRangeValues(dateRange);
        assertEquals("2006-01-01T00:00:00.000-05:00", dateValues.get("from"));
        assertEquals(null, dateValues.get("to"));

        String dateTo = "2006-01-31T00:00:00.000-05:00";
        dateRange = "/" + dateTo;
        dateValues = AidocSearchQueryStringParser.getDateRangeValues(dateRange);
        assertEquals(null, dateValues.get("from"));
        assertEquals("2006-01-31T00:00:00.000-05:00", dateValues.get("to"));

        String dateFrom = "2006-01-01T00:00:00.000-05:00";
        dateTo = "2006-01-31T00:00:00.000-05:00";
        dateRange = dateFrom + "/" + dateTo;
        dateValues = AidocSearchQueryStringParser.getDateRangeValues(dateRange);
        assertEquals("2006-01-01T00:00:00.000-05:00", dateValues.get("from"));
        assertEquals("2006-01-31T00:00:00.000-05:00", dateValues.get("to"));
    }

    public void testParseSimple() {
        Map<String, List<String>> qMap = AidocSearchQueryStringParser
                .parse("category:USA war on terror");
        assertEquals(2, qMap.keySet().size());

        List<String> catValues = qMap.get("category");
        assertEquals(1, catValues.size());
        assertEquals("USA", catValues.get(0));

        List<String> keywords = qMap.get("keywords");
        assertEquals(1, keywords.size());
        assertTrue(keywords.contains("war on terror"));
    }

    public void testParseAdvanced() {
        final String SEARCH_STRING = "torture sort:date china Africa: Summary of human rights concerns in sub-Saharan"
                + " category:USA lang:fr aiclass:nws21"
                + "date:2006-01-01T00:00:00 05:00/2006-02-01T00:00:00 05:00";

        Map<String, List<String>> qMap = AidocSearchQueryStringParser.parse(SEARCH_STRING);
        assertEquals(8, qMap.keySet().size());

        List<String> sortValues = qMap.get("sort");
        assertEquals(1, sortValues.size());
        assertEquals("date", sortValues.get(0));

        List<String> catValues = qMap.get("category");
        assertEquals(1, catValues.size());
        assertEquals("USA", catValues.get(0));

        List<String> langValues = qMap.get("lang");
        assertEquals(1, langValues.size());
        assertEquals("fr", langValues.get(0));
        
        List<String> aiclassValues = qMap.get("aiclass");
        assertEquals(1, aiclassValues.size());
        assertEquals("nws21", aiclassValues.get(0));

        List<String> keywords = qMap.get("keywords");
        assertEquals(1, keywords.size());
        assertTrue(keywords.contains("torture  china Africa: Summary of human rights concerns in sub-Saharan"));

        List<String> fromDates = qMap.get("from");
        assertEquals(1, fromDates.size());
        assertEquals("2006-01-01T00:00:00-05:00", fromDates.get(0));

        List<String> toDates = qMap.get("to");
        assertEquals(1, toDates.size());
        assertEquals("2006-02-01T00:00:00-05:00", toDates.get(0));
    }

    public void testGetTermValues() {
        List<String> result;

        result = AidocSearchQueryStringParser
                .getTermValues("sort", "sort:date");
        assertEquals(1, result.size());
        assertTrue(result.contains("date"));

        result = AidocSearchQueryStringParser.getTermValues("sort",
                " sort:date ");
        assertEquals(1, result.size());
        assertTrue(result.contains("date"));

        result = AidocSearchQueryStringParser.getTermValues("sort",
                "sort:\"date\"");
        assertEquals(1, result.size());
        assertTrue(result.contains("date"));

        result = AidocSearchQueryStringParser.getTermValues("cat", "cat:USA");
        assertEquals(1, result.size());
        assertTrue(result.contains("USA"));

        // 2 cats, one with spaces in quotes
        result = AidocSearchQueryStringParser.getTermValues("cat",
                "cat:USA cat:\"War on Terror\"");
        assertEquals(2, result.size());
        assertTrue(result.contains("USA"));
        assertTrue(result.contains("War on Terror"));

        // mixed term types - get just cat
        result = AidocSearchQueryStringParser.getTermValues("cat",
                "sort:date cat:USA");
        assertEquals(1, result.size());
        assertTrue(result.contains("USA"));
    }

}
