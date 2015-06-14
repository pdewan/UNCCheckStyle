package unc.cs.symbolTable;

import java.util.Map;

public interface STType extends STNameable{
	STMethod[] getDeclaredMethods();
	STMethod[] getMethods();
	STNameable[] getInterfaces();
	STMethod[] getMethod(String aName, String[] aParameterTypes);
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
}
