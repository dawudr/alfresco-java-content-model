package org.amnesty.aidoc.webscript;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.amnesty.aidoc.AiIndex;
import org.amnesty.aidoc.service.AidocRestServiceClientImpl;
import org.amnesty.aidoc.service.ServiceCallResult;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.gargoylesoftware.htmlunit.WebClient;

public class CompareAssetWebScriptTest extends TestCase {

	AidocRestServiceClientImpl aidoc = new AidocRestServiceClientImpl();
	
	WebClient webClient;
	ArrayList<String> aiIndexArray = new ArrayList<String>();
	ArrayList<String> failArray = new ArrayList<String>();
	ArrayList<String> passArray = new ArrayList<String>();
	File indexFile;
	File failFile;
	File passFile;
	String CONTEXT= "/service/library";
	String JS_SCRIPT_NAME= "asset";
	String JAVA_SCRIPT_NAME= "asset2";
	String INDEX_FILENAME="src/test/resources/index-list.csv";
	String FAIL_FILENAME="src/test/resources/fail-list.csv";
	String PASS_FILENAME="src/test/resources/pass-list.csv";
	
	public CompareAssetWebScriptTest() throws MalformedURLException {
	}
	

	protected void setUp() throws Exception {
		
		super.setUp();
        aidoc.configureFromProperties();

		indexFile = new File(INDEX_FILENAME);
		Reader indexFileReader = new FileReader(indexFile);

		failFile = new File(FAIL_FILENAME);
		
		
		passFile = new File(PASS_FILENAME);
		
		
		CsvReader indexReader = new CsvReader(indexFileReader);
		
		indexReader.readHeaders();

		while (indexReader.readRecord())
		{
			String AiIndex = indexReader.get("AiIndex");
			aiIndexArray.add(AiIndex);
			// perform program logic here

		}

		indexReader.close();
		
		if(failFile.exists())
		{
			Reader failFileReader = new FileReader(failFile);
			CsvReader failReader = new CsvReader(failFileReader);
			
			failReader.readHeaders();
	
			while (failReader.readRecord())
			{
				String AiIndex = failReader.get("AiIndex");
				failArray.add(AiIndex);
				// perform program logic here
	
			}

			failReader.close();
			failFileReader.close();
		}
		
		if(passFile.exists())
		{
			Reader passFileReader = new FileReader(passFile);
			CsvReader passReader = new CsvReader(passFileReader);
			
			passReader.readHeaders();
	
			while (passReader.readRecord())
			{
				String AiIndex = passReader.get("AiIndex");
				passArray.add(AiIndex);
				// perform program logic here
	
			}
	
			passReader.close();
			passFileReader.close();
		}
		
	}

	public void testCompareAsset() throws Exception {

		int failedAiIndex = 0;
		int passedAiIndex = 0;
		Charset charset = Charset.forName("utf-8");
		CsvWriter failFileWriter = null;

		File fail = new File(FAIL_FILENAME);
		if(!fail.exists())
		{
			failFileWriter = new CsvWriter(new FileOutputStream(fail,true), ',',charset);
			String[] header = new String[]{"AiIndex","codeJava","codeJS","message"};
			failFileWriter.writeRecord(header);
			//failFileWriter.endRecord();
			failFileWriter.flush();
		}
		else
		{
			failFileWriter = new CsvWriter(new FileOutputStream(fail,true), ',',charset);
		}
		
		CsvWriter passFileWriter = null;

		File pass = new File(PASS_FILENAME);
		if(!pass.exists())
		{
			passFileWriter = new CsvWriter(new FileOutputStream(pass,true), ',',charset);
			String[] header = new String[]{"AiIndex","codeJava","codeJS"};
			passFileWriter.writeRecord(header);
			//passFileWriter.endRecord();
			passFileWriter.flush();
		}
		else
		{
			passFileWriter = new CsvWriter(new FileOutputStream(pass,true), ',',charset);

		}
		
		
		
		
		
		for (int i = 0; i < aiIndexArray.size(); i++) {
			
			String aiIndex = aiIndexArray.get(i);
			System.out.println("AiIndex: " + aiIndex);
			
			
			String [] indexArray = aiIndex.split("/");
			
			String year = indexArray[0];
			String classCode = indexArray[1];
			String documentNo = indexArray[2];
			
			
			if(!failArray.contains(aiIndex) && !passArray.contains(aiIndex) )
			{

			AiIndex index = AiIndex.parse(classCode+"/"+documentNo+"/"+year);

			// String AiIndex = "/2009/AMR53/001";
			
			ServiceCallResult out1 = aidoc.getAssetMetadata(index.getYear(), index .getAiClass(), index.getDocnum(), "en");
			
			ServiceCallResult out2 = aidoc.getAssetMetadata(index.getYear(), index .getAiClass(), index.getDocnum(), "en");
			
			int code1 = out1.getHttpStatusCode();
			int code2 = out2.getHttpStatusCode();

			String code1String = String.valueOf(code1);
			String code2String = String.valueOf(code2);
			String result1 = out1.getOutputDocument().toString();
			String result2 = out2.getOutputDocument().toString();
			
			if (code1 == code2 && code1==200)
			{

				if (!result1.equals(result2)) {
					
					String[] record = new String[]{aiIndex,code1String,code2String,"Content missmatch"};
					failFileWriter.writeRecord(record);
					//failFileWriter.endRecord();
					failFileWriter.flush();
						//failFileWriter.newLine();
						
					failedAiIndex++;
					
				} else {
					String[] record = new String[]{aiIndex,code1String,code2String};
					passFileWriter.writeRecord(record);
					//passFileWriter.endRecord();
					passFileWriter.flush();
						//passWriter.newLine();

					passedAiIndex++;

				}
			}
			else if(code1 == code2 && code1!=200)
			{

				if (!result1.equals(result2)) {
					String[] record = new String[]{aiIndex,code1String,code2String,"Error message missmatch"};
					failFileWriter.writeRecord(record);
					//failFileWriter.endRecord();
					failFileWriter.flush();
						//failWriter.newLine();
						
					failedAiIndex++;
					
				} else {
					String[] record = new String[]{aiIndex,code1String,code2String};
					passFileWriter.writeRecord(record);
					//passFileWriter.endRecord();
					passFileWriter.flush();
						//passWriter.newLine();

					passedAiIndex++;

				}
			}
			else if(code1 != code2)
			{
				String[] record = new String[]{aiIndex,code1String,code2String,"Code missmatch"};
				failFileWriter.writeRecord(record);
				//failFileWriter.endRecord();
				failFileWriter.flush();
				//failWriter.newLine();
				failedAiIndex++;
			}
			else{
				System.err.println("Unknown error");
				System.err.println("Code JS "+code1);
				System.err.println("Code JAVA "+code2);
				System.err.println("Content JS"+result1);
				System.err.println("Content JAVA"+result2);
			}

			// System.out.println(page1.getWebResponse().getContentAsString());
			// System.out.println(page2.getWebResponse().getContentAsString());

			// assertEquals(page1.getWebResponse().getStatusCode(),
			// page2.getWebResponse().getStatusCode());
			
			
			
			}
			else{
				System.out.println("Index found in pass or fail lists");
			}
		}
		
		failFileWriter.close();
		passFileWriter.close();
		System.out.println("Sample size:" + aiIndexArray.size() + " Success:" + passedAiIndex + " Fail:" + failedAiIndex);
		assertTrue(aiIndexArray.size() == passedAiIndex+failedAiIndex);



		
		
	}
}
