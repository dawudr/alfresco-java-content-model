package org.amnesty.aidoc.feeder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.Asset;
import org.amnesty.aidoc.AssetManager;
import org.amnesty.aidoc.AssetRendition;
import org.amnesty.aidoc.Constants;
import org.amnesty.aidoc.Rendition;
import org.amnesty.aidoc.Util;
import org.amnesty.aidoc.aicoreConfig;
import org.apache.log4j.Logger;

public class IndexingFeedActionExecuter extends ActionExecuterAbstractBase {
	
	private NodeService nodeService;
	private FileFolderService fileFolderService;
	private ContentService contentService;
	private IndexingFeedState indexingFeedState;
	private IndexingFeeder indexingFeeder;

	public final static String NAME = "indexingfeeder";
	private boolean publicScope = false;
	
	private static int BASE_LEVEL = 0;
	private static int DOCUMENT_YEAR_LEVEL = 1;
	private static int CLASS_CODE_LEVEL = 2;
	
	private static String CLASS_CODE_STRICT_REGX = "[A-Z]{3}[0-9]{2}";
	private static String DOCUMENT_YEAR_STRICT_REGX = "[1-2]{1}[0-9]{3}";
	private static final Logger logger = Logger.getLogger(IndexingFeedActionExecuter.class);

	
	@Override
	protected void executeImpl(Action arg0, NodeRef arg1) {
		
	    String yearRegex = DOCUMENT_YEAR_STRICT_REGX;
	    String classCodeRegex = CLASS_CODE_STRICT_REGX;
	    Pattern yearPattern = Pattern.compile(yearRegex);
	    Pattern classCodePattern = Pattern.compile(classCodeRegex);
	    Matcher m = null;	  
		  
		try {
			NodeRef baseNode = Util.resolveIndexedDocsNode(nodeService, fileFolderService);	  
		    Iterator<ChildAssociationRef> childAssocIt = nodeService.getChildAssocs(baseNode).iterator();		    
		    
		    while(childAssocIt.hasNext())
		    {
		    	NodeRef child = childAssocIt.next().getChildRef();		    	
		    	String childName = nodeService.getProperty(child, ContentModel.PROP_NAME).toString();
		    	logger.debug("[Child name - year node] "+childName);

		      //ignore children with no children unless they are AI Index folders and showEmptyIndexes is set     
		        Iterator<ChildAssociationRef> grandChildAssocIt = nodeService.getChildAssocs(child).iterator();	        
		        while(grandChildAssocIt.hasNext())
		        {
		        	NodeRef grandChild = grandChildAssocIt.next().getChildRef();	        	
		        	String grandChildName = nodeService.getProperty(grandChild, ContentModel.PROP_NAME).toString();
		        	//logger.debug("[Grandchild name - classcode node] "+grandChildName);

		        		Iterator<ChildAssociationRef> grandGrandChildAssocIt = nodeService.getChildAssocs(grandChild).iterator();
		        		while(grandGrandChildAssocIt.hasNext()) {
				        	NodeRef grandGrandChild = grandGrandChildAssocIt.next().getChildRef();
				        	// Check in Asset nodes only
		        			if(nodeService.getType(grandGrandChild).equals(Constants.PROP_ASSET) && nodeService.getChildAssocs(grandGrandChild, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL).size() > 0) {
					        	String grandGrandChildName = nodeService.getProperty(grandGrandChild, ContentModel.PROP_NAME).toString();
					        	
					        	if(nodeService.hasAspect(grandGrandChild, Constants.ASPECT_FEEDPUBLISHABLE) && (nodeService.getProperty(grandGrandChild, Constants.PROP_FEED_PUBLISHED_STATUS) != null) && !(nodeService.getProperty(grandGrandChild, Constants.PROP_FEED_PUBLISHED_STATUS).equals(""))) {	
						        	String feedPublishedState = nodeService.getProperty(grandGrandChild, Constants.PROP_FEED_PUBLISHED_STATUS).toString().trim();
						        	logger.debug("[GrandGrandchild name - asset node]" + childName + "/" + grandChildName + "/" + grandGrandChildName + " Current FeedPublishedState:" + feedPublishedState);
		
						        	Asset asset = AssetManager.getAsset(nodeService, grandGrandChild, false, false, null);			        	
						        	
						        	if(feedPublishedState.equals(IndexingFeedState.INDEX_FEED_STATUS_PENDING)) {
						        		// Process Assets in Pending state
						        		AssetRendition assetRendition = new AssetRendition(contentService, asset.getDocuments(), null);
							    		ArrayList<Rendition> renditions = assetRendition.getRenditions();
		
							    		if (renditions.size() > 0) {
							    			for (Rendition rendition : renditions) {
							    				if (rendition.getEffectiveDocumentCount() > 0) {
							    					AiIndex aiIndex = AiIndex.parse(asset.getAiIndex());		    
							    					String url = asset.getDocumentYear() + "/" + aiIndex.getAiClass() + "/" + asset.getDocumentNo() + "/" + rendition.getLanguage();
							    					String securityClass = (asset.getSecurityClass().equals("Internal") ? "restricted" : "index");
							    					url = aicoreConfig.GSA_RECORD_URL_PREFIX + "/" + securityClass + "/" + url;
							    					// If one the rendition URLs is not found in GSA then leave the Failed flag set
							    					if(!indexingFeedState.updateIndexFeedStatusProperty(url, grandGrandChild)) {
											        	logger.debug("Feed not found in GSA: " + url);					    						
							    						break;
							    					}
							    				}
							    			}
							    		} else {
							    			// If document is deleted and no renditions left
					    					indexingFeedState.updateIndexFeedStatusProperty(null, grandGrandChild);
							    		}
						        	} else if (feedPublishedState.equals(IndexingFeedState.INDEX_FEED_STATUS_PENDING_REMOVAL)) {
						        		// ^ If Asset's document is pending removal, sychronise the other remaining document
						        		indexingFeeder.addIndex(asset, grandGrandChild, null);						        		
						        	} else if(!feedPublishedState.equals(IndexingFeedState.INDEX_FEED_STATUS_COMPLETED) || !feedPublishedState.equals(IndexingFeedState.INDEX_FEED_STATUS_REMOVED)) {
							        	// ^ Ignore if Asset status is Completed or Removed
						        		if(feedPublishedState.equals(IndexingFeedState.INDEX_FEED_STATUS_FAILED) || feedPublishedState.equals(IndexingFeedState.INDEX_FEED_STATUS_FAILED_IN_ERROR)) {
							        		// Feed has failed so attempt to resend
							        		indexingFeeder.addIndex(asset, grandGrandChild, null);
							        	}
							        }
					        	}
		        			}
				        }
		        }
		    }
		} catch (FileNotFoundException e) {
			logger.error("Unable able to resolve Indexed Documents folder:" + e.getMessage());
		}		    		
	}
	
	
	
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {

	}
	
	public ContentService getContentService() {
		return contentService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	
	public FileFolderService getFileFolderService() {
		return fileFolderService;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public IndexingFeedState getIndexingFeedState() {
		return indexingFeedState;
	}

	public void setIndexingFeedState(IndexingFeedState indexingFeedState) {
		this.indexingFeedState = indexingFeedState;
	}

	public IndexingFeeder getIndexingFeeder() {
		return indexingFeeder;
	}

	public void setIndexingFeeder(IndexingFeeder indexingFeeder) {
		this.indexingFeeder = indexingFeeder;
	}
}