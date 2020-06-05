package unc.tools.checkstyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.puppycrawl.tools.checkstyle.NonExitingMain;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

import unc.cs.checks.STBuilderCheck;
import unc.cs.checks.UNCCheck;
import unc.cs.symbolTable.SymbolTable;
import unc.cs.symbolTable.SymbolTableFactory;

public class CreateMultipleSymbolTables {
	static final String[] myArgs = {"", "", ""};
	static final String[] emptyStrings = {};
	public static void createSymbolTable() {

		try {
			
			System.err.println("Building symbol table:" + new Date(System.currentTimeMillis()));
//			STBuilderCheck.setNonInteractive(true);
//			UNCCheck.setDoNotVisit(true);
//			PrintStream oldOut = System.out;
//
//			try {
//				File aDummyFile = new File(PostProcessingMain.DUMMY_FILE_NAME);
//				aDummyFile.createNewFile();
//				PrintStream aPrintStream = new PrintStream(aDummyFile);
//				System.setOut(aPrintStream);
//
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			NonExitingMain.main(argsList.toArray(emptyStrings));
			System.err.println("Symbol table size:" + SymbolTableFactory.getOrCreateSymbolTable().size());
			STBuilderCheck.setFirstPass(false);
			PostProcessingMain.doSecondPass(SymbolTableFactory.getOrCreateSymbolTable().getAllSTTypes());
			STBuilderCheck.setFirstPass(true);
			
		} catch (UnsupportedEncodingException | FileNotFoundException | CheckstyleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	static List<String> commonArgs= new ArrayList();
	static final String PROJECT_FLAG = "-p";
	
	static List<String> argsList = new ArrayList();
	static void makeCommonArgsList(String[] args) {
		for (int anIndex = 0; anIndex < args.length && !(args[anIndex].equals(PROJECT_FLAG)); anIndex++ ) {
			commonArgs.add(args[anIndex]);
		}
	}
	
	static void makeProjectArgsList(String[] args, int aStartIndex) {
		argsList.clear();
		argsList.addAll(commonArgs);
		for (int anIndex = aStartIndex; anIndex < args.length && !(args[anIndex].equals("-p")); anIndex++ ) {
			argsList.add(args[anIndex]);
		}
		
		
	}	
	public static void main (String[] args) {
		
		if (args.length < 3) {
			System.err.println ("Usage: -c config source");
			return;
		}
		makeCommonArgsList(args);
		UNCCheck.setManualProjectDirectory(true);
		ACheckStyleLogFileManager.setPrintLogInconsistency(false);
		STBuilderCheck.setNonInteractive(true);
		UNCCheck.setDoNotVisit(true);
//		PrintStream oldOut = System.out;
//
//		try {
//			File aDummyFile = new File(PostProcessingMain.DUMMY_FILE_NAME);
//			aDummyFile.createNewFile();
//			PrintStream aPrintStream = new PrintStream(aDummyFile);
//			System.setOut(aPrintStream);
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		int aProjectNumber = 0;
		for (int aStartIndex = commonArgs.size() + 1; aStartIndex < args.length;   ) {
			makeProjectArgsList(args, aStartIndex);
			int aNumFilesAdded = argsList.size() - commonArgs.size();
			if (aNumFilesAdded == 0) break;
			String aProjectName = "project" + aProjectNumber;

			ProjectDirectoryHolder.setCurrentProjectDirectory(aProjectName);
			createSymbolTable();
			aStartIndex += aNumFilesAdded + 1; // #args
			aProjectNumber++;

		}
	

//		System.setOut(oldOut);

		

		
	}
	
	

}
