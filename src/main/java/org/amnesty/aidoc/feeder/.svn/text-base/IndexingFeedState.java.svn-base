package org.amnesty.aidoc.feeder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.amnesty.aidoc.Constants;
import org.amnesty.aidoc.aicoreConfig;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import com.google.enterprise.apis.client.GsaClient;
import com.google.enterprise.apis.client.GsaEntry;
import com.google.gdata.data.Category;
import com.google.gdata.model.atompub.Categories;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public class IndexingFeedState {
	
	private NodeService nodeService;
	private BehaviourFilter policyFilter;
	
	public static final String INDEX_FEED_STATUS_UNKNOWN = "Unknown"; // 1. Pre status
	public static final String INDEX_FEED_STATUS_ACCEPTED = "Accepted"; // 2. Sent HTTP POST 200 to GSA 
	public static final String INDEX_FEED_STATUS_FAILED = "Failed"; // 3. HTTP POST failed.
	public static final String INDEX_FEED_STATUS_PENDING = "Pending"; // 4. Process index GSA Backlogcount incremented
	public static final String INDEX_FEED_STATUS_PENDING_REMOVAL = "Pending Removal"; // 4. Process index GSA Backlogcount incremented
	public static final String INDEX_FEED_STATUS_COMPLETED = "Completed"; // 5. Added document to GSA Index
	public static final String INDEX_FEED_STATUS_REMOVED = "Removed"; // 5. Added document to GSA Index
	public static final String INDEX_FEED_STATUS_FAILED_IN_ERROR = "Failed_in_error"; // 6. Not found document in GSA index
	
	
	private static final Logger logger = Logger.getLogger(IndexingFeedState.class);
	private static byte[] responseBody = null;
	private int initialBackLogCount = -1;
	
	public void initBackLogCount() {
		this.initialBackLogCount = getFeederBackLogCount();
	}

	/*
	 * Feed Files Awaiting Processing 
	 * To view a count of how many feed files remain for the search appliance to process into its index, 
	 * add /getbacklogcount to a search appliance URL at port 19900. The count that this feature provides 
	 * can be used to regulate the feed submission rate. The count also includes connector feed files. 
	 */
	public static int getFeederBackLogCount() {
		logger.debug("Calling getFeederBackLogCount");				
		HttpClient client = new HttpClient();
		GetMethod getMethod = null;
		int backLogCount = -1;
		
		try {
			getMethod = new GetMethod(aicoreConfig.GSA_FEEDER_GETBACKLOGCOUNT_URL);
			client.executeMethod(getMethod);
			responseBody = getMethod.getResponseBody();
			String backLogCountStr = new String(responseBody);
			logger.debug("getbacklogcount Response[" + backLogCountStr + "]");
			backLogCount = Integer.parseInt(backLogCountStr.trim());					
		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage());
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}		
		return backLogCount;
	}	
	
	/*
	 * Works out initial FeedStatus Message after sending HTTP Post to GSA.
	 */
	public String getIndexFeedStatusMessage(int statusCode, String feedAction) {
		logger.debug("Calling getIndexFeedStatusProperty statusCode[" + statusCode + "]");		
		String indexFeedStatus = INDEX_FEED_STATUS_UNKNOWN;
		
		// get HTTP Status code
		if (statusCode == HttpStatus.SC_OK) {	
			
			indexFeedStatus = INDEX_FEED_STATUS_ACCEPTED;
			
			int lastBackLogCount = getFeederBackLogCount();
			if(lastBackLogCount > this.initialBackLogCount) {
				if(feedAction.equals(ContentFeedBuilder.GSAFEED_INPUT_RECORD_ADD)) {
					indexFeedStatus = INDEX_FEED_STATUS_PENDING;					
				} else if(feedAction.equals(ContentFeedBuilder.GSAFEED_INPUT_RECORD_DELETE)){
					indexFeedStatus = INDEX_FEED_STATUS_PENDING_REMOVAL;					
				}
			}			
		} else {
			indexFeedStatus = INDEX_FEED_STATUS_FAILED;
		}
		
		logger.debug("HTTP Statuscode:" + statusCode + " indexFeedStatus:" + indexFeedStatus);
		return indexFeedStatus;					
	}
	
	/*
	 * Updates node Feed Published property when feed is posted to GSA
	 * Parameter: statusCode - HTTP Response from GSA
	 */
	public void updateIndexFeedStatusProperty(int statusCode, NodeRef nodeRef, String feedAction) {	
		logger.debug("Calling updateIndexFeedStatusProperty statusCode[" + statusCode + "]");						
		policyFilter.disableBehaviour(nodeRef, ContentModel.TYPE_CONTENT);
		
		String status = getIndexFeedStatusMessage(statusCode, feedAction);
		
		nodeService.setProperty(nodeRef, Constants.PROP_FEED_PUBLISHED_STATUS, status);
		policyFilter.enableBehaviour(nodeRef, ContentModel.TYPE_CONTENT);
		logger.debug("Updating Feed Published Property to: " + status);	
	}
	
	/*
	 * Updates node Feed Published property when feed is indexed in GSA
	 * Parameter: String - Index URL of document
	 */
	public boolean updateIndexFeedStatusProperty(String url, NodeRef nodeRef) {	
		logger.debug("Calling updateIndexFeedStatusProperty url[" + url + "]");
		policyFilter.disableBehaviour(nodeRef, ContentModel.TYPE_CONTENT);
		
		String indexFeedStatus = nodeService.getProperty(nodeRef, Constants.PROP_FEED_PUBLISHED_STATUS).toString();
		String lastModified = nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIED).toString();	
		
		boolean updatesuccess = false;
		if(url != null) {
			if(indexFeedStatus.equals(INDEX_FEED_STATUS_PENDING_REMOVAL)) {
				// For documents pending removal 
				updatesuccess = verifyIndexedDocumentInGsa(url, lastModified);	
				if(updatesuccess) {
					indexFeedStatus = INDEX_FEED_STATUS_COMPLETED;	
				} else {
					indexFeedStatus = INDEX_FEED_STATUS_PENDING;
				}					
			} else {
				updatesuccess = verifyIndexedDocumentInGsa(url, lastModified);	
				if(updatesuccess) {
					indexFeedStatus = INDEX_FEED_STATUS_COMPLETED;	
				} else {
					indexFeedStatus = INDEX_FEED_STATUS_FAILED_IN_ERROR;
				}					
			}
		
		} else {		
			indexFeedStatus = INDEX_FEED_STATUS_REMOVED;
			updatesuccess = true;
		}

		nodeService.setProperty(nodeRef, Constants.PROP_FEED_PUBLISHED_STATUS, indexFeedStatus);
		policyFilter.enableBehaviour(nodeRef, ContentModel.TYPE_CONTENT);
		logger.debug("Verified updating Feed Published Property to: " + indexFeedStatus);
		
		return(updatesuccess);	
	}
	
	/*
	 * Makes call to GSA Admin to check if feed crawl date is later than Last modified property
	 */
	private boolean verifyIndexedDocumentInGsa(String documentUrl, String lastModified) {
		logger.debug("Calling verifyIndexedDocumentInGsa documentUrl[" + documentUrl + "] lastModified[" + lastModified + "]");								

		SimpleDateFormat sourceDateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		Date lastModifiedDate = new Date();
		try {
			lastModifiedDate = sourceDateFormatter.parse(lastModified);
	
			// Connects to gdaadmin api
			GsaClient myClient = null;
			myClient = new GsaClient(aicoreConfig.GSA_HOSTNAME, 8000, aicoreConfig.GSA_USER, aicoreConfig.GSA_PASSWORD);

			GsaEntry entry = null;
			entry = myClient.getEntry("diagnostics", documentUrl);
			Set <Category> categories = entry.getCategories();
			for(Category category:categories)
			{
				
				logger.debug("Category Label: " + category.getLabel());
				logger.debug("Category LabelLang: " + category.getLabelLang());
				logger.debug("Category LabelScheme: " + category.getScheme());				
				
			}
			
			Calendar latestOnDiskDate = Calendar.getInstance();
			latestOnDiskDate.setTimeZone(TimeZone.getTimeZone("GMT"));
			latestOnDiskDate.setTimeInMillis(Long.parseLong(entry.getGsaContent("latestOnDisk")) * 1000);
			logger.debug("Getting Crawled Document Status from GSA: latestOnDisk [" + sourceDateFormatter.format(latestOnDiskDate.getTime()) + "]");

			// Add 1 hour for time lag and time difference between Alfresco server and GSA server
			latestOnDiskDate.add(Calendar.HOUR, 1);			
			return (latestOnDiskDate.getTime().after(lastModifiedDate));
			
		} catch (ParseException e) {
			logger.error("Date Parse Exception: " + e);
		} catch (AuthenticationException e) {
			logger.error("Unable to login into Google Search Applicance. Exception: " + e);
		} catch (Exception e) {
			logger.error("Unable to feed status from Google Search Appliance. Exception: " + e);
		}
		return false;		
	}

	public NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void setPolicyFilter(BehaviourFilter policyFilter) {
		this.policyFilter = policyFilter;
	}		
	
}