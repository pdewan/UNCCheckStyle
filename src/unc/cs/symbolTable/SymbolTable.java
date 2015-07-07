package unc.cs.symbolTable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public interface SymbolTable {
//	public Map<String, DetailAST> getInterfaceNameToAST() ;
//	public Map<String, DetailAST> getPackageNameToAST() ;
	public Map<String, DetailAST> getMethodCallToAST() ;
//	public Map<String, DetailAST> getMethodDeclarationToAST() ;
//	List<String> matchingFullClassNames(String aTypeName);
//	List<String> matchingFullInterfaceNames(String aTypeName);
//	List<String> matchingFullTypeNames(String aTypeName);
	boolean isClass(String aTypeName);
	boolean isInterface(String aTypeName);
//	Map<String, DetailAST> getClassNameToAST();
	boolean isType(String aTypeName);
	Map<String, STType> getTypeNameToSTClass();
	STType getSTClassByShortName(String aTypeName);
	List<String> matchingFullSTTypeNames(String aTypeName);
	STType getSTClassByFullName(String aTypeName);
	List<String> getAllTypeNames();
	List<STType> getAllSTTypes();
	List<String> getAllInterfaceNames();
	List<String> getAllClassNames();
}
