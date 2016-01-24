package org.amnesty.aidoc;

public class NodeItem {

	private String name;
	private String url;
	private String anchor;
	// other properties are intended for XML output and are undefined for
	// non-asset nodes
	private String document_year;
	private String class_code;
	private String document_no;
	private String language;
	
	public NodeItem()
	{
		super();
	}
	
	public NodeItem(String name,String url)
	{
		this.setName(name);
		this.setUrl(url);
	}
	
	public NodeItem(String name,String url,String anchor,String document_year,String class_code,String document_no,String language )
	{
		this.setName(name);
		this.setUrl(url);
		this.setAnchor(anchor);
		this.setDocumentYear(document_year);
		this.setClassCode(class_code);
		this.setDocumentNo(document_no);
		this.setLanguage(language);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAnchor() {
		return anchor;
	}
	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}
	public String getDocumentYear() {
		return document_year;
	}
	public void setDocumentYear(String documentYear) {
		document_year = documentYear;
	}
	public String getClassCode() {
		return class_code;
	}
	public void setClassCode(String classCode) {
		class_code = classCode;
	}
	public String getDocumentNo() {
		return document_no;
	}
	public void setDocumentNo(String documentNo) {
		document_no = documentNo;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
}
