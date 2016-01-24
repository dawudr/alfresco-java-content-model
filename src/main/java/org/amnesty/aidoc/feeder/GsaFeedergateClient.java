/**
 * Handles HTTP MultiPart POST to the Google Search Appliance Feeder gate URL
 * URL: http://192.168.1.85:19900/xmlfeed
 * 
 */
package org.amnesty.aidoc.feeder;

import java.util.ArrayList;

import org.amnesty.aidoc.aicoreConfig;
import org.amnesty.aidoc.feeder.GsaFeedImpl.Record;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

/**
 * @author drahman
 * 
 * Functions :-
 * 1. Initialise google search appliance feeds input URL from properties.
 * 2. Sends an add or delete a HTTP request as a HTTP Multipart POST with parameters and 
 * content type as specificed in Google's Feeds Protocol Developer guide at 
 * URL: http://code.google.com/apis/searchappliance/documentation/52/xml_reference.html.
 */
public class GsaFeedergateClient {

	/*
	 * GSA static constants
	 */
	
	private static final String GSA_HTTP_POST_PARAMETER_DATASOURCE = "datasource";
	private static final String GSA_HTTP_POST_PARAMETER_FEEDTYPE = "feedtype";
	private static final String GSA_HTTP_POST_PARAMETER_DATA = "data";
	private byte[] responseBody = null;

	private static Logger logger = Logger.getLogger(GsaFeedergateClient.class); 
	
	/**
	 * Sends request to Google Search Applicance to add record URLs.
	 * @throws Exception 
	 */
	public int addContent(ArrayList<Record> recordList) {
		ContentFeedBuilder contentFeed = new ContentFeedBuilder(aicoreConfig.GSA_FEEDER_DOCUMENT_DATASOURCENAME, recordList);
		return pushContentFeed(contentFeed, contentFeed.generateContentFeed());		
	}
	
	/**
	 * Sends request to Google Search Applicance to delete a record URL feed and update the Index Flag in Alfresco.
	 * @throws Exception 
	 */
	public int deleteContent(ArrayList<Record> recordList) {
		ContentFeedBuilder contentFeed = new ContentFeedBuilder(aicoreConfig.GSA_FEEDER_DOCUMENT_DATASOURCENAME, recordList);
		return pushContentFeed(contentFeed, contentFeed.generateContentFeed());		
	}
	
	/**
	 * Pushes the content feed object into Google Search Appliance Feed input and
	 * updates Alfreso DB Indexed Flag if feed is successfully sent.
	 * @throws Exception 
	 */
	public int pushContentFeed(ContentFeedBuilder contentFeed, String contentFeedXml) {	
		int status = 0;
		PostMethod postMethod = null;
		
		try {
			postMethod = new PostMethod(aicoreConfig.GSA_FEEDER_URL);
			// Provide custom retry handler is necessary
			postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
			
			StringPart[] parts = { new StringPart(GSA_HTTP_POST_PARAMETER_DATASOURCE, contentFeed.getDataSource()), new StringPart(GSA_HTTP_POST_PARAMETER_FEEDTYPE, contentFeed.getFeedType()), new StringPart(GSA_HTTP_POST_PARAMETER_DATA, contentFeed.toString()) };
			postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
			HttpClient client = new HttpClient();
			logger.debug("Sending HTTP Post RequestUrl[" + aicoreConfig.GSA_FEEDER_URL + "] Parameters: feedtype[" + contentFeed.getFeedType() + "] data[\r\n" + contentFeed.toString() + "\r\n]");			
			status = client.executeMethod(postMethod);
			this.responseBody = postMethod.getResponseBody();			
			logger.debug("Received HTTP Post ResponseStatus[" + status + "] ResponseBody[" + new String(responseBody) + "]");
		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage());
			return status;					
		} finally {
			if(postMethod != null)
				postMethod.releaseConnection();
		}
		
		return status;
	} 
}
