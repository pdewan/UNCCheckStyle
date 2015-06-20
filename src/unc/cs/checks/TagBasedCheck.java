package unc.cs.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.AnnotationUtility;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
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
	protected List<STNameable> currentMethodTags;
	static STNameable[] emptyNameableArray = {};
 	static List<STNameable> emptyNameableList =new ArrayList();
	protected DetailAST currentTree;

	@Override
	public int[] getDefaultTokens() {
		return new int[] {						
						TokenTypes.CLASS_DEF,  
						TokenTypes.INTERFACE_DEF, 
						};
	}

	public static String[] javaLangClasses = {
		"Integer",
		"Double",
		"Character",
		"String",
		"Boolean",
};
	 static List<STNameable> emptyList = new ArrayList();

	protected static Set<String> externalImports = new HashSet();
	protected static Set<String> javaLangClassesSet;
	public void setIncludeTags(String[] newVal) {
		this.includeTags = new HashSet(Arrays.asList(newVal));		
	}
	public void setExcludeTags(String[] newVal) {
		this.excludeTags = new HashSet(Arrays.asList(newVal));		
	}
	
	public boolean hasExcludeTags() {
		return excludeTags != null && excludeTags.size() > 1;
	}
	
	public boolean hasIncludeTags() {
		return includeTags != null && includeTags.size() > 1;
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
    		if (anSTNameable.getName().equals(aTag)) return true;
    		
    	}
    	return false;
    }
public boolean checkIncludeTagsOfCurrentType() {
	if (!hasIncludeTags() && !hasExcludeTags())
		return true; // all tags checked in this case
	if (fullTypeName == null) {
		System.err.println("Check called without type name being populated");
		return true;
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
 		if (matchesStoredTag(aNameable.getName(), aTag))

// 		if (aNameable.getName().equals(aTag))
 			return true;	    		
 	}
 	return false;
 }
 
 public static String maybeStripQuotes(String aString) {
 	if (aString.indexOf("\"") != -1) // quote rather than named constant
 		return aString.substring(1, aString.length() -1);
 	return aString;
 }
 public static Boolean matchesStoredTag(String aStoredTag, String aDescriptor) {
 		return maybeStripQuotes(aStoredTag).equals(maybeStripQuotes(aDescriptor));
 	
 }
 public  Boolean matchesMyType( String aDescriptor,  String aShort) {
 	String aClassName = shortTypeName;
 	if (aDescriptor == null || aDescriptor.length() == 0)
 		return true;
 	if (aDescriptor.startsWith("@")) {
 		String aTag = aDescriptor.substring(1);
 		return contains(typeTags(), aTag);	    		
 	}
		return aClassName.equals(aDescriptor);
	 }
 public static boolean isJavaLangClass(String aShortClassName) {
	 return javaLangClassesSet.contains(aShortClassName);
 }
 public static boolean isExternalImport(String aShortClassName) {
	 return externalImports.contains(aShortClassName);
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
public Boolean matchesType(String aDescriptor, String aShortClassName) {
	if (aDescriptor == null || aDescriptor.length() == 0)
		return true;
	if (!aDescriptor.startsWith("@")) {
		return aShortClassName.equals(aDescriptor);
	}
	List<STNameable> aTags = getTags(aShortClassName);
	if (aTags == null)
		return null;
//	List<STNameable> aTags = null;
//	if (shortTypeName == null || // guaranteed to not be a pending check
//			aShortClassName.equals(shortTypeName)) {
//		aTags = typeTags();
//	} else {
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(aShortClassName);
//		if (anSTType == null)
//			return null;
//		aTags = Arrays.asList(anSTType.getTags());
//	}
	String aTag = aDescriptor.substring(1);

	return contains(aTags, aTag);
}
public boolean checkTagsOfCurrentType() {
	if (!hasIncludeTags() && !hasExcludeTags())
		return true; // all tags checked in this case
	if (fullTypeName == null) {
		System.err.println("Check called without type name being populated");
		return true;
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
public void maybeVisitMethodTags(DetailAST ast) {  
	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Tags");		
	if (annotationAST == null) {
		currentMethodTags = emptyArrayList;
		return;
	}
	currentMethodTags = getArrayLiterals(annotationAST);
}
public static boolean isArray(String aShortClassName) {
	 return aShortClassName.endsWith("[]");
}
public void beginTree(DetailAST ast) {  
	 super.beginTree(ast);
	 	
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


 static {
 	javaLangClassesSet = new HashSet();
 	for (String aClass:javaLangClasses)
 		javaLangClassesSet.add(aClass);
 }
}