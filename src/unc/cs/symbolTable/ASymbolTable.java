package unc.cs.symbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class ASymbolTable implements SymbolTable{
	Map<String, DetailAST> classNameToAST = new HashMap<>();
	Map<String, DetailAST> interfaceNameToAST = new HashMap<>();
	Map<String, DetailAST> packageNameToAST = new HashMap();
	Map<String, DetailAST> methodCallToAST = new HashMap();
	Map<String, DetailAST> methodDeclarationToAST = new HashMap();
	Map<String, STClass>   typeNameToSTClass = new HashMap<>();
	
	@Override
	public Map<String, DetailAST> getClassNameToAST() {
		return classNameToAST;
	}
	public Map<String, DetailAST> getInterfaceNameToAST() {
		return interfaceNameToAST;
	}
	public Map<String, DetailAST> getPackageNameToAST() {
		return packageNameToAST;
	}
	public Map<String, DetailAST> getMethodCallToAST() {
		return methodCallToAST;
	}
	public Map<String, DetailAST> getMethodDeclarationToAST() {
		return methodDeclarationToAST;
	}
	public static boolean typeMatches(String aFullName, String aShortOrFullName) {
		return aFullName.equals(aShortOrFullName) || aFullName.endsWith("." + aShortOrFullName);
	}
	@Override
	public boolean isType(String aTypeName) {
		return isInterface(aTypeName) || isClass(aTypeName);
	}
	@Override
	public boolean isInterface (String aTypeName) {
		return matchingFullInterfaceNames(aTypeName).size() >= 1;
	}
	@Override
	public boolean isClass (String aTypeName) {
		return matchingFullClassNames(aTypeName).size() >= 1;
	}
	@Override
	public List<String> matchingFullClassNames (String aTypeName) {
		List<String> result = new ArrayList();
		Set<String> aClassNames = classNameToAST.keySet();
		for (String aFullName:aClassNames) {
			if (typeMatches(aFullName, aTypeName)) {
				result.add(aFullName);
			}
		}
		return result;
	}
	@Override
	public List<String> matchingFullInterfaceNames (String aTypeName) {
		List<String> result = new ArrayList();
		Set<String> aClassNames = interfaceNameToAST.keySet();
		for (String aFullName:aClassNames) {
			if (typeMatches(aFullName, aTypeName)) {
				result.add(aFullName);
			}
		}
		return result;
	}
	@Override
	public List<String> matchingFullTypeNames (String aTypeName) {
		List<String> result = matchingFullClassNames(aTypeName);
		result.addAll(matchingFullInterfaceNames(aTypeName));
		return result;
	}	
}
