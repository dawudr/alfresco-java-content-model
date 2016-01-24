package org.amnesty.aidoc.feeder;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.model.ContentModel;
//import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.Asset;
import org.amnesty.aidoc.AssetManager;
import org.amnesty.aidoc.AssetRendition;
import org.amnesty.aidoc.Constants;
import org.amnesty.aidoc.Rendition;
import org.amnesty.aidoc.aicoreConfig;
import org.amnesty.aidoc.aicorePolicyAction;
import org.amnesty.aidoc.feeder.GsaFeedImpl.Meta;
import org.amnesty.aidoc.feeder.GsaFeedImpl.Record;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.alfresco.service.cmr.repository.Path;

/**
 * @author drahman
 * 
 */
public class Indexing implements aicorePolicyAction {

	// Dependencies
	private NodeService nodeService;
	private IndexingFeeder indexingFeeder;
	private boolean enabled;	

	private static final String ASSET_LIBRARY = "Asset Library";
	private static final String INDEXED_DOCUMENTS = "Indexed Documents";

	private static final Logger logger = Logger.getLogger(Indexing.class);

	public void doAddIndexing(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {

		NodeRef parentRef = nodeRef;
		boolean updateAllAsset = false;
		boolean securityClassModified = false;
		String removeCollection = null;

		Path nodePath = nodeService.getPath(nodeRef);
		String displayPath = org.alfresco.util.ISO9075.decode(nodePath.toString());
		if (displayPath.indexOf(ASSET_LIBRARY) != -1 && displayPath.indexOf(INDEXED_DOCUMENTS) != -1) {

			if (this.nodeService.getType(parentRef).equals(Constants.PROP_ASSET) || Constants.ASSET_TYPES.contains(nodeService.getType(parentRef).getLocalName())) {
				if (before.get(Constants.PROP_SECURITY_CLASS) != null && !before.get(Constants.PROP_SECURITY_CLASS).equals(after.get(Constants.PROP_SECURITY_CLASS))) {
					if (before.get(Constants.PROP_SECURITY_CLASS).equals("Public")) {
						removeCollection = "public";
					} else {
						removeCollection = "restricted";
					}
					securityClassModified = true;
				}
			}
			if (securityClassModified) {
				updateAllAsset = true;
			}

			while (!this.nodeService.getType(parentRef).equals(ContentModel.TYPE_STOREROOT)) {

				if (this.nodeService.getType(parentRef).equals(Constants.PROP_ASSET) && nodeService.getChildAssocs(parentRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL).size() > 0) {

					if (updateAllAsset) {
						/* ALL languages */
						logger.debug("ALL languages");
						Asset asset = AssetManager.getAsset(nodeService, parentRef, false, false, null);
						AiIndex aiIndex = AiIndex.parse(asset.getAiIndex());
						String aiIndexStr = asset.getDocumentYear() + "/" + aiIndex.getAiClass() + "/" + asset.getDocumentNo();

						if (securityClassModified) {
							logger.info("Calling deleteIndex for AIIndex[" + aiIndexStr + "]");
							indexingFeeder.deleteIndex(asset, parentRef, removeCollection);
						}

						String addCollection = null;
						if (removeCollection != null && removeCollection.equals("public"))
							addCollection = "restricted";
						// if publish date falls within range
						if (asset.getPublishDate() != null && asset.getPublishDate().before(new Date())) {
							logger.info("Calling addIndex for AIIndex[" + aiIndexStr + "]");							
							indexingFeeder.addIndex(asset, parentRef, addCollection);
						}
						break;
					} else {

						if (Constants.AI_INDEX_TYPES.contains(nodeService.getType(nodeRef).getLocalName()) && nodeService.getProperty(nodeRef, ContentModel.PROP_TITLE) != null) {

							/* Only selected language */
							String lang = (nodeService.getProperty(nodeRef, ContentModel.PROP_LOCALE) != null) ? nodeService.getProperty(nodeRef, ContentModel.PROP_LOCALE).toString() : "en";
							logger.debug("Only selected language:" + lang);
							Asset asset = AssetManager.getAsset(nodeService, parentRef, false, true, lang);
							AiIndex aiIndex = AiIndex.parse(asset.getAiIndex());
							String aiIndexStr = asset.getDocumentYear() + "/" + aiIndex.getAiClass() + "/" + asset.getDocumentNo() + "/" + lang;

							// if publish date falls within range
							if (asset.getPublishDate() != null && asset.getPublishDate().before(new Date())) {
								logger.info("Calling addIndex for AIIndex[" + aiIndexStr + "]");
								indexingFeeder.addIndex(asset, parentRef, null);
							}
							break;
						}
					}
				}
				parentRef = nodeService.getPrimaryParent(parentRef).getParentRef();
				logger.debug("Loop onUpdateProperties Node Name[" + (String) nodeService.getProperty(parentRef, ContentModel.PROP_NAME) + "] NodeType[" + this.nodeService.getType(parentRef).getLocalName() + "]");
			}
		}
	}

	public void doDeleteIndex(NodeRef nodeRef) {
		NodeRef parentRef = nodeRef;
		Path nodePath = nodeService.getPath(nodeRef);
		String displayPath = org.alfresco.util.ISO9075.decode(nodePath.toString());
		boolean isFullAsset = false;
		
		if (this.nodeService.getType(nodeRef).equals(Constants.PROP_ASSET))
		{
			isFullAsset = true;
		}
		
		if (displayPath.indexOf(ASSET_LIBRARY) != -1 && displayPath.indexOf(INDEXED_DOCUMENTS) != -1) {
			while (!this.nodeService.getType(parentRef).equals(ContentModel.TYPE_STOREROOT)) {
				// only check for renditions when we get the Asset and do not
				// continue if Asset folder is empty
				if (this.nodeService.getType(parentRef).equals(Constants.PROP_ASSET) && nodeService.getChildAssocs(parentRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL).size() > 0) {
					logger.debug("Calling assetmanager ASSET Name[" + (String) nodeService.getProperty(parentRef, ContentModel.PROP_NAME) + "] NodeType[" + this.nodeService.getType(parentRef).getLocalName() + "]");
					/* Only selected language */
					String lang = (nodeService.getProperty(nodeRef, ContentModel.PROP_LOCALE) != null) ? nodeService.getProperty(nodeRef, ContentModel.PROP_LOCALE).toString() : "en";
					logger.debug("Only selected language:" + lang);
					Asset asset = AssetManager.getAsset(nodeService, parentRef, false, true, lang);
					AiIndex aiIndex = AiIndex.parse(asset.getAiIndex());
					String aiIndexStr = asset.getDocumentYear() + "/" + aiIndex.getAiClass() + "/" + asset.getDocumentNo() + "/" + lang;

					logger.info("Calling deleteIndex for AIIndex[" + aiIndexStr + "]");
					indexingFeeder.deleteIndex(asset, parentRef, null);
					break;
				}
				parentRef = nodeService.getPrimaryParent(parentRef).getParentRef();
				logger.debug("Loop beforeDeleteNode Node Name[" + (String) nodeService.getProperty(parentRef, ContentModel.PROP_NAME) + "] NodeType[" + this.nodeService.getType(parentRef).getLocalName() + "]");
			}
		}
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public IndexingFeeder getIndexingFeeder() {
		return indexingFeeder;
	}

	public void setIndexingFeeder(IndexingFeeder indexingFeeder) {
		this.indexingFeeder = indexingFeeder;
	}

	/**
	 * removing a full asset
	 */
	
	@Override
	public void doBeforeDelete(NodeRef nodeRef) {
		this.doDeleteIndex(nodeRef);

	}

	@Override
	public void doUpdate(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		this.doAddIndexing(nodeRef, before, after);

	}


	/**
	 * removing something inside an asset
	 */
	@Override
	public void doAfterDelete(ChildAssociationRef arg0) {
		return;
		
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;		
	}


}