

function main(){

		var baseref = "/Aidoc/Asset Library/Indexed Documents";
		var extn = url.extension;
			
		if (extn.length() == 0){
		  model.level = 0;
		  extn = '/';
		}else{

			// Remove trailing slash
			if (extn.length() != 0 && extn.lastIndexOf('/') == (extn.length() - 1))
				extn = extn.substring(0, extn.length()-1);
			
		  aiIndexParts = extn.split('/');
		  model.level = aiIndexParts.length;

			// Add it back in again
			extn += '/';
		
		}	

		switch (model.level){
		  case 0:
				model.docPath = baseref;
		    break;
		  case 1:
				model.docPath = baseref + "/" + aiIndexParts[0] ;
		    break;
		  case 2:
				model.docPath = baseref + "/" + aiIndexParts[0] + "/" + aiIndexParts[1];
				model.year = aiIndexParts[0];
				model.aiClass = aiIndexParts[1];
		    break;
		 }
	     
		model.selectedFolder = roothome.childByNamePath(model.docPath);
		model.extn = extn;
}

main();
