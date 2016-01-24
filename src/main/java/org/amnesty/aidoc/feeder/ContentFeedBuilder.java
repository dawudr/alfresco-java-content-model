package org.amnesty.aidoc.feeder;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.amnesty.aidoc.aicoreConfig;
import org.amnesty.aidoc.feeder.GsaFeedImpl.Header;
import org.amnesty.aidoc.feeder.GsaFeedImpl.Record;
import org.apache.log4j.Logger;

/**
 * @author drahman
 * 
 *         Functions:- 1. Getters and setters for the Content Feed object. 2.
 *         Builds the XML DOM for Content Feed. 3. Transforms the XML DOM into
 *         valid XML document which is output as String.
 * 
 */
public class ContentFeedBuilder {

	private String dataSource = null;
	private String feedType = null;
	private ArrayList<Record> recordList = null;
	
	public ContentFeedBuilder(String dataSource, ArrayList<Record> recordList) {
		super();
		this.dataSource = dataSource;
		this.feedType = aicoreConfig.GSA_FEEDER_DOCUMENT_FEEDTYPE;
		this.recordList = recordList;
	}

	private String contentFeedXMLString = null;
	
	private static Logger logger = Logger.getLogger(ContentFeedBuilder.class);
	public static final String GSAFEED_INPUT_FEEDTYPE_METADATA_AND_URL = "metadata-and-url";

	public static final String GSAFEED_INPUT_RECORD_ADD = "add";
	public static final String GSAFEED_INPUT_RECORD_DELETE = "delete";

	/**
	 * Builds the XML DOM using XML Tag constants as per DTD and values of the
	 * ContentFeed Object.
	 */
	public String generateContentFeed() {
			GsaFeedImpl gsaFeedImpl = new GsaFeedImpl();
			List<Header> gsaFeedImplHeaderList = new ArrayList<Header>();
			Header gsaFeedImplHeader = new Header();
			if (dataSource != null) {
				gsaFeedImplHeader.setDatasource(dataSource);
			}
			if (feedType != null) {
				gsaFeedImplHeader.setFeedtype(feedType);
			}
			gsaFeedImplHeaderList.add(gsaFeedImplHeader);
			gsaFeedImpl.setHeader(gsaFeedImplHeaderList);
			gsaFeedImpl.setGroup(recordList);

			// create string from xml tree
			StringWriter sw = new StringWriter();

			try {
			JAXBContext pContext = JAXBContext.newInstance(new Class[] { GsaFeedImpl.class });
			Marshaller marshaller = pContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.valueOf(true));
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty("com.sun.xml.bind.xmlHeaders", "<!DOCTYPE gsafeed PUBLIC \"-//Google//DTD GSA Feeds//EN\" \"\">\n");
			marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", false);
			marshaller.marshal(gsaFeedImpl, sw);
			
			String marshalledStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<!DOCTYPE gsafeed PUBLIC \"-//Google//DTD GSA Feeds//EN\" \"\">\n" + sw.toString();
			this.contentFeedXMLString = marshalledStr;
						
			} catch (JAXBException e) {
				logger.error("JAXBException " + e);
			} 
		return (this.contentFeedXMLString);
	}

	/**
	 * Getter and Setters to access private fields of object
	 * 
	 * @return
	 */

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getFeedType() {
		return feedType;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}	

	public String toString() {
		return this.contentFeedXMLString;
	}
}
