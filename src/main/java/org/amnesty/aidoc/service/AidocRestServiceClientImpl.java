package org.amnesty.aidoc.service;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Socket;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient; //import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus; //import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.Constants;

/**
 * A helper class that provides Java routines to make calls to our Alfresco
 * services via the Rest API. Call are made using the httpClient library.
 * 
 * Note that this version has the delete functions removed to decrease library
 * dependencies
 * 
 * @author mcox
 */
public class AidocRestServiceClientImpl
    implements AidocRestServiceClient
{
  private final static String PROP_FILENAME = "aidoc-service.properties";

  private static final Properties properties = new Properties();

  private static final Log log = LogFactory
      .getLog( AidocRestServiceClientImpl.class );

  public static boolean HTTPS = true;

  public static boolean HTTP = false;

  private final static Namespace opensearch = Namespace.getNamespace(
      "opensearch", "http://a9.com/-/spec/opensearch/1.1/" );

  // private final static Namespace alf = Namespace.getNamespace( "alf",
  // "http://www.alfresco.org/opensearchds/1.0/" );

  private final static Namespace ai = Namespace.getNamespace( "ai",
      "http://www.amnesty.org/search/1.0/" );

  private final static Namespace atom = Namespace
      .getNamespace( "http://www.w3.org/2005/Atom" );

  private String protocol;

  private String serverPort;

  private String serverHost;

  private String restAPIPath;

  private String serverLogin;

  private String serverPassword;

  private String serverAddress;

  // private String webserviceAddress;

  private HttpClient httpClient;

  private SAXBuilder saxBuilder = new SAXBuilder();

  private final static int SEARCH_PAGE_MAX = 100;

  static
  {

    InputStream is = AidocRestServiceClientImpl.class.getClassLoader()
        .getResourceAsStream( PROP_FILENAME );
    try
    {
      properties.load( is );
      is.close();
    } catch ( Exception e )
    {
      log.error( "Failed to load properties from " + PROP_FILENAME, e );
    }
  }

  public AidocRestServiceClientImpl()
  {
	  try{
		  configureFromProperties();
	  }
	   catch (Exception e) {
		log.error(e.getMessage());
	}
	  
  }

  private static final String getPropertyOrDefault( String propertyName,
      String defaultValue )
  {
    String value = defaultValue;
    
    if  (( properties != null ) && ( properties.get( propertyName ) != null ))
    {
      value = (String) properties.get( propertyName );
    } 
    
    return value;
  }

  public String configureFromProperties() throws Exception
  {
    Boolean useHttps = ( getPropertyOrDefault( "alfresco.server.useHttps", "false" ).equalsIgnoreCase( "FALSE" )) ? false : true;
        
    String port = getPropertyOrDefault( "alfresco.server.port", "8080" );

    String host = getPropertyOrDefault( "alfresco.server.host", "localhost" );

    String path = getPropertyOrDefault( "alfresco.server.path", "/alfresco" );

    String login = getPropertyOrDefault( "alfresco.server.login", "admin" );

    String password = getPropertyOrDefault( "alfresco.server.password", "admin" );

    // String address =ESS = "http://" + SERVER_HOST + ":"
    // + SERVER_PORT + SERVER_PATH;

    return configure( useHttps, host, port, path, login, password );
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.amnesty.aidoc.service.AidocRestServiceClient#configure(boolean,
   *      java.lang.String, java.lang.String, java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  public String configure( boolean useHttps, String serverHost,
      String serverPort, String restAPIPath, String serverLogin,
      String serverPassword ) throws Exception
  {
    try
    {
      if ( serverPort != null )
      {
        Integer.valueOf( serverPort ).intValue();
      }
    } catch ( NumberFormatException e )
    {
      throw new Exception( serverPort + " is not a valid port" );
    }

    if ( serverHost == null )
    {
      serverHost = "localhost";
    }

    this.protocol = (useHttps) ? "HTTPS" : "HTTP";
    this.serverPort = serverPort;
    this.serverHost = serverHost;
    this.restAPIPath = restAPIPath;
    this.serverLogin = serverLogin;
    this.serverPassword = serverPassword;

    serverAddress = protocol + "://";

    if ( restAPIPath == null )
    {
      restAPIPath = "";
    }

    if ( ! restAPIPath.startsWith( "/" ) )
    {
      restAPIPath = "/" + restAPIPath;
    }

    if ( (serverPort == null) || (serverPort.length() == 0) )
    {
      serverAddress += serverHost + restAPIPath;
    } else
    {
      serverAddress += serverHost + ":" + serverPort + restAPIPath;
    }

    /*
     * chop off trailing "/" if there is one as all URIs are appended starting
     * with '/'
     */
    if ( serverAddress.endsWith( "/" ) )
    {
      serverAddress = serverAddress.substring( 0, serverAddress.length() - 1 );
    }

    log.info(  "Using address " + serverAddress );

    // webserviceAddress = serverAddress + "/api";

    httpClient = new HttpClient();

    // httpClient.getParams().setVersion( HttpVersion.HTTP_1_0 );

    httpClient.getParams().setAuthenticationPreemptive( true );

    Credentials weakCredentials = new UsernamePasswordCredentials( serverLogin,
        serverPassword );

    httpClient.getState().setCredentials(
        new AuthScope( AuthScope.ANY_HOST, AuthScope.ANY_PORT,
            AuthScope.ANY_REALM, AuthScope.ANY_SCHEME ), weakCredentials );
    
    return serverAddress;

  }

  @SuppressWarnings("unchecked")
  public Set<String> search( String term, String aiIndexPattern, String type,
      String securityClass, Date publishDateFrom, Date publishDateTo,
      Set<String> categories, Set<String> secondaryCategories )
      throws Exception
  {

    int page = 1;

    Set<String> indexSet = new HashSet<String>();

    ServiceCallResult result = paginatedSearch( term, aiIndexPattern, type,
        securityClass, publishDateFrom, publishDateTo, categories,
        secondaryCategories, 1, SEARCH_PAGE_MAX );
    Document doc = result.getOutputDocument();

    String totalResultsStr = doc.getRootElement().getChildText( "totalResults",
        opensearch );

    int totalResults = Integer.parseInt( totalResultsStr );

    log.debug( "Searching for " + term + ": total predicted results = "
        + totalResults );

    boolean nextFlag = true;

    while ( nextFlag )
    {
      nextFlag = false;

      if ( page > 1 )
      {
        result = paginatedSearch( term, aiIndexPattern, type, securityClass,
            publishDateFrom, publishDateTo, categories, secondaryCategories,
            page, SEARCH_PAGE_MAX );
        doc = result.getOutputDocument();
      }

      List<Element> linkList = doc.getRootElement().getChildren( "link", atom );

      for ( Element link : linkList )
      {
        if ( link.getAttribute( "rel" ).getValue().equalsIgnoreCase( "next" ) )
        {
          nextFlag = true;
          break;
        }
      }

      List<Element> entryList = doc.getRootElement()
          .getChildren( "entry", atom );

      for ( Element entry : entryList )
      {
        String index = entry.getChildText( "formattedAssetIndex", ai );

        if ( index != null )
        {
          indexSet.add( index );
        }
      }

      page++;

    }

    return indexSet;

  }

  public ServiceCallResult paginatedSearch( String term, String aiIndexPattern,
      String type, String securityClass, Date publishDateFrom,
      Date publishDateTo, Set<String> categories,
      Set<String> secondaryCategories, int page, int max ) throws Exception
  {
    log.debug( "Searching on " + term + " page " + page );

    String serviceUri = GET_SEARCH_URI + "?q=" + term + "&p=" + page + "&c="
        + max;

    HttpMethod method = new GetMethod( serverAddress + serviceUri );

    return processMethod( method, "search" );

  }

  public ServiceCallResult getAssetMetadata( String year, String aiClassCode,
      String docnum, String lang ) throws Exception
  {

    String serviceUri = Constants.GET_ASSET_SERVICE_URI + "/" + year + "/"
        + aiClassCode + "/" + docnum + "?format=xml";
    if ( lang != null )
    {
      serviceUri = serviceUri + "&lang=" + lang;
    }

    log.debug( "using " + serverAddress + serviceUri );
    HttpMethod method = new GetMethod( serverAddress + serviceUri );

    return processMethod( method, "getAssetMetadata" );

  }

  // private org.w3c.dom.Document handleResponse( Page page )
  // throws Exception
  // {
  // WebResponse rsp = page.getWebResponse();
  //
  // // File outFile = new File( "rsp.txt" );
  // //
  // // FileWriter out = new FileWriter( outFile, true );
  // // out.write( "STARTING rsp.getContentAsString() at " + new Date() +
  // // "\n" );
  // // out.write( rsp.getContentAsString() );
  // // out.write( "STOPPED rsp.getContentAsString() at " + new Date() + "\n"
  // // );
  // // out.close();
  // //
  // if ( rsp.getStatusCode() != HttpStatus.SC_OK )
  // {
  // String msg = "Server responded with status code " + rsp.getStatusCode() +
  // " " + rsp.getStatusMessage();
  //
  // msg += " " + rsp.getContentAsString();
  //
  // throw new Exception( msg );
  // }
  //
  // if ( page instanceof XmlPage )
  // {
  //
  // XmlPage xmlPage = (XmlPage) page;
  // return xmlPage.getXmlDocument();
  //
  // }
  // else
  // {
  // throw new Exception( "Returned data was not in XML: " +
  // rsp.getContentAsString() );
  // }
  // }

  private ServiceCallResult processMethod( HttpMethod method, String opName )
  {
    ServiceCallResult result = new ServiceCallResult();

    method.setRequestHeader( "charset", "UTF-8" );
    // method.setRequestHeader( "charset", "iso-8859-1" );
    boolean parseOK = false;

    try
    {
      int BUFSIZE = 1024;
      char[] data = new char[BUFSIZE];
      int count;
      StringBuffer buffer = new StringBuffer();

      result.setHttpStatusCode( httpClient.executeMethod( method ) );
      result.setStatusText( method.getStatusText() );

      BufferedReader reader = new BufferedReader( new InputStreamReader( method
          .getResponseBodyAsStream() ) );
      while ( (count = reader.read( data )) != - 1 )
      {
        buffer.append( data, 0, count );
      }

      String response = buffer.toString();
      log.debug( opName + " RESPONSE: " + response );

      try
      {
        result.setOutputDocument( saxBuilder
            .build( new StringReader( response ) ) );
        parseOK = true;
      } catch ( Exception e )
      {
        // N.B. only API errors will return nice XML: others will not
        // parse, so errors parsing error output are not significant.
        if ( result.getHttpStatusCode() == HttpStatus.SC_OK )
        {
          result.setHttpStatusCode( ServiceCallResult.PARSE_ERROR );
          result.setStatusText( "Error parsing content: " + e.getMessage() );
        }
      }

    } catch ( Exception e )
    {
      result.setHttpStatusCode( ServiceCallResult.CONNECTION_ERROR );
      result.setStatusText( e.getMessage() );
    } finally
    {
      method.releaseConnection();
    }

    if ( result.getHttpStatusCode() != HttpStatus.SC_OK )
    {
      if ( parseOK )
      {
        // there should be a nice error doc in XML
        result.setStatusText( result.getOutputDocument().getRootElement()
            .getChildText( "message" ) );
      }

      result.setStatusText( opName + " failed with status "
          + result.getHttpStatusCode() + ": " + result.getStatusText() );
    }

    return result;
  }

  public ServiceCallResult createAsset( String aiClassCode, String type,
      String title, Integer year, Integer docnum, Date publishDate, String aiIndexType,
      String securityClass, Set<String> categories,
      Set<String> secondaryCategories ) throws Exception
  {
	  PostMethod method = createAssetMethod(aiClassCode, type, title,
			  year, docnum, publishDate, aiIndexType, securityClass,
			  null, null, null, categories, secondaryCategories);

    return processMethod( method, "createAsset" );

  }

  @Override
  public ServiceCallResult createAsset(String aiClassCode, String type,
  		String title, Integer year, Integer docnum, Date publishDate,
  		String aiIndexType, String securityClass, String originator,
  		String network, String networkNumber, Set<String> categories,
  		Set<String> secondaryCategories) throws Exception {
	  
	  PostMethod method = createAssetMethod(aiClassCode, type, title,
			  year, docnum, publishDate, aiIndexType, securityClass,
			  originator, network, networkNumber, categories, secondaryCategories);
	  
	  return processMethod( method, "createAsset" );
  }
  
  
  private PostMethod createAssetMethod(String aiClassCode, String type,
	  		String title, Integer year, Integer docnum, Date publishDate,
	  		String aiIndexType, String securityClass, String originator,
	  		String network, String networkNumber, Set<String> categories,
	  		Set<String> secondaryCategories)
  {
	    String publishDateString = null;
	    String docnumString = null;
	    String yearString = null;

	    if ( docnum != null )
	    {
	      docnumString = (new DecimalFormat( "000" )).format( docnum );
	    }

	    if ( year != null )
	    {
	      yearString = year.toString();
	    }

	    // publish date in ISO 8601 format, UTC with time truncated
	    if ( publishDate != null )
	    {
	      SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
	      TimeZone utc = TimeZone.getTimeZone( "UTC" );
	      sdf.setTimeZone( utc );
	      publishDateString = sdf.format( publishDate ) + "T00:00:00.000Z";
	    }

	    PostMethod method = new PostMethod( serverAddress
	        + Constants.CREATE_ASSET_URI );

	    if ( year != null )
	      method.addParameter( "year", yearString );

	    if ( aiClassCode != null )
	      method.addParameter( "class", aiClassCode );

	    if ( docnum != null )
	      method.addParameter( "docnum", docnumString );

	    if ( title != null )
	      method.addParameter( "title", title );

	    if ( type != null )
	      method.addParameter( "type", type );

	    if ( publishDate != null )
	      method.addParameter( "publishDate", publishDateString );

	    if ( securityClass != null )
	      method.addParameter( "securityClass", securityClass );

	    if ( aiIndexType != null )
	        method.addParameter( "aiIndexType", aiIndexType );
	    
	    if ( originator != null )
        method.addParameter( "originator", originator );
	    
	    if ( network != null )
	        method.addParameter( "network", network );
	    
	    if ( networkNumber != null )
	        method.addParameter( "networkNumber", networkNumber );

	    /* One category from each classification */
	    if ( categories != null )
	    {

	      for ( String category : categories )
	      {
	        method.addParameter( "category", category );
	      }
	    }

	    /* One category from each classification */
	    if ( secondaryCategories != null )
	    {
	      for ( String category : secondaryCategories )
	      {
	        method.addParameter( "secondaryCategory", category );
	      }
	    }
	    
	    return method;
  }
  // public void createTypeX( String aiIndex, String edition, String type, Map
  // properties )
  // throws Exception
  // {
  //
  // URL serviceUrl = new URL( serverAddress + Constants.CREATE_TYPE_URI );
  // WebRequestSettings req = new WebRequestSettings( serviceUrl,
  // SubmitMethod.POST );
  //
  // List requestParams = new ArrayList();
  //
  // if ( edition != null )
  // requestParams.add( new NameValuePair( "edition", edition ) );
  //
  // if ( type != null )
  // requestParams.add( new NameValuePair( "type", type ) );
  //
  // requestParams.add( new NameValuePair( "aiIndex", aiIndex ) );
  //
  // for ( int i = 0; i < Constants.TYPE_SVC_ALL_PARAMS.size(); i++ )
  // {
  // String param = (String) Constants.TYPE_SVC_ALL_PARAMS.get( i );
  //
  // if ( properties.get( param ) != null && properties.get( param ) != "" )
  // {
  // requestParams.add( new NameValuePair( param, (String) properties.get(
  // param ) ) );
  // }
  // }
  //
  // req.setCharset( "UTF-8" );
  // req.setRequestParameters( requestParams );
  //
  // Page page = webClient.getPage( req );
  //
  // handleResponse( page );
  //
  // }

  /*
   * (non-Javadoc)
   * 
   * @see org.amnesty.aidoc.service.AidocRestServiceClient#createType(java.lang.String,
   *      java.lang.String, java.lang.String, java.util.Map)
   */
  public ServiceCallResult createType( String aiIndex, String edition,
      String type, Map<String, String> properties ) throws Exception
  {

    PostMethod method = new PostMethod( serverAddress
        + Constants.CREATE_TYPE_URI );

    if ( edition != null )
      method.addParameter( "edition", edition );

    if ( type != null )
      method.addParameter( new NameValuePair( "type", type ) );

    method.addParameter( "aiIndex", aiIndex );

    for ( int i = 0; i < Constants.TYPE_SVC_ALL_PARAMS.size(); i++ )
    {
      String param = (String) Constants.TYPE_SVC_ALL_PARAMS.get( i );

      if ( properties != null && properties.get( param ) != null
          && properties.get( param ) != "" )
      {
        method.addParameter( param, (String) properties.get( param ) );
      }
    }

    return processMethod( method, "createType" );

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.amnesty.aidoc.service.AidocRestServiceClient#updateAsset(java.lang.String,
   *      java.lang.String, java.util.Set)
   */
  public ServiceCallResult updateAsset( String aiIndex, String title,
      Set<String> categories ) throws Exception
  {
    return updateAsset( aiIndex, title, null, null, null, null, null, categories, null, null, null );
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.amnesty.aidoc.service.AidocRestServiceClient#updateAsset(java.lang.String,
   *      java.lang.String, java.lang.String, java.util.Set, java.lang.String,
   *      java.lang.String, java.util.Set)
   */
  public ServiceCallResult updateAsset( String aiIndex, String title,
      String securityClass, String aiIndexType, String originator, String network,
      String networkNumber, Set<String> categories, String invalidated,
      String validityNotes, Set<String> secondaryCategories ) throws Exception
  {
    PostMethod method = new PostMethod( serverAddress
        + Constants.UPDATE_ASSET_SERVICE_URI );

    if ( aiIndex != null )
      method.addParameter( "aiIndex", aiIndex );

    if ( title != null )
      method.addParameter( "title", title );

    if ( invalidated != null )
      method.addParameter( "invalidated", invalidated );

    if ( validityNotes != null )
      method.addParameter( "validityNotes", validityNotes );

    if ( securityClass != null )
      method.addParameter( "securityClass", securityClass );

    if ( aiIndexType != null )
        method.addParameter( "aiIndexType", aiIndexType );
    
    if ( originator != null )
        method.addParameter( "originator", originator );
    
    if ( network != null )
        method.addParameter( "network", network );
    
    if ( networkNumber != null )
        method.addParameter( "networkNumber", networkNumber );
    
    if ( categories != null )
    {
      for ( String category : categories )
      {
        method.addParameter( "category", category );
      }
    }

    if ( secondaryCategories != null )
    {
      for ( String category : secondaryCategories )
      {
        method.addParameter( "secondaryCategory", category );
      }
    }

    return processMethod( method, "updateAsset" );

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.amnesty.aidoc.service.AidocRestServiceClient#updateAsset(java.lang.String,
   *      java.util.Map, java.util.Set, java.util.Set)
   */
  public ServiceCallResult updateAsset( String aiIndex,
      Map<String, String> map, Set<String> categories,
      Set<String> secondaryCategories ) throws Exception
  {
    PostMethod method = new PostMethod( serverAddress
        + UPDATE_ASSET_SERVICE_URI );

    if (( aiIndex == null ) || (aiIndex.length() == 0))
    {
      throw new Exception( "Missing AiIndex");
    }
    
    AiIndex.parse( aiIndex );
    
    method.addParameter( "aiIndex", aiIndex );

    Iterator<String> it = map.keySet().iterator();

    while ( it.hasNext() )
    {
      String key = it.next();
      String value = map.get( key );

      method.addParameter( key, value );
    }

    if ( categories != null )
    {
      for ( String category : categories )
      {
        method.addParameter( "category", category );
      }
    }

    if ( secondaryCategories != null )
    {
      for ( String category : secondaryCategories )
      {
        method.addParameter( "secondaryCategory", category );
      }
    }

    return processMethod( method, "updateAsset" );

  }
  public ServiceCallResult deleteAsset( String aiIndex ) throws Exception
  {
    PostMethod method = new PostMethod( serverAddress
        + DELETE_ASSET_SERVICE_URI );

    if (( aiIndex == null ) || (aiIndex.length() == 0))
    {
      throw new Exception( "Missing AiIndex");
    }
    
    AiIndex.parse( aiIndex );
    
    method.addParameter( "aiIndex", aiIndex );

    return processMethod( method, "deleteAsset" );
  }

  public ServiceCallResult deleteAssetContents( String aiIndex )
      throws Exception
  {
    PostMethod method = new PostMethod( serverAddress
        + DELETE_ASSET_CONTENTS_SERVICE_URI );

    if (( aiIndex == null ) || (aiIndex.length() == 0))
    {
      throw new Exception( "Missing AiIndex");
    }
    
    AiIndex.parse( aiIndex );
    
    method.addParameter( "aiIndex", aiIndex );

    return processMethod( method, "deleteAssetContents" );  
   }
  
  private static void addRequiredPostParameter( PostMethod method,
      String parameter, String value ) throws Exception
  {
    if ( parameter == null )
    {
      throw new Exception( "POST parameter name must not be null!" );
    }

    if ( value == null )
    {
      throw new Exception( "POST parameter " + parameter + " must not be null!" );
    }

    method.addParameter( parameter, value );

  }

  private static void addOptionalPostParameter( PostMethod method,
      String parameter, String value ) throws Exception
  {
    if ( parameter == null )
    {
      throw new Exception( "POST parameter name must not be null!" );
    }

    if ( value != null )
    {
      method.addParameter( parameter, value );
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.amnesty.aidoc.service.AidocRestServiceClient#updateType(java.lang.String,
   *      java.lang.String, java.lang.String, java.lang.String,
   *      java.lang.String, java.util.Map)
   */
  public ServiceCallResult updateType( String aiIndex, String type, String aiAuxiliaryType,
      String lang, String mimetype, String edition,
      Map<String, String> properties ) throws Exception
  {
    PostMethod method = new PostMethod( serverAddress
        + UPDATE_TYPE_SERVICE_URI );

    addRequiredPostParameter( method, "aiIndex", aiIndex );
    addRequiredPostParameter( method, "lang", lang );
    addRequiredPostParameter( method, "mimetype", mimetype );

    addOptionalPostParameter( method, "edition", edition );
    addOptionalPostParameter( method, "type", type );
    addOptionalPostParameter( method, "aiAuxiliaryType", aiAuxiliaryType );
    
    Iterator<String> iterator = properties.keySet().iterator();

    while ( iterator.hasNext() )
    {
      String propName = (String) iterator.next();
      String value = (String) properties.get( propName );

      log.debug( "adding parameter " + propName + "=" + value );
      method.addParameter( propName, value );
    }

    return processMethod( method, "updateType" );

  }

  public ServiceCallResult updateRendition( String aiIndex,
	      String lang,
	      Map<String, String> properties ) throws Exception
	  {
	    PostMethod method = new PostMethod( serverAddress
	        + UPDATE_RENDITION_SERVICE_URI );

	    addRequiredPostParameter( method, "aiIndex", aiIndex );
	    addRequiredPostParameter( method, "lang", lang );
	    
	    Iterator<String> iterator = properties.keySet().iterator();

	    while ( iterator.hasNext() )
	    {
	      String propName = (String) iterator.next();
	      String value = (String) properties.get( propName );

	      log.debug( "adding parameter " + propName + "=" + value );
	      method.addParameter( propName, value );
	    }

	    return processMethod( method, "updateRendition" );

	  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.amnesty.aidoc.service.AidocRestServiceClient#getTicket()
   */
  public String getTicket() throws Exception
  {

    String serviceUri = Constants.LOGIN_SERVICE_URI + "?u=" + serverLogin
        + "&pw=" + serverPassword;

    GetMethod method = new GetMethod( serverAddress + serviceUri );

    ServiceCallResult result = processMethod( method, "getTicket" );

    if ( result.failed() )
    {
      throw new Exception( result.getStatusText() );
    }

    Document doc = result.getOutputDocument();

    return doc.getRootElement().getText();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.amnesty.aidoc.service.AidocRestServiceClient#uploadFile(java.io.File)
   */
  public String uploadFile( File file ) throws Exception
  {
    if ( file == null || file.exists() == false )
    {
      throw new IllegalArgumentException( "File doesn't exist" );
    }
    String ticket = getTicket();

    int port;

    if ( serverPort == null )
    {
      port = (protocol.equalsIgnoreCase( "HTTP" )) ? 80 : 443;

    } else
    {
      port = Integer.valueOf( serverPort ).intValue();
    }

    String result = putContent( file, serverHost, port,
        "application/octet-stream", ticket );
    return result;
  }

  /*
   * private static final String getPropertyOrDefault( String propertyName,
   * String defaultValue ) { String value; if ( properties.get( propertyName ) !=
   * null ) { value = (String) properties.get( propertyName ); } else { value =
   * defaultValue; } return value; }
   */

  /**
   * NOTE: Copied from Alfresco ContentUtils.java and adapted to take a ticket
   * rather then get one using a Webservices call.
   * 
   * Streams content into the repository. Once done a content details string is
   * returned and this can be used to update a content property in a CML
   * statement.
   * 
   * (Added support for HTTPS - mc )
   * 
   * @param file
   *            the file to stream into the repository
   * @param host
   *            the host name of the destination repository
   * @param port
   *            the port name of the destination repository
   * @param mimetype
   *            the mimetype of the file, ignored if null
   * @param ticket
   *            an authentication ticket obtained from a login call
   * @return the content data that can be used to set the content property in a
   *         CML statement
   */
  @SuppressWarnings("deprecation")
  private String putContent( File file, String host, int port, String mimetype,
      String ticket )
  {
    final int BUFFER_SIZE = 4096;

    String result = null;

    try
    {
      String url = ((restAPIPath != null) ? restAPIPath : "") + "/upload/"
          + URLEncoder.encode( file.getName(), "UTF-8" ) + "?ticket=" + ticket;

      if ( mimetype != null )
      {
        url = url + "&mimetype=" + mimetype;
      }

      String request = "PUT " + url + " HTTP/1.1\r\n" + "Content-Length: "
          + file.length() + "\r\n" + "Host: " + host + ":" + port + "\r\n"
          + "Connection: Keep-Alive\r\n" + "\r\n";

      // Open sockets and streams

      Socket socket = null;

      if ( protocol.equalsIgnoreCase( "HTTP" ) )
      {
        socket = new Socket( host, port );
      } else
      {
        // Create an instance of the factory
        SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory
            .getDefault();

        // Create a socket and connect
        socket = (SSLSocket) sslFactory.createSocket( host, port );
      }

      // from
      // http://jakarta.apache.org/httpcomponents/httpclient-3.x/sslguide.html

      // public class Test {
      //                
      // public static final String TARGET_HTTPS_SERVER =
      // "www.verisign.com";
      // public static final int TARGET_HTTPS_PORT = 443;
      //                   
      // public static void main(String[] args) throws Exception {
      //                   
      // Socket socket = SSLSocketFactory.getDefault().
      // createSocket(TARGET_HTTPS_SERVER, TARGET_HTTPS_PORT);
      // try {
      // Writer out = new OutputStreamWriter(
      // socket.getOutputStream(), "ISO-8859-1");
      // out.write("GET / HTTP/1.1\r\n");
      // out.write("Host: " + TARGET_HTTPS_SERVER + ":" +
      // TARGET_HTTPS_PORT + "\r\n");
      // out.write("Agent: SSL-TEST\r\n");
      // out.write("\r\n");
      // out.flush();
      // BufferedReader in = new BufferedReader(
      // new InputStreamReader(socket.getInputStream(), "ISO-8859-1"));
      // String line = null;
      // while ((line = in.readLine()) != null) {
      // System.out.println(line);
      // }
      // } finally {
      // socket.close();
      // }
      // }

      DataOutputStream output = new DataOutputStream( socket.getOutputStream() );
      DataInputStream input = new DataInputStream( socket.getInputStream() );

      try
      {
        if ( socket != null && output != null && input != null )
        {
          // Write the request header
          output.writeBytes( request );
          output.flush();

          // Stream the content onto the server
          InputStream fileInputStream = new FileInputStream( file );

          int byteCount = 0;

          byte[] buffer = new byte[BUFFER_SIZE];

          int bytesRead = - 1;

          while ( (bytesRead = fileInputStream.read( buffer )) != - 1 )
          {
            output.write( buffer, 0, bytesRead );
            byteCount += bytesRead;
          }
          output.flush();
          fileInputStream.close();

          // Read the response and deal with any errors that might
          // occur

          boolean firstLine = true;

          String responseLine;

          while ( (responseLine = input.readLine()) != null )
          {
            if ( firstLine == true )
            {
              if ( responseLine.indexOf( "200" ) != - 1 )
              {
                firstLine = false;
              } else if ( responseLine.indexOf( "401" ) != - 1 )
              {
                throw new RuntimeException(
                    "Content could not be uploaded because invalid credentials have been supplied." );
              } else if ( responseLine.indexOf( "403" ) != - 1 )
              {
                throw new RuntimeException(
                    "Content could not be uploaded because user does not have sufficient priveledges." );
              } else
              {
                throw new RuntimeException(
                    "Error returned from upload servlet (" + responseLine + ")" );
              }
            } else if ( responseLine.indexOf( "contentUrl" ) != - 1 )
            {
              result = responseLine;
              break;
            }
          }
        }
      } finally
      {
        try
        {
          // Close the streams and socket
          if ( output != null )
          {
            output.close();
          }
          if ( input != null )
          {
            input.close();
          }
          if ( socket != null )
          {
            socket.close();
          }
        } catch ( Exception e )
        {
          throw new RuntimeException( "Error closing sockets and streams", e );
        }
      }
    } catch ( Exception e )
    {
      throw new RuntimeException( "Error writing content to repository server",
          e );
    }

    return result;
  }




  // static class XMLFilter
  // extends FilterInputStream
  // {
  //
  // /**
  // * @param in
  // */
  // public XMLFilter( InputStream in )
  // {
  // super( in );
  // // TODO Auto-generated constructor stub
  // }
  //
  // @Override
  // public int read()
  // throws IOException
  // {
  // int value = in.read();
  //
  // if ( value == -1 )
  // {
  // return value;
  // }
  //
  // byte current = (byte) value;
  //
  // if ( ( current == 0x9 ) || ( current == 0xA ) || ( current == 0xD ) || ( (
  // current >= 0x20 ) && ( current <= 0xD7FF ) ) || ( ( current >= 0xE000 ) &&
  // ( current <= 0xFFFD ) ) || ( ( current >= 0x10000 ) && ( current <=
  // 0x10FFFF ) ) )
  // {
  // return value;
  // }
  //
  // return read();
  // }
  //
  // }

}
