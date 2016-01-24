package org.amnesty.aidoc.webscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.acegisecurity.BadCredentialsException;

import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.amnesty.aidoc.Asset;
import org.amnesty.aidoc.AssetManager;
import org.amnesty.aidoc.Util;

public class ClasscodeWebScript extends BaseWebScript {

	private NodeService nodeService;
	private FileFolderService fileFolderService;
	private boolean publicScope = false;

	@Override
	protected Map<String, Object> executeAiImpl(WebScriptRequest req,
			Status status) {

		Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
		
		try{
			
			Map<String, String> args = req.getServiceMatch().getTemplateVars();
			String documentYear = Util.readDocumentYear(args.get("year"), false );
			String classCode = Util.readClassCode(args.get("aiclasssubclass"), false );
			// Get the AiIndex and doc folder for the request url given
			NodeRef classCodeFolder = Util.resolveAssetNode(nodeService, fileFolderService, documentYear, classCode, null);
			
			if ( classCodeFolder == null )
			{
			  throw new IllegalArgumentException( "No such thing as " + req.getExtensionPath() );
			}
	
			
			List<Asset> assets = new ArrayList<Asset>();
			List<ChildAssociationRef> ChildAssocList = nodeService.getChildAssocs(classCodeFolder);
			
			Iterator<ChildAssociationRef> childIt = ChildAssocList.iterator();
			
			while(childIt.hasNext())
			{
				NodeRef child = childIt.next().getChildRef();
				Asset asset = AssetManager.getLightAsset(nodeService, child, publicScope);
				assets.add(asset);
			}

		    
			model.put("assets", assets);
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
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}
	
	public void setPublicScope(boolean publicScope) {
		this.publicScope = publicScope;
	}

}
