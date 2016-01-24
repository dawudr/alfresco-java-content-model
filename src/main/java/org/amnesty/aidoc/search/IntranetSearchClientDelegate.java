package org.amnesty.aidoc.search;

import java.io.IOException;
import java.io.InputStream;

import net.sf.gsaapi.GSAClientDelegate;

import org.amnesty.aidoc.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IntranetSearchClientDelegate implements GSAClientDelegate {
	
	private static final Log logger = LogFactory.getLog(IntranetSearchClientDelegate.class);
	private String user;
	private String password;
	@Override
	public InputStream getResponseStream(String requestUrl) {
	
		Protocol.registerProtocol("https", 
				new Protocol("https", (ProtocolSocketFactory)new EasySSLProtocolSocketFactory(), 443));
		HttpClient httpclient = new HttpClient();
			HttpClientParams params = new HttpClientParams();
		  params.setParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, true);
		  httpclient.setParams(params);  
		  
		  GetMethod httpget = new GetMethod(requestUrl); 
		  httpget.setFollowRedirects(true);
		  httpget.setRequestHeader("Cookie", "COOKIETEST=1");

		    try {
		    	
				httpclient.executeMethod(httpget);

				return httpget.getResponseBodyAsStream();
				
			} catch (HttpException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
		  } 
			return null;
	}
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
