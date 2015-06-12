package unc.cs.symbolTable;

public interface STType extends STNameable{
	STMethod[] getDeclaredMethods();
	STMethod[] getMethods();
	String[] getInterfaces();
	STMethod[] getMethod(String aName, String[] aParameterTypes);
	String getPackage();
	boolean isInterface();
	String getSuperClass();
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
}
