package org.amnesty.aidoc;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRegistry;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.log4j.Logger;


@XmlRegistry
public class AssetManager {
	
	private static Logger logger = Logger.getLogger(AssetManager.class);
	private static SimpleDateFormat sourceDateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
	
	private AssetManager() {
		super();
	}
	
/**
 * 
 * @param nodeService - The service
 * @param nodeRef - The reference
 * @param publicScope - Public documents only?
 * @param selectByLanguage - Only documents in the selected language?
 * @param selectedLanguage - Selected language
 * @return Asset object
 * @throws AccessDeniedException
 * @throws InvalidNodeRefException
 */
	public static Asset getAsset(NodeService nodeService, NodeRef nodeRef, boolean publicScope, boolean selectByLanguage, String selectedLanguage) throws AccessDeniedException, InvalidNodeRefException
	{
		return getAsset(nodeService, nodeRef, publicScope, selectByLanguage, selectedLanguage, false);
	}

	/**
	 * 
	 * @param nodeService - The service
	 * @param nodeRef - The reference
	 * @param publicScope - Public documents only?
	 * @param selectByLanguage - Only documents in the selected language?
	 * @param selectedLanguage - Selected language
	 * @param ignoreFormattedHtml - Ignore formatted edition subfolders
	 * @return
	 * @throws AccessDeniedException
	 * @throws InvalidNodeRefException
	 */
	public static Asset getAsset(NodeService nodeService, NodeRef nodeRef, boolean publicScope, boolean selectByLanguage, String selectedLanguage, boolean ignoreFormattedHtml) throws AccessDeniedException, InvalidNodeRefException
	{
		Asset asset = getLightAsset(nodeService, nodeRef, publicScope);
			
			Util.filterCategories(asset);
				
			List<ChildAssociationRef> childNodes = nodeService.getChildAssocs(nodeRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
			for (ChildAssociationRef EditonNode : childNodes) {
                NodeRef EditonNodeRef = EditonNode.getChildRef(); 
                
                if(nodeService.getType(EditonNodeRef).equals(Constants.TYPE_EDITION)) {
                	
                	Edition edition = getEdition(nodeService, EditonNodeRef);		
					asset.addEdition(edition);			
					
					if(edition.getLastModified()!=null && edition.getLastModified()!=null && asset.getLastModified().before(edition.getLastModified()))
					{
						asset.setLastModified(edition.getLastModified());
					}
					
					List<ChildAssociationRef> documentNodes = nodeService.getChildAssocs(EditonNodeRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
					// 2 - iterate thru document list, checking for actual documents
					for(ChildAssociationRef documentNode : documentNodes) {
			            NodeRef documentNodeRef = documentNode.getChildRef();
			            
			            logger.debug("[Name]: "+nodeService.getProperty(documentNodeRef, ContentModel.PROP_NAME));
			            logger.debug("[Type]: "+nodeService.getType(documentNodeRef).getLocalName());
			            
			            try
			            {
				            Date documentDate = sourceDateFormatter.parse(nodeService.getProperty(documentNodeRef, ContentModel.PROP_MODIFIED).toString());
				            if(asset.getLastModified().before(documentDate))
				            	asset.setLastModified(documentDate);
				     
			            } catch (Exception e) {
			            	logger.error("Date Parse Exception: " + e);
						}			
						
			            
						// 3 - GET DOCUMENT (The first document in the Edition folder)			                
						if (Constants.AI_INDEX_TYPES.contains(nodeService.getType(documentNodeRef).getLocalName())) 
						{				
							
								// Get Document add it to Edition and add it to this
							Document document = getDocument(nodeService, documentNodeRef);	
							document.setEdition(edition.getName());
					    	document.setHasEdition(true);
					    	edition.addDocument(document);
							addAssetDocument(asset, document, publicScope, selectByLanguage, selectedLanguage );
						
						}
						else if ( Constants.AUXILLIARY_TYPES.contains(nodeService.getType(documentNodeRef).getLocalName()) ) 
	                    {
	                		logger.debug("Auxiliar document found: "+nodeService.getType(documentNodeRef).getLocalName());
	                		Document document = getDocument(nodeService, documentNodeRef);
	                		document.setEdition(edition.getName());
	                		document.setHasEdition(true);
	                		edition.addDocument(document);
	                		addAssetDocument(asset, document, publicScope, selectByLanguage, selectedLanguage );
	                    }
						
			            // 4 - GET FOLDER's DOCUMENT (Now look in the HTML and PDF subfolders) Ignore if formatted edition and hide pdf
			            if(nodeService.getType(documentNodeRef).equals(ContentModel.TYPE_FOLDER) && !(edition.getName().equalsIgnoreCase("Formatted Edition") && ignoreFormattedHtml)) { 
			            	
							List<ChildAssociationRef> subDocumentNodes = nodeService.getChildAssocs(documentNodeRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
							logger.debug("Found "+subDocumentNodes.size()+ " subdocument(s)");
							// Traverse thru folders
							for(ChildAssociationRef subDocumentNode : subDocumentNodes) {				                
								NodeRef subDocumentNodeRef = subDocumentNode.getChildRef();				
								try
					            {
						            Date subDocumentDate = sourceDateFormatter.parse(nodeService.getProperty(subDocumentNodeRef, ContentModel.PROP_MODIFIED).toString());
						            if(asset.getLastModified().before(subDocumentDate))
						            	asset.setLastModified(subDocumentDate);
					            } catch (Exception e) {
					            	logger.error("Date Parse Exception: " + e);
								}
								
			                	if (Constants.AI_INDEX_TYPES.contains(nodeService.getType(subDocumentNodeRef).getLocalName()) || nodeService.getType(subDocumentNodeRef).equals(ContentModel.TYPE_CONTENT)) {
				                	Map<QName, Serializable> documentMap = nodeService.getProperties(subDocumentNodeRef);

									// SAVE DOCUMENT
									if(documentMap != null && !documentMap.isEmpty()) {	
										
										Document document = getDocument(nodeService, subDocumentNodeRef);
										document.setEdition(edition.getName());
								    	document.setHasEdition(true);
								    	edition.addDocument(document);
										addAssetDocument(asset, document, publicScope, selectByLanguage, selectedLanguage);
									}
								}
							}
			            }           			                
					}
				}
                
                else{
                	 if ( Constants.AUXILLIARY_TYPES.contains(nodeService.getType(EditonNodeRef).getLocalName()) && (! selectByLanguage ) ) 
                     {
                		 logger.debug("Auxiliar document found: "+nodeService.getType(EditonNodeRef).getLocalName());
                		 Document document = getDocument(nodeService, EditonNodeRef);
                		 document.setEdition("Asset Level");
                		 document.setHasEdition(false);
                		 asset.addAuxiliarDocument(document);
                		 addAssetDocument(asset, document, publicScope, selectByLanguage, selectedLanguage );
                     }
					
				}
                
            }
		
		return asset;
	}
	
	/**
	 * 
	 * @param nodeService - The service
	 * @param nodeRef - The reference
	 * @param publicScope - Public documents only?
	 * @return
	 * @throws AccessDeniedException
	 * @throws InvalidNodeRefException
	 */
	@SuppressWarnings({ "unchecked" })
	public static Asset getLightAsset(NodeService nodeService, NodeRef nodeRef, boolean publicScope) throws AccessDeniedException, InvalidNodeRefException
	{
		logger.debug("Creating new asset");
		Asset asset = new Asset();
		
		String uuid = (String)nodeService.getProperty(nodeRef, ContentModel.PROP_NODE_UUID);
		asset.setUuid(uuid);

		
		if(nodeService.getType(nodeRef).equals(Constants.PROP_ASSET))
		{
			
			
			Map<QName, Serializable> assetMap = nodeService.getProperties(nodeRef);		
			

			AiIndex	aiIndex = Util.getAssetAiIndex(nodeService, nodeRef);

			
			asset.setDocumentYear(aiIndex.getYear());
			asset.setClassCode(aiIndex.getAiClass().substring(0, 3));
			asset.setSubclass(aiIndex.getAiClass().substring(3, 5));
			asset.setDocumentNo(aiIndex.getDocnum());
			asset.setAiIndex(aiIndex.toString());
			asset.setSecurityClass(assetMap.get(Constants.PROP_SECURITY_CLASS).toString());
			asset.setPublic(asset.getSecurityClass().equals("Public"));
			
			if (( publicScope == true ) && ( ! asset.isPublic() ))
			  {
				throw new AccessDeniedException("Not public");
			  }
			
			
			try {
									
				if(assetMap.get(Constants.PROP_PUBLISH_DATE) != null) {
					asset.setPublishDate(sourceDateFormatter.parse((assetMap.get(Constants.PROP_PUBLISH_DATE)).toString()));
				}
				asset.setCreated(sourceDateFormatter.parse((assetMap.get(ContentModel.PROP_CREATED).toString()).toString()));
				asset.setLastModified(sourceDateFormatter.parse((assetMap.get(ContentModel.PROP_MODIFIED).toString()).toString()));
				asset.setModified(sourceDateFormatter.parse((assetMap.get(ContentModel.PROP_MODIFIED).toString()).toString()));
				
			} catch (Exception e) {
				logger.error("Date Parse Exception: " + e);
			}
			
			  boolean isPublished = (( asset.getPublishDate() != null ) && ( new Date().after(asset.getPublishDate()) ));
			  
			  asset.setPublished(isPublished);
			  
			  if (( publicScope == true ) && ( ! asset.isPublished() ))
			  {
				  throw new AccessDeniedException("Not published");
			  }
			
			asset.setPublicationStatus(asset.getPublishDate() == null ? "Unpublished" : (( new Date().after(asset.getPublishDate())? "Published" : "Embargoed")));
			asset.setCreator(assetMap.get(ContentModel.PROP_CREATOR).toString());
			//Never used, now the originator is the name of the person asked for the AiIndex
			//asset.setOriginator((asset.getCreator().equals("service_pressoffice"))? "Drupal" : "Alfresco");
			if (assetMap.get(ContentModel.PROP_ORIGINATOR)!= null)
			{
				asset.setOriginator(assetMap.get(ContentModel.PROP_ORIGINATOR).toString());
			}
			else
			{
				asset.setOriginator("");
			}
			
			if (assetMap.get(Constants.PROP_AI_TITLE)!=null)
			{
				asset.setLatinTitle(assetMap.get(Constants.PROP_AI_TITLE).toString());
			}
			else
			{
				asset.setLatinTitle(assetMap.get(ContentModel.PROP_TITLE).toString());
			}
			
			if (assetMap.get(Constants.PROP_AI_DESCRIPTION)!=null)
			{
				asset.setDescription(assetMap.get(Constants.PROP_AI_DESCRIPTION).toString());
			}
			else
			{
				asset.setDescription(assetMap.get(ContentModel.PROP_DESCRIPTION).toString());
			}
			
			asset.setType(null);
			
			String nativeType = null;
			if(assetMap.get(Constants.PROP_AI_INDEX_TYPE)!=null)
			{
				nativeType = assetMap.get(Constants.PROP_AI_INDEX_TYPE).toString();
			}
			if(nativeType != null && !nativeType.equals("false"))
			{
				asset.setType(nativeType);
			}
			if(asset.getType() == null)
			{
				asset.getProblems().add("Missing native type");
			}
			asset.setNotes(new String());
			asset.setTypeMismatch(false);
			
			if(assetMap.get(Constants.PROP_AI_INDEX_STATUS) != null)
			{
				asset.setAiIndexStatus(assetMap.get(Constants.PROP_AI_INDEX_STATUS).toString());
			}
			
			if(assetMap.get(Constants.PROP_REQUESTED_BY) != null)
			{
				asset.setRequestedBy(assetMap.get(Constants.PROP_REQUESTED_BY).toString());
			}
			
			if(assetMap.get(Constants.PROP_NETWORK) != null)
			{
				asset.setNetwork(assetMap.get(Constants.PROP_NETWORK).toString());
			}
			else
			{
				asset.setNetwork("");
			}
			
			if(assetMap.get(Constants.PROP_NETWORK_NUMBER) != null)
			{
				asset.setNetworkNumber(assetMap.get(Constants.PROP_NETWORK_NUMBER).toString());
			}
			else
			{
				asset.setNetworkNumber("");
			}
			
			if(assetMap.get(ContentModel.PROP_CATEGORIES) != null)
			{
				List<NodeRef> categories = (List<NodeRef>)assetMap.get(ContentModel.PROP_CATEGORIES);
				if(!categories.isEmpty())		
				{
					asset.setCategories(Util.getPrimaryPaths(nodeService, categories));
				}
			}
		
			if(assetMap.get(QName.createQName(Constants.AICORE_MODEL, "secCategories")) != null)
			{
				List<NodeRef> secCategories = (List<NodeRef>)assetMap.get(QName.createQName(Constants.AICORE_MODEL, "secCategories"));
				if(!secCategories.isEmpty())		
				{
					asset.setSecondaryCategories(Util.getPrimaryPaths(nodeService, secCategories));				
				}
			}

		}
		else
		{
			throw new IllegalArgumentException( nodeService.getProperty(nodeRef, ContentModel.PROP_NAME) + " is not an AI Index asset");
		}
		
		logger.debug("Created asset "+asset.getAiIndex());
		return asset;
	}
	
	/**
	 * 
	 * @param nodeService
	 * @param nodeRef
	 * @return Edition object
	 */
	public static Edition getEdition(NodeService nodeService, NodeRef nodeRef)
	{
		Edition edition = new Edition();
		edition.setNode(nodeRef);
		
		Map<QName, Serializable> editionMap = nodeService.getProperties(nodeRef);
    	
    	edition.setName(editionMap.get(ContentModel.PROP_NAME).toString());			
		if(editionMap.get(ContentModel.PROP_TITLE)!=null)
		{
			edition.setTitle(editionMap.get(ContentModel.PROP_TITLE).toString());
		}
		if(editionMap.get(ContentModel.PROP_DESCRIPTION)!=null)
		{
			edition.setDescription(editionMap.get(ContentModel.PROP_DESCRIPTION).toString());
		}
		
		if(editionMap.get(ContentModel.PROP_MODIFIED)!=null)
		{
			Date modified = null;
			try {
				modified = sourceDateFormatter.parse(editionMap.get(ContentModel.PROP_MODIFIED).toString());
			} catch (Exception e) {
				logger.error("Invalid date " + e);
			}
			edition.setLastModified(modified);
		}
		return edition;
		
	}
	
	/**
	 * 
	 * @param nodeService
	 * @param nodeRef
	 * @return Document object
	 */
	public static Document getDocument(NodeService nodeService, NodeRef nodeRef)
	{
		Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);		
		Document document = new Document();
		document.setNode(nodeRef);
		String uuid = (String)nodeService.getProperty(nodeRef, ContentModel.PROP_NODE_UUID);
		document.setUuid(uuid);
		document.setDefaultLanguage("en");
		
		String documentType = nodeService.getType(nodeRef).getLocalName().toString();
		document.setType(documentType);
		
		String name = properties.get(ContentModel.PROP_NAME).toString();
		document.setFilename(name);
		logger.debug("[Document name] "+name);
		if(!name.contains("_"))
		{
			document.setPrimary(true);
		}
		else{
			document.setPrimary(false);
		}
		
		if (properties.get(ContentModel.PROP_TITLE) !=null)
		{
			 String title = properties.get(ContentModel.PROP_TITLE).toString();
			 if (Util.iSValidXMLText(title))
	    	  {
	    		  document.setTitle(title);
	    	  }
	    	  else
	    	  {
	    		  document.setTitle(Util.stripNonValidXMLCharacters(title));
	    	  }
			logger.debug("[Document title] "+name);
		}
		if (properties.get(ContentModel.PROP_DESCRIPTION) !=null)
		{
			document.setDescription(properties.get(ContentModel.PROP_DESCRIPTION).toString()); 
			String description = properties.get(ContentModel.PROP_DESCRIPTION).toString();
			if (Util.iSValidXMLText(description))
	    	  {
	    		  document.setDescription(description);
	    	  }
	    	  else
	    	  {
	    		  document.setDescription(Util.stripNonValidXMLCharacters(description));
	    	  }
			logger.debug("[Document description] "+name);
		}
		
		if(properties.get(ContentModel.PROP_MODIFIED)!=null && !properties.get(ContentModel.PROP_MODIFIED).equals(""))
		{
			Date modified = null;
			try {
				modified = sourceDateFormatter.parse(properties.get(ContentModel.PROP_MODIFIED).toString());
			} catch (Exception e) {
				logger.error("Invalid date " + e);
			}
			document.setLastModified(modified);
		}
		if (properties.get(ContentModel.PROP_LOCALE) !=null)
		{
			document.setLanguage(properties.get(ContentModel.PROP_LOCALE).toString());
		}
		else{
			document.setLanguage(document.getDefaultLanguage());
	      }
		
		try {
			if(properties.get(Constants.PROP_FROM) !=null)			
				document.setEffectiveFrom(sourceDateFormatter.parse(properties.get(Constants.PROP_FROM).toString()));
	      
			if(properties.get(Constants.PROP_TO) !=null)			
				document.setEffectiveTo(sourceDateFormatter.parse(properties.get(Constants.PROP_TO).toString()));
	      	}
			catch (Exception e) {
				logger.error("Invalid date " + e);
			}
		
		NodeRef source = nodeRef;
		  while ( source != null && nodeService.hasAspect(source, ContentModel.ASPECT_COPIEDFROM))
		  {
		    source = (NodeRef)nodeService.getProperty(source, ContentModel.PROP_COPY_REFERENCE);
		    if (source != null) 
		    {
		    	
		      //to do what is this
		    	document.setRelationFormatOf(nodeService.getProperty(source,ContentModel.PROP_NODE_UUID).toString());
		      
		      if(nodeService.getProperty(source,ContentModel.PROP_LOCALE)!=null)
		      {
		    	  document.setLanguage(nodeService.getProperty(source,ContentModel.PROP_LOCALE).toString());
		      }
		      else{
		    	  document.setLanguage(document.getDefaultLanguage());
		      }
		      if (nodeService.getProperty(source,ContentModel.PROP_TITLE) !=null)
		      {
		    	  String title = nodeService.getProperty(source,ContentModel.PROP_TITLE).toString();
		    	  if (Util.iSValidXMLText(title))
		    	  {
		    		  document.setTitle(title);
		    	  }
		    	  else
		    	  {
		    		  document.setTitle(Util.stripNonValidXMLCharacters(title));
		    	  }
		      }      
		      
		      if (nodeService.getProperty(source,ContentModel.PROP_DESCRIPTION) !=null)
		      {
		    	  String description = nodeService.getProperty(source,ContentModel.PROP_DESCRIPTION).toString();
		    	  if (Util.iSValidXMLText(description))
		    	  {
		    		  document.setDescription(description);
		    	  }
		    	  else
		    	  {
		    		  document.setDescription(Util.stripNonValidXMLCharacters(description));
		    	  }
		      }		      
		      
		      try {
		      if(nodeService.getProperty(source, Constants.PROP_FROM) !=null)			
		    	  document.setEffectiveFrom(sourceDateFormatter.parse(nodeService.getProperty(source, Constants.PROP_FROM).toString()));
		      
		      if(nodeService.getProperty(source, Constants.PROP_TO) !=null)			
		    	  document.setEffectiveTo(sourceDateFormatter.parse(nodeService.getProperty(source, Constants.PROP_TO).toString()));
		      }
		      catch (Exception e) {
					logger.error("Invalid date " + e);
				}
		    }
		  }
		  
		    
		try {
			if(properties.get(ContentModel.PROP_MODIFIED) !=null)
				document.setLastModified(sourceDateFormatter.parse(properties.get(ContentModel.PROP_MODIFIED).toString()));
			if(properties.get(ContentModel.PROP_CREATED) !=null)			
				document.setCreated(sourceDateFormatter.parse(properties.get(ContentModel.PROP_CREATED).toString()));
		} catch (Exception e) {
			logger.error("Invalid date " + e);
		}
		
		Date now = new Date();	
		
		boolean effective =	((document.getEffectiveFrom() == null ||  now.after(document.getEffectiveFrom())) && 
				(document.getEffectiveTo() == null || now.before(document.getEffectiveTo()))) ;
		
		document.setEffective(effective);
		document.setGenerated(( nodeService.getProperty(nodeRef, ContentModel.PROP_COPY_REFERENCE) != null ));

		document.setContent((ContentData)properties.get(ContentModel.PROP_CONTENT));
		
		if (document.getContent() != null) {
			document.setFilesize(document.getContent().getSize());//node.content.length();
			document.setMimetype(document.getContent().getMimetype());
			document.setFormat(Util.mimetypeToFormat(document.getContent().getMimetype()));
			if ( document.getFormat().equals("HTML") || document.getFormat().equals("XHTML") )
		    {
				document.setInline(true);
		    }
			else
			{
				document.setInline(false);
			}
		}
		

		return document;
		
	}

	/**
	 * 
	 * @param nodeService
	 * @param edition
	 * @return List of documents in the edition
	 */
	public static ArrayList<Document> getEditionDocuments(NodeService nodeService, Edition edition)
	{
		ArrayList<Document> documents = new ArrayList<Document>();
		List<ChildAssociationRef> documentNodes = nodeService.getChildAssocs(edition.getNode(), ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		// 2 - iterate thru document list, checking for actual documents
		for(ChildAssociationRef documentNode : documentNodes) {
            NodeRef documentNodeRef = documentNode.getChildRef();		
			
			// 3 - GET DOCUMENT (The first document in the Edition folder)			                
			if (Constants.ASSET_TYPES.contains(nodeService.getType(documentNodeRef).getLocalName())) {				
				
					// Get Document add it to Edition and add it to this
				Document document = getDocument(nodeService, documentNodeRef);	
				document.setEdition(edition.getName());
		    	document.setHasEdition(true);
		    	documents.add(document);
				}
			}
		return documents;
	}
	
	/**
	 * 
	 * @param asset - The asset
	 * @param document - The document
	 * @param selectByLanguage - Add only the document language is the selected language?
	 * @param selectedLanguage - Selected language
	 */
	private static void addAssetDocument(Asset asset, Document document, boolean publicScope, boolean selectByLanguage, String selectedLanguage )
	{
	  // in public scope never add documents which are not effective
		
	  if ( publicScope  &&  (! document.isEffective() ))
	  {
		  logger.debug("Non-effective document in public scope is ignored: "+document.getFilename());
	    //doLog( "Non-effective document in public scope is ignored" );
	    return;
	  }

	  // do type check before selection by language to catch type mismatches

	  if (document.isPrimary() )
	  {
	    if ( asset.getType() == null || asset.getType().equalsIgnoreCase("Unknown")) 
	    {
	    	asset.setType(document.getType());
	    }
	    else
	    {
	      if ( asset.getType() != document.getType() )
	      {
	    	  asset.setNotes(asset.getNotes() + "Type mismatch for " + document.getTitle());
	    	  asset.setTypeMismatch(true);
	      }
	     }
	  }

	  asset.getLanguages().put(document.getLanguage(), Util.langToString( document.getLanguage()));

	  
	  if ( selectByLanguage && ( !document.getLanguage().equals(selectedLanguage )))
	  {
		  logger.debug(document.getLanguage()+" does not match "+selectedLanguage);
	    return;
	  }

	  asset.addDocument( document );  	
	  	  logger.debug("Document added to asset "+document.getFilename());
	}
	
    /**
     * Create an instance of {@link Document }
     * 
     */
    public Document createDocument() {
        return new Document();
    }

    /**
     * Create an instance of {@link Asset }
     * 
     */
    public Asset createAsset() {
        return new Asset();
    }


    /**
     * Create an instance of {@link Rendition }
     * 
     */
    public Rendition createRendition() {
        return new Rendition();
    }

}
