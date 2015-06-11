package unc.cs.symbolTable;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTMethod extends AnSTNameable implements STMethod {
	final String declaringClass;
	final String[] parameterTypes;
	final boolean isPublic;
	final boolean isVisible;
	public AnSTMethod(DetailAST ast, String name, 
			String declaringClass, String[] parameterTypes,
			boolean isPublic, String returnType, boolean anIsVisible) {
		super(ast, name);
		this.declaringClass = declaringClass;
		this.parameterTypes = parameterTypes;
		this.isPublic = isPublic;
		this.returnType = returnType;
		isVisible = anIsVisible;
	}
	String returnType;
	
	public String getDeclaringClass() {
		return declaringClass;
	}
	
	public String[] getParameterTypes() {
		return parameterTypes;
	}
	public String getReturnType() {
		return returnType;
	}
	@Override
	public boolean isPublic() {
		return isPublic;
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}

}
