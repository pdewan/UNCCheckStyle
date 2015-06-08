package unc.cs.symbolTable;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTMethod extends AnSTAnnotatable implements STMethod {
	STClass declaringClass;
	String name;
	String[] parameterTypes;
	boolean isPublic;
	public AnSTMethod(DetailAST ast, String name, String[] actualParameters,
			STClass declaringClass, String name2, String[] parameterTypes,
			boolean isPublic, String returnType) {
		super(ast, name, actualParameters);
		this.declaringClass = declaringClass;
		name = name2;
		this.parameterTypes = parameterTypes;
		this.isPublic = isPublic;
		this.returnType = returnType;
	}
	String returnType;
	
	public STClass getDeclaringClass() {
		return declaringClass;
	}
	public String getName() {
		return name;
	}
	public String[] getParameterTypes() {
		return parameterTypes;
	}
	public String getReturnType() {
		return returnType;
	}
	public boolean isPublic() {
		return isPublic;
	}

}
