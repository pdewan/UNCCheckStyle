package unc.cs.symbolTable;

public interface STClass extends STNameable{
	STMethod[] getDeclaredMethods();
	STMethod[] getMethods();
	String[] getInterfaces();
	STMethod[] getMethod(String aName, String[] aParameterTypes);
	String getPackage();
	boolean isInterface();
	String getSuperClass();
}
