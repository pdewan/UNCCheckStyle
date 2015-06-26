package unc.cs.checks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sun.management.jmxremote.ConnectorBootstrap.PropertyNames;
import unc.cs.symbolTable.AnSTType;
import unc.cs.symbolTable.AnSTMethod;
import unc.cs.symbolTable.AnSTNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.AnnotationUtility;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

public class STBuilderCheck extends ComprehensiveVisitCheck{
	protected List<STMethod> stMethods = new ArrayList();
	protected List<STMethod> stConstructors = new ArrayList();
	public static final String MSG_KEY = "stBuilder";
	static String[] projectPackagePrefixes = {"assignment", "project", "homework"};
	

    
    protected void processPreviousMethodData() {
    	if (currentMethodName != null ) {
    		String[] aParameterTypes = currentMethodParameterTypes.toArray(new String[0]);
    		STMethod anSTMethod = new AnSTMethod(
    				currentMethodAST, 
    				currentMethodName, 
    				fullTypeName,
    				aParameterTypes, 
    				currentMethodIsPublic,  
    				currentMethodIsInstance,
    				currentMethodType,
    				currentMethodIsVisible,
    				currentMethodTags.toArray(dummyArray),
    				currentMethodAssignsToGlobalVariable,
    				methodsCalledByCurrentMethod.toArray(new String[0][0]));
    		if (currentMethodIsConstructor)
    			stConstructors.add(anSTMethod);
    		else
    		stMethods.add(anSTMethod);
    	}
    	currentMethodName = null;
    	
    }
    
    public void setProjectPackagePrefixes(String[] aPrefixes) {
    	projectPackagePrefixes = aPrefixes;
    }
    
    public static String[] geProjectPackagePrefixes() {
    	return projectPackagePrefixes;
    }
		
		

    public void beginTree(DetailAST ast) {  
 		 super.beginTree(ast); 		 	
 		 	stMethods.clear();
 		 	stConstructors.clear();
 	    }
	STNameable[] dummyArray = new STNameable[0];

	 
	  protected void processMethodAndClassData() {
		  STMethod[] aMethods = stMethods.toArray(new STMethod[0]);
		  STMethod[] aConstructors = stConstructors.toArray(new STMethod[0]);
	    	STType anSTClass = new AnSTType(
	    			typeAST, 
	    			fullTypeName, 
	    			aMethods, 
	    			aConstructors,
	    			interfaces, 
	    			superClass, 
	    			packageName, 
	    			isInterface,
	    			isGeneric,
	    			isElaboration,
	    			structurePattern,
	    			propertyNames.toArray(dummyArray),
	    			editablePropertyNames.toArray(dummyArray),
	    			typeTags.toArray(dummyArray),
	    			imports.toArray(dummyArray),
	    			globalVariables.toArray(dummyArray));
//	    	anSTClass.initDeclaredPropertyNames(propertyNames.toArray(dummyArray));
//	    	anSTClass.initEditablePropertyNames(editablePropertyNames.toArray(dummyArray));
//	    	anSTClass.initTags(tags.toArray(dummyArray));
//	    	anSTClass.initStructurePatternName(structurePattern);
	    	anSTClass.introspect();
	    	SymbolTableFactory.getOrCreateSymbolTable().getTypeNameToSTClass().put(
	    			fullTypeName, anSTClass);
	    	log (typeNameAST.getLineNo(), msgKey(), fullTypeName);
//	        if (!defined) {
////	            log(ast.getLineNo(), MSG_KEY);
//	        }
		  
	  }


		@Override
		protected String msgKey() {
			// TODO Auto-generated method stub
			return MSG_KEY;
		}
}
