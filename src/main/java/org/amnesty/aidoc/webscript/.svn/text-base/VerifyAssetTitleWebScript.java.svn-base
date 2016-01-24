package org.amnesty.aidoc.webscript;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.amnesty.aidoc.Asset;
import org.amnesty.aidoc.AssetManager;
import org.amnesty.aidoc.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VerifyAssetTitleWebScript extends AbstractWebScript {


	private static final Log logger = LogFactory.getLog(VerifyAssetTitleWebScript.class);

	protected FileFolderService fileFolderService;

	protected NodeService nodeService;


	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

		/*
		try {
			
			
			Map<String, String> args = req.getServiceMatch().getTemplateVars();


			String documentYear = Util.readDocumentYear(args.get("document_year"), true);
			String classCode = Util.readClassCode(args.get("class_code"), true);
			String documentNo = Util.readDocumentNo(args.get("document_no"), true);
			// n.b. language may be null: defaults to en
			
			logger.debug("[Document Year] " + documentYear);
			logger.debug("[Class Code] " + classCode);
			logger.debug("[Document No] " + documentNo);


			// Get the Asset for the request index information
			NodeRef assetNode = Util.resolveAssetNode(nodeService, fileFolderService, documentYear, classCode, documentNo);

			Asset asset = AssetManager.getLightAsset(nodeService, assetNode, false);
			MLText title = asset.getTitle();
			Set<Locale> locales = title.getLocales();
			for (Locale locale:locales)
			{
				res.getWriter().write("Locale "+locale.getDisplayName()+" "+title.get(locale)+"\n");
			}
			
			res.getWriter().write("Default value "+title.getDefaultValue());
			res.getWriter().flush();
			res.getWriter().close();
			
		
		} catch (Exception e) {
			logger.error(e);
			throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null, res);
		}
		*/
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
}