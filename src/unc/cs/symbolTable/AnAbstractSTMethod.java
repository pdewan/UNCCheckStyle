package unc.cs.symbolTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import unc.cs.checks.ComprehensiveVisitCheck;
import unc.cs.checks.STTypeVisited;
import unc.cs.checks.TagBasedCheck;
import unc.cs.checks.TypeVisitedCheck;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;

public abstract class AnAbstractSTMethod extends AnSTNameable implements STMethod {
//	final String declaringClass;
//	final String[] parameterTypes;
//	final boolean isPublic;
//	final boolean isProcedure;
//	final boolean isInstance;
//	final boolean isVisible;
	STType declaringSTType;
	Set<STMethod> allCalledMethods;
	Set<STMethod> allInternallyCalledMethods;
	
	// to avoid duplicates, set
//	Set<STMethod> allCallingMethods;
	Set<STMethod> allInternallyCallingMethods;
	Set<STMethod> callingMethods;
	Set<STMethod> internallyCallingMethods;
	Set<STType> callingTypes;



	protected  boolean isPublicGetter;
	protected  boolean isPublicSetter;
	protected  boolean isGetter;
	protected  boolean isSetter;
	protected  boolean isInit;
	protected  boolean isSynchronized;
	protected  String signature;
//	protected AccessModifier accessModifier = AccessModifier.PACKAGE;
	protected boolean isAbstract = false;
//	final STNameable[] tags;
//	final boolean assignsToGlobal;
//	final String[][] methodsCalled;
	public  static final String GET = "get";
	public  static final String SET = "set";
	public static final String INIT = "init";
	protected List<AccessModifierUsage> accessModifierUsage;
	
	
	public AnAbstractSTMethod(DetailAST ast, String name) {
		super(ast, name);		
//		isSetter = computeIsSetter();
//		isGetter = computeIsGetter();
//		isInit = computeIsInit();
//		signature = displayMethod();
	}
	protected void introspect() {
//		isPublicSetter = computeIsPublicSetter();
//		isPublicGetter = computeIsPublicGetter();
		computeIsSetter();
		computeIsGetter();
		isInit = computeIsInit();
		signature = toStringMethod();
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
	public boolean isPublicSetter() {
		return isPublicSetter;
	}
	@Override
	public boolean isPublicGetter() {
		return isPublicGetter;
	}
	@Override
	public boolean isSetter() {
		return isSetter;
	}
	@Override
	public boolean isGetter() {
		return isGetter;
	}
	
//	protected boolean computeIsPublicSetter() {
//		if (getParameterTypes() == null) {
//			return false;
//		}
//		return getName() != null && getName().startsWith(SET) &&
//				getName().length() > SET.length() &
//				isPublic() &&
//				getParameterTypes().length == 1 &&
//				isProcedure();
//	}
	protected void computeIsSetter() {
		if (getParameterTypes() == null) {
			return ;
		}
		isSetter = getName() != null && getName().startsWith(SET) &&
				getName().length() > SET.length() &
//				isPublic() &&
				getParameterTypes().length == 1 &&
				isProcedure();
		isPublicSetter = isSetter && isPublic();
	}
//	protected boolean computeIsPublicGetter() {
//		if (getParameterTypes() == null) {
//			return false;
//		}
//		return getName() != null && getName().startsWith(GET) &&
//				getName().length() > GET.length() &&
//				isPublic() &&
//				getParameterTypes().length == 0 &&
//				!isProcedure();
//	}
	protected void computeIsGetter() {
		if (getParameterTypes() == null) {
			return ;
		}
		isGetter = getName() != null && getName().startsWith(GET) &&
				getName().length() > GET.length() &&
//				isPublic() &&
				getParameterTypes().length == 0 &&
				!isProcedure();
		isPublicGetter = isGetter && isPublic();
	}
	protected boolean computeIsInit() {
		 return isInit(getName());
	 }
	 String toStringParameterTypes() {
		 if (getParameterTypes() == null)
			 return "null";
		 StringBuilder result = new StringBuilder();
		 
		 for (int i = 0; i < getParameterTypes().length; i++) {
			 if (i > 0) {
				 result.append(PARAMETER_SEPARATOR);
			 }
			 result.append(getParameterTypes()[i]);
		 }
		 return result.toString();
	 }
	 String toStringMethod() {
		 StringBuilder result = new StringBuilder();
		 String aStatic = isInstance()?"":"static ";
//		 String aPublic = isPublic()?"public":"";
		 result.append(aStatic);
		 String aSynchronized = isSynchronized()?"":"synchronized ";
		 result.append(aSynchronized);


		 result.append(ComprehensiveVisitCheck.toAccessString(getAccessToken()));

		 result.append(name);
		 result.append(":");
		 result.append(toStringParameterTypes());
		 result.append("->");
		 result.append(TypeVisitedCheck.toShortTypeName(getReturnType()));
		 return result.toString();

	 }
	 @Override
	 public  boolean isInit() {
			return isInit;
		}
		public static boolean isInit(String aMethodName) {
			return aMethodName != null && aMethodName.startsWith(INIT);
		}
		public String toString() {
			return signature;
		}
		@Override
		public String getSignature() {
			return signature;
		}
		public boolean equals (Object anotherObject) {
			if (anotherObject instanceof STMethod) {
				STMethod anotherSTMethod = (STMethod) anotherObject;
				return getSignature().equals(anotherSTMethod.getSignature());
			}
			return super.equals(anotherObject);
		}
		
		@Override
		public STType getDeclaringSTType() {
			if (declaringSTType == null) {
				declaringSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(getDeclaringClass());
			}
			return declaringSTType;
			
		}
		static STMethod[] emptySTMethods = {};
		//should check length of argument list
		public static STMethod[] toSTMethods (CallInfo aCallInfo) {
			
			String[] aCalledMethod = aCallInfo.getNormalizedCall();
			String aCalledMethodName = aCalledMethod[1];
			String aCalledMethodClassName = aCalledMethod[0];
			if (aCalledMethod.length > 2 || aCalledMethodClassName == null || TagBasedCheck.isExternalClass(aCalledMethodClassName))
				return emptySTMethods;
			STType aCalledMethodClass = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aCalledMethodClassName);
			if (aCalledMethodClass == null) {
//				System.err.println("Null called method class:" + aCalledMethodClassName);
				return null;
			}
			return aCalledMethodClass.getDeclaredMethods(aCalledMethodName);			
		}
		@Override
		public Set<STMethod> getAllDirectlyOrIndirectlyCalledMethods() {
			if (allCalledMethods == null || isIndirectMethodsNotFullProcessed()) {
				allCalledMethods = computeAllDirectlyOrIndirectlyCalledMethods(new HashSet(), this);
			}
			if (allCalledMethods != null)
				addCallerMethod(this, allCalledMethods);
			return allCalledMethods;
		}
		
//		@Override
//		public Set<STMethod> getAllCallingMethods() {
//			return allCallingMethods;
//		}
		@Override
		public Set<STMethod> getCallingMethods() {
			return callingMethods;
		}
		
		@Override
		public Set<STType> getCallingTypes() {
			return callingTypes;
		}
		@Override
		public List<AccessModifierUsage> getAccessModifiersUsed() {
			if (accessModifierUsage == null) {
				accessModifierUsage = AnSTVariable.getAccessModifiersUsed (this, getAccessModifier(), this.getDeclaringSTType(), callingTypes, getCallingMethods());
			}
			return accessModifierUsage;
//			return AnSTVariable.getAccessModifiersUsed (this, getAccessModifier(), this.getDeclaringSTType(), callingTypes, getCallingMethods());
			
		}
		
		@Override	
		public Set<STMethod> getInternallyCallingMethods() {
			if (allInternallyCalledMethods == null) {
				Set<STMethod> result = new HashSet();
				allInternallyCalledMethods = computeAllInternallyDirectlyOrIndirectlyCalledMethods(result, this);
			}
			if (allInternallyCalledMethods != null)
				addCallerMethod(this, allInternallyCalledMethods);
			return internallyCallingMethods;
		}
//		static Set<STMethod> visitedInternalMethods = new HashSet();
		static Set<STMethod> visitedInternalOrExternalMethods = new HashSet();

		@Override
		public Set<STMethod> getAllInternallyDirectlyAndIndirectlyCalledMethods() {
			if (allInternallyCalledMethods == null) {
//				allInternallyCalledMethods = computeAllInternallyCalledMethods(this);
				Set<STMethod> result = new HashSet();
				allInternallyCalledMethods = computeAllInternallyDirectlyOrIndirectlyCalledMethods(result, this);
			}
			return allInternallyCalledMethods;
//			allInternallyCalledMethods = 
//			computeAllInternallyDirectlyOrIndirectlyCalledMethods(this);
			

//			return allInternallyCalledMethods;
		}
		@Override
		public Set<STMethod> getAllInternallyCallingMethods() {
			return allInternallyCallingMethods;
		}
		

		public void addCaller(STMethod aMethod) {
			if (callingMethods == null)
				callingMethods = new HashSet();
			if (callingTypes == null) {
				callingTypes = new HashSet();
			}
			callingMethods.add(aMethod);
			callingTypes.add(aMethod.getDeclaringSTType());
//			allCallingMethods.add(aMethod); // what is the difference?
			if (aMethod.getDeclaringClass().equals(getDeclaringClass())) {
				if (internallyCallingMethods == null) {
					internallyCallingMethods = new HashSet();
				}
				internallyCallingMethods.add(aMethod);
			}
		}
		
		public static void addCallerMethod(STMethod aCallingMethod, Set<STMethod> aCalledMethods) {
			for (STMethod aCalledMethod:aCalledMethods) {
				aCalledMethod.addCaller(aCallingMethod);
			}
		}
		protected boolean indirectMethodsNotFullProcessed = false;
		// duplicates in MethodEffectCheck: 	 protected Boolean checkCalledMethodsOf (STMethod aMethod) {	

// deprecated by getAllLocalMethods and getAllMethods
		@Override
		public boolean isIndirectMethodsNotFullProcessed() {
			return indirectMethodsNotFullProcessed;
		}
		public  Set<STMethod> computeAllDirectlyOrIndirectlyCalledMethods (Set<STMethod> result, STMethod aMethod) {
//			Set<STMethod> result = new HashSet();
//			if (visitedInternalOrExternalMethods.contains(aMethod))
//				return aMethod.getAllDirectlyOrIndirectlyCalledMethods();
//			visitedInternalOrExternalMethods.add(aMethod); // prevent recursion
//			STType aDeclaringType = aMethod.getDeclaringSTType();
//			if (aDeclaringType == null) {
//				System.err.println("Declaring type should not be null");
//				return null;
//			}
			CallInfo[] aCalledMethods = aMethod.getCallInfoOfMethodsCalled();
			for (CallInfo aCallInfo:aCalledMethods) {
				STMethod[] anAllDirectlyCalledMethods = toSTMethods(aCallInfo);
				if (anAllDirectlyCalledMethods == null) {
//					return null;
					indirectMethodsNotFullProcessed = true;
					continue;
				}
					
//				result.addAll(Arrays.asList(anAllDirectlyCalledMethods));
				for (STMethod aDirectlyCalledMethod:anAllDirectlyCalledMethods) {
					if (result.contains(aDirectlyCalledMethod)) {
						continue;
					}
//					aDirectlyCalledMethod.addCaller(aMethod);
					result.add(aDirectlyCalledMethod);
//					Set<STMethod> anAllIndirectlyCalledMethods = aDirectlyCalledMethod.getAllDirectlyOrIndirectlyCalledMethods();
					Set<STMethod> anAllIndirectlyCalledMethods = computeAllDirectlyOrIndirectlyCalledMethods(result, aDirectlyCalledMethod);

					if (anAllIndirectlyCalledMethods == null) {
//						return null;
						indirectMethodsNotFullProcessed = true;

						continue;
					}
					result.addAll(anAllIndirectlyCalledMethods);
				}
			}
			return result;			
		}
		/*
		 * got to think this through, recirsion problems
		 */
		public static Set<STMethod> computeAllInternallyCalledMethods (STMethod aMethod, Set<STMethod> result) {
//			Set<STMethod> result = new HashSet();
//			STType aDeclaringType = aMethod.getDeclaringSTType();
//			if (aDeclaringType == null) {
//				System.err.println("Declaring type should not be null");
//				return null;
//			}
			if (result.contains(aMethod))
				return result; // recursion
			CallInfo[] aCalledMethods = aMethod.getCallInfoOfMethodsCalled();
			for (CallInfo aCallInfo:aCalledMethods) {
				if (!aMethod.getDeclaringClass().contains(aCallInfo.getCalledType()))
						continue;
				String aCalledTypeShortName = ComprehensiveVisitCheck.toShortTypeOrVariableName(aCallInfo.getCalledType());
				// we did not capture the type
				if (Character.isLowerCase(aCalledTypeShortName.charAt(0)))
					continue;
				STMethod[] anAllDirectlyCalledMethods = toSTMethods(aCallInfo);
				if (anAllDirectlyCalledMethods == null) { // probbaly a call to a inner variable
//					System.err.println ("directly called methods should not be null");
					continue;
//					return null;
				}
				result.addAll(Arrays.asList(anAllDirectlyCalledMethods)); // these are in my class
				for (STMethod aDirectlyCalledMethod:anAllDirectlyCalledMethods) {
					
					Set<STMethod> anAllIndirectlyCalledMethods = aDirectlyCalledMethod.getAllInternallyDirectlyAndIndirectlyCalledMethods();
					if (anAllIndirectlyCalledMethods == null) {
						return null;
					}
					result.addAll(anAllIndirectlyCalledMethods);
				}
			}
			
			return result;
		}

		
		public static Set<STMethod> computeAllInternallyDirectlyOrIndirectlyCalledMethods (Set<STMethod> result, STMethod aMethod) {
			
//			Set<STMethod> result = new HashSet();
//			if (visitedInternalMethods.contains(aMethod))
//				return aMethod.getAllInternallyDirectlyAndIndirectlyCalledMethods();
//			visitedInternalMethods.add(aMethod); // prevent recursion
//			STType aDeclaringType = aMethod.getDeclaringSTType();
//			if (aDeclaringType == null) {
//				System.err.println("Declaring type should not be null");
//				return null;
//			}
			CallInfo[] aCalledMethods = aMethod.getCallInfoOfMethodsCalled();
			for (CallInfo aCallInfo:aCalledMethods) {
				if (!aMethod.getDeclaringClass().contains(aCallInfo.getCalledType()))
						continue;
				String aCalledTypeShortName = ComprehensiveVisitCheck.toShortTypeOrVariableName(aCallInfo.getCalledType());
				// we did not capture the type
				if (aCalledTypeShortName.length() == 0) {
//					System.err.println("Null string for short type name");
					continue;
				}
				if (Character.isLowerCase(aCalledTypeShortName.charAt(0)))
					continue;
				STMethod[] anAllDirectlyCalledMethods = toSTMethods(aCallInfo);
				if (anAllDirectlyCalledMethods == null) { // probbaly a call to a inner variable
//					System.err.println ("directly called methods should not be null");
					continue;
//					return null;
				}
//				result.addAll(Arrays.asList(anAllDirectlyCalledMethods)); // these are in my class
//				visitedInternalMethods.add(aMethod); // prevent recursion

//				visitedInternalMethods.addAll(result);
				for (STMethod aDirectlyCalledMethod:anAllDirectlyCalledMethods) {
					if (result.contains(aDirectlyCalledMethod)) {
						continue;
					}
					result.add(aDirectlyCalledMethod);
					
					Set<STMethod> anAllIndirectlyCalledMethods = computeAllInternallyDirectlyOrIndirectlyCalledMethods(result, aDirectlyCalledMethod);
//							aDirectlyCalledMethod.getAllInternallyDirectlyAndIndirectlyCalledMethods();
					if (anAllIndirectlyCalledMethods == null) {
						return null;
					}
//					result.addAll(anAllIndirectlyCalledMethods); // add empty if already visited
//					visitedInternalMethods.addAll(anAllIndirectlyCalledMethods);
//					visitedInternalMethods.add(aMethod); // do not wnat to recurse

				}
			}
//			visitedInternalMethods.add(aMethod); // this is when the visit ends
			return result;
			
			
		}
		@Override
		public Boolean callsInternally(STMethod anSTMethod) {
//			if (getAllInternallyCalledMethods() == null)
//				return null;
//			return getAllInternallyCalledMethods().contains(anSTMethod);
			if (getLocalCallClosure() == null)
				return null;
			return getLocalCallClosure().contains(anSTMethod);
			
		}
		@Override
		public Boolean calls(STMethod anSTMethod) {
			if (getAllDirectlyOrIndirectlyCalledMethods() == null)
				return null;
			return getAllDirectlyOrIndirectlyCalledMethods().contains(anSTMethod);			
		}
		@Override
		public void setDeclaringSTType(STType declaringSTType) {
			this.declaringSTType = declaringSTType;
			
		}
		static List<STNameable> emptyNameableList = new ArrayList();

		@Override
		public List<STNameable> getTypesInstantiated() {
			return emptyNameableList;
		}
		@Override
		public boolean isAbstract() {
			return isAbstract;
		}

		@Override
		public AccessModifier getAccessModifier() {
			return accessModifier;
		}
//		@Override
//		public boolean isParsedMethod() {
//			return true;
//		}
}
