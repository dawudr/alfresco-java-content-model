package org.amnesty.aidoc.service;

import org.apache.commons.httpclient.HttpStatus;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;

public class ServiceCallResult
{
   public final static int UNINITIALIZED = 0;
   public final static int PARSE_ERROR = -100;
   public final static int CONNECTION_ERROR = -200;
    
   private int httpStatusCode;
   private Document outputDocument;
   private String response;
   private String statusText;

   public ServiceCallResult()
   {
       httpStatusCode = UNINITIALIZED;
       statusText = "Status has not been set.";
   }
   
public String getResponse()
{
    return response;
}

public void setResponse( String response )
{
    this.response = response;
}

public int getHttpStatusCode()
{
    return httpStatusCode;
}

public void setHttpStatusCode( int httpStatusCode )
{
    this.httpStatusCode = httpStatusCode;
}
public Document getOutputDocument()
{
    return outputDocument;
}
public void setOutputDocument( Document outputDocument )
{
    this.outputDocument = outputDocument;
}

public String getStatusText()
{
    return statusText;
}

public void setStatusText( String statusText )
{
    this.statusText = statusText;
}
   
   public boolean failed()
   {
       return ( this.httpStatusCode != HttpStatus.SC_OK ) ;
   }

   public boolean notFound()
   {
       return ( this.httpStatusCode == HttpStatus.SC_NOT_FOUND );
   }
   
   public boolean unauthorized()
   {
       
       return ( this.httpStatusCode == HttpStatus.SC_UNAUTHORIZED );
       
   }
   
   public org.w3c.dom.Document getW3cDocument()
   {
    
     if ( outputDocument == null )
     {
       return null;
     }
     else
     {
       try
      {
        DOMOutputter outputter = new DOMOutputter();
        return outputter.output( outputDocument );
         
      } catch ( JDOMException e )
      {
        throw new RuntimeException( e.getMessage() );
      }
     }
   }
   
}
