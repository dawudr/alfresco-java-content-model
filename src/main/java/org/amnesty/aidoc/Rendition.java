package org.amnesty.aidoc;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.service.cmr.repository.ContentData;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.amnesty.aidoc.Document;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "", propOrder = {
    "masterDocument",
    "inlineDocument",
    "attachmentDocument"
})
@XmlRootElement(name = "rendition")
public class Rendition {
	
    @XmlAttribute(required = true)
    protected String language;
	protected List<Document> documents = new ArrayList<Document>();

	private String languageString;
	private int effectiveDocumentCount = 0;
	private String title = null;
	private String description = null;
	@XmlElementRef
	private Document masterDocument;
	@XmlElementRef
	private Document inlineDocument;
	@XmlElementRef
	private Document attachmentDocument;

	private String masterFilename;
	private String inlineFilename;
	private String attachmentFilename;
	
	private ContentData content; 
	
	private Date lastModified;

	protected Rendition() {
	}  
	
	protected Rendition(String language) {
		this.language = language;
	}  
	
	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public ContentData getContent() {
		return content;
	}
	public String getLanguage() {
		return language;
	}

	public String getLanguageString() {
		return languageString;
	}
	
	public void setLanguageString(String languageString) {
		this.languageString = languageString;
	}
	
	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public int getEffectiveDocumentCount() {
		return effectiveDocumentCount;
	}

	public Document getMasterDocument() {
		return masterDocument;
	}

	public Document getInlineDocument() {
		return inlineDocument;
	}

	public Document getAttachmentDocument() {
		return attachmentDocument;
	}

	public void setEffectiveDocumentCount(int effectiveDocumentCount) {
		this.effectiveDocumentCount = effectiveDocumentCount;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setMasterDocument(Document masterDocument) {
		this.masterDocument = masterDocument;
	}

	public void setInlineDocument(Document inlineDocument) {
		this.inlineDocument = inlineDocument;
	}

	public void setContent(ContentData content) {
		this.content = content;
	}

	public void setAttachmentDocument(Document attachmentDocument) {
		this.attachmentDocument = attachmentDocument;
	}
	
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	public String getMasterFilename() {
		return masterFilename;
	}

	public void setMasterFilename(String masterFilename) {
		this.masterFilename = masterFilename;
	}

	public String getInlineFilename() {
		return inlineFilename;
	}

	public void setInlineFilename(String inlineFilename) {
		this.inlineFilename = inlineFilename;
	}

	public String getAttachmentFilename() {
		return attachmentFilename;
	}

	public void setAttachmentFilename(String attachmentFilename) {
		this.attachmentFilename = attachmentFilename;
	}
	
	public Document getDocumentByName(String docName)
	{
		for(Document doc: this.getDocuments())
		{
			if (doc.getFilename().equals(docName))
			{
				return doc;
			}
		}
		return null;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[Title] "+this.getTitle()+"\n");
		sb.append("[Description] "+this.getDescription()+"\n");
		sb.append("[Language] "+this.getLanguage()+"\n");
		sb.append("[Language string] "+this.getLanguageString()+"\n");
		sb.append("[Documents size] "+this.getDocuments().size()+"\n");
		sb.append("[Effective documents] "+this.getEffectiveDocumentCount());
		return sb.toString();
	}
}


