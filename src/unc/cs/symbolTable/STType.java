package unc.cs.symbolTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface STType extends STNameable{
	STMethod[] getDeclaredMethods();
	STMethod[] getMethods();
	STNameable[] getInterfaces();
	STMethod getMethod(String aName, String[] aParameterTypes);
	String getPackage();
	boolean isInterface();
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
	STNameable[] getPropertyNames();
	Map<String, PropertyInfo> getDeclaredPropertyInfos();
	Map<String, PropertyInfo> getPropertyInfos();
	STMethod[] getMethods(String aName);
	List<STNameable> getAllTypes();
	List<String> getNonSuperTypes();
	Boolean isNonSuperType(String aTypeName);
	Boolean isType(String aTypeName);
	Boolean hasPublicMethod(String aSignature);
	Boolean hasDeclaredMethod(String aSignature);
	List<STNameable> getSuperTypes();
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
	List<String> getSignatures();
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
}
