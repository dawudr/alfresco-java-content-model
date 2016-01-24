package org.amnesty.aidoc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.BaseNodeServiceTest;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class providing shared static methods.
 * 
 * @author chatch
 */
public class Util {

    private static Log logger = LogFactory.getLog(Util.class);

    private static ConcurrentHashMap<String, NodeRef> categoryCache = new ConcurrentHashMap<String, NodeRef>();

    private static Set<String> unfoundCategoryCache = Collections.synchronizedSet(new HashSet<String>());

    private static NodeRef indexedDocsFolderRef = null;

    private static ConcurrentHashMap<String, String> iso3Map = new ConcurrentHashMap<String, String>();
    
    private static final String KEYWORD_ISSUE_FILENAME = "keyword_issue.xml";
    
    private static final String KEYWORD_CORRECTIONS_FILENAME = "keyword_corrections.xml";
    
    private static Map<String, List<String>> keywordIssueMap = loadKeywordIssueMap();
    
    private static Map<String, String> keywordCorrectionsMap = loadKeywordCorrectionsMap();

    public static String CLASS_CODE_REGX = "([A-Z]{3})(\\s{0,1})([0-9]{1,2})";
	public static String DOCUMENT_NO_REGX = "[0-9]{1,3}";
	public static String DOCUMENT_YEAR_REGX = "[0-9]{1,4}";
	
	/**
     * build a static map from ISO 639-2 codes to ISO 639-1 codes
     */
    static
    {
        for ( String lang: Locale.getISOLanguages() )
        {
            Locale loc = new Locale( lang );
            iso3Map.put( loc.getISO3Language(), lang );            
        }
    }

    /**
     * Return a Locale for an ISO 639-1 or ISO 639-2 language code string, throwing an IllegalArgumentException if the string is invalid
     * 
     * ISO 639-2 codes without corresponding ISO 639-1 codes are NOT supported
     * 
     * @param lang an ISO 639-1 or ISO 639-2 language code string
     * @return a Locale object
     */
    public static Locale getLocale( String lang )
    {
        if ( lang == null )
        {
            throw new IllegalArgumentException( "Language code must not be null." );
        }
        
        lang = lang.toLowerCase();
        
        if ( lang.length() < 2 || lang.length() > 3 )
        {
            throw new IllegalArgumentException( "Language code must be 2 or 3 letter ISO-639 code." );
        }
        
        if ( lang.length() == 3 )
        {
            String iso2 = iso3Map.get( lang );
            
            if ( iso2 == null )
            {
                throw new IllegalArgumentException( "Unsupported ISO 639-2 code: " + lang + "." );
            }
            
            logger.info( "Switching language code from " + lang + " to " + iso2 );
            
            lang = iso2;
        }
        
        Locale locale = new Locale( lang );
        
        try
        {
            /* All valid language locales support getISO3language method
             * but Locale( String lang ) constructor allows invalid codes without an error
             */
        	
            locale.getISO3Language();
        }
        catch ( MissingResourceException e )
        {
            throw new IllegalArgumentException( "Unrecognised ISO 639-1 code: " + lang + "." );
        }
        
        return locale;
        
    }
    
    /**
     * Encodes each element of an Alfresco content XPath using ISO9075 and
     * prefixes the content namespace (cm).
     * 
     * This is required for AiDoc folders that start with numbers or contain
     * spaces.
     * 
     * @param xPath
     *            Path to encode (eg. /2000/AFR12/001)
     * @return encoded xpath (eg. /cm:_x0032_000/cm:AFR12/cm:_x0030_01)
     */
    
    @Deprecated
    public static String encodeAiXPath(String xPath) {
        // strip off '/' before splitting on '/'
        xPath = xPath.substring(1, xPath.length());

        StringBuffer encodedPath = new StringBuffer();
        for (String folder : xPath.split("/")) {
            encodedPath.append("/cm:" + ISO9075.encode(folder));
        }

        return encodedPath.toString();
    }

    /**
     * Find noderef for a path relative to Indexed Documents.
     * 
     * @param path
     *            Eg. '/2000/AFR46/001'
     * @return matching NodeRef or null if not found
     */
    
    @Deprecated
    public static NodeRef findNodeRelativeToIndexedDocs(String relativePath,
            SearchService searchService) {
        String encodedRelativePath = Util.encodeAiXPath(relativePath);
        return findNode(Constants.INDEXED_DOCS_XPATH + encodedRelativePath,
                searchService);
    }
    
    /**
	 * 
	 * @param nodeService
	 * @param fileFolderService
	 * @param documentYear
	 * @param classCode
	 * @param edition
	 * @param filename
	 * @return NodeRef for the path.
	 * @throws FileNotFoundException
	 */
    public static NodeRef resolveDocumentNode(NodeService nodeService, FileFolderService fileFolderService, 
    		String documentYear, String classCode, String documentNo, String edition, String filename)
    	throws FileNotFoundException{
    	//Get the Asset for the request index information
		  
		  NodeRef rootStoreRef = null;
		  NodeRef rootHomeRef = null;
	      NodeRef node = null;
	      
	      rootStoreRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		  QName qname = QName.createQName(NamespaceService.APP_MODEL_1_0_URI, "company_home");
	      List<ChildAssociationRef> assocRefs = nodeService.getChildAssocs(rootStoreRef, ContentModel.ASSOC_CHILDREN, qname);
	      
	      if(assocRefs.size()>0)
	      {
	    	  rootHomeRef = assocRefs.get(0).getChildRef();
	      }
	      
		List<String> path = new ArrayList<String>();

	      path.add("Asset Library");
	      path.add("Indexed Documents");
	      if(documentYear!=null)
	      {
	      path.add(documentYear);
		      if(classCode!=null)
		      {
		    	  path.add(classCode);
		    	  if(documentNo!=null)
		    	  {
		    		  path.add(documentNo);
		    		  
		    		  if(edition!= null)
		    		  {
		    			  path.add(edition); 
		    			  if(filename!= null)
		    			  {
		    				  path.add(filename); 
		    			  }
		    		  }
		    	  }
		      }
	      }

	      if(rootHomeRef!=null)
	      {
	    	  FileInfo aiIndexInfo = fileFolderService.resolveNamePath(rootHomeRef, path);
	    	  node = aiIndexInfo.getNodeRef();
	      }else
	      {
	    	 throw new FileNotFoundException("Path not found: "+path.toString());
	      }
	      return node;
    }
    
    
	/**
	 * 
	 * @param nodeService
	 * @param fileFolderService
	 * @return NodeRef for Indexed Documents folder.
	 * @throws FileNotFoundException
	 */
	public static NodeRef resolveIndexedDocsNode(NodeService nodeService,FileFolderService fileFolderService) throws FileNotFoundException
	{
		 return resolveDocumentNode(nodeService, fileFolderService, null, null, null, null, null);
	}
	/**
	 * 
	 * @param nodeService
	 * @param fileFolderService
	 * @param documentYear
	 * @param classCode
	 * @param documentNo
	 * @return NodeRef for the path.
	 * @throws FileNotFoundException
	 */
	public static NodeRef resolveAssetNode(NodeService nodeService,FileFolderService fileFolderService, String documentYear, String classCode, String documentNo) throws FileNotFoundException
	{
		 return resolveDocumentNode(nodeService, fileFolderService, documentYear, classCode, documentNo, null, null);
	}
	
	/**
	 * 
	 * @param nodeService
	 * @param fileFolderService
	 * @param documentYear
	 * @return NodeRef for the path.
	 * @throws FileNotFoundException
	 */
	public static NodeRef resolveYearNode(NodeService nodeService,FileFolderService fileFolderService, String documentYear) throws FileNotFoundException
	{
		 return resolveDocumentNode(nodeService, fileFolderService, documentYear, null, null, null, null);
	}
	
	/**
	 * 
	 * @param nodeService
	 * @param fileFolderService
	 * @param documentYear
	 * @param classCode
	 * @return NodeRef for the path.
	 * @throws FileNotFoundException
	 */
	public static NodeRef resolveClassCodeNode(NodeService nodeService,FileFolderService fileFolderService, String documentYear, String classCode) throws FileNotFoundException
	{
		 return resolveDocumentNode(nodeService, fileFolderService, documentYear, classCode, null, null, null);
	}

	/**
	 * 
	 * @param nodeService
	 * @param fileFolderService
	 * @param documentYear
	 * @param classCode
	 * @param documentNo
	 * @param edition
	 * @return NodeRef for the path.
	 * @throws FileNotFoundException
	 */
	public static NodeRef resolveEditionNode(NodeService nodeService,FileFolderService fileFolderService, String documentYear, String classCode, String documentNo, String edition) throws FileNotFoundException
	{
		 return resolveDocumentNode(nodeService, fileFolderService, documentYear, classCode, documentNo, edition, null);
	}
	
    /**
     * Find noderef for a path relative to Indexed Documents.
     * 
     * @param path
     *            Eg. '/2000/AFR46/001'
     * @return matching NodeRef or null if not found
     */
	
	@Deprecated
    public static NodeRef findNodeRelativeToIndexedDocs(
            List<String> pathElements, SearchService searchService,
            FileFolderService fileFolderService) {
        NodeRef nodeRef;
        NodeRef indexedDocsNodeRef = Util
                .getIndexedDocsFolderRef(searchService);
        try {
            FileInfo fileInfo = fileFolderService.resolveNamePath(
                    indexedDocsNodeRef, pathElements);
            nodeRef = fileInfo.getNodeRef();
        } catch (FileNotFoundException e) {
            nodeRef = null;
        }
        return nodeRef;
    }

    /**
     * Find noderef for a full encoded xpath.
     * 
     * @param path
     *            Must start with './' Eg.
     *            './app:company_home/app:dictionary/app:space_templates' and
     *            each element must be ISO9075 encoded where needed.
     * @return matching NodeRef or null if not found
     */
    public static NodeRef findNode(String path, SearchService searchService) {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.addStore(Constants.SEARCH_STORE);
        searchParameters.setLanguage(SearchService.LANGUAGE_XPATH);

        logger.debug("findNode - path: " + path);
        searchParameters.setQuery(path);

        ResultSet results = searchService.query(searchParameters);
        NodeRef nodeRef = (results.length() > 0) ? results.getNodeRef(0) : null;
        results.close();

        return nodeRef;
    }

    /**
     * Build filename for Asset Type, given details as passed to the assettype
     * service.
     * 
     * @return Returns filename in the standard format. Eg.
     *         afr120012002en_cover.rtf
     */
    public static String buildFilename(AiIndex index, String language,
            String type, String mimetype, MimetypeService mimetypeService ) {

    	if ( mimetypeService == null )
        {
        	throw new IllegalArgumentException( "mimetypeService is null" );            
        }

        StringBuffer filename = new StringBuffer();

        filename.append(index.getAiClass());
        filename.append(index.getDocnum());
        filename.append(index.getYear());
        filename.append(language);

        if ( (type != null) && ( Constants.AUXILLIARY_TYPES.contains(type) )) {
            filename.append("_" + type);
        }

        String fileExt = mimetypeService.getExtension( mimetype );
        
        if (fileExt == null) {
            throw new IllegalArgumentException(
                    "Unknown extension for mimetype " + mimetype);
        }
       
        filename.append("." + fileExt);

        return filename.toString().toLowerCase();
    }

    /**
     * Get NodeRef for the Indexed Documents folder. This is the root folder for
     * all Ai docs.
     * 
     * @return NodeRef for Indexed Documents folder as defined by the path
     *         Constants.INDEXED_DOCS_XPATH
     */
    public static NodeRef getIndexedDocsFolderRef(SearchService searchService) {
        /* performance hack: the noderef of this folder will never change */
        if (indexedDocsFolderRef == null) {
            indexedDocsFolderRef = findNode(Constants.INDEXED_DOCS_XPATH, searchService);
            return indexedDocsFolderRef;
        }
        return indexedDocsFolderRef;
    }

    public static String getCurrentYear() {
        int yearInt = Calendar.getInstance().get(Calendar.YEAR);
        return Integer.valueOf(yearInt).toString();
    }

    public static String getAiIndexFromPath(NodeRef nodeRef,
            SearchService searchService, FileFolderService fileFolderService,
            String RE_AIINDEX) {

        NodeRef rootNodeRef = Util.getIndexedDocsFolderRef(searchService);

        if (rootNodeRef == null)
            return null;

        List<FileInfo> classFileInfo;
        try {
            classFileInfo = fileFolderService.getNamePath(rootNodeRef, nodeRef);
        } catch (FileNotFoundException e) {
            return null;
        }
        if (classFileInfo.size() < 3)
            return null;

        String year = classFileInfo.get(0).getName();
        String aiClass = classFileInfo.get(1).getName();
        String docNum = classFileInfo.get(2).getName();

        if (aiClass.length() != 5)
            return null;

        String aiIndex = aiClass.substring(0, 3) + " "
                + aiClass.substring(3, 5) + "/" + docNum + "/" + year;

        // Check the index is valid
        Pattern p = Pattern.compile(RE_AIINDEX);
        Matcher m = p.matcher(aiIndex);
        if (m.matches() == true) {
            return aiIndex;
        } else {
            return null;
        }

    }

    /**
     * Invalidate a given asset setting the invalidated property and any
     * accompanying notes.
     * 
     * @param nodeService NodeService instance
     * @param assetRef NodeRef of an existing asset
     * @param validityNotes Notes to be appended to the validityNotes property
     */
    public static void invalidateAsset(NodeService nodeService,
            NodeRef assetRef, String validityNotes) {
        if (nodeService.hasAspect(assetRef, Constants.ASPECT_AUTOVALIDATABLE) == false) {
            nodeService.addAspect(assetRef, Constants.ASPECT_AUTOVALIDATABLE,
                    null);
            nodeService
                    .setProperty(assetRef, Constants.PROP_VALIDITY_NOTES, "");
        }

        nodeService.setProperty(assetRef, Constants.PROP_INVALIDATED, true);

        if (validityNotes != null) {
            String storedNotes = (String) nodeService.getProperty(assetRef,
                    Constants.PROP_VALIDITY_NOTES);
            nodeService.setProperty(assetRef, Constants.PROP_VALIDITY_NOTES,
                    storedNotes + "<br/>&nbsp;&nbsp;" + validityNotes);
        }
    }
    
    /**
     * Get the NodeRef for a category string, using a static cache. Null results are also cached. 
     * 
     * @param category as string value
     * @param searchService as set by calling webscript
     * @return NodeRef of category or null if not found
     */
    
    @Deprecated
    public static NodeRef findCategory(String category, SearchService searchService, NodeService nodeService) {

    	if ( category == null )
        {
        	throw new IllegalArgumentException( "findCategory: category may not be null." );
        }

/*
         * NB necessary to have separate Set for unfound categories because SynchronizedHashMap doesn't support nulls
         */
    	if ( unfoundCategoryCache.contains( category ))
    	{
    		throw new IllegalArgumentException( "findCategory: Category '" + category + "' was not found." );
    	}
        if ( category.length() == 0 )
        {
        	throw new IllegalArgumentException( "findCategory: category may not be zero-length string." );
        }
        
    	NodeRef localRootNode = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
    	DynamicNamespacePrefixResolver namespacePrefixResolver = new DynamicNamespacePrefixResolver(null);
    	namespacePrefixResolver.registerNamespace(NamespaceService.SYSTEM_MODEL_PREFIX, NamespaceService.SYSTEM_MODEL_1_0_URI);
        namespacePrefixResolver.registerNamespace(NamespaceService.CONTENT_MODEL_PREFIX, NamespaceService.CONTENT_MODEL_1_0_URI);
        namespacePrefixResolver.registerNamespace(BaseNodeServiceTest.TEST_PREFIX, BaseNodeServiceTest.NAMESPACE);
        
        QName namePropQName = QName.createQName("{http://www.alfresco.org/model/content/1.0}name");
        
    	if(categoryCache.size()==0)
    	{
    		String query = "//.[subtypeOf(\"cm:category\")]";
    		List <NodeRef> allCategories = searchService.selectNodes(localRootNode, query, null,namespacePrefixResolver, false);
    		Iterator<NodeRef> allCategoriesIt = allCategories.iterator();
    		  while(allCategoriesIt.hasNext())
    	        {
    	        	NodeRef nodeRef = allCategoriesIt.next();
    	        	String name = (String) nodeService.getProperty(nodeRef, namePropQName);
    	        	logger.debug("Category: "+name);
    	        	categoryCache.put(name, nodeRef);
    	    	
    	        }
    	}
    	
        
    	

    	if (categoryCache.containsKey(category))
    	{
            return categoryCache.get(category);
    	}

        
        
       
        NodeRef categoryNodeRef = null;
        
        nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE).toString();
        
        
        
        String xpathQuery = "//.[subtypeOf(\"cm:category\") and @cm:name=\"" + category + "\"]";
        
        
        List <NodeRef> answer = searchService.selectNodes(localRootNode, xpathQuery, null,namespacePrefixResolver, false);
        
        Iterator<NodeRef> categoriesIt = answer.iterator();
        
        while(categoriesIt.hasNext())
        {
        	NodeRef nodeRef = categoriesIt.next();
        	String value = (String) nodeService.getProperty(nodeRef, namePropQName);
        	if(category.equals(value))
    		{
    			categoryNodeRef = nodeRef;
    			break;
    		}
        }
        
        
/*        ResultSet templateResult = null;
        SearchParameters catSearchParameters = new SearchParameters();
        catSearchParameters.addStore(Constants.SEARCH_STORE);
        catSearchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
        catSearchParameters
                .setQuery("TYPE:\"cm:category\" AND @cm\\:name:\""
                        + category + "\"");
        
        
        Iterator<NodeRef> it;
        try {
            templateResult = searchService.query(catSearchParameters);
            if (templateResult.length() > 0) {
            	it = templateResult.getNodeRefs().iterator();
            	while(it.hasNext())
            	{
            		NodeRef nodeRef = it.next();
            		String value = (String) nodeService.getProperty(nodeRef, namePropQName);
            		if(category.equals(value))
            		{
            			categoryNodeRef = nodeRef;
            			break;
            		}
            	}
            }
        } finally {
            templateResult.close();
        }*/
        
        if ( categoryNodeRef == null )
        {
          logger.warn( "Category '" + category + "' was not found." );
          
          unfoundCategoryCache.add( category );
          throw new IllegalArgumentException( "findCategory: Category '" + category + "' was not found." );
          
        } 
        else
        {
            categoryCache.putIfAbsent( category, categoryNodeRef );
            return categoryNodeRef;
        }      
    }
    
    
    /**
     * Get the NodeRef for a category string, using a static cache. Null results are also cached. 
     * 
     * @param category as string value
     * @param categoryService as set by calling webscript
     * @return nodeService as set by calling webscript
     */
    public static NodeRef findCategory(String category, CategoryService categoryService, NodeService nodeService) {

    	categoryCache = new ConcurrentHashMap<String, NodeRef>();
    	unfoundCategoryCache = Collections.synchronizedSet(new HashSet<String>());
    	
    	category  = category.toLowerCase();
    	
    	if ( category == null )
        {
        	throw new IllegalArgumentException( "findCategory: category may not be null." );
        }

    	/*
         * NB necessary to have separate Set for unfound categories because SynchronizedHashMap doesn't support nulls
         */
    	if ( unfoundCategoryCache.contains( category ))
    	{
    		throw new IllegalArgumentException( "findCategory: Category '" + category + "' was not found." );
    	}
        if ( category.length() == 0 )
        {
        	throw new IllegalArgumentException( "findCategory: category may not be zero-length string." );
        }
        
        
        
        if(categoryCache.size()==0)
    	{
	        Collection<ChildAssociationRef> rootCategories = 
	        	categoryService.getRootCategories(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, ContentModel.ASPECT_GEN_CLASSIFIABLE);
	        
	        cacheCategories(rootCategories, nodeService);
	        
    	}
    	

        
    	if (categoryCache.containsKey(category))
    	{
            return categoryCache.get(category);
    	}
    	
    	else
        {
          logger.warn( "Category '" + category + "' was not found." );
          
          unfoundCategoryCache.add( category );
          throw new IllegalArgumentException( "findCategory: Category '" + category + "' was not found." );
        }  
    }
    
    /**
     * 
     * @param categories
     * @param nodeService
     */
    private static void cacheCategories(Collection<ChildAssociationRef> categories, NodeService nodeService)
    {
    	
    	 Iterator<ChildAssociationRef> categoriesAssocIt = categories.iterator();
    		
	    	while(categoriesAssocIt.hasNext())
	    	{
	    		NodeRef categoryNodeRef = categoriesAssocIt.next().getChildRef();
	    		
	    		String name = (String)nodeService.getProperty(categoryNodeRef, ContentModel.PROP_NAME);
	    		logger.debug("Category: "+name);
	    		categoryCache.putIfAbsent(name.toLowerCase(), categoryNodeRef);
	    		
	    		if(nodeService.getChildAssocs(categoryNodeRef).size()>0)
	    		{
	    			cacheCategories(nodeService.getChildAssocs(categoryNodeRef), nodeService);
	    		}
	    	}
    } 
    
    /**
     * 
     * @param documentType
     * @param document1
     * @param document2
     * @return better rendition
     */
	public static boolean betterRenditionMatch(String documentType,Document document1,Document document2 )
	{
		
	  Map<String, Integer> mimetype_prefs = Constants.MIMETYPE_PREFS.get(documentType);

	  Map<String, Integer> edition_prefs = Constants.EDITION_PREFS.get(documentType);

	  if ( mimetype_prefs == null || edition_prefs == null )
	  {
	    throw new IllegalArgumentException( "Unsupported rendition document type: " + documentType );
	  }


	  if ( ( document1 == null ) || ( ( ! document1.isEffective() ) && ( ! Constants.IGNORE_EFFECTIVITY.get(documentType) ) ) ) return false;
	  

	  if ( ( mimetype_prefs.get(document1.getMimetype()) == null ) || (  edition_prefs.get(document1.getEdition()) == null ) ) return false ; 


	  if ( document2 == null ) return true;
	  

	  if ( edition_prefs.get(document1.getEdition()) < edition_prefs.get(document2.getEdition()) ) return true;
	 
	  return ( mimetype_prefs.get(document1.getMimetype()) < mimetype_prefs.get(document2.getMimetype()) );
	}
	
	/**
	 * 
	 * @param asset
	 */
	public static void filterCategories(Asset asset) {
		//set the Categories
		if(asset.getCategories() != null && !asset.getCategories().isEmpty()) {
			asset.setPrimaryPathRegions(filterPathCategories(asset.getCategories(),"Regions"));
			asset.setPrimaryRegions(filterCategories(asset.getCategories(),"Regions"));
			asset.setPrimaryKeywords(filterCategories(asset.getCategories(),"Keywords"));			
			asset.setPrimaryCampaigns(filterCategories(asset.getCategories(),"Campaigns"));
			asset.setPrimaryIssues(filterCategories(asset.getCategories(),"Issues"));			
		}
		
		
		if(asset.getSecondaryCategories() != null && !asset.getSecondaryCategories().isEmpty()) {
			asset.setSecondaryPathRegions(filterPathCategories(asset.getSecondaryCategories(),"Regions"));
			asset.setSecondaryRegions(filterCategories(asset.getSecondaryCategories(),"Regions"));
			asset.setSecondaryKeywords(filterCategories(asset.getSecondaryCategories(),"Keywords"));
			asset.setSecondaryCampaigns(filterCategories(asset.getSecondaryCategories(),"Campaigns"));
			asset.setSecondaryIssues(filterCategories(asset.getSecondaryCategories(),"Issues"));
		}

		asset.setAllPathRegions(mergedKeywords(asset.getPrimaryPathRegions(), asset.getSecondaryPathRegions()));
		asset.setAllRegions(mergedKeywords(asset.getPrimaryRegions(), asset.getSecondaryRegions()));
		asset.setAllKeywords(mergedKeywords(asset.getPrimaryKeywords(), asset.getSecondaryKeywords()));
		asset.setAllCampaigns(mergedKeywords(asset.getPrimaryCampaigns(), asset.getSecondaryCampaigns()));

		asset.setAllIssues(mapKeywordToIssues(asset.getAllKeywords()));	
				
		asset.setAllCategories(mergedKeywords(asset.getAllKeywords(), asset.getAllRegions()));
		asset.setAllPathCategories(mergedKeywords(asset.getAllKeywords(), asset.getAllPathRegions()));
	}
	
	/**
	 * 
	 * @param primaryPathList
	 * @param filter
	 * @return full path between the filter and the category
	 */
	private static HashSet<String> filterPathCategories(List<String> primaryPathList, String filter) {
		
		HashSet<String> category = new HashSet<String>();
	
		for(String pathString : primaryPathList) {

			if(pathString.contains(filter)) {
		
				String categoryString = pathString.substring(pathString.indexOf(filter));				
				categoryString = categoryString.substring(categoryString.indexOf("}")+1);	
	
				java.util.StringTokenizer st = new java.util.StringTokenizer(categoryString,"}");
	
				while (st.hasMoreTokens()) {
					String s = st.nextToken();
					if(s.contains("/")) {
						s=s.substring(0, s.indexOf("/"));
					}
					
					Map<String, String>  corrections = Util.getKeywordCorrectionsMap();
					s=org.alfresco.util.ISO9075.decode(s);

						if(corrections.containsKey(s.toUpperCase())) {
							category.add((String) corrections.get(s.toUpperCase()));
						} else {
							category.add(s);
						}
				}	
				
			}	
		}
		return category;			
	}	

	/**
	 * Filter out the Regions, Keywords, Issue, Campaigns from Primary Paths of Categories
	 * HashSet de duplicates
	 */
	private static HashSet<String> filterCategories(List<String> primaryPathList, String filter) {
		
		HashSet<String> category = new HashSet<String>();
		
		for(String pathString : primaryPathList) {

			if(pathString.contains(filter)) {
		
				String categoryString = pathString.substring(pathString.indexOf(filter));		
				categoryString = categoryString.substring(categoryString.indexOf("}")+1);	
	
				java.util.StringTokenizer st = new java.util.StringTokenizer(categoryString,"}");
	
				while (st.hasMoreTokens()) {
					String s = st.nextToken();
					if(s.contains("/")) {
						s=s.substring(0, s.indexOf("/"));
					}
					
					Map<String, String>  corrections = Util.getKeywordCorrectionsMap();
					s=org.alfresco.util.ISO9075.decode(s);
					
					if(!st.hasMoreTokens() && !s.equalsIgnoreCase(filter))
					{
						if(corrections.containsKey(s.toUpperCase())) {
							category.add((String) corrections.get(s.toUpperCase()));
						} else {
							category.add(s);
						}
					}
				}	
				
			}	
		}
		return category;			
	}
	
	/**
	 * Merges and de-duplicates two lists into an HashSet
	 **/
	private static HashSet<String> mergedKeywords(HashSet<String> primaryKeywords, HashSet<String> secondaryKeywords) {
		HashSet<String> mergedKeywords = new HashSet<String>();
		if(primaryKeywords != null && !primaryKeywords.isEmpty())
			mergedKeywords.addAll(primaryKeywords);
		if(secondaryKeywords != null && !secondaryKeywords.isEmpty())
			mergedKeywords.addAll(secondaryKeywords);
		return mergedKeywords;		
	}
	
	/**
	 * For each keyword search in KEYWORD_ISSUE_MAP by iterating through List within HashMap then get the key
	 **/
	private static HashSet<String> mapKeywordToIssues(HashSet<String> allKeywords) {
		HashSet<String> mapKeywordToIssues = new HashSet<String>();		
		for(String allKeyword : allKeywords) {
			
			Map<String, List<String>> keywordIssueMap =Util.getKeywordIssueMap();
			
			if(keywordIssueMap.containsKey(allKeyword.toUpperCase())) {
				for(Object mappedKeywords : keywordIssueMap.get(allKeyword.toUpperCase())) {
					mapKeywordToIssues.add(mappedKeywords.toString());
				}
			}
		}
		return mapKeywordToIssues;
	}
	
	public static ArrayList<String> getPrimaryPaths(NodeService nodeService, List<NodeRef> categoryNodes) {
		ArrayList<String> primaryPathsStr = new ArrayList<String>();
		
		for(NodeRef categoryNode : categoryNodes) {
			List<Path> primaryPaths = nodeService.getPaths(categoryNode, true);
			for (Path primaryPath : primaryPaths) {
				primaryPathsStr.add(primaryPath.toString());
			}
		}		
		return primaryPathsStr;		
	}
	
	/**
	 * 
	 * @param nodeService
	 * @param assetNode
	 * @return AiIndex class
	 */
	public static AiIndex getAssetAiIndex(NodeService nodeService, NodeRef assetNode)
	{
		String docNo = nodeService.getProperty(assetNode, ContentModel.PROP_NAME).toString();
		logger.debug("[Document No] "+docNo);
		NodeRef classNode =nodeService.getPrimaryParent(assetNode).getParentRef();	
		String classCode = nodeService.getProperty(classNode, ContentModel.PROP_NAME).toString();	
		logger.debug("[Class Code] "+classCode);
		NodeRef yearNode =nodeService.getPrimaryParent(classNode).getParentRef();
		String year = nodeService.getProperty(yearNode, ContentModel.PROP_NAME).toString();
		logger.debug("[Year] "+year);
		return new AiIndex(classCode,docNo,year);
	}
	
	/**
	 * 
	 * @return The map with the keywords and the list of mapped issues
	 */
	private static Map<String, List<String>> loadKeywordIssueMap()
	{
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		
		try{

			  InputStream in = Util.class.getClassLoader().getResourceAsStream(KEYWORD_ISSUE_FILENAME);
			  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			  DocumentBuilder db = dbf.newDocumentBuilder();
			  org.w3c.dom.Document doc = db.parse(in);
			  doc.getDocumentElement().normalize();
			  logger.debug("Root element " + doc.getDocumentElement().getNodeName());
			  org.w3c.dom.NodeList nodeLst = doc.getElementsByTagName("keyword");
	
			  for (int s = 0; s < nodeLst.getLength(); s++) {
	
				org.w3c.dom.Node fstNode = nodeLst.item(s);
			    
			    if (fstNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
			  
			      org.w3c.dom.Element fstElmnt = (org.w3c.dom.Element) fstNode;
			      org.w3c.dom.NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("name");
			      org.w3c.dom.Element nameElmnt = (org.w3c.dom.Element) fstNmElmntLst.item(0);
			      org.w3c.dom.NodeList name = nameElmnt.getChildNodes();
			      logger.debug("Name : "  + ((org.w3c.dom.Node) name.item(0)).getNodeValue());
			      org.w3c.dom.NodeList valuesElmntLst = fstElmnt.getElementsByTagName("value");
			      List<String> values = new ArrayList<String>();
			      for(int x = 0; x<valuesElmntLst.getLength();x++)
			      {
				      org.w3c.dom.Element valueElmnt = (org.w3c.dom.Element) valuesElmntLst.item(x);
				      org.w3c.dom.NodeList valueList = valueElmnt.getChildNodes();
			      
			    	  logger.debug("Value : " +valueList.item(0).getNodeValue());
			    	  values.add(valueList.item(0).getNodeValue());
			      }
			      
			      map.put(name.item(0).getNodeValue(), values);
			    }
	
			  }
				
		  } catch (Exception e) {
			  logger.error(e);
		  }
		  
		return map;
	}
	
	/**
	 * 
	 * @return The map with the keywords and the corrections.
	 */
	private static Map<String, String> loadKeywordCorrectionsMap()
	{
		Map<String, String> map = new HashMap<String, String>();
		
		try{

			  InputStream in = Util.class.getClassLoader().getResourceAsStream(KEYWORD_CORRECTIONS_FILENAME);
			  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			  DocumentBuilder db = dbf.newDocumentBuilder();
			  org.w3c.dom.Document doc = db.parse(in);
			  doc.getDocumentElement().normalize();
			  logger.debug("Root element " + doc.getDocumentElement().getNodeName());
			  org.w3c.dom.NodeList nodeLst = doc.getElementsByTagName("keyword");
	
			  for (int s = 0; s < nodeLst.getLength(); s++) {
	
				org.w3c.dom.Node fstNode = nodeLst.item(s);
			    
			    if (fstNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
			  
			      org.w3c.dom.Element fstElmnt = (org.w3c.dom.Element) fstNode;
			      org.w3c.dom.NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("name");
			      org.w3c.dom.Element nameElmnt = (org.w3c.dom.Element) fstNmElmntLst.item(0);
			      org.w3c.dom.NodeList name = nameElmnt.getChildNodes();
			      logger.debug("Name : "  + ((org.w3c.dom.Node) name.item(0)).getNodeValue());
			      org.w3c.dom.NodeList valuesElmntLst = fstElmnt.getElementsByTagName("value");
				      org.w3c.dom.Element valueElmnt = (org.w3c.dom.Element) valuesElmntLst.item(0);
				      org.w3c.dom.NodeList valueList = valueElmnt.getChildNodes();
			    	  logger.debug("Value : " +valueList.item(0).getNodeValue());
			      
			      map.put(name.item(0).getNodeValue(), valueList.item(0).getNodeValue());
			    }
	
			  }
				
		  } catch (Exception e) {
			  logger.error(e);
		  }
		  
		return map;
	}
	
	public static String langToString(String language )
	{
		Locale myLocale = new Locale(language);
		String result = myLocale.getDisplayLanguage();
	  
	  if ( result == null || result.equals(""))
	  {
	    result = language;
	  }
	  
	  return result;
	}
	
	static public String readClassCode(String classCode, boolean required) throws IllegalArgumentException
	{
	  
	  if (( classCode == null ) || ( classCode.equals("") ))
	  {
	    if ( required )
	    {
	    	throw new IllegalArgumentException("Missing AI index class code");
	    }
	    else
	    {
	      return null;
	    }
	  }
	  
	  classCode = classCode.toUpperCase();
	  
	  Pattern p = Pattern.compile(CLASS_CODE_REGX);
	  Matcher m = p.matcher(classCode);
	  
	  if ( m.matches() )
	  {
	    String subclass = m.group(3);
	  
	    subclass = StringUtils.leftPad(subclass, 2, '0');
	    
	    classCode = m.group(1) + subclass;
	   
	  }
	  else 
	  {
		  throw new IllegalArgumentException("Bad AI index class code: " + classCode);
	  }

	  return classCode;

	}
	
	/**
	 * read language from the argument templates declared in the name.method.desc.xml file
	 * @param document_no
	 * @param required
	 * @return
	 * @throws IllegalArgumentException
	 */
	static public String readDocumentNo(String documentNo, boolean required) throws IllegalArgumentException
	{
	  
	  if (( documentNo == null ) || ( documentNo.equals("") ))
	  {
	    if ( required )
	    {
	    	throw new IllegalArgumentException("Missing AI index document number");
	    }
	    else
	    {
	      return null;
	    }
	  }

	  Pattern p = Pattern.compile(DOCUMENT_NO_REGX);
	  Matcher m = p.matcher(documentNo);
	  
	  if (m.matches())
	  {
		  documentNo = StringUtils.leftPad(documentNo, 3, '0');
	  }
	  else 
	  {
		  throw new IllegalArgumentException("Bad AI index document number: " + documentNo );
	  }
	  
	  return documentNo;

	}
	
	 /** 
	 * read document_year from the argument templates declared in the name.method.desc.xml file
	 * @param documentYear
	 * @param required
	 * @return Document year
	 * @throws IllegalArgumentException
	 **/
	static public String readDocumentYear(String documentYear, boolean required ) throws IllegalArgumentException
	{
	  
	  if (( documentYear == null ) || ( documentYear.equals("")))
	  {
	    if ( required )
	    {
	    	throw new IllegalArgumentException("Missing AI index document year");
	    }
	    else
	    {
	      return null;
	    }
	  }
	  
	  Pattern p = Pattern.compile(DOCUMENT_YEAR_REGX);
	  Matcher m = p.matcher(documentYear);
	  
	  if (  m.matches())
	  {
		  //Compare parseInt function
	    int yearInt = Integer.parseInt( documentYear, 10 );
	    
	    if ( yearInt < 1900 )
	    {
	      if ( yearInt < 60 )
	      {
	        yearInt += 2000;
	      }
	      else
	      {
	        yearInt += 1900;
	      }
	      
	      documentYear = "" + yearInt;
	    }
	  }
	  else 
	  {
		  throw new IllegalArgumentException("Bad AI index year: " + documentYear);
	  }

	  
	  return documentYear;

	}
	
	/**
	 * read language from the argument templates declared in the name.method.desc.xml file
	 * @param selectedLanguage
	 * @param required
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String readSelectedLanguage(String selectedLanguage, boolean required ) throws IllegalArgumentException
	{
	  
	  if (( selectedLanguage == null ) || ( selectedLanguage.equals("") ))
	  {
		if ( required )
		{
			throw new IllegalArgumentException("Missing language ISO code");
		}
		else
		{
		  return null;
		}
	  }
	  else{
		  return selectedLanguage;	  	  
	  } 
	}
	
	//TODO - how to get mapping from serviceRegsitry??

	public static String mimetypeToFormat(String mimetype )
	{


		if(mimetype.equals("application/rtf"))
	    	return "RTF";
		else if(mimetype.equals("application/vnd.excel"))
			return "MSEXCEL";
		else  if(mimetype.equals("application/pdf"))
			return "PDF";
		else if(mimetype.equals("text/html"))
			return "HTML";
		else if(mimetype.equals("application/msword"))
			return "MSWORD";
		else if(mimetype.equals("application/wordperfect"))
			return "WORDPERFECT";
		else if(mimetype.equals("application/xhtml+xml"))
			return "XHTML";

		else return "UNKNOWN";
	}
	
    public static Map<String, List<String>> getKeywordIssueMap() {
		return keywordIssueMap;
	}
    
	public static Map<String, String> getKeywordCorrectionsMap() {
		return keywordCorrectionsMap;
	}
	
	/**
     * This method ensures that the output String has only
     * valid XML unicode characters as specified by the
     * XML 1.0 standard. For reference, please see
     * <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
     * standard</a>. This method will return an empty
     * String if the input is null or empty.
     *
     * @param in The String whose non-valid characters we want to remove.
     * @return The in String, stripped of non-valid characters.
     */
    public static String stripNonValidXMLCharacters(String in) {
        StringBuffer out = new StringBuffer(); // Used to hold the output.
        char current; // Used to reference the current character.

        if (in == null || ("".equals(in))) return ""; // vacancy test.
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
            if ((current == 0x9) ||
                (current == 0xA) ||
                (current == 0xD) ||
                ((current >= 0x20) && (current <= 0xD7FF)) ||
                ((current >= 0xE000) && (current <= 0xFFFD)) ||
                ((current >= 0x10000) && (current <= 0x10FFFF)))
                out.append(current);
        }
        return out.toString();
    }
    
    public static boolean iSValidXMLText(String xml) {
    	boolean valid = true;

    	if( xml != null ) {
    	valid = xml.matches("^([\\x09\\x0A\\x0D\\x20-\\x7E]|" //# ASCII
    	+ "[\\xC2-\\xDF][\\x80-\\xBF]|" //# non-overlong 2-byte
    	+ "\\xE0[\\xA0-\\xBF][\\x80-\\xBF]|" //# excluding overlongs
    	+ "[\\xE1-\\xEC\\xEE\\xEF][\\x80-\\xBF]{2}|" //# straight 3-byte
    	+ "\\xED[\\x80-\\x9F][\\x80-\\xBF]|" //# excluding surrogates
    	+ "\\xF0[\\x90-\\xBF][\\x80-\\xBF]{2}|" //# planes 1-3
    	+ "[\\xF1-\\xF3][\\x80-\\xBF]{3}|" //# planes 4-15
    	+ "\\xF4[\\x80-\\x8F][\\x80-\\xBF]{2})*$"); //# plane 16
    	}

    	return valid;
    	}


}
