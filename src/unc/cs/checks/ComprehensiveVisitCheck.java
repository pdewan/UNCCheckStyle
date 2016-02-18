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
import unc.cs.symbolTable.AnSTMethodFromMethod;
import unc.cs.symbolTable.AnSTType;
import unc.cs.symbolTable.AnSTMethod;
import unc.cs.symbolTable.AnSTNameable;
import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.PropertyInfo;
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
		ContinuationProcessor {
	protected boolean inMethodOrConstructor;

	// public static final String MSG_KEY = "stBuilder";
	protected boolean isEnum;
	protected boolean isInterface;
	protected boolean isElaboration;
	protected STNameable superClass;
	protected STNameable[] interfaces;
	protected boolean currentMethodIsConstructor;
	protected String currentMethodName;
	DetailAST currentMethodNameAST;
	protected String currentMethodType;
	protected DetailAST currentMethodAST;
	protected boolean currentMethodIsPublic;
	protected boolean currentMethodAssignsToGlobalVariable;
	// protected List<String[]> methodsCalledByCurrentMethod = new ArrayList();
	protected List<CallInfo> methodsCalledByCurrentMethod = new ArrayList();
	protected List<String> globalsAccessedByCurrentMethod = new ArrayList();
	protected List<String> globalsAssignedByCurrentMethod = new ArrayList();

	protected boolean currentMethodIsInstance;

	protected boolean currentMethodIsVisible;
	protected List<String> currentMethodParameterTypes = new ArrayList();
	protected List<String> currentMethodParameterNames = new ArrayList();

	// protected List<STNameable> imports = new ArrayList();
	// protected static Set<String> externalImports = new HashSet();
	// protected static Set<String> javaLangClassesSet;
	protected List<STNameable> propertyNames;
	protected List<STNameable> editablePropertyNames;
	// protected List<STNameable> typeTags;
	// protected List<STNameable> currentMethodTags;
	protected Map<String, String> typeScope = new HashMap();
	protected List<STNameable> globalVariables = new ArrayList();
	protected Map<String, String> globalVariableToType = new HashMap();
	protected Map<String, DetailAST> globalVariableToRHS = new HashMap();

	protected Map<String, List<CallInfo>> globalVariableToCall = new HashMap();
	protected Map<String, String> currentMethodScope = new HashMap();

	protected List<STNameable> typesInstantiated = new ArrayList();
	protected List<STNameable> typesInstantiatedByCurrentMethod = new ArrayList();

	// protected Set<String> excludeTags;
	// protected Set<String> includeTags;
	// protected DetailAST currentTree;
	// protected boolean tagsInitialized;
	// public static String[] javaLangClasses = {
	// "Integer",
	// "Double",
	// "Character",
	// "String",
	// "Boolean",
	// };

	// protected STNameable structurePattern;
	Map<DetailAST, List<DetailAST>> astToPendingChecks = new HashMap();
	Map<DetailAST, Object> astToContinuationData = new HashMap();

	Map<DetailAST, FileContents> astToFileContents = new HashMap();
	Map<String, DetailAST> fileNameToTree = new HashMap();

	protected Set<String> excludeStructuredTypes = new HashSet();

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,
				TokenTypes.INTERFACE_DEF, TokenTypes.TYPE_ARGUMENTS,
				TokenTypes.TYPE_PARAMETERS, TokenTypes.VARIABLE_DEF,
				TokenTypes.PARAMETER_DEF, TokenTypes.METHOD_DEF,
				TokenTypes.CTOR_DEF, TokenTypes.IMPORT,
				TokenTypes.STATIC_IMPORT, TokenTypes.LCURLY, TokenTypes.RCURLY,
				TokenTypes.METHOD_CALL, TokenTypes.IDENT, TokenTypes.ENUM_DEF,
				TokenTypes.LITERAL_NEW, TokenTypes.IDENT };
	}

	// public void setIncludeTags(String[] newVal) {
	// this.includeTags = new HashSet(Arrays.asList(newVal));
	// }
	// public void setExcludeTags(String[] newVal) {
	// this.excludeTags = new HashSet(Arrays.asList(newVal));
	// }
	//
	// public boolean hasExcludeTags() {
	// return excludeTags != null && excludeTags.size() > 1;
	// }
	//
	// public boolean hasIncludeTags() {
	// return includeTags != null && includeTags.size() > 1;
	// }
	//
	// public static boolean contains (Collection<String> aTags, String aTag) {
	// for (String aStoredTag:aTags) {
	// if (matchesStoredTag(aStoredTag, aTag))
	// return true;
	// }
	// return false;
	// }
	//
	// public boolean checkTagOfCurrentType(String aTag) {
	// if (hasIncludeTags()) {
	// return contains(includeTags, aTag);
	// } else { // we know it has exclude tags
	// return !contains(excludeTags, aTag);
	// }
	// }
	// public boolean checkIncludeTagOfCurrentType(String aTag) {
	// return includeTags.contains(aTag);
	//
	// }
	// public boolean checkExcludeTagOfCurrentType(String aTag) {
	// return !excludeTags.contains(aTag);
	//
	// }
	// public boolean checkIncludeTagOfCurrentType(String aTag) {
	// if (
	// return includeTags.contains(aTag);
	// } else { // we know it has exclude tags
	// return !excludeTags.contains(aTag);
	// }
	// }

	// public boolean checkTagsOfCurrentType() {
	// if (!hasIncludeTags() && !hasExcludeTags())
	// return true; // all tags checked in this case
	// if (fullTypeName == null) {
	// System.err.println("Check called without type name being populated");
	// return true;
	// }
	// // STType anSTType =
	// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(typeName);
	// // STNameable[] aCurrentTags = anSTType.getTags();
	// List<STNameable> aCurrentTags = typeTags;
	// for (STNameable aCurrentTag:aCurrentTags) {
	// if (checkTagOfCurrentType(aCurrentTag.getName()))
	// return true;
	// }
	// return false;
	//
	// }
	// public static Boolean hasTag(STNameable[] aTags, String aTag) {
	// for (STNameable anSTNameable:aTags) {
	// if (anSTNameable.getName().equals(aTag)) return true;
	//
	// }
	// return false;
	// }
	// public boolean checkIncludeTagsOfCurrentType() {
	// if (!hasIncludeTags() && !hasExcludeTags())
	// return true; // all tags checked in this case
	// if (fullTypeName == null) {
	// System.err.println("Check called without type name being populated");
	// return true;
	// }
	// STType anSTType =
	// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
	// STNameable[] aCurrentTags = anSTType.getTags();
	// if (hasIncludeTags())
	// return checkIncludeTagsOfCurrentType(aCurrentTags);
	// else
	// return checkExcludeTagsOfCurrentType(aCurrentTags);
	// }
	// // if anyone says exclude, exclude
	// public boolean checkExcludeTagsOfCurrentType(STNameable[] aCurrentTags) {
	//
	// for (STNameable aCurrentTag:aCurrentTags) {
	// if (checkExcludeTagOfCurrentType(aCurrentTag.getName()))
	// return true;
	// }
	// return false;
	//
	// }
	// // if anyone says include, include
	// public boolean checkIncludeTagsOfCurrentType(STNameable[] aCurrentTags) {
	//
	// for (STNameable aCurrentTag:aCurrentTags) {
	// if (checkIncludeTagOfCurrentType(aCurrentTag.getName()))
	// return true;
	// }
	// return false;
	//
	// }
	// static STNameable[] emptyNameableArray = {};
	// static List<STNameable> emptyNameableList =new ArrayList();

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
				anInterfaces.add(new AnSTNameable(anImplementedInterface,
						anImplementedInterface.getText()));
			anImplementedInterface = anImplementedInterface.getNextSibling();
		}
		return (STNameable[]) anInterfaces.toArray(emptyNameableArray);
	}

	protected Map<STMethod, String> methodToSignature = new HashMap();

	public List<STMethod> signaturesToMethods(String[] aSignatures) {
		List<STMethod> aMethods = new ArrayList();
		// do not clear it, as this is set before any signatures
		// methodToSignature.clear();
		for (String aSignature : aSignatures) {
			aSignature = aSignature.trim();
			STMethod aMethod = signatureToMethodorOrConstructor(maybeStripComment(aSignature));

			// aMethods.add(signatureToMethodorOrConstructor(aSignature));
			aMethods.add(aMethod);
			if (!aMethod.getSignature().equals(aSignature))
				methodToSignature.put(aMethod, aSignature);

		}
		return aMethods;
	}

	public STMethod signatureToMethodorOrConstructor(String aSignature) {
		return signatureToMethod(aSignature);
	}

	public static Boolean isIdentifier(String aString) {
		if (aString.length() == 0)
			return false;
		char aFirstChar = aString.charAt(0);
		if (!Character.isLetter(aFirstChar) && (aFirstChar != TAG_CHAR)) {
			return false;
		}
		for (int index = 1; index < aString.length(); index++) { // in case of
																	// tag will
																	// allow
																	// first
																	// char to
																	// be digit
			if (!Character.isLetter(aString.charAt(index))
					&& !Character.isDigit(aString.charAt(index))) {
				return false;
			}
		}
		return true;

	}

	public Boolean matchSignature(STMethod aSpecification, STMethod aMethod) {
		// let someone else add this , some instance method that calls it
		variablesAdded.clear();

		String aSpecifiedReturnType = aSpecification.getReturnType();
		String anActualReturnType = aMethod.getReturnType();

		STNameable[] anActualTypeTags = null;
		if (aSpecifiedReturnType != null
				&& aSpecifiedReturnType.startsWith(TAG_STRING)) {
			STType aReturnSTType = SymbolTableFactory.getOrCreateSymbolTable()
					.getSTClassByShortName(anActualReturnType);

			// STType aReturnSTType =
			// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aSpeifiedReturnType.substring(1));
			if (aReturnSTType == null)
				return null;
			anActualTypeTags = aReturnSTType.getComputedTags();
		}

		if (aMethod == null) {
			System.err.println("Null method name");
			return true;
		}

		Boolean retVal =
		// aSpecification.getParameterTypes().length ==
		// aMethod.getParameterTypes().length &&
		unifyingMatchesNameVariableOrTag(aSpecification.getName(),
				aMethod.getName(), aMethod.getComputedTags())
				&& (aSpecifiedReturnType == null || unifyingMatchesNameVariableOrTag(
						aSpecifiedReturnType, aMethod.getReturnType(),
						anActualTypeTags)

				// matchesNameVariableOrTag(aSpecification.getReturnType(),
				// aMethod.getReturnType(), typeTags)
				);

		if (!retVal) {
			backTrackUnification();
			return false;
		}
		String[] aSpecificationParameterTypes = aSpecification
				.getParameterTypes();
		String[] aMethodParameterTypes = aMethod.getParameterTypes();

		if (aSpecificationParameterTypes == null)
			return true;
		if (aSpecificationParameterTypes.length == 1) {
			if (aSpecificationParameterTypes[0].equals(MATCH_ANYTHING))
				return true;
		}
		if (aSpecificationParameterTypes.length != aMethodParameterTypes.length) {
			return false;
		}
		for (int i = 0; i < aSpecificationParameterTypes.length; i++) {

			String aParameterType = aSpecificationParameterTypes[i];

			STNameable[] parameterTags = null;
			if (aParameterType.startsWith(TAG_STRING)) {

				STType aParameterSTType = SymbolTableFactory
						.getOrCreateSymbolTable().getSTClassByShortName(
								aParameterType.substring(1));
				if (aParameterSTType == null)
					return null;
				parameterTags = aParameterSTType.getComputedTags();
			}

			if (!unifyingMatchesNameVariableOrTag(
					aSpecificationParameterTypes[i], aMethodParameterTypes[i],
					parameterTags)) {
				// backTrackUnification();
				return false;
			}
		}
		return true;

	}

	protected List<STMethod> getMatchingMethods(STType aTargetSTType,
			STMethod aSpecifiedMethod) {
		List<STMethod> result = new ArrayList();
		int i = 0;
		STMethod[] aMethods = aTargetSTType.getMethods();
		if (aMethods == null)
			return null;
		boolean hadNullMatch = false;
		for (STMethod anSTMethod : aMethods) {
			Boolean aMatch = matchSignature(aSpecifiedMethod, anSTMethod);
			if (aMatch == null) {
				hadNullMatch = true;
				continue;
			}

			// if (!matchSignature(aSpecifiedMethod, anSTMethod))

			if (!aMatch)
				continue;
			result.add(anSTMethod);
			// if (anSTMethod.getName().equals(aCallInfo.getCalleee()) &&
			// anSTMethod.getParameterTypes().length ==
			// aCallInfo.getActuals().size()) {
			// return hasTag(anSTMethod, aSpecifiedMethod.getName());
			// }
		}
		if (hadNullMatch)
			return null; // either way we do not know if something bad happened
		return result;

	}

	public static void addAllNoDuplicates(List anOriginal, List aNew) {
		for (Object newElement : aNew) {
			if (anOriginal.contains(newElement))
				continue;
			anOriginal.add(newElement);
		}
	}

	public Boolean matchesCallingMethod(STType anSTType,
			STMethod aSpecifiedMethod, STMethod anActualMethod) {
		// int i = 0;
		Boolean aMatch = matchSignature(aSpecifiedMethod, anActualMethod);
		if (aMatch == null) {
			return null;
		}
		if (aMatch) // check if there is a direct call by the specified method
		// if (retVal)
			return true;
		// now go through the call graph and see if the specified method calls a
		// method that matches the actual method
		List<STMethod> aMatchingSpecifiedMethods = getMatchingMethods(anSTType,
				aSpecifiedMethod);
		if (aMatchingSpecifiedMethods == null) {
			return null;
		}
		for (STMethod aRootMethod : aMatchingSpecifiedMethods) {
			if (aRootMethod == null)
				continue;
			Boolean callsInternally = aRootMethod
					.callsInternally(anActualMethod);
			if (callsInternally == null) {
				continue;
			}
			if (callsInternally)
				// if (aRootMethod.callsInternally(anActualMethod))
				return true;
			// List<STMethod> aCalledMethods =
			// aRootMethod.getLocalCallClosure();
			// for (STMethod aCalledMethod:aCalledMethods) {
			// if (anActualMethod == aCalledMethod)
			// return true;
			// }
		}
		return false;

	}

	public static STMethod signatureToMethod(String aSignature) {
		String[] aNameAndRest = aSignature.split(":");
		if (aNameAndRest.length == 1) {
			if (!aSignature.equals(MATCH_ANYTHING) && !isIdentifier(aSignature)) {
				System.err
						.println("Illegal signature,"
								+ aSignature
								+ ", missing :\n Assuming parameters and return types do not matter");
			}
			return new AnSTMethod(null, aSignature.trim(), null, null, null,
					true, true, false, null, false, null, null, false, null,
					null, null, null);
		}
		if (aNameAndRest.length > 2) {
			System.err.println("Illegal signature," + aSignature
					+ ",  too many :" + aSignature);
			return null;
		}
		// if (aNameAndRest.length != 2) {
		// System.err.println("Illegal signature, missing :" + aSignature);
		// return null;
		// }

		String aName = aNameAndRest[0].trim();
		String[] aReturnTypeAndParameters = aNameAndRest[1].split("->");
		if (aReturnTypeAndParameters.length != 2) {
			System.err.println("Illegal signature, missing ->" + aSignature);
			return null;
		}
		String aReturnType = aReturnTypeAndParameters[1].trim();
		String aParametersString = aReturnTypeAndParameters[0];
		String[] aParameterTypes = aParametersString.equals("") ? new String[0]
				: aParametersString.split(STMethod.PARAMETER_SEPARATOR);
		for (int i = 0; i < aParameterTypes.length; i++) {
			aParameterTypes[i] = aParameterTypes[i].trim();

		}
		return new AnSTMethod(null, aName, null, null, aParameterTypes, true,
				true, false, aReturnType, true, null, null, false, null, null,
				null, null);

	}

	public STMethod signatureToConstructor(String aSignature) {
		int aColonIndex = aSignature.indexOf(':');
		// String[] aNameAndRest = aSignature.split(":");
		if (aColonIndex == -1) {
			System.err.print("Illegal signature, missing :" + aSignature);
			return null;
		}
		String aNameAndRest = aSignature.substring(aColonIndex + 1);
		String aName = "";
		String aReturnType = "";

		String[] aReturnTypeAndParameters = aNameAndRest.split("->");
		String aParametersString = aReturnTypeAndParameters[0].trim();
		String[] aParameterTypes = aParametersString.equals("") ? new String[0]
				: aParametersString.split(STMethod.PARAMETER_SEPARATOR);
		for (int i = 0; i < aParameterTypes.length; i++) {
			aParameterTypes[i] = aParameterTypes[i].trim();

		}
		return new AnSTMethod(null, aName, null, null, aParameterTypes, true,
				true, false, aReturnType, true, null, null, false, null, null,
				null, null);

	}

	protected Map<DetailAST, FileContents> getAstToFileContents() {
		return astToFileContents;
	}

	public static List<String> toNames(Collection<STNameable> aNameables) {
		List result = new ArrayList<>(aNameables.size());
		for (STNameable aNameable : aNameables) {
			result.add(aNameable.getName());
		}
		return result;
	}

	public static List<STType> toSTTypes(Collection<STNameable> aNameables) {
		List<STType> result = new ArrayList<>(aNameables.size());
		for (STNameable aNameable : aNameables) {
			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
					.getSTClassByShortName(aNameable.getName());
			if (anSTType == null) {
				return null;
			}
			result.add(anSTType);
		}
		return result;
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
				aSuperTypes.add(new AnSTNameable(anExtendedType, anExtendedType
						.getText()));
			anExtendedType = anExtendedType.getNextSibling();
		}
		return (STNameable[]) aSuperTypes.toArray(emptyArray);

	}

	// public static List<STNameable> getArrayLiterals (DetailAST
	// parentOfArrayInitializer) {
	// List<STNameable> result = new ArrayList<>();
	// DetailAST arrayInit =
	// parentOfArrayInitializer.findFirstToken(TokenTypes.ANNOTATION_ARRAY_INIT);
	// if (arrayInit == null)
	// arrayInit = parentOfArrayInitializer; // single element array
	// DetailAST anArrayElementExpression =
	// arrayInit.findFirstToken(TokenTypes.EXPR);
	//
	// while (anArrayElementExpression != null) {
	// DetailAST anArrayElementAST = anArrayElementExpression.getFirstChild();
	// result.add(new AnSTNameable(anArrayElementAST,
	// anArrayElementAST.getText()));
	// if (anArrayElementExpression.getNextSibling() == null)
	// break;
	// anArrayElementExpression =
	// anArrayElementExpression.getNextSibling().getNextSibling();
	// }
	// return result;
	// }

	// protected List emptyArrayList = new ArrayList();
	public void maybeVisitPropertyNames(DetailAST ast) {
		// not putting dependency on OE
		DetailAST annotationAST = AnnotationUtility.getAnnotation(ast,
				"PropertyNames");
		if (annotationAST == null) {
			propertyNames = emptyArrayList;
			return;
		}
		propertyNames = getArrayLiterals(annotationAST);
	}

	public void maybeVisitEditablePropertyNames(DetailAST ast) {
		DetailAST annotationAST = AnnotationUtility.getAnnotation(ast,
				"EditablePropertyNames");
		if (annotationAST == null) {
			editablePropertyNames = emptyArrayList;
			return;
		}
		editablePropertyNames = getArrayLiterals(annotationAST);
	}

	public void maybeVisitStructurePattern(DetailAST ast) {
		DetailAST annotationAST = AnnotationUtility.getAnnotation(ast,
				"StructurePattern");
		if (annotationAST == null) {
			structurePattern = null;
			return;
		}
		// if (structurePattern != null)
		// return;
		DetailAST expressionAST = annotationAST.findFirstToken(TokenTypes.EXPR);
		DetailAST actualParamAST = expressionAST.getFirstChild();
		String actualParamText = null;
		if (actualParamAST.getType() == TokenTypes.STRING_LITERAL) {
			actualParamText = actualParamAST.getText();
		} else {
			FullIdent aFullIdent = FullIdent.createFullIdent(actualParamAST);
			actualParamText = aFullIdent.getText();
		}
		// structurePattern = new AnSTNameable(actualParamAST,
		// actualParam.getText());
		structurePattern = new AnSTNameable(actualParamAST, actualParamText);
	}

	public void maybeVisitVisible(DetailAST ast) {
		currentMethodIsVisible = true;
		DetailAST annotationAST = AnnotationUtility.getAnnotation(ast,
				"Visible");
		if (annotationAST == null)
			return;
		DetailAST expressionAST = annotationAST.findFirstToken(TokenTypes.EXPR);
		DetailAST actualParamAST = expressionAST.getFirstChild();
		FullIdent actualParamIDent = FullIdent.createFullIdent(actualParamAST);
		currentMethodIsVisible = !"false".equals(actualParamIDent.getText());
	}

	// protected List<STNameable> typeTags( ) {
	// if (!tagsInitialized) {
	// DetailAST aTypeAST = getEnclosingTypeDeclaration(currentTree);
	// maybeVisitTypeTags(aTypeAST);
	// }
	// return typeTags;
	// }
	// public void maybeVisitTypeTags(DetailAST ast) {
	// if (tagsInitialized) return;
	// tagsInitialized = true;
	// DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Tags");
	// if (annotationAST == null) {
	// typeTags = emptyArrayList;
	// return;
	// }
	// typeTags = getArrayLiterals(annotationAST);
	// }
	// public void maybeVisitMethodTags(DetailAST ast) {
	// DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Tags");
	// if (annotationAST == null) {
	// currentMethodTags = emptyArrayList;
	// return;
	// }
	// currentMethodTags = getArrayLiterals(annotationAST);
	// }

	public void visitEnumDef(DetailAST anEnumDef) {
		isEnum = true;
		typeNameAST = getEnumNameAST(anEnumDef);
		// shortTypeName = getEnumName(anEnumDef);
		shortTypeName = typeNameAST.getText();
		fullTypeName = packageName + "." + shortTypeName;
		typeAST = anEnumDef;
		superClass = null;
		interfaces = emptyNameableArray;
		isInterface = false;
		typeNameable = new AnSTNameable(typeNameAST, fullTypeName);

		// shortTypeName = anEnumDef.getNextSibling().toString();
		// DetailAST anEnumIdent =
		// anEnumDef.getNextSibling().findFirstToken(TokenTypes.IDENT);
		// if (anEnumIdent == null) {
		// System.out.println("null enum ident");
		// }
		// shortTypeName = anEnumIdent.getText();
	}

	// protected static String getEnumName(DetailAST anEnumDef) {
	// return getEnumAST(anEnumDef).toString();
	// }
	// protected static DetailAST getEnumAST(DetailAST anEnumDef) {
	// return anEnumDef.getNextSibling();
	// }

	public void visitType(DetailAST typeDef) {
		if (getVisitInnerClasses()) {
			 initializeTypeState();
		 }
		super.visitType(typeDef);
		
		// this should move to minimal
		maybeVisitStructurePattern(typeDef);
		// why this?
		// if (!checkIncludeExcludeTagsOfCurrentType())
		// return;
		// maybeVisitStructurePattern(typeDef);
		maybeVisitPropertyNames(typeDef);
		maybeVisitEditablePropertyNames(typeDef);
		maybeVisitTypeTags(typeDef);
		
		 
		
	}

	public STNameable getPattern(String aShortClassName) {
		// List<STNameable> aTags = emptyList;

		// these classes have no tags
		// if ( aShortClassName.endsWith("[]") ||
		// allKnownImports.contains(aShortClassName) ||
		// javaLangClassesSet.contains(aShortClassName) ) {
		// return emptyList;
		// }
		if (isArray(aShortClassName) || isJavaLangClass(aShortClassName)) {
			return null;
		}
		if (shortTypeName == null || // guaranteed to not be a pending check
				(aShortClassName.equals(shortTypeName) || aShortClassName
						.endsWith("." + shortTypeName))) {
			return structurePattern;
		} else {
			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
					.getSTClassByShortName(aShortClassName);

			if (anSTType == null) {
				if (isExternalImport(aShortClassName)) // check last as we are
														// not really sure about
														// external
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

	// protected void processPreviousMethodData() {
	// if (currentMethodName != null) {
	// String[] aParameterTypes = currentMethodParameterTypes.toArray(new
	// String[0]);
	// STMethod anSTMethod = new AnSTMethod(
	// currentMethodAST,
	// currentMethodName,
	// typeName,
	// aParameterTypes,
	// currentMethodIsPublic,
	// currentMethodType,
	// currentMethodIsVisible);
	// stMethods.add(anSTMethod);
	// }
	//
	// }
	protected void visitMethod(DetailAST methodDef) {
		processPreviousMethodData(); // this should be done in a leave method
		currentMethodIsConstructor = false;
		visitMethodOrConstructor(methodDef);
		// maybeVisitMethodTags(methodDef);
	}

	protected void visitConstructor(DetailAST methodDef) {
		processPreviousMethodData();
		currentMethodIsConstructor = true;
		visitMethodOrConstructor(methodDef);
		// maybeVisitMethodTags(methodDef); // shouls bw in visitMethodOr

	}

	protected void leaveMethodOrConstructor(DetailAST methodDef) {
		inMethodOrConstructor = false;
	}

	protected void leaveMethod(DetailAST methodDef) {
		leaveMethodOrConstructor(methodDef);
	}

	protected void leaveConstructor(DetailAST methodDef) {
		leaveMethodOrConstructor(methodDef);
	}

	protected void visitMethodOrConstructor(DetailAST methodDef) {
		// if (currentMethodName != null) {
		// String[] aParameterTypes = currentMethodParameterTypes.toArray(new
		// String[0]);
		// STMethod anSTMethod = new AnSTMethod(
		// currentMethodAST,
		// currentMethodName,
		// typeName,
		// aParameterTypes,
		// currentMethodIsPublic,
		// currentMethodType,
		// currentMethodIsVisible);
		// stMethods.add(anSTMethod);
		// }
		// processPreviousMethodData();
		inMethodOrConstructor = true;
		currentMethodType = "";
		currentMethodParameterTypes.clear();
		currentMethodParameterNames.clear();
		currentMethodScope.clear();
		methodsCalledByCurrentMethod.clear();
		globalsAccessedByCurrentMethod.clear();
		currentMethodAssignsToGlobalVariable = false;
		currentMethodTags = emptyNameableList;
		currentMethodComputedTags = emptyNameableList;
		typesInstantiatedByCurrentMethod.clear();
		// DetailAST aMethodNameAST =
		// methodDef.findFirstToken(TokenTypes.IDENT);
		// currentMethodName = aMethodNameAST.getText();
		currentMethodName = getName(methodDef);

		currentMethodIsPublic = isPublic(methodDef);
		currentMethodIsInstance = !isStatic(methodDef);
		if (!currentMethodIsConstructor) {
			// DetailAST typeDef = methodDef.findFirstToken(TokenTypes.TYPE);
			currentMethodType = typeASTToString(methodDef);
			// DetailAST firstChild = typeDef.getFirstChild();
			// if (firstChild.getType() == TokenTypes.ARRAY_DECLARATOR) {
			// // if (firstChild.getText().startsWith("[")) {
			// currentMethodType = firstChild.getFirstChild().getText() + "[]";
			// } else {
			// FullIdent aTypeFullIdent = FullIdent
			// .createFullIdent(firstChild);
			// currentMethodType = aTypeFullIdent.getText();
			// }
		}
		currentMethodAST = methodDef;
		maybeVisitVisible(methodDef);
		maybeVisitMethodTags(methodDef);

	}

	protected void visitParamDef(DetailAST paramDef) {
		final DetailAST grandParentAST = paramDef.getParent().getParent();
		// why this condition?
		// if (!(grandParentAST.getType() == TokenTypes.METHOD_DEF ))
		// return;
		if ((grandParentAST.getType() != TokenTypes.METHOD_DEF && grandParentAST
				.getType() != TokenTypes.CTOR_DEF))
			return;
		final DetailAST aType = paramDef.findFirstToken(TokenTypes.TYPE);
		final DetailAST aParameterNameAST = aType.getNextSibling();
		final String aParameterName = aParameterNameAST.getText();

		// DetailAST aTypeNameAST = aType.findFirstToken(TokenTypes.IDENT);
		// String aTypeName = aTypeNameAST.getText();
		final DetailAST arrayDeclAST = aType
				.findFirstToken(TokenTypes.ARRAY_DECLARATOR);

		final FullIdent anIdentifierType = CheckUtils.createFullType(aType);
		String text = anIdentifierType.getText();
		if (arrayDeclAST != null)
			text = text + "[]";
		currentMethodParameterTypes.add(text);
		currentMethodParameterNames.add(aParameterName);
		addToMethodScope(paramDef);

	}

	protected void visitTypeParameters(DetailAST typeParameters) {
		isGeneric = true;

	}

	protected void visitTypeArguments(DetailAST typeParameters) {
		isElaboration = true;

	}

	protected void visitClass(DetailAST ast) {
		visitType(ast);
		// moving to leave so we can fill symbol table correctly
//		if (!checkIncludeExcludeTagsOfCurrentType())
//			return;
//		STNameable[] superTypes = getSuperTypes(ast);
//		if (superTypes.length == 0)
//			superClass = null;
//		else
//			superClass = superTypes[0];
//		interfaces = getInterfaces(ast);
//		isInterface = false;
	}

	protected void visitInterface(DetailAST ast) {
		visitType(ast);
		// moving to leave so we can fill symbol table correctly
//		if (!checkIncludeExcludeTagsOfCurrentType())
//			return;
//		superClass = null;
//		interfaces = getSuperTypes(ast);
//		isInterface = true;
	}
	protected void leaveClass(DetailAST ast) {
		if (checkIncludeExcludeTagsOfCurrentType()) {		
		STNameable[] superTypes = getSuperTypes(ast);
		if (superTypes.length == 0)
			superClass = null;
		else
			superClass = superTypes[0];
		interfaces = getInterfaces(ast);
		isInterface = false;
		}
		leaveType(ast);

		
	}
	
	
    protected void leaveInterface(DetailAST ast) {
    	if (checkIncludeExcludeTagsOfCurrentType()) {
		superClass = null;
		interfaces = getSuperTypes(ast);
		isInterface = true;
    	}
    	leaveType(ast);

	}

	// public static boolean isProjectImport(String aFullName) {
	// for (String aPrefix:STBuilderCheck.geProjectPackagePrefixes())
	// if (aFullName.startsWith(aPrefix)) return true;
	// return false;
	// }
	// public void visitImport(DetailAST ast) {
	// FullIdent anImport = FullIdent.createFullIdentBelow(ast);
	// String aLongClassName = anImport.getText();
	// String aShortClassName = getLastDescendent(ast).getText();
	//
	// STNameable anSTNameable = new AnSTNameable(ast, aLongClassName);
	// imports.add(anSTNameable);
	// if (!isProjectImport(aLongClassName))
	// externalImports.add(aShortClassName);
	// }
	// public void visitStaticImport(DetailAST ast) {
	// DetailAST anImportAST = ast.getFirstChild().getNextSibling();
	// FullIdent anImport = FullIdent.createFullIdent(
	// anImportAST);
	// STNameable anSTNameable = new AnSTNameable(ast, anImport.getText());
	// imports.add(anSTNameable);
	// }

	protected void visitLCurly(DetailAST ast) {

	}

	protected void visitRCurly(DetailAST ast) {

	}
	// not used, so delete it
//	protected void visitVariableOrParameterDef(DetailAST ast) {
//		// if (!checkIncludeExcludeTagsOfCurrentType())
//		// return;
//		if (ScopeUtils.inCodeBlock(ast))
//			// ||
//			// ast.getParent().getType() == TokenTypes.LITERAL_CATCH)
//			addToMethodScope(ast);
//		else
//			addToTypeScope(ast);
//	}

	public static boolean isArrayIndex(String aVariable) {
		return aVariable != null && aVariable.endsWith("]");
	}

	public static String removeBrackets(String anArrayIndex) {
		return anArrayIndex.substring(0, anArrayIndex.indexOf("["));
	}

	public String lookupType(String aVariable) {
		boolean isArrayIndex = isArrayIndex(aVariable);
		if (isArrayIndex)
			aVariable = removeBrackets(aVariable);
		String result = lookupLocal(aVariable);
		if (result == null)
			result = lookupGlobal(aVariable);
		if (result == null) // method on a class?
			result = aVariable;
		if (isArrayIndex) {
			if (!isArrayIndex(result)) {
				return result; // occurs when the variable is not stored but is
								// looked up, IllegalInitCall
			}
			result = removeBrackets(result);
		}
		return result;
	}

	public String lookupLocal(String aVariable) {
		return currentMethodScope.get(aVariable);
	}

	public String lookupGlobal(String aVariable) {
		if (aVariable == null)
			return null;
		if (aVariable.equals("this")) {
			return fullTypeName; // short type name?
		}
		return typeScope.get(aVariable);
	}

	public void visitVariableDef(DetailAST ast) {
		if (ScopeUtils.inCodeBlock(ast)) {
			// ||
			// ast.getParent().getType() == TokenTypes.LITERAL_CATCH)
			addToMethodScope(ast);
		} else {
			addToTypeScope(ast);
		}
		
//		visitVariableOrParameterDef(paramOrVarDef);
		// code moved to addToScope
		// if (!ScopeUtils.inCodeBlock(paramOrVarDef)) {
		// final DetailAST aTypeParent = paramOrVarDef
		// .findFirstToken(TokenTypes.TYPE);
		// FullIdent aTypeIdent = FullIdent.createFullIdentBelow(aTypeParent);
		// final DetailAST anIdentifier = paramOrVarDef
		// .findFirstToken(TokenTypes.IDENT);
		// DetailAST aMaybeAssign = anIdentifier.getNextSibling();
		// if (aMaybeAssign != null && aMaybeAssign.getType() ==
		// TokenTypes.ASSIGN) {
		// DetailAST anRHS = aMaybeAssign.getFirstChild();
		// globalVariableToRHS.put(anIdentifier.getText(),anRHS);
		// }
		//
		// ;
		// STNameable anSTNameable = new AnSTNameable(paramOrVarDef,
		// anIdentifier.getText(), aTypeIdent.getText());
		// globalVariables.add(anSTNameable);
		// globalVariableToType.put(anIdentifier.getText(),
		// aTypeIdent.getText());
		// }

	}

	// public void visitParameterDef(DetailAST ast) {
	// if (!checkIncludeExcludeTagsOfCurrentType())
	// return;
	// visitVariableOrParameterDef(ast);
	// }
	// This is really kludgy, what aot for parameters etc. we need to open and
	// close scopes.
	// actually we are only looking at granparents that are methods
	public void addToMethodScope(DetailAST paramOrVarDef) {
		// final DetailAST aType =
		// paramOrVarDef.findFirstToken(TokenTypes.TYPE);
		// final DetailAST anIdentifier =
		// paramOrVarDef.findFirstToken(TokenTypes.IDENT);
		// final FullIdent anIdentifierType = CheckUtils.createFullType(aType);
		// currentMethodScope.put(anIdentifier.getText(),
		// anIdentifierType.getText());
		addToScope(paramOrVarDef, currentMethodScope);
	}

	public static String getTypeName(DetailAST paramOrVarDef) {
		DetailAST aTypeAST = paramOrVarDef.findFirstToken(TokenTypes.TYPE);
		//
		DetailAST aSpecificTypeAST = aTypeAST.getFirstChild();
		if (aSpecificTypeAST.getType() == TokenTypes.ARRAY_DECLARATOR) {
			String anElementType = FullIdent.createFullIdentBelow(
					aSpecificTypeAST).getText();
			return anElementType + "[]";
		}
		// return FullIdent.createFullIdentBelow(
		// paramOrVarDef.findFirstToken(TokenTypes.TYPE)).getText();
		return FullIdent.createFullIdentBelow(aTypeAST).getText();

	}

	public static String typeASTToString(DetailAST parentAST) {
		DetailAST typeDef = parentAST.findFirstToken(TokenTypes.TYPE);
		if (typeDef == null) {
			return "";
		}

		DetailAST firstChild = typeDef.getFirstChild();
		String result;
		if (firstChild.getType() == TokenTypes.ARRAY_DECLARATOR) {
			// if (firstChild.getText().startsWith("[")) {
			result = firstChild.getFirstChild().getText() + "[]";
		} else {
			FullIdent aTypeFullIdent = FullIdent.createFullIdent(firstChild);
			result = aTypeFullIdent.getText();
		}
		return result;
	}

	public void addToScope(DetailAST paramOrVarDef, Map<String, String> aScope) {
		int i = 0;
		// final DetailAST aType =
		// paramOrVarDef.findFirstToken(TokenTypes.TYPE);
		final DetailAST anIdentifier = paramOrVarDef
				.findFirstToken(TokenTypes.IDENT);
		// final FullIdent anIdentifierType = CheckUtils.createFullType(aType);
		// final FullIdent anIdentifierType =
		// FullIdent.createFullIdentBelow(aType);
		String aTypeName = getTypeName(paramOrVarDef);

		aScope.put(anIdentifier.getText(), aTypeName);
		if (aScope == typeScope) {
			// final DetailAST aTypeParent = paramOrVarDef
			// .findFirstToken(TokenTypes.TYPE);
			// FullIdent aTypeIdent =
			// FullIdent.createFullIdentBelow(aTypeParent);
			// final DetailAST anIdentifier = paramOrVarDef
			// .findFirstToken(TokenTypes.IDENT);
			DetailAST aMaybeAssign = anIdentifier.getNextSibling();
			if (aMaybeAssign != null
					&& aMaybeAssign.getType() == TokenTypes.ASSIGN) {
				DetailAST anRHS = aMaybeAssign.getFirstChild();
				globalVariableToRHS.put(anIdentifier.getText(), anRHS);
			}

			;
			STNameable anSTNameable = new AnSTNameable(paramOrVarDef,
					anIdentifier.getText(), aTypeName);
			globalVariables.add(anSTNameable);
			globalVariableToType.put(anIdentifier.getText(), aTypeName);

		}
	}

	public void addToTypeScope(DetailAST paramOrVarDef) {

		addToScope(paramOrVarDef, typeScope);
	}

	public Boolean isGlobal(String aName) {
		for (STNameable aGlobal : globalVariables) {
			if (aGlobal.getName().equals(aName))
				return true;
		}
		return false;
	}

	public static List<DetailAST> getEListComponents(DetailAST anEList) {
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
	public static String toLongName(String[] aNormalizedName) {
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
	public CallInfo registerMethodCallAndtoNormalizedParts(DetailAST ast,
			DetailAST aTreeAST) {
		String aTargetName = null;
		String shortMethodName = null;
		String aCastType = null;

		// if (ast.getType() == TokenTypes.CTOR_CALL) {
		// aTargetName = getTypeName(aTreeAST);
		// shortMethodName = aTargetName;
		//
		// } else {
		// DetailAST aLeftMostMethodTargetAST = null;
		// String shortMethodName = null;
		// if (ast.getType() == TokenTypes.METHOD_CALL) {
		// FullIdent aFullIndent = FullIdent.createFullIdentBelow(ast);
		// int i = 0;
		// int j = 0;
		currentMethodNameAST = findLastDescendentOfFirstChild(ast);
		// String shortMethodName = currentMethodNameAST.getText();
		shortMethodName = currentMethodNameAST.getText();

		// String aCastType = null;

		DetailAST aLeftMostMethodTargetAST = currentMethodNameAST
				.getPreviousSibling();

		if (aLeftMostMethodTargetAST != null) { // this should be a while?
			while (aLeftMostMethodTargetAST.getType() == TokenTypes.RPAREN) {
				DetailAST anLParen = aLeftMostMethodTargetAST
						.getPreviousSibling();
				if (anLParen.getType() == TokenTypes.RPAREN) {
					aLeftMostMethodTargetAST = anLParen;
					continue;
				} else if (anLParen.getType() == TokenTypes.LPAREN) {
					DetailAST aTypeAST = anLParen.getFirstChild();
					if (typeAST == null) { // this should not happen
						aLeftMostMethodTargetAST = anLParen;
						break;
					} else if (aTypeAST.getType() == TokenTypes.TYPE) {
						// int i = 0;
						// this is a hack, need to search down the tree for
						// parens
						// and find the first
						// idnetifier or indexop in the tree
						aCastType = aTypeAST.getFirstChild().getText();
						DetailAST anRTypeParen = aTypeAST.getNextSibling();
						DetailAST aCastExpression = anRTypeParen
								.getFirstChild();
						aLeftMostMethodTargetAST = aCastExpression;
						break;

					}
				} else if (anLParen.getType() == TokenTypes.IDENT) {
					aLeftMostMethodTargetAST = anLParen;
					break;
				} else {
					break;
				}
				break;
			}
			if (aLeftMostMethodTargetAST != null) {
				while (aLeftMostMethodTargetAST.getType() == TokenTypes.METHOD_CALL) { // target
																						// is
																						// result
																						// of
																						// method
																						// call
					DetailAST down = aLeftMostMethodTargetAST.getFirstChild();
					if (down != null && down.getType() == TokenTypes.DOT)
						aLeftMostMethodTargetAST = down.getFirstChild(); // go
																			// to
																			// next
																			// call
					else
						break;
				}
			}
			// if (aLeftMostMethodTargetAST != null
			// && aLeftMostMethodTargetAST.getType() == TokenTypes.DOT) {
			// // DetailAST aFirstChild =
			// // aLeftMostMethodTargetAST.getFirstChild();
			// if (aLeftMostMethodTargetAST.getFirstChild().getText()
			// .equals("this")) {
			// // System.out.println ("found doot");
			// aLeftMostMethodTargetAST = aLeftMostMethodTargetAST
			// .getLastChild();
			// // aLeftMostMethodTargetAST =
			// // aLeftMostMethodTargetAST.getLastChild();
			// }
			// }
			// not syre we need this while with the previous code, but maybe
			// public variables
			if (aLeftMostMethodTargetAST != null) {
				while (aLeftMostMethodTargetAST.getType() == TokenTypes.DOT) {
					aLeftMostMethodTargetAST = aLeftMostMethodTargetAST
							.getFirstChild();
				}

			}
			// if (aLeftMostMethodTargetAST.getType() ==
			// TokenTypes.LITERAL_THIS) {
			// if (aLeftMostMethodTargetAST.getNextSibling() != null) {
			// aLeftMostMethodTargetAST =
			// aLeftMostMethodTargetAST.getNextSibling() ;
			// }
			// }
			// if (aLeftMostMethodTargetAST.getType() ==
			// TokenTypes.LITERAL_THIS) {
			// aTargetName = shortTypeName;
			// }
			// else
			if (aLeftMostMethodTargetAST == null) {
				aTargetName = aCastType;
			} else if (aLeftMostMethodTargetAST.getType() == TokenTypes.INDEX_OP) {
				aTargetName = aLeftMostMethodTargetAST.getFirstChild()
						.getText() + "[]";
			} else {
				aTargetName = aLeftMostMethodTargetAST.getText();
			}

			// }
			// aLeftMostMethodTargetAST = aMethodNameAST
			// .getPreviousSibling();
			// } else if (ast.getType() == TokenTypes.LITERAL_NEW) {
			// final FullIdent anIdentifierType =
			// FullIdent.createFullIdentBelow(ast);
			// shortMethodName = toShortTypeName(anIdentifierType.getText());
			// aLeftMostMethodTargetAST = null;
			// }
		}
		DetailAST aCallEList = ast.findFirstToken(TokenTypes.ELIST);
		List<DetailAST> aCallParameters = getEListComponents(aCallEList);

		// String shortMethodName = aMethodNameAST.getText();
		// shortMethodName = aMethodNameAST.getText();

		String[] aNormalizedParts = null;
		if (currentTree != aTreeAST) {
			aNormalizedParts = (String[]) astToContinuationData.get(ast);
			if (aNormalizedParts == null) {
				System.err.println("Normalizedname not saved");
			}
			// } else if (aLeftMostMethodTargetAST.getType() ==
			// TokenTypes.STRING_LITERAL) {
		} else {
			String anInstantiatedType = maybeReturnInstantiatedType(ast
					.getFirstChild());
			if (anInstantiatedType != null) {

				aNormalizedParts = new String[] { anInstantiatedType,
						shortMethodName };

			} else {
				FullIdent aFullIdent = FullIdent.createFullIdentBelow(ast);
				String longMethodName = aFullIdent.getText();
				String[] aCallParts;
				if (longMethodName.length() > 0
						&& !Character.isLetter(longMethodName.charAt(0))) {
					aCallParts = new String[] { aTargetName, shortMethodName };
				} else {
					aCallParts = longMethodName.split("\\.");

					// String[] aCallParts = longMethodName.split("\\.");
					if (aTargetName != null && isIdentifier(aTargetName)) {
						aCallParts[0] = aTargetName;
					}
				}
				aNormalizedParts = toNormalizedClassBasedCall(aCallParts);
			}
		}
		String aNormalizedLongName = toLongName(aNormalizedParts);
		String aCallee = toShortTypeName(aNormalizedLongName);
		CallInfo result = new ACallInfo(ast, currentMethodName, new ArrayList(
				currentMethodParameterTypes), aNormalizedParts[0], aCallee,
				aCallParameters, aNormalizedParts, aCastType);
		if (aLeftMostMethodTargetAST != null) {
			// String aTargetName;
			//
			// if (aLeftMostMethodTargetAST.getType() == TokenTypes.INDEX_OP) {
			// aTargetName = aLeftMostMethodTargetAST.getFirstChild()
			// .getText() + "[]";
			// } else {
			// aTargetName = aLeftMostMethodTargetAST.getText();
			// }
			// // String aTargetName = aLeftMostMethodTargetAST.getText();

			if (isGlobal(aTargetName)) {
				List<CallInfo> aVariableCalls = getVariableCalls(aTargetName);
				// CallInfo aCall = new ACallInfo(
				// currentMethodName, aNormalizedParts[0], aNormalizedParts[1],
				// aCallParameters, aNormalizedParts );
				aVariableCalls.add(result);
			}
		}
		astToContinuationData.put(ast, aNormalizedParts);

		return result;

	}

	// do nto really need this, the register method call works just fine!
	public CallInfo registerConstructorCallAndtoNormalizedParts(DetailAST ast,
			DetailAST aTreeAST) {

		String shortMethodName = null;
		if (ast.getText().startsWith("this")) {// cannot find a type
			shortMethodName = toShortTypeName(getFullTypeName(currentTree));
		} else {

			FullIdent anIdentifierType = FullIdent.createFullIdentBelow(ast);
			shortMethodName = toShortTypeName(anIdentifierType.getText());
		}

		// final FullIdent anIdentifierType =
		// FullIdent.createFullIdentBelow(ast);
		// String shortMethodName = toShortTypeName(anIdentifierType.getText());

		DetailAST aCallEList = ast.findFirstToken(TokenTypes.ELIST);
		// currentMethodNameAST = aCallEList; // previous sibling?
		currentMethodNameAST = aCallEList.getPreviousSibling(); // current
																// calling
																// method or
																// called
																// method?

		List<DetailAST> aCallParameters = getEListComponents(aCallEList);

		// String shortMethodName = aMethodNameAST.getText();
		// shortMethodName = aMethodNameAST.getText();

		String[] aNormalizedParts = null;
		if (currentTree != aTreeAST) {
			aNormalizedParts = (String[]) astToContinuationData.get(ast);
			if (aNormalizedParts == null) {
				System.err.println("Normalizedname not saved");
			}
			// } else if (aLeftMostMethodTargetAST.getType() ==
			// TokenTypes.STRING_LITERAL) {
		} else {

			aNormalizedParts = new String[] { shortMethodName, shortMethodName };
		}
		// need to worry about cast at some point I assume
		CallInfo result = new ACallInfo(ast, currentMethodName, new ArrayList(
				currentMethodParameterTypes), aNormalizedParts[0],
				aNormalizedParts[1], aCallParameters, aNormalizedParts, null);

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

	protected void visitMethodCall(DetailAST ast) {
		// if (!checkIncludeExcludeTagsOfCurrentType())
		// return;
		CallInfo aCallInfo = registerMethodCallAndtoNormalizedParts(ast,
				currentTree);
		methodsCalledByCurrentMethod.add(aCallInfo);

		// String[] aNormalizedParts =
		// registerMethodCallAndtoNormalizedParts(ast,
		// currentTree).getNotmalizedCall();
		// methodsCalledByCurrentMethod.add(aNormalizedParts);
	}

	protected void visitConstructorCall(DetailAST ast) {
		if (!checkIncludeExcludeTagsOfCurrentType())
			return;
		CallInfo aCallInfo = registerConstructorCallAndtoNormalizedParts(ast,
				currentTree);
		// String[] aNormalizedParts =
		// registerConstructorCallAndtoNormalizedParts(ast,
		// currentTree).getNotmalizedCall();
		// methodsCalledByCurrentMethod.add(aNormalizedParts);
		methodsCalledByCurrentMethod.add(aCallInfo);
	}

	protected void visitIdent(DetailAST anIdentAST) {
		// if (!checkIncludeExcludeTagsOfCurrentType())
		// return;
		if (currentMethodName == null)
			return;
		String anIdentName = anIdentAST.getText();
		if (!isGlobal(anIdentName))
			return;
		globalsAccessedByCurrentMethod.add(anIdentName);

		boolean isGlobalAssignedVariable = isGlobalAssignedVariable(anIdentAST);
		if (isGlobalAssignedVariable) {
			currentMethodAssignsToGlobalVariable = true; // this now redundant
			globalsAssignedByCurrentMethod.add(anIdentName);

		}

		// if (currentMethodAssignsToGlobalVariable)
		// return;
		//
		//
		// currentMethodAssignsToGlobalVariable =
		// isGlobalAssignedVariable(anIdentAST);

	}

	/*
	 * Code taken from anIdentAST
	 */
	public static boolean isLHSOfAssignment(DetailAST anIdentAST) {
		DetailAST aParentAST = anIdentAST.getParent();
		final int parentType = aParentAST.getType();
		if (aParentAST.getType() == TokenTypes.DOT)
			return isLHSOfAssignment(aParentAST);
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
				|| TokenTypes.BOR_ASSIGN == parentType || TokenTypes.BAND_ASSIGN == parentType) && anIdentAST
				.getParent().getFirstChild() == anIdentAST); // this should be
																// checked
																// first?
	}

	public boolean isGlobalAssignedVariable(DetailAST anIdentAST) {
		String aName = anIdentAST.getText();

		return (lookupLocal(aName) == null || (anIdentAST.getPreviousSibling() != null && anIdentAST
				.getPreviousSibling().getType() == TokenTypes.LITERAL_THIS))
				&& isLHSOfAssignment(anIdentAST);

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
		return shortFileName(astToFileContents.get(aTreeAST).getFilename());
		// String aFileName;
		// if (aTreeAST == currentTree) {
		// aFileName = getFileContents().getFilename();
		// } else {
		// aFileName = astToFileContents.get(aTreeAST)
		// .getFilename();
		// }
		// return shortFileName(aFileName);
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
	
	protected void initializeTypeState() {
		typeTags = emptyNameableList;
		computedTypeTags = emptyNameableList;
		typeScope.clear();
		fullTypeName = null;
		shortTypeName = null;
		isInterface = false;
		isEnum = false;
		isGeneric = false;
		isElaboration = false;
		// stMethods.clear();
		imports.clear();
		globalVariables.clear();
		typesInstantiated.clear();
		typesInstantiatedByCurrentMethod.clear();
		globalVariableToCall.clear();
		globalVariableToType.clear();
		globalVariableToRHS.clear();
		typeTagsInitialized = false;
		propertyNames = emptyArrayList;
		editablePropertyNames = emptyArrayList;
	}

	@Override
	public void doBeginTree(DetailAST ast) {
		super.doBeginTree(ast);
		// method initializations, should be in visit method
		currentMethodName = null;
		currentMethodAssignsToGlobalVariable = false;
		currentMethodScope.clear();
		methodsCalledByCurrentMethod.clear();
		globalsAccessedByCurrentMethod.clear();
		globalsAssignedByCurrentMethod.clear();
		
		// type initializations
		if (!getVisitInnerClasses())
			initializeTypeState();
//		typeTags = emptyNameableList;
//		computedTypeTags = emptyNameableList;
//		typeScope.clear();
//		fullTypeName = null;
//		shortTypeName = null;
//		isInterface = false;
//		isEnum = false;
//		isGeneric = false;
//		isElaboration = false;
//		// stMethods.clear();
//		imports.clear();
//		globalVariables.clear();
//		typesInstantiated.clear();
//		typesInstantiatedByCurrentMethod.clear();
//		globalVariableToCall.clear();
//		globalVariableToType.clear();
//		globalVariableToRHS.clear();
//		typeTagsInitialized = false;
//		propertyNames = emptyArrayList;
//		editablePropertyNames = emptyArrayList;
		
		currentTree = ast;

		
		maybeCleanUpPendingChecks(ast);
		// pendingChecks().clear();
	}

	protected void processMethodAndClassData() {

	}
	
	

	@Override
	public void doFinishTree(DetailAST ast) {
//		if (checkIncludeExcludeTagsOfCurrentType()) {
//			// System.out.println("finish tree called:" + ast + " "
//			// + getFileContents().getFilename());
//			if (currentMethodName != null)
//				processPreviousMethodData();
//			processMethodAndClassData();
//		}
		// this is more efficient
		processDeferredChecks();
		

	}

	public void processDeferredChecks() {
		doPendingChecks();

	}

	// public static boolean isPublicAndInstance(DetailAST methodOrVariableDef)
	// {
	// boolean foundPublic = false;
	// final DetailAST modifiersAst = methodOrVariableDef
	// .findFirstToken(TokenTypes.MODIFIERS);
	// if (modifiersAst.getFirstChild() != null) {
	//
	// for (DetailAST modifier = modifiersAst.getFirstChild(); modifier != null;
	// modifier = modifier
	// .getNextSibling()) {
	// // System.out.println("Checking modifier:" + modifier);
	// if (modifier.getType() == TokenTypes.LITERAL_STATIC) {
	// // System.out.println("Not instance");
	// return false;
	// }
	// if (modifier.getType() == TokenTypes.LITERAL_PUBLIC) {
	// foundPublic = true;
	// }
	//
	// }
	// }
	//
	// return foundPublic;
	// }
	// public static boolean isPublicAndInstance(DetailAST methodOrVariableDef)
	// {
	// return isPublic(methodOrVariableDef)
	// && ! isStatic(methodOrVariableDef);
	// }
	// public static boolean isPublic(DetailAST methodOrVariableDef) {
	// return methodOrVariableDef.branchContains(TokenTypes.LITERAL_PUBLIC);
	//
	// }
	// public static boolean isStatic(DetailAST methodOrVariableDef) {
	// return methodOrVariableDef.branchContains(TokenTypes.LITERAL_STATIC);
	//
	// }
	// public static boolean isFinal(DetailAST methodOrVariableDef) {
	// return methodOrVariableDef.branchContains(TokenTypes.FINAL);
	//
	// }
	// public static boolean isStaticAndNotFinal(DetailAST methodOrVariableDef)
	// {
	// boolean foundStatic = false;
	// final DetailAST modifiersAst = methodOrVariableDef
	// .findFirstToken(TokenTypes.MODIFIERS);
	// if (modifiersAst.getFirstChild() != null) {
	//
	// for (DetailAST modifier = modifiersAst.getFirstChild(); modifier != null;
	// modifier = modifier
	// .getNextSibling()) {
	// // System.out.println("Checking modifier:" + modifier);
	// if (modifier.getType() == TokenTypes.FINAL) {
	// // System.out.println("Not instance");
	// return false;
	// }
	// if (modifier.getType() == TokenTypes.LITERAL_STATIC) {
	// foundStatic = true;
	// }
	//
	// }
	// }
	//
	// return foundStatic;
	// }
	// public static boolean isStaticAndNotFinal(DetailAST methodOrVariableDef)
	// {
	// return isStatic (methodOrVariableDef)
	// && ! isFinal(methodOrVariableDef);
	// }
	public Boolean doPendingCheck(DetailAST ast, DetailAST aTreeAST) {
		return false;
	}

	protected DetailAST checkedTree;

	// pending check stuff
	public void doPendingChecks() {
		// for (List<FullIdent> aPendingTypeUses:astToPendingTypeUses.values())
		// {

		for (DetailAST aPendingAST : astToPendingChecks.keySet()) {
			if (aPendingAST == currentTree)
				continue;
			List<DetailAST> aPendingChecks = astToPendingChecks
					.get(aPendingAST);
			// FileContents aFileContents = astToFileContents.get(anAST);
			// setFileContents(aFileContents);

			if (aPendingChecks.isEmpty())
				continue;
			List<DetailAST> aPendingTypeChecksCopy = new ArrayList(
					aPendingChecks);
			for (DetailAST aPendingCheck : aPendingTypeChecksCopy) {
				specificationVariablesToUnifiedValues.clear();
				checkedTree = aPendingAST;
				// System.out.println ("Doing pending check: " +
				// getName(getEnclosingTypeDeclaration(aPendingCheck)));
				deferLogging();
				if (doPendingCheck(aPendingCheck, aPendingAST) != null) {
					aPendingChecks.remove(aPendingCheck);
					flushLogAndResumeLogging();
				} else {
					clearLogAndResumeLogging();
				}

			}
		}
	}

	protected void maybeAddToPendingTypeChecks(DetailAST ast) {
		int i = 1;
		if (!checkIncludeExcludeTagsOfCurrentType())
			return;
		specificationVariablesToUnifiedValues.clear();
		deferLogging();
		checkedTree = currentTree;
		if (doPendingCheck(ast, currentTree) == null) {
			clearLogAndResumeLogging();
			// System.out.println ("added to pending checks:" +
			// getName(getEnclosingTypeDeclaration(ast)));
			List<DetailAST> aPendingChecks = pendingChecks();
			if (!aPendingChecks.contains(ast))
				aPendingChecks.add(ast);
			// pendingChecks().add(ast);
		} else {
			flushLogAndResumeLogging();
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

	// public static DetailAST getEnclosingMethodDeclaration(DetailAST anAST) {
	// return getEnclosingTokenType(anAST, TokenTypes.METHOD_DEF);
	// }
	//
	// public static DetailAST getEnclosingClassDeclaration(DetailAST anAST) {
	// return getEnclosingTokenType(anAST, TokenTypes.CLASS_DEF);
	// }
	// public static DetailAST getEnclosingInterfaceDeclaration(DetailAST anAST)
	// {
	// return getEnclosingTokenType(anAST, TokenTypes.INTERFACE_DEF);
	// }
	// public static DetailAST getEnclosingTypeDeclaration(DetailAST anAST) {
	// DetailAST aClassDef = getEnclosingClassDeclaration(anAST);
	// if (aClassDef == null)
	// return getEnclosingInterfaceDeclaration(anAST);
	// else
	// return aClassDef;
	// }

	// public static String getEnclosingShortClassName(DetailAST anAST) {
	// return getName(getEnclosingClassDeclaration(anAST));
	// }
	// public static String getEnclosingShortTypeName(DetailAST anAST) {
	// return getName(getEnclosingTypeDeclaration(anAST));
	// }
	// public static String getEnclosingMethodName(DetailAST anAST) {
	// return getName(getEnclosingMethodDeclaration(anAST));
	// }
	// // not physically but logically enclosing
	// public static DetailAST getEnclosingTokenType(DetailAST anAST, int
	// aTokenType) {
	// if (anAST == null) return null;
	// if (anAST.getType() == aTokenType) return anAST;
	// DetailAST aParent = anAST.getParent();
	// if (aParent != null)
	// return getEnclosingTokenType(aParent, aTokenType);
	// return
	// getFirstRightSiblingTokenType(anAST, aTokenType);
	// }
	// public static DetailAST getFirstRightSiblingTokenType(DetailAST anAST,
	// int aTokenType) {
	// if (anAST == null) return null;
	// if (anAST.getType() == aTokenType) return anAST;
	// return getFirstRightSiblingTokenType(anAST.getNextSibling(), aTokenType);
	//
	// }
	public static DetailAST getRoot(DetailAST anAST, int aTokenType) {
		if (anAST == null)
			return null;
		DetailAST aParent = anAST.getParent();
		if (aParent == null)
			return aParent;
		return getRoot(aParent, aTokenType);

	}

	public int lineNo(DetailAST ast, DetailAST aTreeAST) {
		return aTreeAST == currentTree ? ast.getLineNo() : 0;
	}

	public int lineNo(FullIdent aFullIdent, DetailAST aTreeAST) {
		return aTreeAST == currentTree ? aFullIdent.getLineNo() : 0;
	}

	public int columnNo(DetailAST ast, DetailAST aTreeAST) {
		return aTreeAST == currentTree ? ast.getColumnNo() : 0;
	}

	public int columnNo(FullIdent aFullIdent, DetailAST aTreeAST) {
		return aTreeAST == currentTree ? aFullIdent.getColumnNo() : 0;
	}

	// public boolean contains(List<STNameable> aTags, String aTag) {
	// for (STNameable aNameable:aTags) {
	// if (matchesStoredTag(aNameable.getName(), aTag))
	//
	// // if (aNameable.getName().equals(aTag))
	// return true;
	// }
	// return false;
	// }
	//
	// public static String maybeStripQuotes(String aString) {
	// if (aString.indexOf("\"") != -1) // quote rather than named constant
	// return aString.substring(1, aString.length() -1);
	// return aString;
	// }
	// public static Boolean matchesStoredTag(String aStoredTag, String
	// aDescriptor) {
	// return
	// maybeStripQuotes(aStoredTag).equals(maybeStripQuotes(aDescriptor));
	//
	// }
	//
	// public Boolean matchesMyType( String aDescriptor, String aShort) {
	// String aClassName = shortTypeName;
	// if (aDescriptor == null || aDescriptor.length() == 0)
	// return true;
	// if (aDescriptor.startsWith("@")) {
	// String aTag = aDescriptor.substring(1);
	// return contains(typeTags(), aTag);
	// }
	// return aClassName.equals(aDescriptor);
	// }

	// static List<STNameable> emptyList = new ArrayList();
	// public static boolean isArray(String aShortClassName) {
	// return aShortClassName.endsWith("[]");
	// }
	// public static boolean isJavaLangClass(String aShortClassName) {
	// return javaLangClassesSet.contains(aShortClassName);
	// }
	// public static boolean isExternalImport(String aShortClassName) {
	// return externalImports.contains(aShortClassName);
	// }
	// public List<STNameable> getTags(String aShortClassName) {
	// List<STNameable> aTags = emptyList;
	//
	// // these classes have no tags
	// // if ( aShortClassName.endsWith("[]") ||
	// // allKnownImports.contains(aShortClassName) ||
	// // javaLangClassesSet.contains(aShortClassName) ) {
	// // return emptyList;
	// // }
	// if ( isArray(aShortClassName) ||
	// isJavaLangClass(aShortClassName) ) {
	// return emptyList;
	// }
	// if (shortTypeName == null || // guaranteed to not be a pending check
	// aShortClassName.equals(shortTypeName)) {
	// aTags = typeTags();
	// } else {
	// STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
	// .getSTClassByShortName(aShortClassName);
	// if (anSTType == null) {
	// if (isExternalImport(aShortClassName)) // check last as we are not really
	// sure about external
	// return emptyList;
	// return null;
	// }
	// aTags = Arrays.asList(anSTType.getTags());
	// }
	// return aTags;
	//
	// }
	// public Boolean matchesType(String aDescriptor, String aShortClassName) {
	// if (aDescriptor == null || aDescriptor.length() == 0)
	// return true;
	// if (!aDescriptor.startsWith("@")) {
	// return aShortClassName.equals(aDescriptor);
	// }
	// List<STNameable> aTags = getTags(aShortClassName);
	// if (aTags == null)
	// return null;
	// // List<STNameable> aTags = null;
	// // if (shortTypeName == null || // guaranteed to not be a pending check
	// // aShortClassName.equals(shortTypeName)) {
	// // aTags = typeTags();
	// // } else {
	// // STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
	// // .getSTClassByShortName(aShortClassName);
	// // if (anSTType == null)
	// // return null;
	// // aTags = Arrays.asList(anSTType.getTags());
	// // }
	// String aTag = aDescriptor.substring(1);
	//
	// return contains(aTags, aTag);
	// }
	protected boolean isPrefix(String aTarget, List<String> aPrefixes,
			String aSourceClassName) {
		for (String aPrefix : aPrefixes) {
			String[] aPrefixParts = aPrefix.split(TYPE_SEPARATOR);
			if ((aPrefixParts.length == 2)
					&& !matchesTypeUnifying(aPrefixParts[0], aSourceClassName))
				continue; // not relevant
			String aTruePrefix = aPrefixParts.length == 2 ? aPrefixParts[1]
					: aPrefix;
			if (aTarget.startsWith(aTruePrefix)
					|| aTruePrefix.equals(MATCH_ANYTHING))
				return true;
		}
		return false;
	}

	protected Boolean containedInClasses(String aTarget, List<String> aList,
			String aSourceClassName) {
		for (String aMember : aList) {
			String[] aMemberParts = aMember.split(TYPE_SEPARATOR);
			// if ((aMemberParts.length == 2) && !matchesMyType(aMemberParts[0],
			// aSourceClassName))
			Boolean match = matchesTypeUnifying(aMemberParts[0],
					aSourceClassName);
			if (match == null) {
				return null;
			}
			if ((aMemberParts.length == 2) && !match)

				// if ((aMemberParts.length == 2) &&
				// !matchesType(aMemberParts[0], aSourceClassName))
				continue; // not relevant
			String aTrueMember = aMemberParts.length == 2 ? aMemberParts[1]
					: aMember;
			if (aTarget.equals(aTrueMember))
				return true;
		}
		return false;
	}

	// public static DetailAST getLastDescendent(DetailAST ast) {
	// DetailAST result = ast.getFirstChild();
	// while (result.getChildCount() > 0)
	// result = result.getLastChild();
	// return result;
	// }

	// to share with a couple of subclsases
	public void setExcludeStructuredTypes(String[] newVal) {
		excludeStructuredTypes = new HashSet(Arrays.asList(newVal));
	}

	/*
	 * checking if the target of call is an instantiated type
	 */
	public static String maybeReturnInstantiatedType(DetailAST ast) {
		DetailAST aNewAST = ast.findFirstToken(TokenTypes.LITERAL_NEW);
		if (aNewAST != null) {
			DetailAST anIdentAST = aNewAST.findFirstToken(TokenTypes.IDENT);
			if (aNewAST.findFirstToken(TokenTypes.ARRAY_DECLARATOR) != null
					|| aNewAST.findFirstToken(TokenTypes.ARRAY_INIT) != null) {
				return anIdentAST.getText() + "[]";
			} else
				return anIdentAST.getText();
		} else if (ast.findFirstToken(TokenTypes.STRING_LITERAL) != null) {
			return "String";
		} else

			return null;
	}

	public String[] toNormalizedClassBasedCall(String[] aCallParts) {
		if (aCallParts.length == 3 && "this".equals(aCallParts[0])) { // unncessary
																		// this.global
			aCallParts = new String[] { aCallParts[1], aCallParts[2] };
		}
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
				return aCallParts; // static call or sub method scope
			}
		} else {
			return aCallParts; // System.out.println() probabluy
		}
		return aCallPartsList.toArray(new String[0]);
	}

	// static {
	// javaLangClassesSet = new HashSet();
	// for (String aClass:javaLangClasses)
	// javaLangClassesSet.add(aClass);
	// }
	public void visitNew(DetailAST ast) {
		if (ast.findFirstToken(TokenTypes.ELIST) != null)
			visitConstructorCall(ast);
		else if (ast.findFirstToken(TokenTypes.ARRAY_DECLARATOR) != null)
			;
		visitInstantiation(ast);
		// System.out.println ("array declaration");
	}

	public void visitTypeUse(DetailAST ast) {

	}

	public void maybeVisitMethodTags(DetailAST ast) {
		super.maybeVisitMethodTags(ast);
		List<STNameable> aComputedList = new ArrayList(currentMethodTags);
		aComputedList.add(new AnSTNameable(currentMethodNameAST,
				currentMethodName));
		currentMethodComputedTags = aComputedList;
	}

	protected Boolean isStructuredProperty(PropertyInfo aPropertyInfo) {
		String aType = aPropertyInfo.getType();
		STNameable aSetter = aPropertyInfo.getSetter();
		if (aSetter instanceof AnSTMethodFromMethod)
			return false;// external class
		if (isOEAtomic(aType) || aSetter == null)
			return false;
		STType aPropertyType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aType);
		if (aPropertyType != null) {
			if (!aPropertyType.hasSetter())
				return false; // immutable
			STNameable[] aTags = aPropertyType.getComputedTags();
			// STNameable[] aTags = aPropertyType.getAllComputedTags();
			if (excludeStructuredTypes.size() > 0) {
				if (aTags == null)
					return null;
				if (matchesSomeSpecificationTags(Arrays.asList(aTags),
						excludeStructuredTypes))
					return false;
			}
			return true;
		}
		return null;

	}

	protected void log(DetailAST anAST) {
		String aTypeName = getEnclosingShortClassName(anAST);

		log(anAST, currentTree, aTypeName);

	}

	protected void log(DetailAST ast, String... anExplanations) {
		DetailAST aTreeAST = getEnclosingTreeDeclaration(ast);
		// String aTypeName = getEnclosingShortClassName(ast);
		log(msgKey(), ast, aTreeAST, anExplanations);

	}

	// move this up to ComprehensiveVisitCheck
	protected void log(DetailAST ast, DetailAST aTreeAST,
			Object... anExplanations) {

		log(msgKey(), ast, aTreeAST, anExplanations);

	}

	// move this up to ComprehensiveVisitCheck
	protected void log(FullIdent ast, DetailAST aTreeAST,
			Object... anExplanations) {
		// String aSourceName =
		// shortFileName(astToFileContents.get(aTreeAST).getFilename());
		// if (aTreeAST == currentTree) {
		// log(ast.getLineNo(),
		// msgKey(),
		// aMethodName,
		// aSourceName + ":" + ast.getLineNo());
		// } else {
		// log(0, msgKey(), aMethodName,
		// aSourceName + ":"
		// + ast.getLineNo());
		// }
		log(msgKey(), ast, aTreeAST, anExplanations);

	}

	protected String getLongFileName(DetailAST aTreeAST) {
		if (aTreeAST == currentTree)
			return getFileContents().getFilename();
		FileContents aFileContents = astToFileContents.get(aTreeAST);
		if (aFileContents != null) {
			return aFileContents.getFilename();
		}
		aFileContents = STBuilderCheck.getSingleton().getAstToFileContents()
				.get(aTreeAST);
		if (aFileContents != null) {
			return aFileContents.getFilename();
		}
		return "";
	}

	// protected String composeSourceName (String aFileName, int aLineNo) {
	// return "(" + aFileName + anAST.getLineNo() + ")";
	// }
	protected String composeSourceName(String aFileName, int aLineNo) {
		return "(" + aFileName + ":" + aLineNo + ")";
	}

	protected String composeMessageKey(String aMessageKey) {
		return aMessageKey + ":";
	}

	protected Object[] composeArgs(String aMessageKey, DetailAST aTreeAST,
			int aLineNo, Object... anExplanations) {
		String aLongFileName = getLongFileName(aTreeAST);
		//
		//
		// if (aTreeAST == currentTree)
		// String aLongFileName = aTreeAST ==
		// currentTree?getFileContents().getFilename():
		// astToFileContents.get(aTreeAST).getFilename();
		String aSourceName = shortFileName(aLongFileName);
		Object[] anArgs = new String[anExplanations.length + 3];

		// Object[] anArgs = new String[anExplanations.length + 2];
		anArgs[0] = composeMessageKey(aMessageKey);
		anArgs[1] = composeSourceName(aSourceName, aLineNo);
		anArgs[2] = getEnclosingShortClassName(aTreeAST);
		for (int i = 3; i < anArgs.length; i++) {

			// for (int i = 2; i < anArgs.length; i++) {

			// anArgs[i] = anExplanations[i - 2].toString();
			anArgs[i] = anExplanations[i - 3].toString();

		}
		return anArgs;
	}

	protected void log(String aMessageKey, DetailAST ast, DetailAST aTreeAST,
			Object... anExplanations) {
		int i = 0;
		Object[] anArgs = composeArgs(aMessageKey, aTreeAST, ast.getLineNo(),
				anExplanations);

		// String aSourceName =
		// shortFileName(astToFileContents.get(aTreeAST).getFilename());
		if (aTreeAST == currentTree) {
			// if (anExplanation != null) {
			extendibleLog(ast.getLineNo(),
			// ast.getColumnNo(),
			// msgKey(),
					aMessageKey, anArgs
			// aMessageKey,
			// // aSourceName + ":" + ast.getLineNo(),
			// composeSourceName(aSourceName, ast),
			// anExplanations
			);
			// } else {
			// log(ast.getLineNo(),
			// // msgKey(),
			// aMessageKey,
			// aSourceName + ":" + ast.getLineNo());
			// }
		} else {
			// if (anExplanation != null) {
			extendibleLog(0,
			// aSourceName + ":" + ast.getLineNo(),
			// msgKey(),
					aMessageKey, anArgs
			// aMessageKey,
			// composeSourceName(aSourceName, ast),
			// anExplanations
			);
			// } else {
			// log(0,
			// // msgKey(),
			// aMessageKey,
			// aSourceName + ":"
			// + ast.getLineNo());
			// }
		}
	}

	boolean doCheck(STType anSTType) {
		return true;
	}

	protected void log(String aMessageKey, FullIdent ast, DetailAST aTreeAST,
			Object... anExplanations) {
		// if (!checkRoot) {
		// DetailAST anSTTreeAST =
		// getEnclosingTreeDeclaration(aMethod.getAST());
		// String aLongFileName = anSTTreeAST ==
		// STBuilderCheck.getSTBuilderTree()?getFileContents().getFilename():
		//
		// astToFileContents.get(aTreeAST)
		// .getFilename();
		// // make this conform to the superclass logs
		// log(aMethod.getAST().getLineNo(),
		// msgKey(),
		// aMethod.getName(),
		// shortFileName(aLongFileName)
		// );
		// // super.log(aMethod, aMethod.getName());
		// return false;
		// }
		// String aLongFileName = "";
		// if (aTreeAST == currentTree)
		// aLongFileName = getFileContents().getFilename();
		// else if (aTreeAST ==
		// STBuilderCheck.getSingleton().getSTBuilderTree()) {
		// aLongFileName =
		// STBuilderCheck.getSingleton().getAstToFileContents().get(aTreeAST).getFilename();
		// } else {
		// astToFileContents.get(aTreeAST).getFilename();
		// }
		// String aLongFileName = getLongFileName(aTreeAST);
		// //
		// //
		// // if (aTreeAST == currentTree)
		// // String aLongFileName = aTreeAST ==
		// currentTree?getFileContents().getFilename():
		// // astToFileContents.get(aTreeAST).getFilename();
		// String aSourceName =
		// shortFileName(aLongFileName);
		// Object[] anArgs = new String[anExplanations.length + 2];
		// anArgs[0] = aMessageKey;
		// anArgs[1] = composeSourceName(aSourceName, ast);
		// for (int i = 2; i < anArgs.length; i++) {
		// System.out.println("an explnation " + anExplanations[i-2]);
		// anArgs[i] = anExplanations[i-2].toString();
		// }
		Object[] anArgs = composeArgs(aMessageKey, aTreeAST, ast.getLineNo(),
				anExplanations);

		// String aSourceName =
		// shortFileName(astToFileContents.get(aTreeAST).getFilename());
		if (aTreeAST == currentTree) {
			// if (anExplanation != null) {
			extendibleLog(ast.getLineNo(),
			// ast.getColumnNo(),
			// msgKey(),
					aMessageKey, anArgs
			// aMessageKey,
			// // aSourceName + ":" + ast.getLineNo(),
			// composeSourceName(aSourceName, ast),
			// anExplanations
			);
			// } else {
			// log(ast.getLineNo(),
			// // msgKey(),
			// aMessageKey,
			// aSourceName + ":" + ast.getLineNo());
			// }
		} else {
			// if (anExplanation != null) {
			extendibleLog(0,
			// aSourceName + ":" + ast.getLineNo(),
			// msgKey(),
					aMessageKey, anArgs
			// aMessageKey,
			// composeSourceName(aSourceName, ast),
			// anExplanations
			);
			// } else {
			// log(0,
			// // msgKey(),
			// aMessageKey,
			// aSourceName + ":"
			// + ast.getLineNo());
			// }
		}
	}

	// protected void log(String aMessageKey, FullIdent ast, DetailAST aTreeAST,
	// Object ... anExplanations) {
	// String aLongFileName = aTreeAST ==
	// currentTree?getFileContents().getFilename():
	// astToFileContents.get(aTreeAST).getFilename();
	// String aSourceName =
	// shortFileName(aLongFileName);
	// // String aSourceName =
	// // shortFileName(astToFileContents.get(aTreeAST).getFilename());
	// if (aTreeAST == currentTree) {
	// // if (anExplanation != null) {
	// log(ast.getLineNo(),
	// ast.getColumnNo(),
	// // msgKey(),
	// aMessageKey,
	// aSourceName + ":" + ast.getLineNo(),
	// anExplanations
	// );
	// // } else {
	// // log(ast.getLineNo(),
	// //// msgKey(),
	// // aMessageKey,
	// // aSourceName + ":" + ast.getLineNo());
	// // }
	// } else {
	// // if (anExplanation != null) {
	// log(0,
	// aSourceName + ":"
	// + ast.getLineNo(),
	// // msgKey(),
	// aMessageKey,
	// anExplanations
	// );
	// // } else {
	// // log(0,
	// //// msgKey(),
	// // aMessageKey,
	// // aSourceName + ":"
	// // + ast.getLineNo());
	// // }
	// }
	// }
	protected void visitInstantiation(DetailAST ast) {
		final FullIdent anIdentifierType = FullIdent.createFullIdentBelow(ast);
		String anInstantiatedTypeName = anIdentifierType.getText();
		STNameable anInstantiatedNameable = new AnSTNameable(ast,
				anInstantiatedTypeName);
		if (currentMethodName == null)
			typesInstantiated.add(anInstantiatedNameable);
		else
			typesInstantiatedByCurrentMethod.add(anInstantiatedNameable);
	}
	
	

	protected void doLeaveToken(DetailAST ast) {
		switch (ast.getType()) {
		case TokenTypes.METHOD_CALL:
			leaveMethod(ast);
			break;
		case TokenTypes.CTOR_CALL:
			leaveConstructor(ast);
			break;
		case TokenTypes.INTERFACE_DEF:
			leaveInterface(ast);
			break;
		case TokenTypes.CLASS_DEF:
			leaveClass(ast);
			break;
		default:
			// System.err.println(checkAndFileDescription + "Unexpected token");

		}
	}

	protected void doVisitToken(DetailAST ast) {
		// System.out.println("Check called:" + MSG_KEY);
		switch (ast.getType()) {
		case TokenTypes.PACKAGE_DEF:
			visitPackage(ast);
			return;
		case TokenTypes.CLASS_DEF:
			if (fullTypeName == null // outer class
			|| STBuilderCheck.getSingleton().getVisitInnerClasses()) // avoid inner class if we haev visited
										
				visitClass(ast);
			return;
		case TokenTypes.INTERFACE_DEF:
			if (fullTypeName == null // avoid inner class if we have visited
										// outer class
				|| STBuilderCheck.getSingleton().getVisitInnerClasses())

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
			// if (ast.findFirstToken(TokenTypes.ELIST) != null)
			// visitConstructorCall(ast);
			// else if (ast.findFirstToken(TokenTypes.ARRAY_DECLARATOR) != null)
			// System.out.println ("array declaration");
			visitNew(ast);
			return;
		case TokenTypes.METHOD_CALL:
			visitMethodCall(ast);
			return;
		case TokenTypes.CTOR_CALL:
			visitConstructorCall(ast);
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
		case TokenTypes.LITERAL_IF:
			visitLiteralIf(ast);
			return;
		case TokenTypes.LITERAL_WHILE:
			visitLiteralWhile(ast);
			return;
		case TokenTypes.LITERAL_FOR:
			visitLiteralFor(ast);
			return;

		default:
			System.err.println(checkAndFileDescription + "Unexpected token");
		}

	}

	protected void visitLiteralIf(DetailAST anIfAST) {

	}

	protected void visitLiteralWhile(DetailAST anIfAST) {

	}

	protected void visitLiteralFor(DetailAST anIfAST) {

	}

	protected Map<String, String[]> typeToSpecifications = new HashMap<>();

	protected void registerSpecifications(String aType, String[] aSpecification) {
		typeToSpecifications.put(aType, aSpecification);
	}

	public void setExpectedSpecificationOfType(String aPattern) {
		String[] extractTypeAndSpecification = aPattern.split(TYPE_SEPARATOR);
		String aType = extractTypeAndSpecification[0].trim();
		String[] aSpecifications = extractTypeAndSpecification[1].split(SET_MEMBER_SEPARATOR);
		// typeToSpecification.put(aType, aSpecification);
		registerSpecifications(aType, aSpecifications);
	}

	public void setExpectedTypesAndSpecifications(String[] aPatterns) {
		for (String aPattern : aPatterns) {
			setExpectedSpecificationOfType(aPattern);
		}

	}

	public static boolean matchesRegexes(String aText,
			Collection<String> aRegexCollection) {
		for (String aRegex : aRegexCollection) {
			if (aText.matches(aRegex))
				return true;
		}
		return false;
	}

	public static boolean isPublic(DetailAST methodOrVariableDef) {
		return methodOrVariableDef.branchContains(TokenTypes.LITERAL_PUBLIC);

	}

	public static boolean isStatic(DetailAST methodOrVariableDef) {
		return methodOrVariableDef.branchContains(TokenTypes.LITERAL_STATIC);

	}

	public static boolean isFinal(DetailAST methodOrVariableDef) {
		return methodOrVariableDef.branchContains(TokenTypes.FINAL);
	}

	public static boolean isStaticAndNotFinal(DetailAST methodOrVariableDef) {
		return isStatic(methodOrVariableDef) && !isFinal(methodOrVariableDef);
	}

	public static boolean isPublicAndInstance(DetailAST methodOrVariableDef) {
		return isPublic(methodOrVariableDef) && !isStatic(methodOrVariableDef);
	}

	protected boolean returnValueOnMatch() {
		return true;
	}
	// check all on success, check one on failure
	// should this really be stopOnExpectedReturnValue?
	protected boolean stopOnFailure() {
		return true;
	}

}
