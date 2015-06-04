package unc.cs.checks;

import java.util.List;
import java.util.Map;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public interface SymbolTable {
	public Map<String, DetailAST> getInterfaceNameToAST() ;
	public Map<String, DetailAST> getPackageNameToAST() ;
	public Map<String, DetailAST> getMethodCallToAST() ;
	public Map<String, DetailAST> getMethodDeclarationToAST() ;
	List<String> matchingFullClassNames(String aTypeName);
	List<String> matchingFullInterfaceNames(String aTypeName);
	List<String> matchingFullTypeNames(String aTypeName);
}
