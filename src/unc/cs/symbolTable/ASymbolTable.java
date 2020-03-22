package unc.cs.symbolTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.cs.checks.STBuilderCheck;
import unc.cs.checks.TagBasedCheck;

public class ASymbolTable implements SymbolTable{
//	Map<String, DetailAST> classNameToAST = new HashMap<>();
//	Map<String, DetailAST> interfaceNameToAST = new HashMap<>();
//	Map<String, DetailAST> packageNameToAST = new HashMap();
	Set<String> packageNames =  new HashSet<>();
	Map<String, DetailAST> methodCallToAST = new HashMap();
//	Map<String, DetailAST> methodDeclarationToAST = new HashMap();
	Map<String, STType>   typeNameToSTClass = new HashMap<>();	
	@Override
//	public Map<String, DetailAST> getClassNameToAST() {
//		return classNameToAST;
//	}
//	public Map<String, DetailAST> getInterfaceNameToAST() {
//		return interfaceNameToAST;
//	}
//	public Map<String, DetailAST> getPackageNameToAST() {
//		return packageNameToAST;
//	}
	public Map<String, DetailAST> getMethodCallToAST() {
		return methodCallToAST;
	}
//	public Map<String, DetailAST> getMethodDeclarationToAST() {
//		return methodDeclarationToAST;
//	}
	public static boolean typeMatches(String aFullName, String aShortOrFullName) {
		return aFullName.equals(aShortOrFullName) || aFullName.endsWith("." + aShortOrFullName);
	}
	@Override
	public boolean isType(String aTypeName) {
		STType aClass = getSTClassByShortName(aTypeName);
		return aClass != null;
	}
//	@Override
//	public boolean isType(String aTypeName) {
//		return isInterface(aTypeName) || isClass(aTypeName);
//	}
	@Override
	public boolean isInterface (String aTypeName) {
		STType aClass = getSTClassByShortName(aTypeName);
		return aClass != null && aClass.isInterface();
//		return matchingFullInterfaceNames(aTypeName).size() >= 1;
	}
	@Override
	public boolean isClass (String aTypeName) {
		STType aClass = getSTClassByShortName(aTypeName);
		return aClass != null && !aClass.isInterface() && !aClass.isEnum();
//		return matchingFullClassNames(aTypeName).size() >= 1;
	}
//	@Override
//	public List<String> matchingFullClassNames (String aTypeName) {
//		List<String> result = new ArrayList();
//		Set<String> aClassNames = classNameToAST.keySet();
//		for (String aFullName:aClassNames) {
//			if (typeMatches(aFullName, aTypeName)) {
//				result.add(aFullName);
//			}
//		}
//		return result;
//	}
	@Override
	public List<String> matchingFullSTTypeNames (String aTypeName) {
		List<String> result = new ArrayList();
		Set<String> aFullNames = typeNameToSTClass.keySet();
		for (String aFullName:aFullNames) {
			if (typeMatches(aFullName, aTypeName)) {
				result.add(aFullName);
			}
		}
		return result;
	}
//	@Override
//	public List<String> matchingFullInterfaceNames (String aTypeName) {
//		List<String> result = new ArrayList();
//		Set<String> aClassNames = interfaceNameToAST.keySet();
//		for (String aFullName:aClassNames) {
//			if (typeMatches(aFullName, aTypeName)) {
//				result.add(aFullName);
//			}
//		}
//		return result;
//	}
//	@Override
//	public List<String> matchingFullTypeNames (String aTypeName) {
//		List<String> result = matchingFullClassNames(aTypeName);
//		result.addAll(matchingFullInterfaceNames(aTypeName));
//		return result;
//	}
	@Override
	public STType getSTClassByShortName(String aTypeName) {
		STType retVal = getSTClassByFullName(aTypeName);
		if (retVal != null) {
			return retVal;
		}
		List<String> aFullNames =  matchingFullSTTypeNames(aTypeName);
		if (aFullNames.size() == 0) {
//			System.err.println("No full type name with short name" + aTypeName);
			return null;
		}
		if (aFullNames.size()> 1) {
//			System.err.println("Ambiguous short type names in " + aFullNames + ", returning null:" + aTypeName);
			return null;
		}
		return getSTClassByFullName(aFullNames.get(0));
	}
	@Override
	public STType getSTClassByFullName(String aTypeName) {
//		boolean isExternalClass = aTypeName.startsWith("java.lang") || STBuilderCheck.isExternalImportCacheChecking(aTypeName);
//		if (STBuilderCheck.isJavaLangClass(aTypeName)) {
//			if (Character.isUpperCase(aTypeName.charAt(0))) {
//				aTypeName = "java.lang." + aTypeName;
//				isExternalClass = true;
//			}
//		
//		}
		STType anSTType = typeNameToSTClass.get(aTypeName);
//		if (anSTType == null) {
//			System.out.println("Null ST Type:" + aTypeName);
//			System.out.println(" STable:" + typeNameToSTClass.keySet());
//			for (String aKey:typeNameToSTClass.keySet()) {
//				System.out.println("Type:" + aKey);
//			}
//
//		}
		if (anSTType == null) {
			boolean isExternalClass = aTypeName.startsWith("java.lang") || 
					STBuilderCheck.isExternalImportCacheChecking(aTypeName) ;
					
			if (!isExternalClass && STBuilderCheck.isJavaLangClass(aTypeName)) {
				if (Character.isUpperCase(aTypeName.charAt(0))) {
					aTypeName = "java.lang." + aTypeName;
					isExternalClass = true;
				}
			
			}
			if (!isExternalClass) {
				isExternalClass = STBuilderCheck.isExternalImportCacheChecking(aTypeName);
			}
			if (isExternalClass && STBuilderCheck.getImportsAsExistingClasses()) {
				try {
					Class aClass = Class.forName(aTypeName);
					return STBuilderCheck.addExistingClassSTType(aClass);

				} catch (ClassNotFoundException e) {
					return STBuilderCheck.addExistingClassSTType(aTypeName);
				}
			}
		}
		return anSTType;
//		return anSTType;

	}
//	@Override
//	public Map<String, STType> getTypeNameToSTClass() {
//		return typeNameToSTClass;
//	}
	@Override
	public STType putSTType(String aName, STType anSTType) {
		packageNames.add(aName);
		return typeNameToSTClass.put(aName, anSTType);
	}
	@Override
	public List<String> getAllTypeNames() {
		return new ArrayList(typeNameToSTClass.keySet());
	}
	@Override
	public List<String> getAllClassNames() {
		List<String> aResult = new ArrayList();
		for (String aTypeName: typeNameToSTClass.keySet() ) {
			STType aType = typeNameToSTClass.get(aTypeName);
			if (!aType.isInterface())
				aResult.add(aTypeName);
		}
		return aResult;
		
	}
	@Override
	public List<String> getAllInterfaceNames() {
		List<String> aResult = new ArrayList();
		for (String aTypeName: typeNameToSTClass.keySet() ) {
			STType aType = typeNameToSTClass.get(aTypeName);
			if (aType.isInterface())
				aResult.add(aTypeName);
		}
		return aResult;
		
	}
	@Override
	public List<STType> getAllSTTypes() {
		return new ArrayList(typeNameToSTClass.values());
	}
	@Override
	public void clear() {
		methodCallToAST.clear();
		typeNameToSTClass.clear();
		
	}
	@Override
	public Set<String> getTypeNamesKeySet() {
		return typeNameToSTClass.keySet();
	}
	@Override
	public Set<String> getPackageNames() {
		return packageNames;
	}
}
