package org.amnesty.aidoc.feeder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.Asset;
import org.amnesty.aidoc.AssetRendition;
import org.amnesty.aidoc.Rendition;
import org.amnesty.aidoc.aicoreConfig;
import org.amnesty.aidoc.feeder.GsaFeedImpl.Meta;
import org.amnesty.aidoc.feeder.GsaFeedImpl.Record;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

public class IndexingFeeder {

	private static final Logger logger = Logger.getLogger(Indexing.class);
	private ContentService contentService;
	private IndexingFeedState indexingFeedState;
	
	/*
	 * Sends feeds to GSA containing collection of urls to add or update.
	 */
	public void addIndex(Asset asset, NodeRef parentRef, String collection) {
		logger.debug("Inside addIndex");
		int result = 0;

		// Create a new DocumentRendition object and save everything to here
		AssetRendition assetRendition = new AssetRendition(contentService, asset.getDocuments(), null);

		Date lastModified = asset.getLastModified();
		ArrayList<Rendition> renditions = assetRendition.getRenditions();
		logger.debug("Inside addIndex Number of renditions:  " + renditions.size());

		if (renditions.size() > 0) {

			// ArrayList<RecordItem> recordList = new ArrayList<RecordItem>();
			ArrayList<Record> recordList = new ArrayList<Record>();

			for (Rendition rendition : renditions) {
				if (rendition.getEffectiveDocumentCount() > 0) {
					AiIndex aiIndex = AiIndex.parse(asset.getAiIndex());

					ArrayList<Meta> metaDataList = getMetaDataArrayList(asset, rendition);
					String url = asset.getDocumentYear() + "/" + aiIndex.getAiClass() + "/" + asset.getDocumentNo() + "/" + rendition.getLanguage();
					String securityClass = (asset.getSecurityClass().equals("Internal") ? "restricted" : "index");
					url = (collection != null && collection.equals("restricted")) ? aicoreConfig.GSA_RECORD_URL_PREFIX + "/" + collection + "/" + url : aicoreConfig.GSA_RECORD_URL_PREFIX + "/" + securityClass + "/" + url;
					String displayurl = url;
					ContentData contentData = rendition.getContent();
					String mimetype = (contentData != null && rendition.getContent().getMimetype() != null) ? rendition.getContent().getMimetype() : "text/html";
					String description = StringEscapeUtils.escapeHtml(rendition.getDescription());
					String title = StringEscapeUtils.escapeHtml(rendition.getTitle());
					String action = ContentFeedBuilder.GSAFEED_INPUT_RECORD_ADD;
					boolean lock = false;
					String encoding = null;

					String content = null;
					StringBuilder contentToHtmlStr = new StringBuilder("<html><title>");
					contentToHtmlStr.append(title);
					contentToHtmlStr.append("</title><body>");
					contentToHtmlStr.append(description != null && !description.equals("") ? description : title);
					contentToHtmlStr.append("</body></html>");
					content = contentToHtmlStr.toString();

					Record record = new Record();
					record.setUrl(url);
					record.setDisplayUrl(displayurl);
					record.setAction(action);
					record.setLock(lock);
					record.setMimetype(mimetype);
					record.setLastModified(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z").format(lastModified));
					record.setContent(content);
					record.setEncoding(encoding);
					record.setMeta(metaDataList);
					recordList.add(record);

					logger.info("ADDING INDEX: url[" + url + "] title[" + title + "] content[" + content + "]");
				}
			}
			if (recordList.size() > 0) {
				indexingFeedState.initBackLogCount();
				GsaFeedergateClient gsaFeedergateClient = new GsaFeedergateClient();
				result = gsaFeedergateClient.addContent(recordList);
				indexingFeedState.updateIndexFeedStatusProperty(result, parentRef, ContentFeedBuilder.GSAFEED_INPUT_RECORD_ADD);				
			}
		} else {
			// No documents so set Feed Published to Removed
			indexingFeedState.updateIndexFeedStatusProperty(null, parentRef);				
		}
	}

	/*
	 * Send delete feed to GSA
	 */
	public void deleteIndex(Asset asset, NodeRef parentRef, String collection) {
		int result = 0;
		AssetRendition assetRendition = new AssetRendition(contentService, asset.getDocuments(), null);
		ArrayList<Rendition> renditions = assetRendition.getRenditions();
		ArrayList<Record> recordList = new ArrayList<Record>();

		logger.debug("Inside deleteIndex Number of renditions:  " + renditions.size());
		if (renditions.size() > 0) {
			for (Rendition rendition : renditions) {
				if (rendition.getEffectiveDocumentCount() > 0) {
					// Create a new DocumentRendition object and save everything
					// to
					AiIndex aiIndex = AiIndex.parse(asset.getAiIndex());
					String url = asset.getDocumentYear() + "/" + aiIndex.getAiClass() + "/" + asset.getDocumentNo() + "/" + rendition.getLanguage();
					String securityClass = (asset.getSecurityClass().equals("Internal") ? "restricted" : "index");
					url = (collection != null && collection.equals("restricted")) ? aicoreConfig.GSA_RECORD_URL_PREFIX + "/" + collection + "/" + url : (collection != null && collection.equals("public") ? aicoreConfig.GSA_RECORD_URL_PREFIX + "/index/" + url : aicoreConfig.GSA_RECORD_URL_PREFIX + "/" + securityClass + "/" + url);
					String displayurl = url;
					ContentData contentData = rendition.getContent();
					String mimetype = (contentData != null && rendition.getContent().getMimetype() != null) ? rendition.getContent().getMimetype() : "text/html";
					String title = rendition.getTitle();
					String action = ContentFeedBuilder.GSAFEED_INPUT_RECORD_DELETE;
					boolean lock = new Boolean(aicoreConfig.GSA_FEEDER_DOCUMENT_LOCK);
					Record record = new Record();
					record.setUrl(url);
					record.setDisplayUrl(displayurl);
					record.setAction(action);
					record.setLock(lock);
					record.setMimetype(mimetype);
					recordList.add(record);
					logger.info("DELETING INDEX: url[" + url + "] title[" + title + "]");
				}
			}

			if (recordList.size() > 0) {
				indexingFeedState.initBackLogCount();				
				GsaFeedergateClient gsaFeedergateClient = new GsaFeedergateClient();
				result = gsaFeedergateClient.deleteContent(recordList);
				indexingFeedState.updateIndexFeedStatusProperty(result, parentRef, ContentFeedBuilder.GSAFEED_INPUT_RECORD_DELETE);						
			}
		}
	}

	/*
	 * Add metadata list to GSA feed
	 */
	private ArrayList<Meta> getMetaDataArrayList(Asset asset, Rendition rendition) {
		ArrayList<Meta> metaDataList = new ArrayList<Meta>();
		if (rendition.getTitle() != null && !rendition.getTitle().isEmpty())
			metaDataList.add(new Meta("AI-title", StringEscapeUtils.escapeHtml(rendition.getTitle())));
		if (rendition.getLanguage() != null && !rendition.getLanguage().isEmpty())
			metaDataList.add(new Meta("AI-language", rendition.getLanguage()));
		if (asset.getAiIndex() != null && !asset.getAiIndex().isEmpty())
			metaDataList.add(new Meta("AI-index", asset.getAiIndex()));
		if (asset.getClassCode() != null && !asset.getClassCode().isEmpty())
			metaDataList.add(new Meta("AI-class", asset.getClassCode()));
		if (asset.getSubclass() != null && !asset.getSubclass().isEmpty())
			metaDataList.add(new Meta("AI-subclass", asset.getSubclass()));
		if (asset.getDocumentNo() != null && !asset.getDocumentNo().isEmpty())
			metaDataList.add(new Meta("AI-document-no", asset.getDocumentNo()));
		if (asset.getDocumentYear() != null && !asset.getDocumentYear().isEmpty())
			metaDataList.add(new Meta("AI-document-year", asset.getDocumentYear()));
		SimpleDateFormat targetDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (asset.getSecurityClass() != null && !asset.getSecurityClass().isEmpty())
			metaDataList.add(new Meta("AI-security-class", asset.getSecurityClass()));
		if (asset.getPublishDate() != null) {
			metaDataList.add(new Meta("AI-published", targetDateFormatter.format((asset.getPublishDate()))));
		}
		if (asset.getOriginator() != null && !asset.getOriginator().isEmpty())
			metaDataList.add(new Meta("AI-originator", asset.getOriginator()));
		if (asset.getPublicationStatus() != null && !asset.getPublicationStatus().isEmpty())
			metaDataList.add(new Meta("AI-publication-status", asset.getPublicationStatus()));
		if (asset.getType() != null && !asset.getType().isEmpty())
			metaDataList.add(new Meta("AI-type", asset.getType()));

		if (asset.getPrimaryRegions() != null && asset.getPrimaryRegions().size() > 0) {
			HashSet<String> primaryRegions = asset.getPrimaryRegions();
			for (String primaryRegionsString : primaryRegions) {
				metaDataList.add(new Meta("AI-category-primary-region", primaryRegionsString));
			}
		}

		if (asset.getSecondaryRegions() != null && asset.getSecondaryRegions().size() > 0) {
			HashSet<String> secondaryRegions = asset.getSecondaryRegions();
			for (String secondaryRegionString : secondaryRegions) {
				metaDataList.add(new Meta("AI-category-secondary-region", secondaryRegionString));
			}
		}

		if (asset.getAllRegions() != null && asset.getAllRegions().size() > 0) {
			HashSet<String> allRegions = asset.getAllPathRegions();
			for (String allRegionsString : allRegions) {
				metaDataList.add(new Meta("AI-category-region", allRegionsString));
			}
		}

		if (asset.getPrimaryKeywords() != null && asset.getPrimaryKeywords().size() > 0) {
			HashSet<String> primaryKeywords = asset.getPrimaryKeywords();
			for (String primaryKeywordsString : primaryKeywords) {
				metaDataList.add(new Meta("AI-category-primary-keyword", primaryKeywordsString));
			}
		}

		if (asset.getSecondaryKeywords() != null && asset.getSecondaryKeywords().size() > 0) {
			HashSet<String> secondaryKeywords = asset.getSecondaryKeywords();
			for (String secCategoriesString : secondaryKeywords) {
				metaDataList.add(new Meta("AI-category-secondary-keyword", secCategoriesString));
			}
		}

		if (asset.getPrimaryCampaigns() != null && asset.getPrimaryCampaigns().size() > 0) {
			HashSet<String> primaryCampaigns = asset.getPrimaryCampaigns();
			for (String secCategoriesString : primaryCampaigns) {
				metaDataList.add(new Meta("AI-category-primary-campaign", secCategoriesString));
			}
		}

		if (asset.getSecondaryCampaigns() != null && asset.getSecondaryCampaigns().size() > 0) {
			HashSet<String> secondaryCampaigns = asset.getSecondaryCampaigns();
			for (String secCategoriesString : secondaryCampaigns) {
				metaDataList.add(new Meta("AI-category-secondary-campaign", secCategoriesString));
			}
		}

		if (asset.getAllKeywords() != null && asset.getAllKeywords().size() > 0) {
			HashSet<String> allKeywords = asset.getAllKeywords();
			for (String allKeywordsString : allKeywords) {
				metaDataList.add(new Meta("AI-category-keyword", allKeywordsString));
			}
		}

		if (asset.getAllCampaigns() != null && asset.getAllCampaigns().size() > 0) {
			HashSet<String> allCampaigns = asset.getAllCampaigns();
			for (String allCampaignsString : allCampaigns) {
				metaDataList.add(new Meta("AI-category-campaign", allCampaignsString));
			}
		}

		if (asset.getAllIssues() != null && asset.getAllIssues().size() > 0) {
			HashSet<String> allIssues = asset.getAllIssues();
			for (String allIssuesString : allIssues) {
				metaDataList.add(new Meta("AI-category-issue", allIssuesString));
			}
		}

		if (asset.getLastModified() != null) {
			metaDataList.add(new Meta("AI-last-modified", targetDateFormatter.format((asset.getLastModified()))));
		}
		if (asset.getCreated() != null) {
			metaDataList.add(new Meta("AI-created", targetDateFormatter.format(asset.getCreated())));
		}
		Iterator<String> problemsIt = asset.getProblems().iterator();
		while (problemsIt.hasNext()) {
			metaDataList.add(new Meta("AI-content-problem", problemsIt.next()));
		}
		return metaDataList;
	}
	
	public ContentService getContentService() {
		return contentService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}
	
	public IndexingFeedState getIndexingFeedState() {
		return indexingFeedState;
	}

	public void setIndexingFeedState(IndexingFeedState indexingFeedState) {
		this.indexingFeedState = indexingFeedState;
	}
	
}
