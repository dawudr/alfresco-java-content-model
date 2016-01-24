package org.amnesty.aidoc.webscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import net.sf.gsaapi.GSAClient;
import net.sf.gsaapi.GSAQuery;
import net.sf.gsaapi.GSAResponse;
import net.sf.gsaapi.GSAResult;
import net.sf.gsaapi.GSAQuery.GSAQueryTerm;
import net.sf.gsaapi.constants.Access;
import net.sf.gsaapi.constants.Filter;
import net.sf.gsaapi.constants.OutputFormat;

import org.alfresco.util.GUID;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.aicoreConfig;
import org.amnesty.aidoc.search.IntranetSearchClientDelegate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author iramosbi
 *
 */

public class IntranetSearchWebScript extends BaseWebScript {

    /*
     * AI meta names
     * 
     */   
	public static final String DATE = "date";
	public static final String LANGUAGE = "language";
	public static final String AI_TITLE = "AI-title";
	public static final String AI_LANGUAGE = "AI-language";
	public static final String AI_INDEX = "AI-index";
	public static final String AI_DOCUMENT_YEAR= "AI-document-year";
	public static final String AI_CLASS = "AI-class";
	public static final String AI_SUBCLASS = "AI-subclass";
	public static final String AI_DOCUMENT_NO = "AI-document-no";
	public static final String AI_SECURITY_CLASS = "AI-security-class";
	public static final String AI_PUBLISHED = "AI-published";
	public static final String AI_PUBLICATION_STATUS = "AI-publication-status";
	public static final String AI_TYPE = "AI-type";
	public static final String AI_CATEGORY_REGION = "AI-category-region";
	public static final String AI_CATEGORY_PRIMARY_REGION = "AI-category-primary-region";
	public static final String AI_CATEGORY_SECONDARY_REGION = "AI-category-secondary-region";
	public static final String AI_CATEGORY_PRIMARY_KEYWORD = "AI-category-primary-keyword";
	public static final String AI_CATEGORY_SECONDARY_KEYWORD = "AI-category-secondary-keyword";
	public static final String AI_CATEGORY_ISSUE = "AI-category-issue";
	public static final String AI_CATEGORY_CAMPAIGN = "AI-category-campaign";
	public static final String AI_LAST_MODIFIED = "AI-last-modified";
	public static final String AI_CREATED = "AI-created";
	public static final String AI_CONTENT_PROBLEM = "AI-content-problem";

	/*
	 * default search parameters
	 */
	private static final String INPUT_ENCODING = "utf-8";	
	private static final String OUTPUT_ENCODING = "utf-8";
	private static final String SETTING_FRONTEND = "default_frontend";
	
	/*
	 * Form parameter names
	 */
	private static final String PARAM_QUERY = "q";
	
	private static final String PARAM_START_PAGE = "start";
	
	private static final String PARAM_LANGUAGE = "lr";
	
	private static final String PARAM_COUNTRY = "country";
	
	private static final String PARAM_ISSUE = "issue";
	
	private static final String PARAM_START_DATE = "start_date";
	
	private static final String PARAM_END_DATE = "end_date";
	
	private static final String PARAM_SORT = "sort";
	
	private static final String PARAM_AI_INDEX_CLASS = "aiIndexClass";
	
	private static final String PARAM_AI_INDEX_SUBCLASS = "aiIndexSubClass";
	
	private static final String PARAM_AI_INDEX_NUMBER = "aiIndexNumber";
	
	private static final String PARAM_AI_INDEX_YEAR = "aiIndexYear";
	
	private static final String PARAM_TYPE = "type";
	
	private static final String PARAM_SECURITY_CLASS = "securityClass";

	private static final String PARAM_CAMPAIGN = "campaign";
	
	private static final String PARAM_TITLE = "title";
	
	private static final String PARAM_PRIMARY_KEYWORD = "primaryKeyword";
	
	private static final String PARAM_SECONDARY_KEYWORD = "secondaryKeyword";
	
	private static final String PARAM_PRIMARY_COUNTRY = "primaryCountry";
	
	private static final String PARAM_SECONDARY_COUNTRY = "secondaryCountry";
	
	private static final String PARAM_SEARCH_TYPE = "searchType";
	
	
	/*
	 * Search page settings
	 */
	private static final String PAGE_TITLE = "AIDOC Intranet Search";
	
	private static final String BREAD_CRUMB = "search";
	
	
	public static final List<String> AiMetaDataList = new ArrayList<String>(){

		private static final long serialVersionUID = 1L;
	
		{
			add(DATE);
			add(LANGUAGE);
			add(AI_TITLE);
			add(AI_LANGUAGE);
			add(AI_INDEX);	
			add(AI_CLASS);
			add(AI_SUBCLASS);
			add(AI_DOCUMENT_NO);
			add(AI_DOCUMENT_YEAR);
			add(AI_SECURITY_CLASS);
			add(AI_PUBLISHED);
			add(AI_PUBLICATION_STATUS);
			add(AI_TYPE);
			add(AI_CATEGORY_REGION);
			add(AI_CATEGORY_PRIMARY_REGION);
			add(AI_CATEGORY_SECONDARY_REGION);
			add(AI_CATEGORY_PRIMARY_KEYWORD);
			add(AI_CATEGORY_SECONDARY_KEYWORD);
			add(AI_CATEGORY_ISSUE);
			add(AI_CATEGORY_CAMPAIGN);
			add(AI_LAST_MODIFIED);
			add(AI_CREATED);
			add(AI_CONTENT_PROBLEM);
		}
	};
	
	 private static final Log logger = LogFactory.getLog(IntranetSearchWebScript.class);
	 
	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, Object> executeAiImpl(WebScriptRequest req,
			Status status) {
		
    	Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
    	
    	model.put("title", PAGE_TITLE);
        model.put("breadcrumb",BREAD_CRUMB);
        
		String[] collections = null;
		
    	/*
         * Process input arguments
         */
		
		GSASearchResult searchResult = new GSASearchResult();
		
		GSAClient client = new GSAClient(aicoreConfig.GSA_PROTOCOL, aicoreConfig.GSA_HOSTNAME, 
				aicoreConfig.GSA_PORT, aicoreConfig.GSA_PATH);
		GSAQuery gsaQuery = new GSAQuery();
		GSAQueryTerm gsaQueryTerm = new GSAQueryTerm();
		Properties requieredMetaFields = new Properties();
		Properties partialMetaFields = new Properties();
		
		//Basic and advanced search
		String query = req.getParameter(PARAM_QUERY);
		logger.debug("[" + PARAM_QUERY + "]" + query);
		String formLanguage = req.getParameter(PARAM_LANGUAGE);
		logger.debug("[" + PARAM_LANGUAGE + "]" + formLanguage);
		String sort =  req.getParameter(PARAM_SORT);
		logger.debug("[" + PARAM_SORT + "]" + sort);
		String start_date = req.getParameter(PARAM_START_DATE);
		logger.debug("[" + PARAM_START_DATE + "]" + start_date);
		String end_date = req.getParameter(PARAM_END_DATE);
		logger.debug("[" + PARAM_END_DATE + "]" + end_date);
		String formType =  req.getParameter(PARAM_TYPE);
		logger.debug("[" + PARAM_TYPE + "]" + formType);
		String aiIndexClass = req.getParameter(PARAM_AI_INDEX_CLASS);
		logger.debug("[" + PARAM_AI_INDEX_CLASS + "]" + aiIndexClass);
		String aiIndexSubClass = req.getParameter(PARAM_AI_INDEX_SUBCLASS);
		logger.debug("[" + PARAM_AI_INDEX_SUBCLASS + "]" + aiIndexSubClass);
		String aiIndexNumber = req.getParameter(PARAM_AI_INDEX_NUMBER);
		logger.debug("[" + PARAM_AI_INDEX_NUMBER + "]" + aiIndexNumber);
		String aiIndexYear = req.getParameter(PARAM_AI_INDEX_YEAR);
		logger.debug("[" + PARAM_AI_INDEX_YEAR + "]" + aiIndexYear);
		String searchType = req.getParameter(PARAM_SEARCH_TYPE);
		logger.debug("[" + PARAM_SEARCH_TYPE + "]" + searchType);
		//Basic search
		String formCountry = req.getParameter(PARAM_COUNTRY);
		logger.debug("[" + PARAM_COUNTRY + "]" + formCountry);
		String formIssue =  req.getParameter(PARAM_ISSUE);
		logger.debug("[" + PARAM_ISSUE + "]" + formIssue);
		//Advanced search
		String formCampaign = null;
		String formSecurityClass = null;
		String formTitle = null;
		String[] formPrimaryKeyword = null;
		String[] formSecondaryKeyword = null;
		String[] formPrimaryCountry = null;
		String[] formSecondaryCountry = null;
		if(!StringUtils.isEmpty(searchType))
		{
			if(searchType.equals("advanced"))
			{
				formCampaign = req.getParameter(PARAM_CAMPAIGN);
				logger.debug("[" + PARAM_CAMPAIGN + "]" + formCampaign);
				formSecurityClass =  req.getParameter(PARAM_SECURITY_CLASS);
				logger.debug("[" + PARAM_SECURITY_CLASS + "]" + formSecurityClass);
				formTitle = req.getParameter(PARAM_TITLE);
				logger.debug("[" + PARAM_TITLE + "]" + formTitle);
				formPrimaryKeyword = req.getParameterValues(PARAM_PRIMARY_KEYWORD);
				logger.debug("[" + PARAM_PRIMARY_KEYWORD + "]" + formPrimaryKeyword);
				formSecondaryKeyword = req.getParameterValues(PARAM_SECONDARY_KEYWORD);
				logger.debug("[" + PARAM_SECONDARY_KEYWORD + "]" + formSecondaryKeyword);
				formPrimaryCountry = req.getParameterValues(PARAM_PRIMARY_COUNTRY);
				logger.debug("[" + PARAM_PRIMARY_COUNTRY + "]" + formPrimaryCountry);
				formSecondaryCountry = req.getParameterValues(PARAM_SECONDARY_COUNTRY);
				logger.debug("[" + PARAM_SECONDARY_COUNTRY + "]" + formSecondaryCountry);
				formCountry = null;
				formIssue = null;
			}
		}
		
		//Search by AiIndex
		String aiClass="";	
		String aiSubClass="";
		String aiDocnumber="";
		String aiYear="";			
		

			
		if(!StringUtils.isEmpty(aiIndexClass))
		{
		aiClass = aiIndexClass.toUpperCase();
		partialMetaFields.put(AI_CLASS, aiClass);
		}

		if(!StringUtils.isEmpty(aiIndexSubClass))
		{
			aiSubClass = StringUtils.leftPad(aiIndexSubClass, 2, '0');
			partialMetaFields.put(AI_SUBCLASS, aiSubClass);
		}

		
		if(!StringUtils.isEmpty(aiIndexNumber))
		{
		aiDocnumber = StringUtils.leftPad(aiIndexNumber, 3, '0');
		partialMetaFields.put(AI_DOCUMENT_NO, aiDocnumber);
		}
		
		if(!StringUtils.isEmpty(aiIndexYear))
		{
			if(aiIndexYear.length() == 2 && StringUtils.isNumeric(aiIndexYear))
			{
				if(Integer.parseInt(aiIndexYear)>70) 
					{aiYear=19+aiIndexYear;}
				else
					{aiYear=20+aiIndexYear;}
			}
			else {
				aiYear = aiIndexYear;
			}
			
			partialMetaFields.put(AI_DOCUMENT_YEAR, aiYear);
		}

		//Language filter
		if(!StringUtils.isEmpty(formLanguage))
		{
			gsaQuery.setLanguage(formLanguage);
		}
		
		
		//Basic country filter
		if(!StringUtils.isEmpty(formCountry))
		{
			requieredMetaFields.put(AI_CATEGORY_REGION, formCountry);
		}

		//Basic Issue filter
		if(!StringUtils.isEmpty(formIssue))
		{
			requieredMetaFields.put(AI_CATEGORY_ISSUE, formIssue);
			
		}
		
		//Type filter
		if(!StringUtils.isEmpty(formType))
		{
			requieredMetaFields.put(AI_TYPE, formType);
			
		}
		
		//Campaign filter
		if(!StringUtils.isEmpty(formCampaign))
		{
			requieredMetaFields.put(AI_CATEGORY_CAMPAIGN, formCampaign);
		}
		
		//Primary keyword filter
		if(formPrimaryKeyword!=null)
		{
			if(!ArrayUtils.contains(formPrimaryKeyword, StringUtils.EMPTY))
			{
				StringBuffer sb = new StringBuffer();
				sb.append(formPrimaryKeyword[0]);
			for(int x=1; x<formPrimaryKeyword.length; x++)
			{
				sb.append("."+AI_CATEGORY_PRIMARY_KEYWORD +":"+ formPrimaryKeyword[x]);
			
			}
			requieredMetaFields.put(AI_CATEGORY_PRIMARY_KEYWORD, sb.toString());
			}
		}
		
		//Secondary keyword filter
		if(formSecondaryKeyword!=null)
		{
			if(!ArrayUtils.contains(formSecondaryKeyword, StringUtils.EMPTY))
			{
				StringBuffer sb = new StringBuffer();
				sb.append(formSecondaryKeyword[0]);
			for(int x=0; x<formSecondaryKeyword.length; x++)
			{
				sb.append("."+AI_CATEGORY_SECONDARY_KEYWORD +":"+ formSecondaryKeyword[x]);
			}
				requieredMetaFields.put(AI_CATEGORY_SECONDARY_KEYWORD, sb.toString());
			}
		}
		
		//Primary country or region filter
		if(formPrimaryCountry!=null)
		{
			if(!ArrayUtils.contains(formPrimaryCountry, StringUtils.EMPTY))
			{
				StringBuffer sb = new StringBuffer();
				
				sb.append(formPrimaryCountry[0]);

				
			for(int x=0; x<formPrimaryCountry.length; x++)
			{			
				sb.append("."+AI_CATEGORY_PRIMARY_REGION +":"+ formPrimaryCountry[x]);
			}
			requieredMetaFields.put(AI_CATEGORY_PRIMARY_REGION, sb.toString());
			}
		}
		
		//Secondary country or region filter
		if(formSecondaryCountry!=null)
		{
			if(!ArrayUtils.contains(formSecondaryCountry, StringUtils.EMPTY))
			{
				StringBuffer sb = new StringBuffer();
				
				sb.append(formSecondaryCountry[0]);

			for(int x=0; x<formSecondaryCountry.length; x++)
			{
				sb.append("."+AI_CATEGORY_SECONDARY_REGION +":"+ formSecondaryCountry[x]);				
			}
			requieredMetaFields.put(AI_CATEGORY_SECONDARY_REGION, sb.toString());
			}
		}
		
		//Security class filter
		if(!StringUtils.isEmpty(formSecurityClass))
		{
			if(formSecurityClass.equals("Internal"))
			{
				collections = new String[1];
				
		        collections[0] = aicoreConfig.GSA_INTERNAL_COLLECTION;
			}
			else{
				collections = new String[2];
				
		        collections[0] = aicoreConfig.GSA_PUBLIC_COLLECTION;
		        collections[1] = aicoreConfig.GSA_INTERNAL_COLLECTION;
			}	
			
			requieredMetaFields.put(AI_SECURITY_CLASS, formSecurityClass);
			
		}
		else{
			collections = new String[2];
			
	        collections[0] = aicoreConfig.GSA_PUBLIC_COLLECTION;
	        collections[1] = aicoreConfig.GSA_INTERNAL_COLLECTION;
		}

		gsaQuery.setSiteCollections(collections);
		
		//Form parameters and results
		searchResult.setAiClass(aiClass);
		searchResult.setAiSubclass(aiSubClass);
		searchResult.setAiDocnum(aiDocnumber);
		searchResult.setAiYear(aiYear);
		searchResult.setLanguage(formLanguage);
		searchResult.setCountry(formCountry);
		searchResult.setIssue(formIssue);
		searchResult.setStartDate(start_date);
		searchResult.setEndDate(end_date);
		searchResult.setType(formType);
		searchResult.setSort(sort);
		searchResult.setCampaign(formCampaign);
		searchResult.setSecurityClass(formSecurityClass);
		searchResult.setPrimaryKeyword(formPrimaryKeyword);
		searchResult.setSecondaryKeyword(formSecondaryKeyword);
		searchResult.setPrimaryCountry(formPrimaryCountry);
		searchResult.setSecondaryCountry(formSecondaryCountry);
		searchResult.setSearchType(searchType);

		//Full text search
		if(query==null)
		{
			 searchResult.setKeyword("");
			 model.put("search", searchResult);
			 return model;
    	}
    	else{
			searchResult.setKeyword(StringUtils.replace(query, "\"", "&quot;"));
		}

		//Only in title search
		if(!StringUtils.isEmpty(formTitle))	
		{
			searchResult.setTitle(StringUtils.replace(formTitle, "\"", "&quot;"));
			
			query = "allintitle:" + formTitle;
		}

		//Date range filter
		if(StringUtils.isNotEmpty(start_date) || StringUtils.isNotEmpty(end_date))
		{
			StringBuffer dateQuery = new StringBuffer("inmeta:date:daterange:");
			if(StringUtils.isNotEmpty(start_date))dateQuery.append(start_date);
			dateQuery.append("..");
			if(StringUtils.isNotEmpty(end_date))dateQuery.append(end_date);

				if(StringUtils.isNotEmpty(query))
						{
					query= query +" "+dateQuery;
						}
				else{
					query=dateQuery.toString();
				}				
		}
			
    		
    		
			gsaQueryTerm.setQueryString(query);

		//Sort by date or relevance
		if(sort!=null && sort.equals("date"))
		{
			gsaQuery.setSortByDate(false, 'S');
		}
		else
		{
			gsaQuery.unsetSortByDate();
		}
		
		//Pagination filter
		Integer startIndex= null;
		
		String start =  req.getParameter(PARAM_START_PAGE);
		if(StringUtils.isNotEmpty(start) && StringUtils.isNumeric(start))
		{
			startIndex = Integer.valueOf(start);
		}
		
		if(startIndex!=null)
		{
			gsaQuery.setScrollAhead(startIndex);
		}
        
		

		
		
		
		
		
		
		//OR(True) AND (false)
		gsaQuery.setRequiredMetaFields(requieredMetaFields, false);
		gsaQuery.setPartialMetaFields(partialMetaFields, false);
		gsaQuery.setQueryTerm(gsaQueryTerm);
		
		//Query settings
		gsaQuery.setInputEncoding(INPUT_ENCODING);
		gsaQuery.setOutputEncoding(OUTPUT_ENCODING);
		gsaQuery.setAccess(Access.ALL);
		gsaQuery.setFrontend(SETTING_FRONTEND);
		gsaQuery.setOutputFormat(OutputFormat.XML_NO_DTD);
		gsaQuery.setFilter(Filter.NO_FILTER);
		gsaQuery.setMaxResults(aicoreConfig.GSA_RESULTS_PER_PAGE);
		
		String[] metaFields = new String[AiMetaDataList.size()];
		gsaQuery.setFetchMetaFields(AiMetaDataList.toArray(metaFields));

		
		//Basic Authentication		
		IntranetSearchClientDelegate delegate = new IntranetSearchClientDelegate();
		delegate.setUser(aicoreConfig.GSA_USER);
		delegate.setPassword(aicoreConfig.GSA_PASSWORD);
		client.setClientDelegate(delegate);
		
        try{
        GSAResponse gsaResponse = client.getGSAResponse(gsaQuery);

        searchResult.setResults(gsaResponse.getResults());
        searchResult.setNumResults(gsaResponse.getNumResults());
        searchResult.setGSAQuery(gsaQuery.getValue());
        searchResult.setQuery(req.getQueryString());
        searchResult.setPages(buildPagination(gsaResponse, searchResult));
        
        convertResultsUrls(gsaResponse.getResults());
        model.put("search", searchResult);
        }        
		catch (Exception e) {
			handleError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage(), e,
                    model, status);
		}
		return model;
    }
    
    @SuppressWarnings("unchecked")
	private void convertResultsUrls(List results)
    {
    	Iterator<GSAResult> resultsIt = results.iterator();
    	while(resultsIt.hasNext())
    	{
    		GSAResult result = resultsIt.next();
    		String aiIndexString = result.getMeta(AI_INDEX);
    		if(aiIndexString != null)
    		{		
			AiIndex aiIndex = AiIndex.parse(aiIndexString);
			String lang = result.getMeta(AI_LANGUAGE);
			StringBuffer resutUrl = new StringBuffer(
					aiIndex.getYear()+"/"+aiIndex.getAiClass()+"/"+aiIndex.getDocnum()+
					"/"+lang);
			result.setUrl(resutUrl.toString());
    		}

    	}
    }


    /**
     * Search Result
     *
     * @author iramosbi
     */
    public static class GSASearchResult {
        private String id;

        private String searchTerms;

        private Locale locale;

        private String startPage;
        
        private Long numResults;

		private String query;
		
		private String gSAQuery;

		private String keyword;
        
        private String language;

        private String country;
        
        private String issue;
        
        private String startDate;
        
        private String endDate;
        
        private String type;
        
        private String sort;
        
        private AiIndex aiIndex;
        
        private String AiClass;
        
        private String AiSubclass;
        
        private String AiDocnum;
        
        private String AiYear;
        
        private String securityClass;
        
		private String title;
        
        private String campaign;
        
        private String[] primaryCountry;
        
        private String[] secondaryCountry;
        
        private String[] primaryKeyword;
        
        private String[] secondaryKeyword;

        private Map<String, String>pages;
        
        private String searchType;

		private List<GSAResult> results;
        
        public Long getNumResults() {
			return numResults;
		}

		void setNumResults(Long numResults) {
			this.numResults = numResults;
		}
		
        public String getTitle() {
			return title;
		}

		void setTitle(String title) {
			this.title = title;
		}

		public String getCampaign() {
			return campaign;
		}

		void setCampaign(String campaign) {
			this.campaign = campaign;
		}

		public String[] getPrimaryCountry() {
			return primaryCountry;
		}

		void setPrimaryCountry(String[] primaryCountry) {
			this.primaryCountry = primaryCountry;
		}

		public String[] getSecondaryCountry() {
			return secondaryCountry;
		}

		void setSecondaryCountry(String[] secondaryCountry) {
			this.secondaryCountry = secondaryCountry;
		}

		public String[] getPrimaryKeyword() {
			return primaryKeyword;
		}

		void setPrimaryKeyword(String[] primaryKeyword) {
			this.primaryKeyword = primaryKeyword;
		}

		public String[] getSecondaryKeyword() {
			return secondaryKeyword;
		}

		void setSecondaryKeyword(String[] secondaryKeyword) {
			this.secondaryKeyword = secondaryKeyword;
		}     
        
        public String getSecurityClass() {
			return securityClass;
		}

		void setSecurityClass(String securityClass) {
			this.securityClass = securityClass;
		}

		public String getAiClass() {
			return AiClass;
		}

		void setAiClass(String aiClass) {
			AiClass = aiClass;
		}

		public String getAiSubclass() {
			return AiSubclass;
		}

		void setAiSubclass(String aiSubclass) {
			AiSubclass = aiSubclass;
		}

		public String getAiDocnum() {
			return AiDocnum;
		}

		void setAiDocnum(String aiDocnum) {
			AiDocnum = aiDocnum;
		}

		public String getAiYear() {
			return AiYear;
		}

		void setAiYear(String aiYear) {
			AiYear = aiYear;
		}

		public AiIndex getAiIndex() {
			return aiIndex;
		}

		void setAiIndex(AiIndex aiIndex) {
			this.aiIndex = aiIndex;
		}

        public List<GSAResult> getResults() {
            return results;
        }

        void setResults(List<GSAResult> results) {
            this.results = results;
        }

		public String getQuery() {
			return query;
		}

		void setQuery(String query) {
			this.query = query;
		}
		
        public String getGSAQuery() {
			return gSAQuery;
		}

		void setGSAQuery(String gsaQuery) {
			this.gSAQuery = gsaQuery;
		}

		public String getKeyword() {
			return keyword;
		}

		void setKeyword(String keyword) {
			this.keyword = keyword;
		}

		public String getLanguage() {
			return language;
		}

		void setLanguage(String language) {
			this.language = language;
		}

		public String getCountry() {
			return country;
		}

		void setCountry(String country) {
			this.country = country;
		}

		public String getIssue() {
			return issue;
		}

		void setIssue(String issue) {
			this.issue = issue;
		}

		public String getStartDate() {
			return startDate;
		}

		void setStartDate(String startDate) {
			this.startDate = startDate;
		}
		
		public String getEndDate() {
			return endDate;
		}

		void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		public String getType() {
			return type;
		}

		void setType(String type) {
			this.type = type;
		}

		public String getSort() {
			return sort;
		}

		void setSort(String sort) {
			this.sort = sort;
		}

        public String getStartPage() {
            return startPage;
        }

        void setStartPage(String startPage) {
            this.startPage = startPage;
        }


        public String getSearchTerms() {
            return searchTerms;
        }

        void setSearchTerms(String searchTerms) {
            this.searchTerms = searchTerms;
        }
        
        
        public Map<String, String> getPages() {
			return pages;
		}

		void setPages(Map<String, String> pages) {
			this.pages = pages;
		}

		public String getSearchType() {
			return searchType;
		}

		void setSearchType(String searchType) {
			this.searchType = searchType;
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

        void setLocale(Locale locale) {
            this.locale = locale;
        }

        public String getId() {
            if (id == null) {
                id = GUID.generate();
            }
            return id;
        }
    }

    /**
     * 
     * @param gsaResponse
     * @param searchResult
     * @return Pagination <text><value> Map
     */
    private Map<String, String> buildPagination(GSAResponse gsaResponse, GSASearchResult searchResult)
    {
    	int numPages=10;
    	String query = searchResult.getQuery();
    	double actualPage = Math.floor((double) (((int)gsaResponse.getStartIndex())/aicoreConfig.GSA_RESULTS_PER_PAGE))+1;
    	searchResult.setStartPage(String.valueOf((int)actualPage));
    	String filteredQuery = null;
    	if(StringUtils.contains(query, "&start="))
    	{
    		filteredQuery = StringUtils.remove(query, "&start="+((long)gsaResponse.getStartIndex()-1));
    	}
    	else if(StringUtils.contains(query, "start="))
    	{
    		filteredQuery = StringUtils.remove(query, "start="+((long)gsaResponse.getStartIndex()-1)+"&");	
    	}
    	else{
    		filteredQuery = query;
    	}
    	Map<String, String> pagesMap = new LinkedHashMap<String, String>();
    	long previousValue = gsaResponse.getStartIndex()-aicoreConfig.GSA_RESULTS_PER_PAGE-1;
    	long nextValue = gsaResponse.getEndIndex();
    	if(nextValue>aicoreConfig.GSA_RESULTS_PER_PAGE)
    	{
    		if(previousValue<0) {previousValue=0;}
    	pagesMap.put("First", filteredQuery+"&start=0");
    	pagesMap.put("Prev", filteredQuery+"&start="+previousValue);
    	}
    	
    	double avgPage = Math.floor(aicoreConfig.GSA_RESULTS_PER_PAGE/2);
    	long startPage=0;
    	if(actualPage-avgPage>0)
    	{
    		startPage = Math.round(actualPage-avgPage);
    	}
    	for(long x=startPage; x<startPage+numPages; x++)
    	{
    		long nextStartIndex = x*aicoreConfig.GSA_RESULTS_PER_PAGE;
    		if(nextStartIndex<Math.min(gsaResponse.getNumResults(), GSAQuery.MAX_RESULTS))
    		pagesMap.put(String.valueOf(x+1), filteredQuery+"&start="+nextStartIndex);
    	}
    	if(nextValue<Math.min(gsaResponse.getNumResults(), GSAQuery.MAX_RESULTS))
    	{
    	pagesMap.put("Next", filteredQuery+"&start="+nextValue);
    	pagesMap.put("Last", filteredQuery+"&start="+(Math.min(gsaResponse.getNumResults(), GSAQuery.MAX_RESULTS)-aicoreConfig.GSA_RESULTS_PER_PAGE));
    	}	
			return pagesMap;
    }
}