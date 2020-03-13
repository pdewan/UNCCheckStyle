package unc.tools.checkstyle;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
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
		
//		processTypeInterfaces(anSTType);
//		processTypeProperties(anSTType);
//		processTypeSuperTypes(anSTType);
//		processTypeCallInfos(anSTType);
//		processDeclaredMethods(anSTType);
//		processMethodsCalled(anSTType);
//		processUnknownVariablesAccessed(anSTType);
//		processAccessModifiersUsed(anSTType);
//		processReferencesPerVariable(anSTType);
		

	}
//	<module name="IllegalPropertyNotification">
//	<property name="severity" value="warning" />		
//	<property name="excludeProperties" value="this" />
//</module>
	public static void printProperty(String aProperty, String aValue) {
		System.out.println ("	<property name=\"" + aProperty + "\" value=\"" + aValue + "\"/>");

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
	public static void printExectedGetters(String aScopingType, String[] aPropertyNameAndType) {
		if (aPropertyNameAndType.length %2 != 0) {
			System.err.println("odd array");
			return;
		}
		StringBuilder aPropertiesAndTypesString = new StringBuilder();
	
		for (int i  = 0; i < aPropertyNameAndType.length; i = i+2) {
			aPropertiesAndTypesString.append("\n\t\t" + aPropertyNameAndType[i] + ":" + aPropertyNameAndType[i+1] + "," );
		}
		String[] aPropertyNameAndValue = {"expectedProperties", aPropertiesAndTypesString.toString()};
		
		printWarningModuleAndProperties("ExpectedGetters", aScopingType, aPropertyNameAndValue);
	}
	
	public static void printWarningModuleAndProperties(String aModule, String aScopingType,  String[] aPropertyNamesAndValues) {
		printModuleAndProperties(aModule,"warning",  aScopingType, aPropertyNamesAndValues);
	}
	public static void printInfoModuleAndProperties(String aModule, String aScopingType, String[] aPropertyNamesAndValues) {
		printModuleAndProperties(aModule, "info", aScopingType, aPropertyNamesAndValues);
	}

	public static void printModuleAndProperties(String aModule, String aSeverity, String aScopingType,  String[] aPropertyNamesAndValues) {
		if (aPropertyNamesAndValues.length %2 != 0) {
			System.out.println ("mismatched property name and values ");
		}
		System.out.println ("<module name=\"" + aModule + "\">");
		printProperty("severity", aSeverity);
		printProperty("includeTypeTags", aScopingType);

		for (int i = 0; i < aPropertyNamesAndValues.length; i = i + 2) {
			printProperty(aPropertyNamesAndValues[i], aPropertyNamesAndValues[i+1]);
		}
		System.out.println ("</module>");
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

	public static void processTypeInterfaces(STType anSTType) {

		List<STNameable> anInterfaces = anSTType.getAllInterfaces();
		if (anInterfaces == null) {
			anInterfaces = Arrays.asList(anSTType.getDeclaredInterfaces());
		}
		for (STNameable anInterface : anInterfaces) {
			String aFullName = anInterface.getName();
			if (isExternalType(aFullName) && TagBasedCheck.isExplicitlyTagged(anSTType)) {
				printImplementsExternal(anSTType, aFullName);
				return;
			}
			STType anInterfaceSTType = symbolTable.getSTClassByFullName(anInterface.getName());
			if (anInterfaceSTType == null) {
				// continue;
				anInterfaceSTType = symbolTable.getSTClassByShortName(anInterface.getName());
			}
			if (TagBasedCheck.isExplicitlyTagged(anInterfaceSTType) && TagBasedCheck.isExplicitlyTagged(anSTType)) {
				printImplementsTagged(anSTType, anInterfaceSTType);
			}
		}

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
		for (String anUnkown:anUnknownsAccessedSet) {
			if (!anUnkown.contains(".")) continue;
			String aClassName = TagBasedCheck.fromVariableToTypeName(anUnkown);
			if (TagBasedCheck.isExternalType(aClassName)) {
				System.out.println ("type:" + aSubjectType.getName() + " method " + aRootMethod.getName() + " global " + anUnkown);

			}
		}

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
	public static void processTypeProperties(STType anSTType) {
		if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
			return;
		}
		Map<String,  PropertyInfo> aProperties = anSTType.getPropertyInfos();
		if (aProperties == null) {
			aProperties = anSTType.getDeclaredPropertyInfos();
		}
		for (String aKey:aProperties.keySet()) {
			PropertyInfo aPropertyInfo = aProperties.get(aKey);
			if (aPropertyInfo.getGetter() != null && aPropertyInfo.getGetter().isPublic()) {
				System.out.println (anSTType.getName() + " Property:" + aPropertyInfo);
			}
		}
		 
		
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
		printExectedGetters("KeyValueClass", aGetterProperties);
//		testXMLLogger();


	}

}
