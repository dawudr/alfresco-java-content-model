package org.amnesty.aidoc.webscript;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import net.sf.acegisecurity.BadCredentialsException;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.Asset;
import org.amnesty.aidoc.AssetManager;
import org.amnesty.aidoc.AssetRendition;
import org.amnesty.aidoc.NodeItem;
import org.amnesty.aidoc.Rendition;
import org.amnesty.aidoc.Util;
import org.apache.commons.lang.StringUtils;

public class BrowseWebScript extends BaseWebScript {

	private static String AIDOC_ROOT = "/Aidoc/Asset Library/Indexed Documents";
	private NodeService nodeService;

	private FileFolderService fileFolderService;

	private ContentService contentService;
	
	//select a particular language variant?
	private boolean selectByLanguage = false;
	//access public published documents only?
	private boolean publicScope = false;
	// human browsing
	private boolean browseFlag = false;

	private boolean showParent = false;
	private boolean showEmptyIndexes = false;
	//private boolean showGrandCildren = false;
	
	private static int BASE_LEVEL = 0;
	private static int DOCUMENT_YEAR_LEVEL = 1;
	private static int CLASS_CODE_LEVEL = 2;
	private static int DOCUMENT_NO_LEVEL = 3;

	private static String CLASS_CODE_STRICT_REGX = "[A-Z]{3}[0-9]{2}";
	private static String DOCUMENT_NO_STRICT_REGX = "[0-9]{3}";
	private static String DOCUMENT_YEAR_STRICT_REGX = "[1-2]{1}[0-9]{3}";
	
	private static String UNRESTRICTED_LIBRARY_INDEX = "library/index";
	private static String RESTRICTED_LIBRARY_INDEX = "library/restricted";
	private static String INTRANET_INDEX = "intranet/index";
	
	@Override
	protected Map<String, Object> executeAiImpl(WebScriptRequest req,
			Status status) {
		
		Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);

		
		 

		try{
			
			Map<String, String> args = req.getServiceMatch().getTemplateVars();
			  
			String documentYear = Util.readDocumentYear(args.get("document_year"), false );
			String classCode = Util.readClassCode(args.get("class_code"), false );
			String documentNo = Util.readDocumentNo(args.get("document_no"), false );
		
			Cache cache = new Cache(getDescription().getRequiredCache());
			SimpleDateFormat sourceDateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			  
			  boolean guest = req.isGuest();
		         
			  if (( guest == true ) && ( publicScope == false ))
			  {
					handleError(HttpServletResponse.SC_UNAUTHORIZED,
							"Guest access is disallowed", null,
		                    model, status);
			  }
	
			  model.put("publicScope", publicScope);
			  model.put("showParent", showParent);
			  model.put("breadcrumb", "");
	  
			  String urlBase =  req.getServiceMatch().getPath();
			  
			  //strip final slash off URL base, if present
			  
			  if ( urlBase.lastIndexOf("/") == ( urlBase.length() - 1 ))
			  {
			    urlBase = urlBase.substring( 0, urlBase.length() - 1 );
			  }  
			  
			  int crawlLevel = ( ( documentYear == null ) ? BASE_LEVEL : ( ( classCode == null ) ? DOCUMENT_YEAR_LEVEL : ( ( documentNo == null ) ? CLASS_CODE_LEVEL : DOCUMENT_NO_LEVEL ) ) );
			  logger.debug("[Crawl level] "+crawlLevel);
			  
			  List<String> aiIndexList = new ArrayList<String>();
			  
			  List<String> URLElements = new ArrayList<String>();
			  URLElements.add(urlBase);
			  List<String> filepathElements = new ArrayList<String>();
			  filepathElements.add(AIDOC_ROOT);
			  List<String> nameElements = new ArrayList<String>();
	
			  List<String> childNameElements = new ArrayList<String>();
			  List<String> childElements = new ArrayList<String>();
			  childElements.add(urlBase);
			  
			  List<String> parentElements = new ArrayList<String>();
			  List<String> parentNameElements = new ArrayList<String>();
			  
			  String[] elements = {urlBase, documentYear, classCode, documentNo };
			  List<NodeItem> descendants = new ArrayList<NodeItem>();
			  List<String> anchors = new ArrayList<String>();
			  List<NodeItem> breadcrumbs = new ArrayList<NodeItem>();
			  
			  if ( crawlLevel == BASE_LEVEL )
			  {
			    model.put("breadcrumb", "browse");
			  }
			  else
			  {
				  breadcrumbs.add(new NodeItem( "browse", urlBase ));
			  }
			  
			  for(int i = 1; i <= crawlLevel; i++ )
			  {
			    filepathElements.add( elements[ i ] );
			    nameElements.add( elements[ i ] );
			    URLElements.add( elements[ i ] );
	
			    childNameElements.add( elements[ i ] );
			    childElements.add( elements[ i ] );
			    
			    parentElements.add( elements[ i - 1 ] );
			    
			    if ( i > 1 )
			    {
			      parentNameElements.add( elements[ i - 1 ] );
			      
			      HashMap<String,String> node = new HashMap<String,String>();
			      node.put(elements[ i - 1 ], StringUtils.join(parentElements.toArray(), "/"));
			      
			      breadcrumbs.add( new NodeItem( elements[ i - 1 ], StringUtils.join(parentElements.toArray(), "/") ) );
			    }
			  
			    if ( i == crawlLevel )
			    {
			      model.put("breadcrumb", elements[i]);
			    }
			  
			  }
			  
			  model.put("hasParent", ( crawlLevel != BASE_LEVEL ));
			  model.put("title", (( crawlLevel == BASE_LEVEL ) ? "AIDOC" : StringUtils.join(nameElements.toArray(), "/") )); 
			  model.put("parent", new NodeItem( (( crawlLevel == DOCUMENT_YEAR_LEVEL ) ? "AIDOC" : StringUtils.join(parentNameElements.toArray(), "/") ), StringUtils.join(parentElements.toArray(), "/")));
			  
			  boolean useAnchors = ( ( crawlLevel != DOCUMENT_NO_LEVEL ) && browseFlag );
			  model.put("useAnchors", useAnchors );
			  
			 logger.debug("[Use anchors]"+useAnchors);
			  /*
			  if ( useAnchors )
			  {
			    anchorFunction = anchorFunctions[ crawl_level ];
			  }
			   */
			  
		      
		      NodeRef baseNode = Util.resolveAssetNode(nodeService, fileFolderService, documentYear, classCode, documentNo);
		      
			  if ( crawlLevel == DOCUMENT_NO_LEVEL )
			  {

				  Asset asset = AssetManager.getAsset(nodeService, baseNode, publicScope, selectByLanguage, null);
			    // this represents an AI index -- determine URL form depending on conditions and availablity
				  logger.debug("[Document level] "+asset.getAiIndex());
			    descendants = getAssetNodes( contentService, asset, browseFlag );
			    
			    if (( cache.getLastModified() == null ) || ( cache.getLastModified().before(asset.getLastModified() )));
				  {
				  
				    cache.setLastModified(asset.getLastModified());
				  
				  }
	
			  }
			  else
			  {
			    
			    if ( baseNode == null )
			    {
			    	handleError(HttpServletResponse.SC_BAD_REQUEST,
			    			"Null base node", null,
		                    model, status);
			    	return model;
			    }
			    
			    Date lastModified = sourceDateFormatter.parse((nodeService.getProperty(baseNode, ContentModel.PROP_MODIFIED).toString()).toString());
			    
			    logger.debug("[Last modified]"+lastModified);
			    
			    String regex = matchFunction(crawlLevel);
			    Pattern p = Pattern.compile(regex);
			    Matcher m = null;
				
			    
			    Iterator<ChildAssociationRef> childAssocIt = nodeService.getChildAssocs(baseNode).iterator();
			    
			    while(childAssocIt.hasNext())
			    {
			    	NodeRef child = childAssocIt.next().getChildRef();
			    	
			    	String name = nodeService.getProperty(child, ContentModel.PROP_NAME).toString();
			    	logger.debug("[Child name] "+name);
			    	
			    	m = p.matcher(name);
			    	
			    	if ( m.matches())
			    	{
			    		//matched OK
			    	}
			    	else
			      	{
			    		logger.warn("Children does't match with pattern: "+regex);
			    		//ignore children which do not obey naming conventions
			    		continue;
			      	}
			      

			      //ignore children with no children unless they are AI Index folders and showEmptyIndexes is set
			      
			      if (( !nodeService.getChildAssocs(child).isEmpty() ) || ( showEmptyIndexes && ( crawlLevel == CLASS_CODE_LEVEL )))
			      {
			        
			        String childname;
			        
			        
			        Date childLastModified = sourceDateFormatter.parse((nodeService.getProperty(baseNode, ContentModel.PROP_MODIFIED).toString()).toString());
			        if ( childLastModified.after(lastModified) )
			        {
			          lastModified = childLastModified;
			        }
			        if(childElements.size()==crawlLevel+2)
			        {
			        	childElements.remove(crawlLevel +1);
			        }
			        childElements.add(name);
			        
			        String childURL = StringUtils.join(childElements.toArray(), "/");
			        
			        if ( crawlLevel == CLASS_CODE_LEVEL )
			        {
			          childname = classCode.substring( 0, 3 ) + " " + classCode.substring( 3 ) + "/" + name + "/" + documentYear ;
			          logger.debug("[Genarated child name] "+childname);
			          
			        }
			        else
			        {
			        	if(childNameElements.size()==crawlLevel+1)
			        	{
			        		childNameElements.remove(crawlLevel);
			        	}
			        	childNameElements.add(name);
			          
			          childname = StringUtils.join(childNameElements.toArray(), "/");
			        }
			        
			        String anchor = null;
			        if ( useAnchors )
			        {
			          anchor = anchorFunction( crawlLevel, name );
			          logger.debug("[Anchor] "+anchor);
			        }
			        
			        descendants.add( new NodeItem( childname, childURL, anchor, null, null, null, null ) );
			        
			        //investigate systemfolder child which otherwise empty index folders seem to have..
			        
			        /*
			        if(showGrandCildren)
			        {
			        
			        String grandchildren = "";
			        
			        Iterator<ChildAssociationRef> grandChildAssocIt = nodeService.getChildAssocs(child).iterator();
			        
			        while(grandChildAssocIt.hasNext())
			        {
			        	NodeRef grandChild = grandChildAssocIt.next().getChildRef();
			        	
			        	String grandChildName = nodeService.getProperty(grandChild, ContentModel.PROP_NAME).toString();
			        	String grandChildType = nodeService.getType(grandChild).getLocalName().toString();

			        	if(crawlLevel==BASE_LEVEL)
			        	{
			        		Iterator<ChildAssociationRef> grandGrandChildAssocIt = nodeService.getChildAssocs(grandChild).iterator();
			        		while(grandGrandChildAssocIt.hasNext())
					        {
					        	NodeRef grandGrandChild = grandGrandChildAssocIt.next().getChildRef();
					        	
					        	String grandGrandChildName = nodeService.getProperty(grandGrandChild, ContentModel.PROP_NAME).toString();
					        	
					        	aiIndexList.add(name + "/" + grandChildName + "/" + grandGrandChildName);
					        	
					        }
			        	}
			        	else{
			        		aiIndexList.add(name + "/" + grandChildName);
			        	}
			          grandchildren = grandchildren + grandChildName + " [" + grandChildType + "];";
			        }
			        
			        
			        //logger.debug("[Grandchildren] "+grandchildren);
			        }*/
	
			      }
			     
			    }
	
			    cache.setLastModified(lastModified);
	
			  }
			  
			  if ( browseFlag == true )
			  {
				  Collections.sort(descendants, new sortNodeItems());
				  logger.debug("Sort descendants");
			  }
			  else
			  {
				  sortFunction(crawlLevel, descendants);
				  logger.debug("Sort descendants");
			  }
	
			  if ( useAnchors )
			  {
				String firstAnchor = null;
			    List<String> anchorList = new ArrayList<String>(); //not list
			    
			    for (int i = 0 ; i < descendants.size(); i++ )
			    {
			      if ( !anchorList.contains(descendants.get(i).getAnchor()))
			      {
			    	  anchors.add(descendants.get(i).getAnchor());
			        
			    	  anchorList.add(descendants.get(i).getAnchor());
			        
			        if ( firstAnchor == null )
			        {
			        	firstAnchor = descendants.get(i).getAnchor();
			        	model.put("firstAnchor", firstAnchor);
			        }
			      }
			      
			    }
			  }

			  model.put("cache", cache);
			  model.put("descendants", descendants);
			  model.put("breadcrumbs", breadcrumbs);
			  model.put("anchors", anchors);
			  model.put("aiIndexList", aiIndexList);
		}
		catch (AccessDeniedException e )
		{
			handleError(HttpServletResponse.SC_UNAUTHORIZED,
                    e.getMessage(), null,
                    model, status);
		}
		catch (BadCredentialsException e )
		{
			
			handleError(HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage(), null,
                    model, status);
		}
		 catch (IllegalArgumentException e) {
			 handleError(HttpServletResponse.SC_BAD_REQUEST,
	                    e.getMessage(), null,
	                    model, status);
		}catch (FileNotFoundException e) {
			handleError(HttpServletResponse.SC_NOT_FOUND,
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
	* create range by ten from number
	* 1999 -> "1991-2000"
	* zero-padded  15 -> "011-020"
	* zero-padded  20 -> "011-020"
	**/
	private String tenRange(String aVal )
	{

	   //note: without the radix leading zeroes are interpreted as octal!
	   
	   int aNum = Integer.parseInt( aVal, 10 );
	   
	   int remainder = ( aNum % 10 );

	   
	   if ( remainder == 0 )
	   {
	     remainder = 10 ;  // so 20 -> 11-20 not 21-30
	   }

	   int num1 = ( aNum - remainder ) + 1 ;
	   int num2 = ( aNum - remainder ) + 10 ;
	  
	   String val1 = StringUtils.leftPad(String.valueOf(num1), 3, '0');
	   String val2 = StringUtils.leftPad(String.valueOf(num2), 3, '0');

	   return val1 + "-" + val2;

	}
	
	private String anchorFunction(int crawlLevel, String name)
	{
		//var anchorFunctions = [ tenRange, firstThree, tenRange ];
		if(crawlLevel==0 || crawlLevel==2)
		{
			return tenRange(name);
		}
		else if(crawlLevel==1)
		{
			return name.substring( 0, 3 );
		}
		else{
			return null;
		}
	}
	
	private String matchFunction(int crawlLevel)
	{
		if(crawlLevel==0)
		{
			return DOCUMENT_YEAR_STRICT_REGX;
		}
		else if(crawlLevel==1)
		{
			return CLASS_CODE_STRICT_REGX;
		}
		else if(crawlLevel==2)
		{
			return DOCUMENT_NO_STRICT_REGX;
		}
		else
		{
			return null;
		}
	}
	
	private List<NodeItem> sortFunction(int crawlLevel, List<NodeItem> list)
	{
		if(crawlLevel==0 || crawlLevel==2)
		{
			logger.debug("Sort Reverse Nodes");
			Collections.sort(list, new sortNodeItemsReverse());
			return list;
		}
		else if(crawlLevel==1)
		{
			logger.debug("Sort Nodes");
			Collections.sort(list, new sortNodeItems());
			return list;
		}
		else{
			return null;
		}
	}
	
	public class sortNodeItems implements Comparator<NodeItem>

	{

		@Override
		public int compare(NodeItem node1, NodeItem node2) {
			return node1.getName().compareTo(node2.getName());
			
		}
	  
	}
	
	public class sortNodeItemsReverse implements Comparator<NodeItem>

	{

		@Override
		public int compare(NodeItem node1, NodeItem node2) {
			return node2.getName().compareTo(node1.getName());
		}
	  
	}
	
	public static List<NodeItem> getAssetNodes(ContentService contentService, Asset asset, boolean browseFlag )
	{

	  List<NodeItem> assetNodes = new ArrayList<NodeItem>();

	  
	  AssetRendition assetRendition = new AssetRendition(contentService, asset.getDocuments(), null);
	 
	  Iterator<Rendition> renditionIt = assetRendition.getRenditions().iterator();
	  while(renditionIt.hasNext())
	  {
		  Rendition rendition = renditionIt.next();

	    String urlElement = (( browseFlag == true ) ? INTRANET_INDEX : RESTRICTED_LIBRARY_INDEX );
	   
	    if ( asset.isPublic() && asset.isPublished() && rendition.getEffectiveDocumentCount() > 0 )
	    {
	   
	      urlElement = (( browseFlag == true ) ? INTRANET_INDEX : UNRESTRICTED_LIBRARY_INDEX );
	    
	    }
	    
	    AiIndex aiIndex = AiIndex.parse(asset.getAiIndex());
	    
	    assetNodes.add( new NodeItem( aiIndex.toString() + " (" + rendition.getLanguageString() + ")", "/" + urlElement + "/" + aiIndex.getYear() + "/" + aiIndex.getAiClass() + "/" + aiIndex.getDocnum() + "/" + rendition.getLanguage(), null, aiIndex.getYear(), aiIndex.getAiClass(), aiIndex.getDocnum(), rendition.getLanguage() ));
	  }

	  return assetNodes;
	}
	
	
	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}
	
	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void setSelectByLanguage(boolean selectByLanguage) {
		this.selectByLanguage = selectByLanguage;
	}


	public void setPublicScope(boolean publicScope) {
		this.publicScope = publicScope;
	}

	public void setShowParent(boolean showParent) {
		this.showParent = showParent;
	}

	public void setShowEmptyIndexes(boolean showEmptyIndexes) {
		this.showEmptyIndexes = showEmptyIndexes;
	}
	
	public void setBrowseFlag(boolean browseFlag) {
		this.browseFlag = browseFlag;
	}

}
