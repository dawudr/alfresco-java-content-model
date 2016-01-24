package org.amnesty.aidoc.webscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import net.sf.acegisecurity.BadCredentialsException;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.amnesty.aidoc.Util;

public class ListIndexesWebScript extends BaseWebScript {

	private NodeService nodeService;

	private FileFolderService fileFolderService;

	//access public published documents only?
	private boolean publicScope = false;
	
	private static int BASE_LEVEL = 0;
	private static int DOCUMENT_YEAR_LEVEL = 1;
	private static int CLASS_CODE_LEVEL = 2;
	
	private static String CLASS_CODE_STRICT_REGX = "[A-Z]{3}[0-9]{2}";
	private static String DOCUMENT_YEAR_STRICT_REGX = "[1-2]{1}[0-9]{3}";
	@Override
	protected Map<String, Object> executeAiImpl(WebScriptRequest req,
			Status status) {


		Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
		List<String> aiIndexList = new ArrayList<String>();

	    String yearRegex = DOCUMENT_YEAR_STRICT_REGX;
	    String classCodeRegex = CLASS_CODE_STRICT_REGX;
	    Pattern yearPattern = Pattern.compile(yearRegex);
	    Pattern classCodePattern = Pattern.compile(classCodeRegex);
	    Matcher m = null;
	    
		try{
			
			Map<String, String> args = req.getServiceMatch().getTemplateVars();
			  
			String documentYear = Util.readDocumentYear(args.get("document_year"), false );
			String classCode = Util.readClassCode(args.get("class_code"), false );
			  
		  boolean guest = req.isGuest();
	         
		  if (( guest == true ) && ( publicScope == false ))
		  {
				handleError(HttpServletResponse.SC_UNAUTHORIZED,
						"Guest access is disallowed", null,
	                    model, status);
		  }
		  
		  
		  int crawlLevel = ( ( documentYear == null ) ? BASE_LEVEL : ( ( classCode == null ) ? DOCUMENT_YEAR_LEVEL :CLASS_CODE_LEVEL) );  
		  
		  NodeRef baseNode = Util.resolveAssetNode(nodeService, fileFolderService, documentYear, classCode, null);
		  
		  if ( crawlLevel == BASE_LEVEL )
		  {
			  handleError(HttpServletResponse.SC_BAD_REQUEST,
					  "Base level is not supported by performance", null,
					  model, status);
			  return model;
		  }
		  
		  if ( baseNode == null )
		  {
			  handleError(HttpServletResponse.SC_BAD_REQUEST,
					  "Null base node", null,
					  model, status);
			  return model;
		  }
		  

      		m = yearPattern.matcher(documentYear);
    		if ( !m.matches())
	    	{
    			 handleError(HttpServletResponse.SC_BAD_REQUEST,
   					  "Wrong year format", null,
   					  model, status);
   			  return model;
	    	}
    		
    		if ( classCode!=null)
    		{
    		m = classCodePattern.matcher(classCode);
	    		if (!m.matches())
		    	{
	    			 handleError(HttpServletResponse.SC_BAD_REQUEST,
	   					  "Wrong class code format", null,
	   					  model, status);
	   			  return model;
		    	}
    		}
		    Iterator<ChildAssociationRef> childAssocIt = nodeService.getChildAssocs(baseNode).iterator();
		    
		    
		    while(childAssocIt.hasNext())
		    {
		    	NodeRef child = childAssocIt.next().getChildRef();
		    	
		    	String childName = nodeService.getProperty(child, ContentModel.PROP_NAME).toString();
		    	logger.debug("[Child name] "+childName);

		      //ignore children with no children unless they are AI Index folders and showEmptyIndexes is set
		      
	    	if ( crawlLevel == CLASS_CODE_LEVEL )
		    {
	    			aiIndexList.add(documentYear + "/" + classCode + "/" + childName);
		    }
	    	else{
		        
		        //investigate systemfolder child which otherwise empty index folders seem to have..

		        
		        Iterator<ChildAssociationRef> grandChildAssocIt = nodeService.getChildAssocs(child).iterator();
		        
		        while(grandChildAssocIt.hasNext())
		        {
		        	NodeRef grandChild = grandChildAssocIt.next().getChildRef();
		        	
		        	String grandChildName = nodeService.getProperty(grandChild, ContentModel.PROP_NAME).toString();
		        	logger.debug("[Grandchild name] "+childName);

		        	if( crawlLevel == DOCUMENT_YEAR_LEVEL )
		        	{
		        		m = classCodePattern.matcher(childName);
		        		if(m.matches())
		        		{
			    			aiIndexList.add(documentYear + "/" + childName + "/" + grandChildName);
		        		}

		        	}
		        	
		        	//base level not supported by performance
		        	/*else{
		        		
		        		Iterator<ChildAssociationRef> grandGrandChildAssocIt = nodeService.getChildAssocs(grandChild).iterator();
		        		while(grandGrandChildAssocIt.hasNext())
				        {
				        	NodeRef grandGrandChild = grandGrandChildAssocIt.next().getChildRef();
				        	
				        	String grandGrandChildName = nodeService.getProperty(grandGrandChild, ContentModel.PROP_NAME).toString();
				        	
				        	aiIndexList.add(childName + "/" + grandChildName + "/" + grandGrandChildName);
				        	
				        }
		        }*/

		        }
		      }
	    	}
		    
		    model.put("aiIndexList", aiIndexList);
		}
		catch (AccessDeniedException e )
		{
			handleError(HttpServletResponse.SC_UNAUTHORIZED,
                    e.getMessage(), null,
                    model, status);
		}
		catch (BadCredentialsException e )
		{
			
			handleError(HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage(), null,
                    model, status);
		}
		 catch (IllegalArgumentException e) {
			 handleError(HttpServletResponse.SC_BAD_REQUEST,
	                    e.getMessage(), null,
	                    model, status);
		}catch (FileNotFoundException e) {
			handleError(HttpServletResponse.SC_NOT_FOUND,
                    e.getMessage(), null,
                    model, status);
		}
		catch (Exception e )
		{
			
			handleError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage(), e,
                    model, status);
		}
		return model;
	}


	public NodeService getNodeService() {
		return nodeService;
	}
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	public FileFolderService getFileFolderService() {
		return fileFolderService;
	}
	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}
	
	public boolean isPublicScope() {
		return publicScope;
	}


	public void setPublicScope(boolean publicScope) {
		this.publicScope = publicScope;
	}
}
