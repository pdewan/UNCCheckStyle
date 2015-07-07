package unc.cs.symbolTable;

import java.util.List;
import java.util.Map;

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
	Boolean isNonType(String aTypeName);
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
}
