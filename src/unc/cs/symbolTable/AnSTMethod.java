package unc.cs.symbolTable;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTMethod extends AnSTNameable implements STMethod {
	String declaringClass;
	String[] parameterTypes;
	boolean isPublic;
	public AnSTMethod(DetailAST ast, String name, 
			String declaringClass, String[] parameterTypes,
			boolean isPublic, String returnType) {
		super(ast, name);
		this.declaringClass = declaringClass;
		this.parameterTypes = parameterTypes;
		this.isPublic = isPublic;
		this.returnType = returnType;
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

}
