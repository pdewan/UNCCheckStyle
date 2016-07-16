package unc.cs.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import unc.tools.checkstyle.ProjectSTBuilderHolder;

import com.puppycrawl.tools.checkstyle.api.AnnotationUtility;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

public class CopyOfSTBuilderCheck extends ComprehensiveVisitCheck {

	protected Map<String, Map<String, String[]>> startToSpecification = new HashMap<>();


	protected STType currentSTType;
	protected List<STMethod> stMethods = new ArrayList();
	protected Stack<List<STMethod>> stMethodsStack = new Stack();
	protected List<STNameable> derivedTags = new ArrayList();

	protected List<STMethod> stConstructors = new ArrayList();
	protected Stack<List<STMethod>> stConstructorsStack = new Stack();

	public static final String MSG_KEY = "stBuilder";
	static String[] projectPackagePrefixes = { "assignment", "project",
			"homework" };
	protected String checksName;
	protected  String[] existingClasses = {};
	public  Collection<String> existingClassesShortNamesCollection = new HashSet();
	protected  Collection<String> existingClassesCollection = new HashSet();
	boolean importsAsExistingClasses = false;
	DetailAST sTBuilderTree = null; // make it non static at some point
	protected static CopyOfSTBuilderCheck latestInstance;
	protected boolean visitInnerClasses = false;
	protected Map<String, String[]> classToSpecifications = new HashMap<>();
	protected Map<String, String[]> interfaceToSpecifications = new HashMap<>();

	protected Map<String, String[]> methodToSpecifications = new HashMap<>();
	protected Map<String, String[]> variableToSpecifications = new HashMap<>();
	protected Map<String, String[]> parameterToSpecifications = new HashMap<>();
	protected boolean existingClassesFilled = false;


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


	public CopyOfSTBuilderCheck() {
		latestInstance = this;
		startToSpecification.put(CLASS_START, classToSpecifications);
		startToSpecification.put(INTERFACE_START, interfaceToSpecifications);
		startToSpecification.put(METHOD_START, methodToSpecifications);
		startToSpecification.put(VARIABLE_START, variableToSpecifications);
		startToSpecification.put(PARAMETER_START, parameterToSpecifications);
		System.out.println ("Setting checks name to:" + "Assignments" );
		checksName =  "Assignments";;
		setCheckOnBuild(true); //make symboltable incrementally
		CheckStyleLogManagerFactory.getOrCreateCheckStyleLogManager().checkStyleStarted();

	}
	protected void newProjectDirectory(String aNewProjectDirectory) {
		super.newProjectDirectory(aNewProjectDirectory);
		maybeProcessExistingClasses();
//		System.out.println ("Clearing symbol table");
//		SymbolTableFactory.getOrCreateSymbolTable().clear();
		
	}

	public void setVisitInnerClasses(boolean newVal) {
		visitInnerClasses = newVal;
	}

	public boolean getVisitInnerClasses() {
		return visitInnerClasses;
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
					.add(toShortTypeName(aClassName));
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

	public static CopyOfSTBuilderCheck getLatestInstance() {
		return latestInstance;
	}

	protected void processImports() {
		if (!getImportsAsExistingClasses())
			return;
		for (STNameable aClassName : imports) {
			if (TagBasedCheck.isProjectImport(aClassName.getName()))
				continue;
			processExistingClass(aClassName.getName());
		}
	}

	public Collection<String> getExistingClassShortNameCollection() {
		return existingClassesShortNamesCollection;
	}

	protected  void processExistingClass(String aClassName) {
		if (SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(
				aClassName) != null)
			return;
		try {
			Class aClass = Class.forName(aClassName);
			STType anSTType = new AnSTTypeFromClass(aClass);
			addSTType(anSTType);
		} catch (ClassNotFoundException e) {
			if (existingClassesCollection.contains(aClassName))
				System.err.println("Could not make existing class from: "
						+ aClassName);
			// e.printStackTrace();
		}

	}

	protected void processPreviousMethodData() {
		if (currentMethodName != null) {
			String[] aParameterTypes = currentMethodParameterTypes
					.toArray(new String[0]);
			String[] aParameterNames = currentMethodParameterNames
					.toArray(new String[0]);
			STMethod anSTMethod = new AnSTMethod(
					currentMethodAST,
					currentMethodName,
					fullTypeName,
					aParameterNames,
					aParameterTypes,
					currentMethodIsPublic || isInterface,
					currentMethodIsInstance || isInterface,
					currentMethodIsConstructor,
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
					new ArrayList(localSTVariables),
					new ArrayList(parameterSTVariables)
					);

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

	public boolean getImportsAsExistingClasses() {
		return importsAsExistingClasses;
	}

	public void setImportsAsExistingClasses(boolean aNewVal) {
		importsAsExistingClasses = aNewVal;
	}

	public  String[] getExistingClasses() {
		return existingClasses;
	}

	@Override
	public void doFinishTree(DetailAST ast) {
		if (!getVisitInnerClasses() && checkIncludeExcludeTagsOfCurrentType()) {
			// System.out.println("finish tree called:" + ast + " "
			// + getFileContents().getFilename());
			if (currentMethodName != null)
				processPreviousMethodData();
			processMethodAndClassData();
		}
		super.doFinishTree(ast);
		super.log(ast, "testing st builder");

	}

	public void doBeginTree(DetailAST ast) {
		super.doBeginTree(ast);
		astToFileContents.put(ast, getFileContents());
		// System.out.println("STBuilder" + checkAndFileDescription);
		currentSTType = null;
		if (!ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {
			stMethods.clear();
			stConstructors.clear();
		}
		sTBuilderTree = ast;

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
			// System.out.println("finish tree called:" + ast + " "
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
		// SymbolTableFactory.getOrCreateSymbolTable().getTypeNameToSTClass().put(
		// anSTClass.getName(), anSTClass);
//		if (aName.startsWith("test")) {
//			System.out.println("Putting class:" + aName);
//		}
		SymbolTableFactory.getOrCreateSymbolTable().getTypeNameToSTClass()
				.put(aName, anSTClass);
		// log (typeNameAST.getLineNo(), msgKey(), fullTypeName);

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
				dummyArray, dummyArray, dummyArray, 
				new HashMap(),
//				new HashMap(), 
//				new HashMap(), 
				new ArrayList(), 
				new ArrayList());

		// anSTClass.introspect();
		// anSTClass.findDelegateTypes();
		// SymbolTableFactory.getOrCreateSymbolTable().getTypeNameToSTClass().put(
		// fullTypeName, anSTClass);
		addSTType(anSTClass);

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
		addAllNoDuplicates(result, derivedTags);
		return result;
	}

	protected List<STNameable> computedAndDerivedMethodTags() {
		List<STNameable> result = currentMethodComputedTags;
		List<STNameable> derivedTags = derivedTags(currentMethodAST, METHOD_START);
		addAllNoDuplicates(result, derivedTags);
		return result;
	}
	@Override
	protected  List<STNameable> getAllTags(DetailAST anAST, DetailAST aNameAST, String aTypeName, String aStart ) {
		return getComputedDerivedAndExplicitTags(anAST, aNameAST, aTypeName, aStart);
	}
	protected  List<STNameable> getComputedDerivedAndExplicitTags(DetailAST anAST, DetailAST aNameAST, String aTypeName, String aStart ) {
		List<STNameable> result = getComputedAndExplicitTags(anAST, aNameAST, aTypeName);
		List<STNameable> derivedTags = derivedTags(anAST, aStart);
		addAllNoDuplicates(result, derivedTags);
		return result;
	}

	protected List<STNameable> derivedTags(DetailAST anAST, String aPatternPrefix) {
		derivedTags.clear();
		Map<String, String[]> constructToSpecifications = startToSpecification.get(aPatternPrefix);
		if (constructToSpecifications.isEmpty()) {
			return derivedTags;
		}
		String aText = toStringList(anAST).trim();
		int i = 1;
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
				editablePropertyNames.toArray(dummyArray), typeTags().toArray(
						dummyArray),
				// computedTypeTags().toArray(dummyArray),
				computedAndDerivedTypeTags().toArray(dummyArray),

				imports.toArray(dummyArray),
				globalVariables.toArray(dummyArray), 
				new HashMap<>(	globalVariableToCall), 
//				new HashMap<>(globalVariableToType),
//				new HashMap<>(globalVariableToRHS),
				new ArrayList<>(typesInstantiated),
				new ArrayList(globalSTVariables)
				);

		// anSTClass.introspect();
		// anSTClass.findDelegateTypes();
		// SymbolTableFactory.getOrCreateSymbolTable().getTypeNameToSTClass().put(
		// fullTypeName, anSTClass);
		addSTType(anSTClass);
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

	public static void main(String[] args) {
		System.out.println("[200]".matches("(.*)\\[(.*)\\](.*)"));
		//METHOD_DEF public static void main ( [ String ] args ) { EXPR new java . util . ArrayList ( ) ; VARIABLE_DEF Queue qL EXPR new Queue ( EXPR "links" ) = ; VARIABLE_DEF Queue qA EXPR new Queue ( EXPR "array" ) = ; VARIABLE_DEF String item EXPR [ args EXPR 0 ] = ; EXPR ( qL . enq EXPR item ) ; EXPR ( qA . enq EXPR item ) ; EXPR ( System . out . println EXPR ( qL . numElements ) ) ; EXPR ( System . out . println EXPR ( qA . numElements ) ) ; EXPR ( System . out . println EXPR ( qL . front ) ) ; EXPR ( System . out . println EXPR ( qA . front ) ) ; EXPR ( qL . deq ) ; EXPR ( qA . deq ) ; EXPR ( System . out . println EXPR ( qL . numElements ) ) ; EXPR ( System . out . println EXPR ( qA . numElements ) ) ; for ( FOR_INIT VARIABLE_DEF int i EXPR 1 = ; FOR_CONDITION EXPR < i args . length ; FOR_ITERATOR EXPR ++ i ) { EXPR ( qL . enq EXPR [ args EXPR i ] ) ; EXPR ( qA . enq EXPR [ args EXPR i ] ) ; } for ( FOR_INIT VARIABLE_DEF int i EXPR 1 = ; FOR_CONDITION EXPR < i args . length ; FOR_ITERATOR EXPR ++ i ) { EXPR ( System . out . println EXPR ( qL . front ) ) ; EXPR ( System . out . println EXPR ( qA . front ) ) ; EXPR ( qL . deq ) ; EXPR ( qA . deq ) ; EXPR ( System . out . println EXPR ( qL . numElements ) ) ; EXPR ( System . out . println EXPR ( qA . numElements ) ) ; } }
		//METHOD_DEF public int numElements ( ) { return EXPR ( imp . size ) ;
		System.out.println ("METHOD_DEF public int numElements ( ) { return EXPR ( imp . size ) ;".
				matches("METHOD_DEF .* int (size|numElements).*"));
		System.out.println ("METHOD_DEF public static| void main ( [ String ] args ) { EXPR new java . util . ArrayList ( ) ; VARIABLE_DEF Queue qL EXPR new Queue ( EXPR \"links\" ) = ; VARIABLE_DEF Queue qA EXPR new Queue ( EXPR \"array\" ) = ; VARIABLE_DEF String item EXPR [ args EXPR 0 ] = ; EXPR ( qL . enq EXPR item ) ; EXPR ( qA . enq EXPR item ) ; EXPR ( System . out . println EXPR ( qL . numElements ) ) ; EXPR ( System . out . println EXPR ( qA . numElements ) ) ; EXPR ( System . out . println EXPR ( qL . front ) ) ; EXPR ( System . out . println EXPR ( qA . front ) ) ; EXPR ( qL . deq ) ; EXPR ( qA . deq ) ; EXPR ( System . out . println EXPR ( qL . numElements ) ) ; EXPR ( System . out . println EXPR ( qA . numElements ) ) ; for ( FOR_INIT VARIABLE_DEF int i EXPR 1 = ; FOR_CONDITION EXPR < i args . length ; FOR_ITERATOR EXPR ++ i ) { EXPR ( qL . enq EXPR [ args EXPR i ] ) ; EXPR ( qA . enq EXPR [ args EXPR i ] ) ; } for ( FOR_INIT VARIABLE_DEF int i EXPR 1 = ; FOR_CONDITION EXPR < i args . length ; FOR_ITERATOR EXPR ++ i ) { EXPR ( System . out . println EXPR ( qL . front ) ) ; EXPR ( System . out . println EXPR ( qA . front ) ) ; EXPR ( qL . deq ) ; EXPR ( qA . deq ) ; EXPR ( System . out . println EXPR ( qL . numElements ) ) ; EXPR ( System . out . println EXPR ( qA . numElements ) ) ; } }".

//				matches("METHOD_DEF [(public static)|(static public)] void main.*"));
		matches("METHOD_DEF .* void main.*"));
//METHOD_DEF public boolean empty ( ) { return EXPR ( imp . empty ) ; } }
//		METHOD_DEF int size ( ) ;
		System.out.println ("VARIABLE_DEF private [ String ]".matches("VARIABLE_DEF (private |protected )*\\[ String \\].*"));
		
		
	}

}