package unc.cs.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import com.puppycrawl.tools.checkstyle.api.AnnotationUtility;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.cs.symbolTable.AnSTNameable;
import unc.cs.symbolTable.AnSTType;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public abstract class TagBasedCheck extends TypeVisitedCheck{
	public static final String COMMENT_START = "//";
	public static final char TAG_CHAR = '@';
	public static final String TAG_STRING = "" + TAG_CHAR;
	public static final String MATCH_ANYTHING = "*";

	public static final String TYPE_SEPARATOR = "=";
//	public static final String BASIC_SET_MEMBER_SEPARATOR = "|";
	public static final String BASIC_SET_MEMBER_SEPARATOR = "AND";


//	public static final String SET_MEMBER_SEPARATOR = "\\|";
//	public static final String SET_MEMBER_SEPARATOR = "\\" + BASIC_SET_MEMBER_SEPARATOR;
	public static final String SET_MEMBER_SEPARATOR = BASIC_SET_MEMBER_SEPARATOR;

	public static final String AND_SYMBOL = "\\+";


	protected Set<String> excludeTypeTags;
	protected Set<String> includeTypeTags;
	protected Set<String> excludeMethodTags;
	protected Set<String> includeMethodTags;
	protected List<List<String>> includeSets = new ArrayList();
	protected List<List<String>> excludeSets = new ArrayList();
	protected boolean typeTagsInitialized;
	protected List<STNameable> typeTags;
	protected List<STNameable> computedTypeTags;
	protected STNameable pattern;
	protected List<STNameable> currentMethodTags = emptyList;
	protected List<STNameable> currentVariableTags = emptyList;

	protected List<STNameable> currentMethodComputedTags;
	static STNameable[] emptyNameableArray = {};
 	static List<STNameable> emptyNameableList =new ArrayList();
	protected DetailAST currentTree;
	protected Map<String, Integer> typeToInt = new Hashtable<>();
	protected Map<String, String> specificationVariablesToUnifiedValues = new Hashtable<>();
	protected STNameable structurePattern;
	public static final DetailAST noAST = new DetailAST();


	
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

	/*
	 * T1 | @T2 | T3,  T3 | T4  *
	 */
	public void addIncludeSet(String newVal) {
		String[] aTypes = newVal.split(SET_MEMBER_SEPARATOR);
		for (int i = 0; i < aTypes.length; i++) {
			aTypes[i] = aTypes[i].trim();
		}
		includeSets.add(Arrays.asList(aTypes));		
	}
	public void addExcludeSet(String newVal) {
		String[] aTypes = newVal.split(SET_MEMBER_SEPARATOR);
		for (int i = 0; i < aTypes.length; i++) {
			aTypes[i] = aTypes[i].trim();
		}
		excludeSets.add(Arrays.asList(aTypes));		
	}

	public void setExcludeSets(String[] newVal) {
		for (String aString:newVal)	 {
			addExcludeSet(aString);
		}
	}
	public void setIncludeSets(String[] newVal) {
		for (String aString:newVal)	 {
			addIncludeSet(aString);
		}
	}
	public void setIncludeTypeTags(String[] newVal) {
		this.includeTypeTags = new HashSet(Arrays.asList(newVal));		
	}
	public void setExcludeTypeTags(String[] newVal) {
		this.excludeTypeTags = new HashSet(Arrays.asList(newVal));		
	}
	public void setIncludeMethodTags(String[] newVal) {
		this.includeMethodTags = new HashSet(Arrays.asList(newVal));		
	}
	public void setExcludeMethodTags(String[] newVal) {
		this.excludeMethodTags = new HashSet(Arrays.asList(newVal));		
	}
	
	public boolean hasExcludeTypeTags() {
		return excludeTypeTags != null && excludeTypeTags.size() > 0;
	}
	
	public boolean hasIncludeTypeTags() {
		return includeTypeTags != null && includeTypeTags.size() > 0;
	}
	public boolean hasExcludeMethodTags() {
		return excludeMethodTags != null && excludeMethodTags.size() > 0;
	}
	
	public boolean hasIncludeMethodTags() {
		return includeMethodTags != null && includeMethodTags.size() > 0;
	}
	
	public static  boolean matchesAllAndedSpecificationTag (Collection<STNameable> aStoredTags, String anAndedSpecification) {
		String[] aSpecifications = anAndedSpecification.split(AND_SYMBOL);
	for (String aSpecification:aSpecifications) {
		if (! matchesSomeStoredTag(aStoredTags, aSpecification))
			return false;
	}
	return true;
   }
	public  boolean matchesSomeSpecificationTags (Collection<STNameable> aStoredTags, Collection<String> aSpecifications) {
		for (String aSpecificationTag:aSpecifications) {
			if (aSpecificationTag.equals(MATCH_ANYTHING) || matchesAllAndedSpecificationTag(aStoredTags, aSpecificationTag))
				return true;
		}
		return false;
	   }
	
	public static  Boolean matchesSomeStoredTag (Collection<STNameable> aStoredTags, String aDescriptor) {
		int i = 0;
		for (STNameable aStoredTag:aStoredTags) {
			if (aStoredTag == null) {
//				System.err.println("Null stored tag!");
//				return null;
				continue; // an interface does this
			}
			if (matchesStoredTag(aStoredTag.getName(), aDescriptor)) {
				return true;
			}
		}
		return false;
	   }
	
	
//	public static boolean containsAndedTags (Collection<String> aSpecificationTags, String aStoredTag) {
//		String[] anAndedTags = aStoredTag.split("&");
//		for (String anAndedTag:anAndedTags) {
//			if (!containsTag(aSpecificationTags, anAndedTag))
//				return false;
//		}
//		return true;
//	}
//	public static Boolean matchesAndedTags(String aDescriptor, String aStoredTag) {
//		String[] anAndedTags = aStoredTag.split("&");
//		for (String anAndedTag:anAndedTags) {
//			if (!matchesStoredTag(aStoredTag, anAndedTag))
//				return false;
//		}
//		return true;	
//    }
//	public static boolean containsAndedTags (Collection<String> aStoredTags, STNameab aSpecificationTag) {
//		String[] anAndedTags = aSpecificationTag.split("&");
//		for (String anAndedTag:anAndedTags) {
//			if (!containsSpecificTag(aStoredTags, anAndedTag))
//				return false;
//		}
//		return true;
//	}
	
	public static boolean containsTag (Collection<String> aSpecificationTags, String aStoredTag) {
		for (String aSpecificationTag:aSpecificationTags) {
			if (matchesStoredTag(aStoredTag, aSpecificationTag))
				return true;		
		}
		return false;
	}
	
//	public boolean checkTagOfCurrentType(String aTag) {
//		if (hasIncludeTags()) {
//			return contains(includeTags, aTag);
//		} else { // we know it has exclude tags
//			return !contains(excludeTags, aTag);
//		}
//	}
	/*
	 * do not have to check hasInclude and hasExcludeTags now
	 */
//	public boolean checkTagOfCurrentType(String aStoredTag) {
//		Boolean retVal = true;
//		if (hasIncludeTags()) {
//			 retVal  = containsTag(includeTags, aStoredTag);
//		}
//		if (retVal && hasExcludeTags()) { 
//			return !containsTag(excludeTags, aStoredTag);
//		}
//		return retVal;
//	}
//	public boolean checkIncludeTagOfCurrentType(String aTag) {
//			return includeTags.contains(aTag);
//		
//	}
//	public boolean checkExcludeTagOfCurrentType(String aTag) {
//		return !excludeTags.contains(aTag);
//	
//   } 
	/*
	 * looks like this is has some stored tag, should use anded stuff here also
	 */
	public static  Boolean hasTag(STNameable[] aStoredTags, String aDescriptor) {
		return matchesSomeStoredTag(Arrays.asList(aStoredTags), aDescriptor);
//    	for (STNameable anSTNameable:aTags) {
//    		if (matchesStoredTag(anSTNameable.getName(), aTag)) return true;
//    		
//    	}
//    	return false;
    }
	
	protected int getInt(String aType) {
		Integer retVal = typeToInt.get(aType);
		if (retVal == null) return typeToInt.get(MATCH_ANYTHING); // should not be exercised
		return retVal;
	}
//public boolean checkIncludeTagsOfCurrentType() {
//	if (!hasIncludeTags() && !hasExcludeTags())
//		return true; // all tags checked in this case
//	if (fullTypeName == null) {
////		System.err.println("Check called without type name being populated");
//		// could be the wrong type, class or interface, so we do nto have to check
//		return false;
//	}
//	STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
//	STNameable[] aCurrentTags = anSTType.getComputedTags();
//	if (hasIncludeTags())
//		return checkIncludeTagsOfCurrentType(aCurrentTags);
//	else
//		return checkExcludeTagsOfCurrentType(aCurrentTags);		
//}
// if anyone says exclude, exclude
//public boolean checkExcludeTagsOfCurrentType(STNameable[] aCurrentTags) {
//	
//	for (STNameable aCurrentTag:aCurrentTags) {
//		if (checkExcludeTagOfCurrentType(aCurrentTag.getName()))
//				return true;
//	}
//	return false;
//	
//}
// if anyone says include, include
// public boolean checkIncludeTagsOfCurrentType(STNameable[] aCurrentTags) {
//	
//	for (STNameable aCurrentTag:aCurrentTags) {
//		if (checkIncludeTagOfCurrentType(aCurrentTag.getName()))
//				return true;
//	}
//	return false;
//	
//}
 public STNameable getPattern(String aShortClassName)  {
// 	List<STNameable> aTags = emptyList;

 	// these classes have no tags
// 	if ( aShortClassName.endsWith("[]") ||
// 			allKnownImports.contains(aShortClassName) || 
// 			javaLangClassesSet.contains(aShortClassName) ) {
// 		return emptyList;
// 	}
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
 		return anSTType.getStructurePatternName();
 	}
 	
 }
 public  boolean containsEfficient(List<STNameable> aTags, String aTag, String aTypeName) {
	 return matchesAllAndedSpecificationTag(aTags, aTag) ||
			 matchesPatternEfficient(aTag, aTypeName); // should not need this
// 	for (STNameable aNameable:aTags) {
// 		if (matchesStoredTag(aNameable.getName(), aTag)) {
// 			matchedTypeOrTagAST = aNameable.getAST();
//// 		if (aNameable.getName().equals(aTag))
// 			return true;
// 		}
// 	}
// 	return matchesPattern(aTag, aTypeName);
 }
 
 public  boolean matchesPatternEfficient (String aPatternName, String aTypeName) {
	 STNameable aStructurePattern = getPattern(aTypeName);
		if (aStructurePattern == null)
			return false;
		return aStructurePattern.getName().equals(aPatternName) || 
				aStructurePattern.getName().equals("StructurePatternNames." + aPatternName);
 }
 
 // could return a list also
 public  String findMatchingType (Collection<String> aTypesToBeMatched, STType anSTType) {
		for (String aSpecifiedType:aTypesToBeMatched) {
			matchedTypeOrTagAST = anSTType.getAST();
//				Boolean matches = matchesTypeUnifying(aSpecifiedType, anSTType.getName());
				Boolean matches = matchesAllAndedSpecificationTag(Arrays.asList(anSTType.getComputedTags()), aSpecifiedType);

				if (matches == null) {
					return null;
				}
				if (matches)
//				if (matchesType(aSpecifiedType, anSTType.getName()))
					return aSpecifiedType; 
		}
		return null;
	}
 
 
 public static String maybeStripQuotes(String aString) {
 	if (aString.indexOf("\"") != -1) // quote rather than named constant
 		return aString.substring(1, aString.length() -1);
 	return aString;
 }
 public static String maybeStripComment(String aString) {	 
	 	int aCommentStart = aString.indexOf(COMMENT_START);
	 	if (aCommentStart < 0)
	 		return aString.trim();
	 	return aString.substring(0, aCommentStart).trim();
	 }
 public static String maybeStripAt(String aString) {
	 if (aString.startsWith(TAG_STRING)) {
			return aString.substring(1);
	 }
	 return aString;
 }
 public static Boolean matchesStoredTag(String aStoredTag, String aDescriptor) {
 		return 
 				aDescriptor.equals(MATCH_ANYTHING) || 
 				maybeStripAt( // some may add @ to the tag, remove at  the storage point
 						maybeStripQuotes(
 								aStoredTag)).matches(
 											maybeStripAt(
 													maybeStripQuotes(
 															maybeStripComment(
 																	aDescriptor))));
// 						maybeStripQuotes(aStoredTag).matches(
// 									maybeStripAt(
// 											maybeStripQuotes(aDescriptor)));
 	
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
 public List<STNameable> lookupTags(String aShortClassName)  {
	 STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aShortClassName);
		if (anSTType == null) {
			if (isExternalImport(aShortClassName)) // check last as we are not really sure about external
				return emptyList;			
			return null;
		}
		return Arrays.asList(anSTType.getComputedTags());
 }
 
 public List<STNameable> lookupTagsOfCurrentTree()  {
	STType anSTType = getSTType(currentTree);
	if (anSTType == null) {
		return computedTypeTags(); // STBuilder
	} else {
		return Arrays.asList(anSTType.getComputedTags());
	}
			
	
 }
 
 public List<STNameable> getTags(String aShortClassName)  {
	List<STNameable> aTags = emptyList;


	if ( isArray(aShortClassName) ||
			isJavaLangClass(aShortClassName) ) {
		return emptyList;
	}
	aTags = lookupTags(aShortClassName);
	if (aTags == null && (
//			shortTypeName == null ||
			aShortClassName.equals(shortTypeName))) {
		aTags = computedTypeTags();
	}
	/*
	 * why shortcircuit current one, there was a reason, I forget now
	 */
//	if (shortTypeName == null || // guaranteed to not be a pending check
//			aShortClassName.equals(shortTypeName)) {
////		aTags = typeTags();
//		aTags = computedTypeTags();
//	} else {
//		aTags = lookupTags(aShortClassName);
//////		System.out.println ("Checking symbol table");
////		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
////				.getSTClassByShortName(aShortClassName);
////		if (anSTType == null) {
////			if (isExternalImport(aShortClassName)) // check last as we are not really sure about external
////				return emptyList;			
////			return null;
////		}
////		aTags = Arrays.asList(anSTType.getComputedTags());
//////		aTags = Arrays.asList(anSTType.getAllComputedTags());
//
//	}
	return aTags;
	
}
 public Boolean unifyingMatchesNameVariableOrTag(String aDescriptor, String aName, STNameable[] aTags) {
	 if (aDescriptor == null) {
		 return true;
	 }
	 aDescriptor = aDescriptor.trim();
	 if (aDescriptor.equals(MATCH_ANYTHING)) {
	 	 return true;
	 } else if (aDescriptor.startsWith("$")) {
			String aUnifiedValue = specificationVariablesToUnifiedValues
					.get(aDescriptor);
			if (aUnifiedValue == null) {
				specificationVariablesToUnifiedValues.put(aDescriptor,
						aName);
				variablesAdded.add(aDescriptor);
				return true;
			} else {
				return aName.equals(aUnifiedValue);
			}
		} else if (aDescriptor.startsWith(TAG_STRING)) {
			return hasTag(aTags, aDescriptor);
		}	else {
			String aShortName = toShortTypeName(aName);
//			return aName.matches(aDescriptor) || aName.contains(aDescriptor); // allow regex
			// do not want user scanner to match Scanner class so do not use contains
			// allow regex
			try {
			return aName.matches(aDescriptor) || aShortName.matches(aDescriptor);
			} catch (Exception e) {
				e.printStackTrace();
				return true;
			}

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
//		String aClassName = shortTypeName;
		if (aDescriptor == null || aDescriptor.length() == 0 || aDescriptor.equals(MATCH_ANYTHING))
			return true;
		if (aDescriptor.startsWith(TAG_STRING)) {
			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
			if (anSTType == null) {
				return false;
			}
//			STNameable[] checkTags = anSTType.getAllComputedTags();
			STNameable[] checkTags = anSTType.getComputedTags();

//			String aTag = aDescriptor.substring(1);
//			return contains(typeTags(), aDescriptor, shortTypeName);

			return containsEfficient(Arrays.asList(checkTags), aDescriptor, shortTypeName);
		} else {
			return unifyingMatchesNameVariableOrTag(aDescriptor, shortTypeName, null) ||  unifyingMatchesNameVariableOrTag(aDescriptor, fullTypeName, null);
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
 
public Boolean matchesType(Collection<String> aDescriptors, String aShortClassName) {
	for (String aDescriptor:aDescriptors) {
		Boolean match = matchesTypeUnifying(aDescriptor, aShortClassName);
		if (match == null)
			return null;
		
//		if (matchesType(aDescriptor, aShortClassName))
		if (match)

			return true;
	}
	return false;
}
public  Set<String> setOf(String aType, List<List<String>> aLists) {
	Set<String> result = new HashSet();
	for (List<String> aSet:aLists) {
		Boolean match = matchesType(aSet, aType);
		if (match == null) {
			return null;
		}
//		if (matchesType(aSet, aType)) {
		if (match) {

			result.addAll(aSet);
		}
	}
	return result;
}
public  Set<String> includeSetOf(String aType) {
	return setOf(aType, includeSets);
}
public  Set<String> excludeSetOf(String aType) {
	return setOf(aType, includeSets);
}
protected List<String> filterTypes(List<String> aTypes, String aTypeName) {
	if (aTypes == null)
		return null;
	List<String> result = aTypes;
	if (includeSets.size() > 0) {
		result = filterTypesByIncludeSets(result, aTypeName);
	}
	if (excludeSets.size() > 0) {
		// this should be result
//		aTypes = filterTypesByExcludeSets(result, aTypeName);
		result = filterTypesByExcludeSets(result, aTypeName);

	} 
	return result;
}
protected List<String> filterTypesByIncludeSets(List<String> aTypes, String aTypeName) {
	if (aTypes == null)
		return null;
	Set<String> anIncludeSet = includeSetOf(aTypeName);
	if (anIncludeSet == null)
		return null;
	List<String> result = new ArrayList();
	for (String aType:aTypes) {
		Boolean matches = matchesType(anIncludeSet, aType);
//		if (matchesType(anIncludeSet, aType))
		if (matches == null)
			return null;
		if (matches)
			result.add(aType);
	}
	return result;
		
}
protected List<String> filterTypesByExcludeSets(List<String> aTypes, String aTypeName) {
	if (aTypes == null)
		return null;
	Set<String> anExcludeSet = excludeSetOf(aTypeName);
	if (anExcludeSet == null)
		return null;
	
	List<String> result = new ArrayList();
	for (String aType:aTypes) {
		Boolean match = matchesType(anExcludeSet, aType);
//		if (!matchesType(anExcludeSet, aType))
		if (!match)

			result.add(aType);
	}
	return result;
		
}
public Boolean matchesTypeUnifying(String aDescriptor, String aShortClassName) {
	if (aDescriptor == null || aDescriptor.length() == 0 || aDescriptor.equals(MATCH_ANYTHING ))
		return true;
//	int i = 0;
	if (aShortClassName.contains("]") || // array element
			aShortClassName.contains("[") ||
			aShortClassName.contains("(") ||// casts
			aShortClassName.contains(")"))
//		return false;
		return true; // assume the type is right, 
	aDescriptor = aDescriptor.trim();
//	if (aDescriptor.equals(MATCH_ANYTHING))
//		return true;
	if (!aDescriptor.startsWith(TAG_STRING)) {
//		return aShortClassName.equals(aDescriptor);
		try {
		return unifyingMatchesNameVariableOrTag(aDescriptor, aShortClassName, null);
		} catch (PatternSyntaxException e) {
			System.out.println("Pattern mismatch Descriptor: " + aDescriptor + "aShortClassName "  + aShortClassName);
			e.printStackTrace();
			return false;
		}
	}
//	int i = 0;
	String aTag = aDescriptor.substring(1);
	if (aShortClassName.matches(aTag) || aShortClassName.matches("A" + aTag))
		return true; // in case the class name is the same as tag or is ATag

	List<STNameable> aTags = getTags(aShortClassName);
	if (aTags == null)
		return null;
		// this should be changed back to null at some point
//		return false;

//	String aTag = aDescriptor.substring(1);

	return containsEfficient(aTags, aTag, aShortClassName);
}
public static Boolean matchesType(String aDescriptor, String aShortClassName) {
	if (aDescriptor == null || aDescriptor.length() == 0 || aDescriptor.equals(MATCH_ANYTHING ))
		return true;
	if (
//			aShortClassName.contains("]") || // array element
//			aShortClassName.contains("[") ||
			aShortClassName.contains("(") ||// casts
			aShortClassName.contains(")"))
//		return false;
		return true; // assume the type is right, 
	aDescriptor = aDescriptor.trim();
//	if (aDescriptor.equals(MATCH_ANYTHING))
//		return true;
	if (!aDescriptor.startsWith(TAG_STRING)) {
//		return aShortClassName.equals(aDescriptor);
		try {
		return aShortClassName.matches(aDescriptor);
		} catch (PatternSyntaxException e) {
			System.out.println("Pattern mismatch Descriptor: " + aDescriptor + "aShortClassName "  + aShortClassName);
			e.printStackTrace();
			return false;
		}
	}
	String aTag = aDescriptor.substring(1);
	if (aShortClassName.matches(aTag) || aShortClassName.matches("A" + aTag))
		return true; // in case the class name is the same as tag or is ATag
	STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aShortClassName);
	if (anSTType == null)
		return null;
	List<STNameable> aTags = Arrays.asList(anSTType.getComputedTags());
	if (aTags == null)
		return null;
		// this should be changed back to null at some point
//		return false;

//	String aTag = aDescriptor.substring(1);

	return matchesAllAndedSpecificationTag(aTags, aTag);
}

protected boolean inferTag() {
	return false;
}

protected Boolean checkIncludeExcludeTagsOfCurrentType() {
	if (inferTag())
		return true;
	if (!hasIncludeTypeTags() && !hasExcludeTypeTags())
		return true; // all tags checked in this case
//		return false; // no tags checked in this case
//	
//	if (fullTypeName == null) {
////		System.err.println("Check called without type name being populated");
//		return false;
//	}
	
	return checkIncludeTagsOfCurrentType()	&& checkExcludeTagsOfCurrentType();
//	List<STNameable> aStoredTags = computedTypeTags();
//	for (STNameable aStoredTag:aStoredTags) {
//		if (checkTagOfCurrentType(aStoredTag.getName()))
//				return true;
//	}
//	return false;
	
}
public Boolean checkIncludeExcludeTagsOfCurrentMethod() {
	return checkIncludeExcludeTagsOfMethod(currentMethodComputedTags);
	
}

public Boolean checkIncludeExcludeTagsOfMethod(List<STNameable> aCurrentMethodComputedTags) {
	if (!hasIncludeMethodTags() && !hasExcludeMethodTags())
		return true; // all tags checked in this case
//		return false; // no tags checked in this case
	
	if (aCurrentMethodComputedTags == null) {
//		System.err.println("Check called without type name being populated");
		return false;
	}
	
	return checkIncludeTagsOfMethod(aCurrentMethodComputedTags)	&& checkExcludeTagsOfMethod(aCurrentMethodComputedTags);
//	List<STNameable> aStoredTags = computedTypeTags();
//	for (STNameable aStoredTag:aStoredTags) {
//		if (checkTagOfCurrentType(aStoredTag.getName()))
//				return true;
//	}
//	return false;
	
}
public boolean checkIncludeTagsOfCurrentType() {
	if (!hasIncludeTypeTags())
		return false;
//	return checkTags(includeTags, computedTypeTags());
//	return matchesSomeSpecificationTags(computedTypeTags(), includeTypeTags);
	return matchesSomeSpecificationTags(lookupTagsOfCurrentTree(), includeTypeTags);

	
}
/*
 * return true if type is not to be excluded, that is, checked
 */
public boolean checkExcludeTagsOfCurrentType() {
	if (!hasExcludeTypeTags())
		return true;
//	return !checkTags(excludeTags, computedTypeTags());
	return !matchesSomeSpecificationTags(lookupTagsOfCurrentTree(), excludeTypeTags);	
}

/*
 * return true if method is not to be excluded, that is, checked
 */
public boolean checkExcludeTagsOfMethod(List<STNameable> aCurrentMethodComputedTags) {
	if (!hasExcludeMethodTags())
		return true;
//	return !checkTags(excludeTags, computedTypeTags());
	return !matchesSomeSpecificationTags(aCurrentMethodComputedTags, excludeMethodTags);	
}

public boolean checkIncludeTagsOfMethod(List<STNameable> aCurrentMethodComputedTags) {
	if (!hasIncludeMethodTags())
		return true; // include all
//	return checkTags(includeTags, computedTypeTags());
	return matchesSomeSpecificationTags(aCurrentMethodComputedTags, includeMethodTags);	
}


//public boolean checkTags(Collection<String> aSpecifications, Collection<STNameable> aStoredTags ) {
//	for (STNameable aStoredTag:aStoredTags) {
//		if (checkTagOfCurrentType(aStoredTag.getName()))
//				return true;
//	}
//	return false;
//}
//public boolean checkTags(String aDescription, Collection<STNameable> aStoredTags ) {
//	for (STNameable aStoredTag:aStoredTags) {
//		if (checkTagOfCurrentType(aStoredTag.getName()))
//				return true;
//	}
//	return false;
//}
protected List<STNameable> typeTags( ) {
	if (!typeTagsInitialized) {
		DetailAST aTypeAST = getEnclosingTypeDeclaration(currentTree);
		maybeVisitTypeTags(aTypeAST);
	}
	return typeTags;
}
public static STNameable toShortPatternName(STNameable aLongName) {
	if (aLongName ==null) {
//		System.out.println("Null a long nane:" + aLongName);
		return null;
	}
	String aShortName = TypeVisitedCheck.toShortTypeName(aLongName.getName());

	return  new AnSTNameable(aLongName.getAST(), aShortName);
}

protected List<STNameable> computedTypeTags() {
	typeTags();
	return computedTypeTags;
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
	if (typeTagsInitialized) return;
	typeTagsInitialized = true;
	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Tags");		
	if (annotationAST == null) {
		typeTags = emptyArrayList;		
//		return;
	} else {
	typeTags = getArrayLiterals(annotationAST);
	}
	computedTypeTags = new ArrayList(typeTags);
	if (typeNameable == null) {
		String aName = getName(ast);
		typeNameable = new AnSTNameable(aName);
	}
		
	computedTypeTags.add(typeNameable);
	computedTypeTags.add(toShortPatternName(typeNameable));
	if (structurePattern != null) {
		computedTypeTags.add(structurePattern);
		computedTypeTags.add(toShortPatternName(structurePattern));
	}
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
static protected List<STNameable> emptyNameables = new ArrayList();
public static List<STNameable> getExplicitTags(DetailAST ast) {
	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Tags");	
	if (annotationAST == null) {
		emptyNameableList.clear(); // it may have been changed by compuyted and derived tags
		return emptyNameableList;
	}
	return getArrayLiterals(annotationAST); // allocating new list and reusing old list in 
}
public void maybeVisitMethodTags(DetailAST ast) {  
	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Tags");		
	if (annotationAST == null) {
		currentMethodTags.clear();;
		return;
	}
	currentMethodTags = getArrayLiterals(annotationAST);
}

public void visitImport(DetailAST ast) {
	 FullIdent anImport = FullIdent.createFullIdentBelow(ast);
	 String aLongClassName = anImport.getText();
	 String aShortClassName = findLastDescendentOfFirstChild(ast).getText();

	 STNameable anSTNameable = new AnSTNameable(ast, aLongClassName);
	 imports.add(anSTNameable);
	 if (!isProjectImport(aLongClassName))
		 externalImports.add(aShortClassName);
}
public static boolean isProjectImport(String aFullName) {
	 for (String aPrefix:STBuilderCheck.getProjectPackagePrefixes())
		 if (aPrefix.equals("*") || aFullName.startsWith(aPrefix)) return true;
	 return false;
}
static List<DetailAST> emptyASTList = new ArrayList();
public static DetailAST getFirstInOrderMatchingNode(DetailAST ast, List<Integer> aType  ) {
	return findFirstInOrderUnmatchedMatchingNode(ast, aType, emptyASTList);
//	if (ast.getType() == aType)
//		return ast;
////	if (ast.getChildCount() > 0) return null;
//	DetailAST aResult = null;
//	DetailAST aChild = ast.getFirstChild();
//	while (true) {
//		if (aChild == null)
//			return null;
//		aResult = getFirstInOrderMatchingNode(aChild, aType);
//		if (aResult != null)
//			return aResult;
//		aChild = aChild.getNextSibling();		
//	}
}
public static DetailAST findFirstContainingNode(DetailAST ast, List<Integer> aTypes) {
//	if (aast.getType() == aType &&
	if (ast == null) {
		return null;
	}
	if (aTypes.contains(ast.getType())) {
		return ast;
	}
//	if (ast.getChildCount() > 0) return null;
//	DetailAST aResult = null;
	return findFirstContainingNode(ast.getParent(), aTypes);
}
// look up one level in case after that there is nothing
// if there is then return the next one
public static DetailAST findLastContainingNode(DetailAST ast, List<Integer> aTypes) {
	DetailAST nextLevel = findFirstContainingNode(ast, aTypes);
	if (nextLevel == null)
		return null;
	DetailAST lastLevel = findLastContainingNode(nextLevel.getParent(), aTypes);
	if (lastLevel == null)
		return nextLevel;
	return lastLevel;
}
	

public static DetailAST findFirstInOrderUnmatchedMatchingNode(DetailAST ast, List<Integer> aTypes, List<DetailAST> aMatchedNodes  ) {
//	if (aast.getType() == aType &&
	if (aTypes.contains(ast.getType()) &&

			!aMatchedNodes.contains(ast)) {
		aMatchedNodes.add(ast);
		return ast;
	}
//	if (ast.getChildCount() > 0) return null;
	DetailAST aResult = null;
	DetailAST aChild = ast.getFirstChild();
	while (true) {
		if (aChild == null)
			return null;
		aResult = findFirstInOrderUnmatchedMatchingNode(aChild, aTypes, aMatchedNodes);
		if (aResult != null)
			return aResult;
//		if (aResult != noAST && 
//				!aMatchedNodes.contains(aResult)) {
//			aMatchedNodes.add(aResult);
//			return aResult;
//		}
		aChild = aChild.getNextSibling();		
	}
}
public static DetailAST findFirstInOrderMatchingNode(DetailAST ast, List<Integer> aTypes ) {
	return findFirstInOrderUnmatchedMatchingNode(ast, aTypes, new ArrayList());
}
public static DetailAST findFirstInOrderMatchingNode(DetailAST ast, int aType ) {
	return findFirstInOrderMatchingNode(ast, Arrays.asList(new Integer[] {aType}));
}

public static DetailAST findFirstInOrderUnmatchedNode(DetailAST ast, List<Integer> aTypes, List<DetailAST> aMatchedNodes  ) {
//	if (aast.getType() == aType &&
	if (aTypes.contains(ast.getType()) &&

			!aMatchedNodes.contains(ast)) {
		aMatchedNodes.add(ast);
		return ast;
	}
//	if (ast.getChildCount() > 0) return null;
	DetailAST aResult = null;
	DetailAST aChild = ast.getFirstChild();
	while (true) {
		if (aChild == null)
			return null;
		aResult = findFirstInOrderUnmatchedMatchingNode(aChild, aTypes, aMatchedNodes);
		if (aResult != null)
			return aResult;
//		if (aResult != noAST && 
//				!aMatchedNodes.contains(aResult)) {
//			aMatchedNodes.add(aResult);
//			return aResult;
//		}
		aChild = aChild.getNextSibling();		
	}
}
public static DetailAST findFirstInOrderMatchingNodeAfter(DetailAST ast, List<Integer> aType  ) {
	// first see if someone below our sibling succeeds
	DetailAST aNextSibiling = ast.getNextSibling();
	if (aNextSibiling != null) {
		DetailAST result = getFirstInOrderMatchingNode(aNextSibiling, aType);
		if (result != null) {
			return result;
		}
	}
	// get the first node after our parent, who has not not been visited
	DetailAST aParent = ast.getParent();
	if (aParent == null) {
		return noAST;
	}
	return findFirstInOrderMatchingNodeAfter(aParent, aType);
}
public static List<DetailAST>  findAllInOrderMatchingNodes(DetailAST ast, int aType ) {
	 List<DetailAST> result = new ArrayList();
	fillAllInOrderMatchingNodes(ast, aType, result);
	return result;

}
public static void fillAllInOrderMatchingNodes(DetailAST ast, int aType, List<DetailAST>  aList ) {
	if (ast.getType() == aType)
		aList.add(ast);
	DetailAST aChild = ast.getFirstChild();
	while (true) {
		if (aChild == null)
			return;
		fillAllInOrderMatchingNodes(aChild, aType, aList);		
		aChild = aChild.getNextSibling();		
	}
}
public static DetailAST findLastDescendentOfFirstChild(DetailAST ast) {
	return findLastDescendent(ast.getFirstChild());
//	DetailAST result = ast.getFirstChild();
//	while (result.getChildCount() > 0)
//		result = result.getLastChild();    	
//	return result;    	
}
public static DetailAST findLastDescendent(DetailAST ast) {
	DetailAST result = ast;
	while (result != null && result.getChildCount() > 0)
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
	 	computedTypeTags = emptyNameableList;
//	 	typeScope.clear();
	 	// not sure why we need this as 
	 	fullTypeName = null;
//	 	isInterface = false;
//	 	isGeneric = false;
//	 	isElaboration = false;
////	 	stMethods.clear();
//	 	imports.clear();
//	 	globalVariables.clear();
	 	currentTree = ast;
	 	typeTagsInitialized = false;
//	 	maybeCleanUpPendingChecks(ast);
//		pendingChecks().clear();
   }
public static DetailAST getExpression (DetailAST anIfAST) {
	return anIfAST.getFirstChild().getNextSibling();
}
public static DetailAST getThenPart (DetailAST anIfAST) {
	return getExpression(anIfAST).getNextSibling().getNextSibling();
}
public static DetailAST getElsePart (DetailAST anIfAST) {
	DetailAST aThenPart = getThenPart(anIfAST);
	DetailAST aThenSibling = aThenPart.getNextSibling();
	if (aThenSibling == null || aThenSibling.getType() != TokenTypes.LITERAL_ELSE) {
		return null;
	}
	return aThenPart.getNextSibling().getFirstChild();
}
public static DetailAST getEnclosingMethodDeclaration(DetailAST anAST) {
	return getEnclosingOrRightTokenType(anAST, TokenTypes.METHOD_DEF);
}



public static DetailAST getEnclosingClassDeclaration(DetailAST anAST) {
	return getEnclosingOrRightTokenType(anAST, TokenTypes.CLASS_DEF);
}
public static DetailAST getEnclosingPackageDeclaration(DetailAST anAST) {
	return getEnclosingOrLeftTokenType(anAST, TokenTypes.PACKAGE_DEF);
}
public static DetailAST getEnclosingInterfaceDeclaration(DetailAST anAST) {
	return getEnclosingOrRightTokenType(anAST, TokenTypes.INTERFACE_DEF);
}
public static DetailAST getEnclosingTreeDeclaration(DetailAST anAST) {
	DetailAST root = anAST;
	while (true) {		
		DetailAST aParent = root.getParent();
		if (aParent == null)
			break;
		root = aParent;
	}
	while (true) {
		DetailAST aLeftSibling = root.getPreviousSibling();
		if (aLeftSibling == null)
			break;
		root = aLeftSibling;
	}
	return root;
}
public static DetailAST getEnclosingEnumDeclaration(DetailAST anAST) {
	DetailAST root = anAST;
	while (true) {
		if (root.getType() == TokenTypes.ENUM)
			return anAST;
		DetailAST aParent = root.getParent();
		if (aParent == null)
			break;
		root = aParent;
	}
//	DetailAST result = root.findFirstToken(TokenTypes.ENUM);
	DetailAST anEnumDef = root;
	while (true)  {
		if (anEnumDef.getType() == TokenTypes.ENUM_DEF) 
			break;
		anEnumDef = anEnumDef.getNextSibling();
	}
	return anEnumDef;
//	return anEnumDef.getFirstChild().getNextSibling();
//	return getEnumNameAST(anEnumAST);
}
public static String getFullTypeName(DetailAST aTree) {
	String aTypeName = getName(getEnclosingTypeDeclaration(aTree));
	int i = 1;
	DetailAST aPackageAST = getEnclosingPackageDeclaration(aTree);
	String aPackageName = DEFAULT_PACKAGE;
	if (aPackageAST != null)
		 aPackageName = getPackageName(aPackageAST);
	return aPackageName + "." + aTypeName;
}
public static STType getSTType(DetailAST aTreeAST) {
	int i = 1;
	String aFullName = getFullTypeName(aTreeAST);
//	STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aFullName);
//	if (anSTType == null) {
//		System.out.println("Null symbol table entry for:" + aFullName);
//	}
//	return anSTType;
	STType result = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aFullName);
	if (result == null && STBuilderCheck.getSingleton().getVisitInnerClasses()) {
		result = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(toShortTypeName(aFullName));
		
	}
	return result;
	
}
public static DetailAST getEnclosingTypeDeclaration(DetailAST anAST) {
//	DetailAST result = null;
	if (anAST.getType() == TokenTypes.INTERFACE_DEF) {
		return getEnclosingInterfaceDeclaration(anAST);
	}
	if (anAST.getType() == TokenTypes.ENUM_DEF) {
		return getEnclosingEnumDeclaration(anAST);
	}
	if (anAST.getType() == TokenTypes.CLASS_DEF) {
		return getEnclosingClassDeclaration(anAST);
	}
	DetailAST result =		getEnclosingClassDeclaration(anAST);
	if (result != null)
		return result;
	result = getEnclosingInterfaceDeclaration(anAST);
	if (result != null)
		return result;
	result = getEnclosingEnumDeclaration(anAST);
	if (result != null)
		return result;
	System.err.println(" could not find a type declaration for:" + anAST);
	return result;
	
//		return aClassDef;
//	if (aClassDef == null)
//		return getEnclosingInterfaceDeclaration(anAST);
//	else
//		return aClassDef;
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
public static DetailAST getEnclosingOrRightTokenType(DetailAST anAST, int aTokenType) {
	if (anAST == null) return null;
	if (anAST.getType() == aTokenType) return anAST;
	DetailAST aParent = anAST.getParent();
	if (aParent != null)
	   return getEnclosingOrRightTokenType(aParent, aTokenType);
	return 
			getFirstRightSiblingTokenType(anAST, aTokenType);
//			getFirstLeftSiblingTokenType(anAST, aTokenType);

}
public static DetailAST getEnclosingOrLeftTokenType(DetailAST anAST, int aTokenType) {
	if (anAST == null) return null;
	if (anAST.getType() == aTokenType) return anAST;
	DetailAST aParent = anAST.getParent();
	if (aParent != null)
	   return getEnclosingOrRightTokenType(aParent, aTokenType);
	return 
			getFirstLeftSiblingTokenType(anAST, aTokenType);
//			getFirstLeftSiblingTokenType(anAST, aTokenType);

}
//public static DetailAST getFirstRightSiblingTokenType(DetailAST anAST, int aTokenType) {
//	if (anAST == null) return null;
//	if (anAST.getType() == aTokenType) return anAST;
//	return getFirstRightSiblingTokenType(anAST.getNextSibling(), aTokenType);
//	
//}
public static DetailAST getFirstLeftSiblingTokenType(DetailAST anAST, int aTokenType) {
	if (anAST == null) return null;
	if (anAST.getType() == aTokenType) return anAST;
	return getFirstLeftSiblingTokenType(anAST.getPreviousSibling(), aTokenType);
	
}
public static DetailAST getFirstRightSiblingTokenType(DetailAST anAST, int aTokenType) {
	if (anAST == null) return null;
	if (anAST.getType() == aTokenType) return anAST;
	return getFirstRightSiblingTokenType(anAST.getNextSibling(), aTokenType);
	
}
protected void setIntValueOfType(String newVal) {
	String[] aTypeAndValue = newVal.split (TYPE_SEPARATOR);
	String aType = MATCH_ANYTHING;
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
 
// public static void main (String[] args) {
//	 System.out.println("String".matches(".*\\[\\]"));
// }
}
