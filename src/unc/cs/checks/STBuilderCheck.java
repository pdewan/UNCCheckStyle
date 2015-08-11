package unc.cs.checks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sun.management.jmxremote.ConnectorBootstrap.PropertyNames;
import unc.cs.symbolTable.AnSTType;
import unc.cs.symbolTable.AnSTMethod;
import unc.cs.symbolTable.AnSTNameable;
import unc.cs.symbolTable.AnSTTypeFromClass;
import unc.cs.symbolTable.CallInfo;
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
	protected STType currentSTType;
	protected List<STMethod> stMethods = new ArrayList();
	protected List<STMethod> stConstructors = new ArrayList();
	public static final String MSG_KEY = "stBuilder";
	static String[] projectPackagePrefixes = {"assignment", "project", "homework"};
	protected static String[] existingClasses = {};
	protected static Collection<String> existingClassesCollection = new HashSet();
	boolean importsAsExistingClasses = false;
	 DetailAST sTBuilderTree = null; // make it non static at some point
	protected static STBuilderCheck singleton;
	
	
	public STBuilderCheck() {
		singleton =this;
	}
	protected static void processExistingClasses() {
		for (String aClassName:existingClasses) {
			processExistingClass(aClassName);
//			if (SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aClassName) != null)
//				continue;
//			try {
//				Class aClass = Class.forName(aClassName);
//				STType anSTType = new AnSTTypeFromClass(aClass);
//				addSTType(anSTType);
//			} catch (ClassNotFoundException e) {
//				System.err.println ("Unknown class existing clas: " + aClassName);
//				e.printStackTrace();
//			}
		}
	}
	protected static STBuilderCheck getSingleton() {
		return singleton;
	}
	
	protected  void processImports() {
		if (!getImportsAsExistingClasses())
			return;
		for (STNameable aClassName:imports) {
			processExistingClass(aClassName.getName());			
		}
	}
	
	protected static void processExistingClass(String aClassName) {
		if (SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aClassName) != null)
			return;
		try {
			Class aClass = Class.forName(aClassName);
			STType anSTType = new AnSTTypeFromClass(aClass);
			addSTType(anSTType);
		} catch (ClassNotFoundException e) {
			if  (existingClassesCollection.contains(aClassName))
			System.err.println ("Could not make existing class from: " + aClassName);
//			e.printStackTrace();
		}
		
	}

    
    protected void processPreviousMethodData() {
    	if (currentMethodName != null ) {
    		String[] aParameterTypes = currentMethodParameterTypes.toArray(new String[0]);
    		STMethod anSTMethod = new AnSTMethod(
    				currentMethodAST, 
    				currentMethodName, 
    				fullTypeName,
    				aParameterTypes, 
    				currentMethodIsPublic || isInterface,  
    				currentMethodIsInstance || isInterface,
    				currentMethodIsConstructor,
    				currentMethodType,
    				currentMethodIsVisible,
    				currentMethodTags.toArray(dummyArray),
    				currentMethodComputedTags.toArray(dummyArray),
    			
    				currentMethodAssignsToGlobalVariable,
//    				methodsCalledByCurrentMethod.toArray(new String[0][0]));
			        methodsCalledByCurrentMethod.toArray(new CallInfo[0]));

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
    
    public static String[] getProjectPackagePrefixes() {
    	return projectPackagePrefixes;
    }
    public  void setExistingClasses(String[] aClasses) {
    	existingClasses = aClasses;
    	existingClassesCollection = Arrays.asList(existingClasses);
    	processExistingClasses();
    }
    
    public boolean getImportsAsExistingClasses() {
    	return importsAsExistingClasses;
    }
    
    public  void setImportsAsExistingClasses(boolean aNewVal) {
    	importsAsExistingClasses = aNewVal;
    }
    
    public static String[] getExistingClasses() {
    	return existingClasses;
    }
		
		

    public void doBeginTree(DetailAST ast) {  
 		 super.doBeginTree(ast); 
 		 astToFileContents.put(ast, getFileContents());
// 		 System.out.println("Check" + checkAndFileDescription);
 		 	currentSTType = null;
 		 	stMethods.clear();
 		 	stConstructors.clear();
 		 	sTBuilderTree = ast;
 		 	
 	    }
	public DetailAST getSTBuilderTree() {
		return sTBuilderTree;
	}
	STNameable[] dummyArray = new STNameable[0];

	protected static void addSTType(STType anSTClass) {
		if (!anSTClass.isEnum()) {
		anSTClass.introspect();
    	anSTClass.findDelegateTypes();
		}
    	String aName = anSTClass.getName();
    	if (aName == null) {
    		System.err.println(" null name!");
    		return;
    	}
//    	SymbolTableFactory.getOrCreateSymbolTable().getTypeNameToSTClass().put(
//    			anSTClass.getName(), anSTClass);
    	SymbolTableFactory.getOrCreateSymbolTable().getTypeNameToSTClass().put(
    			aName, anSTClass);
//    	log (typeNameAST.getLineNo(), msgKey(), fullTypeName);
		
	}
	Object[] emptyArray = {};
	STMethod[] emptyMethods = {};
	STType[] emptyTypes = {};
	@Override
	public void visitEnumDef(DetailAST anEnumDef) {
		DetailAST aTypeAST = getEnclosingTypeDeclaration(anEnumDef);
		if (aTypeAST == anEnumDef) { // top-level enum
			super.visitEnumDef(anEnumDef);
			return;
		}
//		isEnum = true;
    	String anEnumName = getEnumName(anEnumDef);
    	String aFullName = packageName + "." + shortTypeName + "." + anEnumName;
	    	STType anSTClass = 
	    	new AnSTType(
	    			anEnumDef, 
	    			anEnumName, 
	    			emptyMethods, 
	    			emptyMethods,
	    			emptyTypes, 
	    			null, 
	    			packageName, 
	    			false,
	    			false,
	    			false,
	    			true,
	    			null,
	    			dummyArray,
	    			dummyArray,
	    			dummyArray,
	    			dummyArray,
	    			dummyArray,
	    			dummyArray,
	    			new HashMap(),
	    			new HashMap());

//	    	anSTClass.introspect();
//	    	anSTClass.findDelegateTypes();	    
//	    	SymbolTableFactory.getOrCreateSymbolTable().getTypeNameToSTClass().put(
//	    			fullTypeName, anSTClass);
	    	addSTType(anSTClass);


//    	shortTypeName = anEnumDef.getNextSibling().toString();
//    	DetailAST anEnumIdent = anEnumDef.getNextSibling().findFirstToken(TokenTypes.IDENT);
//    	if (anEnumIdent == null) {
//    		System.out.println("null enum ident");
//    	}
//    	shortTypeName = anEnumIdent.getText();
    }
    
//    protected static String getEnumName(DetailAST anEnumDef) {
//    	return getEnumAST(anEnumDef).toString();
//    }
//    protected static DetailAST getEnumAST(DetailAST anEnumDef) {
//    	return anEnumDef.getNextSibling();
//    }
	 
	  protected void processMethodAndClassData() {
		  processImports();
		  STMethod[] aMethods = stMethods.toArray(new STMethod[0]);
		  STMethod[] aConstructors = stConstructors.toArray(new STMethod[0]);
	    	STType anSTClass = new AnSTType(
	    			typeAST, 
	    			fullTypeName, // may be an inner class
	    			aMethods, 
	    			aConstructors,
	    			interfaces, 
	    			superClass, 
	    			packageName, 
	    			isInterface,
	    			isGeneric,
	    			isElaboration,
	    			isEnum,
	    			structurePattern,
	    			propertyNames.toArray(dummyArray),
	    			editablePropertyNames.toArray(dummyArray),
	    			typeTags().toArray(dummyArray),
	    			computedTypeTags().toArray(dummyArray),
	    			imports.toArray(dummyArray),
	    			globalVariables.toArray(dummyArray),
	    			new HashMap<>(globalVariableToCall),
	    			new HashMap<>(globalVariableToType));

//	    	anSTClass.introspect();
//	    	anSTClass.findDelegateTypes();	    
//	    	SymbolTableFactory.getOrCreateSymbolTable().getTypeNameToSTClass().put(
//	    			fullTypeName, anSTClass);
	    	addSTType(anSTClass);
//	    	log (typeNameAST.getLineNo(), msgKey(), fullTypeName);
//	        if (!defined) {
////	            log(ast.getLineNo(), MSG_KEY);
//	        }
		  
	  }
	  
	  public static void addKnownClass (Class aClass) {
		  
	  }


		@Override
		protected String msgKey() {
			// TODO Auto-generated method stub
			return MSG_KEY;
		}
	    public void doVisitToken(DetailAST ast) {
	    	super.doVisitToken(ast);
	    }
	    

}
