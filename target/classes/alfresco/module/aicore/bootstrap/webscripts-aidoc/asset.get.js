
var NAMESPACE = "{http://www.amnesty.org/model/aicore/1.0}";
/*
* Utility functions
*/
function set404(message) {
    status.code = 404;
    status.redirect = true;
    status.message = (message != null) ? message : "Document for '" + url.extension + "' does not exist.";
}
function isEdition(node) {
    editionType = NAMESPACE + "Edition";
    if (node != null && node.type == editionType) {
        return true;
    } else {
        return false;
    }
}
function isDocument(node) {
    if (node != null && node.isDocument) {
        return true;
    } else {
        return false;
    }
}
function insertTypeIntoFilename(filename, partType) {
    stem = filename.substring(0, filename.lastIndexOf("."));
    ext = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
    return stem + "_" + partType.toLowerCase() + "." + ext;
}
// Algorithm to decide which doc to get
function getRendition(documents, lang, preferences,editions,ignoreEffectivity) {

    
    for each(edition in editions) {
        for each(preference in preferences) {
            for each(document in documents) {
                msg = document.node.name;
                if (document.edition == edition && (document.isEffective() || ignoreEffectivity) && document.language == lang && document.node.mimetype == preference) {
                    found = true;
                } else {
                    found = false;
                }
                //logger.log(msg + ": " + document.isEffective() + " - " + document.language + " - " + document.node.mimetype + " - " + preference + " - " + found);
                if (found) {
                    return document;
                }
            }
        }
    }
}
// Find implicit links between documents
function addRelationships(documents) {
    partTypes = ["Cover", "Contents"];
    for each(partType in partTypes) {
        for each(document in documents) {
            if (document.node.type == NAMESPACE + partType) {
                part = document.node.name;
                for each(doc in documents) {
                    // Add relationships between a Report and its cover
                    if (insertTypeIntoFilename(doc.node.name, partType) == part) {
                        //logger.log("Found match");
                        //document.isPartOf = doc;
                        doc.hasParts[0] = document;
                    }
                }
            }
        }
    }
}
function langToString(language){
        // Hack! Replace this will code that does the mapping using java Locale
        switch (String(language)) {
              case "en":
                result = "English";
                break;
              case "es":
                result = "Spanish";
                break;
              case "fr":
                result = "French";
                break;
              case "ar":
                result = "Arabic";
                break;
              case "ru":
                result = "Russian";
                break;
              default:
                        result = language;
        }
        
        return result;
}
/*
* Objects
*/
function Asset(extension) {
    aiIndexParts = extension.split("/");
    docPath = "/Aidoc/Asset Library/Indexed Documents/" + aiIndexParts[0] + "/" + aiIndexParts[1] + "/" + aiIndexParts[2];
    aiIndexFolder = roothome.childByNamePath(docPath);
                    
        // TODO. Check we found an aicore:Asset
    this.aiIndexParts = aiIndexParts;
    this.node = aiIndexFolder;
    this.aiIndex = aiIndexParts[1].substring(0, 3) + " " + aiIndexParts[1].substring(3, 5) + "/" + aiIndexParts[2] + "/" + aiIndexParts[0];
    this.latinTitle = aiIndexFolder.properties["title"];
    return this;
}
function Edition(node) {
    if (!isEdition(node)) {
        throw ("Not an edition");
    }
    
    //
    this.node = node;
    return this;
}
function Document(node, edition) {
    if (!isDocument(node)) {
        logger.log(node.name);
        throw ("Not a document type");
    }
    this.inline = null;
    switch (String(node.mimetype)) {
      case "application/rtf":
        this.format = "RTF";
        break;
      case "application/pdf":
        this.format = "PDF";
        break;
      case "text/html":
        this.format = "HTML";
        this.inline = true;
        break;
      case "application/msword":
        this.format = "MSWORD";
        break;
      case "application/xhtml+xml":
        this.inline = true;
        this.format = "XHTML";
        break;
      default:
        this.format = "UNKNOWN";
    }
    
    //
    this.node = node;
    source = node;
    this.type = String(node.type).substring(NAMESPACE.length);
    this.hasParts = [];

		// These values may be overridden if this node is copiedfrom
    this.language = String(node.properties["sys:locale"]);
    this.title = String(node.properties["title"]);
    this.description = String(node.properties["description"]);
    this.effectiveFrom = node.properties["from"];
    this.effectiveTo = node.properties["to"];

    // Set language and formatOf relation
    while (source != null && source.hasAspect("cm:copiedfrom")) {
        source = source.properties["cm:source"];
        if (source != null) {
            this.relationFormatOf = source.properties["sys:node-uuid"];
						this.language = String(source.properties["sys:locale"]);
						this.title = String(source.properties["title"]);
						this.description = String(source.properties["description"]);
						this.effectiveFrom = source.properties["from"];
						this.effectiveTo = source.properties["to"];
        }
    }
    this.url = url.serviceContext + "/api/node/content/workspace/SpacesStore/" + node.properties["sys:node-uuid"] + "/" + node.name;
    this.edition = edition.node.name;
    return this;
}
function Rendition(language, documents){
    this.language = language;
    this.languageString = langToString(language);

    preferences = ["application/msword", "application/rtf", "application/wordperfect","application/pdf"];
    editions = ["Standard Edition", "Formatted Edition", "Web Edition"];
    this.masterDocument = getRendition(documents, language, preferences, editions, true);

    preferences = ["application/xhtml+xml", "text/html"];
    editions = ["Formatted Edition", "Web Edition", "Standard Edition"];
    this.inlineDocument = getRendition(documents, language, preferences, editions, false);

    preferences = ["application/pdf", "application/msword", "application/rtf"];
    editions = ["Formatted Edition", "Web Edition", "Standard Edition"];
    this.attachmentDocument = getRendition(documents, language, preferences, editions, false);

    return this;
}

//New method for the Document class
function isEffective() {

		if (this.effectiveFrom)
			start = this.effectiveFrom;
		else
			start = new Date('1/1/1900');
			
		if (this.effectiveTo)
			end = this.effectiveTo;
		else
			end = new Date('1/1/2099');

		now = new Date();

		if (now > start && now < end)
			return true;
		else
			return false;

}
Document.prototype.isEffective = isEffective;
/*
* Main
*/
function main() {
    // Get the AiIndex and doc folder for the request url given
    var invalidDoc = false;
    var aiIndex, aiIndexFolder;
    var userLang = args["lang"];
    var plain = args["plain"];
    var editions = new Array();
    var documents = new Array();    

    if (userLang == null)
        userLang='en';
            
    // Do we have an asset?
    try {
        asset = new Asset(url.extension);
    }
    catch (e) {
        set404();
        return;
    }
    

    // Do we have an edition?
    for each(child in asset.node.children) {
        if (isEdition(child)) {
            editions[editions.length] = new Edition(child);
        }
    }
    if (editions.length < 1) {
        set404();
        return;
    }
    editionFolder = editions[0].node;

    // Do we have documents?
    for each(edition in editions) {
        for each(child in edition.node.children) {
            if (isDocument(child)) {
                documents[documents.length] = new Document(child, edition);
            }
        }
        // Any more documents in the transforms folders
        for each(child in edition.node.children) {
            if (child.isContainer) {
                for each(file in child.children) {
                    if (file.isDocument) {
                        documents[documents.length] = new Document(file, edition);
                    }
                }
            }
        }
    }
    if (documents.length < 1) {
        set404();
        return;
    }
    addRelationships(documents);

        // Get a list of languages available
        var languages = [];
        for each(doc in documents) {
            foundLang = false;
            for each(language in languages) {
                if(language == doc.language)
                    foundLang=true;
            }
            if (!foundLang){                
                languages[languages.length] = doc.language;
            }
        }
    model.languages = languages;

        // A Rendition is an object that groups files for display by language and business rules
    var renditions = [];
        for each(language in languages) {
            rend = new Rendition(language, documents);
            renditions[renditions.length] = rend; 
            if(language == userLang)
                model.selectedRendition = rend;
        }
        model.renditions = renditions;
    if (model.selectedRendition == null)
        model.selectedRendition = renditions[0];
    
    // Setup model
    model.asset = asset;
    model.assetFolder = asset.node;
    model.editionFolder = editionFolder;
    
    // Documents just for selected language
    var documentsByLanguage = [];
    for each(doc in documents) {
        if (doc.language == userLang) {
            documentsByLanguage[documentsByLanguage.length] = doc;
        }
    }
    model.documentsByLanguage = documentsByLanguage;

    // All docs
    model.documents = documents;
    model.lang = userLang;
    model.xsltUuid = "7479aa59-35dc-11dc-b7e3-2ba0d230dc8e";
    
    // Skin
    model.plain = plain;
    
    // Add caching
    cache.lastModified = new Date("1/1/2008");
    
    // Used to decide whether to display from notes
		publishDate = asset.node.properties["aicore:publishDate"];
    model.inAidoc = publishDate < new Date("5 Dec, 2007"); 
    
    
}
main();

