package org.amnesty.aidoc.feeder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gsafeed", propOrder = {
    "header",
    "group"
})
@XmlRootElement(name = "gsafeed")
public class GsaFeedImpl {

	@XmlElementRef
	protected List<Header> header = new ArrayList<Header>();
	@XmlElementWrapper (name = "group")
	@XmlElementRef
	protected List<Record> group = new ArrayList<Record>();

	public List<Header> getHeader() {
		return header;
	}

	public void setHeader(List<Header> header) {
		this.header = header;
	}

	public List<Record> getGroup() {
		return group;
	}

	public void setGroup(List<Record> group) {
		this.group = group;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "header", propOrder = {
        "datasource",
        "feedtype"
    })   
	@XmlRootElement(name = "header")       
    public static class Header {
        @XmlElement(required = true)
        protected String datasource;
        @XmlElement(required = true)
        protected String feedtype;

        public String getDatasource() {
            return datasource;
        }

        public void setDatasource(String value) {
            this.datasource = value;
        }

        public String getFeedtype() {
            return feedtype;
        }

        public void setFeedtype(String value) {
            this.feedtype = value;
        }
    }
     
	@XmlAccessorType(XmlAccessType.NONE)
	@XmlType(name = "record", propOrder = {
	    "meta",
	    "content"
	})
	@XmlRootElement(name = "record")    
    public static class Record {
        @XmlElement(required = true)
        protected String content;
    	@XmlElementWrapper (name = "metadata")        
        @XmlElement(nillable = true)
    	protected List<Meta> meta;
        @XmlAttribute(required = true)
        protected String displayurl;    	
        @XmlAttribute(required = true)
        protected String url;
        @XmlAttribute(required = true)
        protected String mimetype;
        @XmlAttribute(name = "last-modified")
        protected String lastModified;
        @XmlAttribute(required = true)
        protected String action;
        @XmlAttribute
        protected String encoding;
        @XmlAttribute
        protected boolean lock;
               
        public String getContent() {
            return content;
        }

        public void setContent(String value) {
            this.content = value;
        }

		public List<Meta> getMeta() {
			return meta;
		}

		public void setMeta(List<Meta> meta) {
			this.meta = meta;
		}		

		public String getDisplayUrl() {
			return displayurl;
		}

		public void setDisplayUrl(String displayUrl) {
			this.displayurl = displayUrl;
		}

		public String getUrl() {
            return url;
        }

        public void setUrl(String value) {
            this.url = value;
        }

        public String getMimetype() {
            return mimetype;
        }

        public void setMimetype(String value) {
            this.mimetype = value;
        }

        public String getLastModified() {
            return lastModified;
        }

        public void setLastModified(String value) {
            this.lastModified = value;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String value) {
            this.action = value;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String value) {
            this.encoding = value;
        }
        
        public boolean getLock() {
            return lock;
        }

        public void setLock(boolean value) {
            this.lock = value;
        }
    }
	       
	@XmlAccessorType(XmlAccessType.NONE)
	@XmlType(name = "meta", propOrder = {
	    "name",
	    "content"
	})
    public static class Meta {

        @XmlAttribute(required = true)
        protected String name;
        @XmlAttribute(required = true)
        protected String content;
        
		// constructor for meta data object
        
		public Meta() {	
		}
		
		public Meta(String name, String content) {
			this.name = name;
			this.content = content;		
		}

        public String getName() {
            return name;
        }

        public void setName(String value) {
            this.name = value;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String value) {
            this.content = value;
        }
    }   
}
