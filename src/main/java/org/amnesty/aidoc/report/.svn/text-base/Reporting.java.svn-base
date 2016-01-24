package org.amnesty.aidoc.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.namespace.QName;
import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.Asset;
import org.amnesty.aidoc.AssetManager;
import org.amnesty.aidoc.AssetRendition;
import org.amnesty.aidoc.Constants;
import org.amnesty.aidoc.Rendition;
import org.amnesty.aidoc.Util;
import org.amnesty.aidoc.aicoreConfig;
import org.amnesty.aidoc.aicorePolicyAction;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Reporting implements aicorePolicyAction {

	// Dependencies
	private PolicyComponent policyComponent;
	private NodeService nodeService;
	
	private boolean enabled;

	private static final Logger logger = Logger.getLogger(Reporting.class);
	private static final String ACTION_UPDATE = "UPDATE";
	private static final String ACTION_DELETE = "DELETE";
	private static final String ACTION_DELETE_INDEX = "DELETE_INDEX";

	private static final String ASSET_LIBRARY = "Asset Library";
	private static final String INDEXED_DOCUMENTS = "Indexed Documents";

	public void doAddReport(NodeRef nodeRef) {
		NodeRef parentRef = nodeRef;
		Path nodePath = nodeService.getPath(nodeRef);
		String displayPath = org.alfresco.util.ISO9075.decode(nodePath.toString());
		if (displayPath.indexOf(ASSET_LIBRARY) != -1 && displayPath.indexOf(INDEXED_DOCUMENTS) != -1) {
			while (!this.nodeService.getType(parentRef).equals(ContentModel.TYPE_STOREROOT)) {
				if (this.nodeService.getType(parentRef).equals(Constants.PROP_ASSET)) {

					Asset asset = AssetManager.getAsset(nodeService, parentRef, false, false, null);
					AssetRendition assetRendition = new AssetRendition(asset.getDocuments(), null);
					AssetReportImpl assetReport = convertToAssetReport(asset, assetRendition.getRenditions(), ACTION_UPDATE);
					sendUpdateReportNotification(assetReport);
					break;
				}
				parentRef = nodeService.getPrimaryParent(parentRef).getParentRef();
				logger.debug("Loop onUpdateProperties Node Name[" + (String) nodeService.getProperty(parentRef, ContentModel.PROP_NAME) + "] NodeType[" + this.nodeService.getType(parentRef).getLocalName() + "]");

				if (nodeService.getType(parentRef).equals(ContentModel.TYPE_FOLDER))
					break;
			}
		}
	}

	public void doDeleteReport(NodeRef nodeRef) {
		Path nodePath = nodeService.getPath(nodeRef);
		String displayPath = org.alfresco.util.ISO9075.decode(nodePath.toString());
		logger.debug("[doDeleteReport] Display path: "+displayPath);
		if (displayPath.indexOf(ASSET_LIBRARY) != -1 && displayPath.indexOf(INDEXED_DOCUMENTS) != -1) {
			if (this.nodeService.getType(nodeRef).equals(Constants.PROP_ASSET)) {
				logger.debug("Calling assetmanager ASSET Name[" + (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME) + "] NodeType[" + this.nodeService.getType(nodeRef).getLocalName() + "]");

				AiIndex aiIndex = Util.getAssetAiIndex(nodeService, nodeRef);
				sendDeleteReportNotification(aiIndex);
			}
			else
			{
				logger.debug("No asset type. Type: "+this.nodeService.getType(nodeRef));
			}
		}
	}
	
	public void doAfterDeleteReport(ChildAssociationRef arg0) {
		
		NodeRef nodeRef = arg0.getParentRef();
		
		Path nodePath = nodeService.getPath(nodeRef);
		String displayPath = org.alfresco.util.ISO9075.decode(nodePath.toString());
		logger.debug("[doAfterDeleteReport] Display path: "+displayPath);
		if (displayPath.indexOf(ASSET_LIBRARY) != -1 && displayPath.indexOf(INDEXED_DOCUMENTS) != -1) {

				while (!this.nodeService.getType(nodeRef).equals(ContentModel.TYPE_STOREROOT)) {
					if (this.nodeService.getType(nodeRef).equals(Constants.PROP_ASSET)) {
						Asset asset = AssetManager.getAsset(nodeService, nodeRef, false, false, null);
						AssetRendition assetRendition = new AssetRendition(asset.getDocuments(), null);
						AssetReportImpl assetReport = convertToAssetReport(asset, assetRendition.getRenditions(), ACTION_DELETE);
						sendUpdateReportNotification(assetReport);
						break;
					}
					nodeRef = nodeService.getPrimaryParent(nodeRef).getParentRef();

					if (nodeService.getType(nodeRef).equals(ContentModel.TYPE_FOLDER))
						break;
				}
		}
	}

	/*
	 * Sends feeds add Report Message
	 */
	public void sendUpdateReportNotification(AssetReportImpl assetReport) {
		logger.debug("Updating asset: " + assetReport.getAiIndex() + " action: " + assetReport.getAction());

		try {

			String indexName = StringUtils.remove(StringUtils.deleteWhitespace(assetReport.getAiIndex()), "/");
			File file = new File(aicoreConfig.REPORT_FOLDER + File.separatorChar + indexName + ".xml");
			FileOutputStream fos = new FileOutputStream(file);
			JAXBContext pContext = JAXBContext.newInstance(new Class[] { AssetReportImpl.class });
			Marshaller marshaller = pContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.valueOf(true));
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.marshal(assetReport, fos);

		} catch (JAXBException e) {
			logger.error("JAXBException " + e);
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException " + e.getMessage());
		} catch (Exception e) {
			logger.error("Exception " + e.getMessage());
		}

	}

	/*
	 * Send delete Report Message
	 */
	public void sendDeleteReportNotification(AiIndex aiIndex) {

		try {
			
			logger.debug("Deleting full asset: " + aiIndex);
			String indexName = StringUtils.remove(StringUtils.deleteWhitespace(aiIndex.toString()), "/");

			File file = new File(aicoreConfig.REPORT_FOLDER + File.separatorChar + indexName + ".xml");
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			fileWriter.write("<Asset>\n");
			fileWriter.write("<index>" + aiIndex.toString() + "</index>\n");
			fileWriter.write("<action>" + ACTION_DELETE_INDEX + "</action>\n");
			fileWriter.write("</Asset>\n");
			fileWriter.flush();
			fileWriter.close();

		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException " + e.getMessage());
		}

		catch (IOException e) {
			logger.error("IOException " + e.getMessage());
		} catch (Exception e) {
			logger.error("Exception " + e.getMessage());
		}
	}

	public AssetReportImpl convertToAssetReport(Asset asset, List<Rendition> renditions, String action) {

		AssetReportImpl assetReport = new AssetReportImpl();
		assetReport.setAction(action);
		assetReport.setAiIndex(asset.getAiIndex());
		assetReport.setLatinTitle(asset.getLatinTitle());
		assetReport.setModified(asset.getModified());
		assetReport.setLastModified(asset.getLastModified());
		assetReport.setCreated(asset.getCreated());
		assetReport.setPublishDate(asset.getPublishDate());
		assetReport.setSecurityClass(asset.getSecurityClass());
		assetReport.setCreated(asset.getCreated());
		assetReport.setType(asset.getType());
		if (asset.getAiIndexStatus() != null) {
			assetReport.setAiIndexStatus(asset.getAiIndexStatus());
		}
		if (asset.getRequestedBy() != null) {
			assetReport.setRequestedBy(asset.getRequestedBy());
		}
		if (asset.getNetwork() != null) {
			assetReport.setNetwork(asset.getNetwork());
		}
		if (asset.getNetworkNumber() != null) {
			assetReport.setNetworkNumber(asset.getNetworkNumber());
		}
		if (asset.getDescription() != null) {
			assetReport.setIndexNotes(asset.getDescription());
		}
		List<CategoryReportImpl> categoriesReport = new ArrayList<CategoryReportImpl>();
		for (String category : asset.getPrimaryRegions()) {
			CategoryReportImpl categoryReport = new CategoryReportImpl("region", category);
			categoriesReport.add(categoryReport);
		}
		for (String category : asset.getPrimaryIssues()) {
			CategoryReportImpl categoryReport = new CategoryReportImpl("issue", category);
			categoriesReport.add(categoryReport);
		}
		for (String category : asset.getPrimaryKeywords()) {
			CategoryReportImpl categoryReport = new CategoryReportImpl("keyword", category);
			categoriesReport.add(categoryReport);
		}
		for (String category : asset.getPrimaryCampaigns()) {
			CategoryReportImpl categoryReport = new CategoryReportImpl("campaign", category);
			categoriesReport.add(categoryReport);
		}
		assetReport.setCategories(categoriesReport);

		List<CategoryReportImpl> secCategoriesReport = new ArrayList<CategoryReportImpl>();
		for (String category : asset.getSecondaryRegions()) {
			CategoryReportImpl categoryReport = new CategoryReportImpl("region", category);
			secCategoriesReport.add(categoryReport);
		}
		for (String category : asset.getSecondaryIssues()) {
			CategoryReportImpl categoryReport = new CategoryReportImpl("issue", category);
			secCategoriesReport.add(categoryReport);
		}
		for (String category : asset.getSecondaryKeywords()) {
			CategoryReportImpl categoryReport = new CategoryReportImpl("keyword", category);
			secCategoriesReport.add(categoryReport);
		}
		for (String category : asset.getSecondaryCampaigns()) {
			CategoryReportImpl categoryReport = new CategoryReportImpl("campaign", category);
			secCategoriesReport.add(categoryReport);
		}
		assetReport.setSecondaryCategories(secCategoriesReport);

		Iterator<Rendition> renditionIt = renditions.iterator();

		while (renditionIt.hasNext()) {
			Rendition rendition = renditionIt.next();
			RenditionReportImpl renditionReport = new RenditionReportImpl();
			if (rendition.getLanguage() != null) {
				renditionReport.setLanguage(rendition.getLanguage());
			}
			if (rendition.getTitle() != null) {
				renditionReport.setTitle(rendition.getTitle());
			}
			if (rendition.getDescription() != null) {
				renditionReport.setDescription(rendition.getDescription());
			}
			renditionReport.setLastModified(rendition.getLastModified());
			assetReport.getRenditions().add(renditionReport);
		}
		return assetReport;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public PolicyComponent getPolicyComponent() {
		return policyComponent;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	@Override
	public void doBeforeDelete(NodeRef nodeRef) {
		this.doDeleteReport(nodeRef);
	}
	

	@Override
	public void doAfterDelete(ChildAssociationRef arg0) {
		this.doAfterDeleteReport(arg0);
		
	}

	@Override
	public void doUpdate(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		this.doAddReport(nodeRef);
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
