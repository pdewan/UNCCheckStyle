package unc.cs.symbolTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public interface STType extends STNameable{
	STMethod getGetter(String aPropertyName);
	STMethod getSetter(String aPropertyName);

	STMethod[] getDeclaredMethods();
	STMethod[] getMethods();
	STNameable[] getDeclaredInterfaces();
	STMethod getMethod(String aName, String[] aParameterTypes);
	String getPackage();
	boolean isInterface();
	boolean isEnum();
	STNameable getSuperClass();
//	void initDeclaredPropertyNames(STNameable[] aPropertyNames);
//	void initEditablePropertyNames(STNameable[] anEditablePropertyNames);
//	void initTags(STNameable[] aTags);
	STNameable getStructurePatternName();
//	void initStructurePatternName(STNameable structurePatternName);
	STNameable[] getDeclaredPropertyNames();
	STNameable[] getDeclaredEditablePropertyNames();
	STNameable[] getTags();	
	void introspect();
	STNameable[] getImports();
	STMethod[] getDeclaredConstructors();
	STNameable[] getAllDeclaredPropertyNames();
	Map<String, PropertyInfo> getDeclaredPropertyInfos();
	Map<String, PropertyInfo> getPropertyInfos();
	STMethod[] getMethods(String aName);
	List<STNameable> getAllTypes();
	List<String> getNonSuperTypes();
	Boolean isNonSuperType(String aTypeName);
	Boolean isType(String aTypeName);
	Boolean hasPublicMethod(String aSignature);
	Boolean hasDeclaredMethod(String aSignature);
	List<STNameable> getAllSuperTypes();
	List<String> getAllSignatures();
	List<String> getAllTypeNames();
	List<String> getSuperTypeNames();
	List<String> signaturesCommonWith(STType aType);
	List<String> signaturesCommonWith(String aTypeName);
	List<String> getSubTypes();
	List<String> getPeerTypes();
	List<STNameable> superTypesInCommonWith(String anOtherType);
	List<STNameable> superTypesInCommonWith(STType anOtherType);
	List<String> namesOfSuperTypesInCommonWith(String anOtherType);
	List<String> getPublicInstanceSignatures();
//	Boolean containsSignature(String aTypeName);
//	Boolean containsSignature(STType aType);
//	Boolean containsSignature(List<STType> aList);
	void findDelegateTypes();
	List<STNameable> getAllInterfaces();
	Boolean isSubtypeOf(String aName);
	Boolean isDelegate(String aName);
	boolean isParsedClass();
	Set<String> getDelegates();
	Boolean hasActualProperty(String aName);
	Boolean hasActualEditableProperty(String aName);
	List<PropertyInfo> propertiesCommonWith(STType aType);
	List<PropertyInfo> propertiesCommonWith(String aTypeName);
	List<STMethod> methodsCommonWith(STType aType);
	List<STMethod> methodsCommonWith(String aTypeName);
	boolean waitForSuperTypeToBeBuilt();
	STNameable[] getComputedTags();
	boolean hasSetter();
	STNameable[] getAllComputedTags();
	STMethod[] getDeclaredInitMethods();
	STMethod getDeclaredMethod(String aName, String[] aParameterTypes);
	STMethod[] getDeclaredMethods(String aName);
	List<String> getInstanceSignatures();
//	Set<String> getDeclaredGlobals();
//	String getDeclaredGlobalVariableType(String aGlobal);
	STVariable getDeclaredGlobalSTVariable(String aGlobal);
	STNameable[] getAllDeclaredEditablePropertyNames();
	List<CallInfo> getMethodsCalled();
	List<STNameable> getTypesInstantiated();
	List<STMethod> getInstantiatingMethods(String aTypeName);
	Boolean instantiatesType(String aShortOrLongName);
	List<CallInfo> getAllMethodsCalled();
//	DetailAST getDeclaredGlobalVariableToRHS(String aGlobal);
	List<STVariable> getDeclaredSTGlobals();
	List<String> getDeclaredPublicInstanceSignatures();
	STNameable[] getConfiguredTags();
	STNameable[] getDerivedTags();
	boolean isExternal();
	void setExternal(boolean external);
//	STVariable toSTVariable(String aName);
}
