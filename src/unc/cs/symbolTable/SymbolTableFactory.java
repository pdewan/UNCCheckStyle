package unc.cs.symbolTable;

import java.util.HashMap;
import java.util.Map;

import unc.tools.checkstyle.ProjectDirectoryHolder;


public class SymbolTableFactory {
	static Map<String, SymbolTable> projectToSymbolTable = new HashMap<>();
	
	public Map<String, SymbolTable> getProjectSymbolTables() {
		return projectToSymbolTable;
	}
	
	public static SymbolTable getOrCreateSymbolTable() {
		String aProjectDirectory = ProjectDirectoryHolder.getCurrentProjectDirectory();
		SymbolTable aSymbolTable = projectToSymbolTable.get(aProjectDirectory);
		if (aSymbolTable == null) {
			aSymbolTable = new ASymbolTable();
			projectToSymbolTable.put(aProjectDirectory, aSymbolTable);
		}
		return aSymbolTable;
	}

	public static SymbolTable getSymbolTable() {
		return projectToSymbolTable.get(ProjectDirectoryHolder.getCurrentProjectDirectory());
	}
	public static void reset() {
		projectToSymbolTable.clear();
	}

	public static void setSymbolTable(SymbolTable newValue) {
		String aProjectDirectory = ProjectDirectoryHolder.getCurrentProjectDirectory();
		projectToSymbolTable.put(aProjectDirectory, newValue);
	}
}
