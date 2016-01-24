package org.amnesty.aidoc.webscript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.ParameterCheck;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.Asset;
import org.amnesty.aidoc.AssetManager;
import org.amnesty.aidoc.AssetRendition;
import org.amnesty.aidoc.Document;
import org.amnesty.aidoc.Rendition;
import org.amnesty.aidoc.Util;

public class UpdateRenditionWebScript extends BaseWebScript {

	protected NodeService nodeService;
	
	protected FileFolderService fileFolderService;

	//select a particular language variant?
	private boolean selectByLanguage = true;
	//access public published documents only?
	private boolean publicScope = false;
	
	public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
	
	public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }
	
	@Override
	protected Map<String, Object> executeAiImpl(WebScriptRequest req,
			Status status) {
		
		Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
		
		ParameterCheck.mandatoryString("aiIndex", req.getParameter("aiIndex"));
		ParameterCheck.mandatoryString("lang", req.getParameter("lang"));
		ParameterCheck.mandatoryString("title", req.getParameter("title"));
		
		String aiIndexStr = req.getParameter("aiIndex");
		
		String lang = req.getParameter("lang");
		
		String title = req.getParameter("title");
		
		String description = req.getParameter("description");
		
		AiIndex aiIndex = AiIndex.parse(aiIndexStr);

		NodeRef assetRef;
		try {
			assetRef = Util.resolveAssetNode(nodeService, fileFolderService, aiIndex.getYear(), aiIndex.getAiClass(), aiIndex.getDocnum());
		
			Asset asset = AssetManager.getAsset(nodeService, assetRef, publicScope, selectByLanguage, lang);
			
			List<Document> documents = asset.getDocuments();
			
			if (documents.size() == 0) {
				throw new FileNotFoundException("No effective documents");
			}
			
			AssetRendition assetRendition = new AssetRendition(documents, lang);
			
			Rendition rendition = assetRendition.getSelectedRendition();
			
			if (rendition == null) {
				throw new FileNotFoundException("No selected rendition");
			}
			
			List<Document> renditionDocuments = rendition.getDocuments();
			
			if (renditionDocuments.size() == 0) {
				throw new FileNotFoundException("No rendition documents");
			}
			
			for (Document document : renditionDocuments)
			{
				
				if (req.getParameter("title") != null) {
		            nodeService.setProperty(document.getNode(), ContentModel.PROP_TITLE,
		            		title);
		            logger.debug("Title Updated ["+title+"]");
		        }
		        if (req.getParameter("description") != null) {
		            nodeService
		                    .setProperty(document.getNode(), ContentModel.PROP_DESCRIPTION,
		                    		description);
		            logger.debug("Description Updated ["+description+"]");
		        }
			}
			
			model.put("code", HttpServletResponse.SC_OK);
	        model.put("message", "SUCCESS");
        
		} catch (FileNotFoundException e) {
			
			model.put("code", HttpServletResponse.SC_NOT_FOUND);
			 model.put("message", e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			
			model.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			 model.put("message", e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return model;
	}

}
