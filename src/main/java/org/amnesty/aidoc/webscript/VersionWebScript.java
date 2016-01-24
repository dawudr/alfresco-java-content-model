package org.amnesty.aidoc.webscript;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.service.cmr.module.ModuleDetails;
import org.alfresco.service.cmr.module.ModuleService;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class VersionWebScript extends BaseWebScript {

	private final static String MODULE_ID = "aicore" ;
	
	protected ModuleService moduleService;
	
	public void setModuleService(ModuleService moduleService) {
		this.moduleService = moduleService;
	}

	@Override
	protected Map<String, Object> executeAiImpl(WebScriptRequest req,
			Status status) {
		Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
		String version = null;
		
	    model.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    model.put("message", "INCOMPLETE");	
	    
	    logger.debug("Getting AIDOC " + MODULE_ID + " module version" );
	    
	    if ( moduleService == null )
	    {
	          handleError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
	                  "moduleService was not set for VersionWebScript" , null, model, status);
	          return model;
	    }	    
	    
	    ModuleDetails moduleDetails  = moduleService.getModule( MODULE_ID );
	    
	    if ( moduleDetails == null )
	    {
	          handleError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
	                  "No module details obtained for " + MODULE_ID, null, model, status);
	          return model;
	    	
	    }
	    
	    version = moduleDetails.getVersion().toString();

		model.put("code", HttpServletResponse.SC_OK);
		model.put("message", "SUCCESS");
		model.put("version", version);
		
		return model;
	}

}
