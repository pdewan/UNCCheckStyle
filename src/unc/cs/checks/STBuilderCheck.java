package unc.cs.checks;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import unc.cs.symbolTable.AnSTType;
import unc.cs.symbolTable.AnSTMethod;
import unc.cs.symbolTable.AnSTNameable;
import unc.cs.symbolTable.AnSTTypeFromClass;
import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STVariable;
import unc.cs.symbolTable.SymbolTableFactory;
import unc.tools.checkstyle.AConsentFormVetoer;
import unc.tools.checkstyle.CheckStyleLogManagerFactory;
import unc.tools.checkstyle.ProjectDirectoryHolder;
import unc.tools.checkstyle.ProjectSTBuilderHolder;

import com.puppycrawl.tools.checkstyle.api.AnnotationUtility;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

public class STBuilderCheck extends ComprehensiveVisitCheck {
//	public static final String MSG_KEY = "stBuilder";
	public static final String MSG_KEY = "typeDefined";
	public static final String EXPECTED_TYPES = "expectedTypes";
	protected Map<String, Map<String, String[]>> startToSpecification = new HashMap<>();

	public static final String  EMPTY_STRING = "";
//	protected STType currentSTType;
	protected List<STMethod> stMethods = new ArrayList();
	protected Stack<List<STMethod>> stMethodsStack = new Stack();
	protected List<STNameable> derivedTags = new ArrayList();
	protected List<STNameable> configuredTags = new ArrayList();

	protected List<STMethod> stConstructors = new ArrayList();
	protected Stack<List<STMethod>> stConstructorsStack = new Stack();
	



	public static  String configurationFileName;

	public static  String configurationFileFullName;
	public static final Map<String, String> classToConfiguredClass = new HashMap();
	static String[] projectPackagePrefixes = { "assignment", "project",
			"homework", "test", "comp", "proj", "ass", "hw" };
	static String[] externalPackagePrefixes = { "java", "com.google", "com.sun", "org.apache", "org.eclipse", "bus.uigen", "util", "gradingTools" };
	static String[] externalMethodRegularExpressions = { "trace.*"};
	static String[] externalClassRegularExpressions = { ".*utton.*"};

	static int lastSequenceNumberOfExpectedTypes = -1;
	protected String checksName;
	protected  String[] existingClasses = {};
	public  Collection<String> existingClassesShortNamesCollection = new HashSet();
	protected  Collection<String> existingClassesCollection = new HashSet();
	static boolean importsAsExistingClasses = false;
	DetailAST sTBuilderTree = null; // make it non static at some point
	protected static STBuilderCheck latestInstance;
	protected boolean visitInnerClasses = false;
	protected Map<String, String[]> classToSpecifications = new HashMap<>();
	protected Map<String, String[]> interfaceToSpecifications = new HashMap<>();

	protected Map<String, String[]> methodToSpecifications = new HashMap<>();
	protected Map<String, String[]> variableToSpecifications = new HashMap<>();
	protected Map<String, String[]> parameterToSpecifications = new HashMap<>();
	protected boolean existingClassesFilled = false;
	
	// type defined
	protected List<String> expectedTypes = new ArrayList();	
	protected List<String> unmatchedTypes = new ArrayList();
	protected Map<String, String> tagMatches = new HashMap();
//	protected Set<String> matchedTypes = new HashSet();
	protected boolean overlappingTags = true;
	protected boolean logNoMatches = true;
	protected boolean logMethodsDeclared = false;
	protected boolean logVariablesDeclared = false;
	protected boolean logPropertiesDeclared = false;
	
	protected boolean logAggregateStatistics = false;


    protected String methodsDeclaredString;
    protected String variablesDeclaredString;
    protected String propertiesDeclaredString;
    protected String statisticsString;



	public STBuilderCheck() {
		latestInstance = this;
		startToSpecification.put(CLASS_START, classToSpecifications);
		startToSpecification.put(INTERFACE_START, interfaceToSpecifications);
		startToSpecification.put(METHOD_START, methodToSpecifications);
		startToSpecification.put(VARIABLE_START, variableToSpecifications);
		startToSpecification.put(PARAMETER_START, parameterToSpecifications);
//		System.err.println ("Setting checks name to:" + "Assignments" );
//		checksName =  "Assignments";
		checksName =  "CheckStyle_All";
		setCheckOnBuild(true); //make symboltable incrementally
		CheckStyleLogManagerFactory.getOrCreateCheckStyleLogManager().checkStyleStarted();

	}

    public void setConfigurationFileName(String aConfigurationFileName) {
    	configurationFileName = aConfigurationFileName;
    }
	public void setDerivedTypeTags(String[] aDerivedTagsSpecifications) {
		setExpectedTypesAndSpecifications(classToSpecifications, aDerivedTagsSpecifications);
		setExpectedTypesAndSpecifications(interfaceToSpecifications, aDerivedTagsSpecifications);

	}
	public void setDerivedClassTags(String[] aDerivedTagsSpecifications) {
		setExpectedTypesAndSpecifications(classToSpecifications, aDerivedTagsSpecifications);

	}
	public void setDerivedInterfaceTags(String[] aDerivedTagsSpecifications) {
		setExpectedTypesAndSpecifications(interfaceToSpecifications, aDerivedTagsSpecifications);

	}

	public void setDerivedMethodTags(String[] aDerivedTagsSpecifications) {
		setExpectedTypesAndSpecifications(methodToSpecifications, aDerivedTagsSpecifications);
	}
	public void setDerivedVariableTags(String[] aDerivedTagsSpecifications) {
		setExpectedTypesAndSpecifications(variableToSpecifications, aDerivedTagsSpecifications);
	}


	
	

	public void setVisitInnerClasses(boolean newVal) {
		visitInnerClasses = newVal;
	}

	public boolean getVisitInnerClasses() {
		return visitInnerClasses;
	}
	public void setProjectPackagePrefixes(String[] aPrefixes) {
		projectPackagePrefixes = aPrefixes;
	}
	
	public void setExternalPackagePrefixes(String[] aPrefixes) {
		externalPackagePrefixes = aPrefixes;
	}
	public void setExternalMethodRegularExpressions(String[] newVal) {
		externalMethodRegularExpressions = newVal;
	}
	
	public void setExternalTypeRegularExpressions(String[] newVal) {
		externalClassRegularExpressions = newVal;
	}
	
	public static String[] getProjectPackagePrefixes() {
		return projectPackagePrefixes;
	}
	public static String[] getExternalPackagePrefixes() {
		return externalPackagePrefixes;
	}
	public static String[] getExternalMethodRegularExpressions() {
		return externalMethodRegularExpressions;
	}
	public static String[] getExternalTypeRegularExpressions() {
		return externalClassRegularExpressions;
	}

	public void setExistingClasses(String[] aClasses) {
		existingClasses = aClasses;
		existingClassesCollection = Arrays.asList(existingClasses);
//		processExistingClasses();
	}
	
	public void setChecksName(String newValue) {
		checksName = newValue;
		
	}
	public  String getChecksName() {
		return checksName;
	}

	public static boolean getImportsAsExistingClasses() {
		return importsAsExistingClasses;
	}

	public void setImportsAsExistingClasses(boolean aNewVal) {
		importsAsExistingClasses = aNewVal;
	}

	public  String[] getExistingClasses() {
		return existingClasses;
	}
	
	public boolean isLogMethodsDeclared() {
		return logMethodsDeclared;
	}
	public void setLogMethodsDeclared(boolean newVal) {
		 logMethodsDeclared = newVal;
	}
	public boolean isLogVariablesDeclared() {
		return logVariablesDeclared;
	}
	public void setLogVariablesDeclared(boolean newVal) {
		logVariablesDeclared = newVal;
	}
	public boolean isLogPropertiesDeclared() {
		return logPropertiesDeclared;
	}
	public void setLogPropertiesDeclared(boolean newVal) {
		logPropertiesDeclared = newVal;
	}
	
	public boolean isLogAggregateStatistics() {
		return logAggregateStatistics;
	}

	public void setLogAggregateStatistics(boolean logAggregateStatistics) {
		this.logAggregateStatistics = logAggregateStatistics;
	}

	protected void newProjectDirectory(String aNewProjectDirectory) {
		super.newProjectDirectory(aNewProjectDirectory);
		maybeProcessExistingClasses();
		maybeProcessConfigurationFileName();
//		System.err.println ("Clearing symbol table");
//		SymbolTableFactory.getOrCreateSymbolTable().clear();
		
	}
	protected void maybeProcessConfigurationFileName() {
		String aProjectDirectory = ProjectDirectoryHolder.getCurrentProjectDirectory();
		if (aProjectDirectory == null || configurationFileName == null) {
			return;
		}
		configurationFileFullName = aProjectDirectory + "/" + configurationFileName;
		Scanner aScanner;
		try {
			aScanner = new Scanner (new File (configurationFileFullName));
		
		while (aScanner.hasNext()) {
			String aLine = aScanner.nextLine();
			String[] aLineTokens = aLine.split(",");
			if (aLineTokens.length != 2) {
				return;
			}
			classToConfiguredClass.put(aLineTokens[0], aLineTokens[1]);
		}
		} catch (FileNotFoundException e) {
			return;
		}
	}
	protected void maybeProcessExistingClasses() {
		if (existingClassesFilled) {
			return;
		}
		processExistingClasses();
		existingClassesFilled = true;

	}
	protected  void processExistingClasses() {
		for (String aClassName : existingClasses) {
			existingClassesShortNamesCollection
					.add(toShortTypeOrVariableName(aClassName));
			processExistingClass(aClassName);
			// if
			// (SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aClassName)
			// != null)
			// continue;
			// try {
			// Class aClass = Class.forName(aClassName);
			// STType anSTType = new AnSTTypeFromClass(aClass);
			// addSTType(anSTType);
			// } catch (ClassNotFoundException e) {
			// System.err.println ("Unknown class existing clas: " +
			// aClassName);
			// e.printStackTrace();
			// }
		}
	}

	public static STBuilderCheck getLatestInstance() {
		return latestInstance;
	}
//	protected Map<String, String> importShortToLongName = new HashMap();
	protected void processImports() {
		if (!getImportsAsExistingClasses())
			return;
		for (STNameable aClassName : allImportsOfThisClass) {
			String aLongName = aClassName.getName();
			// star imports?
			String aShortName = toShortTypeOrVariableName(aLongName);
//			if (aShortName != null && !aShortName.isEmpty()) {
//			importShortToLongName.put(aShortName, aLongName);
//			}
//			if (TagBasedCheck.isProjectImport(aClassName.getName()))
//				continue;
			if (isExternalImportCacheChecking(aClassName.getName()))
				processExistingClass(aClassName.getName());
		}
	}

	public Collection<String> getExistingClassShortNameCollection() {
		return existingClassesShortNamesCollection;
	}

	protected  void processExistingClass(String aClassName) {
		if (aClassName.endsWith("*"))
			return;
		if (SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(
				aClassName) != null)
			return;
		try {
			Class aClass = Class.forName(aClassName);
			STType anSTType = new AnSTTypeFromClass(aClass);
			anSTType.setExternal(true);
			addAndUpateCurrentSTTType(anSTType);
		} catch (ClassNotFoundException e) {
//			if (existingClassesCollection.contains(aClassName))
//				System.err.println("Could not make existing class from: "
//						+ aClassName );
			STType anSTType = new AnSTTypeFromClass(aClassName);
			anSTType.setExternal(true);

			addAndUpateCurrentSTTType(anSTType);
			// e.printStackTrace();
		}

	}
//	public static List isGlobalDeclaredMethod(STMethod[] anSTMethods, CallInfo aCallInfo) {
//		for (STMethod aMethod : anSTMethods) {
//			if (aMethod.getName().equals(aCallInfo.getCallee()))
//				return true;
//		}
//		return false;
//	}
	protected void processPreviousMethodData() {
		if (currentMethodName != null) {
			String[] aParameterTypes = currentMethodParameterTypes
					.toArray(new String[0]);
			String[] aLongParameterTypes = toLongTypeNames(aParameterTypes);

			String[] aParameterNames = currentMethodParameterNames
					.toArray(new String[0]);
			STMethod anSTMethod = new AnSTMethod(
					currentMethodAST,
					currentMethodName,
					fullTypeName,
					aParameterNames,
					aLongParameterTypes,
					currentMethodIsPublic || isInterface,
					currentMethodIsInstance || isInterface,
					currentMethodIsConstructor,
					currentMethodIsSynchronized,
					currentMethodType,
					currentMethodIsVisible,
					currentMethodTags.toArray(dummyArray),
					// currentMethodComputedTags.toArray(dummyArray),
					computedAndDerivedMethodTags().toArray(dummyArray),
					currentMethodAssignsToGlobalVariable,
					// methodsCalledByCurrentMethod.toArray(new String[0][0]));
					methodsCalledByCurrentMethod.toArray(new CallInfo[0]),
					new ArrayList(typesInstantiatedByCurrentMethod),
					new ArrayList(globalsAccessedByCurrentMethod),
					new ArrayList(globalsAssignedByCurrentMethod),
					new ArrayList(unknownVariablesAccessedByCurrentMethod),
					new ArrayList(unknownVariablesAssignedByCurrentMethod),
					new ArrayList(localSTVariables),
					new ArrayList(parameterSTVariables),
					new ArrayList(localsAssignedByCurrentMethod),
					new ArrayList(parametersAssignedByCurrentMethod),
					getAccessToken(currentMethodAST),
					numberOfTernaryIfsInCurrentMethod,
					new ArrayList(assertsInCurrentMethod)
					);

			if (currentMethodIsConstructor)
				stConstructors.add(anSTMethod);
			else
				stMethods.add(anSTMethod);
		}
		currentMethodName = null;

	}
	
	
	protected String computePropertiesDeclaredString() {
    	return !isLogPropertiesDeclared()?
    			EMPTY_STRING:
    				toPropertiesDeclaredString(currentSTType);
    		
    }
	protected String computeStatisticsString() {
    	return !isLogAggregateStatistics()?
    			EMPTY_STRING:
    				" Number of Asserts:" + currentSTType.getNumberOfAsserts() +
    				" Number of Ternary Conditionals:" + currentSTType.getNumberOfTernaryConditionals() +
    				" Number of Methods:" + currentSTType.getNumberOfMethods() +
    				" Number of Functions:" + currentSTType.getNumberOfFunctions() +
    				" Number of Non Getter Functions:" + currentSTType.getNumberOfNonGetterFunctions() +
    				" Number of Getters and Setters:" + currentSTType.getNumberOfGettersAndSetters();

    		
    }
    protected String computeMethodsDeclaredString() {
    	return !isLogMethodsDeclared()?
    			EMPTY_STRING:
    				toMethodsDeclaredString(currentSTType);
    		
    }
    protected String computeVariablesDeclaredString() {
    	return !isLogVariablesDeclared()?
    			EMPTY_STRING:
    				toVariablesDeclaredString(currentSTType);
    		
    }
    protected String getStatisticsString() {
    	if (statisticsString == null) {
    		statisticsString = computeStatisticsString();
    	}
    	return statisticsString;
    }
    protected String getPropertiesDeclaredString() {
    	if (propertiesDeclaredString == null) {
    		propertiesDeclaredString = computePropertiesDeclaredString();
    	}
    	return propertiesDeclaredString;
    }
    protected String getMethodsDeclaredString() {
    	if (methodsDeclaredString == null) {
    		methodsDeclaredString = computeMethodsDeclaredString();
    	}
    	return methodsDeclaredString;
    }
    protected String getVariablesDeclaredString() {
    	if (variablesDeclaredString == null) {
    		variablesDeclaredString = computeVariablesDeclaredString();
    	}
    	return variablesDeclaredString;
    }
	@Override
	public void doFinishTree(DetailAST ast) {
		if (!getVisitInnerClasses() && checkIncludeExcludeTagsOfCurrentType()) {
			// System.err.println("finish tree called:" + ast + " "
			// + getFileContents().getFilename());
			if (currentMethodName != null)
				processPreviousMethodData();
			processMethodAndClassData();
		}
		checkTags(ast); // want to check tags after symbol table built
		super.doFinishTree(ast);
//		super.log(ast, "testing st builder");

	}

	public void doBeginTree(DetailAST ast) {
		super.doBeginTree(ast);
		astToFileContents.put(ast, getFileContents());
		// System.err.println("STBuilder" + checkAndFileDescription);
		currentSTType = null;
		if (!ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {
			stMethods.clear();
			stConstructors.clear();
		}
		sTBuilderTree = ast;
		// print once per each sequence number
//		if (isNewSequenceNumber() && !isAutoBuild()) {
		if (sequenceNumber > lastSequenceNumberOfExpectedTypes && !isAutoBuild()) {
			
			if (expectedTypes.size() > 0) {

    		extendibleLog(0,
    				
    				EXPECTED_TYPES, new Object[] {EXPECTED_TYPES + ":", expectedTypes.toString()}
			
			);
			}
    		lastSequenceNumberOfExpectedTypes = sequenceNumber;
    		
    	}

	}

	@Override
	public void visitType(DetailAST ast) {
		super.visitType(ast);
		if (getVisitInnerClasses()) {
			// we want them stacked so allocate data structures
			stMethods = new ArrayList();
			stConstructors = new ArrayList();
			stMethodsStack.push(stMethods);
			stConstructorsStack.push(stConstructors);
			typeTagsInitialized = false; // recompute them

		}
		visitClassOrInterface(ast);
	}

	@Override
	protected void leaveClass(DetailAST ast) {
		super.leaveClass(ast);
	}

	@Override
	protected void leaveInterface(DetailAST ast) {
		super.leaveInterface(ast);
	}

	@Override
	public void leaveType(DetailAST ast) {
		if (!getVisitInnerClasses())
			return;
		if (checkIncludeExcludeTagsOfCurrentType()) {
			// System.err.println("finish tree called:" + ast + " "
			// + getFileContents().getFilename());
			if (currentMethodName != null)
				processPreviousMethodData();
			processMethodAndClassData();
		}
		super.leaveType(ast);
		if (getVisitInnerClasses()) {
			stMethods = myPop(stMethodsStack);
			// stMethodsStack.pop();
			// stMethods = stMethodsStack.peek();
			stConstructors = myPop(stConstructorsStack);

			// stConstructorsStack.pop();
			// stConstructors = stConstructorsStack.peek();
		}

	}

	public DetailAST getSTBuilderTree() {
		return sTBuilderTree;
	}

	STNameable[] dummyArray = new STNameable[0];
	
	protected void addAndUpateCurrentSTTType(STType anSTType) {
		addSTType(anSTType);
		currentSTType = anSTType;
	}
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
//	
//		SymbolTableFactory.getOrCreateSymbolTable().getTypeNameToSTClass()
//				.put(aName, anSTClass);
		
		SymbolTableFactory.getOrCreateSymbolTable()
		.putSTType(aName, anSTClass);
		
		

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
		// isEnum = true;
		String anEnumName = getEnumName(anEnumDef);
		String aFullName = packageName + "." + shortTypeName + "." + anEnumName;
		STType anSTClass = new AnSTType(anEnumDef, anEnumName, emptyMethods,
				emptyMethods, emptyTypes, null, packageName, false, false,
				false, true, null, dummyArray, dummyArray, dummyArray,
				dummyArray, dummyArray, dummyArray, dummyArray, dummyArray,
				new HashMap(),
//				new HashMap(), 
//				new HashMap(), 
				new ArrayList(), 
				new ArrayList(),
				new HashMap(),
				new HashMap()
				);

		// anSTClass.introspect();
		// anSTClass.findDelegateTypes();
		// SymbolTableFactory.getOrCreateSymbolTable().getTypeNameToSTClass().put(
		// fullTypeName, anSTClass);
		addAndUpateCurrentSTTType(anSTClass);

		// shortTypeName = anEnumDef.getNextSibling().toString();
		// DetailAST anEnumIdent =
		// anEnumDef.getNextSibling().findFirstToken(TokenTypes.IDENT);
		// if (anEnumIdent == null) {
		// System.err.println("null enum ident");
		// }
		// shortTypeName = anEnumIdent.getText();
	}

	// protected static String getEnumName(DetailAST anEnumDef) {
	// return getEnumAST(anEnumDef).toString();
	// }
	// protected static DetailAST getEnumAST(DetailAST anEnumDef) {
	// return anEnumDef.getNextSibling();
	// }

	public  boolean isDerivedTag (DetailAST anAST, String aText, String[] aPatterns, String aPatternPrefix) {
		int i = 0;
		for (String aPattern: aPatterns) {
			String anActualPattern = maybeStripComment(aPattern);
			if (anActualPattern.startsWith(TAG_STRING)) {
				if (!(anAST.getType() == TokenTypes.CLASS_DEF || anAST.getType() == TokenTypes.INTERFACE_DEF)) {
					return false;
				}
				if (!hasTaggedMember(anActualPattern)) {
					return false;
				} 
			} else if (!aText.matches(aPatternPrefix + anActualPattern)) {
				return false; // expecrt all patterns 
			}
		}
		return true;
	}
//	public  boolean isDerivedTag (DetailAST anAST, String aText, String[] aPatterns, String aPatternPrefix) {
//		int i = 0;
//		for (String aPattern: aPatterns) {
//			String anActualPattern = maybeStripComment(aPattern);
//			if (anActualPattern.startsWith(TAG_STRING)) {
//				if (!(anAST.getType() == TokenTypes.CLASS_DEF || anAST.getType() == TokenTypes.INTERFACE_DEF)) {
//					return false;
//				}
//				if (!hasTaggedMethod(anActualPattern)) {
//					return false;
//				} 
//			} else if (!aText.matches(anActualPattern)) {
//				return false; // expecrt all patterns 
//			}
//		}
//		return true;
//	}

	protected List<STNameable> computedAndDerivedTypeTags() {
		List<STNameable> result = computedTypeTags();
		List<STNameable> derivedTags = derivedTags(typeAST, isInterface?INTERFACE_START:CLASS_START);
		String aConfiguredName = classToConfiguredClass.get(shortTypeName);
		
		addAllNoDuplicates(result, new HashSet(derivedTags));
		configuredTags.clear();
		if (aConfiguredName != null) {
			STNameable aNameable = new AnSTNameable(aConfiguredName);
			configuredTags.add(aNameable);
			result.add(aNameable);
		}
		return result;
	}

	protected List<STNameable> computedAndDerivedMethodTags() {
		List<STNameable> result = currentMethodComputedTags;
		List<STNameable> derivedTags = derivedTags(currentMethodAST, METHOD_START);
		addAllNoDuplicates(result, new HashSet(derivedTags));
		return result;
	}
	@Override
	protected  List<STNameable> getAllTags(DetailAST anAST, DetailAST aNameAST, String aTypeName, String aStart ) {
		return getComputedDerivedAndExplicitTags(anAST, aNameAST, aTypeName, aStart);
	}
	protected  List<STNameable> getComputedDerivedAndExplicitTags(DetailAST anAST, DetailAST aNameAST, String aTypeName, String aStart ) {
		List<STNameable> result = getComputedAndExplicitTags(anAST, aNameAST, aTypeName);
		List<STNameable> derivedTags = derivedTags(anAST, aStart);
		addAllNoDuplicates(result, new HashSet (derivedTags));
		return result;
	}

	protected List<STNameable> derivedTags(DetailAST anAST, String aPatternPrefix) {
		derivedTags.clear();
		Map<String, String[]> constructToSpecifications = startToSpecification.get(aPatternPrefix);
		if (constructToSpecifications.isEmpty()) {
			return derivedTags;
		}
		String aText = toStringList(anAST).trim();
		for (String aKey : constructToSpecifications.keySet()) {
			if (isDerivedTag(anAST, aText, constructToSpecifications.get(aKey), aPatternPrefix)) {
				derivedTags.add(new AnSTNameable(aKey));
			}
		}
		return derivedTags;

	}

	protected STMethod getTaggedMethod(String aTag) {
		for (STMethod aMethod : stMethods) {
			if (hasTag(aMethod.getComputedTags(), aTag)) {
				return aMethod;
			}
		}
		return null;
	}
	protected STVariable getTaggedVariable(String aTag, List<STVariable> aVariables) {
		for (STVariable aVariable : aVariables) {
			if (hasTag(aVariable.getTags(), aTag)) {
				return aVariable;
			}
		}
		return null;
	}
	protected boolean hasTaggedVariable(String aTag, List<STVariable> aVariables) {
		return getTaggedVariable(aTag, aVariables) != null;
	}
	
	protected boolean hasTaggedMember(String aTag) {
		return hasTaggedMethod(aTag) || hasTaggedVariable(aTag, globalSTVariables);
	}

	protected boolean hasTaggedMethod(String aTag) {
		return getTaggedMethod(aTag) != null;
	}

	protected void processMethodAndClassData() {
		processImports();
		STMethod[] aMethods = stMethods.toArray(new STMethod[0]);
		STMethod[] aConstructors = stConstructors.toArray(new STMethod[0]);
		STType anSTClass = new AnSTType(
				typeAST,
				fullTypeName, // may be an inner class
				aMethods, aConstructors, interfaces, superClass, packageName,
				isInterface, isGeneric, isElaboration, isEnum,
				structurePattern, propertyNames.toArray(dummyArray),
				editablePropertyNames.toArray(dummyArray), 
				typeTags().toArray(
						dummyArray),				
				// computedTypeTags().toArray(dummyArray),
				computedAndDerivedTypeTags().toArray(dummyArray),
				configuredTags.toArray(dummyArray),
				derivedTags.toArray(dummyArray),
				allImportsOfThisClass.toArray(dummyArray),
				globalVariables.toArray(dummyArray), 
				new HashMap<>(	globalVariableToCall), 
//				new HashMap<>(globalVariableToType),
//				new HashMap<>(globalVariableToRHS),
				new ArrayList<>(typesInstantiated),
				new ArrayList(globalSTVariables),
				new HashMap<>(globalIdentToLHS),
				new HashMap<>(globalIdentToRHS)
				);

		// anSTClass.introspect();
		// anSTClass.findDelegateTypes();
		// SymbolTableFactory.getOrCreateSymbolTable().getTypeNameToSTClass().put(
		// fullTypeName, anSTClass);
		addAndUpateCurrentSTTType(anSTClass);
		// log (typeNameAST.getLineNo(), msgKey(), fullTypeName);
		// if (!defined) {
		// // log(ast.getLineNo(), MSG_KEY);
		// }

	}

	public static void addKnownClass(Class aClass) {

	}

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}

	public void doVisitToken(DetailAST ast) {
		super.doVisitToken(ast);
	}
	// from type visited
	public void setExpectedTypes(String[] anExpectedClasses) {
		expectedTypes = Arrays.asList(anExpectedClasses);
		unmatchedTypes = new ArrayList(expectedTypes);		
	}
	
	public void setOverlappingTags(boolean newVal) {
		overlappingTags = newVal;		
	}
	
	public void setLogNoMatches(boolean newVal) {
		logNoMatches = newVal;
	}
	public void setLogMethodsDefined(boolean newVal) {
		logNoMatches = newVal;
	}
	public void checkTags(DetailAST ast) {
		List<String> checkTags = new ArrayList( overlappingTags?expectedTypes:unmatchedTypes);
//    	System.err.println("Checking full type name: " + fullTypeName);
    	if (tagMatches.containsKey(fullTypeName)) {
    		tagMatches.remove(fullTypeName);
    		if (!overlappingTags) {
    			unmatchedTypes.remove(tagMatches.get(fullTypeName));
    		}
    	}
    	
    	boolean aFoundMatch = false;
		String aClassOrInterface = isInterface?"Interface":"Class";

    	for (String anExpectedClassOrTag:checkTags) {
    		if ( matchesMyType(maybeStripComment(anExpectedClassOrTag))) {
    			tagMatches.put(fullTypeName, anExpectedClassOrTag);
//    			matchedTypes.add(fullTypeName);
    			unmatchedTypes.remove(anExpectedClassOrTag);
//    			if (shownMissingClasses) {
//    				log("expectedTypes", ast, ast, expectedTypes.toString().replaceAll(",", " "));
//        			shownMissingClasses = false;
//
//    			} 
//    			else {

//    			log(ast, anExpectedClassOrTag, unmatchedTypes.toString().replaceAll(",", " "));
//    			String aClassOrInterface = isInterface?"Interface":"Class";
//    			System.err.println ("STBuilder:" + aClassOrInterface + " " + anExpectedClassOrTag);
    			log(ast, anExpectedClassOrTag, aClassOrInterface, getStatisticsString(), getMethodsDeclaredString(), getVariablesDeclaredString(), getPropertiesDeclaredString());
    			aFoundMatch = true;
//
//    			}
    		}
    		
    	}
    	if (!aFoundMatch && logNoMatches) {
    		
			log(ast, "None", aClassOrInterface, getStatisticsString(), getMethodsDeclaredString(), getVariablesDeclaredString(), getPropertiesDeclaredString());
		}
//    		
//    		
//    	}
//    	
//    	for (String anExpectedClassOrTag:expectedClasses) {
//    		if ( matchesMyType(anExpectedClassOrTag)) {
////    			DetailAST aTypeAST = getEnclosingClassDeclaration(currentTree);
//    			log(currentTree, msgKey(), shortTypeName, expectedClasses.toString());
//    		}
//    		
//    		
//    	}
	}
    public void visitClassOrInterface(DetailAST ast) {  
		
//    	super.visitType(ast);
//    	if (fullTypeName.contains("ListImp")) {
//    		System.err.println ("found inner interface");
//    	}
//    	if (shownMissingClasses) {
//			log("expectedTypes", ast, ast, expectedTypes.toString().replaceAll(",", " "));
//			shownMissingClasses = false;
//
//		} 
    	// do not print on autobuild, only batch build
//    	if (isNewSequenceNumber() && !isAutoBuild()) {
//    		extendibleLog(0,
//    				
//    				EXPECTED_TYPES, new Object[] {EXPECTED_TYPES, expectedTypes.toString()}
//			
//			);
//    		
//    	}
    	// What does thie mean when we have not computed tags?
    	// should we not use the symbol table
    	Boolean check = checkIncludeExcludeTagsOfCurrentType();
    	if (check == null)
    		return;
    	if (!check)
    		return;
//    	List<String> checkTags = new ArrayList( overlappingTags?expectedTypes:unmatchedTypes);
////    	System.err.println("Checking full type name: " + fullTypeName);
//    	if (tagMatches.containsKey(fullTypeName)) {
//    		tagMatches.remove(fullTypeName);
//    		if (!overlappingTags) {
//    			unmatchedTypes.remove(tagMatches.get(fullTypeName));
//    		}
//    	}
//    	
//    	boolean aFoundMatch = false;
//    	for (String anExpectedClassOrTag:checkTags) {
//    		if ( matchesMyType(maybeStripComment(anExpectedClassOrTag))) {
//    			tagMatches.put(fullTypeName, anExpectedClassOrTag);
////    			matchedTypes.add(fullTypeName);
//    			unmatchedTypes.remove(anExpectedClassOrTag);
////    			if (shownMissingClasses) {
////    				log("expectedTypes", ast, ast, expectedTypes.toString().replaceAll(",", " "));
////        			shownMissingClasses = false;
////
////    			} 
////    			else {
//
////    			log(ast, anExpectedClassOrTag, unmatchedTypes.toString().replaceAll(",", " "));
//    			log(ast, anExpectedClassOrTag);
//    			aFoundMatch = true;
////
////    			}
//    		}
//    		
//    	}
//    	if (!aFoundMatch && logNoMacthes) {
//			log(ast, "No Expected Tag");
//		}
////    		
////    		
////    	}
////    	
////    	for (String anExpectedClassOrTag:expectedClasses) {
////    		if ( matchesMyType(anExpectedClassOrTag)) {
//////    			DetailAST aTypeAST = getEnclosingClassDeclaration(currentTree);
////    			log(currentTree, msgKey(), shortTypeName, expectedClasses.toString());
////    		}
////    		
////    		
////    	}


    }

	public static void main(String[] args) {
		System.err.println("[200]".matches("(.*)\\[(.*)\\](.*)"));
		//METHOD_DEF public static void main ( [ String ] args ) { EXPR new java . util . ArrayList ( ) ; VARIABLE_DEF Queue qL EXPR new Queue ( EXPR "links" ) = ; VARIABLE_DEF Queue qA EXPR new Queue ( EXPR "array" ) = ; VARIABLE_DEF String item EXPR [ args EXPR 0 ] = ; EXPR ( qL . enq EXPR item ) ; EXPR ( qA . enq EXPR item ) ; EXPR ( System . out . println EXPR ( qL . numElements ) ) ; EXPR ( System . out . println EXPR ( qA . numElements ) ) ; EXPR ( System . out . println EXPR ( qL . front ) ) ; EXPR ( System . out . println EXPR ( qA . front ) ) ; EXPR ( qL . deq ) ; EXPR ( qA . deq ) ; EXPR ( System . out . println EXPR ( qL . numElements ) ) ; EXPR ( System . out . println EXPR ( qA . numElements ) ) ; for ( FOR_INIT VARIABLE_DEF int i EXPR 1 = ; FOR_CONDITION EXPR < i args . length ; FOR_ITERATOR EXPR ++ i ) { EXPR ( qL . enq EXPR [ args EXPR i ] ) ; EXPR ( qA . enq EXPR [ args EXPR i ] ) ; } for ( FOR_INIT VARIABLE_DEF int i EXPR 1 = ; FOR_CONDITION EXPR < i args . length ; FOR_ITERATOR EXPR ++ i ) { EXPR ( System . out . println EXPR ( qL . front ) ) ; EXPR ( System . out . println EXPR ( qA . front ) ) ; EXPR ( qL . deq ) ; EXPR ( qA . deq ) ; EXPR ( System . out . println EXPR ( qL . numElements ) ) ; EXPR ( System . out . println EXPR ( qA . numElements ) ) ; } }
		//METHOD_DEF public int numElements ( ) { return EXPR ( imp . size ) ;
		System.err.println ("METHOD_DEF public int numElements ( ) { return EXPR ( imp . size ) ;".
				matches("METHOD_DEF .* int (size|numElements).*"));
		System.err.println ("METHOD_DEF public static| void main ( [ String ] args ) { EXPR new java . util . ArrayList ( ) ; VARIABLE_DEF Queue qL EXPR new Queue ( EXPR \"links\" ) = ; VARIABLE_DEF Queue qA EXPR new Queue ( EXPR \"array\" ) = ; VARIABLE_DEF String item EXPR [ args EXPR 0 ] = ; EXPR ( qL . enq EXPR item ) ; EXPR ( qA . enq EXPR item ) ; EXPR ( System . out . println EXPR ( qL . numElements ) ) ; EXPR ( System . out . println EXPR ( qA . numElements ) ) ; EXPR ( System . out . println EXPR ( qL . front ) ) ; EXPR ( System . out . println EXPR ( qA . front ) ) ; EXPR ( qL . deq ) ; EXPR ( qA . deq ) ; EXPR ( System . out . println EXPR ( qL . numElements ) ) ; EXPR ( System . out . println EXPR ( qA . numElements ) ) ; for ( FOR_INIT VARIABLE_DEF int i EXPR 1 = ; FOR_CONDITION EXPR < i args . length ; FOR_ITERATOR EXPR ++ i ) { EXPR ( qL . enq EXPR [ args EXPR i ] ) ; EXPR ( qA . enq EXPR [ args EXPR i ] ) ; } for ( FOR_INIT VARIABLE_DEF int i EXPR 1 = ; FOR_CONDITION EXPR < i args . length ; FOR_ITERATOR EXPR ++ i ) { EXPR ( System . out . println EXPR ( qL . front ) ) ; EXPR ( System . out . println EXPR ( qA . front ) ) ; EXPR ( qL . deq ) ; EXPR ( qA . deq ) ; EXPR ( System . out . println EXPR ( qL . numElements ) ) ; EXPR ( System . out . println EXPR ( qA . numElements ) ) ; } }".

//				matches("METHOD_DEF [(public static)|(static public)] void main.*"));
		matches("METHOD_DEF .* void main.*"));
//METHOD_DEF public boolean empty ( ) { return EXPR ( imp . empty ) ; } }
//		METHOD_DEF int size ( ) ;
		System.err.println ("VARIABLE_DEF private [ String ]".matches("VARIABLE_DEF (private |protected )*\\[ String \\].*"));
		
		
	}
	

}
