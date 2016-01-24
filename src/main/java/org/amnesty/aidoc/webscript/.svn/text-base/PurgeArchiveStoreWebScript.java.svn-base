package org.amnesty.aidoc.webscript;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.repo.node.archive.NodeArchiveService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class PurgeArchiveStoreWebScript extends BaseWebScript {

	
	  /*
	   * Spring service dependencies
	   */
	  protected NodeService nodeService;

	  protected PermissionService permissionService;
	  
	  protected NodeArchiveService nodeArchiveService;
	  
	  public void setNodeService(NodeService nodeService) {
	      this.nodeService = nodeService;
	  }
	 
	  public void setPermissionService( PermissionService permissionService )
	  {
	    this.permissionService = permissionService;
	  }

	  public void setNodeArchiveService(NodeArchiveService nodeArchiveService) {
			this.nodeArchiveService = nodeArchiveService;
		}

	@Override
	protected Map<String, Object> executeAiImpl(WebScriptRequest req,
			Status status) {

		Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);

	    model.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    model.put("message", "INCOMPLETE");	
	    
	    logger.debug("Purging archive store" );

	    if ( nodeArchiveService == null )
	    {
	          handleError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
	                  "nodeArchiveService was not set for PurgeArchiveStoreWebScript" , null, model, status);
	          return model;
	    }
	    
	    if ( nodeService == null )
	    {
	          handleError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
	                  "nodeService was not set for PurgeArchiveStoreWebScript" , null, model, status);
	          return model;
	    }

	    if ( permissionService == null )
	    {
	          handleError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
	                  "permissionService was not set for PurgeArchiveStoreWebScript" , null, model, status);
	          return model;
	    }

	    try {
	    	
	    	//Better way to test for admin rights using PermissionService.ADMINISTRATOR_AUTHORITY??
	    	
	    	StoreRef workspaceStoreRef = new StoreRef("workspace", "SpacesStore");
	    	
	    	NodeRef rootNode = nodeService.getRootNode( workspaceStoreRef );
	    	
	        if ( permissionService.hasPermission( rootNode, PermissionService.DELETE_CHILDREN ) != AccessStatus.ALLOWED )
	        {
	          handleError(HttpServletResponse.SC_UNAUTHORIZED,
	                  "Cannot purge archive: insufficient permissions" , null, model, status);
	          return model;
	        } 
	        
	        nodeArchiveService.purgeAllArchivedNodes( workspaceStoreRef );
	    	
			model.put("code", HttpServletResponse.SC_OK);
			model.put("message", "SUCCESS");
			
		} catch (Exception e) {
	        handleError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
	                "Purging archive store failed: " + e.getMessage(), e, model, status);	
		}		
		
	    return model;
	}


}
