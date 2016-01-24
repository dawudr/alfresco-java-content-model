// Create the model for the categories Web Script

// Function to get the depth of the hierarchy: assumes that categories cannot contain documents - all children are subcategories
// Also assumes that all children have the same hierarchy depth.

function getHierarchyDepth( category )
{

 if ( category.children.length > 0 )
 {
    return 1 + getHierarchyDepth( category.children[0] );
 }
 else return 0;

}

//Function to find a category matching terms down the hierarchy tree

function findCategory( categories, termArray, index )
{
  
  for ( i in categories )
  {

    if ( categories[i].name.equalsIgnoreCase( termArray[ index ] ) )
    {

      if ( ( index + 1 ) < termArray.length )
      {

        return findCategory( categories[i].children, termArray, index + 1 );

      }
      else return categories[i];

    }

  }

  return null;
 
}

var rootCategories = classification.getRootCategories( 'cm:generalclassifiable' );

model.title = decodeURI( url.extension );

model.found = false;

model.query = url.extension;

var termArray = new Array();

termArray = model.title.split( '/' );

model.baseCategory = findCategory( rootCategories, termArray, 0 );

if ( model.baseCategory != null )
{

  model.found = true;
  
  model.hierarchy = getHierarchyDepth( model.baseCategory ) - 1 ;
  
}


