package org.amnesty.aidoc.webscript;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import net.sf.acegisecurity.BadCredentialsException;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.Asset;
import org.amnesty.aidoc.AssetManager;
import org.amnesty.aidoc.AssetRendition;
import org.amnesty.aidoc.Constants;
import org.amnesty.aidoc.Rendition;
import org.amnesty.aidoc.Util;
import org.amnesty.aidoc.aicoreConfig;
import org.amnesty.aidoc.feeder.ContentFeedBuilder;
import org.amnesty.aidoc.feeder.GsaFeedergateClient;
import org.amnesty.aidoc.feeder.IndexingFeedState;
import org.amnesty.aidoc.feeder.GsaFeedImpl.Meta;
import org.amnesty.aidoc.feeder.GsaFeedImpl.Record;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

public class AssetFeedWebScript extends AbstractWebScript {

	private NodeService nodeService;

	private FileFolderService fileFolderService;
	private ContentService contentService;

	// access public published documents only?
	private boolean publicScope = false;

	private static int BASE_LEVEL = 0;
	private static int DOCUMENT_YEAR_LEVEL = 1;
	private static int CLASS_CODE_LEVEL = 2;

	private static String CLASS_CODE_STRICT_REGX = "[A-Z]{3}[0-9]{2}";
	private static String DOCUMENT_YEAR_STRICT_REGX = "[1-2]{1}[0-9]{3}";

	private static final Log logger = LogFactory.getLog(AssetFeedWebScript.class);	
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res)
			throws IOException {
		
		Map<String, String> args = req.getServiceMatch().getTemplateVars();
		
		String yearRegex = DOCUMENT_YEAR_STRICT_REGX;
		String classCodeRegex = CLASS_CODE_STRICT_REGX;
		Pattern yearPattern = Pattern.compile(yearRegex);
		Pattern classCodePattern = Pattern.compile(classCodeRegex);
		int feedSentCount = 0;
		Matcher m = null;

		try {
			String documentYear = Util.readDocumentYear(args.get("document_year"), false);
			String classCode = Util.readClassCode(args.get("class_code"), false);
			logger.debug("AssetFeedWebScript document_year:" + args.get("document_year") + " class_code:" + args.get("class_code"));

			boolean guest = req.isGuest();

			if ((guest == true) && (publicScope == false)) {
				throw new AccessDeniedException("Guest access is disallowed");
			}

			int crawlLevel = ((documentYear == null) ? BASE_LEVEL : ((classCode == null) ? DOCUMENT_YEAR_LEVEL : CLASS_CODE_LEVEL));

			NodeRef baseNode = Util.resolveAssetNode(nodeService, fileFolderService, documentYear, classCode, null);
			if (crawlLevel == BASE_LEVEL) {
				throw new IllegalArgumentException("Base level is not supported by performance");
			}

			if (baseNode == null) {
				throw new IllegalArgumentException("Null base node");
			}

			m = yearPattern.matcher(documentYear);
			if (!m.matches()) {
				throw new IllegalArgumentException("Wrong year format");
			}

			if (classCode != null) {
				m = classCodePattern.matcher(classCode);
				if (!m.matches()) {
					throw new IllegalArgumentException("Wrong class code format");
				}
			}
			
			
			String state = (aicoreConfig.GSA_FEEDER_DELAY_SECONDS >= 0) ? "Processing feeds.... \n" : "Feeds disabled, \"gsa.feeder.delay.seconds\" property must be >= 0 \n";
			res.getOutputStream().write(state.getBytes());
			res.getOutputStream().flush();																																		
			
			
			// Get list of nodes
			Iterator<ChildAssociationRef> childAssocIt = nodeService.getChildAssocs(baseNode).iterator();

			while (childAssocIt.hasNext()) {
				// Get childs of Classcode nodes
				NodeRef child = childAssocIt.next().getChildRef();
				String childName = nodeService.getProperty(child, ContentModel.PROP_NAME).toString();
				logger.debug("Child name [" + childName + "]");
						
				if (crawlLevel == CLASS_CODE_LEVEL) {					
					
					// Fetch Assets in CLASS CODE level folders				
					m = yearPattern.matcher(childName);
					//if (m.matches()) {
					
						// check if type Asset and fetch documents
						if (nodeService.getType(child).equals(Constants.PROP_ASSET)) {
							logger.debug("Crawling by CLASSCODE [" + classCode + "]");
							
							// Create and send Asset Feed
							ArrayList<Record> feeds = fetchRenditionsFromAssetNode(child);	
							if (feeds != null && feeds.size() >0) {
								for(Record feed: feeds) {
								feedSentCount++;
								String statusStr = feed.getUrl() + sendAssetFeed(feed) + "\n";
								res.getOutputStream().write(statusStr.getBytes());
								res.getOutputStream().flush();																																		
								}
							}
						} // asset node test
					//}
				} else {					
					
					// Fetch CLASSCODES within YEAR level folders 
					Iterator<ChildAssociationRef> grandChildAssocIt = nodeService.getChildAssocs(child).iterator();

					while (grandChildAssocIt.hasNext()) {
						
						// Fetch Assets in CLASS CODE level folders
						NodeRef grandChild = grandChildAssocIt.next().getChildRef();					
						String grandChildName = nodeService.getProperty(grandChild, ContentModel.PROP_NAME).toString();
						logger.debug("Grandchild name [" + grandChildName + "]");
						if (crawlLevel == DOCUMENT_YEAR_LEVEL) {
							
							// check if type Asset and fetch documents
							if (nodeService.getType(grandChild).equals(Constants.PROP_ASSET)) {
								
								m = classCodePattern.matcher(childName);
								if (m.matches()) {
									logger.debug("Crawling by YEAR [" + documentYear + "]");
									
									// Create and send Asset Feed									
									ArrayList<Record> feeds = fetchRenditionsFromAssetNode(grandChild);	
									if (feeds != null && feeds.size() >0) {
										for(Record feed: feeds) {
										feedSentCount++;
										String statusStr = feed.getUrl() + "   -> " +   sendAssetFeed(feed) + "\n";
										res.getOutputStream().write(statusStr.getBytes());
										res.getOutputStream().flush();																																		
										}
									}
								} else {
									logger.debug("No matching Classcode pattern [child name] " + childName);
								}
							} // asset node test
						}
					}
				}
			}
			res.getOutputStream().write(("Number of documents found.... " + feedSentCount).getBytes());
			res.getOutputStream().close();
		}
		catch (AccessDeniedException e )
		{
			logger.error(e.getMessage());
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		catch (BadCredentialsException e )
		{
			logger.error(e.getMessage());
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		 catch (IllegalArgumentException e) 
		 { 
			 logger.error(e.getMessage());
			 res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}catch (FileNotFoundException e) {
			
	    	logger.error(e.getMessage());
	        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		catch (InterruptedException e )
		{
			logger.error(e.getMessage());
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	public String sendAssetFeed(Record feed) throws InterruptedException  {
		
	ArrayList <Record> recordList = new ArrayList<Record>();
	recordList.add(feed);
	
	String status = "";
	if (recordList.size() > 0 && aicoreConfig.GSA_FEEDER_DELAY_SECONDS >= 0) {
		GsaFeedergateClient gsaFeedergateClient = new GsaFeedergateClient();
		int result = gsaFeedergateClient.addContent(recordList);
		IndexingFeedState indexingFeedState = new IndexingFeedState();
		status = indexingFeedState.getIndexFeedStatusMessage(result, ContentFeedBuilder.GSAFEED_INPUT_RECORD_ADD);
		Thread.sleep(aicoreConfig.GSA_FEEDER_DELAY_SECONDS * 1000);
	}
	
	return status;	
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

	public ContentService getContentService() {
		return contentService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public boolean isPublicScope() {
		return publicScope;
	}

	public void setPublicScope(boolean publicScope) {
		this.publicScope = publicScope;
	}
	
	public ArrayList<Record> fetchRenditionsFromAssetNode(NodeRef AssetNode) {
		
		Asset asset = AssetManager.getAsset(nodeService, AssetNode, false, false, null);
		ArrayList<Record> recordList = new ArrayList<Record>();
		
		if (asset != null && asset.getAiIndex() != null) {
			logger.debug("Processing asset.... " + asset.getAiIndex());		
		}
		
		// Create a new DocumentRendition object and save everything to here
		AssetRendition assetRendition = new AssetRendition(contentService, asset.getDocuments(), null);
		ArrayList<Rendition> renditions = assetRendition.getRenditions();
		
		if (renditions.size() > 0) {
			for (Rendition rendition : renditions) {
				if (rendition.getEffectiveDocumentCount() > 0) {					
					// Convert renditions into a Record object for Document feed
					recordList.add(createRecord(asset, rendition));
				}
			}
		} else {
			logger.debug("No Renditions found skipping asset.");		
		}
		return recordList;		
	}
	
	public Record createRecord(Asset asset, Rendition rendition) {
		
		AiIndex aiIndex = AiIndex.parse(asset.getAiIndex());
		Date lastModified = asset.getLastModified();
		ArrayList<Meta> metaDataList = getMetaDataArrayList(asset, rendition);
		String url = asset.getDocumentYear() + "/" + aiIndex.getAiClass() + "/" + asset.getDocumentNo() + "/" + rendition.getLanguage();
		String securityClass = (asset.getSecurityClass().equals("Internal") ? "restricted" : "index");
		url = aicoreConfig.GSA_RECORD_URL_PREFIX + "/" + securityClass + "/" + url;
		String displayurl = url;
		ContentData contentData = rendition.getContent();
		String mimetype = (contentData != null && rendition.getContent().getMimetype() != null) ? rendition.getContent().getMimetype() : "text/html";
		String description = StringEscapeUtils.escapeHtml(rendition.getDescription());
		String title = StringEscapeUtils.escapeHtml(rendition.getTitle());
		String action = ContentFeedBuilder.GSAFEED_INPUT_RECORD_ADD;
		boolean lock = new Boolean(aicoreConfig.GSA_FEEDER_DOCUMENT_LOCK);
		String encoding = null;
		
/*	    String content=null;
	    if(contentData!=null)
	    {
	    	logger.debug("Content Data found");
		    String contenturl=contentData.getContentUrl();
		    
		    
		    ContentReader reader = contentService.getRawReader(contenturl);
		    if ((reader == null) || (!(reader.exists())))
		    {
		    	logger.error("Unable to locate content for " +contenturl);
		    }
		    else{
		    	logger.debug("Content Found: " +contenturl);
			    content = reader.getContentString();
		    }    			    
	    }
*/
	    
/*		StringBuilder contentToHtmlStr = new StringBuilder("<html><title>");
		contentToHtmlStr.append(title);
		contentToHtmlStr.append("</title><body>");
		contentToHtmlStr.append(description != null && !description.equals("") ? description : title);
		contentToHtmlStr.append("</body></html>");
		content = contentToHtmlStr.toString();*/
		logger.info("Adding index.... url[" + url + "] title[" + title + "]");
		
		Record record = new Record();
		record.setUrl(url);
		record.setDisplayUrl(displayurl);
		record.setAction(action);
		record.setLock(lock);
		record.setMimetype(mimetype);
		record.setLastModified(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z").format(lastModified));
		//record.setContent(content);
		record.setEncoding(encoding);
		record.setMeta(metaDataList);			
		return record;		
	}

	public ArrayList<Meta> getMetaDataArrayList(Asset asset, Rendition rendition) {
		ArrayList<Meta> metaDataList = new ArrayList<Meta>();
		SimpleDateFormat targetDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
			metaDataList.add(new Meta("robots", "nocache"));		
		if (rendition.getDescription() != null && !rendition.getDescription().isEmpty())
			metaDataList.add(new Meta("description", rendition.getDescription()));
		if (asset.getAllPathCategories() != null && !asset.getAllPathCategories().isEmpty())			
			metaDataList.add(new Meta("keywords", asset.getAllPathCategories().toString()));		
		if (rendition.getLanguage() != null && !rendition.getLanguage().isEmpty())
			metaDataList.add(new Meta("language", rendition.getLanguage()));
		if (asset.getPublishDate() != null) {
			metaDataList.add(new Meta("date", targetDateFormatter.format((asset.getPublishDate()))));
		}		
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
}
