package org.amnesty.aidoc.webscript;

import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.CategoryService;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CategoriesWebScript extends BaseWebScript{

		private NodeService nodeService;
		private CategoryService categoryService;

		private static final Log logger = LogFactory.getLog(CategoriesWebScript.class);
	@Override
	protected Map<String, Object> executeAiImpl(WebScriptRequest req,
			Status status) {
		
		Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
		

		model.put("title", "");
		
		try
		{

		Collection<ChildAssociationRef> rootCategories = categoryService.getRootCategories(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, ContentModel.ASPECT_GEN_CLASSIFIABLE);
		String urlExtension = req.getExtensionPath();
		String title = URLDecoder.decode(urlExtension, "UTF-8");
		model.put("title", title);
		
		logger.debug("[Title] "+title);
		boolean found = false;
		

		model.put("query", urlExtension);

		 String[] termArray = title.split( "/" );

		 
		 NodeRef baseCategory = findCategory( rootCategories, termArray, 0 );

		 model.put("baseCategory", baseCategory);
		 
		if ( baseCategory != null )
		{

		  found = true;
		  
		  int hierarchy = getHierarchyDepth( baseCategory ) - 1 ;
		  model.put("hierarchy", hierarchy);
		  
		}
		
		model.put("found", found);
		
		logger.debug("[Found] "+found);
		
		}
		catch (Exception e) {
			handleError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage(), e,
                    model, status);
		}

		return model;
	}
	
	// Create the model for the categories Web Script

	// Function to get the depth of the hierarchy: assumes that categories cannot contain documents - all children are subcategories
	// Also assumes that all children have the same hierarchy depth.

	public int getHierarchyDepth(NodeRef category )
	{
	
	 List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(category);
	 if ( childAssocs.size() > 0 )
	 {
	    return 1 + getHierarchyDepth( childAssocs.get(0).getChildRef() );
	 }
	 else return 0;

	}
	
	//Function to find a category matching terms down the hierarchy tree

	private NodeRef findCategory(Collection<ChildAssociationRef> categories, String[] termArray, int index )
	{
    	
    	Iterator<ChildAssociationRef> categoriesAssocIt = categories.iterator();

    	while(categoriesAssocIt.hasNext())
    	{
    	
    		NodeRef category = categoriesAssocIt.next().getChildRef();

			String name = (String)nodeService.getProperty(category, ContentModel.PROP_NAME);
		    if ( name.equalsIgnoreCase( termArray[ index ] ) )
		    {
	
		      if ( ( index + 1 ) < termArray.length )
		      {
		    	  
		    	List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(category);
		    	
		        return findCategory( childAssocs, termArray, index + 1 );
	
		      }
		      else return category;
	
		    }

	  }

	  return null;
	 
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	

	public void setCategoryService(CategoryService categoryService) {
			this.categoryService = categoryService;
		}
}
