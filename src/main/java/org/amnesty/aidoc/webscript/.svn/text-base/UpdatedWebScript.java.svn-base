package org.amnesty.aidoc.webscript;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.acegisecurity.BadCredentialsException;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.util.ISO8601DateFormat;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.Asset;
import org.amnesty.aidoc.AssetManager;
import org.amnesty.aidoc.AssetRendition;
import org.amnesty.aidoc.Constants;
import org.amnesty.aidoc.NodeItem;
import org.amnesty.aidoc.Rendition;
import org.amnesty.aidoc.Util;

public class UpdatedWebScript extends BaseWebScript {
	
	private SearchService searchService;
	
	private NodeService nodeService;
	
	private ContentService contentService;
	
	private FileFolderService fileFolderService;

	private static String AIDOC_ROOT = "/Asset Library/Indexed Documents" ;

	@Override
	protected Map<String, Object> executeAiImpl(WebScriptRequest req,
			Status status) {
		
		Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
		
		model.put("title", "");
		
		try {
		//access public published documents only?
		  boolean publicScope = false;
		  //select a particular language variant?
		  boolean selectByLanguage = false;
		  
		  boolean doModified = true;
		  
		  boolean doEffectivity=true;	
		  
		  boolean legacy = false;
		  
		  boolean guest = req.isGuest();
	         
		  if (( guest == true ))
		  {
			  publicScope = true;
		  }
		  
		  logger.debug("[Guest] "+guest);
		  
		  String serviceContextUrl = req.getServiceContextPath();
		  String contextUrl = req.getContextPath();
		  
		  if ( req.getParameter("legacy") != null && req.getParameter("legacy").equalsIgnoreCase("true")) legacy = true;
		  
		  int offset = 0 ;
		 
		  if ( req.getParameter("offset") != null ) offset = Integer.parseInt( req.getParameter("offset") );

		  int span = 0;
		  
		  if ( req.getParameter("span") != null ) span = Integer.parseInt( req.getParameter("span") );
		
	  model.put("publicScope", publicScope);
	  
	  if ( span > 0 )
	  {
		model.put("hasNext", true);
	    int nextOffset = offset + 1;
	    int nextSpan = span - 1;
	    model.put("next", new NodeItem( "Next", "?span=" + nextSpan + "&offset=" + nextOffset ));
	  }

	  Date searchDate = (( offset== 0 ) ? new Date() : offsetDate( new Date(), 0 - offset));


	 // N.B. We should use a PATH element to restrict to index docs but it seems to time out!
	 // I.E. ("+PATH:\"/app:company_home/cm:Asset_x0020_Library/cm:Indexed_x0020_Documents//*\" +@cm\\:modified: etc etc
	 
	  //search for indexes with matching publish date: this will include indexes coming out of embargo
	  String searchString = makeDaySearch( "@aicore", "publishDate", searchDate );
	  
	  //if set, search for modification date

	  if ( doModified )
	  {
		  searchString += makeDaySearch( "@cm", "modified", searchDate );
	  }

	  //if set, search for effectivity dates: notice effectivity-to uses a date offset into the past by one day, to catch expired content

	  if ( doEffectivity )
	  {
		  searchString += makeDaySearch( "@cm", "from", searchDate ) + makeDaySearch( "@cm", "to", offsetDate( searchDate, -1 ) );
	  }
	  
	  SearchParameters catSearchParameters = new SearchParameters();
      catSearchParameters.addStore(Constants.SEARCH_STORE);
      catSearchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
      catSearchParameters.setQuery(searchString);
      ResultSet searchNodes = searchService.query(catSearchParameters);
      
	  

	  List<String> assets = new ArrayList<String>();
	  
	  List<NodeItem> links;
	  
	  List<NodeItem> nodes = new ArrayList<NodeItem>();
	  
	  Iterator<NodeRef> searchNodesIt = searchNodes.getNodeRefs().iterator();
	  
	  while(searchNodesIt.hasNext())	  
	  {
		  try
		  {
		  NodeRef node = searchNodesIt.next();

		  Path nodePath = nodeService.getPath(node);
		  
		  String displayPath = org.alfresco.util.ISO9075.decode(nodePath.toString());
		  displayPath = displayPath.replaceAll("(\\{.+?\\})", "");
	    if ( displayPath.indexOf( AIDOC_ROOT ) != -1 )
	    {
	      String nodeName = (String) nodeService.getProperty(node, ContentModel.PROP_NAME);
	      String fullPath = displayPath + "/" + nodeName;
	      
	      logger.debug("[Full path]"+fullPath);
	      String[] indexParts = fullPath.split("/");
	      
	      //do not bother with changes above the asset level, e.g, new year folders, etc
	      if ( indexParts.length > 6 )
	      {
	       
	        String documentYear = indexParts[4];
	        String classCode = indexParts[5];
	        String documentNo = indexParts[6];
	        logger.debug("[Document year] "+documentYear);
	        logger.debug("[Class code] "+classCode);
	        logger.debug("[Document No] "+documentNo);
	        if ( !assets.contains(documentYear + "/" + classCode + "/" + documentNo))
	        {
	        	/*
		        	NodeRef assetNode = node;
					while (!this.nodeService.getType(assetNode).equals(ContentModel.TYPE_STOREROOT) ) {
						if(this.nodeService.getType(assetNode).equals(Constants.PROP_ASSET)) {		
							break;
						}else
						{
							assetNode = nodeService.getPrimaryParent(assetNode).getParentRef();
						}
						
					}
					*/
					
	        	NodeRef assetNode = Util.resolveAssetNode(nodeService, fileFolderService, documentYear, classCode, documentNo);
	        	logger.debug("Asset node found");
	        	
	        	Asset asset = AssetManager.getAsset(nodeService, assetNode, publicScope, selectByLanguage, null);
				
	        	links = new ArrayList<NodeItem>();

	    	  
	    	  AssetRendition assetRendition = new AssetRendition(contentService, asset.getDocuments(), null);
	    	 
	    	  Iterator<Rendition> renditionIt = assetRendition.getRenditions().iterator();
	    	  while(renditionIt.hasNext())
	    	  {
	    		  Rendition rendition = renditionIt.next();

	    	    String urlElement = null ;
	    	   
	    	    if ( asset.isPublic() && asset.isPublished() && rendition.getEffectiveDocumentCount() > 0 )
	    	    {
	    	   
	    	      urlElement = "/library/index";
	    	    
	    	    }else{
	    	    	urlElement = "/library/restricted";
	    	    }
	    	    
	    	    AiIndex aiIndex = AiIndex.parse(asset.getAiIndex());
	    	    
	    	    String scriptUrl = null;
	    	    if(!legacy)
	    	    {
		    	    scriptUrl = serviceContextUrl + urlElement +
		    		"/" + aiIndex.getYear() + "/" + aiIndex.getAiClass() + "/" + aiIndex.getDocnum() + "/" + 
		    		rendition.getLanguage();
	    	    }else{
	    	    	scriptUrl = contextUrl+"/en/library/asset"+"/"+aiIndex.getYear() + "/" + 
	    	    	aiIndex.getAiClass()+ "/" + aiIndex.getAiClass().toLowerCase()+aiIndex.getDocnum()+
	    	    	aiIndex.getYear()+rendition.getLanguage()+".html";
	    	    }

	    	    links.add( new NodeItem( aiIndex.toString() + " (" + rendition.getLanguageString() + ")", scriptUrl));

	    	  }
	    	  
	    	  
	    	  
	          
	        	Iterator<NodeItem> linksIt = links.iterator();
	        	
	        	while(linksIt.hasNext())
	        	{
	        	
	        		nodes.add(linksIt.next());
	        	}

	          assets.add(documentYear + "/" + classCode + "/" + documentNo);
	          
	        }
	        
	      }
	      
	    }

	  }
		    catch (AccessDeniedException e) {
				  logger.debug(e.getMessage());		  
			  }
	  }
	  
	  DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy");
	  String title = nodes.size() + " results for " + dateFormat.format(searchDate);
	  
	  model.put("nodes", nodes);
	  model.put("title", title);
	  
	  Cache cache = new Cache();
	  cache.setIsPublic(false);
	  cache.setNeverCache(false);
	  
	  //cache.isPublic = false;
	  //cache.neverCache = false;
	  //cache maxAge (in seconds) is one houir fopr zero offset (=today) otherwise 1 day
	  if ( offset == 0 )
	  {		  
		cache.setMaxAge(new Long(60 * 60));
		cache.setMustRevalidate(true);
	    //cache.maxAge = ( 60 * 60 ) ;
	    //cache.mustRevalidate = true;
	  }
	  else
	  {
		cache.setMaxAge(new Long(60 * 60 * 24));
		cache.setMustRevalidate(false);
	    //cache.maxAge = ( 60 * 60 * 24 ) ;
	    //cache.mustRevalidate = false;
	  }
	 
	  model.put("cache", cache);
	  
	  
	  
		} 
		catch (BadCredentialsException e )
		{
			model.put("title", "Forbidden");
			handleError(HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage(), null,
                    model, status);
		}
		catch (IllegalArgumentException e) {
			handleError(HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage(), null,
                    model, status);
		}	
		catch (Exception e )
		{
			
			handleError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage(), e,
                    model, status);
		}
		
		return model;
	}
	
	/**
	 * n.b.Can use the short form for namespace
	 */
	public String makeDaySearch(String namespace, String fieldName, Date dayDate )
	{
	   
	  return namespace + "\\:" + fieldName + ":[" + ISO8601DateFormat.format( dayDate )+ "T00:00:00" + " TO " + ISO8601DateFormat.format( dayDate )+  "T23:59:59" + "] ";

	}
	
	public Date offsetDate(Date dateObject,int days )
	{

	  long milis = dateObject.getTime();

	  milis += ( days * 60 * 60 * 24 * 1000 );
	 
	  Date diffDay = new Date();
	  
	  diffDay.setTime( milis );
	  
	  return diffDay;

	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	
	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}
	
	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

}
