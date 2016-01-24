package org.amnesty.aidoc.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.amnesty.aidoc.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AiIndexedAspect implements NodeServicePolicies.OnMoveNodePolicy,
        NodeServicePolicies.OnCreateNodePolicy,
        NodeServicePolicies.OnUpdateNodePolicy {

    public static final String RE_AIINDEX = "([a-zA-Z]{3}) ([0-9]{2})/([0-9]{3})/([0-9]{4})";

    private static final String AICORE_CONTENT_TYPE = "http://www.amnesty.org/model/aicore/1.0";

    /* Aspect names */
    public static final QName ASPECT_AIINDEXED = QName.createQName(
            AICORE_CONTENT_TYPE, "aiIndexed");

    /* Property names */
    public static final QName ASPECT_AIINDEX = QName.createQName(
            AICORE_CONTENT_TYPE, "aiIndex");

    public static final StoreRef STORE_REF_ARCHIVE_SPACESSTORE = new StoreRef("archive", "SpacesStore");
    
    private static Log logger = LogFactory.getLog(AiIndexedAspect.class);

    private PolicyComponent policyComponent;

    private NodeService nodeService;

    private FileFolderService fileFolderService;

    private SearchService searchService;

    /**
     * Default constructor for bean construction
     */
    public AiIndexedAspect() {
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

        logger.debug("Initialising AiIndexedAspect behaviour");

        // Register the policy behaviours
        this.policyComponent.bindClassBehaviour(QName.createQName(
                NamespaceService.ALFRESCO_URI, "onMoveNode"), ASPECT_AIINDEXED,
                new JavaBehaviour(this, "onMoveNode",
                        NotificationFrequency.TRANSACTION_COMMIT));

        // Register the policy behaviours
        this.policyComponent.bindClassBehaviour(QName.createQName(
                NamespaceService.ALFRESCO_URI, "onCreateNode"),
                ASPECT_AIINDEXED, new JavaBehaviour(this, "onCreateNode",
                        NotificationFrequency.TRANSACTION_COMMIT));

        // Register the policy behaviours
        this.policyComponent.bindClassBehaviour(QName.createQName(
                NamespaceService.ALFRESCO_URI, "onUpdateNode"),
                ASPECT_AIINDEXED, new JavaBehaviour(this, "onUpdateNode",
                        NotificationFrequency.TRANSACTION_COMMIT));

    }

    public void onMoveNode(ChildAssociationRef oldChildAssocRef,
            ChildAssociationRef newChildAssocRef) {
      
      if ( newChildAssocRef.getParentRef().getStoreRef().equals( STORE_REF_ARCHIVE_SPACESSTORE ))
      {
        return;
      } 
      
      logger.debug("Calling onMoveNode");
        
 //       logger.info( "newChildAssocRef.getParentRef().getStoreRef().toString()=" + newChildAssocRef.getParentRef().getStoreRef().toString() );
 //       logger.info( "StoreRef.STORE_REF_ARCHIVE_SPACESSTORE=" + StoreRef.STORE_REF_ARCHIVE_SPACESSTORE );
        
        setIndexOnNode(newChildAssocRef.getChildRef());
    }

    public void onCreateNode(ChildAssociationRef childAssocRef) {
        
      if ( childAssocRef.getParentRef().getStoreRef().equals( STORE_REF_ARCHIVE_SPACESSTORE ))
      {
        return;
      }        
      
      logger.debug("Calling onCreateNode");

 //       logger.info( "childAssocRef.getParentRef().getStoreRef().toString()=" + childAssocRef.getParentRef().getStoreRef().toString() );
 //       logger.info( "StoreRef.STORE_REF_ARCHIVE_SPACESSTORE=" + StoreRef.STORE_REF_ARCHIVE_SPACESSTORE );
        setIndexOnNode(childAssocRef.getChildRef());
    }

    public void onUpdateNode(NodeRef nodeRef) {
      
      if ( nodeRef.getStoreRef().equals( STORE_REF_ARCHIVE_SPACESSTORE ))
      {
        return;
      }
      logger.debug("Calling onUpdateNode");
      
      // do not index Working Copy of a Document      
      if((!nodeService.exists(nodeRef) ||
    		  (nodeService.getProperty(nodeRef, ContentModel.PROP_WORKING_COPY_MODE) != null && 
    		  nodeService.getProperty(nodeRef, ContentModel.PROP_WORKING_COPY_MODE).equals("onlineEditing")))) {
    	  return;
      }
      setIndexOnNode(nodeRef);
    }

    private void setIndexOnNode(NodeRef nodeRef) {
      
      if ( nodeRef.getStoreRef().equals( STORE_REF_ARCHIVE_SPACESSTORE ))
      {
        return;
      }
      
        // Get current and suggested indexes
        String aiIndex = Util.getAiIndexFromPath(nodeRef, searchService,
                fileFolderService, RE_AIINDEX);
        String currIndex = (String) nodeService.getProperty(nodeRef,
                ASPECT_AIINDEX);

        // Handle an add
        if (aiIndex != null && !aiIndex.equals(currIndex)) {
            nodeService.setProperty(nodeRef, ASPECT_AIINDEX, aiIndex);
            logger.debug("Added index to node: " + aiIndex);
        }
        else if (aiIndex == null && currIndex != null) {
            logger.debug("Removed aiIndex from node");
            nodeService.setProperty(nodeRef, ASPECT_AIINDEX, null);
        }
        else
            logger.debug("AiIndex is correct. No action taken");
    }

}
