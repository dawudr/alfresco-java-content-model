package org.amnesty.aidoc.webscript;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.acegisecurity.BadCredentialsException;

import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.amnesty.aidoc.Util;

public class SpaceWebScript extends BaseWebScript{

	private NodeService nodeService;
	private FileFolderService fileFolderService;
	
	@Override
	protected Map<String, Object> executeAiImpl(WebScriptRequest req,
			Status status) {
	
			Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
			
			try{
			String baseref = "/Aidoc/Asset Library/Indexed Documents";
			String extn = req.getExtensionPath();
			String[] aiIndexParts = null;
			String docPath = new String();
			String year = null;
			String classCode = null;
			
			int level;
			
			if (extn.length() == 0){
				level = 0;
				extn = "/";
			}else{

				// Remove trailing slash
				if (extn.length() != 0 && extn.lastIndexOf('/') == (extn.length() - 1))
					extn = extn.substring(0, extn.length()-1);
				
			  aiIndexParts = extn.split("/");
			  level = aiIndexParts.length;

				// Add it back in again
				extn += '/';
			
			}	

			 model.put("level",level);
			 
			switch (level){
			  case 0:
				  docPath = baseref;
			    break;
			  case 1:
				  docPath = baseref + "/" + aiIndexParts[0];
			    break;
			  case 2:
				  docPath = baseref + "/" + aiIndexParts[0] + "/" + aiIndexParts[1];
				  year = aiIndexParts[0];
				  classCode = aiIndexParts[1];
				  	model.put("year", aiIndexParts[0]);
				  	model.put("aiClass",aiIndexParts[1]);
			    break;
			 }
		     
			model.put("docPath", docPath);
			NodeRef selectedFolder = Util.resolveAssetNode(nodeService, fileFolderService, year, classCode, null);
			model.put("selectedFolder",selectedFolder);
			model.put("extn",extn);
			}
			catch (BadCredentialsException e )
			{
				
				handleError(HttpServletResponse.SC_FORBIDDEN,
	                    e.getMessage(), null,
	                    model, status);
			}
			catch (FileNotFoundException e) {
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
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}


}
