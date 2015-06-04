package unc.cs.checks;

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
	public boolean isInterface (String aTypeName) {
		Set<String> anInterfaceNames = interfaceNameToAST.keySet();
		for (String aFullName:anInterfaceNames) {
			if (aFullName.contains(aTypeName)) return true;
		}
		return false;
	}
	public boolean isClass (String aTypeName) {
		return matchingFullClassNames(aTypeName).size() > 1;
	}
	@Override
	public List<String> matchingFullClassNames (String aTypeName) {
		List<String> result = new ArrayList();
		Set<String> aClassNames = classNameToAST.keySet();
		for (String aFullName:aClassNames) {
			if (aFullName.contains(aTypeName)) {
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
			if (aFullName.contains(aTypeName)) {
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
