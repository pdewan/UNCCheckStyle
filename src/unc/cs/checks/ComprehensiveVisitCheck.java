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
import sun.reflect.generics.scope.MethodScope;
import unc.cs.symbolTable.ACallInfo;
import unc.cs.symbolTable.AnSTType;
import unc.cs.symbolTable.AnSTMethod;
import unc.cs.symbolTable.AnSTNameable;
import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.AnnotationUtility;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.ScopeUtils;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

public abstract class ComprehensiveVisitCheck extends TagBasedCheck implements
ContinuationProcessor{
	
//	public static final String MSG_KEY = "stBuilder";
	protected boolean isEnum;
	protected boolean isInterface;
	protected boolean isElaboration;
	protected STNameable superClass;
	protected STNameable[] interfaces;
	protected boolean currentMethodIsConstructor;
	protected String currentMethodName;
	protected String currentMethodType;
	protected DetailAST currentMethodAST;
	protected boolean currentMethodIsPublic;
	protected boolean currentMethodAssignsToGlobalVariable;
	protected List<String[]> methodsCalledByCurrentMethod = new ArrayList();
	protected boolean currentMethodIsInstance;

	protected boolean currentMethodIsVisible;;
	protected List<String> currentMethodParameterTypes = new ArrayList();
//	protected List<STNameable> imports = new ArrayList();
//	protected static Set<String> externalImports = new HashSet();
//	protected static Set<String> javaLangClassesSet;
	protected List<STNameable> propertyNames;
	protected List<STNameable> editablePropertyNames;
//	protected List<STNameable> typeTags;
//	protected List<STNameable> currentMethodTags;
	protected Map<String, String> typeScope = new HashMap();
	protected List<STNameable> globalVariables = new ArrayList();
	protected Map<String, List<CallInfo>> globalVariableToCall = new HashMap();
	protected Map<String, String> currentMethodScope = new HashMap();
//	protected Set<String> excludeTags;
//	protected Set<String> includeTags;
//	protected DetailAST currentTree;
//	protected boolean tagsInitialized;
//	public static String[] javaLangClasses = {
//					"Integer",
//					"Double",
//					"Character",
//					"String",
//					"Boolean",
//	};
					
	


//	protected STNameable structurePattern;
	Map<DetailAST, List<DetailAST>> astToPendingChecks = new HashMap();
	Map<DetailAST, Object> astToContinuationData = new HashMap();

	Map<DetailAST, FileContents> astToFileContents = new HashMap();
	Map<String, DetailAST> fileNameToTree = new HashMap();

	
	@Override
	public int[] getDefaultTokens() {
		return new int[] {
						TokenTypes.PACKAGE_DEF, 
						TokenTypes.CLASS_DEF,  
						TokenTypes.INTERFACE_DEF, 
						TokenTypes.TYPE_ARGUMENTS,
						TokenTypes.TYPE_PARAMETERS,
						TokenTypes.VARIABLE_DEF,
						TokenTypes.PARAMETER_DEF,
						TokenTypes.METHOD_DEF, 
						TokenTypes.CTOR_DEF,
						TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT,
						TokenTypes.LCURLY,
						TokenTypes.RCURLY,
						TokenTypes.METHOD_CALL,
						TokenTypes.IDENT,
						TokenTypes.ENUM_DEF
						};
	}
	
//	public void setIncludeTags(String[] newVal) {
//		this.includeTags = new HashSet(Arrays.asList(newVal));		
//	}
//	public void setExcludeTags(String[] newVal) {
//		this.excludeTags = new HashSet(Arrays.asList(newVal));		
//	}
//	
//	public boolean hasExcludeTags() {
//		return excludeTags != null && excludeTags.size() > 1;
//	}
//	
//	public boolean hasIncludeTags() {
//		return includeTags != null && includeTags.size() > 1;
//	}
//	
//	public static boolean contains (Collection<String> aTags, String aTag) {
//		for (String aStoredTag:aTags) {
//			if (matchesStoredTag(aStoredTag, aTag))
//				return true;		
//		}
//		return false;
//	}
//	
//	public boolean checkTagOfCurrentType(String aTag) {
//		if (hasIncludeTags()) {
//			return contains(includeTags, aTag);
//		} else { // we know it has exclude tags
//			return !contains(excludeTags, aTag);
//		}
//	}
//	public boolean checkIncludeTagOfCurrentType(String aTag) {
//			return includeTags.contains(aTag);
//		
//	}
//	public boolean checkExcludeTagOfCurrentType(String aTag) {
//		return !excludeTags.contains(aTag);
//	
//   } 
//	public boolean checkIncludeTagOfCurrentType(String aTag) {
//		if (
//			return includeTags.contains(aTag);
//		} else { // we know it has exclude tags
//			return !excludeTags.contains(aTag);
//		}
//	}
	
//	public boolean checkTagsOfCurrentType() {
//		if (!hasIncludeTags() && !hasExcludeTags())
//			return true; // all tags checked in this case
//		if (fullTypeName == null) {
//			System.err.println("Check called without type name being populated");
//			return true;
//		}
////		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(typeName);
////		STNameable[] aCurrentTags = anSTType.getTags();
//		List<STNameable> aCurrentTags = typeTags;
//		for (STNameable aCurrentTag:aCurrentTags) {
//			if (checkTagOfCurrentType(aCurrentTag.getName()))
//					return true;
//		}
//		return false;
//		
//	}
//	  public static Boolean hasTag(STNameable[] aTags, String aTag) {
//	    	for (STNameable anSTNameable:aTags) {
//	    		if (anSTNameable.getName().equals(aTag)) return true;
//	    		
//	    	}
//	    	return false;
//	    }
//	public boolean checkIncludeTagsOfCurrentType() {
//		if (!hasIncludeTags() && !hasExcludeTags())
//			return true; // all tags checked in this case
//		if (fullTypeName == null) {
//			System.err.println("Check called without type name being populated");
//			return true;
//		}
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
//		STNameable[] aCurrentTags = anSTType.getTags();
//		if (hasIncludeTags())
//			return checkIncludeTagsOfCurrentType(aCurrentTags);
//		else
//			return checkExcludeTagsOfCurrentType(aCurrentTags);		
//	}
//	// if anyone says exclude, exclude
//	public boolean checkExcludeTagsOfCurrentType(STNameable[] aCurrentTags) {
//		
//		for (STNameable aCurrentTag:aCurrentTags) {
//			if (checkExcludeTagOfCurrentType(aCurrentTag.getName()))
//					return true;
//		}
//		return false;
//		
//	}
//	// if anyone says include, include
//     public boolean checkIncludeTagsOfCurrentType(STNameable[] aCurrentTags) {
//		
//		for (STNameable aCurrentTag:aCurrentTags) {
//			if (checkIncludeTagOfCurrentType(aCurrentTag.getName()))
//					return true;
//		}
//		return false;
//		
//	}
// 	static STNameable[] emptyNameableArray = {};
// 	static List<STNameable> emptyNameableList =new ArrayList();

 
	public static STNameable[] getInterfaces(DetailAST aClassDef) {
    	List<STNameable> anInterfaces = new ArrayList();
    	int numInterfaces = 0;
		DetailAST implementsClause = aClassDef
				.findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);
		if (implementsClause == null)
			return emptyNameableArray;
		DetailAST anImplementedInterface = implementsClause
				.findFirstToken(TokenTypes.IDENT);
		while (anImplementedInterface != null) {
			if (anImplementedInterface.getType() == TokenTypes.IDENT)
				anInterfaces.add(new AnSTNameable (anImplementedInterface, anImplementedInterface.getText()));
			anImplementedInterface = anImplementedInterface.getNextSibling();
		}
		return (STNameable[]) anInterfaces.toArray(emptyNameableArray);    	
    }
    
    
    public static STNameable[] getSuperTypes(DetailAST aClassDef) {
    	List<STNameable> aSuperTypes = new ArrayList();
    	STNameable[] emptyArray = {};
    	int numInterfaces = 0;
		DetailAST extendsClause = aClassDef
				.findFirstToken(TokenTypes.EXTENDS_CLAUSE);
		if (extendsClause == null)
			return emptyArray;
		DetailAST anExtendedType = extendsClause
				.findFirstToken(TokenTypes.IDENT);
		while (anExtendedType != null) {
			if (anExtendedType.getType() == TokenTypes.IDENT)
				aSuperTypes.add(new AnSTNameable(anExtendedType, anExtendedType.getText()));
			anExtendedType = anExtendedType.getNextSibling();
		}
		return (STNameable[]) aSuperTypes.toArray(emptyArray);
		
    }
//    public static List<STNameable> getArrayLiterals (DetailAST parentOfArrayInitializer) {
//    	List<STNameable> result = new ArrayList<>();
//    	 DetailAST arrayInit =
//    			parentOfArrayInitializer.findFirstToken(TokenTypes.ANNOTATION_ARRAY_INIT);
//		 if (arrayInit == null)
//			 arrayInit = parentOfArrayInitializer; // single element array
//		 DetailAST anArrayElementExpression = arrayInit.findFirstToken(TokenTypes.EXPR);
//		 
//		 while (anArrayElementExpression != null) {
//			 DetailAST anArrayElementAST = anArrayElementExpression.getFirstChild();
//			 result.add(new AnSTNameable(anArrayElementAST, anArrayElementAST.getText()));
//			 if (anArrayElementExpression.getNextSibling() == null)
//				 break;
//			 anArrayElementExpression = anArrayElementExpression.getNextSibling().getNextSibling();
//		 }
//		 return result;
//    }

//     protected List emptyArrayList = new ArrayList();
    public void maybeVisitPropertyNames(DetailAST ast) {  
    	// not putting dependency on OE
		DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "PropertyNames");		
		if (annotationAST == null) {
			propertyNames = emptyArrayList;
			return;
		}
		propertyNames = getArrayLiterals(annotationAST);
    }
    public void maybeVisitEditablePropertyNames(DetailAST ast) {  
    	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "EditablePropertyNames");		
		if (annotationAST == null) {
			editablePropertyNames = emptyArrayList;
			return;
		}
		editablePropertyNames = getArrayLiterals(annotationAST);
    }
    public void maybeVisitStructurePattern(DetailAST ast) {  
    	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "StructurePattern");		
		if (annotationAST == null) {
			structurePattern = null;
			return;
		}
//		if (structurePattern != null)
//			return;
		DetailAST expressionAST = annotationAST.findFirstToken(TokenTypes.EXPR);
		DetailAST actualParamAST = expressionAST.getFirstChild();
		FullIdent actualParamIDent = FullIdent.createFullIdent(actualParamAST);
		structurePattern = new AnSTNameable(actualParamAST, actualParamIDent.getText());		
    }
    public void maybeVisitVisible(DetailAST ast) {  
    	currentMethodIsVisible = true;
    	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Visible");		
		if (annotationAST == null)
			return;
		DetailAST expressionAST = annotationAST.findFirstToken(TokenTypes.EXPR);
		DetailAST actualParamAST = expressionAST.getFirstChild();
		FullIdent actualParamIDent = FullIdent.createFullIdent(actualParamAST);
		currentMethodIsVisible = !"false".equals(actualParamIDent.getText());
    }
//    protected List<STNameable> typeTags( ) {
//    	if (!tagsInitialized) {
//    		DetailAST aTypeAST = getEnclosingTypeDeclaration(currentTree);
//    		maybeVisitTypeTags(aTypeAST);
//    	}
//    	return typeTags;
//    }
//    public void maybeVisitTypeTags(DetailAST ast) { 
//    	if (tagsInitialized) return;
//    	tagsInitialized = true;
//    	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Tags");		
//		if (annotationAST == null) {
//			typeTags = emptyArrayList;
//			return;
//		}
//		typeTags = getArrayLiterals(annotationAST);
//    }
//    public void maybeVisitMethodTags(DetailAST ast) {  
//    	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Tags");		
//		if (annotationAST == null) {
//			currentMethodTags = emptyArrayList;
//			return;
//		}
//		currentMethodTags = getArrayLiterals(annotationAST);
//    }
    
    public void visitEnumDef(DetailAST anEnumDef) {
    	isEnum = true;
    	shortTypeName = getEnumName(anEnumDef);
		fullTypeName = packageName + "." + shortTypeName;
    	typeAST = anEnumDef;
    	superClass = null;
    	interfaces = emptyNameableArray;
		isInterface = false;


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
    
    public void visitType(DetailAST typeDef) {  
    	super.visitType(typeDef);
		maybeVisitStructurePattern(typeDef);
		maybeVisitPropertyNames(typeDef);
		maybeVisitEditablePropertyNames(typeDef);
		maybeVisitTypeTags(typeDef);
//		FullIdent aFullIdent = CheckUtils.createFullType(ast);
//		typeName = aFullIdent.getText();
    }
    public STNameable getPattern(String aShortClassName)  {
//    	List<STNameable> aTags = emptyList;

    	// these classes have no tags
//    	if ( aShortClassName.endsWith("[]") ||
//    			allKnownImports.contains(aShortClassName) || 
//    			javaLangClassesSet.contains(aShortClassName) ) {
//    		return emptyList;
//    	}
    	if ( isArray(aShortClassName) ||
    			isJavaLangClass(aShortClassName) ) {
    		return null;
    	}
    	if (shortTypeName == null || // guaranteed to not be a pending check
    			(aShortClassName.equals(shortTypeName) || aShortClassName.endsWith("." + shortTypeName))) {
    		return structurePattern;
    	} else {
    		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
    				.getSTClassByShortName(aShortClassName);
    	
    		if (anSTType == null) {
    			if (isExternalImport(aShortClassName)) // check last as we are not really sure about external
    				return null;			
    			return null;
    		}
    		if (anSTType.isEnum())
    			return null;
    		return anSTType.getStructurePatternName();
    	}
    	
    }
  protected void processPreviousMethodData() {
	  
  }

    
//    protected void processPreviousMethodData() {
//    	if (currentMethodName != null) {
//    		String[] aParameterTypes = currentMethodParameterTypes.toArray(new String[0]);
//    		STMethod anSTMethod = new AnSTMethod(
//    				currentMethodAST, 
//    				currentMethodName, 
//    				typeName,
//    				aParameterTypes, 
//    				currentMethodIsPublic,     				
//    				currentMethodType,
//    				currentMethodIsVisible);
//    		stMethods.add(anSTMethod);
//    	}
//    	
//    }
  public void visitMethod(DetailAST methodDef) {
  	processPreviousMethodData();
  	currentMethodIsConstructor = false;
  	visitMethodOrConstructor(methodDef);
  	maybeVisitMethodTags(methodDef);
  }
   public void visitConstructor(DetailAST methodDef) {
	  	processPreviousMethodData();
	  	currentMethodIsConstructor = true;
	  	visitMethodOrConstructor(methodDef);
   }

		
    public void visitMethodOrConstructor(DetailAST methodDef) {
//    	if (currentMethodName != null) {
//    		String[] aParameterTypes = currentMethodParameterTypes.toArray(new String[0]);
//    		STMethod anSTMethod = new AnSTMethod(
//    				currentMethodAST, 
//    				currentMethodName, 
//    				typeName,
//    				aParameterTypes, 
//    				currentMethodIsPublic,     				
//    				currentMethodType,
//    				currentMethodIsVisible);
//    		stMethods.add(anSTMethod);
//    	}    
//    	processPreviousMethodData();
    	currentMethodParameterTypes.clear(); 
    	currentMethodScope.clear();
	 	methodsCalledByCurrentMethod.clear();
	 	currentMethodAssignsToGlobalVariable = false;
	 	currentMethodTags = emptyNameableList;
//    	DetailAST aMethodNameAST = methodDef.findFirstToken(TokenTypes.IDENT);
//    	currentMethodName = aMethodNameAST.getText();
    	currentMethodName = getName(methodDef);

    	currentMethodIsPublic = isPublic(methodDef);
    	currentMethodIsInstance = !isStatic(methodDef);
    	if (!currentMethodIsConstructor) {
    	DetailAST typeDef = methodDef.findFirstToken(TokenTypes.TYPE);
    	FullIdent aTypeFullIdent = FullIdent.createFullIdent(typeDef.getFirstChild());
    	currentMethodType = aTypeFullIdent.getText();
        }
    	currentMethodAST = methodDef;
    	maybeVisitVisible(methodDef);    	
	}
    public void visitParamDef(DetailAST paramDef) {
    	final DetailAST grandParentAST = paramDef.getParent().getParent();
		if (!(grandParentAST.getType() == TokenTypes.METHOD_DEF))
			return;
		final DetailAST aType = paramDef.findFirstToken(TokenTypes.TYPE);
//		DetailAST aTypeNameAST = aType.findFirstToken(TokenTypes.IDENT);
//		String aTypeName = aTypeNameAST.getText();
		final DetailAST arrayDeclAST =
				aType.findFirstToken(TokenTypes.ARRAY_DECLARATOR);

		final FullIdent anIdentifierType = CheckUtils.createFullType(aType);
		String text = anIdentifierType.getText();
		if (arrayDeclAST != null)
			text = text + "[]";
		currentMethodParameterTypes.add(text);

    }
    public void visitTypeParameters(DetailAST typeParameters) {
    	isGeneric = true;

    }
    public void visitTypeArguments(DetailAST typeParameters) {
    	isElaboration = true;

    }
	public void visitClass(DetailAST ast) {
		visitType(ast);
		STNameable[] superTypes = getSuperTypes(ast);
		if (superTypes.length == 0)
			superClass = null;
		else
			superClass = superTypes[0];
		interfaces = getInterfaces(ast);
		isInterface = false;
	}
	 public void visitInterface(DetailAST ast) {
	    	visitType(ast);
	    	superClass = null;
	    	interfaces = getSuperTypes(ast);
			isInterface = true;
	 }
//	 public static boolean isProjectImport(String aFullName) {
//		 for (String aPrefix:STBuilderCheck.geProjectPackagePrefixes())
//			 if (aFullName.startsWith(aPrefix)) return true;
//		 return false;
//	 }
//	 public void visitImport(DetailAST ast) {
//		 FullIdent anImport = FullIdent.createFullIdentBelow(ast);
//		 String aLongClassName = anImport.getText();
//		 String aShortClassName = getLastDescendent(ast).getText();
//	
//		 STNameable anSTNameable = new AnSTNameable(ast, aLongClassName);
//		 imports.add(anSTNameable);
//		 if (!isProjectImport(aLongClassName))
//			 externalImports.add(aShortClassName);
//	 }
//	 public void visitStaticImport(DetailAST ast) {
//		 DetailAST anImportAST = ast.getFirstChild().getNextSibling();
//		 FullIdent anImport = FullIdent.createFullIdent(
//	                anImportAST);
//		 STNameable anSTNameable = new AnSTNameable(ast, anImport.getText());
//		 imports.add(anSTNameable);
//	 }
	 
	 public void visitLCurly(DetailAST ast) {
		 
	 }
	 
     public void visitRCurly(DetailAST ast) {
		 
	 }
     
     public void visitVariableOrParameterDef(DetailAST ast) {
    	 if (ScopeUtils.inCodeBlock(ast))
    		 addToMethodScope(ast);
    	 else
    		 addToTypeScope(ast);		 
	 }
     
     public String lookupType (String aVariable) {
    	 String result = lookupLocal(aVariable);
    	 if (result == null)
    		  result = lookupGlobal(aVariable);
    	 if (result == null) // method on a class?
    		 result = aVariable;
    	 return result;
     }
     
     public String lookupLocal(String aVariable) {
    	 return  currentMethodScope.get(aVariable);
     }
     public String lookupGlobal(String aVariable) {
    	 return  typeScope.get(aVariable);
     }
     
     public void visitVariableDef(DetailAST paramOrVarDef) {
    	 visitVariableOrParameterDef(paramOrVarDef);
    	 if (!ScopeUtils.inCodeBlock(paramOrVarDef)) {
    		 final DetailAST aType = paramOrVarDef.findFirstToken(TokenTypes.TYPE);
    	 		final DetailAST anIdentifier = paramOrVarDef.findFirstToken(TokenTypes.IDENT);
//    	 		final FullIdent anIdentifierType = CheckUtils.createFullType(aType);
//    	 		final FullIdent anIdentifierType = FullIdent.createFullIdent(aType);
    	 		STNameable anSTNameable = new AnSTNameable(paramOrVarDef, anIdentifier.getText(), aType.getText());
    	 		globalVariables.add(anSTNameable);
    	 }
		 
	 }
     
     public void visitParameterDef(DetailAST ast) {
    	 visitVariableOrParameterDef(ast);
	 }	
     
     public void addToMethodScope(DetailAST paramOrVarDef) {
//    	 final DetailAST aType = paramOrVarDef.findFirstToken(TokenTypes.TYPE);
// 		final DetailAST anIdentifier = paramOrVarDef.findFirstToken(TokenTypes.IDENT);
// 		final FullIdent anIdentifierType = CheckUtils.createFullType(aType);
// 		currentMethodScope.put(anIdentifier.getText(), anIdentifierType.getText());
    	 addToScope(paramOrVarDef, currentMethodScope);
     }
     
     public static String getTypeName (DetailAST paramOrVarDef) {
    	 return FullIdent.createFullIdentBelow(paramOrVarDef.findFirstToken(TokenTypes.TYPE)).getText();

     }
     
     public void addToScope(DetailAST paramOrVarDef, Map<String, String> aScope) {
//    	 final DetailAST aType = paramOrVarDef.findFirstToken(TokenTypes.TYPE);
 		final DetailAST anIdentifier = paramOrVarDef.findFirstToken(TokenTypes.IDENT);
// 		final FullIdent anIdentifierType = CheckUtils.createFullType(aType);
// 		final FullIdent anIdentifierType = FullIdent.createFullIdentBelow(aType);

 		aScope.put(anIdentifier.getText(), getTypeName(paramOrVarDef));
     }
     
     public void addToTypeScope(DetailAST paramOrVarDef) {
//    	 final DetailAST aType = paramOrVarDef.findFirstToken(TokenTypes.TYPE);
// 		final DetailAST anIdentifier = paramOrVarDef.findFirstToken(TokenTypes.IDENT);
// 		final FullIdent anIdentifierType = CheckUtils.createFullType(aType);
// 		typeScope.put(anIdentifier.getText(), anIdentifierType.getText());
    	 addToScope(paramOrVarDef, typeScope);
     }
     
     public Boolean isGlobal(String aName) {
    	 for (STNameable aGlobal:globalVariables) {
    		 if (aGlobal.getName().equals(aName))
    			 return true;
    	 }
    	 return false;
     }
     public static List<DetailAST> getEListComponents (DetailAST anEList) {
    	 List<DetailAST> result = new ArrayList();
    	 DetailAST aParameter = anEList.getFirstChild();
    	 
    	 while (true) {
    		 if (aParameter == null)
        		 return result;
    		 else 
    			 result.add(aParameter);
    		 DetailAST aCommaAST = aParameter.getNextSibling();
    		 if (aCommaAST == null)
    			 return result;
    		 aParameter = aCommaAST.getNextSibling();
    	 }     	  
    	 
     }
     // ouch from subclass
     public String toLongName(String[] aNormalizedName) {
 		StringBuffer retVal = new StringBuffer();
 		int index = 0;
 		while (true) {
 			if (index >= aNormalizedName.length) {
 				return retVal.toString();
 			}
 			if (index > 0)
 				retVal.append(".");
 			retVal.append(aNormalizedName[index]);
 			index++;
 		}
 	}
     /*
      * Ouch, I am going to ad side effects
      */
 	public CallInfo registerMethodCallAndtoNormalizedParts(DetailAST ast, DetailAST aTreeAST) {
// 		DetailAST aLeftMostMethodTargetAST = null;
// 		String shortMethodName = null;
// 		if (ast.getType() == TokenTypes.METHOD_CALL) {
// 		FullIdent aFullIndent = FullIdent.createFullIdentBelow(ast);
 		DetailAST aMethodNameAST = getLastDescendent(ast);
		String shortMethodName = aMethodNameAST.getText();

 		DetailAST aLeftMostMethodTargetAST = aMethodNameAST
 				.getPreviousSibling();
// 		aLeftMostMethodTargetAST = aMethodNameAST
// 				.getPreviousSibling();
// 		} else if (ast.getType() == TokenTypes.LITERAL_NEW) { 			
// 			final FullIdent anIdentifierType = FullIdent.createFullIdentBelow(ast);
// 			shortMethodName = toShortTypeName(anIdentifierType.getText());
// 			aLeftMostMethodTargetAST = null;
// 		}
 		DetailAST aCallEList = ast.findFirstToken(TokenTypes.ELIST);
 		List<DetailAST> aCallParameters = getEListComponents(aCallEList);
 		
 		
// 		String shortMethodName = aMethodNameAST.getText();
// 		 shortMethodName = aMethodNameAST.getText();

 		String[] aNormalizedParts = null;
 		if (currentTree != aTreeAST) {
 			aNormalizedParts = (String[]) astToContinuationData.get(ast);
 			if (aNormalizedParts == null) {
 				System.err.println("Normalizedname not saved");
 			}
 			// } else if (aLeftMostMethodTargetAST.getType() ==
 			// TokenTypes.STRING_LITERAL) {
 		} else {
 			String anInstantiatedType = maybeReturnInstantiatedType(ast.getFirstChild());
 			if (anInstantiatedType != null) {

 				aNormalizedParts = new String[] { anInstantiatedType, shortMethodName };

 			} else {
 				FullIdent aFullIdent = FullIdent.createFullIdentBelow(ast);
 				String longMethodName = aFullIdent.getText();

 				String[] aCallParts = longMethodName.split("\\.");
 				aNormalizedParts = toNormalizedClassBasedCall(aCallParts);
 			}
 		}
 		String aNormalizedLongName = toLongName(aNormalizedParts);
		String aCallee = toShortTypeName(aNormalizedLongName);
 		CallInfo result = new ACallInfo(
				currentMethodName, aNormalizedParts[0], aCallee, aCallParameters, aNormalizedParts );
 		if (aLeftMostMethodTargetAST != null) {
 		String aTargetName = aLeftMostMethodTargetAST.getText();
 		
 		if (isGlobal(aTargetName)) {
 			List<CallInfo> aCalls = getVariableCalls(aTargetName);
// 			CallInfo aCall = new ACallInfo(
// 					currentMethodName, aNormalizedParts[0], aNormalizedParts[1], aCallParameters, aNormalizedParts );
 			aCalls.add(result); 			
 		}
 		}
 		astToContinuationData.put(ast, aNormalizedParts);

 		return result;

 	}
 	// do nto really need this, the register method call works just fine!
 	public CallInfo registerConstructorCallAndtoNormalizedParts(DetailAST ast, DetailAST aTreeAST) {
 		
 			final FullIdent anIdentifierType = FullIdent.createFullIdentBelow(ast);
 			String shortMethodName = toShortTypeName(anIdentifierType.getText());
 		
 		DetailAST aCallEList = ast.findFirstToken(TokenTypes.ELIST);
 		List<DetailAST> aCallParameters = getEListComponents(aCallEList);
 		
 		
// 		String shortMethodName = aMethodNameAST.getText();
// 		 shortMethodName = aMethodNameAST.getText();

 		String[] aNormalizedParts = null;
 		if (currentTree != aTreeAST) {
 			aNormalizedParts = (String[]) astToContinuationData.get(ast);
 			if (aNormalizedParts == null) {
 				System.err.println("Normalizedname not saved");
 			}
 			// } else if (aLeftMostMethodTargetAST.getType() ==
 			// TokenTypes.STRING_LITERAL) {
 		} else {
 			

 				aNormalizedParts = new String[] {shortMethodName, shortMethodName};
 			}
 		
 		CallInfo result = new ACallInfo(
				currentMethodName, aNormalizedParts[0], aNormalizedParts[1], aCallParameters, aNormalizedParts );
 		
 		
 		astToContinuationData.put(ast, aNormalizedParts);


 		return result;

 	}
 	List<CallInfo> getVariableCalls(String aName) {
 		List<CallInfo> aVariableCalls = globalVariableToCall.get(aName);
 		if (aVariableCalls == null) {
 			aVariableCalls = new ArrayList();
 	 		globalVariableToCall.put(aName, aVariableCalls);
 		}
 		return aVariableCalls;		
 		
 		
 	}
     
     public void visitMethodCall(DetailAST ast) {
    	 String[] aNormalizedParts = registerMethodCallAndtoNormalizedParts(ast, currentTree).getNotmalizedCall();
 		methodsCalledByCurrentMethod.add(aNormalizedParts);
     }
     
     public void visitConstructorCall(DetailAST ast) {
    	 String[] aNormalizedParts = registerConstructorCallAndtoNormalizedParts(ast, currentTree).getNotmalizedCall();
 		methodsCalledByCurrentMethod.add(aNormalizedParts);
     }
     public void visitIdent(DetailAST anIdentAST) {
    	 if (currentMethodName == null)
    		 return;
    	 if (currentMethodAssignsToGlobalVariable)
    		 return;
    	 currentMethodAssignsToGlobalVariable = 
    			 isGlobalVariable(anIdentAST) ;
     }
     /*
      * Code taken from anIdentAST
      */
     public static boolean inAssignment(DetailAST anIdentAST) {
    	 final int parentType = anIdentAST.getParent().getType();
         // TODO: is there better way to check is ast
         // in left part of assignment?
        return ((TokenTypes.POST_DEC == parentType
                 || TokenTypes.DEC == parentType
                 || TokenTypes.POST_INC == parentType
                 || TokenTypes.INC == parentType
                 || TokenTypes.ASSIGN == parentType
                 || TokenTypes.PLUS_ASSIGN == parentType
                 || TokenTypes.MINUS_ASSIGN == parentType
                 || TokenTypes.DIV_ASSIGN == parentType
                 || TokenTypes.STAR_ASSIGN == parentType
                 || TokenTypes.MOD_ASSIGN == parentType
                 || TokenTypes.SR_ASSIGN == parentType
                 || TokenTypes.BSR_ASSIGN == parentType
                 || TokenTypes.SL_ASSIGN == parentType
                 || TokenTypes.BXOR_ASSIGN == parentType
                 || TokenTypes.BOR_ASSIGN == parentType
                 || TokenTypes.BAND_ASSIGN == parentType)
                 && anIdentAST.getParent().getFirstChild() == anIdentAST); // this should be checked first? 
     }
     
     public boolean isGlobalVariable (DetailAST anIdentAST ) {
    	 String aName = anIdentAST.getText();
    	 return lookupLocal(aName) == null && 
    			 inAssignment(anIdentAST);
    	 
     }
	
	protected List<DetailAST> pendingChecks() {
		List<DetailAST> result = astToPendingChecks.get(currentTree);
		if (result == null) {
			result = new ArrayList<>();
			astToPendingChecks.put(currentTree, result);
			astToFileContents.put(currentTree, getFileContents());
			fileNameToTree.put(getFileContents().getFilename(), currentTree);

		}
		return result;
	}
	protected String getShortFileName(DetailAST aTreeAST) {
		return shortFileName(astToFileContents.get(aTreeAST)
				.getFilename());
//		String aFileName;
//		if (aTreeAST == currentTree) {
//			aFileName = getFileContents().getFilename();
//		} else {			
//			aFileName = astToFileContents.get(aTreeAST)
//				.getFilename();
//		}
//		return shortFileName(aFileName);
	}
	 protected void maybeCleanUpPendingChecks(DetailAST aNewTree) {
		 String aFileName = getFileContents().getFilename();
		 DetailAST prevIncarnation = fileNameToTree.get(aFileName);
		 if (prevIncarnation != null) {
			 astToFileContents.remove(prevIncarnation);
			 astToPendingChecks.get(prevIncarnation).clear();
		 } 
		 pendingChecks().clear(); // this is needed to allocate a new entry
		 
	 }
	 @Override
	    public void doBeginTree(DetailAST ast) {  
		 super.doBeginTree(ast);
		 	currentMethodName = null;
		 	currentMethodAssignsToGlobalVariable = false;
		 	currentMethodScope.clear();
		 	methodsCalledByCurrentMethod.clear();
		 	typeTags = emptyNameableList;
		 	computedTypeTags = emptyNameableList;
		 	typeScope.clear();
		 	fullTypeName = null;
		 	isInterface = false;
		 	isEnum = false;
		 	isGeneric = false;
		 	isElaboration = false;
//		 	stMethods.clear();
		 	imports.clear();
		 	globalVariables.clear();
		 	globalVariableToCall.clear();
		 	currentTree = ast;
		 	tagsInitialized = false;
			propertyNames = emptyArrayList;
			editablePropertyNames = emptyArrayList;
		 	maybeCleanUpPendingChecks(ast);
//			pendingChecks().clear();
	    }
	  protected void processMethodAndClassData() {
		  
	  }
	 


	    @Override
	    public void doFinishTree(DetailAST ast) {
	    	System.out.println ("finish tree called:" + ast + " " + getFileContents().getFilename());
	    	if (currentMethodName != null)
	    	processPreviousMethodData();
	    	processMethodAndClassData();
	    	// this is more efficient
	    	processDeferredChecks(); 
//	    	ContinuationNotifierFactory.getOrCreateSingleton()
//			.notifyContinuationProcessors();
	    	System.out.println ("finish tree ended:" + ast + " " + getFileContents().getFilename());

	    }
	    
		public void processDeferredChecks() {
			doPendingChecks();

		}

//		public static boolean isPublicAndInstance(DetailAST methodOrVariableDef) {
//			boolean foundPublic = false;
//			final DetailAST modifiersAst = methodOrVariableDef
//					.findFirstToken(TokenTypes.MODIFIERS);
//			if (modifiersAst.getFirstChild() != null) {
//		
//				for (DetailAST modifier = modifiersAst.getFirstChild(); modifier != null; modifier = modifier
//						.getNextSibling()) {
//					// System.out.println("Checking modifier:" + modifier);
//					if (modifier.getType() == TokenTypes.LITERAL_STATIC) {
//						// System.out.println("Not instance");
//						return false;
//					}
//					if (modifier.getType() == TokenTypes.LITERAL_PUBLIC) {
//						foundPublic = true;
//					}
//		
//				}
//			}
//		
//			return foundPublic;
//		}
//		public static boolean isPublicAndInstance(DetailAST methodOrVariableDef) {
//			return isPublic(methodOrVariableDef) 
//					&& ! isStatic(methodOrVariableDef);
//		}
//		public static boolean isPublic(DetailAST methodOrVariableDef) {
//			return methodOrVariableDef.branchContains(TokenTypes.LITERAL_PUBLIC);
//					
//		}
//		public static boolean isStatic(DetailAST methodOrVariableDef) {
//			return methodOrVariableDef.branchContains(TokenTypes.LITERAL_STATIC);
//					
//		}
//		public static boolean isFinal(DetailAST methodOrVariableDef) {
//			return methodOrVariableDef.branchContains(TokenTypes.FINAL);
//					
//		}
//		public static boolean isStaticAndNotFinal(DetailAST methodOrVariableDef) {
//			boolean foundStatic = false;
//			final DetailAST modifiersAst = methodOrVariableDef
//					.findFirstToken(TokenTypes.MODIFIERS);
//			if (modifiersAst.getFirstChild() != null) {
//		
//				for (DetailAST modifier = modifiersAst.getFirstChild(); modifier != null; modifier = modifier
//						.getNextSibling()) {
//					// System.out.println("Checking modifier:" + modifier);
//					if (modifier.getType() == TokenTypes.FINAL) {
//						// System.out.println("Not instance");
//						return false;
//					}
//					if (modifier.getType() == TokenTypes.LITERAL_STATIC) {
//						foundStatic = true;
//					}
//		
//				}
//			}
//		
//			return foundStatic;
//		}
//		public static boolean isStaticAndNotFinal(DetailAST methodOrVariableDef) {
//			return isStatic (methodOrVariableDef)
//					&& ! isFinal(methodOrVariableDef);
//		}
		public Boolean doPendingCheck(DetailAST ast, DetailAST aTreeAST) {
			return false;
		}

		// pending check stuff
		public void doPendingChecks() {
			// for (List<FullIdent> aPendingTypeUses:astToPendingTypeUses.values())
			// {

			for (DetailAST aPendingAST : astToPendingChecks.keySet()) {
				if (aPendingAST == currentTree) continue; 
				List<DetailAST> aPendingChecks = astToPendingChecks.get(aPendingAST);
				// FileContents aFileContents = astToFileContents.get(anAST);
				// setFileContents(aFileContents);

				if (aPendingChecks.isEmpty())
					continue;
				List<DetailAST> aPendingTypeChecksCopy = new ArrayList(
						aPendingChecks);
				for (DetailAST aPendingCheck : aPendingTypeChecksCopy) {
					specificationVariablesToUnifiedValues.clear();
//					System.out.println ("Doing pending check: " + getName(getEnclosingTypeDeclaration(aPendingCheck)));
					if (doPendingCheck(aPendingCheck, aPendingAST) != null)
						aPendingChecks.remove(aPendingCheck);

				}
			}
		}
		protected void maybeAddToPendingTypeChecks(DetailAST ast) {
			if (!checkIncludeExcludeTagsOfCurrentType())
				return;
			specificationVariablesToUnifiedValues.clear();
			if (doPendingCheck(ast, currentTree) == null) {
//				System.out.println ("added to pending checks:" + getName(getEnclosingTypeDeclaration(ast)));
				List<DetailAST> aPendingChecks = pendingChecks();
				if (!aPendingChecks.contains(ast))
					aPendingChecks.add(ast);
//				pendingChecks().add(ast);
			}

			// if (isMatchingClassName(ident.getText())) {
			// log(ident.getLineNo(), ident.getColumnNo(), msgKey(),
			// ident.getText());
			// }
		}
		public static String shortFileName(String longName) {
			int index = longName.lastIndexOf('/');
			if (index <= 0)
				index = longName.lastIndexOf('\\');
			if (index <= 0)
				return longName;
			return longName.substring(index + 1);
		}
//		public static DetailAST getEnclosingMethodDeclaration(DetailAST anAST) {
//	    	return getEnclosingTokenType(anAST, TokenTypes.METHOD_DEF);
//	    }
//		
//		public static DetailAST getEnclosingClassDeclaration(DetailAST anAST) {
//	    	return getEnclosingTokenType(anAST, TokenTypes.CLASS_DEF);
//	    }
//		public static DetailAST getEnclosingInterfaceDeclaration(DetailAST anAST) {
//	    	return getEnclosingTokenType(anAST, TokenTypes.INTERFACE_DEF);
//	    }
//		public static DetailAST getEnclosingTypeDeclaration(DetailAST anAST) {
//			DetailAST aClassDef = getEnclosingClassDeclaration(anAST);
//			if (aClassDef == null)
//				return getEnclosingInterfaceDeclaration(anAST);
//			else
//				return aClassDef;
//	    }
		
		
		
//		public static String getEnclosingShortClassName(DetailAST anAST) {
//			return getName(getEnclosingClassDeclaration(anAST));
//		}
//		public static String getEnclosingShortTypeName(DetailAST anAST) {
//			return getName(getEnclosingTypeDeclaration(anAST));
//		}
//		public static String getEnclosingMethodName(DetailAST anAST) {
//			return getName(getEnclosingMethodDeclaration(anAST));
//		}
//	    // not physically but logically enclosing
//	    public static DetailAST getEnclosingTokenType(DetailAST anAST, int aTokenType) {
//	    	if (anAST == null) return null;
//	    	if (anAST.getType() == aTokenType) return anAST;
//	    	DetailAST aParent = anAST.getParent();
//	    	if (aParent != null)
//	    	   return getEnclosingTokenType(aParent, aTokenType);
//	    	return 
//	    			getFirstRightSiblingTokenType(anAST, aTokenType);
//	    }
//	    public static DetailAST getFirstRightSiblingTokenType(DetailAST anAST, int aTokenType) {
//	    	if (anAST == null) return null;
//	    	if (anAST.getType() == aTokenType) return anAST;
//	    	return getFirstRightSiblingTokenType(anAST.getNextSibling(), aTokenType);
//	    	
//	    }
	    public static DetailAST getRoot(DetailAST anAST, int aTokenType) {
	    	if (anAST == null) return null;
	    	DetailAST aParent = anAST.getParent();
	    	if (aParent == null)
	    		return aParent;
	    	return getRoot(aParent, aTokenType);
	    	
	    }
	    
	    public int lineNo(DetailAST ast, DetailAST aTreeAST) {
	    	 return aTreeAST == currentTree?ast.getLineNo():0;
	    }
	    public int lineNo(FullIdent aFullIdent, DetailAST aTreeAST) {
	    	 return aTreeAST == currentTree?aFullIdent.getLineNo():0;
	    }
	    public int columnNo(DetailAST ast, DetailAST aTreeAST) {
	         return  aTreeAST == currentTree?ast.getColumnNo():0;
	    }
	    public int columnNo(FullIdent aFullIdent, DetailAST aTreeAST) {
	         return  aTreeAST == currentTree?aFullIdent.getColumnNo():0;
	    }
	    
//	    public boolean contains(List<STNameable> aTags, String aTag) {
//	    	for (STNameable aNameable:aTags) {
//	    		if (matchesStoredTag(aNameable.getName(), aTag))
//
////	    		if (aNameable.getName().equals(aTag))
//	    			return true;	    		
//	    	}
//	    	return false;
//	    }
//	    
//	    public static String maybeStripQuotes(String aString) {
//	    	if (aString.indexOf("\"") != -1) // quote rather than named constant
//	    		return aString.substring(1, aString.length() -1);
//	    	return aString;
//	    }
//	    public static Boolean matchesStoredTag(String aStoredTag, String aDescriptor) {
//	    		return maybeStripQuotes(aStoredTag).equals(maybeStripQuotes(aDescriptor));
//	    	
//	    }
//	    
//	    public  Boolean matchesMyType( String aDescriptor,  String aShort) {
//	    	String aClassName = shortTypeName;
//	    	if (aDescriptor == null || aDescriptor.length() == 0)
//	    		return true;
//	    	if (aDescriptor.startsWith("@")) {
//	    		String aTag = aDescriptor.substring(1);
//	    		return contains(typeTags(), aTag);	    		
//	    	}
//			return aClassName.equals(aDescriptor);
//		 }
	  
//	 static List<STNameable> emptyList = new ArrayList();
//	 public static boolean isArray(String aShortClassName) {
//		 return aShortClassName.endsWith("[]");
//	 }
//	 public static boolean isJavaLangClass(String aShortClassName) {
//		 return javaLangClassesSet.contains(aShortClassName);
//	 }
//	 public static boolean isExternalImport(String aShortClassName) {
//		 return externalImports.contains(aShortClassName);
//	 }
//	 public List<STNameable> getTags(String aShortClassName)  {
//		List<STNameable> aTags = emptyList;
//
//		// these classes have no tags
////		if ( aShortClassName.endsWith("[]") ||
////				allKnownImports.contains(aShortClassName) || 
////				javaLangClassesSet.contains(aShortClassName) ) {
////			return emptyList;
////		}
//		if ( isArray(aShortClassName) ||
//				isJavaLangClass(aShortClassName) ) {
//			return emptyList;
//		}
//		if (shortTypeName == null || // guaranteed to not be a pending check
//				aShortClassName.equals(shortTypeName)) {
//			aTags = typeTags();
//		} else {
//			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//					.getSTClassByShortName(aShortClassName);
//			if (anSTType == null) {
//				if (isExternalImport(aShortClassName)) // check last as we are not really sure about external
//					return emptyList;			
//				return null;
//			}
//			aTags = Arrays.asList(anSTType.getTags());
//		}
//		return aTags;
//		
//	}
//	public Boolean matchesType(String aDescriptor, String aShortClassName) {
//		if (aDescriptor == null || aDescriptor.length() == 0)
//			return true;
//		if (!aDescriptor.startsWith("@")) {
//			return aShortClassName.equals(aDescriptor);
//		}
//		List<STNameable> aTags = getTags(aShortClassName);
//		if (aTags == null)
//			return null;
////		List<STNameable> aTags = null;
////		if (shortTypeName == null || // guaranteed to not be a pending check
////				aShortClassName.equals(shortTypeName)) {
////			aTags = typeTags();
////		} else {
////			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
////					.getSTClassByShortName(aShortClassName);
////			if (anSTType == null)
////				return null;
////			aTags = Arrays.asList(anSTType.getTags());
////		}
//		String aTag = aDescriptor.substring(1);
//
//		return contains(aTags, aTag);
//	}
	    protected boolean isPrefix (String aTarget, List<String> aPrefixes, String aSourceClassName) {
			 for (String aPrefix:aPrefixes) {
				 String[] aPrefixParts = aPrefix.split(TYPE_SEPARATOR);
				 if ((aPrefixParts.length == 2) && !matchesType(aPrefixParts[0], aSourceClassName))
					 continue; // not relevant
				 String aTruePrefix = aPrefixParts.length == 2?aPrefixParts[1]:aPrefix;
				 if (aTarget.startsWith(aTruePrefix) || aTruePrefix.equals("*"))
					 return true;
			 }
			 return false;
		 }
	    protected boolean containedInClasses (String aTarget, List<String> aList, String aSourceClassName) {
			 for (String aMember:aList) {
				 String[] aMemberParts = aMember.split(TYPE_SEPARATOR);
//				 if ((aMemberParts.length == 2) && !matchesMyType(aMemberParts[0], aSourceClassName))

				 if ((aMemberParts.length == 2) && !matchesType(aMemberParts[0], aSourceClassName))
					 continue; // not relevant
				 String aTrueMember = aMemberParts.length == 2?aMemberParts[1]:aMember;
				 if (aTarget.equals(aTrueMember))
					 return true;
			 }
			 return false;
		 }
//	    public static DetailAST getLastDescendent(DetailAST ast) {
//	    	DetailAST result = ast.getFirstChild();
//	    	while (result.getChildCount() > 0)
//	    		result = result.getLastChild();    	
//	    	return result;    	
//	    }
	    /*
	     * checking if the target of call is an instantiated type
	     */
	    public static String maybeReturnInstantiatedType(DetailAST ast) {
			DetailAST aNewAST = ast.findFirstToken(TokenTypes.LITERAL_NEW);
			if (aNewAST != null) {
				DetailAST anIdentAST = aNewAST.findFirstToken(TokenTypes.IDENT);
				if (aNewAST.findFirstToken(TokenTypes.ARRAY_DECLARATOR) != null || 
						aNewAST.findFirstToken(TokenTypes.ARRAY_INIT) != null) {
					return anIdentAST.getText() + "[]";
				} else return anIdentAST.getText();
			} else if (ast.findFirstToken(TokenTypes.STRING_LITERAL) != null) {
				return "String";
			} else
				
				return null;
		}
	    public String[] toNormalizedClassBasedCall(String[] aCallParts) {
			List<String> aCallPartsList = new ArrayList();
			if (aCallParts.length == 1 || "this".equals(aCallParts[0])) { // put the
																			// name
																			// of
																			// the
																			// class
																			// in
																			// which
																			// the
																			// call
																			// occurs
				aCallPartsList.add(fullTypeName);
				aCallPartsList.add(aCallParts[aCallParts.length - 1]);
			} else if (aCallParts.length == 2) {
				String aType = lookupType(aCallParts[0]);
				if (aType != null) { // not a static method
					aCallPartsList.add(aType);
					aCallPartsList.add(aCallParts[1]);
				} else {
					return aCallParts; // static call
				}
			} else {
				return aCallParts; // System.out.println() probabluy
			}
			return aCallPartsList.toArray(new String[0]);
		}
//	    static {
//	    	javaLangClassesSet = new HashSet();
//	    	for (String aClass:javaLangClasses)
//	    		javaLangClassesSet.add(aClass);
//	    }
	    public void visitNew(DetailAST ast) {
	    	if (ast.findFirstToken(TokenTypes.ELIST) != null)
				visitConstructorCall(ast);
				else if (ast.findFirstToken(TokenTypes.ARRAY_DECLARATOR) != null)
					;
//					System.out.println ("array declaration");
	    }
	    public void visitTypeUse(DetailAST ast) {
	    	
	    }
	    public void doVisitToken(DetailAST ast) {
//	    	System.out.println("Check called:" + MSG_KEY);
			switch (ast.getType()) {
			case TokenTypes.PACKAGE_DEF: 
				visitPackage(ast);
				return;
			case TokenTypes.CLASS_DEF:
				visitClass(ast);
				return;
			case TokenTypes.INTERFACE_DEF:
				visitInterface(ast);
				return;
			case TokenTypes.METHOD_DEF:
				visitMethod(ast);
				return;
			case TokenTypes.CTOR_DEF:
				visitConstructor(ast);
				return;
			case TokenTypes.PARAMETER_DEF:
				visitParamDef(ast);
				return;
			case TokenTypes.TYPE_PARAMETERS:
				visitTypeParameters(ast);
				return;		
			case TokenTypes.TYPE_ARGUMENTS:
				visitTypeArguments(ast);
				return;
			case TokenTypes.IMPORT:
				visitImport(ast);
				return;
			case TokenTypes.STATIC_IMPORT:
				visitStaticImport(ast);
				return;
			case TokenTypes.LCURLY:
				visitLCurly(ast);
				return;
			case TokenTypes.RCURLY:
				visitRCurly(ast);
				return;
			case TokenTypes.VARIABLE_DEF:
				visitVariableDef(ast);
				return;
			case TokenTypes.LITERAL_NEW:
//				if (ast.findFirstToken(TokenTypes.ELIST) != null)
//				visitConstructorCall(ast);
//				else if (ast.findFirstToken(TokenTypes.ARRAY_DECLARATOR) != null)
//					System.out.println ("array declaration");
				visitNew(ast);
				return;
			case TokenTypes.METHOD_CALL:
				visitMethodCall(ast);
				return;
			case TokenTypes.IDENT:
				visitIdent(ast);
				return;
			case TokenTypes.ENUM_DEF:
				visitEnumDef(ast);
				return;
			case TokenTypes.TYPE:
				visitTypeUse(ast);
				return;
				
			default:
				System.err.println("Unexpected token");
			}
			
		}
	    
}
