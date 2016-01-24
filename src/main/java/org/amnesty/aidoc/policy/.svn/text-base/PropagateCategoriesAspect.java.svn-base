package org.amnesty.aidoc.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.amnesty.aidoc.Constants;
import org.amnesty.aidoc.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropagateCategoriesAspect implements
        NodeServicePolicies.OnMoveNodePolicy,
        NodeServicePolicies.OnCreateNodePolicy,
        NodeServicePolicies.OnUpdatePropertiesPolicy {

    private static final String AICORE_CONTENT_TYPE = "http://www.amnesty.org/model/aicore/1.0";

    /* Aspect names */
    public static final QName ASPECT_PROPAGATECATEGORIES = QName.createQName(
            AICORE_CONTENT_TYPE, "propagateCategories");

    private static Log logger = LogFactory
            .getLog(PropagateCategoriesAspect.class);

    private PolicyComponent policyComponent;

    private NamespaceService namespaceService;

    private NodeService nodeService;

    private FileFolderService fileFolderService;

    private SearchService searchService;

    private ArrayList<NodeRef> pNodesToRemove;

    /**
     * Default constructor for bean construction
     */
    public PropagateCategoriesAspect() {
    }

    /**
     * Sets the policy component
     * 
     * @param policyComponent
     *          the policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    /**
     * Sets the node service
     * 
     * @param nodeService
     *          the node service
     */
    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public FileFolderService getFileFolderService() {
        return fileFolderService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public SearchService getSearchService() {
        return searchService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Spring initialise method used to register the policy behaviours
     */
    public void initialise() {

        logger.debug("Initializing AiIndexedAspect behavior");

        // Register the policy behaviours for parents that propagate categories

        this.policyComponent.bindClassBehaviour(QName.createQName(
                NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
                ASPECT_PROPAGATECATEGORIES, new JavaBehaviour(this,
                        "onUpdateProperties",
                        NotificationFrequency.TRANSACTION_COMMIT));

        // Register the policy behaviours for children that inherit categories

        this.policyComponent
                .bindClassBehaviour(QName.createQName(
                        NamespaceService.ALFRESCO_URI, "onMoveNode"),
                        ContentModel.ASPECT_GEN_CLASSIFIABLE,
                        new JavaBehaviour(this, "onMoveNode",
                                NotificationFrequency.TRANSACTION_COMMIT));

        this.policyComponent.bindClassBehaviour(QName.createQName(
                NamespaceService.ALFRESCO_URI, "onCreateNode"),
                ContentModel.ASPECT_GEN_CLASSIFIABLE, new JavaBehaviour(this,
                        "onCreateNode",
                        NotificationFrequency.TRANSACTION_COMMIT));

    }

    public void onUpdateProperties(NodeRef nodeRef,
            Map<QName, Serializable> before, Map<QName, Serializable> after) {
        // This is an Asset. If its categories change we want to search for all
        // classifiable general and overwrite their categories
        logger.debug("Calling onUpdateProperties");

        List<?> beforeCat = (List<?>) before
                .get(ContentModel.PROP_CATEGORIES);
        List<?> afterCat = (List<?>) after
                .get(ContentModel.PROP_CATEGORIES);

        if (afterCat == null) {
            logger.debug("No categories");
            return;
        }

        // Work out if categories have been added or removed
        boolean changed = false;
        if (beforeCat != null) {
            for (Object object : afterCat) {
                NodeRef node = (NodeRef) object;
                if (!beforeCat.contains(node))
                    changed = true;
            }
            for (Object object : beforeCat) {
                NodeRef node = (NodeRef) object;
                if (!afterCat.contains(node))
                    changed = true;
            }
        } else
            changed = true;

        if (!changed) {
            logger.debug("Not Changed");
            return;
        }

        // Remove categories like "Region", "Issue" that we don't want user's
        // setting directly
        // We can't change afterCat because of "ConcurrentModificationException"
        // so we clone it instead
        ArrayList<NodeRef> nodesToRemove = getRestrictedCategories();
        ArrayList<Object> newCat = new ArrayList<Object>();
        for (Object object : afterCat) {
            if (!nodesToRemove.contains(object))
                newCat.add(object);
        }

        // Update children
        ArrayList<NodeRef> childNodes = getChildCategorizedNodes(nodeRef);
        for (NodeRef childRef : childNodes) {
            nodeService.setProperty(childRef, ContentModel.PROP_CATEGORIES,
                    newCat);
        }

        // Add mapped synonyms
        // ArrayList<NodeRef> nodesToAdd = getMappedCategories(newCat);
        // for (Object node : nodesToAdd) {
        // newCat.add(node);
        // }

        logger.debug("Saving new properties");
        nodeService.setProperty(nodeRef, ContentModel.PROP_CATEGORIES, newCat);

    }

    public void onMoveNode(ChildAssociationRef oldChildAssocRef,
            ChildAssociationRef newChildAssocRef) {
        logger.debug("Calling onMoveNode");
        updateCategoriesFromAncestor(newChildAssocRef.getChildRef());
    }

    public void onCreateNode(ChildAssociationRef childAssocRef) {
        logger.debug("Calling onCreateNode");

        updateCategoriesFromAncestor(childAssocRef.getChildRef());
    }

    private void updateCategoriesFromAncestor(NodeRef nodeRef) {

        List<FileInfo> classFileInfo;
        try {
        	NodeRef rootNodeRef = Util.resolveIndexedDocsNode(nodeService, fileFolderService);
            classFileInfo = fileFolderService.getNamePath(rootNodeRef, nodeRef);
        } catch (FileNotFoundException e) {
            return;
        }

        for (FileInfo fileInfo : classFileInfo) {
            NodeRef node = fileInfo.getNodeRef();
            if (nodeService.hasAspect(node, ASPECT_PROPAGATECATEGORIES)) {
                nodeService.setProperty(nodeRef, ContentModel.PROP_CATEGORIES,
                        nodeService.getProperty(node,
                                ContentModel.PROP_CATEGORIES));
                logger.debug("Updated categories from ancestor");
            }
        }
    }

    private ArrayList<NodeRef> getRestrictedCategories() {

        if (pNodesToRemove == null) {
            // Process a list of keywords that should never be added and remove
            // them from the after list.
            ArrayList<String> restrictedCategories = new ArrayList<String>();
            restrictedCategories.add("Campaigns");
            restrictedCategories.add("Keywords");
            restrictedCategories.add("Issues");
            restrictedCategories.add("Regions");
            pNodesToRemove = getCategoriesFromString(restrictedCategories);
        }
        return pNodesToRemove;
    }

    private ArrayList<NodeRef> getChildCategorizedNodes(NodeRef nodeRef) {

        ArrayList<NodeRef> nodeRefs = new ArrayList<NodeRef>();

        String shortPath = nodeService.getPath(nodeRef).toPrefixString(
                namespaceService);

        String query = "ASPECT:\"{http://www.alfresco.org/model/content/1.0}generalclassifiable\" AND"
                + " PATH:\"" + shortPath + "//*\"   ";

        ResultSet templateResult = null;
        try {
            SearchParameters catSearchParameters = new SearchParameters();
            catSearchParameters.addStore(Constants.SEARCH_STORE);
            catSearchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);

            logger.debug("Searching for:" + query);
            catSearchParameters.setQuery(query);

            templateResult = searchService.query(catSearchParameters);

            logger.debug("Results:" + templateResult.length());

            for (ResultSetRow resultSetRow : templateResult) {
                nodeRefs.add(resultSetRow.getNodeRef());
            }
        } finally {
            if (templateResult != null)
                templateResult.close();
        }

        return nodeRefs;
    }

    private ArrayList<NodeRef> getCategoriesFromString(
            ArrayList<String> categories) {
        /*
         * Get and validate categories, obtaining NodeRefs at the same time
         */
        ArrayList<NodeRef> categoryNodeRefs = null;
        if (categories != null) {
            categoryNodeRefs = new ArrayList<NodeRef>(categories.size());
            for (String category : categories) {
                logger.debug("looking up category [" + category + "]");

                ResultSet templateResult = null;
                try {
                    SearchParameters catSearchParameters = new SearchParameters();
                    catSearchParameters.addStore(Constants.SEARCH_STORE);
                    catSearchParameters
                            .setLanguage(SearchService.LANGUAGE_LUCENE);
                    catSearchParameters
                            .setQuery("TYPE:\"cm:category\" AND @cm\\:name:\""
                                    + category + "\"");

                    templateResult = searchService.query(catSearchParameters);
                    if (templateResult.length() == 0) {
                        // Skip
                    } else
                        categoryNodeRefs.add(templateResult.getNodeRef(0));

                } finally {
                    if (templateResult != null)
                        templateResult.close();
                }

            }
            return categoryNodeRefs;
        }
        return null;
    }

}
