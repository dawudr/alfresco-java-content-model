package org.amnesty.aidoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class aicoreConfig {

	public static String GSA_PROTOCOL = "https";
	public static String GSA_PATH = "/search";
	public static String GSA_HOSTNAME = "gsa1.amnesty.org"; //192.168.1.85
	public static int GSA_PORT = 443;
	public static String GSA_PUBLIC_COLLECTION = "ai_unrestricted";//ai_restricted	
	public static String GSA_INTERNAL_COLLECTION = "ai_restricted";//ai_unrestrected
	public static String GSA_USER = "alfresco";
	public static String GSA_PASSWORD = "alfresco";
	public static Integer GSA_RESULTS_PER_PAGE = 10;
	public static String GSA_FEEDER_URL = "http://192.168.1.85:19900/xmlfeed";
	public static String GSA_FEEDER_GETBACKLOGCOUNT_URL = "http://192.168.1.85:19900/getbacklogcount";
	public static String GSA_RECORD_URL_PREFIX = "http://www.amnesty.org/service/library/index/";
	public static String GSA_FEEDER_DOCUMENT_DATASOURCENAME = "test";
	public static String GSA_FEEDER_DOCUMENT_FEEDTYPE = "full";	//full or incremental
	public static String GSA_FEEDER_DOCUMENT_LOCK = "false"; // true or false
	public static String REPORT_FOLDER = "/home/alfresco/sybase_xml";
	public static int GSA_FEEDER_DELAY_SECONDS = -1; // -1 turns feeder off.
	
	private static final String AICORE_CONFIG_FILE = "aicore.properties";
	
	private static final Properties aicoreProperaties = loadProperties(AICORE_CONFIG_FILE, aicoreConfig.class.getClassLoader());

	private static Logger logger = Logger.getLogger(aicoreConfig.class); 
	
	private aicoreConfig() {
		super();
	}

	public static Properties getAicoreproperaties() {
		return aicoreProperaties;
	}
	
	/**
     * Replace the default values with the properties file values
     * @param name
     * @param loader
     * @return properties for the search
     */
    private static Properties loadProperties (String name, ClassLoader loader)
    {
    	Properties result = null;
        InputStream in = null;
        
        try{
	    	if (loader == null) loader = ClassLoader.getSystemClassLoader ();
	    	in = loader.getResourceAsStream(name);
	    	if (in != null)
	        {
	            result = new Properties ();
	            result.load (in); // Can throw IOException
	            GSA_PROTOCOL = result.getProperty("gsa.server.protocol");
	            GSA_HOSTNAME = result.getProperty("gsa.server.host");
	            String numPort = result.getProperty("gsa.server.port");
	            if(StringUtils.isNumeric(numPort)){GSA_PORT = Integer.valueOf(numPort);}
	            GSA_PATH = result.getProperty("gsa.server.path");
	            GSA_USER = result.getProperty("gsa.user");
	            GSA_PASSWORD = result.getProperty("gsa.password");
	            GSA_PUBLIC_COLLECTION = result.getProperty("gsa.public.collection");
	            GSA_INTERNAL_COLLECTION = result.getProperty("gsa.internal.collection");
	            String numResult = result.getProperty("gsa.result_per_page");
	            if(StringUtils.isNumeric(numResult)){GSA_RESULTS_PER_PAGE = Integer.valueOf(numResult);}
	            GSA_FEEDER_URL = result.getProperty("gsa.feeder.url");
	            GSA_FEEDER_GETBACKLOGCOUNT_URL = result.getProperty("gsa.feeder.getbacklogcount.url");
	            GSA_RECORD_URL_PREFIX = result.getProperty("gsa.record.url.prefix");
	            GSA_FEEDER_DOCUMENT_DATASOURCENAME = result.getProperty("gsa.feeder.document.datasourcename");
	        	GSA_FEEDER_DOCUMENT_FEEDTYPE = result.getProperty("gsa.feeder.document.feedtype");	            
	            GSA_FEEDER_DOCUMENT_LOCK = result.getProperty("gsa.feeder.document.lock");
	            GSA_FEEDER_DELAY_SECONDS = Integer.parseInt(result.getProperty("gsa.feeder.delay.seconds"));	            
	            REPORT_FOLDER = result.getProperty("report.folder");
	        }
        }
    	catch (IOException e)
        {
    		logger.error("Failed to load properties from "+name);
            result = null;
        }
    	return result;
    }
}
