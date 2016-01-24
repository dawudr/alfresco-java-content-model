package org.amnesty.aidoc.webscript;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.acegisecurity.BadCredentialsException;

import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.amnesty.aidoc.Asset;
import org.amnesty.aidoc.AssetManager;
import org.amnesty.aidoc.AssetRendition;
import org.amnesty.aidoc.Document;
import org.amnesty.aidoc.Edition;
import org.amnesty.aidoc.NodeItem;
import org.amnesty.aidoc.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DocumentWebScript extends BaseWebScript{
	
	private boolean showInternal = true;
	
	private static final Log logger = LogFactory.getLog(DocumentWebScript.class);
	
	protected FileFolderService fileFolderService;

    protected NodeService nodeService;

    protected ContentService contentService;

	@Override
	protected Map<String, Object> executeAiImpl(WebScriptRequest req,
			Status status) {
		
		Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
		
		
		model.put("title", "");
		
		try
		{
		  Map<String, String> args = req.getServiceMatch().getTemplateVars();
			
		  String documentYear = Util.readDocumentYear(args.get("document_year"), true );
		  String classCode = Util.readClassCode(args.get("class_code"), true );
		  String documentNo = Util.readDocumentNo(args.get("document_no"), true );
		  //n.b. language may be null: defaults to en
		  String selectedLanguage = Util.readSelectedLanguage(args.get("selected_language"), false );
		  
		  if ( selectedLanguage == null || selectedLanguage.equals("")) selectedLanguage="en";
		  
		  logger.debug("[Selected lenguage] "+selectedLanguage);
		  

		  model.put("breadcrumb", Util.langToString( selectedLanguage ));
		  
		  List<NodeItem> breadcrumbs = new ArrayList<NodeItem>();
		  String urlBase = req.getServiceContextPath() + "/intranet/browse";  
		  breadcrumbs.add( new NodeItem( "browse", urlBase ) );
		  
		  urlBase += "/" + documentYear ;  
		  breadcrumbs.add( new NodeItem( documentYear, urlBase ) );

		  urlBase += "/" + classCode ;  
		  breadcrumbs.add( new NodeItem( classCode, urlBase ) );
		  
		  urlBase += "/" + documentNo ;  
		  breadcrumbs.add( new NodeItem( documentNo, urlBase ) );

		  model.put("breadcrumbs", breadcrumbs);
		 
		  //Get the Asset for the request index information
		  
		  NodeRef assetNode = Util.resolveAssetNode(nodeService, fileFolderService, documentYear, classCode, documentNo);
	      
	      Asset asset = AssetManager.getAsset(nodeService, assetNode, false, true, selectedLanguage);
	      
		  if(logger.isDebugEnabled())
		  {
			  logger.debug("Asset");
			  logger.debug(asset.toString());
			  Iterator<Edition> editionsIt = asset.getEditions().iterator();
			  while(editionsIt.hasNext())
			  {
				  logger.debug("Edition");
				  logger.debug(editionsIt.next().toString());
			  }
		  }
		  
		  List<Document> documents = asset.getDocuments();

		  if (documents.size() == 0 )
		  {
			  handleError(HttpServletResponse.SC_NOT_FOUND,
					  "No effective documents", null,
	                    model, status);
			  return model;
		  }
		  
		  
		  AssetRendition assetRendition = new AssetRendition(contentService, documents, selectedLanguage);	
			
			if(asset.isTypeMismatch()) asset.getProblems().add("Type mismatch");
			if(assetRendition.isTitleMismatch()) asset.getProblems().add("Title mismatch");
			if(assetRendition.isDescriptionMismatch()) asset.getProblems().add("Description mismatch");
			if(assetRendition.isMissingLanguage()) asset.getProblems().add("Missing language");
			if(assetRendition.isContentMissing()) asset.getProblems().add("Missing content");
		    
		    if ( assetRendition.getSelectedRendition() == null )
		    {
		    	model.put("title", "Not Found");
		    	handleError(HttpServletResponse.SC_NOT_FOUND,
						  "No selected rendition", null,
		                    model, status);
		      return model;
		    }
		    
		    if(logger.isDebugEnabled())
			  {
		    	logger.debug("Selected Rendtion");
		    	assetRendition.getSelectedRendition().toString();
			  }
		    String title = assetRendition.getSelectedRendition().getTitle();
		    String description = assetRendition.getSelectedRendition().getDescription();
		    ContentData contentData = assetRendition.getSelectedRendition().getContent();
		    String content=null;
		    if(contentData!=null)
		    {
		    	logger.debug("Content Data found");
			    String contenturl=contentData.getContentUrl();
			    
			    
			    ContentReader reader = contentService.getRawReader(contenturl);
			    if ((reader == null) || (!(reader.exists())))
			    {
			    	logger.error("Unable to locate content for " +contenturl);
			    }
			    else{
			    	logger.debug("Content Found: " +contenturl);
				    content = reader.getContentString();
			    }    			    
		    }

		    if(title==null) title ="";
		    if(content==null) content ="";
		    if(description==null) description ="";
		    
		    model.put("title", title);
		    model.put("description", description);
		    model.put("content", content);
		    model.put("selectedRendition", assetRendition.getSelectedRendition());
		    model.put("lang", selectedLanguage);
		   
		    
		    // These are to present some content for full-text indexing even if there is no HTML content: not sure if this is really necessary 
		    // since title and description are readible in the head section
		    model.put("indexContent", (( !content.equals("") ) ? content : (( !description.equals("") ) ? description : title )));
		    // tells Freemarker whether the content requires HTML encoding
		    model.put("indexContentIsHtml", (( !content.equals("") ) ? true : false ));

		  model.put("documents", asset.getDocuments());
		  model.put("editions", asset.getEditions());
		  model.put("renditions", assetRendition.getRenditions());
		  
		  //only expose integrity problems in internal contexts
		  if ( showInternal )
		  {
		    model.put("problems", asset.getProblems());
		  }
		  else
		  {
		    model.put("problems", new ArrayList<String>());
		  }
		  
		  model.put("asset", asset);
		  model.put("assetFolder", asset.getNode());
		  model.put("lastModified", asset.getLastModified());
		  model.put("document_year", documentYear);
		  model.put("document_no", documentNo);
		  model.put("class_code", classCode);
		  model.put("ai_class", classCode.substring( 0, 3));
		  model.put("ai_subclass", classCode.substring( 3, 5));
		  
		  Cache cache = new Cache(getDescription().getRequiredCache());
		  cache.setLastModified(asset.getLastModified());
		  cache.setIsPublic((asset.isPublic() && asset.isPublished()));
		  //cache.setNeverCache(! (asset.isPublic() && asset.isPublished()));
		  
		  
		  cache.setNeverCache(false);
		  //cache maxAge (in seconds) is the same as twice time passed since last modified 
		  cache.setMaxAge((( new Date().getTime() - asset.getLastModified().getTime() ) / 500 ));
		  model.put("cache", cache);
		 
		  cache.setMustRevalidate(false);
		}
		catch (AccessDeniedException e )
		{
			handleError(HttpServletResponse.SC_FORBIDDEN,
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

	public FileFolderService getFileFolderService() {
		return fileFolderService;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public ContentService getContentService() {
		return contentService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

}
