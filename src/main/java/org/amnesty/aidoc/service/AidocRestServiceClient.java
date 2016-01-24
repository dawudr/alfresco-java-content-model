package org.amnesty.aidoc.service;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface AidocRestServiceClient
{

  public static final String CREATE_ASSET_URI = "/service/aidoc/createasset";

  public static final String CREATE_TYPE_URI = "/service/aidoc/createtype";

  public static final String GET_ASSET_SERVICE_URI = "/service/aidoc/asset";

  public static final String UPDATE_ASSET_SERVICE_URI = "/service/aidoc/updateasset";

  public static final String DELETE_ASSET_SERVICE_URI = "/service/aidoc/deleteasset";

  public static final String DELETE_ASSET_CONTENTS_SERVICE_URI = "/service/aidoc/deleteassetcontents";

  public static final String UPDATE_TYPE_SERVICE_URI = "/service/aidoc/updatetype";
  
  public static final String UPDATE_RENDITION_SERVICE_URI = "/service/aidoc/updaterendition";

  public static final String LOGIN_SERVICE_URI = "/service/api/login";

  public final static String GET_SEARCH_URI = "/service/aidoc/search.atom";

  /**
   * Configure using a properties file
   * @throws Exception
   */  
  public String configureFromProperties() throws Exception;
  
  /**
   * @param useHttps set true to use HTTPS, false otherwise
   * @param serverPort self-explanatory
   * @param serverHost self-explanatory
   * @param restAPIPath URI for webscripts service root
   * @param serverLogin self-explanatory
   * @param serverPassword self-explanatory
   * @throws Exception
   */
  public String configure( boolean useHttps, String serverHost,
      String serverPort, String restAPIPath, String serverLogin,
      String serverPassword ) throws Exception;

  /**
   * Full search
   * @param term full-text search term
   * @param aiIndexPattern TODO
   * @param type TODO
   * @param securityClass TODO
   * @param publishDateFrom TODO
   * @param publishDateTo TODO
   * @param categories TODO
   * @param secondaryCategories TODO
   * @return returns set of AI indexes
   * @throws Exception
   */

  public Set<String> search( String term, String aiIndexPattern, String type, String securityClass, Date publishDateFrom, Date publishDateTo, Set<String> categories, Set<String> secondaryCategories ) throws Exception;

  /**
   * Paginated search
   * @param term full-text search term
   * @param aiIndexPattern TODO
   * @param type TODO
   * @param securityClass TODO
   * @param publishDateFrom TODO
   * @param publishDateTo TODO
   * @param categories TODO
   * @param secondaryCategories TODO
   * @param page index of page within result set
   * @param max max entries in result set
   * @return ServiceCallResult containing list of AI Indexes
   * @throws Exception
   */
  public ServiceCallResult paginatedSearch( String term, String aiIndexPattern, String type, String securityClass, Date publishDateFrom, Date publishDateTo, Set<String> categories, Set<String> secondaryCategories, int page, int max )
      throws Exception;

  public ServiceCallResult getAssetMetadata( String year,
      String aiClassCode, String docnum, String lang ) throws Exception;

  public ServiceCallResult createAsset( String aiClassCode, String type,
      String title, Integer year, Integer docnum, Date publishDate, String aiIndexType,
      String securityClass, Set<String> categories,
      Set<String> secondaryCategories ) throws Exception;
  
  public ServiceCallResult createAsset( String aiClassCode, String type,
	      String title, Integer year, Integer docnum, Date publishDate, String aiIndexType,
	      String securityClass, String originator, String network, String networkNumber, Set<String> categories,
	      Set<String> secondaryCategories ) throws Exception;

  public ServiceCallResult createType( String aiIndex, String edition,
      String type, Map<String, String> properties ) throws Exception;

  public ServiceCallResult updateAsset( String aiIndex, String title,
      Set<String> categories ) throws Exception;

  public ServiceCallResult updateAsset( String aiIndex, String title,
      String securityClass, String aiIndexType, String originator, String network,
      String networkNumber, Set<String> categories, String invalidated,
      String validityNotes, Set<String> secondaryCategories ) throws Exception;

  public ServiceCallResult updateAsset( String aiIndex,
      Map<String, String> map, Set<String> categories,
      Set<String> secondaryCategories ) throws Exception;

  public ServiceCallResult deleteAsset( String aiIndex ) throws Exception;

  public ServiceCallResult deleteAssetContents( String aiIndex ) throws Exception;

  public ServiceCallResult updateType( String aiIndex, String type, String aiAuxiliaryType,
      String lang, String mimetype, String edition,
      Map<String, String> properties ) throws Exception;

  public ServiceCallResult updateRendition( String aiIndex,
	      String lang, Map<String, String> properties ) throws Exception;
  /**
   * Get Alfresco security ticket
   * 
   * @return ticket string value
   * @throws Exception
   */
  public String getTicket() throws Exception;

  /**
   * Upload a file to alfresco and get back the contenturl
   */
  public String uploadFile( File file ) throws Exception;

}