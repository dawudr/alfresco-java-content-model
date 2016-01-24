package org.amnesty.aidoc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.amnesty.aidoc.feeder.Indexing;
import org.amnesty.aidoc.report.Reporting;
import org.apache.log4j.Logger;

public class AiNodeServicePolicies implements NodeServicePolicies.OnUpdatePropertiesPolicy, NodeServicePolicies.BeforeDeleteNodePolicy, 
NodeServicePolicies.OnDeleteNodePolicy {

	// Dependencies


	private PolicyComponent policyComponent;
	private Reporting reportingAction;
	private Indexing indexingAction;

	// Behaviours
	private Behaviour beforeDeleteNode;
	private Behaviour onDeleteNode;
	private Behaviour onUpdateProperties;

	private static final Logger logger = Logger.getLogger(AiNodeServicePolicies.class);

	private List<aicorePolicyAction> actions = new ArrayList<aicorePolicyAction>();

	public void init() {
		if (logger.isDebugEnabled())
			logger.debug("Initializing AiNodeServicePolicies behaviors");

		// Create behaviours
		this.beforeDeleteNode = new JavaBehaviour(this, "beforeDeleteNode", NotificationFrequency.FIRST_EVENT);
		this.onDeleteNode = new JavaBehaviour(this, "onDeleteNode", NotificationFrequency.FIRST_EVENT);
		this.onUpdateProperties = new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT);

		// Bind behaviours to node policies
		//this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "beforeDeleteNode"), ContentModel.TYPE_CONTENT, this.beforeDeleteNode);
		
		this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "beforeDeleteNode"), Constants.PROP_ASSET, this.beforeDeleteNode);
		this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "beforeDeleteNode"), Constants.TYPE_AICORE_DOCUMENT, this.beforeDeleteNode);
		this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onDeleteNode"), Constants.TYPE_AICORE_DOCUMENT, this.onDeleteNode);
		this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onDeleteNode"), Constants.TYPE_EDITION, this.onDeleteNode);
		
		this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"), Constants.TYPE_AICORE_DOCUMENT, this.onUpdateProperties);
		this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"), Constants.PROP_ASSET, this.onUpdateProperties);
	}

	/*
	 * Event listener for Security Class Property changes of Asset node and when
	 * Document Content is added or properties updated.
	 * Updating asset or aidocument
	 */
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {

		// prevent the Search feed status update to node triggering off again
		// if ((before.get(Constants.PROP_SEARCH_FEED_STATUS) == null) ||
		// (before.get(Constants.PROP_SEARCH_FEED_STATUS) != null &&
		// before.get(Constants.PROP_SEARCH_FEED_STATUS).equals(after.get(Constants.PROP_SEARCH_FEED_STATUS))))
		// {

		Iterator<aicorePolicyAction> actionIt = actions.iterator();
		while (actionIt.hasNext()) {
			aicorePolicyAction action = actionIt.next();
			if (action.isEnabled()) {
				action.doUpdate(nodeRef, before, after);
			}
		}
	}

	/*
	 * Event listeners for when document is deleted.
	 * Removing a full asset
	 */
	public void beforeDeleteNode(NodeRef nodeRef) {

		Iterator<aicorePolicyAction> actionIt = actions.iterator();
		while (actionIt.hasNext()) {
			aicorePolicyAction action = actionIt.next();
			if (action.isEnabled()) {
				action.doBeforeDelete(nodeRef);
			}
		}
	}
	
	/**
	 * removing an edition or a aidocument
	 */
	@Override
	public void onDeleteNode(ChildAssociationRef arg0, boolean arg1) {
		logger.debug("Inside onDeleteNode ChildAssociationRef[" + arg0 + "]");

		Iterator<aicorePolicyAction> actionIt = actions.iterator();
		while (actionIt.hasNext()) {
			aicorePolicyAction action = actionIt.next();
			if (action.isEnabled()) {
				action.doAfterDelete(arg0);
			}
		}
		
	}

	public PolicyComponent getPolicyComponent() {
		return policyComponent;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public Reporting getReportingAction() {
		return reportingAction;
	}

	public void setReportingAction(Reporting reportingAction) {
		this.reportingAction = reportingAction;
	}

	public Indexing getIndexingAction() {
		return indexingAction;
	}

	public void setIndexingAction(Indexing indexingAction) {
		this.indexingAction = indexingAction;
	}

	public List<aicorePolicyAction> getActions() {
		return actions;
	}

	public void setActions(List<aicorePolicyAction> actions) {
		this.actions = actions;
	}

}
