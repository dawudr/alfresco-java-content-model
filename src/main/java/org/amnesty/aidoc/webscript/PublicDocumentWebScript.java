package org.amnesty.aidoc.webscript;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.amnesty.aidoc.Asset;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PublicDocumentWebScript extends DocumentWebScript {

	private static final Log logger = LogFactory.getLog(PublicDocumentWebScript.class);
	
	@Override
	protected Map<String, Object> executeAiImpl(WebScriptRequest req,
			Status status) {
  
		  Map<String, Object> model = super.executeAiImpl(req, status);
		  
		  if(model.get("asset") != null)
		  {
			  Asset asset = (Asset)model.get("asset");
			  
			  if(!asset.isPublic())
			  {
				  logger.debug("Asset " + asset.getAiIndex() + " is not public.");
				  model.put("title", "Not a public document");
				  handleError(HttpServletResponse.SC_NOT_FOUND, "Not a public document", null,
		                  model, status); 
			  }
		  }
          return model;
	  
	}
	
}
