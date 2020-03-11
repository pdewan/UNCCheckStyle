package unc.cs.symbolTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import unc.cs.checks.ComprehensiveVisitCheck;
import unc.cs.checks.TagBasedCheck;
import unc.cs.checks.TypeVisitedCheck;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;
import com.sun.nio.sctp.SctpStandardSocketOptions.InitMaxStreams;

public abstract class AnAbstractSTType extends AnSTNameable implements STType {
	// protected STNameable[] declaredPropertyNames,
	// declaredEditablePropertyNames;
	// protected final STNameable[] imports;
	protected boolean external = false;
	
	protected STMethod staticBlocks ;
	

	protected STMethod[] initMethods;
//	protected List<STVariable>	globalSTVariables;


	protected STMethod[] declaredMethods; // initialized by subclass
	protected STMethod[] methods; // initialized on demand
	protected STNameable[] allComputedTags; // initialized on demand
	protected STMethod[] declaredConstructors;
	protected STNameable[] declaredInterfaces;
	protected STNameable[] interfaces;
	protected STNameable[] declaredFields;
	protected List<STVariable>	globalSTVariables;

	protected String packageName;
	// protected final boolean isInterface, isGeneric, isElaboration;
	protected STNameable superClass;
	// protected final STNameable structurePatternName;
	// protected STMethod[] inits;
	protected Map<String, PropertyInfo> declaredPropertyInfo = new HashMap();
	protected Map<String, PropertyInfo> allPropertyInfos; // initialized on demand
	// protected List<STMethod> declaredInits = new ArrayList();
	// protected Map<String, List<CallWithoutArguments>> globalVariableToCall =
	// new HashMap();
	protected Set<String> delegates = new HashSet();
	protected boolean hasSetter = false;
	protected List<String> subTypes;
	protected List<STNameable> superTypes;

	protected Set<STType> stSubTypes = new HashSet();
	protected  Set<STType> superClasses = new HashSet();
	protected STType stSuperClass;
	protected List<STNameable> allTypes;
//	protected AccessModifier accessModifier = AccessModifier.PACKAGE;
	protected boolean isAbstract = false;
	protected Set<STType> referenceTypes;
	protected List<AccessModifierUsage> accessModifierUsage;

//	Set<STType> superTypes = new HashSet();

	public AnAbstractSTType(DetailAST ast, String name) {
		super(ast, name);

	}
	@Override
	public boolean waitForSuperTypeToBeBuilt() {
		return true;
	}

	public STMethod[] getDeclaredMethods() {
		return declaredMethods;
	}

	@Override
	public STMethod[] getDeclaredConstructors() {
		return declaredConstructors;
	}

	public STNameable[] getDeclaredInterfaces() {
		return declaredInterfaces;
	}

	public String getPackage() {
		return packageName;
	}

	// public boolean isInterface() {
	// return isInterface;
	// }
	public static void addToList(List aList, Object[] anAdditions) {
		for (Object anAddition : anAdditions) {
			if (aList.contains(anAddition))
				continue;
			aList.add(anAddition);
		}
	}

	protected STMethod[] emptyMethods = new STMethod[0];
	protected List<CallInfo> emptyCalls = new ArrayList();
	

	@Override
	public STMethod[] getMethods() {
		if (methods == null) {
			methods = computeMethods();
			if (methods != null) {
				if (getStaticBlocks() != null) {
				getStaticBlocks().refreshUnknowns();
				}
				for (STMethod aMethod:methods) {
					aMethod.refreshUnknowns();
				}
			}
		}
		return methods;
//		List<STMethod> retVal = new ArrayList();
//		addToList(retVal, getDeclaredMethods());
//		STNameable aSuperType = getSuperClass();
//		if (aSuperType != null
//				&& !TagBasedCheck.isExternalClass(aSuperType.getName())) {
//			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//					.getSTClassByShortName(aSuperType.getName());
//			if (anSTType == null) {
//				if (waitForSuperTypeToBeBuilt())
//					return null;
//				else
//					return retVal.toArray(emptyMethods);
//			}
//			STMethod[] superTypeMethods = anSTType.getMethods();
//			if (superTypeMethods == null) // some supertype not compiled
//				return null;
////			addToList(retVal, anSTType.getMethods());
//			addToList(retVal, superTypeMethods);
//
//		}
//		return retVal.toArray(emptyMethods);
	}
	
	public STMethod[] computeMethods() {

		List<STMethod> retVal = new ArrayList();
		addToList(retVal, getDeclaredMethods());
		List<STNameable> anAllTypes = null;
		if (isInterface()) {
			anAllTypes = getAllSuperTypes();
		} else {
			anAllTypes = new ArrayList();
			STNameable aSuperClass = getSuperClass();
			if (aSuperClass != null) {
			anAllTypes.add(getSuperClass());
			}
		}
		if (anAllTypes == null) { // should never happen
			return null;
		}
		for (STNameable aSuperType:anAllTypes) {
			if (aSuperType == null || aSuperType.getName() == null) {
				return null;
			}
			if (TagBasedCheck.isExternalType(aSuperType.getName())) {
				continue;
			}
			Object aSuperTypeMethods = addMethodsOfSuperType(retVal, aSuperType);
			if (aSuperTypeMethods == null) {
				return null;
			}
		}
//		
//		STNameable aSuperType = getSuperClass(); // so this does not look at
//													// super interfaces
//		Object aSuperTypeMethods = addMethodsOfSuperType(retVal, aSuperType);
//		if (aSuperTypeMethods == null) {
//			return null;
//		}
		
//		if (aSuperType != null
//				&& !TagBasedCheck.isExternalClass(aSuperType.getName())) {
//			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//					.getSTClassByShortName(aSuperType.getName());
//			if (anSTType == null) {
//				if (waitForSuperTypeToBeBuilt())
//					return null;
//				else
//					return retVal.toArray(emptyMethods);
//			}
//			STMethod[] superTypeMethods = anSTType.getMethods();
//			if (superTypeMethods == null) // some supertype not compiled
//				return null;
//			// addToList(retVal, anSTType.getMethods());
//			addToList(retVal, superTypeMethods);
//
//		}
		return retVal.toArray(emptyMethods);
	}
public STMethod[] computeMethodsOld() {
		
		List<STMethod> retVal = new ArrayList();
		addToList(retVal, getDeclaredMethods());
		STNameable aSuperType = getSuperClass(); // so this does not look at super interfaces
		if (aSuperType != null
				&& !TagBasedCheck.isExternalClass(aSuperType.getName())) {
			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
					.getSTClassByShortName(aSuperType.getName());
			if (anSTType == null) {
				if (waitForSuperTypeToBeBuilt())
					return null;
				else
					return retVal.toArray(emptyMethods);
			}
			STMethod[] superTypeMethods = anSTType.getMethods();
			if (superTypeMethods == null) // some supertype not compiled
				return null;
//			addToList(retVal, anSTType.getMethods());
			addToList(retVal, superTypeMethods);

		}
		return retVal.toArray(emptyMethods);
	}
public List<STMethod>  addMethodsOfSuperType(List<STMethod> retVal, STNameable aSuperType) {
	

	if (aSuperType != null
			&& !TagBasedCheck.isExternalClass(aSuperType.getName())) {
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aSuperType.getName());
		if (anSTType == null) {
			if (waitForSuperTypeToBeBuilt())
				return null;
			else
				return retVal;
		}
		STMethod[] superTypeMethods = anSTType.getMethods();
		if (superTypeMethods == null) // some supertype not compiled
			return null;
//		addToList(retVal, anSTType.getMethods());
		addToList(retVal, superTypeMethods);

	}
	return retVal;
}
	
//	static STMethod[] emptySTMethods = {};
//	
//	public static STMethod[] toSTMethods (CallInfo aCallInfo) {
//		
//		String[] aCalledMethod = aCallInfo.getNormalizedCall();
//		String aCalledMethodName = aCalledMethod[1];
//		String aCalledMethodClassName = aCalledMethod[0];
//		if (aCalledMethod.length > 2 || aCalledMethodClassName == null || TagBasedCheck.isExternalClass(aCalledMethodClassName))
//			return emptySTMethods;
//		STType aCalledMethodClass = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aCalledMethodClassName);
//		if (aCalledMethodClass == null) {
////			System.err.println("Null called method class:" + aCalledMethodClassName);
//			return null;
//		}
//		return aCalledMethodClass.getMethods(aCalledMethodName);	
//		
//		
//	}
//
//	public static STMethod[] initAllCalledAndCallingMethods (STMethod aMethod) {
//		List<STMethod> result = new ArrayList();
////		STType aDeclaringType = aMethod.getDeclaringSTType();
////		if (aDeclaringType == null) {
////			System.err.println("Declaring type should not be null");
////			return null;
////		}
//		CallInfo[] aCalledMethods = aMethod.methodsCalled();
//		for (CallInfo aCallInfo:aCalledMethods) {
//			STMethod[] anAllDirectlyCalledMethods = toSTMethods(aCallInfo);
//			if (anAllDirectlyCalledMethods == null)
//				return null;
//			result.addAll(Arrays.asList(anAllDirectlyCalledMethods));
//			for (STMethod aDirectlyCalledMethod:anAllDirectlyCalledMethods) {
//				STMethod[] anAllIndirectlyCalledMethods = initAllCalledAndCallingMethods(aDirectlyCalledMethod);
//				if (anAllIndirectlyCalledMethods == null) {
//					return null;
//				}
//				result.addAll(Arrays.asList(anAllIndirectlyCalledMethods));
//			}
//		}
//		return result.toArray(emptySTMethods);
//		
//		
//	}
	
	

	
	public STNameable[] computeAllComputedTags() {
		List<STNameable> retVal = new ArrayList();
		addToList(retVal, getComputedTags());
		STNameable aSuperType = getSuperClass();
		if (aSuperType != null
				&& !TagBasedCheck.isExternalClass(aSuperType.getName())) {
			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
					.getSTClassByShortName(aSuperType.getName());
			if (anSTType == null) {
				if (waitForSuperTypeToBeBuilt())
					return null;
				else
					return retVal.toArray(emptyMethods);
			}
			STNameable[] superTypeTags = anSTType.getAllComputedTags();
			if (superTypeTags == null) // some supertype not compiled
				return null;
//			addToList(retVal, anSTType.getMethods());
			addToList(retVal, superTypeTags);

		}
		return retVal.toArray(emptyNameables);
	}
	public STNameable[] getAllComputedTags() {
		if (allComputedTags == null)
			allComputedTags = computeAllComputedTags();
		return allComputedTags;
//		List<STNameable> retVal = new ArrayList();
//		addToList(retVal, getComputedTags());
//		STNameable aSuperType = getSuperClass();
//		if (aSuperType != null
//				&& !TagBasedCheck.isExternalClass(aSuperType.getName())) {
//			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//					.getSTClassByShortName(aSuperType.getName());
//			if (anSTType == null) {
//				if (waitForSuperTypeToBeBuilt())
//					return null;
//				else
//					return retVal.toArray(emptyMethods);
//			}
//			STNameable[] superTypeTags = anSTType.getAllComputedTags();
//			if (superTypeTags == null) // some supertype not compiled
//				return null;
////			addToList(retVal, anSTType.getMethods());
//			addToList(retVal, superTypeTags);
//
//		}
//		return retVal.toArray(emptyNameables);
	}

	@Override
	public STMethod getMethod(String aName, String[] aParameterTypes) {
//		STMethod[] aMethods = getMethods();
//		if (aMethods == null) {
//			return null;
//		}
		return getMethod(getMethods(), aName, aParameterTypes);
//		for (STMethod aMethod : aMethods) {
//			if (aMethod.getName().equals(aName)
//					&& aMethod.getParameterTypes().equals(aParameterTypes))
//				return aMethod;
//		}
//		return null;
	}
	public static STMethod getMethod(STMethod[] aMethods, String aName, String[] aParameterTypes) {
		if (aMethods == null) {
			return null;
		}
//		int i = 5;
		for (STMethod aMethod : aMethods) {
			if (aMethod.getName().equals(aName)
					&& Arrays.asList(aMethod.getParameterTypes()).equals(Arrays.asList(aParameterTypes)))
				return aMethod;
		}
		return null;
	}
	@Override
	public STMethod getDeclaredMethod(String aName, String[] aParameterTypes) {
		return getMethod(getDeclaredMethods(), aName, aParameterTypes);

	}

	protected STMethod[] emptyMethodArray = new STMethod[0];
	public STMethod[] computeAllMethods(String aName) {
		List<STMethod> resultList = new ArrayList();
		STMethod[] aMethods = getMethods();

		if (aMethods == null) {
			if (waitForSuperTypeToBeBuilt())
				return null;
			else
				aMethods = getDeclaredMethods();
		}
		for (STMethod aMethod : aMethods) {
			if (aMethod.getName().equals(aName))
				resultList.add(aMethod);
		}
		return resultList.toArray(emptyMethodArray);

	}
	@Override
	public STMethod[] getMethods(String aName) {
		List<STMethod> resultList = new ArrayList();
		STMethod[] aMethods = getMethods();

		if (aMethods == null) {
			if (waitForSuperTypeToBeBuilt())
				return null;
			else
				aMethods = getDeclaredMethods();
		}
		for (STMethod aMethod : aMethods) {
			if (aMethod.getName().equals(aName))
				resultList.add(aMethod);
		}
		return resultList.toArray(emptyMethodArray);

	}
	@Override
	public STMethod[] getDeclaredMethods(String aName) {
		List<STMethod> resultList = new ArrayList();
		STMethod[] aMethods = getDeclaredMethods();

//		if (aMethods == null) {
//			if (waitForSuperTypeToBeBuilt())
//				return null;
//			else
//				aMethods = getDeclaredMethods();
//		}
		for (STMethod aMethod : aMethods) {
			if (aMethod.getName().equals(aName))
				resultList.add(aMethod);
		}
		return resultList.toArray(emptyMethodArray);

	}

	@Override
	public STNameable getSuperClass() {
		return superClass;
	}

	// @Override
	// public STNameable[] getDeclaredPropertyNames() {
	// return declaredPropertyNames;
	// }
	// recursion is safer
	@Override
	public STNameable[] getAllDeclaredPropertyNames() {
		List<STNameable> result = new ArrayList<>();
		// STNameable[] aPropertyNames = getDeclaredPropertyNames();
		STNameable[] aPropertyNames;

		STType anSTClass = this;
		while (true) {
			aPropertyNames = anSTClass.getDeclaredPropertyNames();
			for (STNameable aNameable : aPropertyNames) {
				result.add(aNameable);
			}
			STNameable aSuperClass = anSTClass.getSuperClass();
			if (aSuperClass == null
					|| TagBasedCheck.isExternalClass(aSuperClass.getName()))
				break;
			STType anSTSuperClass = SymbolTableFactory.getOrCreateSymbolTable()
					.getSTClassByShortName(aSuperClass.getName());
			if (anSTSuperClass == null) {
				if (anSTClass.waitForSuperTypeToBeBuilt())
					return null;
				else
					break;
				// return null; // assume that we are only inheriting our own
				// types
			}
			anSTClass = anSTSuperClass;

		}
		return result.toArray(new STNameable[0]);
	}
	@Override
	public STNameable[] getAllDeclaredEditablePropertyNames() {
		List<STNameable> result = new ArrayList<>();
		// STNameable[] aPropertyNames = getDeclaredPropertyNames();
		STNameable[] aPropertyNames;

		STType anSTClass = this;
		while (true) {
			aPropertyNames = anSTClass.getDeclaredEditablePropertyNames();
			for (STNameable aNameable : aPropertyNames) {
				result.add(aNameable);
			}
			STNameable aSuperClass = anSTClass.getSuperClass();
			if (aSuperClass == null
					|| TagBasedCheck.isExternalClass(aSuperClass.getName()))
				break;
			STType anSTSuperClass = SymbolTableFactory.getOrCreateSymbolTable()
					.getSTClassByShortName(aSuperClass.getName());
			if (anSTSuperClass == null) {
				if (anSTClass.waitForSuperTypeToBeBuilt())
					return null;
				else
					break;
				// return null; // assume that we are only inheriting our own
				// types
			}
			anSTClass = anSTSuperClass;

		}
		return result.toArray(new STNameable[0]);
	}
	// public void initDeclaredPropertyNames(STNameable[] propertyNames) {
	// this.declaredPropertyNames = propertyNames;
	// }
	// @Override
	// public STNameable[] getDeclaredEditablePropertyNames() {
	// return declaredEditablePropertyNames;
	// }
	// // public void initEditablePropertyNames(STNameable[]
	// editablePropertyNames) {
	// // this.declaredEditablePropertyNames = editablePropertyNames;
	// // }
	// @Override
	// public STNameable[] getTags() {
	// return tags;
	// }
	// @Override
	// public STNameable[] getImports() {
	// return imports;
	// }
	// public void initTags(STNameable[] tags) {
	// this.tags = tags;
	// }
	// @Override
	// public STNameable getStructurePatternName() {
	// return structurePatternName;
	// }
	// @Override
	// public void initStructurePatternName(STNameable structurePatternName) {
	// this.structurePatternName = structurePatternName;
	// }
	// public static final String GET = "get";
	// public static final String SET = "set";
	// public static final String INIT = "init";
	// public static boolean isInit(STMethod anSTMethod) {
	// return isInit(anSTMethod.getName());
	// }
	// public static boolean isInit(String aMethodName) {
	// return aMethodName.startsWith(INIT);
	// }
//	protected void maybeProcessInit(STMethod anSTMethod) {
//		// if (!anSTMethod.getName().startsWith(INIT)) return;
//		// if (isInit(anSTMethod)) return;
//		if (anSTMethod.isInit())
//			return;
//
//		String aPropertyName = anSTMethod.getName()
//				.substring(AnSTMethod.GET.length()).toLowerCase();
//		String aPropertyType = anSTMethod.getReturnType();
//		PropertyInfo aPropertyInfo = actualPropertyInfo.get(aPropertyName);
//		if (aPropertyInfo == null) {
//			aPropertyInfo = new APropertyInfo(aPropertyName, aPropertyType);
//			actualPropertyInfo.put(aPropertyName, aPropertyInfo);
//		}
//		aPropertyInfo.setGetter(anSTMethod);
//	}

	// public static boolean isGetter(STMethod anSTMethod) {
	// return anSTMethod.getName().startsWith(GET) &&
	// anSTMethod.isPublic() &&
	// anSTMethod.getParameterTypes().length == 0;
	// }

	protected void maybeProcessGetter(STMethod anSTMethod) {
	
//		if (!anSTMethod.isPublicGetter())
//			return;
		if (!anSTMethod.isGetter())
			return;
		
		String aPropertyName = anSTMethod.getName().substring(
				AnSTMethod.GET.length());
		String aPropertyType = anSTMethod.getReturnType();
		PropertyInfo aPropertyInfo = declaredPropertyInfo.get(aPropertyName);
		if (aPropertyInfo == null) {
			aPropertyInfo = new APropertyInfo(this, aPropertyName, aPropertyType);
			declaredPropertyInfo.put(aPropertyName, aPropertyInfo);
		}
		aPropertyInfo.setGetter(anSTMethod);
	}

	// public STNameable[] getDeclaredFields() {
	// return declaredFields;
	// }
	// public static boolean isSetter(STMethod anSTMethod) {
	// return anSTMethod.getName().startsWith(SET) &&
	// anSTMethod.isPublic() &&
	// anSTMethod.getParameterTypes().length != 1 &&
	// "void".equals(anSTMethod.getReturnType());
	// }
	protected void maybeProcessSetter(STMethod anSTMethod) {
	
//		if (!anSTMethod.isPublicSetter())
//			return;
		if (!anSTMethod.isSetter())
			return;
		hasSetter = true;
		
		String aPropertyName = anSTMethod.getName().substring(
				AnSTMethod.SET.length());

		String aPropertyType = anSTMethod.getParameterTypes()[0];
		PropertyInfo aPropertyInfo = declaredPropertyInfo.get(aPropertyName);
		if (aPropertyInfo == null) {
			aPropertyInfo = new APropertyInfo(this, aPropertyName, aPropertyType);
			declaredPropertyInfo.put(aPropertyName, aPropertyInfo);
		}
		aPropertyInfo.setSetter(anSTMethod);
	}
	@Override
	public boolean hasSetter() {
		return hasSetter;
	}
	@Override
	public void introspect() {
		for (STMethod anSTMethod : getDeclaredMethods()) {
			anSTMethod.setDeclaringSTType(this);
			maybeProcessGetter(anSTMethod);
			maybeProcessSetter(anSTMethod);
		}
	}

	// @Override
	// public void findDelegateTypes() {
	// Collection<List<CallWithoutArguments>> aCalls =
	// globalVariableToCall.values();
	// for (List<CallWithoutArguments> aCallList:aCalls){
	// for (CallWithoutArguments aCall:aCallList) {
	// if (aCall.getCalleee().equals(aCall.getCaller())) {
	// delegates.add(aCall.getCalledType());
	// }
	// }
	// }
	// }
	@Override
	public Boolean isSubtypeOf(String aName) {
		// STType anSTType =
		// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aName);
		// if (anSTType == null) return null;
		// List<STNameable> anAllTypes = anSTType.getAllTypes();
		// STType anSTType =
		// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aName);
		// if (anSTType == null) return null;
		List<STNameable> anAllTypes = getAllTypes();
		if (anAllTypes == null) {
			if (waitForSuperTypeToBeBuilt())
				return null;
			else
				return false;
		}
		for (STNameable aNameable : anAllTypes) {
			if (aNameable.getName().equals(aName))
				return true;
		}
		return false;
	}

	@Override
	public Boolean isDelegate(String aName) {
		for (String aDelegateType : getDelegates()) {
			if (aName.equals(aDelegateType))
				return true;
			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
					.getSTClassByShortName(aName);
			if (anSTType == null) {
				if (waitForSuperTypeToBeBuilt())
					return null;
				else
					continue;
			}
			
			Boolean isSubType = anSTType.isSubtypeOf(aDelegateType);
			if (isSubType == null)
				if (waitForSuperTypeToBeBuilt())
					return null;
				else
					continue;
//			if (anSTType.isSubtypeOf(aDelegateType))
			if (isSubType)

				return true;
		}
		return false;
	}

	@Override
	public Map<String, PropertyInfo> getDeclaredPropertyInfos() {
		return declaredPropertyInfo;
	}

	public static PropertyInfo getPropertyInfo(String aName,
			Map<String, PropertyInfo> aPropertyInfos) {
		if (aPropertyInfos == null)
			return null;
		Set<String> aPropertyNames = aPropertyInfos.keySet();
		for (String aPropertyName : aPropertyNames) {
			if (aPropertyName.equalsIgnoreCase(aName))
				return aPropertyInfos.get(aPropertyName);
		}
		return null;
	}

	@Override
	public Boolean hasActualProperty(String aName) {
		Map<String, PropertyInfo> aPropertyInfos = getPropertyInfos();
		if (aPropertyInfos == null) {
			if (waitForSuperTypeToBeBuilt())
				return null;
			else
				return false;
		}

		PropertyInfo aPropertyInfo = getPropertyInfo(aName, aPropertyInfos);
		return aPropertyInfo != null && aPropertyInfo.getGetter() != null;
	}

	@Override
	public Boolean hasActualEditableProperty(String aName) {
		Map<String, PropertyInfo> aPropertyInfos = getPropertyInfos();
		if (aPropertyInfos == null) {
			if (waitForSuperTypeToBeBuilt())
				return null;
			else
				return false;
		}
		PropertyInfo aPropertyInfo = getPropertyInfo(aName, aPropertyInfos);
		if (aPropertyInfo == null)
			return false;
		return (aPropertyInfo.getSetter() != null);

	}
	void mergingPut (Map<String, PropertyInfo> aPropertyInfos, String aProperty, PropertyInfo aPropertyInfo) {
		PropertyInfo anOriginal = aPropertyInfos.get(aProperty);
		if (anOriginal == null)
			aPropertyInfos.put(aProperty, aPropertyInfo);
		else
			merge (anOriginal, aPropertyInfo);
	}
	protected void merge (PropertyInfo anOriginal, PropertyInfo aNew ) {
		if (anOriginal.getGetter() == null)
			anOriginal.setGetter(aNew.getGetter());
		if (anOriginal.getSetter() == null)
			anOriginal.setSetter(aNew.getSetter());
	}
//	public Map<String, PropertyInfo> computeAllPropertyInfos() {
//		Map<String, PropertyInfo> result = new HashMap<>();
//		Map<String, PropertyInfo> aPropertyInfos = new HashMap();
//		STType anSTClass = this;
//		while (true) {
//			aPropertyInfos = anSTClass.getDeclaredPropertyInfos();
//			for (String aPropertyName : aPropertyInfos.keySet()) {
////				result.put(aPropertyName, aPropertyInfos.get(aPropertyName));
//				mergingPut(result, aPropertyName, aPropertyInfos.get(aPropertyName));
//			}
//			STNameable aSuperClass = anSTClass.getSuperClass();
//			if (aSuperClass == null
//					|| TagBasedCheck.isExternalClass(aSuperClass.getName()))
//				break;
//			STType aSuperClassSTType = SymbolTableFactory.getOrCreateSymbolTable()
//					.getSTClassByShortName(aSuperClass.getName());
//			if (aSuperClassSTType == null) {
//				if (anSTClass.waitForSuperTypeToBeBuilt()) {
//				return null; // assume that we are only inheriting our own types
//				} else {
//					break;
//				}
//					
//			}
//			anSTClass = aSuperClassSTType;
//		}
//		return result;
//	}
	public Map<String, PropertyInfo> computeAllPropertyInfos() {
		Map<String, PropertyInfo> result = new HashMap<>();		
		Map<String, PropertyInfo> aPropertyInfos = getDeclaredPropertyInfos();
		for (String aPropertyName : aPropertyInfos.keySet()) {
//			result.put(aPropertyName, aPropertyInfos.get(aPropertyName));
			mergingPut(result, aPropertyName, aPropertyInfos.get(aPropertyName));
		}
		
		List<STNameable> allSuperTypes = getAllSuperTypes();
		if (allSuperTypes == null)
			return null;
		for (STNameable aSuperType:allSuperTypes) {
			if (TagBasedCheck.isExternalClass(aSuperType.getName())) {
				continue;
			}
			STType aSuperClassSTType = SymbolTableFactory.getOrCreateSymbolTable()
					.getSTClassByShortName(aSuperType.getName());
			if (aSuperClassSTType ==null) {
				return null; // this should not happen
			} 
			Map<String, PropertyInfo> aSuperPropertyInfos = aSuperClassSTType.getPropertyInfos();
			if (aSuperPropertyInfos == null)
				return null;
			for (String aPropertyName : aSuperPropertyInfos.keySet()) {
//				result.put(aPropertyName, aPropertyInfos.get(aPropertyName));
				mergingPut(result, aPropertyName, aSuperPropertyInfos.get(aPropertyName));
			}
		}
		
		
		return result;
	}


	// // should use recursion actually
	@Override
	public Map<String, PropertyInfo> getPropertyInfos() {
		if (allPropertyInfos == null) {
			allPropertyInfos = computeAllPropertyInfos();
		}
//		allPropertyInfos = computeAllPropertyInfos();
		return allPropertyInfos;
//		Map<String, PropertyInfo> result = new HashMap<>();
//		Map<String, PropertyInfo> aPropertyInfos = new HashMap();
//		STType anSTClass = this;
//		while (true) {
//			aPropertyInfos = anSTClass.getDeclaredPropertyInfos();
//			for (String aPropertyName : aPropertyInfos.keySet()) {
//				result.put(aPropertyName, aPropertyInfos.get(aPropertyName));
//			}
//			STNameable aSuperClass = anSTClass.getSuperClass();
//			if (aSuperClass == null
//					|| TagBasedCheck.isExternalClass(aSuperClass.getName()))
//				break;
//			STType aSuperClassSTType = SymbolTableFactory.getOrCreateSymbolTable()
//					.getSTClassByShortName(aSuperClass.getName());
//			if (aSuperClassSTType == null) {
//				if (anSTClass.waitForSuperTypeToBeBuilt()) {
//				return null; // assume that we are only inheriting our own types
//				} else {
//					break;
//				}
//					
//			}
//			anSTClass = aSuperClassSTType;
//		}
//		return result;
	}
	public static List<STNameable> getAllTypes(STNameable aParentType, STType anOriginalType) {
		STType aParentSTType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aParentType.getName());
				if (aParentSTType == null) {
					if (anOriginalType.waitForSuperTypeToBeBuilt())
						return null;
					else
						return emptyList;
				} else {
					return getAllTypes(aParentSTType);
				}		
		
	}
	public static List<STNameable> getAllInterfaces(STNameable aParentType, STType anOriginalType) {
		STType aParentSTType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aParentType.getName());
				if (aParentSTType == null) {
					if (anOriginalType.waitForSuperTypeToBeBuilt())
						return null;
					else
						return emptyList;
				} else {
					return getAllInterfaces(aParentSTType);
				}		
		
	}

	public static List<STNameable> getAllTypes(STType anSTType) {
		if (TagBasedCheck.isExternalClass(TypeVisitedCheck
				.toShortTypeName(anSTType.getName())))
			return emptyList;
		List<STNameable> result = new ArrayList();
		result.add(anSTType);
		List<STNameable> aSuperTypes = getAllSuperTypes(anSTType);
		if (aSuperTypes == null)
			return null;


		addAllNonDuplicates(result, aSuperTypes);
		List<STNameable> anInterfaces = getAllInterfaces(anSTType);
		if (anInterfaces == null) 
			return null;
		addAllNonDuplicates(result, anInterfaces);
		// result.addAll(aSuperType);
		return result;
	}

	public static List<STNameable> getAllInterfaces(STType anSTType) {
		if (TagBasedCheck.isExternalClass(TypeVisitedCheck
				.toShortTypeName(anSTType.getName())))
			return emptyList;
		List<STNameable> result = new ArrayList();
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(aType.getName());
//		if (anSTType == null)
//			if (anSTType.waitForSuperTypeToBeBuilt())
//				return null;
//			else
//				return result;
		if (anSTType.isInterface()) {
//			System.err.println("An interface does not have an interface:"
//					+ anSTType.getName());
			return emptyList;
		}
//		if (anSTType.getName().contains("Vertical") || anSTType.getName().contains("Anim")) {
//		System.err.println("checking all ointerfaces " + anSTType.getName());
//	}
		STNameable[] anInterfaces = anSTType.getDeclaredInterfaces();
		for (STNameable anInterface : anInterfaces) {
//			STType anSTInterface = SymbolTableFactory.getOrCreateSymbolTable()
//			.getSTClassByShortName(anInterface.getName());
//			if (anSTInterface == null)
//				if (anSTType.waitForSuperTypeToBeBuilt())
//					return null;
//				else
//					continue;
			int i = 0;
			List<STNameable> anInterfaceTypes = getAllTypes(anInterface, anSTType);
			if (anInterfaceTypes == null) {
				if (TagBasedCheck.isExternalClass(TypeVisitedCheck
						.toShortTypeName(anInterface.getName()))) {
//					result.add(anInterface);
					anInterfaceTypes = new ArrayList();
					anInterfaceTypes.add(anInterface);
				}
				else if (anSTType.waitForSuperTypeToBeBuilt())
					return null;
				else
					continue;
			}
//			result.addAll(anInterfaceTypes);
			addAllNonDuplicates(result, anInterfaceTypes);
		}
		STNameable aSuperClass = anSTType.getSuperClass();
//		STType anSTSuperClass = SymbolTableFactory.getOrCreateSymbolTable()
//		.getSTClassByShortName(aSuperClass.getName());
//		if (anSTSuperClass == null)
//			if (anSTType.waitForSuperTypeToBeBuilt())
//				return null;
//			else
//				return result;
		 if (aSuperClass == null)
		 return result;
//		List<STNameable> aSuperInterfaces = getAllInterfaces(anSTSuperClass, anSTType);
		List<STNameable> aSuperInterfaces = getAllInterfaces(aSuperClass, anSTType);
		if (aSuperInterfaces == null) {
			if (anSTType.waitForSuperTypeToBeBuilt())
				return null;
			else
				return result;
		}
		addAllNonDuplicates(result, aSuperInterfaces);
		return result;
	}

	public static void addAllNonDuplicates(List aList, List anAdditions) {
		for (Object anAddition : anAdditions) {
			if (aList.contains(anAddition))
				continue;
			aList.add(anAddition);
		}
	}

	public static List emptyList = new ArrayList();
	public static STNameable[] emptyNameables = new STNameable[0];


	public static List<STNameable> getAllSuperTypes(STNameable aType) {
		if (TagBasedCheck.isExternalClass(TypeVisitedCheck
				.toShortTypeName(aType.getName())))
			return emptyList;
		List<STNameable> result = new ArrayList();
//		result.add(aType);

		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aType.getName());
		if (anSTType == null)
			return null;
		if (anSTType.isInterface()) {
			STNameable[] anInterfaces = anSTType.getDeclaredInterfaces();
			for (STNameable anInterface : anInterfaces) {
				if (result.contains(anInterface)) // an interface may be
													// extended by many
					continue;
				result.add(anInterface);
				List<STNameable> anInterfaceTypes = getAllSuperTypes(anInterface);
				if (anInterfaceTypes == null)
					if (anSTType.waitForSuperTypeToBeBuilt())
						return null;
					else
						continue;
				// result.addAll(anInterfaceTypes);
				addAllNonDuplicates(result, anInterfaceTypes);
			}
		} else {

			STNameable aSuperClass = anSTType.getSuperClass();
			if (aSuperClass == null)
				return result;
			result.add(aSuperClass);
			List<STNameable> aSuperTypes = getAllSuperTypes(aSuperClass);
			if (aSuperTypes == null) {
				if (anSTType.waitForSuperTypeToBeBuilt())
					return null; // not made completely
				else
					return result;
			}
			result.addAll(aSuperTypes);
			// addAllNonDuplicates(result, aSuperTypes);
		}
		return result;
	}
	
//	List<STNameable> allTypes;

	@Override
	public List<STNameable> getAllTypes() {
		if (allTypes == null) {
			List<STNameable> result = new ArrayList();
			allTypes = getAllTypes(this);
		}
//		List<STNameable> result = new ArrayList();
//		return getAllTypes(this);
		return allTypes;

	}
	List<STNameable> allInterfaces;
	@Override
	public List<STNameable> getAllInterfaces() {
		if (allInterfaces ==null) {
			
		
		List<STNameable> result = new ArrayList();
		allInterfaces = getAllInterfaces(this);
		}
		return allInterfaces;
	}
	
	
	public List<String> computeAllTypeNames() {
		List<STNameable> allTypes = getAllTypes();
		if (allTypes == null)
			if (waitForSuperTypeToBeBuilt())
				return null;
			else
				return emptyList;
		return toNameList(allTypes);
	}
	List<String> allTypeNames;


	@Override
	public List<String> getAllTypeNames() {
		if (allTypeNames == null) {
			allTypeNames = computeAllTypeNames();
		}
		return allTypeNames;
//		List<STNameable> allTypes = getAllTypes();
//		if (allTypes == null)
//			if (waitForSuperTypeToBeBuilt())
//				return null;
//			else
//				return emptyList;
//		return toNameList(allTypes);
	}
	
	public List<String> computeSuperTypeNames() {
		List<STNameable> aTypes = getAllSuperTypes();
		if (aTypes == null) {
			if (waitForSuperTypeToBeBuilt())
				return null;
			else
				return emptyList;
		}
		return toNameList(aTypes);
	}
	List<String> superTypeNames;
	@Override
	public List<String> getSuperTypeNames() {
		if (superTypeNames == null) {
			superTypeNames = computeSuperTypeNames();
		}
		return superTypeNames;
//		List<STNameable> aTypes = getSuperTypes();
//		if (aTypes == null) {
//			if (waitForSuperTypeToBeBuilt())
//				return null;
//			else
//				return emptyList;
//		}
//		return toNameList(aTypes);
	}
//	List<STNameable> superTypes;
	@Override
	public List<STNameable> getAllSuperTypes() {
		if (superTypes == null)
			superTypes = getAllSuperTypes(this);
//		superTypes = getAllSuperTypes(this);
		return superTypes;
		// List<STNameable> result = new ArrayList();
//		return getAllSuperTypes(this);

	}

	public static List<String> toNameList(List<STNameable> aNameableList) {
		if (aNameableList == null)
			return null;
		List<String> result = new ArrayList();
		for (STNameable aNameable : aNameableList) {
			String aShortName = TypeVisitedCheck.toShortTypeName(aNameable
					.getName());
			if (!result.contains(aShortName))
				result.add(aShortName);
		}
		return result;
	}

	public static List<String> toNormalizedList(List<String> anOriginal) {
		if (anOriginal == null)
			return null;
		List<String> result = new ArrayList();
		for (String aNonNormalizedEntry : anOriginal) {
			result.add(TypeVisitedCheck.toShortTypeName(aNonNormalizedEntry));
		}
		return result;
	}
	
	public List<String> computeNonSuperTypes() {
		SymbolTable aSymbolTable = SymbolTableFactory.getOrCreateSymbolTable();
		List<String> anAllTypes;
		if (isInterface())
			anAllTypes = aSymbolTable.getAllInterfaceNames();
		else
			anAllTypes = aSymbolTable.getAllClassNames();
		List<String> aNormalizedTypes = toNormalizedList(anAllTypes);
//		List<String> anAllMyTypes = toNameList(getAllSuperTypes());
		List<String> anAllMyTypes = toNameList(getAllTypes());

		return difference(aNormalizedTypes, anAllMyTypes);
	}
	List<String> nonSuperTypes;
	
	@Override
	public List<String> getNonSuperTypes() {
		if (nonSuperTypes == null)
			nonSuperTypes = computeNonSuperTypes();
		return nonSuperTypes;
//		SymbolTable aSymbolTable = SymbolTableFactory.getOrCreateSymbolTable();
//		List<String> anAllTypes;
//		if (isInterface())
//			anAllTypes = aSymbolTable.getAllInterfaceNames();
//		else
//			anAllTypes = aSymbolTable.getAllClassNames();
//		List<String> aNormalizedTypes = toNormalizedList(anAllTypes);
//		List<String> anAllMyTypes = toNameList(getSuperTypes());
//		return difference(aNormalizedTypes, anAllMyTypes);
	}
	

	/*
	 * remove delegates non super types
	 */

	public List<String> computeSubTypes() {
	
		List<String> aNonSuperTypes = getNonSuperTypes();
		List<String> result = new ArrayList();
		String myShortName = TypeVisitedCheck.toShortTypeName(name);
		for (String aNonSuperType : aNonSuperTypes) {
			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
					.getSTClassByShortName(aNonSuperType);
//			if (anSTType == null) {
//				System.err.println("nul st type");
//			}
			if (anSTType == null)
				return null;
			if (!anSTType.waitForSuperTypeToBeBuilt())
				continue;
			// makes sense to move it up
//			if (anSTType == null)
//				return null;
			List<String> aSuperTypes = toNormalizedList(anSTType
					.getSuperTypeNames());
			if (aSuperTypes == null)
				return null;
			if (aSuperTypes.contains(myShortName))
				result.add(aNonSuperType);
		}
//		result.remove(getShortName());
		return result;
	}
//	List<String> subTypes;
	public List<String> getSubTypes() {
		if (subTypes == null)
			subTypes = computeSubTypes();
		return subTypes;
//		List<String> aNonSuperTypes = getNonSuperTypes();
//		List<String> result = new ArrayList();
//		String myShortName = TypeVisitedCheck.toShortTypeName(name);
//		for (String aNonSuperType : aNonSuperTypes) {
//			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//					.getSTClassByShortName(aNonSuperType);
////			if (anSTType == null) {
////				System.err.println("nul st type");
////			}
//			if (anSTType == null)
//				return null;
//			if (!anSTType.waitForSuperTypeToBeBuilt())
//				continue;
//			// makes sense to move it up
////			if (anSTType == null)
////				return null;
//			List<String> aSuperTypes = toNormalizedList(anSTType
//					.getSuperTypeNames());
//			if (aSuperTypes == null)
//				return null;
//			if (aSuperTypes.contains(myShortName))
//				result.add(aNonSuperType);
//		}
//		return result;
	}
//	@Override
	public List<String> computePeerTypes() {
		List<String> aNonSuperTypes = toNormalizedList(getNonSuperTypes());
		if (aNonSuperTypes == null) {
			if (waitForSuperTypeToBeBuilt())
				return null;
			else
				return emptyList;
		}
		List<String> aSubTypes = toNormalizedList(getSubTypes());
		if (aSubTypes == null) {
			if (waitForSuperTypeToBeBuilt())
				return null;
			else
				return emptyList;
		}
		List<String> aResult = difference(aNonSuperTypes, aSubTypes);
		if (aResult != null) {
			aResult.remove(getShortName());
		}
		if (isInterface() || aResult == null)
			return aResult;
		// find delegates
		if (getDelegates().size() == 0)
			return aResult;
		List<String> aFinalResult = new ArrayList();
		for (String aType : aResult) {
			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
					.getSTClassByShortName(aType);
			if (anSTType == null)
				return null;
			Boolean isDelegate = isDelegate(aType);
			if (isDelegate == null) {
				if (waitForSuperTypeToBeBuilt())
					return null;
				else
					return emptyList;
			}
			Boolean isDelegator = anSTType.isDelegate(getShortName());
			if (isDelegator == null) {
				if (waitForSuperTypeToBeBuilt())
					return null;
				else
					return emptyList;
			}
			if (!isDelegator && !isDelegate) {
				// if (!delegates.contains(aType))
				aFinalResult.add(aType);
			}
		}
		return aFinalResult;
	}
	List<String> peerTypes;
	public List<String> getPeerTypes() {
		if (peerTypes == null)
			peerTypes = computePeerTypes();
		return peerTypes;
//		List<String> aNonSuperTypes = toNormalizedList(getNonSuperTypes());
//		if (aNonSuperTypes == null) {
//			if (waitForSuperTypeToBeBuilt())
//				return null;
//			else
//				return emptyList;
//		}
//		List<String> aSubTypes = toNormalizedList(getSubTypes());
//		if (aSubTypes == null) {
//			if (waitForSuperTypeToBeBuilt())
//				return null;
//			else
//				return emptyList;
//		}
//		List<String> aResult = difference(aNonSuperTypes, aSubTypes);
//		if (isInterface() || aResult == null)
//			return aResult;
//		// find delegates
//		if (getDelegates().size() == 0)
//			return aResult;
//		List<String> aFinalResult = new ArrayList();
//		for (String aType : aResult) {
//			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//					.getSTClassByShortName(aType);
//			if (anSTType == null)
//				return null;
//			Boolean isDelegate = isDelegate(aType);
//			if (isDelegate == null) {
//				if (waitForSuperTypeToBeBuilt())
//					return null;
//				else
//					return emptyList;
//			}
//			Boolean isDelegator = anSTType.isDelegate(getShortName());
//			if (isDelegator == null) {
//				if (waitForSuperTypeToBeBuilt())
//					return null;
//				else
//					return emptyList;
//			}
//			if (!isDelegator && !isDelegate) {
//				// if (!delegates.contains(aType))
//				aFinalResult.add(aType);
//			}
//		}
//		return aFinalResult;
	}

	public String getShortName() {
		return TypeVisitedCheck.toShortTypeName(getName());
	}

	@Override
	public Boolean isNonSuperType(String aTypeName) {
		return getNonSuperTypes().contains(
				TypeVisitedCheck.toShortTypeName(aTypeName));
	}

	@Override
	public Boolean isType(String aTypeName) {
		List<STNameable> aTypes = getAllTypes();
		if (aTypes == null) {
			if (waitForSuperTypeToBeBuilt())
				return null;
			else
				return false;
		}
		return toNameList(aTypes).contains(
				TypeVisitedCheck.toShortTypeName(aTypeName));
	}

	@Override
	public Boolean hasPublicMethod(String aSignature) {
		STMethod[] stMethods = getMethods();
		if (stMethods == null) {
			if (waitForSuperTypeToBeBuilt())
				return null;
			else
				return false;
		}
		return Arrays.asList(stMethods).contains(aSignature);
	}

	@Override
	public Boolean hasDeclaredMethod(String aSignature) {
		STMethod[] stMethods = getDeclaredMethods();
		if (stMethods == null)
			return null;
		return Arrays.asList(stMethods).contains(aSignature);
	}

	public static List intersect(Collection aList1, Collection aList2) {
		List aResult = new ArrayList();
		for (Object anElement1 : aList1) {
			for (Object anElement2 : aList2) {
				if (anElement1 == null) {
				System.err.println ("An lement 1" + anElement1 + " 2:" + anElement2);
//				continue;
				}
				
				if (anElement1.equals(anElement2)) {
					aResult.add(anElement1);
					break;
				}
			}
		}
		return aResult;
	}

	public static List difference(List aList1, List aList2) {

		List aResult = new ArrayList();
		if (aList2 == null) {
			aResult.addAll(aList1);
			return aResult;
		}
		for (Object anElement : aList1) {
			if (!aList2.contains(anElement))
				aResult.add(anElement);
		}

		return aResult;
	}

	public static List<STNameable> commonSuperTypes(String aType1, String aType2) {
		// List<STNameable> result = new ArrayList();
		STType anSTType1 = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aType1);
		if (anSTType1 == null)
			return null;
		STType anSTType2 = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aType2);
		if (anSTType2 == null)
			return null;
		// List<STNameable> aSuperTypes1 = anSTType1.getSuperTypes();
		// if (aSuperTypes1 == null)
		// return null;
		// List<STNameable> aSuperTypes2 = anSTType2.getSuperTypes();
		// if (aSuperTypes2 == null)
		// return null;
		// return intersect (aSuperTypes1, aSuperTypes2);
		return commonSuperTypes(anSTType1, anSTType2);
	}

	public static List<STNameable> commonSuperTypes(STType anSTType1,
			STType anSTType2) {
		//
		List<STNameable> aSuperTypes1 = anSTType1.getAllSuperTypes();
		if (aSuperTypes1 == null)
			return null;
		List<STNameable> aSuperTypes2 = anSTType2.getAllSuperTypes();
		if (aSuperTypes2 == null)
			return null;
		return intersect(aSuperTypes1, aSuperTypes2);
	}

	@Override
	public List<STNameable> superTypesInCommonWith(String anOtherType) {
		return commonSuperTypes(this.getName(), anOtherType);
	}

	@Override
	public List<String> namesOfSuperTypesInCommonWith(String anOtherType) {
		return toNameList(superTypesInCommonWith(anOtherType));
	}

	@Override
	public List<STNameable> superTypesInCommonWith(STType anOtherType) {
		return commonSuperTypes(this, anOtherType);
	}
	List<String> allSignatures;
	public List<String> computeAllSignatures() {
		List<String> result = new ArrayList();
		STMethod[] anSTMethods = getMethods();
		if (anSTMethods == null)
			return null;
		for (STMethod anSTMethod : anSTMethods) {
			result.add(anSTMethod.getSignature());
		}
		return result;
	}

	@Override
	public List<String> getAllSignatures() {
		if (allSignatures == null)
			allSignatures = computeAllSignatures();
		return allSignatures;
//		List<String> result = new ArrayList();
//		STMethod[] anSTMethods = getMethods();
//		if (anSTMethods == null)
//			return null;
//		for (STMethod anSTMethod : anSTMethods) {
//			result.add(anSTMethod.getSignature());
//		}
//		return result;
	}
	
	public List<String> computePublicInstanceSignatures() {
		List<String> result = new ArrayList();
		STMethod[] anSTMethods = getMethods();
		if (anSTMethods == null)
			return null;
		for (STMethod anSTMethod : anSTMethods) {
			if (anSTMethod.isPublic() && anSTMethod.isInstance())
				result.add(anSTMethod.getSignature());
		}
		return result;
	}
	@Override
	public List<String> getDeclaredPublicInstanceSignatures() {
		List<String> result = new ArrayList();
		STMethod[] anSTMethods = getDeclaredMethods();
		if (anSTMethods == null)
			return null;
		for (STMethod anSTMethod : anSTMethods) {
			if (anSTMethod.isPublic() && anSTMethod.isInstance())
				result.add(anSTMethod.getSignature());
		}
		return result;
	}
	public List<String> computeInstanceSignatures() {
		List<String> result = new ArrayList();
		STMethod[] anSTMethods = getMethods();
		if (anSTMethods == null)
			return null;
		for (STMethod anSTMethod : anSTMethods) {
			if (anSTMethod.isInstance())
				result.add(anSTMethod.getSignature());
		}
		return result;
	}
	
	List<String> publicInstanceSignatures;

	@Override
	public List<String> getPublicInstanceSignatures() {
		if (publicInstanceSignatures == null)
		   publicInstanceSignatures = computePublicInstanceSignatures();
		return publicInstanceSignatures;
		

	}
	
	List<String> instanceSignatures;

	@Override
	public List<String> getInstanceSignatures() {
		if (instanceSignatures == null)
			instanceSignatures = computeInstanceSignatures();
		return instanceSignatures;		

	}

	public static List<String> commonSignatures(String aType1, String aType2) {
		STType anSTType1 = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aType1);
		if (anSTType1 == null)
			return null;
		STType anSTType2 = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aType2);
		if (anSTType2 == null)
			return null;
		return commonSignatures(anSTType1, anSTType2);
		//
	}
	public static List<STMethod> commonMethods(String aType1, String aType2) {
		STType anSTType1 = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aType1);
		if (anSTType1 == null)
			return null;
		STType anSTType2 = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aType2);
		if (anSTType2 == null)
			return null;
		return commonMethods(anSTType1, anSTType2);
		//
	}

	public static List<String> commonSignatures(STType aType1, STType aType2) {
		List<String> aSignatures1 = aType1.getPublicInstanceSignatures();
		if (aSignatures1 == null)
			return null;
		List<String> aSignatures2 = aType2.getPublicInstanceSignatures();
		if (aSignatures2 == null)
			return null;
		return intersect(aSignatures1, aSignatures2);
		//
	}
	public static List<STMethod> commonMethods(STType aType1, STType aType2) {
		STMethod[] aMethods1 = aType1.getMethods();
		if (aMethods1 == null)
			return null;
		STMethod[] aMethods2 = aType2.getMethods();
		if (aMethods2 == null)
			return null;
		return intersect(Arrays.asList(aMethods1), Arrays.asList(aMethods2));
		//
	}
	public static List<PropertyInfo> commonProperties(String aType1, String aType2) {
		STType anSTType1 = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aType1);
		if (anSTType1 == null)
			return null;
		STType anSTType2 = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aType2);
		if (anSTType2 == null)
			return null;
		return commonProperties(anSTType1, anSTType2);
		//
	}
	public static List<PropertyInfo> commonProperties(STType aType1, STType aType2) {
		Map<String, PropertyInfo> anInfoMap1 = aType1.getPropertyInfos();
		if (anInfoMap1 == null)
			return null;
		Collection<PropertyInfo> anInfos1 = anInfoMap1.values();
		Map<String, PropertyInfo> anInfoMap2 = aType2.getPropertyInfos();
		if (anInfoMap2 == null)
			return null;
		Collection<PropertyInfo> anInfos2 = anInfoMap2.values();
		
		return intersect(anInfos1, anInfos2);
		//
	}

	@Override
	public List<String> signaturesCommonWith(STType aType) {
		return commonSignatures(this, aType);
	}
	
	@Override
	public List<STMethod> methodsCommonWith(STType aType) {
		return commonMethods(this, aType);
	}
	
	@Override
	public List<PropertyInfo> propertiesCommonWith(STType aType) {
		return commonProperties(this, aType);
	}

	@Override
	public List<String> signaturesCommonWith(String aTypeName) {
		STType aPeerType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aTypeName);
		if (aPeerType == null)
			return null;
		return commonSignatures(this, aPeerType);
	}
	@Override
	public List<STMethod> methodsCommonWith(String aTypeName) {
		STType aPeerType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aTypeName);
		if (aPeerType == null)
			return null;
		return methodsCommonWith(aPeerType);
	}
	@Override
	public List<PropertyInfo> propertiesCommonWith(String aTypeName) {
		STType aPeerType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aTypeName);
		if (aPeerType == null)
			return null;
		return commonProperties(this, aPeerType);
	}

	public static Boolean containsSignature(String aTypeName, String aSignature) {
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aTypeName);
		if (anSTType == null)
			return null;
		return containsSignature(anSTType, aSignature);
	}
	public static Boolean containsMethod(String aTypeName, STMethod aMethod) {
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aTypeName);
		if (anSTType == null)
			return null;
		return containsMethod(anSTType, aMethod);
	}
	public static Boolean containsProperty(String aTypeName, PropertyInfo aProperty) {
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(aTypeName);
		if (anSTType == null)
			return null;
		return containsProperty(anSTType, aProperty);
	}

	public static Boolean containsSignature(STType aType, String aSignature) {
		List<String> aSignatures = aType.getPublicInstanceSignatures();
		if (aSignatures == null)
			return null;
		return aSignatures.contains(aSignature);
	}
	public static Boolean containsMethod(STType aType, STMethod aMethod) {
		List<STMethod> aMethods = Arrays.asList(aType.getMethods());
		if (aMethods == null)
			return null;
		return aMethods.contains(aMethod);
	}
	public static Boolean containsProperty(STType aType, PropertyInfo aProperty) {
		Collection<PropertyInfo> aProperties = aType.getPropertyInfos().values();
		if (aProperties == null)
			return null;
		return aProperties.contains(aProperty);
	}

	// public static Boolean haveDelegateRelatonship (String aTypeName1, String
	// aTypeName2) {
	// STType aType1 =
	// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName1);
	// if (aType1 == null)
	// return null;
	//
	// STType aType2 =
	// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName1);
	//
	// }
	// public static Boolean containsSignature (List<STType> aList) {
	// Boolean retVal = false;
	// for (STType aType:aList) {
	// retVal = containsSignature(aType);
	// if (retVal)
	// return true;
	// }
	// return retVal;
	// }
	public static Boolean containsSignature(List<String> aList,
			String aSignature) {
		Boolean retVal = false;
		for (String aType : aList) {
			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
					.getSTClassByShortName(aType);
			if (anSTType == null) {
				retVal = null;
				continue;
			}
			retVal = containsSignature(aType, aSignature);
			if (retVal)
				return true;
		}
		return retVal;
	}
	public static Boolean containsMethod(List<String> aList,
			STMethod aMethod) {
		Boolean retVal = false;
		for (String aType : aList) {
			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
					.getSTClassByShortName(aType);
			if (anSTType == null) {
				retVal = null;
				continue;
			}
			retVal = containsMethod(aType, aMethod);
			if (retVal)
				return true;
		}
		return retVal;
	}
	public static Boolean containsProperty(List<String> aList,
			PropertyInfo aSignature) {
		Boolean retVal = false;
		for (String aType : aList) {
			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
					.getSTClassByShortName(aType);
			if (anSTType == null) {
				retVal = null;
				continue;
			}
			retVal = containsProperty(aType, aSignature);
			if (retVal)
				return true;
		}
		return retVal;
	}
	
	static STMethod[] emptySTMethods = {};

	public static STMethod[] computeInitMethods(STMethod[] aMethods) {
		List<STMethod> result = new ArrayList();
		for (STMethod aMethod: aMethods) {
			if (aMethod.isInit())
				result.add(aMethod);
		}
		return result.toArray(emptySTMethods);
		
	}
	@Override
	public STMethod[] getDeclaredInitMethods() {
		if (initMethods == null)
			initMethods = computeInitMethods(getDeclaredMethods());
		return initMethods;
	}
	public static boolean contains (List<STNameable> anInterfaces, String anInterface) {
		String aShortInterface = ComprehensiveVisitCheck.toShortTypeOrVariableName(anInterface);
		for (STNameable aNameable:anInterfaces) {
			String aMember = ComprehensiveVisitCheck.toShortTypeOrVariableName(aNameable.getName());
			return aMember.equals(aShortInterface);
		}
		return false;
	}
	
	public static List<STType> getImplementations(String anInterface) {
		List<STType> result = new ArrayList();
		SymbolTable aSymbolTable = SymbolTableFactory.getSymbolTable();
		for (STType anSTType:aSymbolTable.getAllSTTypes()) {
			if (anSTType.isInterface())
				continue;
			List<STNameable> interfaces = anSTType.getAllInterfaces();
			if (interfaces == null)
				return null;
			if (contains(interfaces, anInterface)) {
				result.add(anSTType);
			}			
		}
		return result;
	}
	@Override
	public STMethod getGetter(String aPropertyName) {
		STMethod[] aDeclaredMethods = getDeclaredMethods();
		for (STMethod aMethod:aDeclaredMethods) {
			if (aMethod.getName().toLowerCase().equals("get" + aPropertyName.toLowerCase()) && aMethod.getParameterNames().length == 0) {
				return aMethod;
			}
		}
		return null;
	}
	@Override
	public STMethod getSetter(String aPropertyName) {
		STMethod[] aDeclaredMethods = getDeclaredMethods();
		for (STMethod aMethod:aDeclaredMethods) {
			if (aMethod.getName().toLowerCase().equals("set" + aPropertyName.toLowerCase()) && aMethod.getParameterNames().length == 1) {
				return aMethod;
			}
		}
		return null;
	}
	@Override
	public boolean isExternal() {
		return external;
	}
	@Override
	public void setExternal(boolean external) {
		this.external = external;
	}
	protected Integer numberOfFunctions;
	@Override
	public int getNumberOfFunctions() {
		if (numberOfFunctions == null) {
			numberOfFunctions = 0;
			for (STMethod aMethod: getDeclaredMethods()) {
				numberOfFunctions += 
						(aMethod.isProcedure()?0:1);
			}
		}
		return numberOfFunctions;
	}
	
	protected Integer numberOfNonGetterFunctions;

	@Override
	public int getNumberOfNonGetterFunctions() {
		if (numberOfNonGetterFunctions == null) {
			numberOfNonGetterFunctions = 0;
			for (STMethod aMethod: getDeclaredMethods()) {
				numberOfNonGetterFunctions += 
						(aMethod.isProcedure() || aMethod.isGetter()?0:1);
			}
		}
		return numberOfNonGetterFunctions;
	}
	protected Integer numberOfGettersAndSetters;
	@Override
	public int getNumberOfGettersAndSetters() {
		if (numberOfGettersAndSetters == null) {
			numberOfGettersAndSetters = 0;
			for (STMethod aMethod: getDeclaredMethods()) {
				numberOfGettersAndSetters += 
						(aMethod.isSetter() || aMethod.isGetter()?1:0);
			}
		}
		return numberOfGettersAndSetters;
	}
	
	protected Integer numberOfMethods;
	@Override
	public int getNumberOfMethods() {
		if (numberOfMethods == null) {
			numberOfMethods = getDeclaredMethods().length;
		}
		return numberOfMethods;
	}
	
	protected Integer numberOfNonGettersAndSetters;
	@Override
	public int getNumberOfNonGettersAndSetters() {
		if (numberOfNonGettersAndSetters == null) {
			numberOfNonGettersAndSetters = getNumberOfMethods() - getNumberOfGettersAndSetters();
//			numberOfMethods = 0;
//			for (STMethod aMethod: getDeclaredMethods()) {
//				numberOfMethods += 1;
		}
		
		return numberOfNonGettersAndSetters;
	}
	protected Integer numberOfPublicMethods;
	@Override
	public int getNumberOfPublicMethods() {
		if (numberOfPublicMethods == null) {
			numberOfPublicMethods = getNumberOfDeclaredMethodsWithAccessMode(AccessModifier.PUBLIC);
			
		}
		return numberOfPublicMethods;		
	}
	protected Integer numberOfProtectedMethods;
	@Override
	public int getNumberOfProtectedMethods() {
		if (numberOfProtectedMethods == null) {
			numberOfProtectedMethods = getNumberOfDeclaredMethodsWithAccessMode(AccessModifier.PROTECTED);
			
		}
		return numberOfProtectedMethods;		
	}
	protected Integer numberOfPackageAccessMethods;
	@Override
	public int getNumberOfPackageAccessMethods() {
		if (numberOfPackageAccessMethods == null) {
			numberOfPackageAccessMethods = getNumberOfDeclaredMethodsWithAccessMode(AccessModifier.PACKAGE);
			
		}
		return numberOfPackageAccessMethods;		
	}
	protected Integer numberOfPrivateMethods;
	@Override
	public int getNumberOfPrivateMethods() {
		if (numberOfPrivateMethods == null) {
			numberOfPrivateMethods = getNumberOfDeclaredMethodsWithAccessMode(AccessModifier.PRIVATE);
			
		}
		return numberOfPrivateMethods;		
	}
	@Override
	public double getFractionOfPrivateMethods() {
		return ((double) getNumberOfPrivateMethods())/getNumberOfMethods();
	}
	@Override
	public double getFractionOfProtectedMethods() {
		return ((double) getNumberOfProtectedMethods())/getNumberOfMethods();
	}
	@Override
	public double getFractionOfPackageAccessMethods() {
		return ((double) getNumberOfPackageAccessMethods())/getNumberOfMethods();
	}
	@Override
	public double getFractionOfPublicMethods() {
		return ((double) getNumberOfPublicMethods())/getNumberOfMethods();
	}
	@Override
	public double getAverageAccessModeOfMethods() {
	
		return getFractionOfPrivateMethods()*(AccessModifier.PRIVATE.ordinal()) +
				getFractionOfProtectedMethods()*(AccessModifier.PROTECTED.ordinal()) +
				getFractionOfPackageAccessMethods()*(AccessModifier.PACKAGE.ordinal()) +
				getFractionOfPublicMethods()*(AccessModifier.PUBLIC.ordinal());
		
	}
	
	//
	
	protected Integer numberOfDeclaredVariables;
	@Override
	public int getNumberOfDeclaredVariables() {
		if (numberOfDeclaredVariables == null) {
			numberOfDeclaredVariables = getDeclaredSTGlobals().size();
			
		}
		return numberOfDeclaredVariables;		
	}
	protected Integer numberOfPublicVariables;
	@Override
	public int getNumberOfPublicVariables() {
		if (numberOfPublicVariables == null) {
			numberOfPublicVariables = getNumberOfDeclaredVariablesWithAccessMode(AccessModifier.PUBLIC);
			
		}
		return numberOfPublicVariables;		
	}
	protected Integer numberOfProtectedVariables;
	@Override
	public int getNumberOfProtectedVariables() {
		if (numberOfProtectedVariables == null) {
			numberOfProtectedVariables = getNumberOfDeclaredVariablesWithAccessMode(AccessModifier.PROTECTED);
			
		}
		return numberOfProtectedVariables;		
	}
	protected Integer numberOfPackageAccessVariables;
	@Override
	public int getNumberOfPackageAccessVariables() {
		if (numberOfPackageAccessVariables == null) {
			numberOfPackageAccessVariables = getNumberOfDeclaredVariablesWithAccessMode(AccessModifier.PACKAGE);
			
		}
		return numberOfPackageAccessVariables;		
	}
	protected Integer numberOfPrivateVariables;
	@Override
	public int getNumberOfPrivateVariables() {
		if (numberOfPrivateVariables == null) {
			numberOfPrivateVariables = getNumberOfDeclaredVariablesWithAccessMode(AccessModifier.PRIVATE);
			
		}
		return numberOfPrivateVariables;		
	}
	
	@Override
	public double getFractionOfPrivateVariables() {
		return ((double) getNumberOfPrivateVariables())/getNumberOfDeclaredVariables();
	}
	@Override
	public double getFractionOfProtectedVariables() {
		return ((double) getNumberOfProtectedVariables())/getNumberOfDeclaredVariables();
	}
	@Override
	public double getFractionOfPackageAccessVariables() {
		return ((double) getNumberOfPackageAccessVariables())/getNumberOfDeclaredVariables();
	}
	@Override
	public double getFractionOfPublicVariables() {
		return ((double) getNumberOfPublicVariables())/getNumberOfDeclaredVariables();
	}
	@Override
	public double getAverageAccessModeOfVariables() {
	
		return getFractionOfPrivateVariables()*(AccessModifier.PRIVATE.ordinal()) +
				getFractionOfProtectedVariables()*(AccessModifier.PROTECTED.ordinal()) +
				getFractionOfPackageAccessVariables()*(AccessModifier.PACKAGE.ordinal()) +
				getFractionOfPublicVariables()*(AccessModifier.PUBLIC.ordinal());
		
	}
	//-----------
	@Override
	public int getNumberOfDeclaredProperties() {
		return declaredPropertyInfo.size();
	}
	protected Integer numberOfDeclaredProperties;
	
	protected Integer numberOfPublicProperties;
	@Override
	public int getNumberOfPublicProperties() {
		if (numberOfPublicProperties == null) {
			numberOfPublicProperties = getNumberOfDeclaredPropertiesWithAccessMode(AccessModifier.PUBLIC);
			
		}
		return numberOfPublicProperties;		
	}
	protected Integer numberOfProtectedProperties;
	@Override
	public int getNumberOfProtectedProperties() {
		if (numberOfProtectedProperties == null) {
			numberOfProtectedProperties = getNumberOfDeclaredPropertiesWithAccessMode(AccessModifier.PROTECTED);
			
		}
		return numberOfProtectedProperties;		
	}
	protected Integer numberOfPackageAccessProperties;
	@Override
	public int getNumberOfPackageAccessProperties() {
		if (numberOfPackageAccessProperties == null) {
			numberOfPackageAccessProperties = getNumberOfDeclaredPropertiesWithAccessMode(AccessModifier.PACKAGE);
			
		}
		return numberOfPackageAccessProperties;		
	}
	protected Integer numberOfPrivateProperties;
	@Override
	public int getNumberOfPrivateProperties() {
		if (numberOfPrivateProperties == null) {
			numberOfPrivateProperties = getNumberOfDeclaredPropertiesWithAccessMode(AccessModifier.PRIVATE);
			
		}
		return numberOfPrivateProperties;		
	}
	
	protected Integer numberOfEditableProperties;

	@Override
	public int getNumberOfEditableProperties() {
		if (numberOfEditableProperties == null) {
			numberOfEditableProperties = getNumberOfDeclaredEditableProperties(declaredPropertyInfo.values());
			
		}
		return numberOfEditableProperties;		
	}
	protected Integer numberOfReadOnlyProperties;

	@Override
	public int getNumberOfReadOnlyProperties() {
		
		if (numberOfReadOnlyProperties == null) {
			numberOfReadOnlyProperties = getNumberOfDeclaredReadOnlyProperties(declaredPropertyInfo.values());
			
		}
		return numberOfReadOnlyProperties;			
	}
	protected Integer numberOfWriteOnlyProperties;

	@Override
	public int getNumberOfWriteOnlyProperties() {
		
		if (numberOfWriteOnlyProperties == null) {
			numberOfWriteOnlyProperties = getNumberOfDeclaredWriteOnlyProperties(declaredPropertyInfo.values());
			
		}
		return numberOfWriteOnlyProperties;			
	}
	
	@Override
	public double getFractionOfPrivateProperties() {
		return ((double) getNumberOfPrivateProperties())/getNumberOfDeclaredProperties();
	}
	@Override
	public double getFractionOfProtectedProperties() {
		return ((double) getNumberOfProtectedProperties())/getNumberOfDeclaredProperties();
	}
	@Override
	public double getFractionOfPackageAccessProperties() {
		return ((double) getNumberOfPackageAccessProperties())/getNumberOfDeclaredProperties();
	}
	@Override
	public double getFractionOfPublicProperties() {
		return ((double) getNumberOfPublicProperties())/getNumberOfDeclaredProperties();
	}
	
	@Override
	public double getFractionOfReadOnlyProperties() {
		return ((double) getNumberOfReadOnlyProperties())/getNumberOfDeclaredProperties();
	}
	@Override
	public double getFractionOfEditableProperties() {
		return ((double) getNumberOfEditableProperties())/getNumberOfDeclaredProperties();
	}
	@Override
	public double getFractionOfWriteOnlyProperties() {
		return ((double) getNumberOfWriteOnlyProperties())/getNumberOfDeclaredProperties();
	}
	
	
	
	@Override
	public double getAverageAccessModeOfProperties() {
	
		return getFractionOfPrivateProperties()*(AccessModifier.PRIVATE.ordinal()) +
				getFractionOfProtectedProperties()*(AccessModifier.PROTECTED.ordinal()) +
				getFractionOfPackageAccessProperties()*(AccessModifier.PACKAGE.ordinal()) +
				getFractionOfPublicProperties()*(AccessModifier.PUBLIC.ordinal());
		
	}
	//-----------
	
	public static int getNumberOfMethodsWithAccessMode(STMethod[] anSTMethods, AccessModifier anAccessModifier) {
		if (anSTMethods == null) {
			return 0;
		}
		int retVal = 0;
			for (STMethod aMethod: anSTMethods) {
				retVal += 
						(aMethod.getAccessModifier() == anAccessModifier?1:0);
			}
		
		return retVal;
		
	}
	/*
	 * Ugh, should really combine STMethod and STVariable in one type with getAccessmodifier.
	 */
	public static int getNumberOfVariablesWithAccessMode(List<STVariable> anSTVariables, AccessModifier anAccessModifier) {
		if (anSTVariables == null) {
			return 0;
		}
		int retVal = 0;
			for (STVariable aVariable: anSTVariables) {
				retVal += 
						(aVariable.getAccessModifier() == anAccessModifier?1:0);
			}
		
		return retVal;
		
	}
	public static int getNumberOfDeclaredPropertiesWithAccessMode(Collection<PropertyInfo> aProperties, AccessModifier anAccessModifier) {
		if (aProperties == null) {
			return 0;
		}
		int retVal = 0;
			for (PropertyInfo aProperty: aProperties) {
				retVal += 
						(aProperty.getAccessModifier() == anAccessModifier?1:0);
			}
		
		return retVal;
		
	}
	public static int getNumberOfDeclaredEditableProperties(Collection<PropertyInfo> aProperties) {
		if (aProperties == null) {
			return 0;
		}
		int retVal = 0;
			for (PropertyInfo aProperty: aProperties) {
				retVal += 
						(aProperty.isEditable()?1:0);
			}
		
		return retVal;
		
	}
	public static int getNumberOfDeclaredReadOnlyProperties(Collection<PropertyInfo> aProperties) {
		if (aProperties == null) {
			return 0;
		}
		int retVal = 0;
			for (PropertyInfo aProperty: aProperties) {
				retVal += 
						(aProperty.isReadOnly()?1:0);
			}
		
		return retVal;
		
	}
	public static int getNumberOfDeclaredWriteOnlyProperties(Collection<PropertyInfo> aProperties) {
		if (aProperties == null) {
			return 0;
		}
		int retVal = 0;
			for (PropertyInfo aProperty: aProperties) {
				retVal += 
						(aProperty.isWriteOnly()?1:0);
			}
		
		return retVal;
		
	}
	protected  int getNumberOfDeclaredMethodsWithAccessMode(AccessModifier anAccessModifier) {
		return getNumberOfMethodsWithAccessMode(getDeclaredMethods(), anAccessModifier);
		
	}
	protected  int getNumberOfDeclaredVariablesWithAccessMode(AccessModifier anAccessModifier) {
		return getNumberOfVariablesWithAccessMode(getDeclaredSTGlobals(), anAccessModifier);
		
	}
	protected  int getNumberOfDeclaredPropertiesWithAccessMode(AccessModifier anAccessModifier) {
		return getNumberOfDeclaredPropertiesWithAccessMode(declaredPropertyInfo.values(), anAccessModifier);
		
	}
	
	@Override
	public STVariable getDeclaredGlobalSTVariable(String aGlobal) {
		if (globalSTVariables == null) {
			return null;
		}
//		STVariable result = null;
		for (STVariable anSTVariable:globalSTVariables) {
			String aShortGlobalName = TagBasedCheck.toShortTypeOrVariableName(aGlobal);			 
			if (anSTVariable.getName().equals(aShortGlobalName)) {
				return anSTVariable;
			}
		}
		return null;
	}
	// @Override
	// public boolean isParsedClass() {
	// return true;
	// }

	@Override
	public boolean isAbstract() {
		return isAbstract;
	}
	
	@Override
	public Set<STType> getReferenceTypes() {
		if (referenceTypes == null) {
			referenceTypes = new HashSet();
			List<STVariable> aDeclaredSTGlobals = getDeclaredSTGlobals();
			if (aDeclaredSTGlobals != null) {
			for (STVariable anSTVariable:aDeclaredSTGlobals) {
				Set<STType> aVariableReferences = anSTVariable.getReferenceTypes();
				if (aVariableReferences != null)
				referenceTypes.addAll(anSTVariable.getReferenceTypes());
			}
			}
			STMethod[] aDeclaredMethods = getDeclaredMethods();

			for (STMethod anSTMethod:aDeclaredMethods) {
				Set<STType> aMethodReferences = anSTMethod.getCallingTypes();
				if (aMethodReferences != null)
				referenceTypes.addAll(aMethodReferences);				
			}
		}
		return referenceTypes;
	}
	@Override
	public List<AccessModifierUsage> getAccessModifiersUsed() {
		if (accessModifierUsage == null) {
			accessModifierUsage = AnSTVariable.getAccessModifiersUsed (this, this.getAccessModifier(), this, getReferenceTypes(), null);
		}
		return accessModifierUsage;
		
//		return AnSTVariable.getAccessModifiersUsed (this, this.getAccessModifier(), this, getReferenceTypes(), null);
		
	}
	@Override
	public double getNumberOfReferencesPerVariable() {
		List<STVariable> aGlobals = getDeclaredSTGlobals();
		if (aGlobals == null || aGlobals.size() == 0) {
			return -1;
		}
		double aTotalReferences = 0.0;

		for (STVariable aVariable:aGlobals) {
			Set<DetailAST> aReferences = aVariable.getReferences();
			aTotalReferences += aReferences == null?0:aReferences.size();
		}
		return aTotalReferences/getDeclaredSTGlobals().size();
	}
	@Override
	public double getNumberOfReferencesPerConstant() {
		List<STVariable> aGlobals = getDeclaredSTGlobals();
		if (aGlobals == null || aGlobals.size() == 0) {
			return -1;
		}
		double aTotalReferences = 0.0;
		int aNumConstants = 0;
		for (STVariable aVariable:aGlobals) {
			if (!aVariable.isFinal())
				continue;
			aNumConstants++;
			Set<DetailAST> aReferences = aVariable.getReferences();
			aTotalReferences += aReferences == null?0:aReferences.size();
		}
		return aTotalReferences/aNumConstants;
	}
	@Override
	public double getNumberOfAssignmentsPerVariable() {
		List<STVariable> aGlobals = getDeclaredSTGlobals();
		if (aGlobals == null || aGlobals.size() == 0) {
			return -1;
		}
		double aTotalAssignments = 0.0;

		for (STVariable aVariable:aGlobals) {
			Set<DetailAST> anAssignments = aVariable.getAssignments();
			aTotalAssignments += anAssignments == null?0:anAssignments.size();
		}
		return aTotalAssignments/getDeclaredSTGlobals().size();
	}
	@Override
	public STMethod getStaticBlocks() {
		return staticBlocks;
	}
//	@Override
//	public AccessModifier getAccessModifier() {
//		return accessModifier;
//	}
	@Override
	public void setStaticBlocks(STMethod staticBlocks) {
		this.staticBlocks = staticBlocks;
	}
}
