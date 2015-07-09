package unc.cs.symbolTable;

import unc.cs.checks.STTypeVisited;
import unc.cs.checks.TypeVisitedCheck;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public abstract class AnAbstractSTMethod extends AnSTNameable implements STMethod {
//	final String declaringClass;
//	final String[] parameterTypes;
//	final boolean isPublic;
//	final boolean isProcedure;
//	final boolean isInstance;
//	final boolean isVisible;
	protected  boolean isGetter;
	protected  boolean isSetter;
	protected  boolean isInit;
	protected  String signature;
//	final STNameable[] tags;
//	final boolean assignsToGlobal;
//	final String[][] methodsCalled;
	public  static final String GET = "get";
	public  static final String SET = "set";
	public static final String INIT = "init";
	
	public AnAbstractSTMethod(DetailAST ast, String name) {
		super(ast, name);		
//		isSetter = computeIsSetter();
//		isGetter = computeIsGetter();
//		isInit = computeIsInit();
//		signature = displayMethod();
	}
	protected void introspect() {
		isSetter = computeIsSetter();
		isGetter = computeIsGetter();
		isInit = computeIsInit();
		signature = displayMethod();
	}
	
//	public String getDeclaringClass() {
//		return declaringClass;
//	}
//	
//	public String[] getParameterTypes() {
//		return parameterTypes;
//	}
//	public String getReturnType() {
//		return returnType;
//	}
//	@Override
//	public boolean isPublic() {
//		return isPublic;
//	}
//	@Override
//	public boolean isInstance() {
//		return isInstance;
//	}
//	
//	@Override
//	public boolean assignsToGlobal() {
//		return assignsToGlobal;
//	}
//	@Override
//	public String[][] methodsCalled() {
//		return methodsCalled;
//	}
//
//	@Override
//	public boolean isVisible() {
//		return isVisible;
//	}
//
//	@Override
//	public STNameable[] getTags() {
//		return tags;
//	}
//
//	@Override
//	public boolean isProcedure() {
//		return isProcedure;
//	}
	@Override
	public boolean isSetter() {
		return isSetter;
	}
	@Override
	public boolean isGetter() {
		return isGetter;
	}
	
	protected boolean computeIsSetter() {
		return getName().startsWith(SET) &&
				isPublic() &&
				getParameterTypes().length == 1 &&
				isProcedure();
	}
	protected boolean computeIsGetter() {
		return getName().startsWith(GET) &&
				isPublic() &&
				getParameterTypes().length == 0 &&
				!isProcedure();
	}
	protected boolean computeIsInit() {
		 return isInit(getName());
	 }
	 String displayParameterTypes() {
		 StringBuilder result = new StringBuilder();
		 for (int i = 0; i < getParameterTypes().length; i++) {
			 if (i > 0) {
				 result.append(",");
			 }
			 result.append(getParameterTypes()[i]);
		 }
		 return result.toString();
	 }
	 String displayMethod() {
		 StringBuilder result = new StringBuilder();
		 result.append(name);
		 result.append(":");
		 result.append(displayParameterTypes());
		 result.append("->");
		 result.append(TypeVisitedCheck.toShortTypeName(getReturnType()));
		 return result.toString();

	 }
	 @Override
	 public  boolean isInit() {
			return isInit;
		}
		public static boolean isInit(String aMethodName) {
			return aMethodName.startsWith(INIT);
		}
		public String toString() {
			return signature;
		}
		@Override
		public String getSignature() {
			return signature;
		}

//		@Override
//		public boolean isParsedMethod() {
//			return true;
//		}
}
