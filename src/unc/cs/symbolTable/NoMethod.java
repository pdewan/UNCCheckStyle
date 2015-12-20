package unc.cs.symbolTable;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class NoMethod extends AnSTMethod implements STMethod{

	public NoMethod(DetailAST ast, String name, String declaringClass,
			String[] parameterTypes, boolean isPublic, boolean anIsInstance,
			boolean anIsConstructor, String returnType, boolean anIsVisible,
			STNameable[] aTags, STNameable[] aComputedTags,
			boolean isAssignsToGlobal, CallInfo[] aMethodsCalled) {
		super(ast, name, declaringClass, parameterTypes, isPublic, anIsInstance,
				anIsConstructor, returnType, anIsVisible, aTags, aComputedTags,
				isAssignsToGlobal, aMethodsCalled);
		// TODO Auto-generated constructor stub
	}
	public NoMethod() {
		super(null, null, null, null, false, false, false, null, false, null, null, false, null);
	}

}
