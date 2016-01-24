package org.amnesty.aidoc.webscript;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.ml.MultilingualContentService;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.ParameterCheck;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.Status;
import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.Constants;
import org.amnesty.aidoc.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This service creates a new asset type given an ai index, edition, type, file
 * and metadata.
 * 
 * An folder for the ai index must already exist prior to this call or an error
 * is raised.
 */
public class CreateTypeWebScript extends BaseWebScript {

    protected ActionService actionService;

    protected MimetypeService mimetypeService;

    protected DictionaryService dictionaryService;

    protected ContentService contentService;

    protected FileFolderService fileFolderService;

    protected MultilingualContentService multilingualContentService;

    protected NodeService nodeService;

    protected RuleService ruleService;

    protected SearchService searchService;

    protected static NodeRef templateRef = null;

    protected static NodeRef formattedTemplateRef = null;
    
    protected static String EDITION_TEMPLATE_PATH = Constants.SPACE_TEMPLATES_XPATH
    + "/cm:Edition_x0020_Template";
    
    protected static String FORMATTED_EDITION_TEMPLATE_PATH = Constants.SPACE_TEMPLATES_XPATH
    + "/cm:Formatted_x0020_Edition_x0020_Template";
    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest,
     *      org.alfresco.web.scripts.WebScriptResponse)
     */
    protected Map<String, Object> executeAiImpl(WebScriptRequest req,
            Status status) {
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);

        Map<String, String> reqParams = new HashMap<String, String>();
        for (String name : Constants.TYPE_SVC_ALL_PARAMS) {
            reqParams.put(name, req.getParameter(name));
        }

        try {
            CreateTypeService service = new CreateTypeService(actionService,
                    dictionaryService, contentService, fileFolderService,
                    multilingualContentService, nodeService, ruleService,
                    searchService, mimetypeService);
            service.createType(reqParams);
        } catch (IllegalArgumentException e) {
            handleError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), e,
                    model, status);
            return model;
        } catch (Exception e) {
            handleError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e
                    .getMessage(), e, model, status);
            return model;
        }

        logger.debug("finished with success");

        model.put("code", HttpServletResponse.SC_OK);
        model.put("message", "SUCCESS");
        return model;
    }

    /**
     * This service will create an asset type. This involves: - file upload -
     * edition folder created from a space template - new node with metadata for
     * the actual content.
     * 
     * @author chatch
     */
    public class CreateTypeService {

        private Log logger = LogFactory.getLog(this.getClass());

        protected ActionService actionService;

        protected MimetypeService mimetypeService;

        protected DictionaryService dictionaryService;

        protected ContentService contentService;

        protected FileFolderService fileFolderService;

        protected MultilingualContentService multilingualContentService;

        protected NodeService nodeService;

        protected RuleService ruleService;

        protected SearchService searchService;

        public CreateTypeService(ServiceRegistry registry) {
            actionService = registry.getActionService();
            dictionaryService = registry.getDictionaryService();
            contentService = registry.getContentService();
            fileFolderService = registry.getFileFolderService();
            nodeService = registry.getNodeService();
            ruleService = registry.getRuleService();
            searchService = registry.getSearchService();

            final QName MULTILINGUAL_CONTENT_SERVICE = QName
                    .createQName(NamespaceService.ALFRESCO_URI,
                            "MultilingualContentService");
            multilingualContentService = (MultilingualContentService) registry
                    .getService(MULTILINGUAL_CONTENT_SERVICE);
        }

        public CreateTypeService(ActionService actionService,
                DictionaryService dictionaryService,
                ContentService contentService,
                FileFolderService fileFolderService,
                MultilingualContentService multilingualContentService,
                NodeService nodeService, RuleService ruleService,
                SearchService searchService, MimetypeService mimetypeService) {

            this.actionService = actionService;
            this.dictionaryService = dictionaryService;
            this.contentService = contentService;
            this.fileFolderService = fileFolderService;
            this.multilingualContentService = multilingualContentService;
            this.nodeService = nodeService;
            this.ruleService = ruleService;
            this.searchService = searchService;
            this.mimetypeService = mimetypeService;
        }

        public void createType(Map<String, String> params)
                throws FileExistsException, FileNotFoundException {
            /*
             * Get and validate request parameters
             */
            for (String name : Constants.TYPE_SVC_MANDATORY_PARAMS) {
                try {
                    logger.debug(name + "=" + params.get(name));
                    ParameterCheck.mandatoryString(name, params.get(name));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                            "Missing mandatory parameter " + name);
                }
            }
            for (String name : Constants.TYPE_SVC_NON_MANDATORY_PARAMS) {
                logger.debug(name + "=" + params.get(name));
            }

            
            /*
             * Check AiIndex folder exists and fail if not
             */
            AiIndex aiIndex = AiIndex.parse(params.get("aiIndex"));
            String year = aiIndex.getYear();
            String aiClass = aiIndex.getAiClass();
            String docnum = aiIndex.getDocnum();
            
            NodeRef aiIndexFolderRef = null;
            try
            {
            aiIndexFolderRef = Util.resolveAssetNode(nodeService, fileFolderService, 
            		year, aiClass, docnum);
            }
            catch(FileNotFoundException e)
            {
            	throw new IllegalArgumentException(
                "Asset does not exist. Call the createasset service first.");
            }
            String documentType = params.get("type");
            
        	logger.debug("Request Parameter Type : "+ documentType);
        	
            String assetIndexType = (String) nodeService.getProperty(aiIndexFolderRef, Constants.PROP_AI_INDEX_TYPE);
            
            /* Check Asset type is valid */
            /* TODO refactor to use AUXILIARY_TYPES with null type
             * signifying "inherit type from parent aiIndexType"
             */
            
            
            QName typeQName = null;
            
            boolean isAuxiliary = false;
            
            if(documentType == null && assetIndexType.equalsIgnoreCase("Unknown"))
        	{
            	throw new IllegalArgumentException("Type parameter is mandatory when index type is Unknown");
        	}
            
            if (Constants.AUXILLIARY_TYPES.contains(documentType))
            {
            	isAuxiliary = true;
            }
            
            if(assetIndexType.equalsIgnoreCase("Unknown"))      	
            {
            	
            	if(!Constants.ASSET_TYPES.contains(documentType))
            	{
            		 throw new IllegalArgumentException("Type '"
                             + documentType + "' is invalid");
            	}
            	
            	else if(isAuxiliary)
            	{
            		throw new IllegalArgumentException(
            				"Type cannot be auxiliary if index type is unknown");
            	}
            	else
            	{
            		nodeService.setProperty(aiIndexFolderRef, Constants.PROP_AI_INDEX_TYPE, (String)documentType);
	            	
	            	typeQName = QName.createQName("{" + Constants.AICORE_MODEL
	                        + "}" + documentType);
            	}
            	
            }
            else
            {
            	if(isAuxiliary)
            	{
            		typeQName = QName.createQName("{" + Constants.AICORE_MODEL
                            + "}" + documentType);
            	}
            	else
            	{
            		typeQName = QName.createQName("{" + Constants.AICORE_MODEL
	                        + "}" + assetIndexType);
            	}
            }


            /* Default the edition if not given */
            String edition = params.get("edition");
            if (edition == null || edition.equals("")) {
                edition = "Standard Edition";
                logger.debug("defaulting edition to " + edition);
            }

            /* user getLocale to test for valid iso639-1 or ISO639-2 code */
            Locale locale = Util.getLocale(params.get("lang"));


            /*
             * Determine folder to create the asset type in. If it is a summary
             * or pinksheet it should go in the asset folder, otherwise it
             * should go in an edition folder.
             */
            NodeRef typeParentRef = null;
            if (documentType!=null && (documentType.equals("Pinksheet")
                    || documentType.equals("Summary"))) {
                typeParentRef = aiIndexFolderRef;
            } else {
                /* check for edition folder and create if it doesn't exist */
                NodeRef editionNodeRef = null;
                try
                {
                editionNodeRef = Util.resolveEditionNode(nodeService, fileFolderService, year, aiClass, docnum, edition);
                }
                catch (FileNotFoundException e) {
                	editionNodeRef = createEditionFolder(aiIndexFolderRef,
                            edition);
				}
                typeParentRef = editionNodeRef;
            }

            /*
             * Create the type
             */
            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(
                    1);

            String filename = Util.buildFilename(aiIndex, locale.getLanguage(),
                    documentType, params.get("mimetype"), mimetypeService );
            logger.debug("filename set = " + filename);
            properties.put(ContentModel.PROP_NAME, filename);

            properties.put(ContentModel.PROP_TITLE, params.get("title"));
            properties.put(ContentModel.PROP_DESCRIPTION, params
                    .get("description"));
            properties.put(ContentModel.PROP_AUTHOR, "Amnesty International");

            if (( params.get("to") != null ) && ( params.get("to").trim().length() > 0 )){
                Date toDate = ISO8601DateFormat.parse(params.get("to"));
                properties.put(Constants.PROP_TO, toDate);
            }

            if ((params.get("from") != null)  && ( params.get("from").trim().length() > 0 )){
                Date fromDate = ISO8601DateFormat.parse(params.get("from"));
                properties.put(Constants.PROP_FROM, fromDate);
            }

            /* Pull out the store address part of the content url */
            String contentUrl = params.get("contenturl");
            if (contentUrl.contains("=") && contentUrl.contains("|")) {
                contentUrl = contentUrl.substring(contentUrl.indexOf("=") + 1,
                        contentUrl.indexOf("|"));
            }
            logger.debug("store ref from contenturl=[" + contentUrl + "]");

            
            //NamespaceService.CONTENT_MODEL_1_0_URI;
            NodeRef typeNodeRef = nodeService
                    .createNode(
                            typeParentRef,
                            ContentModel.ASSOC_CONTAINS,
                            QName
                                    .createQName(NamespaceService.CONTENT_MODEL_1_0_URI
                                            + filename), typeQName, properties)
                    .getChildRef();
            logger.debug("created type with ref=" + typeNodeRef);

            // The old approach requires admin access. See AIDOC-406
            /*
            ContentReader contentReader = contentService
                    .getRawReader(contentUrl);
            contentReader.setMimetype(params.get("mimetype"));

            ContentWriter contentWriter = contentService.getWriter(typeNodeRef,
                    ContentModel.PROP_CONTENT, true);
            contentWriter.setMimetype(params.get("mimetype"));
            
            contentWriter.putContent(contentReader);*/
            
            // Instead we just set the content property. Faster and simpler.
            
            ContentData contentData = new ContentData(contentUrl, params.get("mimetype"), 0, "UTF-8");
            nodeService.setProperty(typeNodeRef, ContentModel.PROP_CONTENT, contentData);

            /*
             * Add required aspects. Now added in the aicoreModel.xml
             */
            // nodeService.addAspect(typeNodeRef, Constants.ASPECT_TITLED,
            // null);
            // nodeService.addAspect(typeNodeRef, Constants.ASPECT_AUTHOR,
            // null);
            /*
             * Add multilingual/translation aspect first seeing if a translation
             * already exists
             */
            NodeRef existingTranslationNodeRef = null;
            List<ChildAssociationRef> children = nodeService.getChildAssocs(
                    typeParentRef, ContentModel.ASSOC_CONTAINS,
                    RegexQNamePattern.MATCH_ALL);

            for (ChildAssociationRef ref : children) {
                NodeRef childRef = ref.getChildRef();

                /*
                 * Translations are applied for documents of the same type so
                 * skip if this child is not the same type as the one being
                 * created
                 */
                QName childType = nodeService.getType(childRef);
                if (childType.equals(typeQName) == false) {
                    continue;
                }

                if (multilingualContentService.isTranslation(childRef)) {
                    existingTranslationNodeRef = childRef;
                    break;
                }
            }

            /* Don't link asset level formatted edition content */
            boolean isAssetLevelFormattedEdition = (documentType!=null && (documentType.equals(
                    "Summary") || documentType.equals("Pinksheet")))
                    && edition.equals("Formatted Edition");

            if (existingTranslationNodeRef == null
                    || isAssetLevelFormattedEdition) {
                try {
					multilingualContentService.makeTranslation(typeNodeRef, locale);
				} catch (Throwable e) {
					logger.warn( "Translation issue: " + e.getMessage(), e);
				}
            } else {
                try {
					NodeRef pivotTranslationNodeRef = multilingualContentService
					        .getPivotTranslation(existingTranslationNodeRef);
					multilingualContentService.addTranslation(typeNodeRef,
					        pivotTranslationNodeRef, locale);
				} catch (Throwable e) {
					logger.warn( "Translation issue: " + e.getMessage(), e);
				}
            }

            /*
             * Force content transform rules as they are not triggering for some
             * reason ... See AIDOC-270
             */
            /*
             * Removed try-catch - otherwise webscript returns success even with transformation failure
             * MC 04/06/08 See AIDOC-498
             */
//            try {

                List<Rule> rules = ruleService.getRules(typeParentRef);
                for (Rule rule : rules) {
                    if (rule.getTitle().startsWith("Transform")) {
                        actionService.executeAction(rule.getAction(),
                                typeNodeRef, true, false);
                    }
                }

//            } catch (Exception e) {
//                logger.error("transformation failed", e);
//            }

        }

        /**
         * Create edition folder given an Ai Index folder reference and edition
         * name. Formatted Edition's are a simple folder create however all
         * other editions are created from an edition space template.
         * 
         * @param aiIndexFolderRef
         *          NodeRef for an existing Ai Index folder
         * @param edition
         *          Edition name corresponding to an edition space template
         * @param status
         *          WebscriptStatus object populated on error
         * @param model
         *          Webscript model object populated on error
         * 
         * @return NodeRef of new edition folder or null if failed to create
         * @throws FileNotFoundException
         * @throws FileExistsException
         */
        private NodeRef createEditionFolder(NodeRef aiIndexFolderRef,
                String edition) throws FileExistsException,
                FileNotFoundException {
	
	            FileInfo editionFileInfo = null;
	
	            if (edition.equals("Formatted Edition")) {
	            	
	            	
		        if (formattedTemplateRef == null)
		        	formattedTemplateRef = Util.findNode(FORMATTED_EDITION_TEMPLATE_PATH,
		                    searchService);
		        if (formattedTemplateRef == null) {
		            throw new IllegalArgumentException(
		                    "Template does not exist for Formated Edition Template");
	        }
	        
		        /*
	             * Create edition folder
	             */
	            editionFileInfo = fileFolderService.copy(formattedTemplateRef,
	                    aiIndexFolderRef, edition);

            } else {
                /*
                 * Look up template space and get ref
                 */
                
                if (templateRef == null)
                    templateRef = Util.findNode(EDITION_TEMPLATE_PATH,
                            searchService);
                if (templateRef == null) {
                    throw new IllegalArgumentException(
                            "Template does not exist for Edition Template");
                }

                /*
                 * Create edition folder
                 */
                editionFileInfo = fileFolderService.copy(templateRef,
                        aiIndexFolderRef, edition);
            }

            NodeRef editionFolderRef = editionFileInfo.getNodeRef();
            logger.debug("created edition folder with ref=" + editionFolderRef);
            return editionFolderRef;
        }
    }

    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public void setMultilingualContentService(
            MultilingualContentService multilingualContentService) {
        this.multilingualContentService = multilingualContentService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setRuleService(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setMimetypeService( MimetypeService mimetypeService )
    {
        this.mimetypeService = mimetypeService;
        
        logger.debug( "Setting mimetypeService: " + mimetypeService );
    }

}
