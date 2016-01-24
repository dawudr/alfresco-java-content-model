package org.amnesty.aidoc.webscript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;
import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.Constants;
import org.amnesty.aidoc.Util;

public class UpdateAssetWebScript extends BaseWebScript {

    /*
     * Spring service dependencies
     */
    protected NodeService nodeService;
    
    protected FileFolderService fileFolderService;
    
    protected CategoryService categoryService;

    public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
    
    public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.extensions.webscripts.DeclarativeWebScript#executeImpl(org.springframework.extensions.webscripts.WebScriptRequest,
     *      org.springframework.extensions.webscripts.WebScriptResponse)
     */
    @Override
    protected Map<String, Object> executeAiImpl(WebScriptRequest req,
            Status status) {

        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);

        /*
         * Get and validate AiIndex parameters
         */
        String aiIndexStr = req.getParameter("aiIndex");
        String title = req.getParameter("title");
        String invalidatedStr = req.getParameter("invalidated");
        String validityNotes = req.getParameter("validityNotes");
        String publishDate = req.getParameter("publishDate");
        String securityClass = req.getParameter("securityClass");
        String aiIndexType = req.getParameter("aiIndexType");
        String originator = req.getParameter("originator");
        String network = req.getParameter("network");
        String networkNumber = req.getParameter("networkNumber");
        String withdrawn = req.getParameter("withdrawn");

        Boolean invalidated = null;
        if (invalidatedStr != null && !invalidatedStr.equals("")) {
            invalidated = new Boolean(invalidatedStr);
        }

        logger.debug("Updating aiIndex=" + aiIndexStr);
        logger.debug("title=" + title);
        logger.debug("invalidated=" + invalidated);
        logger.debug("validityNotes=" + validityNotes);
        logger.debug("publishDate=" + publishDate);
        logger.debug("securityClass=" + securityClass);
        logger.debug("aiIndexType=" + aiIndexType);
        logger.debug("originator=" + originator);
        logger.debug("network=" + network);
        logger.debug("networkNumber=" + networkNumber);
        logger.debug("withdrawn=" + withdrawn);

        ParameterCheck.mandatoryString("aiIndex", aiIndexStr);

        /*
         * Validate the security class if it was given
         */
        if (securityClass != null
                && securityClass.equals("") == false
                && Constants.SECURITY_CLASS_TYPES.contains(securityClass) == false) {
            handleError(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid security class " + securityClass
                            + ". Must be one of "
                            + Constants.SECURITY_CLASS_TYPES.toString(), null,
                    model, status);
            return model;

        }

        /*
         * Lookup asset node, return error if not found
         */
        AiIndex aiIndex = AiIndex.parse(aiIndexStr);
        
        NodeRef assetRef = null;
        try
        {
        	 assetRef = Util.resolveAssetNode(
             		nodeService, fileFolderService, aiIndex.getYear(), aiIndex.getAiClass(), aiIndex.getDocnum());
        }
        catch (FileNotFoundException e)
        {
        	handleError(HttpServletResponse.SC_NOT_FOUND,
                    "Cannot update asset. No such asset as " + aiIndexStr , null, model, status);
            return model;
        }

        /*
         * Get and validate categories, obtaining NodeRefs at the same time
         */
        WebScriptServletRequest servletReq = (WebScriptServletRequest) req;
        HttpServletRequest httpReq = servletReq.getHttpServletRequest();

        String[] categories = httpReq.getParameterValues("category");
        String[] secondaryCategories = httpReq.getParameterValues("secondaryCategory");

        List<String> categoriesList = null;
        if (categories != null) {
            categoriesList = Arrays.asList(categories);
        } else {
            categoriesList = new ArrayList<String>();
        }

        List<String> secCategoriesList = null;
        if (secondaryCategories != null) {
            secCategoriesList = Arrays.asList(secondaryCategories);
        } else {
            secCategoriesList = new ArrayList<String>();
        }

        // Primary
        ArrayList<NodeRef> categoryNodeRefs = null;
        
        if (categoriesList != null) {
            categoryNodeRefs = new ArrayList<NodeRef>(categoriesList.size());
            for (String category : categoriesList) {
                logger.debug("looking up category [" + category + "]");

                NodeRef categoryNodeRef = Util.findCategory( category, categoryService, nodeService );
                if (categoryNodeRef != null)
                    categoryNodeRefs.add(categoryNodeRef);
                else {
                    String msg = "Category [" + category
                            + "] does not exist";
                    logger.warn(msg);

                    invalidated = true;
                    validityNotes = validityNotes + msg
                            + "<br/>&nbsp;&nbsp;";
                }

            }
        }

        // Secondary
            ArrayList<NodeRef> secCategoryNodeRefs = null;
            if (secCategoriesList != null) {
                secCategoryNodeRefs = new ArrayList<NodeRef>(secCategoriesList.size());
                for (String category : secCategoriesList) {
                    logger.debug("looking up secondary category [" + category
                            + "]");

                    NodeRef categoryNodeRef = Util.findCategory( category, categoryService, nodeService );
                    if (categoryNodeRef != null)
                        secCategoryNodeRefs.add(categoryNodeRef);
                    else {
                        String msg = "Secondary category [" + category
                                + "] does not exist";
                        logger.warn(msg);

                        invalidated = true;
                        validityNotes = validityNotes + msg
                                + "<br/>&nbsp;&nbsp;";
                    }

                }
            }

        /*
         * Update asset
         */
        if (title != null && !title.equals("")) {
            nodeService.setProperty(assetRef, Constants.PROP_AI_TITLE, title);
        }

        /*
         * Apply categories
         */
        if (categoryNodeRefs != null) {
            nodeService.setProperty(assetRef, ContentModel.PROP_CATEGORIES,
                    categoryNodeRefs);
        }

        if (secCategoryNodeRefs != null) {
            nodeService.setProperty(assetRef, Constants.PROP_SEC_CATEGORIES,
                    secCategoryNodeRefs);
        }
        
        if (invalidated != null || validityNotes != null) {
            Util.invalidateAsset(nodeService, assetRef, validityNotes);
        }

        if (securityClass != null) {
            nodeService.setProperty(assetRef, Constants.PROP_SECURITY_CLASS,
                    securityClass);
        }
        
        if (aiIndexType != null) {
            nodeService.setProperty(assetRef, Constants.PROP_AI_INDEX_TYPE,
            		aiIndexType);

            logger.debug("Asset aiIndexType updated: "+aiIndexType);
            QName typeQName = QName.createQName("{" + Constants.AICORE_MODEL
                    + "}" + aiIndexType);
            
            List<ChildAssociationRef> documentNodes = nodeService.getChildAssocs(assetRef);
            for(ChildAssociationRef documentNode : documentNodes) {
            	NodeRef documentNodeRef = documentNode.getChildRef();
            	
            	logger.debug("Node Name: "+nodeService.getProperty(documentNodeRef, ContentModel.PROP_NAME)+
            			" Type: "+nodeService.getType(documentNodeRef));
            	
            	if(Constants.AI_INDEX_TYPES.contains(nodeService.getType(documentNodeRef).getLocalName()))
            	{
            		nodeService.setType(documentNodeRef, typeQName);
            		logger.debug(nodeService.getProperty(documentNodeRef, ContentModel.PROP_NAME)+ " type updated");
            	}
            	else if(nodeService.getType(documentNodeRef).equals(Constants.TYPE_EDITION))
            	{
            		List<ChildAssociationRef> subDocumentNodes = nodeService.getChildAssocs(documentNodeRef);
            		for(ChildAssociationRef subDocumentNode : subDocumentNodes) {				                
						NodeRef subDocumentNodeRef = subDocumentNode.getChildRef();
						
						logger.debug("Node Name: "+nodeService.getProperty(subDocumentNodeRef, ContentModel.PROP_NAME)+
		            			" Type: "+nodeService.getType(subDocumentNodeRef));
						
						if(Constants.AI_INDEX_TYPES.contains(nodeService.getType(subDocumentNodeRef).getLocalName()))
		            	{
							nodeService.setType(subDocumentNodeRef, typeQName);
							logger.debug(nodeService.getProperty(subDocumentNodeRef, ContentModel.PROP_NAME)+ " type updated");
		            	}
						else if(nodeService.getType(subDocumentNodeRef).equals(ContentModel.TYPE_FOLDER))
						{
							List<ChildAssociationRef> subSubDocumentNodes = nodeService.getChildAssocs(subDocumentNodeRef);
							
							for(ChildAssociationRef subSubDocumentNode : subSubDocumentNodes) {	
								NodeRef subSubDocumentNodeRef = subSubDocumentNode.getChildRef();
								
								logger.debug("Node Name: "+nodeService.getProperty(subSubDocumentNodeRef, ContentModel.PROP_NAME)+
				            			" Type: "+nodeService.getType(subSubDocumentNodeRef));
								
								if(Constants.AI_INDEX_TYPES.contains(nodeService.getType(subSubDocumentNodeRef).getLocalName()))
				            	{
									nodeService.setType(subSubDocumentNodeRef, typeQName);
									logger.debug(nodeService.getProperty(subSubDocumentNodeRef, ContentModel.PROP_NAME)+ " type updated");
				            	}
							}
							
						}
            		
            		}
            		
            	}
            
            }
        }
        
        if (originator != null) {
            nodeService.setProperty(assetRef, ContentModel.PROP_ORIGINATOR,
                    originator);
        }
        
        if (network != null) {
            nodeService.setProperty(assetRef, Constants.PROP_NETWORK,
            		network);
        }
        
        if (networkNumber != null) {
            nodeService.setProperty(assetRef, Constants.PROP_NETWORK_NUMBER,
            		networkNumber);
        }
        
        if (withdrawn != null && withdrawn.equals("true")){
            
            if (!nodeService.hasAspect(assetRef, Constants.ASPECT_WITHDRAWABLE))
                nodeService.addAspect(assetRef, Constants.ASPECT_WITHDRAWABLE, null);
            
            nodeService.setProperty(assetRef,
                    Constants.PROP_WITHDRAWN, true);
        }
        
        if (withdrawn != null && withdrawn.equals("false")){
            
            if (!nodeService.hasAspect(assetRef, Constants.ASPECT_WITHDRAWABLE))
                nodeService.addAspect(assetRef, Constants.ASPECT_WITHDRAWABLE, null);
            
            nodeService.setProperty(assetRef,
                    Constants.PROP_WITHDRAWN, false);
        }

        if (publishDate != null) {
            
            if ( publishDate.trim().length() == 0 ) {
                logger.debug( "nullifying publishDate" );
                nodeService.setProperty(assetRef, Constants.PROP_PUBLISH_DATE,
                        null);
              
            }
            else {
              nodeService.setProperty(assetRef, Constants.PROP_PUBLISH_DATE,
                    publishDate);
            }
        }

        logger.debug("asset updated");

        model.put("code", HttpServletResponse.SC_OK);
        model.put("message", "SUCCESS");
        return model;
    }

}