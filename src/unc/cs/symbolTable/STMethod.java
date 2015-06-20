package unc.cs.symbolTable;

public interface STMethod extends STNameable{
	String getDeclaringClass();
	String getName();
	String[] getParameterTypes();
	String getReturnType();
	boolean isPublic();
	boolean isVisible();
	STNameable[] getTags();
	boolean assignsToGlobal();
	String[][] methodsCalled();
}
