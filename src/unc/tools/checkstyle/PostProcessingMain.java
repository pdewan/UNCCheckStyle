package unc.tools.checkstyle;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.NonExitingMain;
import com.puppycrawl.tools.checkstyle.Main;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

import unc.cs.checks.STBuilderCheck;
import unc.cs.checks.TagBasedCheck;
import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
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
				Set<STMethod> aCalledMethods = aMethod.getCallingMethods();
				if (aCalledMethods != null) {
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
		
		processTypeInterfaces(anSTType);
		processTypeProperties(anSTType);
		processTypeSuperTypes(anSTType);
		processTypeMethodCalls(anSTType);
		processDeclaredMethods(anSTType);
		processGlobalVariables(anSTType);
		

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
	public static void processGlobalVariables(STType anSTType) {
		if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
			return;
		}
		STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
		for (STMethod aMethod:aMethods) {
			
			Set<String> anUnknownsAccessed = aMethod.getUnknownAccessed().keySet();
			Set<String> anUnknownAssigned = aMethod.getUnknownAssigned().keySet();
			if (anUnknownsAccessed != null) {
			for (String anUnknown:anUnknownsAccessed) {
				if (anUnknown.contains(".")) {
					System.out.println ("type:" + anSTType.getName() + " method " + aMethod.getName() + " anKnown " + anUnknown);
				}
			}
			}
			
			
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

	public static void processTypeMethodCalls(STType anSTType) {
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

	}

}
