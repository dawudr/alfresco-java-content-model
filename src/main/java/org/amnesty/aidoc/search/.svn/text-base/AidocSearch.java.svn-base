/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * and Open Source Software ("FLOSS") applications as described in Alfresco's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.amnesty.aidoc.search;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.repo.template.TemplateNode;
import org.alfresco.repo.web.scripts.bean.KeywordSearch;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.ParameterCheck;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Aidoc keyword search as defined in the document
 * https://intranet.amnesty.org/imst/wiki/display/AIDOC/REST+API.
 */
public class AidocSearch extends KeywordSearch {

    private static final Log logger = LogFactory.getLog(AidocSearch.class);

    /*
     * Search uses the full content type in ASPECT and TYPE directives. The
     * shorter version cm:content is not yet supported.
     */
    private static final String CONTENT_TYPE = "http://www.alfresco.org/model/content/1.0";

    private static final String AICORE_CONTENT_TYPE = "http://www.amnesty.org/model/aicore/1.0";

    /*
     * (non-Javadoc)
     *
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest,
     *      org.alfresco.web.scripts.WebScriptResponse)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req,
            Status status) {
        /*
         * Process input arguments
         */
        String searchTerms = req.getParameter("q");
        ParameterCheck.mandatoryString("q", searchTerms);

        String startPageArg = req.getParameter("p");
        int startPage = 1;
        try {
            startPage = new Integer(startPageArg);
        } catch (NumberFormatException e) {
            // NOTE: use default startPage
        }

        String itemsPerPageArg = req.getParameter("c");
        int itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
        try {
            itemsPerPage = new Integer(itemsPerPageArg);
        } catch (NumberFormatException e) {
            // NOTE: use default itemsPerPage
        }

        String language = req.getParameter("lang");

        /*
         * Execute the search
         */
        SearchResult results;
        results = search(searchTerms, startPage, itemsPerPage, language, req);

        /*
         * Create model
         */
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        model.put("search", results);
        return model;
    }

    /**
     * Execute the search
     *
     * @param searchTerms
     * @param startPage
     *          Search page to start results on
     * @param itemsPerPage
     *          Number of items to display per search page
     * @param language
     *          Language to restrict search results by. If null search will
     *          return all results.
     * @return
     */
    private SearchResult search(String searchTerms, int startPage,
            int itemsPerPage, String language, WebScriptRequest req) {
        SearchResult searchResult = null;
        ResultSet results = null;

        logger.debug("invoking AidocSearch");

        try {

            Map<String, List<String>> queryMap = AidocSearchQueryStringParser.parse(searchTerms);
            StringBuffer query = new StringBuffer();

            /*
             * 1. First add the general restriction parameters so we query over
             * the right dataset
             */

            query.append("TYPE:\"{" + AICORE_CONTENT_TYPE
                    + "}Document\" AND \n");
            query.append("ASPECT:\"{" + CONTENT_TYPE + "}effectivity\" AND \n");
            query.append("ASPECT:\"{" + CONTENT_TYPE + "}mlDocument\" AND \n");

            // Exclude documents without an effective date

            // Warning! We probably have timezone problems at the moment
            // See AIDOC-138

            /* Set the date range if provided */
            String fromDate = "1900-01-01T00:00:00";
            if (queryMap.get("from") != null) {
                List<String> fromList = queryMap.get("from");
                fromDate = (String) fromList.get(0);
            }
            String toDate = ISO8601DateFormat.format(new Date());
            if (queryMap.get("to") != null) {
                List<String> toList = queryMap.get("to");
                toDate = (String) toList.get(0);
            }

            query
                    .append(" @cm\\:from:[" + fromDate + " TO " + toDate
                            + "] AND");

            /*
             * Process language from query. If it exists this overrides form
             * parameter
             */
            if (queryMap.containsKey("lang")) {
                List<String> langList = (List<String>) queryMap.get("lang");
                String lang = langList.get(0);
                if (lang != null && lang.length() > 0)
                    language = lang;
            }
            if (language != null && language.length() > 0) {
                language = language.replace("-", "_");
            }
            logger.debug("language: " + language);

            /* Process language */
            // Locale locale = I18NUtil.getLocale();
            Locale locale = null;
            if (language != null && language.length() > 0
                    && !language.equals("any")) {
                locale = new Locale(language);
                query.append(" @sys\\:locale:\"" + locale.getLanguage()
                        + "_\" AND");
            }

            /*
             * 2. Now add the user's parameters
             */

            if (AidocSearchQueryStringParser.isDocumentIdQuery(searchTerms)) {
                query = buildIndexQuery(query, searchTerms);
            } else {
                query = buildStandardQuery(query, queryMap, language);
            }

            if (query.toString().trim().endsWith("AND")) {
                query.delete(query.length() - 4, query.length());
            }

            /*
             * Create SearchParameters to go with Lucene search string
             */
            SearchParameters parameters = new SearchParameters();
            parameters.addStore(SEARCH_STORE);
            parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
            // Doesn't work yet. See AIDOC-466
            // parameters.setDefaultOperator(SearchParameters.AND);
            parameters.setQuery(query.toString());

            /* Add locale clause if locale is set */
            // if (locale != null) {
            // parameters.addLocale(locale);
            // }
            // Add sort clause if sort:date specified
            if (queryMap.containsKey("sort")) {
                List<String> sortValues = (List<String>) queryMap.get("sort");

                boolean asc = false;
                if (queryMap.containsKey("order")) {
                    List<String> orderValues = (List<String>) queryMap
                            .get("order");
                    if (orderValues.get(0).equals("asc"))
                        asc = true;
                }

                if (sortValues.get(0).equals("date")) {
                    logger.debug("sort by date");
                    if (asc) {
                        logger.debug("order descending");
                    } else
                        logger.debug("order ascending");
                    parameters.addSort("@"
                            + QName.createQName(CONTENT_TYPE, "from"), asc);
                }
            } else
                parameters.addSort("@"
                        + QName.createQName(CONTENT_TYPE, "from"), false);

            /*
             * Execute query
             */
            if (logger.isDebugEnabled()) {
                logger.debug("Search parameters: searchTerms=" + searchTerms
                        + ", startPage=" + startPage + ", itemsPerPage="
                        + itemsPerPage);
                logger.debug("Issuing lucene search: " + query);
            }

            results = searchService.query(parameters);
            int totalResults = results.length();

            logger.debug("Results: " + totalResults + " rows (limited: "
                    + results.getResultSetMetaData().getLimitedBy() + ")");

            int totalPages = (totalResults / itemsPerPage);
            totalPages += (totalResults % itemsPerPage != 0) ? 1 : 0;

            /* construct search result */
            searchResult = new SearchResult();
            searchResult.setSearchTerms(searchTerms);
            searchResult.setLocale(locale);
            searchResult.setItemsPerPage(itemsPerPage);
            searchResult.setStartPage(startPage);
            searchResult.setTotalResults(totalResults);
            if (totalResults == 0) {
                searchResult.setTotalPages(0);
                searchResult.setStartIndex(0);
                searchResult.setTotalPageItems(0);
            } else if (totalPages != 0
                    && (startPage < 1 || startPage > totalPages)) {
                /* handle out-of-range by displaying no results */
                searchResult.setTotalPages(totalPages);
                searchResult.setStartIndex(0);
                searchResult.setTotalPageItems(0);
            } else {
                searchResult.setTotalPages(totalPages);
                searchResult
                        .setStartIndex(((startPage - 1) * itemsPerPage) + 1);
                searchResult.setTotalPageItems(Math.min(itemsPerPage,
                        totalResults - searchResult.getStartIndex() + 1));
            }
            SearchTemplateNode[] nodes = new SearchTemplateNode[searchResult
                    .getTotalPageItems()];
            for (int i = 0; i < searchResult.getTotalPageItems(); i++) {
                NodeRef node = results.getNodeRef(i
                        + searchResult.getStartIndex() - 1);
                float score = results.getScore(i + searchResult.getStartIndex()
                        - 1);
                nodes[i] = new SearchTemplateNode(node, score);
            }
            searchResult.setResults(nodes);
            return searchResult;
        } catch (Exception e) {
            logger.error("Search failed", e);
            return null;
        } finally {
            if (results != null) {
                results.close();
            }
        }
    }

    private StringBuffer buildIndexQuery(StringBuffer query, String identifier) {

        String formatted = AidocSearchQueryStringParser
                .formatIdQuery(identifier);
        query.append(" @aicore\\:aiIndex:\"");
        query.append(formatted);
        query.append("\" AND");
        return query;
    }

    private StringBuffer buildStandardQuery(StringBuffer query, Map<String, List<String>> queryMap,
            String language) {

        // Our query template. Prioritize new content.
        String tpl = "("
                + "("
                + "(@cm\\:title:(terms)^8 OR  @cm\\:description:(terms)^4 OR TEXT:(terms))"
                + " AND @cm\\:from:[primRange]"
                + ") OR "
//                + "("
//                + "(@cm\\:title:(terms)^8 OR  @cm\\:description:(terms)^4 OR TEXT:(terms))"
//                + " AND @cm\\:from:[secRange]"
//                + ")^2 OR "
                + "(@cm\\:title:(terms)^8 OR  @cm\\:description:(terms)^4 OR TEXT:(terms))"
                + ")" + ")";

        Date from = new Date();
        from.setYear(from.getYear() - 2);
        String primRange = ISO8601DateFormat.format(from) + " TO "
                + ISO8601DateFormat.format(new Date());
        // from.setYear(from.getYear() - 4);
        // String secRange = ISO8601DateFormat.format(from) + " TO "
        // + ISO8601DateFormat.format(new Date());

        // Compile regular expression
        Pattern pattern = Pattern.compile("primRange");
        Matcher matcher = pattern.matcher(tpl);
        tpl = matcher.replaceAll(primRange);

        // pattern = Pattern.compile("secRange");
        // matcher = pattern.matcher(tpl);
        // tpl = matcher.replaceAll(secRange);

        /* Process categories */
        if (queryMap.containsKey("category")) {
            List<String> catValues = (List<String>) queryMap.get("category");
            for (String category : catValues) {
                logger.debug("Search category: " + category);
                category = category.replace(" ", "_x0020_");
                category = category.replace(",", "_x002c_");
                query.append(" PATH:\"/cm:generalclassifiable//cm:" + category
                        + "//*\" AND ");
            }
        }

        if (queryMap.containsKey("cat")) {
            List<String> catValues = (List<String>) queryMap.get("cat");
            for (String category : catValues) {

                if (CategoryToClassMap.hm.containsKey(category.toUpperCase())) {
                    String mappedClass = (String) CategoryToClassMap.hm
                            .get(category.toUpperCase());
                    logger.debug("Mapping " + category + " to class: "
                            + mappedClass);
                    query.append(" @aicore\\:aiIndex:\"");
                    query.append(mappedClass + "*");
                    query.append("\" AND");
                } else
                    logger.debug("No AiClass found for: " + category);
            }
        }

        /* Process keywords */
        if (queryMap.containsKey("keywords")) {
            List<String> keywords = (List<String>) queryMap.get("keywords");

            pattern = Pattern.compile("terms");
            matcher = pattern.matcher(tpl);

            if (keywords.size() == 1)
                tpl = matcher.replaceAll(keywords.get(0));
            else if (keywords.size() == 0)
                tpl = matcher.replaceAll("?");
            else
                tpl = matcher.replaceAll("bad query");

            query.append(tpl);

        }

        return query;
    }

    /**
     * Search Result
     *
     * @author davidc
     */
    public static class SearchResult {
        private String id;

        private String searchTerms;

        private Locale locale;

        private int itemsPerPage;

        private int totalPages;

        private int totalResults;

        private int totalPageItems;

        private int startPage;

        private int startIndex;

        private SearchTemplateNode[] results;

        public int getItemsPerPage() {
            return itemsPerPage;
        }

        /* package */void setItemsPerPage(int itemsPerPage) {
            this.itemsPerPage = itemsPerPage;
        }

        public TemplateNode[] getResults() {
            return results;
        }

        /* package */void setResults(SearchTemplateNode[] results) {
            this.results = results;
        }

        public int getStartIndex() {
            return startIndex;
        }

        /* package */void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public int getStartPage() {
            return startPage;
        }

        /* package */void setStartPage(int startPage) {
            this.startPage = startPage;
        }

        public int getTotalPageItems() {
            return totalPageItems;
        }

        /* package */void setTotalPageItems(int totalPageItems) {
            this.totalPageItems = totalPageItems;
        }

        public int getTotalPages() {
            return totalPages;
        }

        /* package */void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getTotalResults() {
            return totalResults;
        }

        /* package */void setTotalResults(int totalResults) {
            this.totalResults = totalResults;
        }

        public String getSearchTerms() {
            return searchTerms;
        }

        /* package */void setSearchTerms(String searchTerms) {
            this.searchTerms = searchTerms;
        }

        public Locale getLocale() {
            return locale;
        }

        /**
         * @return XML 1.0 Language Identification
         */
        public String getLocaleId() {
            return (locale != null) ? locale.toString().replace('_', '-') : "";
        }

        /* package */void setLocale(Locale locale) {
            this.locale = locale;
        }

        public String getId() {
            if (id == null) {
                id = GUID.generate();
            }
            return id;
        }
    }

}
