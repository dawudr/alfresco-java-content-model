package org.amnesty.aidoc.webscript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.util.ParameterCheck;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;
import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.Constants;
import org.amnesty.aidoc.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.icu.util.Calendar;

public class CreateAssetWebScript extends BaseWebScript {

    protected static NodeRef assetTemplateRef = null;

    protected FileFolderService fileFolderService;

    protected NodeService nodeService;

    protected SearchService searchService;

    protected CategoryService categoryService;
    
    public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }


    @Override
    protected Map<String, Object> executeAiImpl(WebScriptRequest req,
            Status status) {
        
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        
        String[] paramNames = req.getParameterNames();
        
        for ( String paramName: paramNames )
        {
            String[] values = req.getParameterValues( paramName );
            
            for ( String value : values )
            {
                logger.debug( "Parameter [" + paramName + "] = [" +  value + "]" );
            }
        }

        if ( req.isGuest() ) 
        {
            logger.debug( "Running as guest" );
        }
        else
        {
            logger.debug( "Not running as guest" );
        }
        
        String aiClass = req.getParameter("class");
        String type = req.getParameter("type");
        String year = req.getParameter("year");
        String docnum = req.getParameter("docnum");
        String title = req.getParameter("title");
        String publishDate = req.getParameter("publishDate");
        String aiIndexType = req.getParameter("aiIndexType");
        String originator = req.getParameter("originator");
        String network = req.getParameter("network");
        String networkNumber = req.getParameter("networkNumber");
        String securityClass = req.getParameter("securityClass");
        String withdrawn = req.getParameter("withdrawn");
        
        WebScriptServletRequest servletReq = (WebScriptServletRequest) req;
        HttpServletRequest httpReq = servletReq.getHttpServletRequest();
        String[] categories = httpReq.getParameterValues("category");
        String[] secondaryCategories = httpReq
                .getParameterValues("secondaryCategory");

        if (req.isGuest() == true){
            handleError(HttpServletResponse.SC_UNAUTHORIZED, "Guest user is unauthorized", null,
                    model, status);
            return model;
        }
        
        AiIndex aiIndex = null;
        try {
            CreateAssetService service = new CreateAssetService(
                    fileFolderService, nodeService, searchService);
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
            aiIndex = service.createAsset(year, aiClass, docnum, type, title,
                    publishDate, aiIndexType, securityClass, originator, network, networkNumber, withdrawn, categoriesList,
                    secCategoriesList);
        } catch (IllegalArgumentException e) {
            handleError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), e,
                    model, status);
            return model;
        } catch (AccessDeniedException ade) {
            handleError(HttpServletResponse.SC_UNAUTHORIZED, ade.getMessage(), ade,
                    model, status);
            return model;
        } catch (Exception e) {
            handleError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e
                    .getMessage(), e, model, status);
            return model;
        }

        logger.debug("finished with success");

        return populateSuccessModel(model, aiIndex, title);
    }

    private Map<String, Object> populateSuccessModel(Map<String, Object> model,
            AiIndex aiIndex, String title) {

        model.put("aiIndex", aiIndex);
        model.put("title", title);

        model.put("code", HttpServletResponse.SC_OK);
        model.put("message", "SUCCESS");

        return model;
    }

    /**
     * This service will create an asset folder for an Aidoc AiIndex.
     * 
     * @author chatch
     */
    public class CreateAssetService {

        private Log logger = LogFactory.getLog(this.getClass());

        protected FileFolderService fileFolderService;

        protected NodeService nodeService;

        protected SearchService searchService;

        protected Repository repository;
        
        public CreateAssetService(FileFolderService fileFolderService,
                NodeService nodeService, SearchService searchService) {
            this.fileFolderService = fileFolderService;
            this.nodeService = nodeService;
            this.searchService = searchService;
        }

        public AiIndex createAsset(String year, String aiClass, String docnum,
                String type, String title, String publishDate,
                String aiIndexType, String securityClass, String originator, String network, String networkNumber, 
                String withdrawn, List<String> categoriesList,
                List<String> secCategoriesList) throws FileExistsException,
                FileNotFoundException {

            logger.debug("class=" + aiClass);
            logger.debug("type=" + type);
            logger.debug("year=" + year);
            logger.debug("docnum=" + docnum);
            logger.debug("title=" + title);
            logger.debug("publishDate=" + publishDate);
            logger.debug("securityClass=" + securityClass);
            logger.debug("withdrawn=" + withdrawn);
            logger.debug("aiIndexType=" + aiIndexType);
            logger.debug("originator=" + originator);
            logger.debug("network=" + network);
            logger.debug("networkNumber=" + networkNumber);
            
            ParameterCheck.mandatoryString("title", title);

            if (aiClass == null && type == null) {
                throw new IllegalArgumentException(
                        "Must provide one of the parameters type or class");
            }

            if (aiClass == null) {
                aiClass = Constants.ASSETTYPE_TO_AI_CLASS_MAP.get(type);
                if (aiClass == null) {
                    throw new IllegalArgumentException("Invalid type " + type
                            + ". Must be one of "
                            + Constants.ASSETTYPE_TO_AI_CLASS_MAP.keySet());
                } else {
                    securityClass = "Public";
                }
            }

            if (year == null) {
                int yearInt = Calendar.getInstance().get(Calendar.YEAR);
                year = Integer.valueOf(yearInt).toString();
            }

            if (securityClass == null || securityClass.equals("")) {
                securityClass = "Internal";
            } else if (Constants.SECURITY_CLASS_TYPES.contains(securityClass) == false) {
                throw new IllegalArgumentException("Invalid security class "
                        + securityClass + ". Must be one of "
                        + Constants.SECURITY_CLASS_TYPES.toString());
            }
                
            
            if ( aiIndexType == null )
            {
            	aiIndexType = "Unknown";
            	
            } else if (Constants.AI_INDEX_TYPES.contains(aiIndexType) == false) {
                throw new IllegalArgumentException("Invalid AI index type "
                        + aiIndexType + ". Must be one of "
                        + Constants.AI_INDEX_TYPES.toString());
   
            	
            }
            
            /*
             * If all index parts were passed and asset already exists log a
             * warning and return success
             */
            NodeRef aiIndexNode = null;
            if (year != null && aiClass != null && docnum != null) {
            	
            	try
            	{
            	 aiIndexNode = Util.resolveAssetNode(nodeService, fileFolderService, year, aiClass, docnum);
            	}
            	catch (FileNotFoundException e) {
            		logger.debug("asset folder doesn't exists");
				}
            	
                if (aiIndexNode != null) {
                	logger
                    .debug("asset folder already exists so returning success");
                	return new AiIndex(aiClass, docnum, year);
                }
            }

            /*
             * Get and validate categories, obtaining NodeRefs at the same time
             */
            boolean invalidated = false;
            String validityNotes = "";
            
            // Primary
            ArrayList<NodeRef> categoryNodeRefs = null;
            if (categoriesList != null) {
                categoryNodeRefs = new ArrayList<NodeRef>(categoriesList.size());
                for (String category : categoriesList) {
                    logger.debug("looking up category [" + category + "]");

                    NodeRef categoryNodeRef = Util.findCategory(category, categoryService, nodeService);
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

                    NodeRef categoryNodeRef = Util.findCategory(category, categoryService, nodeService);
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
             * Get Indexed Documents root node, look for year and class and
             * create them if they don't exist.
             */
            
            NodeRef yearNode = Util.resolveYearNode(nodeService, fileFolderService, year);
            
            logger.debug( "Found yearNode: " + yearNode );
            
            if (yearNode == null) {
            	
            	NodeRef indexedAssetsRoot = Util.resolveIndexedDocsNode(nodeService, fileFolderService);
            	
                FileInfo yearFileInfo = fileFolderService.create(
                        indexedAssetsRoot, year, ContentModel.TYPE_FOLDER);
                yearNode = yearFileInfo.getNodeRef();
            }

            NodeRef classNodeRef = nodeService.getChildByName(yearNode,
                    ContentModel.ASSOC_CONTAINS, aiClass);
            if (classNodeRef == null) {
                FileInfo classFileInfo = fileFolderService.create(yearNode,
                        aiClass, ContentModel.TYPE_FOLDER);
                classNodeRef = classFileInfo.getNodeRef();
            }

            /*
             * Get NodeRef for the Asset Template if not already set
             */
            if (assetTemplateRef == null) {
                String templatePath = Constants.SPACE_TEMPLATES_XPATH
                        + "/cm:Asset_x0020_Template";
                assetTemplateRef = Util.findNode(templatePath, searchService);
            }

            /*
             * Create the asset
             */
            if (docnum == null || docnum.equals("")) {
                docnum = nextDocumentNo(classNodeRef);
            }

            /*
             * Create asset folder
             */
            FileInfo assetFileInfo = fileFolderService.copy(assetTemplateRef,
                    classNodeRef, docnum);
            NodeRef assetFolderRef = assetFileInfo.getNodeRef();
            logger.info("assetFolderRef=" + assetFolderRef.toString());

            nodeService.setProperty(assetFolderRef, Constants.PROP_AI_TITLE,
                    title);

            if (( publishDate != null ) && ( publishDate.trim().length() > 0 )) {
                nodeService.setProperty(assetFolderRef,
                        Constants.PROP_PUBLISH_DATE, publishDate);
            }

            if (securityClass != null) {
                nodeService.setProperty(assetFolderRef,
                        Constants.PROP_SECURITY_CLASS, securityClass);
            }
            
            if (originator != null) {
                nodeService.setProperty(assetFolderRef,
                		ContentModel.PROP_ORIGINATOR, originator);
            }
            
            if (network != null) {
                nodeService.setProperty(assetFolderRef,
                		Constants.PROP_NETWORK, network);
            }
            
            if (networkNumber != null) {
                nodeService.setProperty(assetFolderRef,
                		Constants.PROP_NETWORK_NUMBER, networkNumber);
            }
            
            if (withdrawn != null && withdrawn.equals("true")){
                nodeService.setProperty(assetFolderRef,
                        Constants.PROP_WITHDRAWN, true);
            }

            if (withdrawn != null && withdrawn.equals("false")){
                nodeService.setProperty(assetFolderRef,
                        Constants.PROP_WITHDRAWN, false);
            }

            if (aiIndexType != null ){
                nodeService.setProperty(assetFolderRef,
                        Constants.PROP_AI_INDEX_TYPE, aiIndexType );
            }

            /*
             * Set classification aspect and apply categories
             */
            nodeService.addAspect(assetFolderRef, ContentModel.ASPECT_GEN_CLASSIFIABLE, null);
            if (categoryNodeRefs != null) {
                nodeService.setProperty(assetFolderRef, ContentModel.PROP_CATEGORIES,
                        categoryNodeRefs);
            }

            nodeService.addAspect(assetFolderRef, Constants.ASPECT_SECONDARYCLASSIFIABLE, null);
            if (secCategoryNodeRefs != null) {
                nodeService.setProperty(assetFolderRef, Constants.PROP_SEC_CATEGORIES,
                        secCategoryNodeRefs);
            }

            /*
             * Set validity notes if any categories were invalid above
             */
            if (invalidated == true) {
                Util
                        .invalidateAsset(nodeService, assetFolderRef,
                                validityNotes);
            }

            AiIndex aiIndex = new AiIndex(aiClass, docnum, year);
            logger.debug("created " + aiIndex);
            return aiIndex;
        }

        private String nextDocumentNo(NodeRef classSpaceRef) {

            List<FileInfo> folders = fileFolderService
                    .listFolders(classSpaceRef);

            Integer high = 0;

            for (FileInfo info : folders) {
                String name = info.getName();
                try {
                    Integer num = Integer.parseInt(name);
                    if (num > high)
                        high = num;
                } catch (Exception e) {
                    // Ignore
                }

            }

            Integer newNum = high + 1;
            String docNo = newNum.toString();
            if (docNo.length() == 1)
                docNo = "00" + docNo;
            else if (docNo.length() == 2)
                docNo = "0" + docNo;

            return docNo;

        }
    }

}