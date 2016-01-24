package org.amnesty.aidoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Constants class used by AiDoc Alfresco classes
 * 
 * @author chatch
 */
public class Constants {

    /*
     * Main Alfresco store
     */
    public static final StoreRef SEARCH_STORE = new StoreRef(
            StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");

    /*
     * Aidoc Service Paths
     */
    public static final String CREATE_ASSET_URI = "/service/aidoc/createasset";

    public static final String CREATE_TYPE_URI = "/service/aidoc/createtype";

    public static final String GET_ASSET_SERVICE_URI = "/service/aidoc/asset";

    public static final String UPDATE_ASSET_SERVICE_URI = "/service/aidoc/updateasset";

    public static final String UPDATE_TYPE_SERVICE_URI = "/service/aidoc/updatetype";

    public static final String UPDATE_RENDITION_SERVICE_URI = "/service/aidoc/updaterendition";
    
    public static final String LOGIN_SERVICE_URI = "/service/api/login";

    /*
     * Repository XPaths
     */
    public static final String INDEXED_DOCS_XPATH = "./app:company_home/cm:Asset_x0020_Library/cm:Indexed_x0020_Documents";

    public static final String SPACE_TEMPLATES_XPATH = "./app:company_home/app:dictionary/app:space_templates";

    /*
     * Aicore Types
     */
    public static final String AICORE_MODEL = "http://www.amnesty.org/model/aicore/1.0";

    private static final String ASSET_TYPES_ARRAY[] = { "ActionDocument",
            "Appendix", "Contents", "Cover", "Document", "Newsletter",
            "MediaAdvisory", "MediaBriefing", "Pinksheet", "PressRelease",
            "Report", "Summary", "UrgentAction", "PressItem", "PublicStatement","OpenLetter", 
            "MedicalAction", "Postcard", "Calendar", "Poster", "PolicyDocument", "QuestionsAndAnswers" };

    private static final String[] AI_INDEX_TYPES_ARRAY = { "ActionDocument",
        "Document", "Newsletter", "MediaAdvisory", "MediaBriefing", "PressRelease",
        "Report", "Unknown", "UrgentAction", "PressItem", "PublicStatement","OpenLetter", 
        "MedicalAction", "Postcard", "Calendar", "Poster", "PolicyDocument", "QuestionsAndAnswers"};
    
    private static final String AUXILLIARY_TYPES_ARRAY[] = { 
        "Appendix", "Contents", "Cover", "Pinksheet", "Summary" };

    public static final List<String> AI_INDEX_TYPES = Arrays
    .asList( AI_INDEX_TYPES_ARRAY );

    public static final List<String> AUXILLIARY_TYPES = Arrays
    .asList( AUXILLIARY_TYPES_ARRAY );
    		
    public static final List<String> ASSET_TYPES = Arrays.asList(ASSET_TYPES_ARRAY);

    private static final String SECURITY_CLASS_TYPES_ARRAY[] = { "Public",
            "Internal" };

    public static final List<String> SECURITY_CLASS_TYPES = Arrays
            .asList(SECURITY_CLASS_TYPES_ARRAY);

    public static final QName TYPE_EDITION = QName.createQName(AICORE_MODEL,
            "Edition");
    
    public static final QName TYPE_AICORE_DOCUMENT = QName.createQName(AICORE_MODEL,
    "Document");
    /*
     * Amnesty core aspects
     */
    public static final QName ASPECT_AUTOVALIDATABLE = QName.createQName(
            AICORE_MODEL, "autovalidatable");
    
    public static final QName ASPECT_PUBLISHABLE = QName.createQName(
            AICORE_MODEL, "publishable");
    
    public static final QName ASPECT_WITHDRAWABLE = QName.createQName(
            AICORE_MODEL, "withdrawable");
    
    public static final QName ASPECT_ITEMSTATUSFLAGS = QName.createQName(
            AICORE_MODEL, "itemStatusFlags");

    public static final QName ASPECT_SECURITYCLASSIFIABLE = QName.createQName(
            AICORE_MODEL, "securityClassifiable");

    public static final QName ASPECT_SECONDARYCLASSIFIABLE = QName.createQName(
            AICORE_MODEL, "secondaryclassifiable");
    
    public static final QName ASPECT_FEEDPUBLISHABLE = QName.createQName(
            AICORE_MODEL, "feedPublishable");    
    
    /*
     * Amnesty core properties
     */
    
    public static final QName PROP_ASSET = QName.createQName(
            AICORE_MODEL, "Asset");
    
    public static final QName PROP_AI_INDEX = QName.createQName(
            AICORE_MODEL, "AiIndex");
    
    public static final QName PROP_AI_TITLE = QName.createQName(
            AICORE_MODEL, "aiTitle");
    
    public static final QName PROP_AI_DESCRIPTION = QName.createQName(
            AICORE_MODEL, "aiDescription");
    public static final QName PROP_PUBLISH_DATE = QName.createQName(
            AICORE_MODEL, "publishDate");

    public static final QName PROP_INVALIDATED = QName.createQName(
            AICORE_MODEL, "invalidated");

    public static final QName PROP_VALIDITY_NOTES = QName.createQName(
            AICORE_MODEL, "validityNotes");

    public static final QName PROP_SECURITY_CLASS = QName.createQName(
            AICORE_MODEL, "securityClass");

    public static final QName PROP_WITHDRAWN = QName.createQName(
            AICORE_MODEL, "withdrawn");

    public static final QName PROP_AI_INDEX_TYPE = QName.createQName(
            AICORE_MODEL, "aiIndexType");

    public static final QName PROP_AI_AUXILIARY_TYPE = QName.createQName(
            AICORE_MODEL, "aiAuxiliaryType");
    
    public static final QName PROP_FEED_PUBLISHED_STATUS = QName.createQName(
            AICORE_MODEL, "feedPublishedStatus");
    
    public static final QName PROP_AI_INDEX_STATUS = QName.createQName(
            AICORE_MODEL, "aiIndexStatus");
    
    public static final QName PROP_REQUESTED_BY = QName.createQName(
            AICORE_MODEL, "requestedBy");
    
    public static final QName PROP_NETWORK = QName.createQName(
            AICORE_MODEL, "network");
    
    public static final QName PROP_NETWORK_NUMBER = QName.createQName(
            AICORE_MODEL, "networkNumber");
    
    public static final QName PROP_SEC_CATEGORIES = QName.createQName(
            AICORE_MODEL, "secCategories");
    
    /*
     * Alfresco Content model properties and aspects
     */
    public static final QName PROP_TO = QName.createQName(
            NamespaceService.CONTENT_MODEL_1_0_URI, "to");

    public static final QName PROP_FROM = QName.createQName(
            NamespaceService.CONTENT_MODEL_1_0_URI, "from");

    public static final QName ASPECT_TITLED = QName.createQName(
            NamespaceService.CONTENT_MODEL_1_0_URI, "titled");

    public static final QName ASPECT_AUTHOR = QName.createQName(
            NamespaceService.CONTENT_MODEL_1_0_URI, "author");

    /*
     * Data and input parameter Mappings
     * TODO this design limits press releases to 999 per year
     */
    public static final Map<String, String> ASSETTYPE_TO_AI_CLASS_MAP = new HashMap<String, String>();
    static {
        ASSETTYPE_TO_AI_CLASS_MAP.put("PressRelease", "PRE01");
        ASSETTYPE_TO_AI_CLASS_MAP.put("MediaAdvisory", "PRE02");
        ASSETTYPE_TO_AI_CLASS_MAP.put("MediaBriefing", "PRE03");
    }

//    public static final Map<String, String> FILE_EXT_TO_MIMETYPE_MAP = new HashMap<String, String>();
//    static {
//        FILE_EXT_TO_MIMETYPE_MAP.put("pdf", "application/pdf");
//        FILE_EXT_TO_MIMETYPE_MAP.put("rtf", "application/rtf");
//        FILE_EXT_TO_MIMETYPE_MAP.put("doc", "application/msword");
//        FILE_EXT_TO_MIMETYPE_MAP.put("wp", "application/wordperfect");
//        FILE_EXT_TO_MIMETYPE_MAP.put("wpd", "application/wordperfect");
//        FILE_EXT_TO_MIMETYPE_MAP.put("txt", "text/plain");
//        FILE_EXT_TO_MIMETYPE_MAP.put("html", "text/html");
//        FILE_EXT_TO_MIMETYPE_MAP.put("xhtml", "application/xhtml+xml");
//        FILE_EXT_TO_MIMETYPE_MAP.put("xml", "text/xml");
//        FILE_EXT_TO_MIMETYPE_MAP.put("xml", "application/xml");
//        FILE_EXT_TO_MIMETYPE_MAP.put("csv", "text/csv");
//    }
//
//    public static final Map<String, String> MIMETYPE_TO_FILE_EXT_MAP = new HashMap<String, String>();
//    static {
//        for (String key : FILE_EXT_TO_MIMETYPE_MAP.keySet()) {
//            MIMETYPE_TO_FILE_EXT_MAP
//                    .put(FILE_EXT_TO_MIMETYPE_MAP.get(key), key);
//        }
//    }

    /*
     * Create type service parameters
     */
    public static final List<String> TYPE_SVC_MANDATORY_PARAMS = new ArrayList<String>();

    public static final List<String> TYPE_SVC_NON_MANDATORY_PARAMS = new ArrayList<String>();

    public static final List<String> TYPE_SVC_ALL_PARAMS = new ArrayList<String>();


    static {
        TYPE_SVC_MANDATORY_PARAMS.add("aiIndex");
        TYPE_SVC_MANDATORY_PARAMS.add("contenturl");
        TYPE_SVC_MANDATORY_PARAMS.add("lang");
        TYPE_SVC_MANDATORY_PARAMS.add("title");
        TYPE_SVC_MANDATORY_PARAMS.add("mimetype");
    };

    static {
    	TYPE_SVC_NON_MANDATORY_PARAMS.add("type");
        TYPE_SVC_NON_MANDATORY_PARAMS.add("edition");
        TYPE_SVC_NON_MANDATORY_PARAMS.add("description");
        TYPE_SVC_NON_MANDATORY_PARAMS.add("to");
        TYPE_SVC_NON_MANDATORY_PARAMS.add("from");
        TYPE_SVC_NON_MANDATORY_PARAMS.add("masterurl");
    };

    static {
        TYPE_SVC_ALL_PARAMS.addAll(TYPE_SVC_MANDATORY_PARAMS);
        TYPE_SVC_ALL_PARAMS.addAll(TYPE_SVC_NON_MANDATORY_PARAMS);
    }
    
    public static final Map <String, Integer> MASTER_MIMETYPE_PREFS = new HashMap<String, Integer>();
    	static {
    	MASTER_MIMETYPE_PREFS.put("application/msword",0);
    	MASTER_MIMETYPE_PREFS.put("application/rtf",1); 
    	MASTER_MIMETYPE_PREFS.put("application/wordperfect",2);
    	MASTER_MIMETYPE_PREFS.put("application/pdf",3);
    	MASTER_MIMETYPE_PREFS.put("application/xhtml+xml",4);
    	MASTER_MIMETYPE_PREFS.put("text/html",5);
		}
	
	static final Map<String, Integer> MASTER_EDITION_PREFS = new HashMap<String, Integer>();
		static {
		MASTER_EDITION_PREFS.put("Standard Edition",0);
		MASTER_EDITION_PREFS.put("Formatted Edition",1);
		MASTER_EDITION_PREFS.put("Web Edition",3);							
		}
	
	private static final Map<String, Integer> INLINE_MIMETYPE_PREFS = new HashMap<String, Integer>();
		static {
		INLINE_MIMETYPE_PREFS.put("application/xhtml+xml",0);
		INLINE_MIMETYPE_PREFS.put("text/html",1);
		}
	
	static final Map<String, Integer> INLINE_EDITION_PREFS = new HashMap<String, Integer>();
		static {
		INLINE_EDITION_PREFS.put("Web Edition",0);
		INLINE_EDITION_PREFS.put("Formatted Edition",1); 
		INLINE_EDITION_PREFS.put("Standard Edition",2);
		}
	
	private static final Map<String, Integer> ATTACHMENT_MIMETYPE_PREFS = new HashMap<String, Integer>();
		static {
		ATTACHMENT_MIMETYPE_PREFS.put("application/pdf",0);
		}

	static final Map<String, Integer> ATTACHMENT_EDITION_PREFS = new HashMap<String, Integer>();
		static {
		ATTACHMENT_EDITION_PREFS.put("Formatted Edition",0);
		ATTACHMENT_EDITION_PREFS.put("Web Edition",1); 
		ATTACHMENT_EDITION_PREFS.put("Standard Edition",2);
		}
	
	static final Map<String, Boolean>  IGNORE_EFFECTIVITY = new HashMap<String, Boolean>();
		static { 
		IGNORE_EFFECTIVITY.put("MASTER",true);
		IGNORE_EFFECTIVITY.put("INLINE",false);
		IGNORE_EFFECTIVITY.put("ATTACHMENT",false);
		}
	
	static final Map<String, Map<String, Integer>> MIMETYPE_PREFS = new HashMap<String, Map<String, Integer>>();
		static {
		MIMETYPE_PREFS.put("MASTER",MASTER_MIMETYPE_PREFS); 
		MIMETYPE_PREFS.put("INLINE",INLINE_MIMETYPE_PREFS);
		MIMETYPE_PREFS.put("ATTACHMENT",ATTACHMENT_MIMETYPE_PREFS);
		}
	
	static final Map<String, Map<String, Integer>> EDITION_PREFS = new HashMap<String, Map<String, Integer>>();
		static {
			EDITION_PREFS.put("MASTER",MASTER_EDITION_PREFS);
			EDITION_PREFS.put("INLINE",INLINE_EDITION_PREFS);
			EDITION_PREFS.put("ATTACHMENT",ATTACHMENT_EDITION_PREFS);
		}

}
