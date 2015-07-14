package unc.cs.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.AnnotationUtility;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.cs.symbolTable.AnSTNameable;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public abstract class TagBasedCheck extends TypeVisitedCheck{
	protected Set<String> excludeTags;
	protected Set<String> includeTags;
	protected boolean tagsInitialized;
	protected List<STNameable> typeTags;
	protected STNameable pattern;
	protected List<STNameable> currentMethodTags;
	static STNameable[] emptyNameableArray = {};
 	static List<STNameable> emptyNameableList =new ArrayList();
	protected DetailAST currentTree;
	protected Map<String, Integer> typeToInt = new Hashtable<>();
	protected Map<String, String> specificationVariablesToUnifiedValues = new Hashtable<>();
	
	protected List<String> variablesAdded = new ArrayList(); //should be cleared by a matcher
	@Override
	public int[] getDefaultTokens() {
		return new int[] {						
						TokenTypes.CLASS_DEF,  
						TokenTypes.INTERFACE_DEF, 
						};
	}
	
	public static String[] primitiveTypes = {
		"int",
		"double",
		"char",
		"boolean",
		
};

	public static String[] javaLangTypes = {
//		"int",
//		"double",
//		"char",
//		"boolean",
		"Integer",
		"Double",
		"Character",
		"String",
		"Boolean",
};
	static List<STNameable> emptyList = new ArrayList();

	protected static Set<String> externalImports = new HashSet();
	protected List<STNameable> imports = new ArrayList();

	protected static Set<String> javaLangTypesSet;
	protected static Set<String> primitiveTypesSet;

	public void setIncludeTags(String[] newVal) {
		this.includeTags = new HashSet(Arrays.asList(newVal));		
	}
	public void setExcludeTags(String[] newVal) {
		this.excludeTags = new HashSet(Arrays.asList(newVal));		
	}
	
	public boolean hasExcludeTags() {
		return excludeTags != null && excludeTags.size() > 0;
	}
	
	public boolean hasIncludeTags() {
		return includeTags != null && includeTags.size() > 0;
	}
	
	public static boolean contains (Collection<String> aTags, String aTag) {
		for (String aStoredTag:aTags) {
			if (matchesStoredTag(aStoredTag, aTag))
				return true;		
		}
		return false;
	}
	
	public boolean checkTagOfCurrentType(String aTag) {
		if (hasIncludeTags()) {
			return contains(includeTags, aTag);
		} else { // we know it has exclude tags
			return !contains(excludeTags, aTag);
		}
	}
	public boolean checkIncludeTagOfCurrentType(String aTag) {
			return includeTags.contains(aTag);
		
	}
	public boolean checkExcludeTagOfCurrentType(String aTag) {
		return !excludeTags.contains(aTag);
	
   } 
	public static Boolean hasTag(STNameable[] aTags, String aTag) {
    	for (STNameable anSTNameable:aTags) {
    		if (matchesStoredTag(anSTNameable.getName(), aTag)) return true;
    		
    	}
    	return false;
    }
	
	protected int getInt(String aType) {
		Integer retVal = typeToInt.get(aType);
		if (retVal == null) return typeToInt.get("*"); // should not be exercised
		return retVal;
	}
public boolean checkIncludeTagsOfCurrentType() {
	if (!hasIncludeTags() && !hasExcludeTags())
		return true; // all tags checked in this case
	if (fullTypeName == null) {
//		System.err.println("Check called without type name being populated");
		// could be the wrong type, class or interface, so we do nto have to check
		return false;
	}
	STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
	STNameable[] aCurrentTags = anSTType.getTags();
	if (hasIncludeTags())
		return checkIncludeTagsOfCurrentType(aCurrentTags);
	else
		return checkExcludeTagsOfCurrentType(aCurrentTags);		
}
// if anyone says exclude, exclude
public boolean checkExcludeTagsOfCurrentType(STNameable[] aCurrentTags) {
	
	for (STNameable aCurrentTag:aCurrentTags) {
		if (checkExcludeTagOfCurrentType(aCurrentTag.getName()))
				return true;
	}
	return false;
	
}
// if anyone says include, include
 public boolean checkIncludeTagsOfCurrentType(STNameable[] aCurrentTags) {
	
	for (STNameable aCurrentTag:aCurrentTags) {
		if (checkIncludeTagOfCurrentType(aCurrentTag.getName()))
				return true;
	}
	return false;
	
}
 public boolean contains(List<STNameable> aTags, String aTag) {
 	for (STNameable aNameable:aTags) {
 		if (matchesStoredTag(aNameable.getName(), aTag)) {
 			matchedTypeOrTagAST = aNameable.getAST();
// 		if (aNameable.getName().equals(aTag))
 			return true;
 		}
 	}
 	return false;
 }
 
 // could return a list also
 public  String findMatchingType (Set<String> aSpecifiedTypes, STType anSTType) {
		for (String aSpecifiedType:aSpecifiedTypes) {
			matchedTypeOrTagAST = anSTType.getAST();
				if (matchesType(aSpecifiedType, anSTType.getName()))
					return aSpecifiedType; 
		}
		return null;
	}
 
 public static String maybeStripQuotes(String aString) {
 	if (aString.indexOf("\"") != -1) // quote rather than named constant
 		return aString.substring(1, aString.length() -1);
 	return aString;
 }
 public static Boolean matchesStoredTag(String aStoredTag, String aDescriptor) {
 		return maybeStripQuotes(aStoredTag).equals(maybeStripQuotes(aDescriptor));
 	
 }
 
 public static boolean isJavaLangClass(String aShortClassName) {
	 return javaLangTypesSet.contains(aShortClassName);
 }
 public static boolean isExternalImport(String aShortClassName) {
	 return externalImports.contains(aShortClassName);
 }
 
 public static boolean isExternalClass(String aShortClassName) {
	STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aShortClassName);
	if (anSTType != null)
		return false;
	return aShortClassName.equals("Object") || isExternalImport(aShortClassName) || isJavaLangClass(aShortClassName);
 }
 public List<STNameable> getTags(String aShortClassName)  {
	List<STNameable> aTags = emptyList;

	// these classes have no tags
//	if ( aShortClassName.endsWith("[]") ||
//			allKnownImports.contains(aShortClassName) || 
//			javaLangClassesSet.contains(aShortClassName) ) {
//		return emptyList;
//	}
	if ( isArray(aShortClassName) ||
			isJavaLangClass(aShortClassName) ) {
		return emptyList;
	}
	if (shortTypeName == null || // guaranteed to not be a pending check
			aShortClassName.equals(shortTypeName)) {
		aTags = typeTags();
	} else {
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aShortClassName);
		if (anSTType == null) {
			if (isExternalImport(aShortClassName)) // check last as we are not really sure about external
				return emptyList;			
			return null;
		}
		aTags = Arrays.asList(anSTType.getTags());
	}
	return aTags;
	
}
 public Boolean matchesNameOrVariable(String aDescriptor, String aName) {
	 if (aDescriptor.startsWith("$")) {
			String aUnifiedValue = specificationVariablesToUnifiedValues
					.get(aDescriptor);
			if (aUnifiedValue == null) {
				specificationVariablesToUnifiedValues.put(aDescriptor,
						aUnifiedValue);
				variablesAdded.add(aDescriptor);
				return true;
			} else {
				return aName.equals(aUnifiedValue);
			}
		} else {
			return aName.equals(aDescriptor);
		}
	 
 }
 protected void backTrackUnification() {
	 for (String aVariable:variablesAdded) {
		 specificationVariablesToUnifiedValues.remove(aVariable);
	 }
	 variablesAdded.clear();
 }
//for some reason this is not supposed to call matchedMyType with clas name
	public Boolean matchesMyType(String aDescriptor) {
		String aClassName = shortTypeName;
		if (aDescriptor == null || aDescriptor.length() == 0 || aDescriptor.equals("*"))
			return true;
		if (aDescriptor.startsWith("@")) {
			String aTag = aDescriptor.substring(1);
			return contains(typeTags(), aTag);
		} else {
			return matchesNameOrVariable(aDescriptor, aClassName);
		}
			
//		} else if (aDescriptor.startsWith("$")) {
//			String aUnifiedValue = specificationVariablesToUnifiedValues
//					.get(aDescriptor);
//			if (aUnifiedValue == null) {
//				specificationVariablesToUnifiedValues.put(aDescriptor,
//						aUnifiedValue);
//				return true;
//			} else {
//				return aClassName.equals(aUnifiedValue);
//			}
//		} else {
//			return aClassName.equals(aDescriptor);
//		}
	}
 protected DetailAST matchedTypeOrTagAST;
 

public Boolean matchesType(String aDescriptor, String aShortClassName) {
	if (aDescriptor == null || aDescriptor.length() == 0 || aDescriptor.equals("*" ))
		return true;
//	if (aDescriptor.equals("*"))
//		return true;
	if (!aDescriptor.startsWith("@")) {
//		return aShortClassName.equals(aDescriptor);
		return matchesNameOrVariable(aDescriptor, aShortClassName);
	}
	List<STNameable> aTags = getTags(aShortClassName);
	if (aTags == null)
		return null;

	String aTag = aDescriptor.substring(1);

	return contains(aTags, aTag);
}
public boolean checkTagsOfCurrentType() {
	// this makes no sense to me
	if (!hasIncludeTags() && !hasExcludeTags())
		return true; // all tags checked in this case
	if (fullTypeName == null) {
//		System.err.println("Check called without type name being populated");
		return false;
	}
//	STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(typeName);
//	STNameable[] aCurrentTags = anSTType.getTags();
	List<STNameable> aCurrentTags = typeTags;
	for (STNameable aCurrentTag:aCurrentTags) {
		if (checkTagOfCurrentType(aCurrentTag.getName()))
				return true;
	}
	return false;
	
}
protected List<STNameable> typeTags( ) {
	if (!tagsInitialized) {
		DetailAST aTypeAST = getEnclosingTypeDeclaration(currentTree);
		maybeVisitTypeTags(aTypeAST);
	}
	return typeTags;
}

public static List<STNameable> getArrayLiterals (DetailAST parentOfArrayInitializer) {
	List<STNameable> result = new ArrayList<>();
	 DetailAST arrayInit =
			parentOfArrayInitializer.findFirstToken(TokenTypes.ANNOTATION_ARRAY_INIT);
	 if (arrayInit == null)
		 arrayInit = parentOfArrayInitializer; // single element array
	 DetailAST anArrayElementExpression = arrayInit.findFirstToken(TokenTypes.EXPR);
	 
	 while (anArrayElementExpression != null) {
		 DetailAST anArrayElementAST = anArrayElementExpression.getFirstChild();
		 result.add(new AnSTNameable(anArrayElementAST, anArrayElementAST.getText()));
		 if (anArrayElementExpression.getNextSibling() == null)
			 break;
		 anArrayElementExpression = anArrayElementExpression.getNextSibling().getNextSibling();
	 }
	 return result;
}
protected List emptyArrayList = new ArrayList();

public void maybeVisitTypeTags(DetailAST ast) { 
	if (tagsInitialized) return;
	tagsInitialized = true;
	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Tags");		
	if (annotationAST == null) {
		typeTags = emptyArrayList;
		return;
	}
	typeTags = getArrayLiterals(annotationAST);
}

//public void maybeVisitPattern(DetailAST ast) { 
//	if (patternInitialized) return;
//	patternInitialized = true;
//	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "StructurePattern");		
//	if (annotationAST == null) {
//		typeTags = emptyArrayList;
//		return;
//	}
//	typeTags = getArrayLiterals(annotationAST);
//}
public void maybeVisitMethodTags(DetailAST ast) {  
	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Tags");		
	if (annotationAST == null) {
		currentMethodTags = emptyArrayList;
		return;
	}
	currentMethodTags = getArrayLiterals(annotationAST);
}
public void visitImport(DetailAST ast) {
	 FullIdent anImport = FullIdent.createFullIdentBelow(ast);
	 String aLongClassName = anImport.getText();
	 String aShortClassName = getLastDescendent(ast).getText();

	 STNameable anSTNameable = new AnSTNameable(ast, aLongClassName);
	 imports.add(anSTNameable);
	 if (!isProjectImport(aLongClassName))
		 externalImports.add(aShortClassName);
}
public static boolean isProjectImport(String aFullName) {
	 for (String aPrefix:STBuilderCheck.geProjectPackagePrefixes())
		 if (aFullName.startsWith(aPrefix)) return true;
	 return false;
}
public static DetailAST getLastDescendent(DetailAST ast) {
	DetailAST result = ast.getFirstChild();
	while (result.getChildCount() > 0)
		result = result.getLastChild();    	
	return result;    	
}
public void visitStaticImport(DetailAST ast) {
	 DetailAST anImportAST = ast.getFirstChild().getNextSibling();
	 FullIdent anImport = FullIdent.createFullIdent(
               anImportAST);
	 STNameable anSTNameable = new AnSTNameable(ast, anImport.getText());
	 imports.add(anSTNameable);
}
public static boolean isArray(String aShortClassName) {
	 return aShortClassName.endsWith("[]");
}
public void doBeginTree(DetailAST ast) {  
	 super.doBeginTree(ast);
	 	
	 	typeTags = emptyNameableList;
//	 	typeScope.clear();
//	 	fullTypeName = null;
//	 	isInterface = false;
//	 	isGeneric = false;
//	 	isElaboration = false;
////	 	stMethods.clear();
//	 	imports.clear();
//	 	globalVariables.clear();
	 	currentTree = ast;
	 	tagsInitialized = false;
//	 	maybeCleanUpPendingChecks(ast);
//		pendingChecks().clear();
   }
public static DetailAST getEnclosingMethodDeclaration(DetailAST anAST) {
	return getEnclosingTokenType(anAST, TokenTypes.METHOD_DEF);
}

public static DetailAST getEnclosingClassDeclaration(DetailAST anAST) {
	return getEnclosingTokenType(anAST, TokenTypes.CLASS_DEF);
}
public static DetailAST getEnclosingInterfaceDeclaration(DetailAST anAST) {
	return getEnclosingTokenType(anAST, TokenTypes.INTERFACE_DEF);
}
public static DetailAST getEnclosingTypeDeclaration(DetailAST anAST) {
	DetailAST aClassDef = getEnclosingClassDeclaration(anAST);
	if (aClassDef == null)
		return getEnclosingInterfaceDeclaration(anAST);
	else
		return aClassDef;
}
public static String getEnclosingShortClassName(DetailAST anAST) {
	return getName(getEnclosingClassDeclaration(anAST));
}
public static String getEnclosingShortTypeName(DetailAST anAST) {
	return getName(getEnclosingTypeDeclaration(anAST));
}
public static String getEnclosingMethodName(DetailAST anAST) {
	return getName(getEnclosingMethodDeclaration(anAST));
}
// not physically but logically enclosing
public static DetailAST getEnclosingTokenType(DetailAST anAST, int aTokenType) {
	if (anAST == null) return null;
	if (anAST.getType() == aTokenType) return anAST;
	DetailAST aParent = anAST.getParent();
	if (aParent != null)
	   return getEnclosingTokenType(aParent, aTokenType);
	return 
			getFirstRightSiblingTokenType(anAST, aTokenType);
}
public static DetailAST getFirstRightSiblingTokenType(DetailAST anAST, int aTokenType) {
	if (anAST == null) return null;
	if (anAST.getType() == aTokenType) return anAST;
	return getFirstRightSiblingTokenType(anAST.getNextSibling(), aTokenType);
	
}
protected void setIntValueOfType(String newVal) {
	String[] aTypeAndValue = newVal.split (">");
	String aType = "*";
	String aValueString = "";
	if (aTypeAndValue.length == 1) {
		aValueString = aTypeAndValue[0];
		
	} else if (aTypeAndValue.length == 2){
		aType = aTypeAndValue[0];
		aValueString = aTypeAndValue[1];			
	}
	try {				
		int aValue = Integer.parseInt(aValueString);
		typeToInt.put(aType, aValue);

	} catch (Exception e) {
		System.out.println ("Did not get int type value");
		e.printStackTrace();
	}	
}
public static boolean isPrimitive(List<String> aTypes) {
	return aTypes.size() == 1 && isPrimitive(aTypes.get(0));
}

public static boolean isShape(String aType) {
	return aType.equals(PointPatternCheck.POINT_PATTERN) ||
			aType.equals(LinePatternCheck.LINE_PATTERN) ||
			aType.equals(OvalPatternCheck.OVAL_PATTERN) ||
			aType.equals(RectanglePatternCheck.RECTANGLE_PATTERN) ||
			aType.equals(StringShapePatternCheck.STRING_PATTERN) ||
			aType.equals(ImagePatternCheck.IMAGE_PATTERN);
}
public static boolean isShape(List<String> aTypes) {
	if (isPrimitive(aTypes)) return false;
	for (String aType:aTypes) {
		if (isShape(aType)) return true;
	}
	return false;
}
public static boolean isPrimitive(String aType) {
	return primitiveTypesSet.contains(aType);
}
public static boolean isOEAtomic(String aType) {
	return aType.equals("String") || isPrimitive(aType);
}

 static {
 	javaLangTypesSet = new HashSet();
 	primitiveTypesSet = new HashSet();
 	for (String aClass:javaLangTypes)
 		javaLangTypesSet.add(aClass);
 	for (String aPrimitive:primitiveTypes)
 		primitiveTypesSet.add(aPrimitive);
 	javaLangTypesSet.addAll(primitiveTypesSet);
// 	for (String aPrimitive:primitiveTypes)
// 		javaLangTypesSet.add(aPrimitive);
 }
}
