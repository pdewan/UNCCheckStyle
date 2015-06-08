package unc.cs.symbolTable;

public interface STMethod extends STElement{
	STClass getDeclaringClass();
	String getName();
	String[] getParameterTypes();
	String getReturnType();
}
