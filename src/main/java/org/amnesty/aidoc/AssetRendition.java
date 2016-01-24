package org.amnesty.aidoc;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.apache.log4j.Logger;


public class AssetRendition {

	private static Logger logger = Logger.getLogger(AssetRendition.class);
	private ArrayList<Rendition> renditions = new ArrayList<Rendition>();
	private Rendition selectedRendition = null;
	private boolean titleMismatch = false;
	private boolean descriptionMismatch = false;
	private boolean contentMissing = false;
	private boolean missingLanguage = false;
	
	public AssetRendition(List<Document> documents, String selectedLanguage)
	{
		
		this.buildAssetRendition(null, documents, selectedLanguage);
	}
	
	public AssetRendition(ContentService contentService, List<Document> documents, String selectedLanguage)
	{
		this.buildAssetRendition(contentService, documents, selectedLanguage);
	}
	
	
	private void buildAssetRendition(ContentService contentService, List<Document> documents, String selectedLanguage)
	{
		
		Iterator<Document> docIt = documents.iterator();
		while(docIt.hasNext())
		{
			Document document = docIt.next();
			
			//skip is document is auxiliary
			if(!document.isPrimary()) continue;
			
			Rendition thisRendition = null;
			
			Iterator<Rendition> renditionsIterator = this.getRenditions().iterator();
			  
			while(renditionsIterator.hasNext())
			{
				Rendition tempRendition = renditionsIterator.next();
				if(tempRendition.getLanguage().equals(document.getLanguage()))
				{
					thisRendition = tempRendition;
				 	break;
				}
			}
			  
			  if(thisRendition==null)
			  {
				  thisRendition = new Rendition( document.getLanguage());
				  thisRendition.setLanguageString(Util.langToString(document.getLanguage()));
				  this.getRenditions().add(thisRendition);
				  
				  if ( document.getLanguage().equals(selectedLanguage) )
				    {
					  this.setSelectedRendition(thisRendition);
				    }
			  }
			  else
			  {
				  
				  logger.debug( "Rendition for '" + document.getLanguage() + "' was not null" );
			  }
			  		
			  		if ( document.getLanguage() == null || document.getLanguage().equals("unknown"))
			  		{
			  			setMissingLanguage(true);
			  		}
			  		
			  		thisRendition.getDocuments().add( document );				  
					  
					ContentData content = document.getContent();
					  
					  if ( document.getContent() != null )
						{
						  	if(contentService!=null)
						  	{
						  		if(content.getContentUrl()!=null)
						  		{
									ContentReader reader = contentService.getRawReader(content.getContentUrl());
									
									if ((reader == null) || (!(reader.exists())))
									{
										logger.debug("Unable to locate content for " +content.getContentUrl());
										setContentMissing(true);
									}
						  		}
						  	}
						}
						else{
							logger.error("Unable to locate content for " +document.getTitle());
							setContentMissing(true);
						}
						
					  if ( document.isPrimary() )
					  {
					   
					    if ( document.isEffective() )
					    {
					    	thisRendition.setEffectiveDocumentCount(thisRendition
									.getEffectiveDocumentCount() + 1);
					    }
					   
					    if ( thisRendition.getTitle() == null )
					    {
					    	thisRendition.setTitle(document.getTitle());
					    }
					    else
					    {
					      if (! thisRendition.getTitle().equals(document.getTitle()) )
					      {
					    	  setTitleMismatch(true);
					      }
					    }
					   
					    if ( thisRendition.getDescription() == null )
					    {
					    	thisRendition.setDescription(document.getDescription());
					    }
					    else
					    {
					      if (! thisRendition.getDescription().equals(document.getDescription()) )
					      {
					    	  setDescriptionMismatch(true);
					      }
					    }
	
					    if(thisRendition.getLastModified() == null)
					    {
					    	thisRendition.setLastModified(document.getLastModified());
					    }
					    else
					    {
					    	if(thisRendition.getLastModified().before(document.getLastModified()))
					    	{
					    		thisRendition.setLastModified(document.getLastModified());
					    	}
					    }
					    
					    if ( Util.betterRenditionMatch( "MASTER", document, thisRendition.getMasterDocument() ) )
					    {
					    	thisRendition.setMasterDocument(document);
					    	thisRendition.setTitle(document.getTitle());
					    	thisRendition.setDescription(document.getDescription());
					    	thisRendition.setMasterFilename(document.getFilename());
	
					    }
					   
					    if ( Util.betterRenditionMatch( "INLINE", document, thisRendition.getInlineDocument() ) )
					    {
					    	thisRendition.setInlineDocument(document);
					    	thisRendition.setInlineFilename(document.getFilename());
							if ( content != null )
							{							
								thisRendition.setContent(content);							
							}
					    }
					   
					    if ( Util.betterRenditionMatch( "ATTACHMENT", document, thisRendition.getAttachmentDocument() ) )
					    {
					    	thisRendition.setAttachmentDocument(document);
					    	thisRendition.setAttachmentFilename(document.getFilename());
					    }
				   
					  }
		}
	}
	
	public ArrayList<Rendition> getRenditions() {
		return renditions;
	}
	public void setRenditions(ArrayList<Rendition> renditions) {
		this.renditions = renditions;
	}
	
	public Rendition getRendition(String lang) {
		
		for (Rendition rendition : this.getRenditions())
		{
			if(rendition.getLanguage().equals(lang))
			{
				return rendition;
			}
		}
		return null;
	}
	
	public Rendition getSelectedRendition() {
		return selectedRendition;
	}
	public void setSelectedRendition(Rendition selectedRendition) {
		this.selectedRendition = selectedRendition;
	}
	
	public boolean isTitleMismatch() {
		return titleMismatch;
	}

	public void setTitleMismatch(boolean titleMismatch) {
		this.titleMismatch = titleMismatch;
	}

	public boolean isDescriptionMismatch() {
		return descriptionMismatch;
	}

	public void setDescriptionMismatch(boolean descriptionMismatch) {
		this.descriptionMismatch = descriptionMismatch;
	}

	public boolean isContentMissing() {
		return contentMissing;
	}

	public void setContentMissing(boolean contentMissing) {
		this.contentMissing = contentMissing;
	}

	public boolean isMissingLanguage() {
		return missingLanguage;
	}

	public void setMissingLanguage(boolean missingLanguage) {
		this.missingLanguage = missingLanguage;
	}
}