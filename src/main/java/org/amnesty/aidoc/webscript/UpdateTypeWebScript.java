package org.amnesty.aidoc.webscript;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.ParameterCheck;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.Constants;
import org.amnesty.aidoc.Util;

public class UpdateTypeWebScript extends BaseWebScript {
    /*
     * Spring service dependencies
     */
    protected NodeService nodeService;

    protected FileFolderService fileFolderService;

    protected RuleService ruleService;

	protected ActionService actionService;

    protected ContentService contentService;

    protected MimetypeService mimetypeService;

    
    public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public void setActionService(ActionService actionService) {
		this.actionService = actionService;
	}
	
    public void setMimetypeService( MimetypeService mimetypeService )
    {
        this.mimetypeService = mimetypeService;
        logger.debug( "Set mimetypeService to " + mimetypeService );
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

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest,
     *      org.alfresco.web.scripts.WebScriptResponse)
     */
    @Override
    protected Map<String, Object> executeAiImpl(WebScriptRequest req,
           Status status) {

        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);

        /*
         * Get and validate request parameters
         */
        Map<String, String> reqParams = new HashMap<String, String>();
        for (String name : Constants.TYPE_SVC_ALL_PARAMS) {
            reqParams.put(name, req.getParameter(name));
            logger.debug(name + "=" + req.getParameter(name));
        }
        Locale locale;
        try {
            ParameterCheck.mandatoryString("aiIndex", reqParams.get("aiIndex"));
//            ParameterCheck.mandatoryString("type", reqParams.get("type"));
//            ParameterCheck.mandatoryString("lang", reqParams.get("lang"));
            locale = Util.getLocale( reqParams.get("lang") );
            ParameterCheck.mandatoryString("mimetype", reqParams
                    .get("mimetype"));
        } catch (IllegalArgumentException e) {
            handleError(HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage(), null,
                    model, status);
            return model;
        }

        /* Default the edition if not given */
        String edition = reqParams.get("edition");
        if (edition == null || edition.equals("")) {
            edition = "Standard Edition";
        }

        /*
         * Lookup type, return error if not found
         */
        AiIndex aiIndexObj = AiIndex.parse(reqParams.get("aiIndex"));
        
        String year = aiIndexObj.getYear();
        String aiClass = aiIndexObj.getAiClass();
        String docNum = aiIndexObj.getDocnum();
        String filename = Util.buildFilename(aiIndexObj, locale.getLanguage(),
                reqParams.get("type"), reqParams.get("mimetype"), mimetypeService);
        
        
        NodeRef assetTypeRef = null;
        try
        {
        	
        	assetTypeRef = Util.resolveDocumentNode(nodeService, fileFolderService, year, aiClass, docNum, edition, filename);
        }
        catch (Exception e) {
        	//try again with the ISO 639-2 filename instead
        	logger.debug( "Asset type does not exist [" + filename + "]" );
        	
        	String filenameISO3 = Util.buildFilename(aiIndexObj, locale.getISO3Language(),
                    reqParams.get("type"), reqParams.get("mimetype"), mimetypeService);
        	
        	try
        	{
        		assetTypeRef = Util.resolveDocumentNode(nodeService, fileFolderService, year, aiClass, docNum, edition, filenameISO3);
        	}
        	catch (Exception e2) {
        		handleError(HttpServletResponse.SC_NOT_FOUND,
                        "Asset type does not exist ["
                                + filenameISO3 + filename + "]", null, model, status);
                return model;
        	}
		}

        /*
         * Update asset type properties
         */
        
        if (reqParams.get("aiAuxiliaryType") != null) {
            nodeService.setProperty(assetTypeRef, Constants.PROP_AI_AUXILIARY_TYPE,
                    reqParams.get("aiAuxiliaryType"));
            logger.debug("Auxiliary Type Updated ["+reqParams.get("aiAuxiliaryType")+"]");
        }
        
        if (reqParams.get("title") != null) {
            nodeService.setProperty(assetTypeRef, ContentModel.PROP_TITLE,
                    reqParams.get("title"));
            logger.debug("Title Updated ["+reqParams.get("title")+"]");
        }
        if (reqParams.get("description") != null) {
            nodeService
                    .setProperty(assetTypeRef, ContentModel.PROP_DESCRIPTION,
                            reqParams.get("description"));
            logger.debug("Description Updated ["+reqParams.get("description")+"]");
        }
        //added "nullability"
        if (reqParams.get("to") != null) {
            if (reqParams.get("to").trim().length() != 0 ) {
                Date toDate = ISO8601DateFormat.parse(reqParams.get("to"));
                nodeService.setProperty(assetTypeRef, Constants.PROP_TO, toDate);
                logger.debug("Date To Updated ["+toDate+"]");
            } else {
                logger.debug( "nullifying effectivity to");
                nodeService.setProperty(assetTypeRef, Constants.PROP_TO, null);
                logger.debug("Date To Updated [null]");
            }
             
        }
        if (reqParams.get("from") != null) {
            if (reqParams.get("from").trim().length() != 0 ) {
                Date fromDate = ISO8601DateFormat.parse(reqParams.get("from"));
                nodeService.setProperty(assetTypeRef, Constants.PROP_FROM, fromDate);
                logger.debug("Date To Updated ["+fromDate+"]");
            } else {
                logger.debug( "nullifying effectivity from");
                nodeService.setProperty(assetTypeRef, Constants.PROP_FROM, null);
                logger.debug("Date From Updated [null]");
            }
        }
        if (reqParams.get("masterurl") != null) {
            logger.warn("masterurl property not implemented yet");
        }

        if (reqParams.get("contenturl") != null) {
            /* Pull out the store address part of the content url */
            String contentUrl = reqParams.get("contenturl");
            contentUrl = contentUrl.substring(contentUrl.indexOf("=") + 1,
                    contentUrl.indexOf("|"));
            logger.debug("store ref from contenturl=[" + contentUrl + "]");

            //ContentReader contentReader = contentService
            //        .getRawReader(contentUrl);
            //contentReader.setMimetype(reqParams.get("mimetype"));
            
            try
            {
                ContentData contentData = new ContentData(contentUrl, reqParams.get("mimetype"), 0, "UTF-8");
                nodeService.setProperty(assetTypeRef, ContentModel.PROP_CONTENT, contentData);
            }
            catch ( InvalidNodeRefException e )
            {
                handleError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Error with content handling", e, model, status);
                return model;
            }
            
            NodeRef parentRef = nodeService.getPrimaryParent(assetTypeRef).getParentRef();
            logger.debug("Parent name: " + nodeService.getProperty(parentRef, ContentModel.PROP_NAME));
            logger.debug("Parent type: " + nodeService.getType(parentRef).getLocalName());
            List<Rule> rules = ruleService.getRules(parentRef);
            for (Rule rule : rules) {
            	logger.debug("Rule title : " + rule.getTitle());
                if (rule.getTitle().startsWith("Transform")) {
                    actionService.executeAction(rule.getAction(),
                    		assetTypeRef, true, false);
                }
            }
            
            //nodeService.setProperty(assetTypeRef, ContentModel.PROP_CONTENT,
            //        contentData);
        }

        logger.debug("finished with success");

        model.put("code", HttpServletResponse.SC_OK);
        model.put("message", "SUCCESS");
        return model;
    }

}