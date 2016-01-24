package org.amnesty.aidoc.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "", propOrder = {
	"action",
    "aiIndex",
    "latinTitle",
    "modified",
    "lastModified",
    "created",
    "publishDate",
    "securityClass",
    "type",
    "aiIndexStatus",
    "requestedBy",
    "originator",
    "network",
    "networkNumber",
    "indexNotes",
    "categories",
    "secondaryCategories",
    "renditions"
})
@XmlRootElement(name = "Asset")
public class AssetReportImpl{
	@XmlElement(required = true)
	private String action;
	@XmlElement(name="index", required = true)
	private String aiIndex;
	@XmlElement(required = true)
	private String latinTitle;
	@XmlElement(required = true)
    @XmlSchemaType(name = "date")
	private Date modified;
	@XmlElement(required = true)
    @XmlSchemaType(name = "date")
	private Date lastModified;
	@XmlElement(name="creationDate", required = true)
    @XmlSchemaType(name = "date")
	private Date created;
	@XmlElement(required = true)
    @XmlSchemaType(name = "date")
	private Date publishDate;
	@XmlElement(required = true)
	private String securityClass;
	@XmlElement(required = true)
	private String type;
	@XmlElement(name="status", required = true, defaultValue = "")
	private String aiIndexStatus = new String();
	@XmlElement(required = true, defaultValue = "")
	private String requestedBy = new String();
	@XmlElement(required = true, defaultValue = "")
	private String originator = new String();
	@XmlElement(required = true, defaultValue = "")
	private String network = new String();
	@XmlElement(required = true, defaultValue = "")
	private String networkNumber = new String();
	@XmlElement(required = true, defaultValue = "")
	private String indexNotes = new String();
	@XmlElementWrapper (name = "categories")
	@XmlElementRef
	private List<CategoryReportImpl> categories = new ArrayList<CategoryReportImpl>();
	@XmlElementWrapper (name = "secCategories")
	@XmlElementRef
	private List<CategoryReportImpl> secondaryCategories = new ArrayList<CategoryReportImpl>();
	@XmlElementWrapper (name = "renditions")
	@XmlElementRef
	private List<RenditionReportImpl> renditions = new ArrayList<RenditionReportImpl>();
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getAiIndex() {
		return aiIndex;
	}
	public void setAiIndex(String aiIndex) {
		this.aiIndex = aiIndex;
	}
	public String getLatinTitle() {
		return latinTitle;
	}
	public void setLatinTitle(String latinTitle) {
		this.latinTitle = latinTitle;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
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
	public Date getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	public String getSecurityClass() {
		return securityClass;
	}
	public void setSecurityClass(String securityClass) {
		this.securityClass = securityClass;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAiIndexStatus() {
		return aiIndexStatus;
	}
	public void setAiIndexStatus(String aiIndexStatus) {
		this.aiIndexStatus = aiIndexStatus;
	}
	public String getRequestedBy() {
		return requestedBy;
	}
	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}
	public String getOriginator() {
		return originator;
	}
	public void setOriginator(String originator) {
		this.originator = originator;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public String getNetworkNumber() {
		return networkNumber;
	}
	public void setNetworkNumber(String networkNumber) {
		this.networkNumber = networkNumber;
	}
	public String getIndexNotes() {
		return indexNotes;
	}
	public void setIndexNotes(String indexNotes) {
		this.indexNotes = indexNotes;
	}
	public List<CategoryReportImpl> getCategories() {
		return categories;
	}
	public void setCategories(List<CategoryReportImpl> categories) {
		this.categories = categories;
	}
	public List<CategoryReportImpl> getSecondaryCategories() {
		return secondaryCategories;
	}
	public void setSecondaryCategories(List<CategoryReportImpl> secondaryCategories) {
		this.secondaryCategories = secondaryCategories;
	}
	public List<RenditionReportImpl> getRenditions() {
		return renditions;
	}
	public void setRenditions(List<RenditionReportImpl> renditions) {
		this.renditions = renditions;
	}

}
