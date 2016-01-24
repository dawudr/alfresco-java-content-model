package org.amnesty.aidoc.policy;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SecurityClassifiableAspect implements
        NodeServicePolicies.OnUpdatePropertiesPolicy {

    private static final String SEC_CLASS_EVERYONEANDGUEST = "Public";

    private static final String SEC_CLASS_EVERYONE = "Internal";

    private static final String AICORE_CONTENT_TYPE = "http://www.amnesty.org/model/aicore/1.0";

    /* Aspect names */
    public static final QName ASPECT_SECURITYCLASSIFIABLE = QName.createQName(
            AICORE_CONTENT_TYPE, "securityClassifiable");

    /* Property names */
    public static final QName ASPECT_SECURITYCLASS = QName.createQName(
            AICORE_CONTENT_TYPE, "securityClass");

    private static Log logger = LogFactory
            .getLog(SecurityClassifiableAspect.class);

    private PolicyComponent policyComponent;

    private NodeService nodeService;

    private PermissionService permissionService;

    /**
     * Default constructor for bean construction
     */
    public SecurityClassifiableAspect() {
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

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * Spring initilaise method used to register the policy behaviours
     */
    public void initialise() {

        logger.debug("Initializing SecurityClassifiableAspect behavior");

        // Register the policy behaviours
        this.policyComponent.bindClassBehaviour(QName.createQName(
                NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
                ASPECT_SECURITYCLASSIFIABLE, new JavaBehaviour(this,
                        "onUpdateProperties",
                        NotificationFrequency.TRANSACTION_COMMIT));

    }

    public void onUpdateProperties(NodeRef nodeRef,
            Map<QName, Serializable> before, Map<QName, Serializable> after) {
       
        String beforeSec = (String) before
        .get(ASPECT_SECURITYCLASS);
        
        String afterSec = (String) after
        .get(ASPECT_SECURITYCLASS);

        if (afterSec == null)
            return;
        
        if (beforeSec != null)
            if (beforeSec.equalsIgnoreCase(afterSec))
                return;

        logger.debug("Properties updated");

        String secClass = (String) nodeService.getProperty(nodeRef,
                ASPECT_SECURITYCLASS);
        
        if (secClass.equalsIgnoreCase(SEC_CLASS_EVERYONE)) {
            permissionService.clearPermission(nodeRef,
                    PermissionService.GUEST_AUTHORITY);
            permissionService.setPermission(nodeRef,
                    PermissionService.ALL_AUTHORITIES,
                    PermissionService.CONSUMER, true);
            logger.debug("Disallowing guest permission to node");
        }

        if (secClass.equalsIgnoreCase(SEC_CLASS_EVERYONEANDGUEST)) {
            permissionService.setPermission(nodeRef,
                    PermissionService.GUEST_AUTHORITY,
                    PermissionService.CONSUMER, true);
            permissionService.setPermission(nodeRef,
                    PermissionService.ALL_AUTHORITIES,
                    PermissionService.CONSUMER, true);
            logger.debug("Allowing guest permission to node");
        }

    }
}
