package org.amnesty.aidoc;
/**
 * 
 */


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author iramosbi
 *
 */

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "", propOrder = {
    "name",
    "title",
    "description",
    "documents"
})
@XmlRootElement(name = "edition")
public class Edition {
	
	private NodeRef node;

	@XmlElement(required = true)
	private String name;
	@XmlElement(required = true)
	private String title;
	@XmlElement(required = true)
	private String description;
	@XmlElementWrapper (name = "documents")
	@XmlElementRef
	private List<Document> documents = new ArrayList<Document>();

	private String creator;
	private Date modified;

	protected Edition() {
	}	
	
	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getCreator() {
		return creator;
	}
	
	public NodeRef getNode() {
		return node;
	}
	
	public void setNode(NodeRef node) {
		this.node = node;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public Date getLastModified() {
		return modified;
	}

	public void setLastModified(Date modified) {
		this.modified = modified;
	}
	
	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	
	public void addDocument(Document document)
	{
		documents.add(document);
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[Name] "+this.getName()+"\n");
		sb.append("[Title] "+this.getTitle()+"\n");
		sb.append("[Description] "+this.getDescription()+"\n");
		sb.append("[Last Modified] "+this.getLastModified()+"\n");
		sb.append("[Creator] "+this.getCreator());
		return sb.toString();
	}
}
