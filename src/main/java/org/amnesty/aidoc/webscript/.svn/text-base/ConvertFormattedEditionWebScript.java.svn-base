package org.amnesty.aidoc.webscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.acegisecurity.BadCredentialsException;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.amnesty.aidoc.Asset;
import org.amnesty.aidoc.AssetManager;
import org.amnesty.aidoc.Document;
import org.amnesty.aidoc.Edition;
import org.amnesty.aidoc.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConvertFormattedEditionWebScript extends BaseWebScript {

	private static final String ARG_GUEST = "guest";
	
	protected FileFolderService fileFolderService;

	protected NodeService nodeService;
	
	protected RuleService ruleService;
	
	protected ActionService actionService;
	
	protected static NodeRef templateRef = null;
	
	private static final Log logger = LogFactory.getLog(ConvertFormattedEditionWebScript.class);

	@Override
	protected Map<String, Object> executeAiImpl(WebScriptRequest req,
			Status status) {
		
		Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
		
		
		Map<String, String> args = req.getServiceMatch().getTemplateVars();
		  
			model.put("title", "Convert Formatted Edition");
			
		  try {
			String document_year = Util.readDocumentYear(args.get("document_year"), true );
			String class_code = Util.readClassCode(args.get("class_code"), true );
			String document_no = Util.readDocumentNo(args.get("document_no"), true );
			
			String isGuest = req.getParameter(ARG_GUEST);
			  
			  boolean guest = false;
			  
		         if (isGuest != null)
		         {
		        	 guest = Boolean.parseBoolean(isGuest);
		         }
		         
			  if (( guest == true ))
			  {
				  handleError(HttpServletResponse.SC_UNAUTHORIZED,
							"Guest access is disallowed", null,
		                    model, status);
			  }
			  
			  NodeRef rootStoreRef = null;
			  NodeRef rootHomeRef = null;
		      NodeRef aiIndexNode = null;
		      NodeRef templateNode = null;
		      
		      rootStoreRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			  QName qname = QName.createQName(NamespaceService.APP_MODEL_1_0_URI, "company_home");
		      List<ChildAssociationRef> assocRefs = nodeService.getChildAssocs(rootStoreRef, ContentModel.ASSOC_CHILDREN, qname);
		      
		      if(assocRefs.size()>0)
		      {
		    	  rootHomeRef = assocRefs.get(0).getChildRef();
		      }
		      
		      List<String> aiIndexPath = new ArrayList<String>();

		      aiIndexPath.add("Asset Library");
		      aiIndexPath.add("Indexed Documents");
		      aiIndexPath.add(document_year);
		      aiIndexPath.add(class_code);
		      aiIndexPath.add(document_no);
		      
		      List<String> templatePath = new ArrayList<String>();
		      templatePath.add("Data Dictionary");
		      templatePath.add("Space Templates");
		      templatePath.add("Formatted Edition Template");
		      if(rootHomeRef!=null)
		      {
		    	  FileInfo aiIndexInfo = fileFolderService.resolveNamePath(rootHomeRef, aiIndexPath);
		    	  aiIndexNode = aiIndexInfo.getNodeRef();
		    	  
		    	  FileInfo templateInfo = fileFolderService.resolveNamePath(rootHomeRef, templatePath);
		    	  templateNode = templateInfo.getNodeRef();
		      }else
		      {
		    	 throw new FileNotFoundException("rootHome not found");
		      }
			  
	
		      Rule pdfToHtmlRule = null;
		      List<Rule> rules = ruleService.getRules(templateNode);
              for (Rule rule : rules) {
                  if (rule.getTitle().equals("Transform assets to html")) {
                	  pdfToHtmlRule = rule;
                	  break;
                  }
              }
              if(pdfToHtmlRule==null)
              {
            	  throw new FileNotFoundException("Rule Not Found");
              }

              NodeRef htmlFolder = nodeService.getChildByName(templateNode, ContentModel.ASSOC_CONTAINS, "HTML");
              
              if(htmlFolder==null)
              {
            	  throw new FileNotFoundException("HTML Folder Not Found");
              }

		      Asset asset = AssetManager.getAsset(nodeService, aiIndexNode, true, true, "en");
		      ArrayList<Edition>Editions = asset.getEditions();
		      Iterator<Edition> editionIterator = Editions.iterator();
		      while(editionIterator.hasNext())
		      {
		    	  Edition edition = editionIterator.next();
		    	  
		    	  if(edition.getName().equals("Formatted Edition"))
		    		{
		    		  logger.debug("Formatted Edition found");
		    		  NodeRef editionNodeRef = edition.getNode();
		    		  //fileFolderService.copy(htmlFolder, editionNodeRef, "HTML");
		    		  fileFolderService.create(editionNodeRef, "HTML", ContentModel.TYPE_FOLDER);
		              ruleService.saveRule(editionNodeRef, pdfToHtmlRule);
		              
		    		  ArrayList<Document> documents = AssetManager.getEditionDocuments(nodeService, edition);
		    		  logger.debug(documents.size()+" documents found");
		    		  Iterator<Document> documentIt = documents.iterator();
		    		  while(documentIt.hasNext())
		    		  {
		    			Document document = documentIt.next();

		    			actionService.executeAction(pdfToHtmlRule.getAction(),
		    					document.getNode(), true, false);
		    			logger.debug("Rule executed");
		    		  }
		    		  //Create HTML folder
		    		  //fileFolderService.copy(htmlFolder, editionNodeRef, "HTML");
		    		  //fileFolderService.create(editionNodeRef, "HTML", ContentModel.TYPE_FOLDER);
		    		  //ruleService.saveRule(editionNodeRef, pdfToHtmlRule);
		    		  break;    
		    		} 
		    	}
			  
			  
		}
		catch (AccessDeniedException e )
		{
			handleError(HttpServletResponse.SC_FORBIDDEN,
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
                    e.getMessage(), null,
                    model, status);
		}
		  
		return model;
	}
	
    public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}


	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}
	
	public void setActionService(ActionService actionService) {
		this.actionService = actionService;
	}
}
