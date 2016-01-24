package org.amnesty.aidoc;


import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "", propOrder = {
    "uuid",
    "type",
    "filename",
    "disposition",
    "mimetype",
    "language",
    "format",
    "title",
    "description",
    "effectiveFrom",
    "effectiveTo",
    "edition",
    "generated",
    "lastModified",
    "created"
})
@XmlRootElement(name = "document")
public class Document {
	
	@XmlElement(name="id", required = true)
	protected String uuid;
    @XmlElement(required = true)
    protected String type;
    @XmlElement(required = true)
    protected String filename;
    @XmlElement(required = true)
    protected String disposition;
    @XmlElement(required = true)
    protected String mimetype;
    @XmlElement(required = true)
    protected String language;
    @XmlElement(required = true)
    protected String format;
    @XmlElement(required = true)
    protected String title;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(name="effectiveDateFrom", required = true)
    protected Date effectiveFrom;
	@XmlElement(name="effectiveDateTo", required = true)
	protected Date effectiveTo;
	@XmlElement(required = true)
    protected String edition;
    @XmlElement(required = true)
    protected boolean generated;
    @XmlElement(name="modified", required = true)
    protected Date lastModified;
	@XmlElement(name="created", required = true)
	protected Date created;
    
    private NodeRef node;
	private String notes; 
	private ContentData content; 
	private String relationFormatOf;
	
	
	private boolean primary;
	private boolean effective;
	private String defaultLanguage; 

	private boolean inline;
	private long filesize;
	private boolean hasEdition;
	
	private Document[] hasParts = new Document[0];
	
	
	protected Document() {

	}


	public NodeRef getNode() {
		return node;
	}

	public void setNode(NodeRef node) {
		this.node = node;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ContentData getContent() {
		return content;
	}

	public void setContent(ContentData content) {
		this.content = content;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public Date getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public Date getEffectiveTo() {
		return effectiveTo;
	}

	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}

	public String getRelationFormatOf() {
		return relationFormatOf;
	}

	public void setRelationFormatOf(String relationFormatOf) {
		this.relationFormatOf = relationFormatOf;
	}

	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
	public boolean isEffective()
	{
		return effective;
	}
	
	public void setEffective(boolean effective) {
		this.effective = effective;
	}
	
	public boolean isInline() {
		return inline;
	}

	public String getFormat() {
		return format;
	}

	public long getFilesize() {
		return filesize;
	}

	public boolean isGenerated() {
		return generated;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setGenerated(boolean generated) {
		this.generated = generated;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setInline(boolean inline) {
		this.inline = inline;
	}

	public void setHasEdition(boolean hasEdition) {
		this.hasEdition = hasEdition;
	}

	public boolean isHasEdition() {
		return hasEdition;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public Document[] getHasParts() {
		return hasParts;
	}

	public void setHasParts(Document[] hasParts) {
		this.hasParts = hasParts;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[Default language] "+this.getDefaultLanguage()+"\n");
		sb.append("[Type] "+this.getType()+"\n");
		sb.append("[Name] "+this.getFilename()+"\n");
		sb.append("[Primary] "+this.isPrimary()+"\n");
		sb.append("[Title] "+this.getTitle()+"\n");
		sb.append("[Description] "+this.getDescription()+"\n");
		sb.append("[Source UUID] "+this.getRelationFormatOf()+"\n");
		sb.append("[Effective From] "+this.getEffectiveFrom()+"\n");
	    sb.append("[Effective To] "+this.getEffectiveTo()+"\n");
	    sb.append("[Last modified] "+this.getLastModified()+"\n");
		sb.append("[Created] "+this.getCreated()+"\n");
		sb.append("[Generated] "+this.isGenerated()+"\n");
		sb.append("[Content] "+this.getContent().toString()+"\n");
	    sb.append("[Filesize] "+this.getFilesize()+"\n");
	    sb.append("[Mimetype] "+this.getMimetype()+"\n");
	    sb.append("[Format] "+this.getFormat()+"\n");  
		sb.append("[Inline] "+this.isInline()+"\n");
	    sb.append("[Edition] "+this.getEdition());
	    return sb.toString();
	}
	
}
