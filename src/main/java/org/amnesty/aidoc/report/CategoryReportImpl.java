package org.amnesty.aidoc.report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "category")
public class CategoryReportImpl {

	@XmlAttribute(required = true)
    protected String type;
	@XmlValue
	protected String value;
	
	public CategoryReportImpl()
	{
		super();
	}
	
	public CategoryReportImpl(String type, String value) {
		super();
		this.type = type;
		this.value = value;
	}
}
