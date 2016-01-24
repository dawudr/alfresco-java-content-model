package org.amnesty.aidoc.webscript;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;

import org.amnesty.aidoc.service.AidocServiceClientRemoteImpl;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.SubmitMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class GenerateTestSample {

	public GenerateTestSample() throws Exception {

		ArrayList<String> aiIndexArray;
		AidocServiceClientRemoteImpl aidoc = new AidocServiceClientRemoteImpl();
		WebClient webClient;
		String serverUrl = "https://cmsqa.amnesty.org";
		String serverLogin = "******";
		String serverPassword = "******";
		int documentsFound = 0;
		System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "error");

		DefaultCredentialsProvider credentialsProvider = new DefaultCredentialsProvider();
		credentialsProvider.addCredentials(serverLogin, serverPassword);
		webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_6_0);
		webClient.setCredentialsProvider(credentialsProvider);
		webClient.setThrowExceptionOnFailingStatusCode(false);

		int k = 0;
		String[] classCode = { "ACT", "AFR", "AMR", "ASA", "DOC", "EUR", "FIN", "IOR", "MDE", "NWS", "ORG", "POL", "PRE", "REG"};
		int classYear = 2005;
		int sampleQuantity = 10000;
		int retry =0;
		int classCodeNum = 1;
		int docNum = 1;
		int skipIfNotFound = 0;

		for (int i = 0; i < sampleQuantity; i++) {

			if (k == classCode.length) {
				k = 0;
				classYear--;
			}
			
			if (classYear == 1983)
				break;

			
						
			String AiIndex = "/" + classYear + "/" + classCode[k] + classCodeNum + "/" + docNum;
			
			// String AiIndex = "/2009/AMR53/001";

			URL JS_URL = new URL("https://cmsqa.amnesty.org/service/library/index" + AiIndex);
			WebRequestSettings req = new WebRequestSettings(JS_URL, SubmitMethod.GET);
			HtmlPage page1 = (HtmlPage) webClient.getPage(req);
			if (page1.getWebResponse().getStatusCode() == 401) {
				retry++;
				if (retry == 2) {
					throw new Exception("Invalid Server Credentials");
				} else {
					continue;
				}
			}
			else if (page1.getWebResponse().getStatusCode() == 503) {
				System.out.println("Documents found:" + documentsFound);				
				throw new Exception("Service Temporarily Unavailable");
			}
			else if (page1.getWebResponse().getStatusCode() == 200) {
				FileWriter fWriter = null;
				BufferedWriter writer = null;
				try {
					fWriter = new FileWriter("\\test-sample2.txt", true);
					writer = new BufferedWriter(fWriter);
					writer.write(AiIndex);
					writer.newLine();
					documentsFound++;
					writer.close();
				} catch (Exception e) {
				}
				retry = 0;
				docNum++;
				skipIfNotFound = 0;
			}	
			else if (page1.getWebResponse().getStatusCode() == 500) {
				classCodeNum++;
				skipIfNotFound++;
				
				if(classCodeNum > 80 || skipIfNotFound > 12) {
					k++;
					classCodeNum = 1;
					skipIfNotFound = 0;
				}
					
				docNum = 1;
			}
			else if (page1.getWebResponse().getStatusCode() == 404) {
				docNum++;
			}
			System.out.println("Current aiIndex[" + AiIndex + "] Sample Quantity[" + (i + 1) + "/" + sampleQuantity + "]");
		}
		
		System.out.println("Documents found:" + documentsFound);
	}

	public static void main(String[] args) {

		try {
			GenerateTestSample sample = new GenerateTestSample();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
