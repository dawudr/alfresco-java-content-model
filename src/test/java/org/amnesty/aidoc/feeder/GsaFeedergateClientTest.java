package org.amnesty.aidoc.feeder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.amnesty.aidoc.aicoreConfig;
import org.amnesty.aidoc.feeder.ContentFeedBuilder;
import org.amnesty.aidoc.feeder.GsaFeedergateClient;
import org.amnesty.aidoc.feeder.GsaFeedImpl.Meta;
import org.amnesty.aidoc.feeder.GsaFeedImpl.Record;
import junit.framework.TestCase;

/**
 * @author root
 * 
 */
public class GsaFeedergateClientTest extends TestCase {

	protected GsaFeedergateClient gsaFeedergateClient;
	protected ContentFeedBuilder contentAdd, contentDelete;
	protected ArrayList<Record> recordList, recordList2;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		gsaFeedergateClient = new GsaFeedergateClient();
		
		ArrayList<Meta> metaDataList = new ArrayList<Meta>();
		metaDataList.add(new Meta("AI-title","Administration still mulling trial"));
		metaDataList.add(new Meta("AI-language","en"));
		metaDataList.add(new Meta("AI-index","AMR 51/088/2009"));
		metaDataList.add(new Meta("AI-class","AMR"));
		metaDataList.add(new Meta("AI-subclass","51"));
		metaDataList.add(new Meta("AI-document-no","088"));
		metaDataList.add(new Meta("AI-document-year","2009"));
		metaDataList.add(new Meta("AI-security-class","Public"));
		metaDataList.add(new Meta("AI-published","2009-07-31"));
		metaDataList.add(new Meta("AI-index-year","2009-01-01"));
		metaDataList.add(new Meta("AI-originator","Alfresco"));
		metaDataList.add(new Meta("AI-publication-status","Published"));
		metaDataList.add(new Meta("AI-type","Document"));
		String content = ("<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" /><title>Macao national security law threatens human rights</title></head><body>The law, which covers acts of sedition, secession, subversion and treason against the Central People's Government of China, is open to potential misuse.</body></html>");

		ArrayList<Record> recordList = new ArrayList<Record>();
		Record record = new Record();
		record.setUrl("http://test.amnesty.org/sampleDocument.html");
		record.setDisplayUrl("http://test.amnesty.org/sampleDocument.html");
		record.setAction(ContentFeedBuilder.GSAFEED_INPUT_RECORD_ADD);
		record.setLock(false);
		record.setMimetype("text/plain");
		record.setLastModified(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z").format(new Date()));
		record.setContent(content);
		record.setEncoding(null);
		record.setMeta(metaDataList);
		recordList.add(record);		

		contentAdd = new ContentFeedBuilder(aicoreConfig.GSA_FEEDER_DOCUMENT_DATASOURCENAME, recordList);
		
		Record record2 = new Record();
		record2.setUrl("http://test.amnesty.org/sampleDocument.html");
		record2.setDisplayUrl("http://test.amnesty.org/sampleDocument.html");
		record2.setAction(ContentFeedBuilder.GSAFEED_INPUT_RECORD_DELETE);
		record2.setMimetype("text/plain");
		recordList.add(record2);		
		
		
		ArrayList<Record> recordList2 = new ArrayList<Record>();
		recordList2.add(record2);		
		contentDelete = new ContentFeedBuilder(aicoreConfig.GSA_FEEDER_DOCUMENT_DATASOURCENAME, recordList2);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPushContentFeed() {
		
		assertEquals(true, gsaFeedergateClient.pushContentFeed(contentAdd, contentAdd.generateContentFeed()));
		
		assertEquals(true, gsaFeedergateClient.pushContentFeed(contentDelete, contentDelete.generateContentFeed()));
	}

	public void testAddContent() {
		String url = "https://192.168.1.137/service/library/crawl/2009/sampleDocument.html";
		String content = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" /><title>Macao national security law threatens human rights</title></head><body>The law, which covers acts of sedition, secession, subversion and treason against the Central People's Government of China, is open to potential misuse.</body></html>";
		
		
		
		assertEquals(true, gsaFeedergateClient.addContent(recordList));
	}
	
	public void testdeleteContent() {
		String url = "https://192.168.1.137/service/library/crawl/2009/sampleDocument.html";
		assertEquals(true, gsaFeedergateClient.deleteContent(recordList2));
	}
}
