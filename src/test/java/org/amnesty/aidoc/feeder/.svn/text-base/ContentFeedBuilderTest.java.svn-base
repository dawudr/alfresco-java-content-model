package org.amnesty.aidoc.feeder;

import org.amnesty.aidoc.feeder.ContentFeedBuilder;
import org.amnesty.aidoc.feeder.GsaFeedImpl.Record;
import org.amnesty.aidoc.feeder.GsaFeedImpl.Meta;
import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ContentFeedBuilderTest extends XMLTestCase {

	protected ContentFeedBuilder contentFeedBuilder;
	protected String sampleGsaFeed;

	protected void setUp() throws Exception {
		BufferedReader input = new BufferedReader(new FileReader(new File("\\sampleGsaFeed2.xml")));

		StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			while ((line = input.readLine()) != null) {
				stringBuilder.append(line);
			}
			input.close();
		sampleGsaFeed = stringBuilder.toString();
		
		String content = "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" /><title>Macao national security law threatens human rights</title></head><body>The law, which covers acts of sedition, secession, subversion and treason against the Central People's Government of China, is open to potential misuse.</body></html>";
		ArrayList<Meta> metaDataList = new ArrayList<Meta>();
		metaDataList.add(new Meta("AI-title","Administration still mulling trial"));
		metaDataList.add(new Meta("AI-language",""));
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
		
		contentFeedBuilder = new ContentFeedBuilder(ContentFeedBuilder.GSAFEED_INPUT_RECORD_ADD, recordList); 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testXmlToString() {
		System.out.println(contentFeedBuilder.generateContentFeed());
		assertTrue(contentFeedBuilder.generateContentFeed()instanceof String);
	}
	
	public void testValidationWithDTD() throws Exception {
		XMLUnit.getTestDocumentBuilderFactory().setValidating(true);
		//Document testDocument = XMLUnit.buildTestDocument(contentFeedBuilder.getXmlToString());
		String gsaDTDUrl = new URL("http://192.168.1.85:7800/gsafeed.dtd").toExternalForm();
		
        Validator v1 = new Validator(sampleGsaFeed, gsaDTDUrl);
        assertTrue("sample document validates against gsa DTD", v1.isValid());
        
        Validator v2 = new Validator(contentFeedBuilder.generateContentFeed(), gsaDTDUrl);
        assertTrue("test document validates against gsa DTD", v2.isValid());
        
	}
	
	public void testForEquality() throws Exception {
		System.out.println(contentFeedBuilder.generateContentFeed().trim());
		System.out.println(sampleGsaFeed);

		assertXMLEqual(sampleGsaFeed, contentFeedBuilder.generateContentFeed());

		
	}

}
