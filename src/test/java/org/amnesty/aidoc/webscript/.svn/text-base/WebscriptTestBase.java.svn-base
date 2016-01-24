package org.amnesty.aidoc.webscript;

import java.net.MalformedURLException;
import java.net.URL;

import org.amnesty.aidoc.service.AidocServiceClientRemoteImpl;

import junit.framework.TestCase;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Base class for our rest api tests.
 * 
 * @author chatch
 */
public abstract class WebscriptTestBase extends TestCase {

    protected final URL SERVICE_URL;

    protected WebClient webClient;

    /**
     * Sets up WebClienthandle with auth credentials and builds URL to the
     * service using port number and passed URI.
     * 
     * @param serviceUri
     *            Service uri that follows the alfresco root address
     * @throws MalformedURLException
     */
    public WebscriptTestBase(String serviceUri) throws MalformedURLException {
        DefaultCredentialsProvider credentialsProvider = new DefaultCredentialsProvider();
        credentialsProvider.addCredentials(AidocServiceClientRemoteImpl.SERVER_LOGIN,
                AidocServiceClientRemoteImpl.SERVER_PASSWORD);

        webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_6_0);
        webClient.setCredentialsProvider(credentialsProvider);

        SERVICE_URL = new URL(AidocServiceClientRemoteImpl.SERVER_ADDRESS + serviceUri);
    }

}
