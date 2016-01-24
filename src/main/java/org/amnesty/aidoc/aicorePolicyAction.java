package org.amnesty.aidoc;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

public interface aicorePolicyAction {

	boolean enabled = true;
	
	public void doUpdate(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after);
	
	public void doBeforeDelete(NodeRef nodeRef);
	
	public void doAfterDelete(ChildAssociationRef arg0);
	
	public boolean isEnabled();
	
	public void setEnabled(boolean enabled);
}
