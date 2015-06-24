package unc.cs.symbolTable;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTMethod extends AnSTNameable implements STMethod {
	final String declaringClass;
	final String[] parameterTypes;
	final boolean isPublic;
	final boolean isProcedure;
	final boolean isInstance;
	final boolean isVisible;
	protected final boolean isGetter;
	protected final boolean isSetter;
	protected final boolean isInit;
	final STNameable[] tags;
	final boolean assignsToGlobal;
	final String[][] methodsCalled;
	public  static final String GET = "get";
	public  static final String SET = "set";
	public static final String INIT = "init";
	
	public AnSTMethod(DetailAST ast, String name, 
			String declaringClass, String[] parameterTypes,
			boolean isPublic, boolean anIsInstance, String returnType,
			boolean anIsVisible, STNameable[] aTags,
			boolean isAssignsToGlobal,
			String[][] aMethodsCalled) {
		super(ast, name);
		this.declaringClass = declaringClass;
		this.parameterTypes = parameterTypes;
		this.isPublic = isPublic;
		isInstance = anIsInstance;
		this.returnType = returnType;
		isProcedure = returnType == null || "void".equals(returnType);
//			return true;
		isVisible = anIsVisible;
		tags = aTags;
		assignsToGlobal = isAssignsToGlobal;
		methodsCalled = aMethodsCalled;
		isSetter = computeIsSetter();
		isGetter = computeIsGetter();
		isInit = computeIsInit();
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
	public boolean assignsToGlobal() {
		return assignsToGlobal;
	}
	@Override
	public String[][] methodsCalled() {
		return methodsCalled;
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}

	@Override
	public STNameable[] getTags() {
		return tags;
	}

	@Override
	public boolean isProcedure() {
		return isProcedure;
	}
	@Override
	public boolean isSetter() {
		return isSetter;
	}
	@Override
	public boolean isGetter() {
		return isGetter;
	}
	
	 boolean computeIsSetter() {
		return getName().startsWith(SET) &&
				isPublic() &&
				getParameterTypes().length == 1 &&
				isProcedure;
	}
	 boolean computeIsGetter() {
		return getName().startsWith(GET) &&
				isPublic() &&
				getParameterTypes().length == 0 &&
				!isProcedure;
	}
	 boolean computeIsInit() {
		 return isInit(getName());
	 }
	 @Override
	 public  boolean isInit() {
			return isInit;
		}
		public static boolean isInit(String aMethodName) {
			return aMethodName.startsWith(INIT);
		}

}
