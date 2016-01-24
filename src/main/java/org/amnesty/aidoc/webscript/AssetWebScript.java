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
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.amnesty.aidoc.Asset;
import org.amnesty.aidoc.AssetManager;
import org.amnesty.aidoc.AssetRendition;
import org.amnesty.aidoc.Constants;
import org.amnesty.aidoc.Document;
import org.amnesty.aidoc.Util;

public class AssetWebScript extends BaseWebScript {

	
	private NodeService nodeService;
	
	private ContentService contentService;

	private FileFolderService fileFolderService;

	private boolean publicScope;
    private boolean selectByLanguage = true;

	private boolean useDefaultLanguage = false;
	
	private boolean ignoreFormattedHtml = false;
	
	@Override
	protected Map<String, Object> executeAiImpl(WebScriptRequest req,
			Status status) {
		
		Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
		
		Map<String, String> args = req.getServiceMatch().getTemplateVars();
		
		try
		{
			
		
	    // Get the AiIndex and doc folder for the request url given
	    String urlExtension = req.getExtensionPath();
	    NodeRef editionFolder= null;
	    String userLang = req.getParameter("lang");
	    String plain = req.getParameter("plain"); 
	    HashMap<String,String> languages;
	    
	    if(userLang == null && useDefaultLanguage)
	    {
	    	userLang = "en";
	    }
	    if (userLang == null){
	    	throw new IllegalArgumentException("Unspecified user language");
	    }

	    
	    String documentYear = Util.readDocumentYear(args.get("year"), true );
		String classCode = Util.readClassCode(args.get("aiclasssubclass"), true );
		String documentNo = Util.readDocumentNo(args.get("documentno"), true );
	    
		NodeRef assetNode = Util.resolveAssetNode(nodeService, fileFolderService, documentYear, classCode, documentNo);
	    
	    // Do we have an asset?
	    Asset asset = AssetManager.getAsset(nodeService, assetNode, publicScope, selectByLanguage, userLang, ignoreFormattedHtml);

	    if ( publicScope && (asset.getPublishDate() == null || asset.getPublishDate().after(new Date()) ))
	    {
	    	logger.debug("[Publish date] "+asset.getPublishDate());
	    	throw new FileNotFoundException("Document for '" + urlExtension + "' does not exist.");
	    	/*
	        set404();
	        return;
	        */
	    }
	    
	    // Do we have an edition?

	    if (publicScope && asset.getEditions().size() < 1) {
	    	logger.debug("[Editions size] "+asset.getEditions().size());
	    	throw new FileNotFoundException("Document for '" + urlExtension + "' does not exist.");
	    }
	    
	    
	    if(asset.getEditions().size()>0)
	    {	    
	    editionFolder = asset.getEditions().get(0).getNode();
	    }

	    if (publicScope && asset.getDocuments().size() < 1) {
	    	logger.debug("[Documents size] "+asset.getDocuments().size());
	    	throw new FileNotFoundException("Document for '" + urlExtension + "' does not exist.");
	    	/*
	        set404();
	        return;
	        */
	    }
	    
	    if( asset.getDocuments().size() > 0)
	    {
	    	addRelationships(asset.getDocuments());

	    }
	    
	    	languages = asset.getLanguages();

	    
	    model.put("languages", languages);
	        // A Rendition is an object that groups files for display by language and business rules
	    

	    AssetRendition assetRendition = new AssetRendition(contentService, asset.getDocuments(), userLang);
	    
	    
	    if (publicScope && assetRendition.getSelectedRendition() == null){
	    	throw new FileNotFoundException("No documents found for language: " + userLang);
	    }
	    
	    model.put("renditions", assetRendition.getRenditions());
	    model.put("selectedRendition", assetRendition.getSelectedRendition());
	    
	    String content=null;
	    
	    if(assetRendition.getSelectedRendition()!=null)
	    {
		    ContentData contentData = assetRendition.getSelectedRendition().getContent();
		    
		    if(contentData!=null)
		    {
		    	logger.debug("Content Data found");
			    String contenturl=contentData.getContentUrl();
			    
			    
			    ContentReader reader = contentService.getRawReader(contenturl);
			    if ((reader == null) || (!(reader.exists())))
			    {
			    	logger.debug("Unable to locate content for " +contenturl);
			    }
			    else{
			    	logger.debug("Content Found: " +contenturl);
				    content = reader.getContentString();
			    }    			    
		    }
	    }
	    model.put("content", content);
	    
	    
	    
	    // Setup model
	    //model.asset = asset;
	    model.put("asset", asset);
	    //model.assetFolder = asset.node;
	    model.put("assetFolder", asset.getNode());
	    //model.editionFolder = editionFolder;
	    model.put("editionFolder", editionFolder);
	    // Documents just for selected language
	    //var documentsByLanguage = [];
	    List<Document> documentsByLanguage = new ArrayList<Document>();
	    Iterator<Document> documentsIt = asset.getDocuments().iterator();
	    while(documentsIt.hasNext())
	    {
	    	Document document = documentsIt.next();
	    	if (document.getLanguage().equals(userLang)) {
	    		documentsByLanguage.add(document);
	    	}
	    }
	    
	    model.put("documentsByLanguage", documentsByLanguage);
	    // All docs
	    model.put("documents", asset.getDocuments());
	    model.put("lang", userLang);
	    model.put("xsltUuid","7479aa59-35dc-11dc-b7e3-2ba0d230dc8e");
	    
	    // Skin
	    model.put("plain", plain);
		}
		catch (AccessDeniedException e )
		{
			status.setRedirect(true);
			handleError(HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage(), null,
                    model, status);
		}
		catch (BadCredentialsException e )
		{
			status.setRedirect(true);
			handleError(HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage(), null,
                    model, status);
		}
		 catch (IllegalArgumentException e) 
		 { 
			 status.setRedirect(true);
			 handleError(HttpServletResponse.SC_BAD_REQUEST,
	                    e.getMessage(), null,
	                    model, status);
		}catch (FileNotFoundException e) {
	    	logger.debug(e.getMessage());
	    	status.setRedirect(true);
	    	status.setMessage(e.getMessage());
	        status.setCode(HttpServletResponse.SC_NOT_FOUND);
		}
		catch (Exception e )
		{
			status.setRedirect(true);
			handleError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage(), e,
                    model, status);
		}
		return model;
	}


	

	private String insertTypeIntoFilename(String filename, String partType) {
	    String stem = filename.substring(0, filename.lastIndexOf("."));
	    String ext = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
	    return stem + "_" + partType.toLowerCase() + "." + ext;
	}
	
	// Find implicit links between documents
	private void addRelationships(List<Document> documents) {
	    String[] partTypes = new String[]{"Cover", "Contents"};
	    for (int x=0;x<partTypes.length;x++) {
	    	Iterator<Document> documentIt = documents.iterator();
	    	while(documentIt.hasNext())
	    	{
	    		Document document = documentIt.next();
	    		if(document.getType().equals("{"+Constants.AICORE_MODEL+"}" + partTypes[x]))
	    		{
	    			String part = document.getFilename();
	    			
	    			for(Document doc: documents)
	    			{
	    				if (insertTypeIntoFilename(doc.getFilename(), partTypes[x]) == part) {
	    					Document[] hasPart = doc.getHasParts();
	    					hasPart[0] = doc;
	    					doc.setHasParts(hasPart);
	    				}
	    			}
	    		}
	    	}
	    }
	}


	public void setIgnoreFormattedHtml(boolean ignoreFormattedHtml) {
		this.ignoreFormattedHtml = ignoreFormattedHtml;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}
	
    public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}
    
    public boolean isUseDefaultLanguage() {
		return useDefaultLanguage;
	}

	public void setUseDefaultLanguage(boolean useDefaultLanguage) {
		this.useDefaultLanguage = useDefaultLanguage;
	}
	
    public void setPublicScope(boolean publicScope) {
		this.publicScope = publicScope;
	}

	public void setSelectByLanguage(boolean selectByLanguage) {
		this.selectByLanguage = selectByLanguage;
	}
	
}
