package org.amnesty.aidoc.report;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "", propOrder = {
    "title",
    "description",
    "lastModified"
})
@XmlRootElement(name = "rendition")
public class RenditionReportImpl {

	
    @XmlAttribute(required = true)
    protected String language = new String();
    
    @XmlElement(required = true)
	private String title = new String();
	@XmlElement(required = true)
	private String description = new String();
	@XmlElement(required = true)
    protected Date lastModified;
	
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
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
}
