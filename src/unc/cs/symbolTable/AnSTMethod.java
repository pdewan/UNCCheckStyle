package unc.cs.symbolTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import unc.cs.checks.ComprehensiveVisitCheck;
import unc.cs.checks.STTypeVisited;
import unc.cs.checks.TagBasedCheck;
import unc.cs.checks.TypeVisitedCheck;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTMethod extends AnAbstractSTMethod implements STMethod {
	final String declaringClass;
	final String[] parameterNames;
	final String[] parameterTypes;
	final boolean isPublic;
	final boolean isProcedure;
	final boolean isInstance;
	final boolean isVisible;
	final boolean isConstructor;
	// protected final boolean isGetter;
	// protected final boolean isSetter;
	// protected final boolean isInit;
	// protected final String signature;
	final STNameable[] tags;
	final STNameable[] computedTags;
	final boolean assignsToGlobal;
	// final String[][] methodsCalled;
	final CallInfo[] methodsCalled;
	// protected Set<STMethod> callingMethods = new HashSet();

	protected List<STMethod> localMethodsCalled;
	protected List<STMethod> localCallClosure;

	protected List<STMethod> allMethodsCalled;
	protected List<STMethod> allCallClosure;
	protected List<STNameable> typesInstantiated;
	protected List<String> globalsAssigned;
	protected List<String> globalsAccessed;
	protected List<String> unknownAccessed;

	protected List<String> unknownAssigned;

	protected List<STVariable> localSTVariables;

	protected List<STVariable> parameterSTVariables;
	protected List<STVariable> parametersAssigned;
	protected List<STVariable> localsAssigned;
	protected Integer accessToken;

	public static final String GET = "get";
	public static final String SET = "set";
	public static final String INIT = "init";
	protected String returnType;

	protected int numberOfTernaryConditionals;
	protected List<STType> asserts;

	static List<STNameable> anEmptyList = new ArrayList();

	public AnSTMethod(DetailAST ast, String name, String declaringClass, String[] aParameterNames,
			String[] parameterTypes, boolean isPublic, boolean anIsInstance, boolean anIsConstructor,
			boolean anIsSychronized, String returnType, boolean anIsVisible, STNameable[] aTags,
			STNameable[] aComputedTags, boolean isAssignsToGlobal,
			// String[][] aMethodsCalled
			CallInfo[] aMethodsCalled, List<STNameable> aTypesInstantiated, List<String> aGlobalsAccessed,
			List<String> aGlobalsAssigned, List<String> anUnknownAccessed, List<String> anUnknownAssigned,
			List<STVariable> aLocalVariables, List<STVariable> aParameters, List<STVariable> aLocalsAssigned,
			List<STVariable> aParametersAssigned,

			Integer anAccessToken, int aNumberOfTernaryOperators, List<STType> anAsserts) {
		super(ast, name);
		this.declaringClass = declaringClass;
		this.parameterTypes = parameterTypes;
		parameterNames = aParameterNames;
		this.isPublic = isPublic;
		isInstance = anIsInstance;
		if (returnType == null)
			this.returnType = declaringClass;
		else
			this.returnType = returnType;
		isProcedure = returnType == null || "void".equals(returnType);
		// return true;
		isVisible = anIsVisible;
		tags = aTags;
		computedTags = aComputedTags;
		assignsToGlobal = isAssignsToGlobal;
		methodsCalled = aMethodsCalled;
		isConstructor = anIsConstructor;
		isSynchronized = anIsSychronized;
		if (methodsCalled != null) {
			for (CallInfo aCallInfo : aMethodsCalled) {
				aCallInfo.setCallingMethod(this);
			}
		}
		typesInstantiated = aTypesInstantiated;
		globalsAssigned = aGlobalsAssigned;

		globalsAccessed = aGlobalsAccessed;
		unknownAccessed = anUnknownAccessed;
		unknownAssigned = anUnknownAssigned;
		localSTVariables = aLocalVariables;
		parameterSTVariables = aParameters;
		accessToken = anAccessToken != null ? anAccessToken : ComprehensiveVisitCheck.DEFAULT_ACCESS_TOKEN;
		numberOfTernaryConditionals = aNumberOfTernaryOperators;
		asserts = anAsserts;
		introspect();

	}

	@Override
	public void setDeclaringSTType(STType declaringSTType) {
		super.setDeclaringSTType(declaringSTType);
		if (globalsAssigned != null) {
			for (String aGlobal : globalsAssigned) {
				STVariable anSTVariable = getDeclaringSTType().getDeclaredGlobalSTVariable(aGlobal);
				if (anSTVariable != null) {
					anSTVariable.getMethodsAssigning().add(this);
				}

			}
			if (globalsAccessed != null) {
				for (String aGlobal : globalsAccessed) {
					STVariable anSTVariable = getDeclaringSTType().getDeclaredGlobalSTVariable(aGlobal);
					if (anSTVariable != null) {
						anSTVariable.getMethodsAccessing().add(this);
					}

				}
			}
		}

	}

	public String getDeclaringClass() {
		return declaringClass;
	}

	@Override
	public String[] getParameterNames() {
		return parameterNames;
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
	// @Override
	// public String[][] methodsCalled() {
	// return methodsCalled;
	// }

	@Override
	public CallInfo[] getMethodsCalled() {
		return methodsCalled;
	}
	
	public static STType getDeclaringISAClass(STNameable aCurrentClassName, CallInfo aCallInfo) {
		if (aCurrentClassName == null) {
			return null;
		}
		STType aCurrentClass = SymbolTableFactory.getSymbolTable().getSTClassByFullName(aCurrentClassName.getName());

		Set<STMethod> aMethods = ComprehensiveVisitCheck.getMatchingCalledMethods(aCurrentClass, aCallInfo);
		if (aMethods.size() > 0) {
			return aCurrentClass;
		}
		STNameable aSuperClassName = aCurrentClass.getSuperClass();
		return getDeclaringISAClass(aSuperClassName, aCallInfo);
		
	}
	
	public void correctCallers () {
		for (CallInfo aCall : methodsCalled) {
			if (!aCall.hasUnknownCalledType()) {
				continue;
			}
			STNameable aCalledType = aCall.getCalledSTType();
			if (aCalledType == null || "super".equals(aCall.getCalledType())) {
				String aCalledTypeName = aCall.getCallingType();
				if (aCalledTypeName != null) {
					STType aCallingType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aCalledTypeName);
						aCalledType = aCallingType;
					}
				
			}
			STType anSTType = getDeclaringISAClass(aCalledType, aCall);
			if (anSTType == null) {
				continue;
			}
			aCall.setCalledSTType(anSTType);
		}
	}

	@Override
	public List<STMethod> getLocalMethodsCalled() {
		correctCallers();
		if (localMethodsCalled == null) {
			List<STMethod> aList = new ArrayList();
			for (CallInfo aCall : methodsCalled) {
				
				if (ComprehensiveVisitCheck.toShortTypeOrVariableName(aCall.getCalledType())
						.equals(ComprehensiveVisitCheck.toShortTypeOrVariableName(getDeclaringClass()))) {
					// STMethod anSTMethod = aCall.getMatchingCalledMethods();
					Set<STMethod> anSTMethods = aCall.getMatchingCalledMethods();

					if (anSTMethods == null) {
						System.err.println("Could not create local st method, misguessed target type:" + aCall);
						continue;
						// return null;
					}
					ComprehensiveVisitCheck.addAllNoDuplicates(aList, anSTMethods);

				}
			}
			localMethodsCalled = aList;
			for (STMethod aMethod : localMethodsCalled) {
				aMethod.addCaller(this);
			}
		}
		return localMethodsCalled;
	}

	@Override
	public List<STMethod> getAllMethodsCalled() {
		correctCallers();
		if (allMethodsCalled == null) {
			List<STMethod> aList = new ArrayList();
			for (CallInfo aCall : methodsCalled) {

				Set<STMethod> anSTMethods = aCall.getMatchingCalledMethods();
				if (anSTMethods == null) {
					return null;
				}
				// aList.add(anSTMethod);
				// ComprehensiveVisitCheck.addAllNoDuplicates(allMethodsCalled,
				// anSTMethods);
				ComprehensiveVisitCheck.addAllNoDuplicates(aList, anSTMethods);

			}
			allMethodsCalled = aList;
			for (STMethod aMethod : allMethodsCalled) {
				aMethod.addCaller(this);
			}
		}
		return localMethodsCalled;
	}

	@Override
	public List<STMethod> getLocalCallClosure() {
		if (localCallClosure == null) {
			List<STMethod> aList = new ArrayList();
			// localMethodsCalled = getLocalMethodsCalled();
			// if (localMethodsCalled == null) {
			// return null;
			// }
			fillLocalCallClosure(aList);
			if (aList.contains(null))
				return null;
			localCallClosure = aList;

		}
		return localCallClosure;
	}

	@Override
	public void fillLocalCallClosure(List<STMethod> aList) {

		localMethodsCalled = getLocalMethodsCalled();
		if (localMethodsCalled == null) {
			aList.add(null);
			return;
		}

		for (STMethod anSTMethod : localMethodsCalled) {
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
	public void fillAllCallClosure(List<STMethod> aList) {

		localMethodsCalled = getAllMethodsCalled();
		if (localMethodsCalled == null) {
			aList.add(null);
			return;
		}

		for (STMethod anSTMethod : aList) {
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

	// @Override
	// public boolean isSetter() {
	// return isSetter;
	// }
	// @Override
	// public boolean isGetter() {
	// return isGetter;
	// }
	//
	// protected boolean computeIsSetter() {
	// return getName().startsWith(SET) &&
	// isPublic() &&
	// getParameterTypes().length == 1 &&
	// isProcedure;
	// }
	// protected boolean computeIsGetter() {
	// return getName().startsWith(GET) &&
	// isPublic() &&
	// getParameterTypes().length == 0 &&
	// !isProcedure;
	// }
	// protected boolean computeIsInit() {
	// return isInit(getName());
	// }
	// String displayParameterTypes() {
	// StringBuilder result = new StringBuilder();
	// for (int i = 0; i < parameterTypes.length; i++) {
	// if (i > 0) {
	// result.append(",");
	// }
	// result.append(parameterTypes[i]);
	// }
	// return result.toString();
	// }
	// String displayMethod() {
	// StringBuilder result = new StringBuilder();
	// result.append(name);
	// result.append(":");
	// result.append(displayParameterTypes());
	// result.append("->");
	// result.append(TypeVisitedCheck.toShortTypeName(returnType));
	// return result.toString();
	//
	// }
	// @Override
	// public boolean isInit() {
	// return isInit;
	// }
	// public static boolean isInit(String aMethodName) {
	// return aMethodName.startsWith(INIT);
	// }
	// public String toString() {
	// return signature;
	// }
	// @Override
	// public String getSignature() {
	// return signature;
	// }
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

	// @Override
	// public STType getDeclaringSTType() {
	// return declaringSTType;
	// }
	@Override
	public Boolean instantiatesType(String aShortOrLongName) {
		for (STNameable anInsantiatedNameable : typesInstantiated) {
			String anInstantiatedType = ComprehensiveVisitCheck.toShortTypeOrVariableName(anInsantiatedNameable.getName());
			String anExpectedType = ComprehensiveVisitCheck.toShortTypeOrVariableName(aShortOrLongName);
			Boolean result = ComprehensiveVisitCheck.matchesType(anExpectedType, anInstantiatedType);
			if (result == null)
				return null;
			if (result)
				return result;
			// return result;
			// if (anInstantiatedType.equals(anExpectedType))
			// return true;
		}
		return false;
	}

	@Override
	public List<String> getGlobalsAssigned() {
		return globalsAssigned;
	}

	@Override
	public List<String> getGlobalsAccessed() {
		return globalsAccessed;
	}

	@Override
	public Integer getAccessToken() {
		return accessToken;
	}

	@Override
	public List<STVariable> getLocalVariables() {
		return localSTVariables;
	}

	@Override
	public List<STVariable> getParameters() {
		return parameterSTVariables;
	}

	@Override
	public boolean isSynchronized() {
		return isSynchronized;
	}

	@Override
	public int getNumberOfTernaryConditionals() {
		return numberOfTernaryConditionals;
	}

	@Override
	public List<STType> getAsserts() {
		return asserts;
	}

	@Override
	public int getNumberOfAsserts() {
		return asserts == null ? 0 : asserts.size();

	}
	@Override
	public List<String> getUnknownAccessed() {
		return unknownAccessed;
	}
	@Override
	public List<String> getUnknownAssigned() {
		return unknownAssigned;
	}

	@Override
	public void addFullNamesToUnknowns() {
		for (int i = 0; i < unknownAccessed.size(); i++) {
			String anUnknown = unknownAccessed.get(i);
			String aShortName = anUnknown;
			if (anUnknown.contains(".")) {
				if (anUnknown.startsWith("super") || anUnknown.startsWith("this") ) {
					aShortName = ComprehensiveVisitCheck.toShortTypeOrVariableName(anUnknown);
				} else {
					continue;
				}
			}
			String aFullName = TagBasedCheck.toLongVariableName(getDeclaringSTType(), aShortName);
			if (aFullName != aShortName) {
				unknownAccessed.set(i, aFullName);
				if (unknownAssigned != null) {
					int anAssignedIndex = unknownAssigned.indexOf(anUnknown);
					if (anAssignedIndex >= 0) {
						unknownAssigned.set(anAssignedIndex, aFullName);
					}
					anAssignedIndex = unknownAssigned.indexOf(aShortName);
					if (anAssignedIndex >= 0) {
						unknownAssigned.set(anAssignedIndex, aFullName);
					}
				}
			}
		}

		for (int i = 0; i < unknownAssigned.size(); i++) {
			String anUnknown = unknownAssigned.get(i);
			if (anUnknown.contains(".")) {
				continue;
			}
			String aFullName = TagBasedCheck.toLongVariableName(getDeclaringSTType(), anUnknown);
			if (aFullName != anUnknown) {
				unknownAssigned.set(i, aFullName);

			}
		}

	}

}
