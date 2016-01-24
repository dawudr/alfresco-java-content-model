Alfresco Reporting Integration Solution Design Reference

Documents in Alfresco content repository are crawled and indexed using the crawling facility of the Google Search Appliance. Alfresco document urls are exposed by means of a crawl webscript in Aidoc which allows the GSA to replicate the company folder structure; Year/ClassCode/Document Number under which documents are indexed.
Currently the GSA is set to continuous crawl the Alfresco document repository on a daily basis at specified times outside the scheduled backup time.
However allowing the GSA to continuously crawl documents has the following drawbacks:
•	Unnecessary load on the Alfresco server - the frequency of documents in Alfresco being add, modified or deleted is minimal and does not require a continuous crawl process running constantly.
•	When there is downtime on the Alfresco server, the GSA crawler receives at HTTP Response code 404 – “Document not found" and assumes the document has been removed from the Alfresco repository. Therefore it attempts to remove the document from its index.
•	GSA crawler is a time consuming process and often requires approximately 3-4 days to crawl the whole Alfresco document collection.

Requirements

The proposed change to Alfresco’s integration with GSA is to use an asynchronous XML feed that is automatically triggered upon a change in Aidoc. This will use the Feed’s facility provided by the GSA. This will allow for the GSA continuous crawler to be switched off.
The use of feeds will have the following advantages:
•	Documents that can be crawled but are best recrawled at different times than those set by the automatic crawl scheduler that runs on the search appliance. 
•	Documents that can be crawled but there are no links on your web site that allow the crawler to discover them during a new crawl. 
•	Documents that can be crawled but are much more quickly uploaded using feeds, due to web server or network problems. 

How it works

The first step is to implement an action that includes logic to generate an XML feed with the properties of an Alfresco document node and send a multipart HTTP POST to GSA. Alfresco content repository has a mechanism to trigger actions, this is called behaviours. The behaviour can bound to a Policy which specifies the Alfresco event that will trigger the action to which the behaviour is attached to. 
Then the second stage is to collect the response from the GSA and verify that the feed has been successfully sent and indexed/removed in the GSA. The result of the response will be recorded as a new property added to the Asset node in Alfresco.
As there is a time lag between the GSA accepting the HTTP POST and completing the Indexing of a document, a third stage is required. This would be to implement another action that uses the Alfresco task scheduler to search the repository and verify the document has been successfully Indexed in the GSA. If it fails, this action will attempt to resend the feed again.
