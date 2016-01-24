package org.amnesty.aidoc.webscript;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.amnesty.aidoc.Asset;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RestrictedDocumentWebScript extends DocumentWebScript {

	private static final Log logger = LogFactory.getLog(RestrictedDocumentWebScript.class);
	
	@Override
	protected Map<String, Object> executeAiImpl(WebScriptRequest req,
			Status status) {
  
		boolean guest = req.isGuest();
		
		 if(guest)
		  {
			 Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
			 model.put("title", "Unauthorized");
			 handleError(HttpServletResponse.SC_UNAUTHORIZED, "Guest user is unauthorized", null,
	                    model, status);
			 return model;
		  }
		 
		Map<String, Object> model = super.executeAiImpl(req, status);
		  
		if(model.get("asset") != null)
		  {
			  Asset asset = (Asset)model.get("asset");
			  
			  if(asset.isPublic())
			  {
				  logger.debug("Asset " + asset.getAiIndex() + " is not restricted.");
				  model.put("title", "Not restricted document");
				  handleError(HttpServletResponse.SC_NOT_FOUND, "Not restricted document", null,
		                  model, status); 
			  }
		  }
          return model;
	  
	}
}
