package unc.tools.checkstyle;

import java.util.Map;

import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTable;
import unc.cs.symbolTable.SymbolTableFactory;

public class TestCreateMultipleSymbolTables {
	static final String SOURCE1 = "src/test";
	static final String SOURCE2 = "src/test/version1";

	static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";

	//static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION, "-f", "xml", SOURCE};
	static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION, "-p", SOURCE1, "-p", SOURCE2};
	
	public static void main (String[] args) {
		CreateMultipleSymbolTables.main(ARGS);
		Map<String, SymbolTable> aSymbolTables = SymbolTableFactory.getProjectSymbolTables();
		for (String aProject:aSymbolTables.keySet()) {
			System.err.println("Project:" + aProject + " -----------------------");
			SymbolTable aSymbolTable = aSymbolTables.get(aProject);
			for (String aTypeName:aSymbolTable.getTypeNamesKeySet()) {
				STType anSTType = aSymbolTable.getSTClassByFullName(aTypeName);
				System.err.println("Type:" + aTypeName);

			}
			
		}
	}

}
