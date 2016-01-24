package org.amnesty.aidoc;
/**
 * 
 */


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author iramosbi
 *
 */

public class Asset{
	
	private NodeRef node;
	
	private String uuid;
	private String aiIndex;
	private String securityClass;
	private Date modified;
	private Date lastModified;
	private Date created;
	private Date publishDate;
	private String latinTitle;
	private String type;
	private String description;
	private List<String> categories = new ArrayList<String>();
	private List<String> secondaryCategories = new ArrayList<String>();

	
	private List<Document> documents = new ArrayList<Document>();
	private HashMap<String,String> languages = new HashMap<String,String>();
	private List<Document> auxiliarDocuments = new ArrayList<Document>();
	private ArrayList<Edition> editions = new ArrayList<Edition>();
	
	private String classCode;
	private String documentYear;
	private String subclass;	
	private String documentNo;
	private boolean isPublic = false;
	private boolean isPublished;
	private String publicationStatus;
	private String originator;
	private String creator;
	private String notes; 
	private boolean typeMismatch = false;
	private String aiIndexStatus;
	private String requestedBy;
	private String network;
	private String networkNumber;

	private HashSet<String> primaryPathRegions = new HashSet<String>();
	private HashSet<String> secondaryPathRegions = new HashSet<String>();
	private HashSet<String> primaryRegions = new HashSet<String>();
	private HashSet<String> secondaryRegions = new HashSet<String>();
	private HashSet<String> primaryKeywords = new HashSet<String>();
	private HashSet<String> secondaryKeywords = new HashSet<String>();
	private HashSet<String> primaryCampaigns = new HashSet<String>();
	private HashSet<String> secondaryCampaigns = new HashSet<String>();
	private HashSet<String> primaryIssues = new HashSet<String>();
	private HashSet<String> secondaryIssues = new HashSet<String>();
	private HashSet<String> allPathRegions = new HashSet<String>();
	private HashSet<String> allRegions = new HashSet<String>();
	private HashSet<String> allKeywords = new HashSet<String>();
	private HashSet<String> allCampaigns = new HashSet<String>();
	private HashSet<String> allIssues = new HashSet<String>();
	private HashSet<String> allPathCategories = new HashSet<String>();
	private HashSet<String> allCategories = new HashSet<String>();
	
	// Problems Hashset
	private HashSet<String> problems =new HashSet<String>();

    
    protected Asset() {
    	
    }
	
	public String getUuid() {
		return uuid;
	}



	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ArrayList<Edition> getEditions() {
		return editions;
	}
	
	public void addEdition(Edition editionObject) {
		this.editions.add(editionObject);
	}	
	
	public String getAiIndex() {
		return aiIndex;
	}
	
	public String getClassCode() {
		return classCode;
	}
	
	public String getDocumentYear() {
		return documentYear;
	}


	public String getDocumentNo() {
		return documentNo;
	}
	
	public String getSubclass() {
		return subclass;
	}
	
	public String getSecurityClass() {
		return securityClass;
	}
	
	public boolean isPublic() {
		return isPublic;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public boolean isPublished() {
		return isPublished;
	}

	public String getPublicationStatus() {
		return publicationStatus;
	}

	
	public String getOriginator() {
		return originator;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public HashSet<String> getPrimaryPathRegions() {
		return primaryPathRegions;
	}
	
	public void setPrimaryPathRegions(HashSet<String> primaryPathRegions) {
		this.primaryPathRegions = primaryPathRegions;
	}

	public HashSet<String> getSecondaryPathRegions() {
		return secondaryPathRegions;
	}

	public void setSecondaryPathRegions(HashSet<String> secondaryPathRegions) {
		this.secondaryPathRegions = secondaryPathRegions;
	}
	
	public HashSet<String> getPrimaryRegions() {
		return primaryRegions;
	}
	public void setPrimaryRegions(HashSet<String> primaryRegions) {
		this.primaryRegions = primaryRegions;
	}
	public HashSet<String> getSecondaryRegions() {
		return secondaryRegions;
	}
	public void setSecondaryRegions(HashSet<String> secondaryRegions) {
		this.secondaryRegions = secondaryRegions;
	}
	public HashSet<String> getAllRegions() {
		return allRegions;
	}
	public void setAllRegions(HashSet<String> allRegions) {
		this.allRegions = allRegions;
	}
	
	public HashSet<String> getAllPathRegions() {
		return allPathRegions;
	}

	public void setAllPathRegions(HashSet<String> allPathRegions) {
		this.allPathRegions = allPathRegions;
	}

	public HashSet<String> getAllPathCategories() {
		return allPathCategories;
	}

	public void setAllPathCategories(HashSet<String> allPathCategories) {
		this.allPathCategories = allPathCategories;
	}
	public HashSet<String> getPrimaryKeywords() {
		return primaryKeywords;
	}
	public void setPrimaryKeywords(HashSet<String> primaryKeywords) {
		this.primaryKeywords = primaryKeywords;
	}
	public HashSet<String> getSecondaryKeywords() {
		return secondaryKeywords;
	}
	public void setSecondaryKeywords(HashSet<String> secondaryKeywords) {
		this.secondaryKeywords = secondaryKeywords;
	}
	public HashSet<String> getPrimaryCampaigns() {
		return primaryCampaigns;
	}
	public void setPrimaryCampaigns(HashSet<String> primaryCampaigns) {
		this.primaryCampaigns = primaryCampaigns;
	}
	public HashSet<String> getSecondaryCampaigns() {
		return secondaryCampaigns;
	}
	public void setSecondaryCampaigns(HashSet<String> secondaryCampaigns) {
		this.secondaryCampaigns = secondaryCampaigns;
	}
	public HashSet<String> getPrimaryIssues() {
		return primaryIssues;
	}
	public void setPrimaryIssues(HashSet<String> primaryIssues) {
		this.primaryIssues = primaryIssues;
	}
	public HashSet<String> getSecondaryIssues() {
		return secondaryIssues;
	}
	public void setSecondaryIssues(HashSet<String> secondaryIssues) {
		this.secondaryIssues = secondaryIssues;
	}
	public HashSet<String> getAllKeywords() {
		return allKeywords;
	}
	public void setAllKeywords(HashSet<String> allKeywords) {
		this.allKeywords = allKeywords;
	}
	public HashSet<String> getAllCampaigns() {
		return allCampaigns;
	}
	public void setAllCampaigns(HashSet<String> allCampaigns) {
		this.allCampaigns = allCampaigns;
	}
	public HashSet<String> getAllIssues() {
		return allIssues;
	}
	public void setAllIssues(HashSet<String> allIssues) {
		this.allIssues = allIssues;
	}
	public HashSet<String> getAllCategories() {
		return allCategories;
	}
	public void setAllCategories(HashSet<String> allCategories) {
		this.allCategories = allCategories;
	}		

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	
	public List<String> getSecondaryCategories() {
		return secondaryCategories;
	}
	
	public void setSecondaryCategories(List<String> secondaryCategories) {
		this.secondaryCategories = secondaryCategories;
	}

	public String getLatinTitle() {
		return latinTitle;
	}	

	public NodeRef getNode() {
		return node;
	}	

	public String getType() {
		return type;
	}

	public Date getModified() {
		return modified;
	}
	
	public Date getLastModified() {
		return lastModified;
	}	
	
	public Date getCreated() {
		return created;
	}
	
	public HashSet<String> getProblems() {
		return problems;
	}


	public List<Document> getDocuments()
	{	
		return documents;
	}
	
	public String getNotes() {
		return notes;
	}


	public boolean isTypeMismatch() {
		return typeMismatch;
	}
	
	public HashMap<String,String> getLanguages() {
		return languages;
	}



	public void setLanguages(HashMap<String,String> languages) {
		this.languages = languages;
	}



	public void setAiIndex(String aiIndex) {
		this.aiIndex = aiIndex;
	}



	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}



	public void setDocumentYear(String documentYear) {
		this.documentYear = documentYear;
	}



	public void setSubclass(String subclass) {
		this.subclass = subclass;
	}



	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}



	public void setSecurityClass(String securityClass) {
		this.securityClass = securityClass;
	}



	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}



	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}



	public void setPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}



	public void setPublicationStatus(String publicationStatus) {
		this.publicationStatus = publicationStatus;
	}



	public void setOriginator(String originator) {
		this.originator = originator;
	}



	public void setCreator(String creator) {
		this.creator = creator;
	}



	public void setLatinTitle(String latinTitle) {
		this.latinTitle = latinTitle;
	}



	public void setNode(NodeRef node) {
		this.node = node;
	}


	public void setCreated(Date created) {
		this.created = created;
	}



	public void setType(String type) {
		this.type = type;
	}



	public void setNotes(String notes) {
		this.notes = notes;
	}


	public void setTypeMismatch(boolean typeMismatch) {
		this.typeMismatch = typeMismatch;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public void setEditions(ArrayList<Edition> editions) {
		this.editions = editions;
	}


	public void setProblems(HashSet<String> problems) {
		this.problems = problems;
	}


	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	
	public void addDocument(Document document)
	{
		this.documents.add(document);
	}

	
	public List<Document> getAuxiliarDocuments() {
		return auxiliarDocuments;
	}
	
	public void setAuxiliarDocuments(List<Document> auxiliarDocuments) {
		this.auxiliarDocuments = auxiliarDocuments;
	}
	
	public void addAuxiliarDocument(Document document)
	{
		this.auxiliarDocuments.add(document);
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
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[AiIndex] "+this.getAiIndex()+"\n");		
		sb.append("[Asset Publish date] "+this.getPublishDate()+"\n");
		sb.append("[Created] "+this.getCreated()+"\n");
		sb.append("[Last Modified] "+this.getLastModified()+"\n");
		sb.append("[Creator] "+this.getCreator()+"\n");
		sb.append("[Originator] "+this.getOriginator()+"\n");
		sb.append("[Publication status] "+this.getPublicationStatus()+"\n");
		sb.append("[Node] "+this.getNode()+"\n");
		sb.append("[Title] "+this.getLatinTitle()+"\n");
		sb.append("[Type] "+this.getType()+"\n");
		sb.append("[Security class] "+this.getSecurityClass()+"\n");
		sb.append("[Public] "+this.isPublic()+"\n");
		sb.append("[Primary Categories size] " + this.getCategories().size()+"\n");
		sb.append("[Secondary Categories size] " + this.getSecondaryCategories().size()+"\n");
		sb.append("[Editions size] "+this.getEditions().size()+"\n");
		sb.append("[Documents size] "+this.getDocuments().size()+"\n");
		sb.append("[Problems] " + this.getProblems().toString());
		return sb.toString();
	}

}
