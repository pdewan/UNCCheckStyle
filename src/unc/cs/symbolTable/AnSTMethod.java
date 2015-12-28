package unc.cs.symbolTable;

import java.util.ArrayList;
import java.util.List;

import unc.cs.checks.ComprehensiveVisitCheck;
import unc.cs.checks.STTypeVisited;
import unc.cs.checks.TypeVisitedCheck;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTMethod extends AnAbstractSTMethod  implements STMethod {
	final String declaringClass;
	final String[] parameterTypes;
	final boolean isPublic;
	final boolean isProcedure;
	final boolean isInstance;
	final boolean isVisible;
	final boolean isConstructor;
//	protected final boolean isGetter;
//	protected final boolean isSetter;
//	protected final boolean isInit;
//	protected final String signature;
	final STNameable[] tags;
	final STNameable[] computedTags;
	final boolean assignsToGlobal;
//	final String[][] methodsCalled;
	final CallInfo[] methodsCalled;
	protected List<STMethod> localMethodsCalled;
	protected List<STMethod> localCallClosure;
	
	protected List<STMethod> allMethodsCalled;
	protected List<STMethod> allCallClosure;
	protected List<STNameable> typesInstantiated;

	
	public  static final String GET = "get";
	public  static final String SET = "set";
	public static final String INIT = "init";
	
	public AnSTMethod(DetailAST ast, String name, 
			String declaringClass, String[] parameterTypes,
			boolean isPublic, boolean anIsInstance, 
			boolean anIsConstructor,
			String returnType,
			boolean anIsVisible, 
			STNameable[] aTags,
			STNameable[] aComputedTags,
			boolean isAssignsToGlobal,
//			String[][] aMethodsCalled
			CallInfo[] aMethodsCalled,
			List<STNameable> aTypesInstantiated
			) {
		super(ast, name);
		this.declaringClass = declaringClass;
		this.parameterTypes = parameterTypes;
		this.isPublic = isPublic;
		isInstance = anIsInstance;
		if (returnType == null)
			this.returnType = declaringClass;
		else
		this.returnType = returnType;
		isProcedure = returnType == null || "void".equals(returnType);
//			return true;
		isVisible = anIsVisible;
		tags = aTags;
		computedTags = aComputedTags;
		assignsToGlobal = isAssignsToGlobal;
		methodsCalled = aMethodsCalled;
		isConstructor = anIsConstructor;
		if (methodsCalled != null) {
		for (CallInfo aCallInfo:aMethodsCalled) {
			aCallInfo.setCallingMethod(this);
		}
		}
		typesInstantiated = aTypesInstantiated;
		introspect();
//		isSetter = computeIsSetter();
//		isGetter = computeIsGetter();
//		isInit = computeIsInit();
//		signature = displayMethod();
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
	public boolean isInstance() {
		return isInstance;
	}
	
	@Override
	public boolean assignsToGlobal() {
		return assignsToGlobal;
	}
//	@Override
//	public String[][] methodsCalled() {
//		return methodsCalled;
//	}
	
	@Override
	public CallInfo[] getMethodsCalled() {
		return methodsCalled;
	}
	@Override
	public List<STMethod> getLocalMethodsCalled() {
		if (localMethodsCalled == null) {
			List<STMethod> aList = new ArrayList();
			for (CallInfo aCall:methodsCalled) {
				if (ComprehensiveVisitCheck.toShortTypeName(aCall.getCalledType()).
						equals(ComprehensiveVisitCheck.toShortTypeName(getDeclaringClass()))) {
					int i = 0;
					STMethod anSTMethod = aCall.getCalledMethod();
					if (anSTMethod == null) {
						System.err.println("Could not create local st method, misguessed target type:" + aCall);
						continue;
//						return null;
					} 
					aList.add(anSTMethod);
											
				}
			}
			localMethodsCalled = aList;
		}
		return localMethodsCalled;
	}
	@Override
	public List<STMethod> getAllMethodsCalled() {
		if (allMethodsCalled == null) {
			List<STMethod> aList = new ArrayList();
			for (CallInfo aCall : methodsCalled) {

				STMethod anSTMethod = aCall.getCalledMethod();
				if (anSTMethod == null) {
					return null;
				}
				aList.add(anSTMethod);

			}
			allMethodsCalled = aList;
		}
		return localMethodsCalled;
	}
	@Override
	public List<STMethod> getLocalCallClosure() {
		if (localCallClosure == null) {
			List<STMethod> aList = new ArrayList();
//			localMethodsCalled = getLocalMethodsCalled();
//			if (localMethodsCalled == null) {
//				return null;
//			}
			fillLocalCallClosure(aList);
			if (aList.contains(null))
				return null;
			localCallClosure = aList;
		
		}
		return localCallClosure;
	}
	@Override
	public void  fillLocalCallClosure(List<STMethod> aList) {
	
			localMethodsCalled = getLocalMethodsCalled();
			if (localMethodsCalled == null) {
				aList.add(null);
				return;
			}
		
			for (STMethod anSTMethod:localMethodsCalled) {
				if (aList.contains(anSTMethod))
					continue;
				aList.add(anSTMethod);
				anSTMethod.fillLocalCallClosure(aList);
			}		
	}
	@Override
	public List<STMethod> getAllCallClosure() {
		if (allCallClosure == null) {
			List<STMethod> aList = new ArrayList();
			allMethodsCalled = getAllMethodsCalled();
			if (allMethodsCalled == null) {
				return null;
			}
			fillLocalCallClosure(aList);
			if (aList.contains(null))
				return null;
			localCallClosure = aList;
		
		}
		return localCallClosure;
	}
	@Override
	public void  fillAllCallClosure(List<STMethod> aList) {
	
			localMethodsCalled = getAllMethodsCalled();
			if (localMethodsCalled == null) {
				aList.add(null);
				return;
			}
		
			for (STMethod anSTMethod:aList) {
				if (aList.contains(anSTMethod))
					continue;
				aList.add(anSTMethod);
				anSTMethod.fillAllCallClosure(aList);
			}		
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
	
	
	
	
//	@Override
//	public boolean isSetter() {
//		return isSetter;
//	}
//	@Override
//	public boolean isGetter() {
//		return isGetter;
//	}
//	
//	protected boolean computeIsSetter() {
//		return getName().startsWith(SET) &&
//				isPublic() &&
//				getParameterTypes().length == 1 &&
//				isProcedure;
//	}
//	protected boolean computeIsGetter() {
//		return getName().startsWith(GET) &&
//				isPublic() &&
//				getParameterTypes().length == 0 &&
//				!isProcedure;
//	}
//	protected boolean computeIsInit() {
//		 return isInit(getName());
//	 }
//	 String displayParameterTypes() {
//		 StringBuilder result = new StringBuilder();
//		 for (int i = 0; i < parameterTypes.length; i++) {
//			 if (i > 0) {
//				 result.append(",");
//			 }
//			 result.append(parameterTypes[i]);
//		 }
//		 return result.toString();
//	 }
//	 String displayMethod() {
//		 StringBuilder result = new StringBuilder();
//		 result.append(name);
//		 result.append(":");
//		 result.append(displayParameterTypes());
//		 result.append("->");
//		 result.append(TypeVisitedCheck.toShortTypeName(returnType));
//		 return result.toString();
//
//	 }
//	 @Override
//	 public  boolean isInit() {
//			return isInit;
//		}
//		public static boolean isInit(String aMethodName) {
//			return aMethodName.startsWith(INIT);
//		}
//		public String toString() {
//			return signature;
//		}
//		@Override
//		public String getSignature() {
//			return signature;
//		}
		@Override
		public STNameable[] getComputedTags() {
		return computedTags;
	}

		@Override
		public boolean isParsedMethod() {
			return true;
		}
		@Override
		public boolean isConstructor() {
			return isConstructor;
		}
		@Override
		public List<STNameable> getTypesInstantiated() {
			return typesInstantiated;
		}
//		@Override
//		public STType getDeclaringSTType() {
//			return declaringSTType;
//		}
		@Override
		public boolean instantiatesType (String aShortOrLongName) {
			for (STNameable anInsantiatedNameable:typesInstantiated) {
				String anInstantiatedType = 
						ComprehensiveVisitCheck.toShortTypeName(anInsantiatedNameable.getName());
				String anExpectedType =
						ComprehensiveVisitCheck.toShortTypeName(aShortOrLongName);
				if (anInstantiatedType.equals(anExpectedType))
					return true;
			}
			return false;
		}
		
}
