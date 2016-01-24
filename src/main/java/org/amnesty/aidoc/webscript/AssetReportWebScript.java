package org.amnesty.aidoc.webscript;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import net.sf.acegisecurity.BadCredentialsException;

import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.amnesty.aidoc.Asset;
import org.amnesty.aidoc.AssetManager;
import org.amnesty.aidoc.AssetRendition;
import org.amnesty.aidoc.Rendition;
import org.amnesty.aidoc.Util;
import org.amnesty.aidoc.report.AssetReportImpl;
import org.amnesty.aidoc.report.CategoryReportImpl;
import org.amnesty.aidoc.report.RenditionReportImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

public class AssetReportWebScript extends AbstractWebScript {

	
	private NodeService nodeService;

	private FileFolderService fileFolderService;

	private static final Log logger = LogFactory.getLog(AssetReportWebScript.class);

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res)
			throws IOException {
		
		Map<String, String> args = req.getServiceMatch().getTemplateVars();
		
		try
		{
	        
	    String documentYear = Util.readDocumentYear(args.get("year"), true );
		String classCode = Util.readClassCode(args.get("aiclasssubclass"), true );
		String documentNo = Util.readDocumentNo(args.get("documentno"), true );
	    
		NodeRef assetNode = Util.resolveAssetNode(nodeService, fileFolderService, documentYear, classCode, documentNo);		

		Asset asset = AssetManager.getAsset(nodeService, assetNode, false, false, null);
		AssetRendition assetRendition = new AssetRendition(asset.getDocuments(), null);
		AssetReportImpl assetReport = convertToAssetReport(asset, assetRendition.getRenditions());
		JAXBContext pContext = JAXBContext.newInstance(new Class[] { AssetReportImpl.class });
		Marshaller marshaller = pContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.valueOf(true));
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.marshal(assetReport, res.getOutputStream());

		}
		catch (AccessDeniedException e )
		{
			logger.error(e.getMessage());
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		catch (BadCredentialsException e )
		{
			logger.error(e.getMessage());
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		 catch (IllegalArgumentException e) 
		 { 
			 logger.error(e.getMessage());
			 res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}catch (FileNotFoundException e) {
			
	    	logger.error(e.getMessage());
	        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		catch (Exception e )
		{
			logger.error(e.getMessage());
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}


	

	public AssetReportImpl convertToAssetReport(Asset asset, List<Rendition> renditions)
	{
		String ACTION_UPDATE = "UPDATE";
	
		AssetReportImpl assetReport = new AssetReportImpl();
		assetReport.setAction(ACTION_UPDATE);
		assetReport.setAiIndex(asset.getAiIndex());
		assetReport.setLatinTitle(asset.getLatinTitle());
		assetReport.setModified(asset.getModified());
		assetReport.setLastModified(asset.getLastModified());
		assetReport.setCreated(asset.getCreated());
		assetReport.setPublishDate(asset.getPublishDate());
		assetReport.setSecurityClass(asset.getSecurityClass());
		assetReport.setCreated(asset.getCreated());
		assetReport.setType(asset.getType());
		if(asset.getAiIndexStatus()!=null)
		{
		assetReport.setAiIndexStatus(asset.getAiIndexStatus());
		}
		if(asset.getRequestedBy()!=null)
		{
		assetReport.setRequestedBy(asset.getRequestedBy());
		}
		if(asset.getOriginator()!=null)
		{
		assetReport.setOriginator(asset.getOriginator());
		}
		if(asset.getNetwork()!=null)
		{
		assetReport.setNetwork(asset.getNetwork());
		}
		if(asset.getNetworkNumber()!=null)
		{
		assetReport.setNetworkNumber(asset.getNetworkNumber());
		}
		if(asset.getDescription()!=null)
		{
		assetReport.setIndexNotes(asset.getDescription());
		}
		List<CategoryReportImpl> categoriesReport = new ArrayList<CategoryReportImpl>();
		for(String category:asset.getPrimaryRegions())
		{
			CategoryReportImpl categoryReport = new CategoryReportImpl("region",category);
			categoriesReport.add(categoryReport);
		}
		for(String category:asset.getPrimaryIssues())
		{
			CategoryReportImpl categoryReport = new CategoryReportImpl("issue",category);
			categoriesReport.add(categoryReport);
		}
		for(String category:asset.getPrimaryKeywords())
		{
			CategoryReportImpl categoryReport = new CategoryReportImpl("keyword",category);
			categoriesReport.add(categoryReport);
		}
		for(String category:asset.getPrimaryCampaigns())
		{
			CategoryReportImpl categoryReport = new CategoryReportImpl("campaign",category);
			categoriesReport.add(categoryReport);
		}
		assetReport.setCategories(categoriesReport);
		
		List<CategoryReportImpl> secCategoriesReport = new ArrayList<CategoryReportImpl>();
		for(String category:asset.getSecondaryRegions())
		{
			CategoryReportImpl categoryReport = new CategoryReportImpl("region",category);
			secCategoriesReport.add(categoryReport);
		}
		for(String category:asset.getSecondaryIssues())
		{
			CategoryReportImpl categoryReport = new CategoryReportImpl("issue",category);
			secCategoriesReport.add(categoryReport);
		}
		for(String category:asset.getSecondaryKeywords())
		{
			CategoryReportImpl categoryReport = new CategoryReportImpl("keyword",category);
			secCategoriesReport.add(categoryReport);
		}
		for(String category:asset.getSecondaryCampaigns())
		{
			CategoryReportImpl categoryReport = new CategoryReportImpl("campaign",category);
			secCategoriesReport.add(categoryReport);
		}
		assetReport.setSecondaryCategories(secCategoriesReport);
		
		Iterator<Rendition> renditionIt = renditions.iterator();
		
		while(renditionIt.hasNext())
		{
			Rendition rendition = renditionIt.next();
			RenditionReportImpl renditionReport = new RenditionReportImpl();
			if(rendition.getLanguage()!=null)
			{
				renditionReport.setLanguage(rendition.getLanguage());
			}
			if(rendition.getTitle()!=null)
			{
				renditionReport.setTitle(rendition.getTitle());
			}
			if(rendition.getDescription()!=null)
			{
				renditionReport.setDescription(rendition.getDescription());
			}
				renditionReport.setLastModified(rendition.getLastModified());
			assetReport.getRenditions().add(renditionReport);
		}
		return assetReport;
	}


	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
    public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}
	
}
