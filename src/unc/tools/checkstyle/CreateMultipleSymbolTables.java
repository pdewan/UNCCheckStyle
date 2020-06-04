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
	public static void createSymbolTable() {

		try {
			
			System.err.println("Building symbol table:" + new Date(System.currentTimeMillis()));
			STBuilderCheck.setNonInteractive(true);
			UNCCheck.setDoNotVisit(true);
			PrintStream oldOut = System.out;

			try {
				File aDummyFile = new File(PostProcessingMain.DUMMY_FILE_NAME);
				aDummyFile.createNewFile();
				PrintStream aPrintStream = new PrintStream(aDummyFile);
				System.setOut(aPrintStream);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			NonExitingMain.main(myArgs);
			System.err.println("Symbol table size:" + SymbolTableFactory.getOrCreateSymbolTable().size());
			
		} catch (UnsupportedEncodingException | FileNotFoundException | CheckstyleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	public static void main (String[] args) {
		
		if (args.length < 3) {
			System.err.println ("Usage: -c config source");
			return;
		}
		ACheckStyleLogFileManager.setPrintLogInconsistency(false);

		myArgs[0] = args[0];
		myArgs[1] = args[1];
		String aConfigurationFileName = args[1];
		for (int i = 2; i < args.length; i++) {
			myArgs[2] = args[i];
			createSymbolTable();
			
		}
		
	}
	
	

}
