package org.amnesty.aidoc.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AidocSearchQueryStringParser {

    private static final String[] TERM_TYPES = { "category", "cat", "sort", "lang",
            "date", "order", "aiclass" };

    private static final String DRUPAL_DATE_HACK = "date:(....-..-..T..:..:.. ..:../....-..-..T..:..:.. ..:..)";

    private static final String MATCH_WITHOUT_QUOTES_REGEX = "\\S+:([^\"][\\S]+)";

    private static final String MATCH_WITH_QUOTES_REGEX = "\\S+:\"([^\"]+)\"";

    //
    // Start of example Java code for matching and formatting
    // query strings that match the format of a unique
    // document identifier.
    //
    private static final String MATCH_AI_INDEX_REGEX = ".*([a-zA-Z]{3})\\s?(\\d{2}/\\d{3}/)(\\d{2,4}).*";

    public static boolean isDocumentIdQuery(String queryString) {
        Pattern pattern = Pattern.compile(MATCH_AI_INDEX_REGEX);
        Matcher matcher = pattern.matcher(queryString);
        return matcher.matches();
    }

    public static String formatIdQuery(String queryString) {
        StringBuffer formattedQuery = new StringBuffer();
        Pattern pattern = Pattern.compile(MATCH_AI_INDEX_REGEX);
        Matcher matcher = pattern.matcher(queryString);
        if (matcher.matches()) {
            formattedQuery.append(matcher.group(1).toUpperCase());
            formattedQuery.append(" ");
            formattedQuery.append(matcher.group(2));
            String year = matcher.group(3);
            if (year.length() == 2) {
                int y = Integer.valueOf(year);
                if (y > 49) {
                    formattedQuery.append("19");
                } else {
                    formattedQuery.append("20");
                }
            }
            formattedQuery.append(year);
        }
        return formattedQuery.toString();
    }

    //
    // End of example
    //

    /**
     * 
     * @param queryString
     * @return
     */
    public static Map<String, List<String>> parse(String queryString) {
        Map<String, List<String>> queryTerms = new HashMap<String, List<String>>();

        // *** BEGIN - Hack to handle invalid Drupal date syntax bug
        // date:1970-01-01T00:00:00 00:00/2002-12-10T00:00:00 00:00
        Pattern pattern = Pattern.compile(DRUPAL_DATE_HACK);
        Matcher matcher = pattern.matcher(queryString);
        while (matcher.find()) {
            String range = matcher.group(1);
            range = range.replace(" ", "-");
            List<String> dateTermValues = new ArrayList<String>();
            dateTermValues.add(range);
            queryTerms.put("date", dateTermValues);
        }
        queryString = matcher.replaceAll("");
        // *** END HACK

        // Get terms with special syntax name:value
        for (String termName : TERM_TYPES) {
            List<String> termValues = getTermValues(termName, queryString);
            if (termValues != null && termValues.size() > 0) {
                queryTerms.put(termName, termValues);
            }
        }

        // if date range present split it up and replace with separate to
        // and from values
        if (queryTerms.containsKey("date")) {

            Map<String, String> dateValues = getDateRangeValues(queryTerms.get("date").get(0));
            if (dateValues.get("to") != null) {
                List<String> toList = new ArrayList<String>();
                toList.add((String) dateValues.get("to"));
                queryTerms.put("to", toList);
            }
            if (dateValues.get("from") != null) {
                List<String> fromList = new ArrayList<String>();
                fromList.add((String) dateValues.get("from"));
                queryTerms.put("from", fromList);
            }
        }

        // Strip out above terms just processed
        for (String termName : TERM_TYPES) {

            String matchWithoutQuotesRegex = MATCH_WITHOUT_QUOTES_REGEX
                    .replaceFirst("\\\\S\\+", termName);
            queryString = queryString.replaceAll(matchWithoutQuotesRegex, "");

            String matchWithQuotesRegex = MATCH_WITH_QUOTES_REGEX.replaceFirst(
                    "\\\\S\\+", termName);
            queryString = queryString.replaceAll(matchWithQuotesRegex, "");
        }

        // Get any remaining keywords - split on space and trim each
        /*
         * String[] splitKeywords = queryString.split("\\s"); List<String>
         * keywords = new ArrayList<String>(); for (String keywordValue :
         * splitKeywords) { String trimmedKeyword = keywordValue.trim(); if
         * (trimmedKeyword.length() > 0) {
         * 
         * int catIndex = trimmedKeyword.indexOf("cat:"); if (catIndex == 0)
         * trimmedKeyword = trimmedKeyword.substring(4);
         * keywords.add(trimmedKeyword); } } if (keywords.size() > 0) {
         * queryTerms.put("keywords", keywords); }
         */

        List<String> keywords = new ArrayList<String>();
        if (queryString.trim().length() > 0) {
            keywords.add(queryString.trim());
            queryTerms.put("keywords", keywords);
        }

        return queryTerms;
    }

    /**
     * Looks in query string for matches of syntax like "cat:category" where
     * "cat" is the termName and category is the value.
     * 
     * If spaces are in the value it can be specified like cat:"category with
     * spaces".
     * 
     * @param termName
     *          Name to look for before the colon. Eg. cat in example above.
     * @param queryString
     * @return List of matching values or empty list if none found.
     */
    protected static List<String> getTermValues(String termName,
            String queryString) {
        List<String> termValues = new ArrayList<String>();

        // Matches without quotes
        String matchWithoutQuotesRegex = MATCH_WITHOUT_QUOTES_REGEX
                .replaceFirst("\\\\S\\+", termName);
        Pattern pattern = Pattern.compile(matchWithoutQuotesRegex);
        Matcher matcher = pattern.matcher(queryString);
        while (matcher.find()) {
            termValues.add(matcher.group(1));
        }

        // Matches with quotes
        String matchWithQuotesRegex = MATCH_WITH_QUOTES_REGEX.replaceFirst(
                "\\\\S\\+", termName);
        pattern = Pattern.compile(matchWithQuotesRegex);
        matcher = pattern.matcher(queryString);
        while (matcher.find()) {
            termValues.add(matcher.group(1));
        }

        return termValues;
    }

    /**
     * Parses the date range format which is 2 ISO8601 format dates separated by
     * a /. If / is left out and only one date appears it is assumed the date is
     * the from date. See examples below:
     * 
     * date:2007-01-31T00:00:00-05:00 - all content published on or after the
     * 31st of January 2007 at 00:00 UTC -5 hours
     * 
     * date:/2007-01-31T00:00:00-05:00 - all content published on or before the
     * 31st of January 2007 at 00:00 UTC -5 hours
     * 
     * date:2007-01-01T00:00:00-00:00/2007-01-31T00:00:00-00:00 - all content
     * published between 1st of January 00:00 UTC and the 31st of January 2007
     * 00:00 UTC
     * 
     * @param dateRange
     *          Date range string as specified above
     * @return dateValues map with keys 'to' and 'from' mapping to date string
     *         values
     */
    protected static Map<String, String> getDateRangeValues(String dateRange) {
        Map<String, String> dateValues = new HashMap<String, String>();

        String fromDateStr = null;
        String toDateStr = null;

        if (dateRange != null && dateRange.contains("/")) {
            String[] parts = dateRange.split("/");
            if (parts[0].trim().length() > 0) {
                fromDateStr = parts[0];
            }
            if (parts[1].trim().length() > 0) {
                toDateStr = parts[1];
            }
        } else {
            fromDateStr = dateRange;
        }

        dateValues.put("from", fromDateStr);
        dateValues.put("to", toDateStr);

        return dateValues;
    }

}
