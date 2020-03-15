package unc.tools.checkstyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.NonExitingMain;
import com.puppycrawl.tools.checkstyle.XMLLogger;
import com.puppycrawl.tools.checkstyle.Main;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;

import unc.cs.checks.ClassDefinedCheck;
import unc.cs.checks.MissingMethodTextCheck;
import unc.cs.checks.STBuilderCheck;
import unc.cs.checks.TagBasedCheck;
import unc.cs.parseTree.AnIFStatement;
import unc.cs.symbolTable.AccessModifierUsage;
import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.STVariable;
import unc.cs.symbolTable.SymbolTable;
import unc.cs.symbolTable.SymbolTableFactory;

public class PostProcessingMain {
	//// static final String SOURCE =
	//// "C:\\Users\\dewan\\Downloads\\twitter-heron";
	//// static final String SOURCE =
	//// "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce";
	// static final String SOURCE =
	//// "D:\\dewan_backup\\Java\\UNCCheckStyle\\src\\test";
	//
	//// static final String SOURCE =
	//// "C:\\Users\\dewan\\Downloads\\twitter-heron\\contrib\\bolts\\kafka\\src\\java\\org\\apache\\heron\\bolts\\kafka\\KafkaBolt.java";
	// static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";
	// static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION, SOURCE};
	static boolean printOnlyTaggedClasses = false;

	static SymbolTable symbolTable;
	static STBuilderCheck sTBuilderCheck;
	static String[] externalPackagePrefixes;
	static String[] externalMethodRegularExpressions;
	static String[] externalClassRegularExpressions;
	static List<STType> sTTypes;
	static final String  CHECKS_FILE_NAME = "generated_checks.xml";
	static PrintStream checksPrintStream;

	public static void initGlobals() {
		symbolTable = SymbolTableFactory.getSymbolTable();
		sTBuilderCheck = STBuilderCheck.getLatestInstance();
		externalPackagePrefixes = sTBuilderCheck.getExternalPackagePrefixes();
		externalMethodRegularExpressions = sTBuilderCheck.getExternalMethodRegularExpressions();
		externalClassRegularExpressions = sTBuilderCheck.getExternalTypeRegularExpressions();
		sTTypes = symbolTable.getAllSTTypes();

	}
	
	public static STMethod[] getDeclaredOrAllMethods(STType anSTType) {
		STMethod[] retVal = anSTType.getMethods();
		if (retVal == null) {
			retVal = anSTType.getDeclaredMethods();
		}
		return retVal;
	}

	public static void processTypes(List<STType> anSTTypes) {
		// create some side effects first
		for (STType anSTType : anSTTypes) {
			if (anSTType.isExternal()) {
				continue; // these methods have no callers
			}
			if (anSTType.getStaticBlocks() != null) {
				
			anSTType.getStaticBlocks().refreshUnknowns();
			}
			STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
			for (STMethod aMethod:aMethods) {
				aMethod.getLocalMethodsCalled(); // side effect of adding caller
				aMethod.refreshUnknowns();
//				aMethod.getAllMethodsCalled();
			}
			

		}
		for (STType anSTType : anSTTypes) {
			if (anSTType.isExternal()) {
				continue; // these methods have no callers
			}
			STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
			for (STMethod aMethod:aMethods) {
//				aMethod.getLocalMethodsCalled();
				aMethod.getAllMethodsCalled();// side effect of adding caller
			}
			

		}
		for (STType anSTType : anSTTypes) {
			if (anSTType.isExternal()) {
				continue; // these methods have no callers
			}
			STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
			for (STMethod aMethod:aMethods) {
//				aMethod.getLocalMethodsCalled();
				aMethod.getAllInternallyDirectlyAndIndirectlyCalledMethods();// side effect of adding caller
			}
			

		}
		for (STType anSTType : anSTTypes) {
			if (anSTType.isExternal()) {
				continue; // these methods have no callers
			}
			STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
			for (STMethod aMethod:aMethods) {
//				aMethod.getLocalMethodsCalled();
				aMethod.getAllDirectlyOrIndirectlyCalledMethods();// side effect of adding caller
			}			

		}
		for (STType anSTType : anSTTypes) {
			if (!anSTType.isExternal()) {
				continue; // these methods have no callers
			}
			STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
			if (aMethods == null) {
				aMethods = anSTType.getDeclaredMethods();
			}
			for (STMethod aMethod:aMethods) {
//				aMethod.getLocalMethodsCalled();
				Set<STMethod> aCallingMethods = aMethod.getCallingMethods();
				if (aCallingMethods != null) {
				System.out.println (anSTType + ":" + aMethod + ":" + aMethod.getCallingMethods());
				}
			}			

		}
		for (STType anSTType : anSTTypes) {
			processTypePrint(anSTType);
			// List<STNameable> anInterfaces = anSTType.getAllInterfaces();
			// if (anInterfaces == null) {
			// anInterfaces = Arrays.asList(anSTType.getDeclaredInterfaces());
			// }

		}
		
	}

	public static void processTypePrint(STType anSTType) {
		if (anSTType.isInterface()) {
			return;
		}
		
		printTypeInterfaces(anSTType);
		printTypeSuperTypes(anSTType);
		processTypeProperties(anSTType);
//		processTypeSuperTypes(anSTType);
//		processTypeCallInfos(anSTType);
//		processDeclaredMethods(anSTType);
//		processMethodsCalled(anSTType);
		processUnknownVariablesAccessed(anSTType);
//		processAccessModifiersUsed(anSTType);
//		processReferencesPerVariable(anSTType);
		

	}
//	<module name="IllegalPropertyNotification">
//	<property name="severity" value="warning" />		
//	<property name="excludeProperties" value="this" />
//</module>
	public static void printProperty(String aProperty, String aValue) {
		checksPrintStream.println ("\t\t<property name=\"" + aProperty + "\" value=\"" + aValue + "\"/>");

	}
//	 <module name="ExpectedGetters">
//		<property name="severity" value="warning" />	
//		<property name="includeTypeTags" value="KeyValueClass" />				
//		<property name="expectedProperties" 
//		         value="
//					 Key:.*, 
//					 Value:.*				
//					" 
//		/>
//	</module>
	public static void printExectedPairs(String aCheckName, String aPropertyName, String aScopingType, String[] aPairs) {
		if (aPairs.length %2 != 0) {
			System.err.println("odd array");
			return;
		}
		StringBuilder aPropertiesAndTypesString = new StringBuilder();
	
		for (int i  = 0; i < aPairs.length; i = i+2) {
			aPropertiesAndTypesString.append("\n\t\t\t" + aPairs[i] + ":" + aPairs[i+1] + "," );
		}
		String[] aPropertyNameAndValue = {aPropertyName, aPropertiesAndTypesString.toString()};

		printWarningModuleAndProperties(aCheckName, aScopingType, aPropertyNameAndValue);
	}
	public static String toChecksList( String[] aList) {
		
		StringBuilder aPropertiesString = new StringBuilder();
	
		for (int i  = 0; i < aList.length; i = i+2) {
			aPropertiesString.append("\n\t\t\t" + aList[i] + "," );
		}
		return aPropertiesString.toString();

	}
//	public static void printExpectedInterfaces(String aScopingType, String[] aPropertyNameAndType) {
//		printExectedPairs("ExpectedInterfaces", "expectedInterfaces", aScopingType, aPropertyNameAndType);
//	}
	public static void printExpectedGetters(String aScopingType, String[] aPropertyNameAndType) {
		printExectedPairs("ExpectedGetters", "expectedProperties", aScopingType, aPropertyNameAndType);
	}
//	<module name="ExpectedSetters">
//	<property name="severity" value="warning" />	
//	<property name="includeTypeTags" value="ModelClass" />				
//	<property name="expectedProperties" 
//	         value="
//				 InputString:String, 
//				" 
//	/>
//</module>
	public static void printExpectedSetters(String aScopingType, String[] aPropertyNameAndType) {
		printExectedPairs("ExpectedSetters", "expectedProperties", aScopingType, aPropertyNameAndType);
	}
//	<module name="ExpectedSignatures">
//	<property name="severity" value="warning" />
//	<property name="includeTypeTags" value="ModelClass" />					
//	<property name="expectedSignatures"
//		value="
//		    addPropertyChangeListener:PropertyChangeListener->void,
//		" 
//	/>
//</module>
	public static void printExpectedSignatures(String aScopingType, String[] aPropertyNameAndType) {
		printExectedPairs("ExpectedSignatures", "expectedSignatures", aScopingType, aPropertyNameAndType);
	}
//	<module name="MissingMethodCall">
//	<property name="severity" value="warning" />
//	<property name="includeTypeTags" value="ControllerClass" />					
//	<property name="expectedCalls"
//		value="
//		    .*!setInputString:String->void,
//		" 
//	/>
//</module>
	public static void printExpectedCalls(String aScopingType, String[] aPropertyNameAndType) {
		printExectedPairs("MissingMethodCall", "expectedCalls", aScopingType, aPropertyNameAndType);
	}
	public static void printWarningModuleAndProperties(String aModule, String aScopingType,  String[] aPropertyNamesAndValues) {
		printModuleAndProperties(aModule,"warning",  aScopingType, aPropertyNamesAndValues);
	}
	public static void printInfoModuleAndProperties(String aModule, String aScopingType, String[] aPropertyNamesAndValues) {
		printModuleAndProperties(aModule, "info", aScopingType, aPropertyNamesAndValues);
	}

	public static void printModuleAndProperties(String aModule, String aSeverity, String aScopingType,  String[] aPropertyNamesAndValues) {
		if (aPropertyNamesAndValues.length %2 != 0) {
			System.err.println ("mismatched property name and values ");
		}
//		checksPrintStream.println ("\t<module name=\"" + aModule + "\">");
//		printModuleStart(aModule);
//		printProperty("severity", aSeverity);
//		printProperty("includeTypeTags", aScopingType);
		printModuleStart(aModule, aSeverity, aScopingType);

		for (int i = 0; i < aPropertyNamesAndValues.length; i = i + 2) {
			printProperty(aPropertyNamesAndValues[i], aPropertyNamesAndValues[i+1]);
		}
//		checksPrintStream.println ("</module>");
		printModuleEnd();
	}
	
	
	public static void printModuleStart(String aModule) {
		checksPrintStream.println ("\t<module name=\"" + aModule + "\">");

	}
	public static void printModuleStart(String aModule, String aSeverity, String aScopingType) {
		printModuleStart(aModule);
		printProperty("severity", aSeverity);
		printProperty("includeTypeTags", aScopingType);

	}
	public static void printModuleEnd() {
		checksPrintStream.println ("\t</module>");

	}
	
	public static void printModuleSingleProperty(String aModule, String aSeverity, String aScopingType, String aProperty,  String[] aPropertyValues) {
		
//		checksPrintStream.println ("\t<module name=\"" + aModule + "\">");
		printModuleStart(aModule, aSeverity, aScopingType);
		String aPropertiesString = toChecksList(aPropertyValues);
		checksPrintStream.println ("\t\t<property name=\"" + aProperty + "\" value=\"" + aPropertiesString + "\"/>");
		printModuleEnd();
	}


	public static boolean isExternalType(String aFullName) {
//		return !TagBasedCheck.isProjectImport(aFullName) || TagBasedCheck.isExternalType(aFullName);
		return TagBasedCheck.isExternalType(aFullName);

	}

	public static void printImplementsExternal(STType anSTType, String anInterfaceFullName) {
		System.out.println("printImplementsExternal:" + anSTType.getName() + "implements:" + anInterfaceFullName);
	}

	public static void printImplementsTagged(STType anSTType, STType anInterfaceType) {

		Set<String> aMissingTags = TagBasedCheck.tagsNotContainedIn(anSTType, anInterfaceType);
		if (aMissingTags.isEmpty()) {
			return;
		}
		System.out.println("printImplementsTagged:" + anSTType.getName() + "," + anInterfaceType.getName());

	}

	public static void processTypeSuperTypes(STType anSTType) {
		if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
			return;
		}
		List<STNameable> aSuperClasses = anSTType.getAllSuperTypes();
		if (aSuperClasses == null) {
			STNameable aSuperClass = anSTType.getSuperClass();
			if (aSuperClass == null) {
				return;
			}
			aSuperClasses = new ArrayList();
			aSuperClasses.add(aSuperClass);
		}

		for (STNameable aClass : aSuperClasses) {
			String aFullName = aClass.getName();
			if (isExternalType(aFullName)) {
				printExternalSuperClass(anSTType, aFullName);
				return;
			}
			STType aClassSTType = symbolTable.getSTClassByFullName(aClass.getName());
			if (aClassSTType == null) {
				// continue;
				aClassSTType = symbolTable.getSTClassByShortName(aClass.getName());
			}
			if (TagBasedCheck.isExplicitlyTagged(aClassSTType)) {
				printTaggedSuperclass(anSTType, aClassSTType);
			}
		}

	}

	private static void printTaggedSuperclass(STType anSTType, STType aSuperType) {
		Set<String> aMissingTags = TagBasedCheck.tagsNotContainedIn(anSTType, aSuperType);
		if (aMissingTags.isEmpty()) {
			return;
		}
		System.out.println("printTaggedSuperclass:" + anSTType.getName() + "," + aSuperType.getName());
	}

	private static void printExternalSuperClass(STType anSTType, String aFullName) {
		System.out.println("printTaggedSuperclass:" + anSTType.getName() + "," + aFullName);

	}

	public static void printTypeInterfaces(STType anSTType) {
		if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
			return;
		}
		String aTypeOutputName = toOutputType(anSTType);


		List<STNameable> anInterfaces = anSTType.getAllInterfaces();
		if (anInterfaces == null) {
			anInterfaces = Arrays.asList(anSTType.getDeclaredInterfaces());
		}
		List<String> aRequiredInterfaces = new ArrayList();
		for (STNameable anInterface : anInterfaces) {
			String aFullName = anInterface.getName();
//			String anOutputName = toOutputType(aFullName);
//			if (anOutputName == TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSON || anOutputName.equals(aTypeOutputName)) {
//				
//				continue;
//			}
			if (!TagBasedCheck.isExternalType(aFullName)) {
				continue;
			}
//			aRequiredInterfaces.add(anOutputName);
			aRequiredInterfaces.add(aFullName);

//			if (isExternalType(aFullName) && TagBasedCheck.isExplicitlyTagged(anSTType)) {
//				printImplementsExternal(anSTType, aFullName);
//				return;
//			}
//			STType anInterfaceSTType = symbolTable.getSTClassByFullName(anInterface.getName());
//			if (anInterfaceSTType == null) {
//				// continue;
//				anInterfaceSTType = symbolTable.getSTClassByShortName(anInterface.getName());
//			}
//			if (TagBasedCheck.isExplicitlyTagged(anInterfaceSTType) && TagBasedCheck.isExplicitlyTagged(anSTType)) {
//				printImplementsTagged(anSTType, anInterfaceSTType);
//			}
		}
		if (aRequiredInterfaces.size() == 0) {
			return;
		}
		printModuleSingleProperty("ExpectedInterfaces", "warning", aTypeOutputName, "expectedInterfaces", aRequiredInterfaces.toArray(stringArray) );

		

	}
	public static void printTypeSuperTypes(STType anSTType) {
		if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
			return;
		}
		String aTypeOutputName = toOutputType(anSTType);


		List<STNameable> aSuperClasses = anSTType.getAllSuperTypes();
		if (aSuperClasses == null) {
			aSuperClasses = Arrays.asList(anSTType.getSuperClass());
		}
		List<String> aRequiredClasses = new ArrayList();
		for (STNameable aSuperClass : aSuperClasses) {
			String aFullName = aSuperClass.getName();
			String anOutputName = toOutputType(aFullName);
			if (anOutputName == TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSON || anOutputName.equals(aTypeOutputName) || anOutputName.equals("java.lang.Object")) {
				
				continue;
			}
//			if (!TagBasedCheck.isExternalType(aFullName)) {
//				continue;
//			}
			aRequiredClasses.add(anOutputName);
//			aRequiredClasses.add(aFullName);

//			if (isExternalType(aFullName) && TagBasedCheck.isExplicitlyTagged(anSTType)) {
//				printImplementsExternal(anSTType, aFullName);
//				return;
//			}
//			STType anInterfaceSTType = symbolTable.getSTClassByFullName(anInterface.getName());
//			if (anInterfaceSTType == null) {
//				// continue;
//				anInterfaceSTType = symbolTable.getSTClassByShortName(anInterface.getName());
//			}
//			if (TagBasedCheck.isExplicitlyTagged(anInterfaceSTType) && TagBasedCheck.isExplicitlyTagged(anSTType)) {
//				printImplementsTagged(anSTType, anInterfaceSTType);
//			}
		}
		if (aRequiredClasses.size() == 0) {
			return;
		}
		printModuleSingleProperty("ExpectedSuperTypes", "warning", aTypeOutputName, "expectedSuperTypes", aRequiredClasses.toArray(stringArray) );

		

	}
	public static void processDeclaredMethods(STType anSTType) {
		if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
			return;
		}
		STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
		for (STMethod aMethod:aMethods) {
			if (!aMethod.isPublic()) {
				continue;
			}
			String[] aNormalizedTypes = TagBasedCheck.toNormalizedTypes(aMethod.getParameterTypes());
			String aReturnType = aMethod.getReturnType();
			String aNormalizedReturnType = TagBasedCheck.toNormalizedType(aReturnType);
			
			System.out.println("Types:" + Arrays.toString(aNormalizedTypes) + " return:" +aNormalizedReturnType);
			
		}
	}
	public static void processUnknownVariablesAccessed(STType aSubjectType, STMethod aRootMethod, STMethod aMethod) {
		Map<String, Set<DetailAST>> anUnKnownsAccessed = aMethod.getUnknownAccessed();
		if (anUnKnownsAccessed == null) {
			return;
		}
		Set<String> anUnknownsAccessedSet = anUnKnownsAccessed.keySet();
		List<String> aMethodAndVariables = new ArrayList();
		for (String anUnknown:anUnknownsAccessedSet) {
			if (!anUnknown.contains(".")) continue;
			if (anUnknown.contains("System.")) continue;
			String aClassName = TagBasedCheck.fromVariableToTypeName(anUnknown);
			if (TagBasedCheck.isExternalType(aClassName)) {
				String aSimpleMethodSignature = aRootMethod.getSimpleChecksSignature();
				int aLastDotIndex = anUnknown.lastIndexOf(".");
				String aRegularExpression = 
						TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSON + 
						anUnknown.substring(aLastDotIndex + 1) +
					TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSON;
				aMethodAndVariables.add(
						aSimpleMethodSignature + 
						MissingMethodTextCheck.CALLER_TEXT_SEPARATOR +
						aRegularExpression);
				

//				aMethodAndVariables.add (anUnkown);
//				System.out.println ("type:" + aSubjectType.getName() + " method " + aRootMethod.getName() + " global " + anUnkown);

			}
		}
		if (aMethodAndVariables.size() == 0) {
			return;
		}
		String aTypeOutputName = toOutputType(aSubjectType);
		printModuleSingleProperty("MethodAccessesGlobal", "warning", aTypeOutputName, "expectedReferences", aMethodAndVariables.toArray(stringArray) );

		

	}
	
	public static void processUnknownVariablesAccessed(STType aSubjectType, STMethod aRootMethod, Set<STMethod> aMethods) {
		if (aMethods == null) {
			return;
		}
		for (STMethod aMethod:aMethods) {
			processUnknownVariablesAccessed(aSubjectType, aRootMethod, aMethod);
		}
	}
	public static void processUnknownVariablesAccessed(STType anSTType) {
		if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
			return;
		}
		STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
		for (STMethod aMethod:aMethods) {
			if (! aMethod.isPublic()) {
				continue;
			}
			Set<STMethod> aCalledMethods = aMethod.getAllDirectlyOrIndirectlyCalledMethods();
//			Set<String> anUnknownsAccessed = aMethod.getUnknownAccessed().keySet();
			processUnknownVariablesAccessed(anSTType, aMethod, aMethod);
			processUnknownVariablesAccessed(anSTType, aMethod, aCalledMethods);

//			Set<String> anUnknownAssigned = aMethod.getUnknownAssigned().keySet();
//			if (anUnknownsAccessed != null) {
//			for (String anUnknown:anUnknownsAccessed) {
//				if (anUnknown.contains(".")) {
//					System.out.println ("type:" + anSTType.getName() + " method " + aMethod.getName() + " anKnown " + anUnknown);
//				}
//			}
//			}
			
			
		}
	}
	protected static String getSubtypeTagged (STType anSTType) {
		if (TagBasedCheck.isExplicitlyTagged(anSTType)) {
			return anSTType.getName();
		}
		List<String> aSubtypes = anSTType.getSubTypes();
		for (String aSubtype:aSubtypes) {
			if (TagBasedCheck.isExplicitlyTagged(aSubtype)) {
				return aSubtype;
			}
		}
	    return null;
	}
	protected static String getSubtypeExternal (STType anSTType) {
		if (TagBasedCheck.isExternalType(anSTType.getName())) {
			return anSTType.getName();
		}
		List<String> aSubtypes = anSTType.getSubTypes();
		for (String aSubtype:aSubtypes) {
			if (TagBasedCheck.isExternalType(aSubtype)) {
				return aSubtype;
			}
		}
	    return null;
	}
	public static void processAccessModifiersUsed(STType anSTType) {
		System.out.println(anSTType.getName() + " "  + STBuilderCheck.toAccessModifiersUsedString(anSTType));
		STMethod[] aMethods = anSTType.getDeclaredMethods();
		if (aMethods != null) {
		for (STMethod aMethod:aMethods) {
			List<AccessModifierUsage> aUsage = aMethod.getAccessModifiersUsed();
			if (aUsage != null)
//				System.out.println("Access Modifier Usage:" + anSTType.getName() + "," +  aUsage);
				writeXMLMessage(anSTType.getFileName(), anSTType.getAST(), anSTType.getName() + "," +  aUsage);
		}
		}
		List<STVariable> aVariables = anSTType.getDeclaredSTGlobals();
		if (aVariables != null) {
		for (STVariable aVariable:anSTType.getDeclaredSTGlobals()) {
//			System.out.println("Access Modifier Usage:" + anSTType.getName() + "," + aVariable.getAccessModifiersUsed());
			writeXMLMessage(anSTType.getFileName(), anSTType.getAST(), anSTType.getName() + "," +  aVariable.getAccessModifiersUsed());

		}
		
		}
	}
	public static void processReferencesPerVariable(STType anSTType) {
		if (anSTType.isExternal()) {
			return; // these methods have no callers
		}
		writeXMLMessage(anSTType.getFileName(), anSTType.getAST(), anSTType.getName() + " Average references per constant:" + anSTType.getNumberOfReferencesPerConstant());
		writeXMLMessage(anSTType.getFileName(), anSTType.getAST(), anSTType.getName() + " Average references per variable:" + anSTType.getNumberOfReferencesPerVariable());
		writeXMLMessage(anSTType.getFileName(), anSTType.getAST(), anSTType.getName() + " Average assignments per variable:" + anSTType.getNumberOfAssignmentsPerVariable());

//		System.out.println(anSTType.getName() + " Average references per constant:" + anSTType.getNumberOfReferencesPerConstant());
//
//		System.out.println(anSTType.getName() + " Average references per variable:" + anSTType.getNumberOfReferencesPerVariable());
//
//		System.out.println(anSTType.getName() + " Average assignments per variable:" + anSTType.getNumberOfAssignmentsPerVariable());

	}
	public static void writeXMLMessage(String aFileName, DetailAST anAST, String aMessage) {
		if (xmlLogger == null) {
			xmlLogger = new XMLLogger(System.out, true) ;
			xmlLogger.auditStarted(null);
		}
		 final LocalizedMessage message =
		            new LocalizedMessage(anAST.getLineNo(), anAST.getColumnNo(),
		                "messages.properties", aMessage, null, SeverityLevel.INFO, "module",
		           PostProcessingCustomMain.class, null);
		        final AuditEvent evstart = new AuditEvent(new Object(), aFileName, null);
		        xmlLogger.fileStarted(evstart);
		        final AuditEvent ev = new AuditEvent(new Object(), aFileName, message);

//		        xmlLogger.fileStarted(ev);
		        xmlLogger.addError(ev);
//		        xmlLogger.fileFinished(ev);
		        xmlLogger.fileFinished(evstart);
		;

	}
	/*
	 * <error line="8" column="9" severity="info" message="test.TestSuperClass.Global Constant superConstant2 Identifier Components= [super, Constant, 2]" source="unc.cs.checks.MnemonicNameCheck"/>

	 */
	static XMLLogger xmlLogger;
	
//	public static void 
	public static void testXMLLogger() {
		xmlLogger = new XMLLogger(System.out, false) ;
		xmlLogger.auditStarted(null);
		String[] args = {"FooClass", "FooTag", "2", "3", "4"};
        final LocalizedMessage message =
            new LocalizedMessage(1, 1,
                "messages.properties", "classDefined", args, SeverityLevel.INFO, "module",
           ClassDefinedCheck.class, null);
        final AuditEvent evstart = new AuditEvent(new Object(), "Test.java", null);


        final AuditEvent ev = new AuditEvent(new Object(), "Test.java", message);
        xmlLogger.fileStarted(evstart);

//        xmlLogger.fileStarted(ev);
        xmlLogger.addError(ev);
//        xmlLogger.fileFinished(ev);
        xmlLogger.fileFinished(evstart);
        xmlLogger.fileStarted(evstart);

//      xmlLogger.fileStarted(ev);
      xmlLogger.addError(ev);
//      xmlLogger.fileFinished(ev);
      xmlLogger.fileFinished(evstart);

        xmlLogger.auditFinished(null);
//        verifyXml(getPath("ExpectedXMLLoggerError.xml"), outStream, message.getMessage());
	}
	public static void processMethodsCalled(STType anSTType) {
		if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
			return;
		}
		STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
		for (STMethod aMethod:aMethods) {
			if (! aMethod.isPublic()) {
				continue;
			}
			Set<STMethod> aCalledMethods = aMethod.getAllDirectlyOrIndirectlyCalledMethods();
			if (aCalledMethods == null) {
				continue;
			}
			for (STMethod aCalledMethod: aCalledMethods) {
				if (!aCalledMethod.isPublic()) {
					continue;
				}
				STType aCalledType = aCalledMethod.getDeclaringSTType();
				String aSubtype = getSubtypeTagged(aCalledType);
				if (aSubtype == null) {
					aSubtype = getSubtypeExternal(anSTType);
				}
				if (aSubtype != null) {
					System.out.println("Calling type:" + anSTType + " calling method: " + aMethod + " called type " + aSubtype + " called method " + aCalledMethod);

				}
				

			
			}
//			Set<String> anUnknownsAccessed = aMethod.getUnknownAccessed().keySet();
			processUnknownVariablesAccessed(anSTType, aMethod, aMethod);
			processUnknownVariablesAccessed(anSTType, aMethod, aCalledMethods);

//			Set<String> anUnknownAssigned = aMethod.getUnknownAssigned().keySet();
//			if (anUnknownsAccessed != null) {
//			for (String anUnknown:anUnknownsAccessed) {
//				if (anUnknown.contains(".")) {
//					System.out.println ("type:" + anSTType.getName() + " method " + aMethod.getName() + " anKnown " + anUnknown);
//				}
//			}
//			}
			
			
		}
	}

	public static void printCallInfo(STType aCallerSTType, STType aCalledSTType, CallInfo aCallInfo) {
		String aCallingTypeName = aCallerSTType.getName();
		String aCalledTypeName = aCallInfo.getCalledType();
		STMethod aCallingMethod = aCallInfo.getCallingMethod();
		Set<STMethod> aCalledMethods = aCallInfo.getMatchingCalledMethods();
		String aCallee = aCallInfo.getCallee();
		System.out.println(aCallingTypeName + ":" + aCallingMethod + ":" + aCalledTypeName + ":" + aCallee + ":"
				+ aCalledSTType + aCalledMethods);
	}
	public static String toTaggedType(STType anSTType) {
		List<String> aTags = TagBasedCheck.getNonComputedTagsList(anSTType);
		if (aTags.size() == 0) {
			return null;
		}
		return aTags.get(0);
	}
	public static String toOutputType (String aTypeName) {
		if (TagBasedCheck.isExternalType(aTypeName)) {
			return aTypeName;
		}
		String anElementTypeName = TagBasedCheck.toElementTypeName(aTypeName);
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(anElementTypeName);
		return toOutputType(anSTType);
//		if (anSTType != null) {
//			String aTag = toTaggedType(anSTType);
//			if (aTag != null) {
//				return "@" + aTag;
//			}
////			if (TagBasedCheck.isExplicitlyTagged(anSTType)) {
////				return aTypeName;
////			}
//		}
//		return TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSON;

//		return ".*";
	}
	public static String toOutputType (STType anSTType) {
	
		if (anSTType != null) {
			String aTag = toTaggedType(anSTType);
			if (aTag != null) {
				return "@" + aTag;
			}
//			if (TagBasedCheck.isExplicitlyTagged(anSTType)) {
//				return aTypeName;
//			}
		}
		return TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSON;

//		return ".*";
	}
	static String[] stringArray = {};
	public static void processTypeProperties(STType anSTType) {
		if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
			return;
		}
		String aTaggedType = toOutputType(anSTType.getName());
		Map<String,  PropertyInfo> aProperties = anSTType.getPropertyInfos();
		if (aProperties == null) {
			aProperties = anSTType.getDeclaredPropertyInfos();
		}
		Set<String> aKeys = aProperties.keySet();
		if (aKeys.size() == 0) {
			return;
		}
		List<String> aNamesAndTypes = new ArrayList(aKeys.size()*2);
//		int anIndex = 0;
		for (String aKey:aKeys) {
			PropertyInfo aPropertyInfo = aProperties.get(aKey);			
			if (aPropertyInfo.getGetter() != null && aPropertyInfo.getGetter().isPublic()) {
				String aPropertyName = aPropertyInfo.getName();
				String aPropertyType = aPropertyInfo.getType();
				String anOutputPropertyType = toOutputType(aPropertyType);
				aNamesAndTypes.add(aPropertyName);
				aNamesAndTypes.add(anOutputPropertyType);
//
//				aNamesAndTypes[anIndex] = aPropertyName;
//				aNamesAndTypes[anIndex+1] = anOutputPropertyType;
//				anIndex = anIndex + 2;				
//				System.out.println (anSTType.getName() + 
//						"name:" +  aPropertyName +
//						" type:" + anOutputPropertyType);
						
			}
		}
//		printExpectedGetters(anSTType.getName(), aNamesAndTypes);
		printExpectedGetters(aTaggedType, aNamesAndTypes.toArray(stringArray));

		 
		
	}

	public static void processTypeCallInfos(STType anSTType) {
		if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
			return;
		}
		List<CallInfo> aCallInfos = anSTType.getAllMethodsCalled();
		if (aCallInfos == null) {
			aCallInfos = anSTType.getMethodsCalled();
		}
		for (CallInfo aCallInfo : aCallInfos) {
			String aCalledType = aCallInfo.getCalledType();
			STType aCalledSTType = SymbolTableFactory.getSymbolTable().getSTClassByFullName(aCalledType);
			if (aCalledSTType == null) {
				aCalledSTType = SymbolTableFactory.getSymbolTable().getSTClassByShortName(aCalledType);
			}
			if (isExternalType(aCalledType)) {
				printCallInfo(anSTType, aCalledSTType, aCallInfo);
			}
			if (aCalledSTType != null && TagBasedCheck.isExplicitlyTagged(aCalledSTType)) {
				printCallInfo(anSTType, aCalledSTType, aCallInfo);

			}
		}
//		List<STNameable> anInterfaces = anSTType.getAllInterfaces();
//		if (anInterfaces == null) {
//			anInterfaces = Arrays.asList(anSTType.getDeclaredInterfaces());
//		}
//		for (STNameable anInterface : anInterfaces) {
//			String aFullName = anInterface.getName();
//			if (isExternalType(aFullName)) {
//				printImplementsExternal(anSTType, aFullName);
//				return;
//			}
//			STType anInterfaceSTType = symbolTable.getSTClassByFullName(anInterface.getName());
//			if (anInterfaceSTType == null) {
//				// continue;
//				anInterfaceSTType = symbolTable.getSTClassByShortName(anInterface.getName());
//			}
//			if (TagBasedCheck.isExplicitlyTagged(anInterfaceSTType)) {
//				printImplementsTagged(anSTType, anInterfaceSTType);
//			}
//		}

	}

	public static boolean isPrintOnlyTaggedClasses() {
		return printOnlyTaggedClasses;
	}

	public static void setPrintOnlyTaggedClasses(boolean printOnlyTaggedClasses) {
		PostProcessingMain.printOnlyTaggedClasses = printOnlyTaggedClasses;
	}

	public static void main(String[] args) {
		File aFile = new File(CHECKS_FILE_NAME);
			try {
				aFile.createNewFile();
				checksPrintStream = new PrintStream(new File(CHECKS_FILE_NAME));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		// Main.main(ARGS);

		try {
			NonExitingMain.main(args);
			initGlobals();
			processTypes(sTTypes);
		} catch (UnsupportedEncodingException | FileNotFoundException | CheckstyleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if    (xmlLogger != null)     
			xmlLogger.auditFinished(null);
		String[] aPropertyNamesAndValues = {"prop", "1", "prop2", "2"};
		
		printWarningModuleAndProperties("test module", "KeyValueClass", aPropertyNamesAndValues);
		String[] aGetterProperties = {"Key", ".*", "Value", ".*"};
		printExpectedGetters("KeyValueClass", aGetterProperties);
		checksPrintStream.close();
//		testXMLLogger();


	}

}
