package unc.tools.checkstyle;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.NonExitingMain;
import com.puppycrawl.tools.checkstyle.Main;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

import unc.cs.checks.STBuilderCheck;
import unc.cs.checks.TagBasedCheck;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTable;
import unc.cs.symbolTable.SymbolTableFactory;

public class PostProcessingMain {
//	static final String SOURCE = "C:\\Users\\dewan\\Downloads\\twitter-heron";
	static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce";
//	static final String SOURCE  = "C:\\Users\\dewan\\Downloads\\twitter-heron\\contrib\\bolts\\kafka\\src\\java\\org\\apache\\heron\\bolts\\kafka\\KafkaBolt.java";
	static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";
	static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION, SOURCE}; 
	static SymbolTable symbolTable;
	static STBuilderCheck sTBuilderCheck;
	static String[] externalPackagePrefixes;
	static String[] externalMethodRegularExpressions ;
	static String[] externalClassRegularExpressions ;
	static List<STType> sTTypes ;
	
	public static void initGlobals() {
		symbolTable = SymbolTableFactory.getSymbolTable();
		 sTBuilderCheck = STBuilderCheck.getLatestInstance();
		 externalPackagePrefixes = sTBuilderCheck.getExternalPackagePrefixes();
		externalMethodRegularExpressions = sTBuilderCheck.getExternalMethodRegularExpressions();
		externalClassRegularExpressions = sTBuilderCheck.getExternalTypeRegularExpressions();
		sTTypes = symbolTable.getAllSTTypes();
		
	}
	public static void processTypes (List<STType> anSTTypes) {
		for (STType anSTType:anSTTypes) {
			processType(anSTType);
//			List<STNameable> anInterfaces = anSTType.getAllInterfaces();
//			if (anInterfaces == null) {
//				anInterfaces = Arrays.asList(anSTType.getDeclaredInterfaces());
//			}
			
			
		}
	}
	public static void processType (STType anSTType) {
			if (anSTType.isInterface()) {
				return;
			}
			processTypeInterfaces(anSTType);
			processTypeSuperTypes(anSTType);
			
			
		
	}
	
	public static boolean isExternalType (String aFullName) {
		return
				!TagBasedCheck.isProjectImport(aFullName) || TagBasedCheck.isExternalType(aFullName);
	}
	public static void printImplementsExternal(STType anSTType, String anInterfaceFullName) {
		
	}
    public static void printImplementsTagged(STType anSTType, STType anInterfaceType) {
    	
    	Set<String> aMissingTags = TagBasedCheck.tagsNotContainedIn(anSTType, anInterfaceType);
    	if (aMissingTags.isEmpty()) {
    		return;
    	}
    	System.out.println ("printImplementsTagged:" + anSTType.getName() + "," + anInterfaceType.getName());
    		
    	
		
	}
	public static void processTypeSuperTypes (STType anSTType) {
		List<STNameable> aSuperClasses = anSTType.getAllSuperTypes();
		if (aSuperClasses == null) {
			STNameable aSuperClass = anSTType.getSuperClass();
			if (aSuperClass == null) {
				return;
			}
			aSuperClasses = new ArrayList();
			aSuperClasses.add(aSuperClass);
		}
		
		for (STNameable aClass:aSuperClasses) {
			String aFullName = aClass.getName();
			if (isExternalType(aFullName)) {
				printExternalSuperClass(anSTType, aFullName);
				return;
			}
			STType aClassSTType = symbolTable.getSTClassByFullName(aClass.getName());
			if (aClassSTType == null) {
//				continue;
				aClassSTType = symbolTable.getSTClassByShortName(aClass.getName());
			}
			if (TagBasedCheck.isExplicitlyTagged(aClassSTType)){
				printTaggedSuperclass(anSTType, aClassSTType);
			}
		}
		
	}

	private static void printTaggedSuperclass(STType anSTType, STType aSuperType ) {
		Set<String> aMissingTags = TagBasedCheck.tagsNotContainedIn(anSTType, aSuperType);
    	if (aMissingTags.isEmpty()) {
    		return;
    	}
    	System.out.println ("printTaggedSuperclass:" + anSTType.getName() + "," + aSuperType.getName());		
	}
	private static void printExternalSuperClass(STType anSTType, String aFullName) {
    	System.out.println ("printTaggedSuperclass:" + anSTType.getName() + "," + aFullName);		

		
	}
	public static void processTypeInterfaces (STType anSTType) {
	
	List<STNameable> anInterfaces = anSTType.getAllInterfaces();
	if (anInterfaces == null) {
		anInterfaces = Arrays.asList(anSTType.getDeclaredInterfaces());
	}
	for (STNameable anInterface:anInterfaces) {
		String aFullName = anInterface.getName();
		if (isExternalType(aFullName)) {
			printImplementsExternal(anSTType, aFullName);
			return;
		}
		STType anInterfaceSTType = symbolTable.getSTClassByFullName(anInterface.getName());
		if (anInterfaceSTType == null) {
//			continue;
			anInterfaceSTType = symbolTable.getSTClassByShortName(anInterface.getName());
		}
		if (TagBasedCheck.isExplicitlyTagged(anInterfaceSTType)){
			printImplementsTagged(anSTType, anInterfaceSTType);
		}
	}
	
	

}
	
	public static void main (String[] args) {
//		    Main.main(ARGS);

			try {
				NonExitingMain.main(ARGS);
				initGlobals();
				processTypes(sTTypes);
			} catch (UnsupportedEncodingException | FileNotFoundException | CheckstyleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
	}

}
