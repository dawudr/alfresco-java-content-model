package org.amnesty.aidoc.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.util.ISO9075;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLDelete;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.WebServiceFactory;
import org.amnesty.aidoc.Constants;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.SubmitMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

/**
 * A helper class that provides Java routines to make calls to our Alfresco
 * services via the Rest API. Call are made using the HtmlUnit library.
 * 
 * This class looks for the file aidoc-service.properties on the classpath. The
 * properties that can be defined are as follows and if not found are set to the
 * default shown:
 * 
 * alfresco.server.port (Default: 8080) alfresco.server.host (Default:
 * localhost) alfresco.server.login (Default: admin) alfresco.server.password
 * ((Default: admin)
 * 
 * @author chatch
 */

@Deprecated
public class AidocServiceClientRemoteImpl implements AidocServiceClient {

    private static final Log logger = LogFactory
            .getLog(AidocServiceClientRemoteImpl.class);

    private static final Properties properties = new Properties();
    static {
        final String PROP_FILENAME = "aidoc-service.properties";
        InputStream is = AidocServiceClientRemoteImpl.class.getClassLoader()
                .getResourceAsStream(PROP_FILENAME);
        try {
            properties.load(is);
            is.close();
        } catch (Exception e) {
            logger.error("Failed to load properties from " + PROP_FILENAME
                    + ". Will use defaults ...", e);
        }
    }

    public static final String SERVER_PORT = getPropertyOrDefault(
            "alfresco.server.port", "8080");

    public static final String SERVER_HOST = getPropertyOrDefault(
            "alfresco.server.host", "localhost");

    public static final String SERVER_PATH = getPropertyOrDefault(
            "alfresco.server.path", "/alfresco");

    public static final String SERVER_LOGIN = getPropertyOrDefault(
            "alfresco.server.login", "admin");

    public static final String SERVER_PASSWORD = getPropertyOrDefault(
            "alfresco.server.password", "admin");

    public static String SERVER_ADDRESS = "http://" + SERVER_HOST + ":"
            + SERVER_PORT + SERVER_PATH;

    public static final String WEBSERVICE_ADDRESS = SERVER_ADDRESS + "/api";

    static {
        /*
         * chop off trailing "/" if there is one as all URIs are appended
         * starting with '/'
         */
        if (SERVER_ADDRESS.endsWith("/")) {
            SERVER_ADDRESS = SERVER_ADDRESS.substring(0, SERVER_ADDRESS
                    .length() - 1);
        }
        logger.info("server: " + SERVER_ADDRESS);
    }

    private static final WebClient webClient;
    static {
        DefaultCredentialsProvider credentialsProvider = new DefaultCredentialsProvider();
        credentialsProvider.addCredentials(SERVER_LOGIN, SERVER_PASSWORD);
        webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_6_0);
        webClient.setCredentialsProvider(credentialsProvider);
        webClient.setThrowExceptionOnFailingStatusCode(false);
    }

    public Document getAssetMetadata(String year, String aiClass,
            String docnum, String lang) throws Exception {

        String serviceUri = Constants.GET_ASSET_SERVICE_URI + "/" + year + "/"
                + aiClass + "/" + docnum + "?format=xml";
        if (lang != null) {
            serviceUri = serviceUri + "&lang=" + lang;
        }
        URL serviceUrl = new URL(SERVER_ADDRESS + serviceUri);

        WebRequestSettings req = new WebRequestSettings(serviceUrl,
                SubmitMethod.GET);
        req.setCharset("UTF-8");

        XmlPage page = (XmlPage) webClient.getPage(req);

        WebResponse rsp = page.getWebResponse();
        if (rsp.getStatusCode() != 200) {
            throw new Exception("getAsset service call failed: "
                    + rsp.getStatusMessage());
        }

        return page.getXmlDocument();
    }

    public String createAsset(String aiClass, String type, String title,
            String year, String docnum, String publishDate,
            String securityClass, List<String> categories, List<String> secondaryCategories) throws Exception {
        URL serviceUrl = new URL(SERVER_ADDRESS + Constants.CREATE_ASSET_URI);
        WebRequestSettings req = new WebRequestSettings(serviceUrl,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        if (year != null)
            requestParams.add(new NameValuePair("year", year));
        if (aiClass != null)
            requestParams.add(new NameValuePair("class", aiClass));
        if (docnum != null)
            requestParams.add(new NameValuePair("docnum", docnum));
        if (title != null)
            requestParams.add(new NameValuePair("title", title));
        if (type != null)
            requestParams.add(new NameValuePair("type", type));
        if (publishDate != null)
            requestParams.add(new NameValuePair("publishDate", publishDate));
        if (securityClass != null)
            requestParams
                    .add(new NameValuePair("securityClass", securityClass));

        /* One category from each classification */
        if (categories != null) {
            for (String category : categories) {
                requestParams.add(new NameValuePair("category", category));
            }
        }
        
        /* One category from each classification */
        if (secondaryCategories != null) {
            for (String category : secondaryCategories) {
                requestParams.add(new NameValuePair("secondaryCategory", category));
            }
        }

        req.setCharset("UTF-8");
        req.setRequestParameters(requestParams);

        XmlPage page = (XmlPage) webClient.getPage(req);

        WebResponse rsp = page.getWebResponse();
        if (rsp.getStatusCode() != HttpServletResponse.SC_OK) {
            String message = page.getXmlDocument().getElementsByTagName(
                    "message").item(0).getTextContent();
            throw new Exception(message + "(createAsset)");
        }

        return page.getXmlDocument().getDocumentElement().getFirstChild()
                .getNodeValue();
    }

    public void createType(String aiIndex, String edition, String type,
            Map<String, String> properties) throws Exception {
        URL serviceUrl = new URL(SERVER_ADDRESS + Constants.CREATE_TYPE_URI);
        WebRequestSettings req = new WebRequestSettings(serviceUrl,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        if (edition != null)
            requestParams.add(new NameValuePair("edition", edition));
        if (type != null)
            requestParams.add(new NameValuePair("type", type));

        requestParams.add(new NameValuePair("aiIndex", aiIndex));

        for (String param : Constants.TYPE_SVC_ALL_PARAMS) {
            if (properties.get(param) != null && properties.get(param) != "") {
                requestParams.add(new NameValuePair(param, properties
                        .get(param)));
            }
        }

        req.setCharset("UTF-8");
        req.setRequestParameters(requestParams);

        XmlPage page = (XmlPage) webClient.getPage(req);

        WebResponse rsp = page.getWebResponse();
        if (rsp.getStatusCode() != HttpServletResponse.SC_OK) {
            String message = page.getXmlDocument().getElementsByTagName(
                    "message").item(0).getTextContent();
            throw new Exception(message + "(createType)");
        }
    }

    public void updateAsset(String aiIndex, String title,
            List<String> categories) throws Exception {
        updateAsset(aiIndex, title, null, categories, null, null, null);
    }

    public void updateAsset(String aiIndex, String title, String securityClass,
            List<String> categories, String invalidated, String validityNotes,
            List<String> secondaryCategories)
            throws Exception {
        URL serviceUrl = new URL(SERVER_ADDRESS
                + Constants.UPDATE_ASSET_SERVICE_URI);
        WebRequestSettings req = new WebRequestSettings(serviceUrl,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        if (aiIndex != null)
            requestParams.add(new NameValuePair("aiIndex", aiIndex));
        if (title != null)
            requestParams.add(new NameValuePair("title", title));
        if (invalidated != null)
            requestParams.add(new NameValuePair("invalidated", invalidated));
        if (validityNotes != null)
            requestParams
                    .add(new NameValuePair("validityNotes", validityNotes));
        if (securityClass != null)
            requestParams
                    .add(new NameValuePair("securityClass", securityClass));

        if (categories != null) {
            for (String category : categories) {
                requestParams.add(new NameValuePair("category", category));
            }
        }

        if (secondaryCategories != null) {
            for (String category : secondaryCategories) {
                requestParams.add(new NameValuePair("secondaryCategory", category));
            }
        }

        req.setCharset("UTF-8");
        req.setRequestParameters(requestParams);

        XmlPage page = (XmlPage) webClient.getPage(req);

        WebResponse rsp = page.getWebResponse();
        if (rsp.getStatusCode() != 200) {
            throw new Exception("updateAsset service call failed: "
                    + rsp.getStatusMessage());
        }
    }

    public void updateType(String aiIndex, String type, String lang,
            String mimetype, Map<String, String> properties) throws Exception {
        URL serviceUrl = new URL(SERVER_ADDRESS
                + Constants.UPDATE_TYPE_SERVICE_URI);
        WebRequestSettings req = new WebRequestSettings(serviceUrl,
                SubmitMethod.POST);

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new NameValuePair("aiIndex", aiIndex));
        requestParams.add(new NameValuePair("type", type));
        requestParams.add(new NameValuePair("lang", lang));
        requestParams.add(new NameValuePair("mimetype", mimetype));

        for (String propName : properties.keySet()) {
            if (properties.get(propName) != null
                    && properties.get(propName) != "") {
                requestParams.add(new NameValuePair(propName, properties
                        .get(propName)));
            }
        }

        req.setCharset("UTF-8");
        req.setRequestParameters(requestParams);

        XmlPage page = (XmlPage) webClient.getPage(req);

        WebResponse rsp = page.getWebResponse();
        if (rsp.getStatusCode() != 200) {
            throw new Exception("updateType service call failed: "
                    + rsp.getStatusMessage());
        }
    }

    /**
     * Takes a username and password and returns an authentication ticket.
     * 
     * @param username
     *            Alfresco username
     * @param password
     *            Password of username.
     * @return ticket string value
     */
    public String getTicket(String username, String password) throws Exception {
        String serviceUri = Constants.LOGIN_SERVICE_URI + "?u=" + username
                + "&pw=" + password;

        URL serviceUrl = new URL(SERVER_ADDRESS + serviceUri);

        WebRequestSettings req = new WebRequestSettings(serviceUrl,
                SubmitMethod.GET);
        req.setCharset("UTF-8");

        XmlPage page = (XmlPage) webClient.getPage(req);

        WebResponse rsp = page.getWebResponse();
        if (rsp.getStatusCode() != 200) {
            throw new Exception("login service call failed: "
                    + rsp.getStatusMessage());
        }

        return page.getXmlDocument().getFirstChild().getTextContent();
    }

    /**
     * Get ticket using login and password found in aidoc-service.properties on
     * the classpath.
     * 
     * @return ticket string value
     * @throws Exception
     */
    public String getTicket() throws Exception {
        return getTicket(SERVER_LOGIN, SERVER_PASSWORD);
    }

    /**
     * Upload a file to alfresco and get back the contenturl
     */
    public String uploadFile(File file) throws Exception {
        if (file == null || file.exists() == false) {
            throw new IllegalArgumentException("file doesn't exist");
        }
        String ticket = getTicket(SERVER_LOGIN, SERVER_PASSWORD);
        String result = putContent(file, SERVER_HOST, Integer
                .valueOf(SERVER_PORT), "application/octet-stream", ticket);
        return result;
    }

    /**
     * Delete a year folder
     * 
     * @param year
     *            Year part of the Ai Index
     * @return true if deleted, false if not or exception occured
     */
    public boolean deleteYearFolder(String year) {
        return deleteFolder("/" + year);
    }

    /**
     * Delete an Ai Index folder.
     * 
     * @param year
     *            Year part of the Ai Index
     * @param aiClass
     *            Class part of the Ai Index
     * @param docnum
     *            Document number part of the Ai Index
     * @return true if deleted, false if not or exception occured
     */
    public boolean deleteAiIndexFolder(String year, String aiClass,
            String docnum) {
        return deleteFolder("/" + year + "/" + aiClass + "/" + docnum);
    }

    /**
     * Deletes a folder relative to company_home
     * 
     * @param path
     *            Relative path from Index Documents Eg. '/2006/AFR20'
     * @return true if deleted, false if not or exception occured
     */
    private static boolean deleteFolder(String path) {
        StringBuffer aiIndexPath = new StringBuffer();
        try {
            WebServiceFactory.setEndpointAddress(WEBSERVICE_ADDRESS);

            AuthenticationUtils.startSession(SERVER_LOGIN, SERVER_PASSWORD);

            Store store = new Store(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");

            aiIndexPath.append(Constants.INDEXED_DOCS_XPATH);

            // strip off leading '/' before splitting on '/'
            path = path.substring(1, path.length());
            for (String folder : path.split("/")) {
                aiIndexPath.append("/cm:" + ISO9075.encode(folder));
            }

            Reference folderRef = new Reference(store, null, aiIndexPath
                    .toString());
            Predicate predicate = new Predicate(new Reference[] { folderRef },
                    null, null);

            CMLDelete delete = new CMLDelete();
            delete.setWhere(predicate);
            CML cmlDelete = new CML();
            cmlDelete.setDelete(new CMLDelete[] { delete });

            UpdateResult[] results = WebServiceFactory.getRepositoryService()
                    .update(cmlDelete);
            return (results.length == 1);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();
            if (stackTrace.contains("found 0 nodes")) {
                logger.debug("folder does not exist at [" + aiIndexPath + "]");
            } else {
                logger.error("exception thrown deleting folder [" + aiIndexPath
                        + "]", e);
            }
            return false;
        }
    }

    private static final String getPropertyOrDefault(String propertyName,
            String defaultValue) {
        String value;
        if (properties.get(propertyName) != null) {
            value = (String) properties.get(propertyName);
        } else {
            value = defaultValue;
        }
        return value;
    }

    /**
     * NOTE: Copied from Alfresco ContentUtils.java and adapted to take a ticket
     * rather then get one using a Webservices call.
     * 
     * Streams content into the repository. Once done a content details string
     * is returned and this can be used to update a content property in a CML
     * statement.
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
     * @return the content data that can be used to set the content property in
     *         a CML statement
     */

    private static String putContent(File file, String host, int port,
            String mimetype, String ticket) {
        final int BUFFER_SIZE = 4096;

        String result = null;

        try {
            String url = SERVER_PATH + "/upload/"
                    + URLEncoder.encode(file.getName(), "UTF-8") + "?ticket="
                    + ticket;
            if (mimetype != null) {
                url = url + "&mimetype=" + mimetype;
            }

            String request = "PUT " + url + " HTTP/1.1\n" + "Content-Length: "
                    + file.length() + "\n" + "Host: " + host + ":" + port
                    + "\n" + "Connection: Keep-Alive\n" + "\n";

            // Open sockets and streams
            Socket socket = new Socket(host, port);
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());

            try {
                if (socket != null && os != null && is != null) {
                    // Write the request header
                    os.writeBytes(request);

                    // Stream the content onto the server
                    InputStream fileInputStream = new FileInputStream(file);
                    int byteCount = 0;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead = -1;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                        byteCount += bytesRead;
                    }
                    os.flush();
                    fileInputStream.close();

                    // Read the response and deal with any errors that might
                    // occur
                    boolean firstLine = true;
                    String responseLine;
                    while ((responseLine = is.readLine()) != null) {
                        if (firstLine == true) {
                            if (responseLine.contains("200") == true) {
                                firstLine = false;
                            } else if (responseLine.contains("401") == true) {
                                throw new RuntimeException(
                                        "Content could not be uploaded because invalid credentials have been supplied.");
                            } else if (responseLine.contains("403") == true) {
                                throw new RuntimeException(
                                        "Content could not be uploaded because user does not have sufficient priveledges.");
                            } else {
                                throw new RuntimeException(
                                        "Error returned from upload servlet ("
                                                + responseLine + ")");
                            }
                        } else if (responseLine.contains("contentUrl") == true) {
                            result = responseLine;
                            break;
                        }
                    }
                }
            } finally {
                try {
                    // Close the streams and socket
                    if (os != null) {
                        os.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Error closing sockets and streams", e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error writing content to repository server", e);
        }

        return result;
    }

}
