package unc.cs.symbolTable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import unc.cs.checks.ComprehensiveVisitCheck;
import unc.cs.checks.STBuilderCheck;
import unc.cs.checks.TagBasedCheck;
import unc.cs.checks.TypeVisitedCheck;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;
import com.sun.nio.sctp.SctpStandardSocketOptions.InitMaxStreams;

public class AnSTType extends AnAbstractSTType implements STType {
	protected final STNameable[] declaredPropertyNames, declaredEditablePropertyNames, tags, configuredTags, derivedTags, computedTags, imports;

	protected final boolean isInterface, isGeneric, isElaboration, isEnum;
//	protected final STNameable superClass;
	protected final  STNameable structurePatternName;	

//	protected List<STVariable>	globalSTVariables;
	protected Map<String, List<CallInfo>> globalVariableToCall ;
//	protected Map<String, String> globalVariableToType ;
//	protected Map<String, DetailAST> globalVariableToRHS ;
	protected List<CallInfo> methodsCalled = new ArrayList();
	protected List<CallInfo> allMethodsCalled ;
	
	

	protected List<STNameable> typesInstantiated;
	protected Map<String, List<DetailAST>> globalIdentToRHS;
	protected Map<String, List<DetailAST>> globalIdentToLHS ;
	protected Set<Integer> modifiers;
	protected String fileName;
//	protected AccessModifier accessModifier;
//	protected boolean isAbstract; 


//	protected Set<String> delegates = new HashSet();
	
	
	

	public AnSTType(
			String aFileName,
			DetailAST ast, 
			String name, 
			STMethod aStaticBlocks,
			STMethod[] declaredMethods,
			STMethod[] aDeclaredConstructors,
			STNameable[] interfaces, STNameable superClass,
			String packageName, boolean isInterface,
			boolean anIsGeneric,
			boolean anIsElaboration,
			boolean anIsEnum,
			STNameable aStructurePatternName,
			STNameable[] aDeclaredPropertyNames, 
			STNameable[] aDeclaredEditablePropertyNames, 
			STNameable[] aTags,
			STNameable[] aComputedTags,
			STNameable[] aConfiguredTags,
			STNameable[] aDerivedTags,
			STNameable[] anImports,
			STNameable[] aFields,
			Map<String, List<CallInfo>> aGlobalVariableToCall,
//			Map<String, String> aGlobalVariableToType,
//			Map<String, DetailAST> aGlobalVariableToRHS,
			List<STNameable> aTypesInstantiated,
			List<STVariable> aGlobalSTVariables,
			Map<String, List<DetailAST>> aGlobalIdentToLHS,
			Map<String, List<DetailAST>> aGlobalIdentToRHS,
			Set<Integer> aModifiers
			) {
		super(ast, name);
		if (aFileName != null)
			fileName = aFileName;
		else
			fileName = name;
		if (name.contains("TrickOrTreatPostProcessingCustomMain")) {
			System.err.println (" found TrickOrTreatPostProcessingCustomMain");
		}
		staticBlocks = aStaticBlocks;
		this.declaredMethods = declaredMethods;
		this.declaredConstructors = aDeclaredConstructors;
		if (aDeclaredConstructors == null || aDeclaredConstructors.length == 0) {
			STMethod aConstructor = AnSTMethod.createDefaultConstructor(name, ast);
			declaredConstructors = new STMethod[] {aConstructor};
		}
		this.declaredInterfaces = interfaces;
		this.superClass = superClass;
		if (superClass == null) {
			superClass = STBuilderCheck.getExistingClassSTType(Object.class);
		}
		this.packageName = packageName;
		this.isInterface = isInterface;
		isGeneric = anIsGeneric;
		isElaboration = anIsElaboration;
		isEnum = anIsEnum;
		structurePatternName = aStructurePatternName;
		declaredPropertyNames = aDeclaredPropertyNames;
		declaredEditablePropertyNames = aDeclaredEditablePropertyNames;
		tags = aTags;
		configuredTags = aConfiguredTags;
		derivedTags = aDerivedTags;
		imports = anImports;
		declaredFields = aFields;
		globalVariableToCall = aGlobalVariableToCall;
//		globalVariableToRHS = aGlobalVariableToRHS;
		computedTags = aComputedTags;
//		globalVariableToType = aGlobalVariableToType;
//		if (!isInterface) {
//			for (STMethod aMethod:declaredMethods) {
//				methodsCalled.addAll(Arrays.asList(aMethod.getCallInfoOfMethodsCalled()));
//				aMethod.setDeclaringType(this);
//				aMethod.processGlobals();
//			}
//			for (STMethod aConstructor:declaredConstructors) {
//				aConstructor.setDeclaringSTType(this);
//				methodsCalled.addAll(Arrays.asList(aConstructor.getCallInfoOfMethodsCalled()));
//				aConstructor.processGlobals();
//			}
//		}
		typesInstantiated = aTypesInstantiated;
		globalSTVariables = aGlobalSTVariables;
		globalIdentToLHS = aGlobalIdentToLHS;
		globalIdentToRHS = aGlobalIdentToRHS;
		modifiers = aModifiers;
		accessModifier = STBuilderCheck.toAccessModifier(modifiers);
		isAbstract = modifiers != null && modifiers.contains(TokenTypes.ABSTRACT);
		if (isInterface) {
			for (STMethod aMethod:declaredMethods) {
				aMethod.setPublic(true);
			}
		}
		//if (!isInterface) 
		else {
			getStaticBlocks().setDeclaringSTType(this);
			getStaticBlocks().processGlobals();
			// need to process callinfos also
			
			for (STMethod aMethod:declaredMethods) {
				methodsCalled.addAll(Arrays.asList(aMethod.getCallInfoOfMethodsCalled()));
				aMethod.setDeclaringType(this);
				aMethod.processGlobals();
			}
			try {
			for (STMethod aConstructor:declaredConstructors) {
				aConstructor.setDeclaringSTType(this);
				CallInfo[] aMethodsCalled = aConstructor.getCallInfoOfMethodsCalled();
				if (aMethodsCalled != null) {
				methodsCalled.addAll(Arrays.asList(aMethodsCalled));
				aConstructor.processGlobals();
				}
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}
//	public static STNameable toShortPatternName(STNameable aLongName) {
//		String aShortName = TypeVisitedCheck.toShortTypeName(aLongName.getName());
//		return  new AnSTNameable(aLongName.getAST(), aShortName);
//	}
	/*
	 * How about adding class name to this?
	 */
	


	//	protected STNameable[] computeTags() {
//		STNameable[] result;
//		if (structurePatternName == null) {
//			result = tags;
//		} else {	
//			
//			List<STNameable> aComputedList = new ArrayList(Arrays.asList(tags));
//			aComputedList.add(structurePatternName);
//			aComputedList.add(toShortPatternName(structurePatternName));
////			String aShortName = TypeVisitedCheck.toShortTypeName(structurePatternName.getName());
////			STNameable aShortStructurePatternName = new AnSTNameable(structurePatternName.getAST(), aShortName);
////			aComputedList.add(aShortStructurePatternName);
//			result = aComputedList.toArray(new STNameable[0]);
//		}
//		return result;
//		
//		
//	}
//	public STMethod[] getDeclaredMethods() {
//		return declaredMethods;
//	}
//	@Override
//	public STMethod[] getDeclaredConstructors() {
//		return declaredConstructors;
//	}
//	public STNameable[] getInterfaces() {
//		return declaredInterfaces;
//	}
//	
//	public String getPackage() {
//		return packageName;
//	}
	public boolean isInterface() {
		return isInterface;
	}
	public static void addToList(List<STMethod> aList, STMethod[] anAdditions) {
		for (STMethod anAddition:anAdditions) {
			aList.add(anAddition);
		}
	}
//	STMethod[] emptyMethods = new STMethod[0];
//	@Override
//	public STMethod[] getMethods() {
//		List<STMethod> retVal = new ArrayList();
//		addToList(retVal, getDeclaredMethods());
//		STNameable aSuperType = getSuperClass();
//		if (aSuperType != null &&
//				!TagBasedCheck.isExternalClass(aSuperType.getName())) {
//			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aSuperType.getName());
//			if (anSTType == null)
//				return null;
//			addToList(retVal, anSTType.getMethods());
//		}
//		return retVal.toArray(emptyMethods);
//	}
//	@Override
//	public STMethod getMethod(String aName, String[] aParameterTypes) {
//		STMethod[] aMethods = getMethods();
//		if (aMethods == null)
//			return null;
//		for (STMethod aMethod:aMethods) {
//			if (aMethod.getName().equals(aName) && aMethod.getParameterTypes().equals(aParameterTypes))
//				return aMethod;
//		}
//		return null;
//	}
//	STMethod[] emptyMethodArray = new STMethod[0];
//	
//		
//	
//	@Override
//	public STMethod[] getMethods(String aName) {
//		List<STMethod> resultList = new ArrayList();
//		STMethod[] aMethods = getMethods();
//		if (aMethods == null)
//			return null;
//		for (STMethod aMethod:aMethods) {
//			if (aMethod.getName().equals(aName))
//				resultList.add(aMethod);
//		}
//		return resultList.toArray(emptyMethodArray);
//		
//	}
//	@Override
//	public STNameable getSuperClass() {
//		return superClass;
//	}
	@Override
	public STNameable[] getDeclaredPropertyNames() {
		return declaredPropertyNames;
	}
	// recursion is safer
	@Override
	public STNameable[] getAllDeclaredPropertyNames() {
		List<STNameable> result = new ArrayList<>();
//		STNameable[] aPropertyNames = getDeclaredPropertyNames();
		STNameable[] aPropertyNames;

		STType anSTClass = this;
		while (true) {
			aPropertyNames = anSTClass.getDeclaredPropertyNames();
			for (STNameable aNameable:aPropertyNames) {
				result.add(aNameable);
			}
			STNameable aSuperClass = anSTClass.getSuperClass();
			if (aSuperClass == null || TagBasedCheck.isExternalClass(aSuperClass.getName()))
			     break;
			 anSTClass = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aSuperClass.getName());
			if (anSTClass == null)
				return null; // assume that we are only inheriting our own types
			
		}
		return  result.toArray(new STNameable[0]);
	}
	
//	public void initDeclaredPropertyNames(STNameable[] propertyNames) {
//		this.declaredPropertyNames = propertyNames;
//	}
	@Override
	public STNameable[] getDeclaredEditablePropertyNames() {
		return declaredEditablePropertyNames;
	}
//	public void initEditablePropertyNames(STNameable[] editablePropertyNames) {
//		this.declaredEditablePropertyNames = editablePropertyNames;
//	}
	@Override
	public STNameable[] getTags() {
		return tags;
	}
	@Override
	public STNameable[] getComputedTags() {
		return computedTags;
	}
	@Override
	public STNameable[] getConfiguredTags() {
		return configuredTags;
	}
	@Override
	public STNameable[] getDerivedTags() {
		return derivedTags;
	}
	@Override
	public STNameable[] getImports() {
		return imports;
	}
//	public void initTags(STNameable[] tags) {
//		this.tags = tags;
//	}
	@Override
	public STNameable getStructurePatternName() {
		return structurePatternName;
	}
//	@Override
//	public void initStructurePatternName(STNameable structurePatternName) {
//		this.structurePatternName = structurePatternName;
//	}
//	public  static final String GET = "get";
//	public  static final String SET = "set";
//	public static final String INIT = "init";
//	public static boolean isInit(STMethod anSTMethod) {
//		return isInit(anSTMethod.getName());
//	}
//	public static boolean isInit(String aMethodName) {
//		return aMethodName.startsWith(INIT);
//	}
//	void maybeProcessInit(STMethod anSTMethod) {
////		if (!anSTMethod.getName().startsWith(INIT))  return;
////		if (isInit(anSTMethod)) return;
//		if (anSTMethod.isInit()) return;
//
//		String aPropertyName = anSTMethod.getName().substring(AnSTMethod.GET.length()).toLowerCase();
//		String aPropertyType = anSTMethod.getReturnType();
//		PropertyInfo aPropertyInfo = actualPropertyInfo.get(aPropertyName);
//		if (aPropertyInfo == null) {
//			aPropertyInfo = new APropertyInfo();
//			actualPropertyInfo.put(aPropertyName, aPropertyInfo);
//		}			
//		aPropertyInfo.setGetter(anSTMethod);
//	}
//	public static boolean isGetter(STMethod anSTMethod) {
//		return anSTMethod.getName().startsWith(GET) &&
//				anSTMethod.isPublic() &&
//				anSTMethod.getParameterTypes().length == 0;
//	}

//	void maybeProcessGetter(STMethod anSTMethod) {
////		if (!isGetter(anSTMethod))
////			return;
//		if (!anSTMethod.isGetter())
//			return;
////		if (!anSTMethod.getName().startsWith(GET) ||
////				!anSTMethod.isPublic() ||
////				anSTMethod.getParameterTypes().length != 0) return;
////		String aPropertyName = anSTMethod.getName().substring(AnSTMethod.GET.length()).toLowerCase();
//		String aPropertyName = anSTMethod.getName().substring(AnSTMethod.GET.length());
//		String aPropertyType = anSTMethod.getReturnType();
//		PropertyInfo aPropertyInfo = actualPropertyInfo.get(aPropertyName);
//		if (aPropertyInfo == null) {
//			aPropertyInfo = new APropertyInfo();
//			actualPropertyInfo.put(aPropertyName, aPropertyInfo);
//		}			
//		aPropertyInfo.setGetter(anSTMethod);
//	}
	@Override
	public STNameable[] getDeclaredFields() {
		return declaredFields;
	}
//	public static boolean isSetter(STMethod anSTMethod) {
//		return anSTMethod.getName().startsWith(SET) &&
//				anSTMethod.isPublic() &&
//				anSTMethod.getParameterTypes().length != 1 &&
//				"void".equals(anSTMethod.getReturnType());
//	}
//	void maybeProcessSetter(STMethod anSTMethod) {
////		if (!anSTMethod.getName().startsWith(SET) ||
////		!anSTMethod.isPublic() ||
////		anSTMethod.getParameterTypes().length != 1) return;
////		if (!isSetter(anSTMethod)) 
////			return;	
//		if (!anSTMethod.isSetter()) 
//			return;
////		String aPropertyName = anSTMethod.getName().substring(AnSTMethod.SET.length()).toLowerCase();
//		String aPropertyName = anSTMethod.getName().substring(AnSTMethod.SET.length());
//
//		String aPropertyType = anSTMethod.getReturnType();
//		PropertyInfo aPropertyInfo = actualPropertyInfo.get(aPropertyName);
//		if (aPropertyInfo == null) {
//			aPropertyInfo = new APropertyInfo();
//			actualPropertyInfo.put(aPropertyName, aPropertyInfo);
//		}			
//		aPropertyInfo.setSetter(anSTMethod);
//	}
//	@Override
//	public void introspect() {
//		for (STMethod anSTMethod:declaredMethods) {
//			maybeProcessGetter(anSTMethod);
//			maybeProcessSetter(anSTMethod);			
//		}
//	}
	@Override
	public void findDelegateTypes() {
		Collection<List<CallInfo>> aCalls = 
				globalVariableToCall.values();
		for (List<CallInfo> aCallList:aCalls){
			for (CallInfo aCall:aCallList) {
				if (aCall.getCallee().equals(aCall.getCaller())) {
					delegates.add(aCall.getCalledType());
				}
			}
		}
	}
//	@Override
//	public Boolean isSubtypeOf(String aName) {
////		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aName);
////		if (anSTType == null) return null;
////		List<STNameable> anAllTypes = anSTType.getAllTypes();
////		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aName);
////		if (anSTType == null) return null;
//		List<STNameable> anAllTypes = getAllTypes();
//		for (STNameable aNameable: anAllTypes) {
//			if (aNameable.getName().equals(aName))
//				return true;
//		}
//		return false;		
//	}
//	@Override
//	public Boolean isDelegate(String aName) {
//		for (String aDelegateType:delegates) {
//			if (aName.equals(aDelegateType))
//				return true;
//			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aName);
//			if (anSTType == null) {
//				return null;
//			}
//			if (anSTType.isSubtypeOf(aDelegateType))
//				return true;			
//		}
//		return false;
//	}
	
//	@Override
//	public Map<String, PropertyInfo> getDeclaredPropertyInfos() {
//		return actualPropertyInfo;
//	}
	// should use recursion actually
//	@Override
//	public Map<String, PropertyInfo> getPropertyInfos() {
//		Map<String, PropertyInfo> result = new HashMap<>();
//		Map<String, PropertyInfo> aPropertyInfos = new HashMap();
//		STType anSTClass = this;
//		while (true) {
//			aPropertyInfos = anSTClass.getDeclaredPropertyInfos();	
//			for (String aPropertyName:aPropertyInfos.keySet()) {
//				result.put(aPropertyName, aPropertyInfos.get(aPropertyName));
//			}
//			STNameable aSuperClass = anSTClass.getSuperClass();
//			if (aSuperClass == null || TagBasedCheck.isExternalClass(aSuperClass.getName()))
//			     break;
//			anSTClass = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aSuperClass.getName());
//			if (anSTClass == null)
//				return null; // assume that we are only inheriting our own types
//		}
//		return result;
//	}
//	public static List<STNameable> getAllTypes(STNameable aType) {
//		if (TagBasedCheck.isExternalClass(TypeVisitedCheck.toShortTypeName(aType.getName())))
//			return emptyList;
//		List<STNameable> result = new ArrayList();
//		result.add(aType);
//		STType anSTType =  SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType.getName());
//		if (anSTType == null) return null;
//		STNameable[] anInterfaces = anSTType.getInterfaces();
//		for (STNameable anInterface:anInterfaces) {
//			 List<STNameable> anInterfaceTypes = getAllTypes(anInterface);
//			 if (anInterfaceTypes == null)
//				 return null;
//			 result.addAll(anInterfaceTypes);			 
//		}
//		if (anSTType.isInterface())
//			return result;
//		STNameable aSuperClass = anSTType.getSuperClass();
//		if (aSuperClass == null) 
//			return result;
//		List<STNameable> aSuperTypes = getAllTypes(anSTType.getSuperClass());
//		if (aSuperTypes == null)
//			return null;
//		addAllNonDuplicates(result, aSuperTypes);
////		result.addAll(aSuperType);
//		return result;		
//	}
//	public static List<STNameable> getAllInterfaces(STNameable aType) {
//		if (TagBasedCheck.isExternalClass(TypeVisitedCheck.toShortTypeName(aType.getName())))
//			return emptyList;
//		List<STNameable> result = new ArrayList();
//		STType anSTType =  SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType.getName());
//		if (anSTType == null) return null;
//		if (anSTType.isInterface()) {
//			System.err.println("An interface does not have an interface:" + aType.getName());
//			return null;
//		}
//		STNameable[] anInterfaces = anSTType.getInterfaces();
//		for (STNameable anInterface:anInterfaces) {
//			 List<STNameable> anInterfaceTypes = getAllTypes(anInterface);
//			 if (anInterfaceTypes == null)
//				 return null;
//			 result.addAll(anInterfaceTypes);			 
//		}		
//		return result;		
//	}
//	public static void addAllNonDuplicates (List aList, List anAdditions ) {
//		for (Object anAddition:anAdditions) {
//			if (aList.contains(anAddition)) continue;
//			aList.add(anAddition);
//		}
//	}
//	public static List emptyList = new ArrayList();
//	public static List<STNameable> getAllSuperTypes(STNameable aType) {
//		if (TagBasedCheck.isExternalClass(TypeVisitedCheck.toShortTypeName(aType.getName())))
//			return emptyList;	
//		List<STNameable> result = new ArrayList();
//		result.add(aType);
//		
//		STType anSTType =  SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType.getName());
//		if (anSTType == null) return null;
//		if (anSTType.isInterface()) {
//		STNameable[] anInterfaces = anSTType.getInterfaces();
//		for (STNameable anInterface:anInterfaces) {
//			if (result.contains(anInterface)) // an interface may be extended by many
//				continue;
//			List<STNameable> anInterfaceTypes = getAllSuperTypes(anInterface);
//			 if (anInterfaceTypes == null)
//				 return null;
////			 result.addAll(anInterfaceTypes);	
//			 addAllNonDuplicates(result, anInterfaceTypes);	
//		}
//		} else {
//			
//		
//		STNameable aSuperClass = anSTType.getSuperClass();
//		if (aSuperClass == null) 
//			return result;	
//		List<STNameable> aSuperTypes = getAllSuperTypes(anSTType.getSuperClass());
//		if (aSuperTypes == null)
//			return null;
//		result.addAll(aSuperTypes);
////		addAllNonDuplicates(result, aSuperTypes);
//		}
//		return result;		
//	}
//	@Override
//	public List<STNameable> getAllTypes() {		
//		List<STNameable> result = new ArrayList();
//		return getAllTypes(this);	
//		
//	}
//	@Override
//	public List<STNameable> getAllInterfaces() {		
//		List<STNameable> result = new ArrayList();
//		return getAllInterfaces(this);		
//	}
//	@Override
//	public List<String> getAllTypeNames() {
//		List<STNameable> allTypes = getAllTypes();
//		if (allTypes == null) return null;
//		return toNameList(allTypes);
//	}
//	@Override
//	public List<String> getSuperTypeNames() {
//		List<STNameable> aTypes = getSuperTypes();
//		if (aTypes == null) return null;
//		return toNameList(aTypes);
//	}
//	@Override
//	public List<STNameable> getSuperTypes() {		
//		List<STNameable> result = new ArrayList();
//		return getAllSuperTypes(this);	
//		
//	}
//	public static List<String> toNameList(List<STNameable> aNameableList) {
//		if (aNameableList == null) return null;
//		List<String> result = new ArrayList();
//		for (STNameable aNameable:aNameableList) {
//			String aShortName = TypeVisitedCheck.toShortTypeName(aNameable.getName());
//			if (!result.contains(aShortName))
//			     result.add(aShortName);
//		}
//		return result;
//	}
//	public static List<String> toNormalizedList(List<String> anOriginal) {
//		if (anOriginal ==null) return null;
//		List<String> result = new ArrayList();
//		for (String aNonNormalizedEntry:anOriginal) {
//			result.add(TypeVisitedCheck.toShortTypeName(aNonNormalizedEntry));
//		}
//		return result;
//	}
//	@Override
//	public List<String> getNonSuperTypes() {		
//		SymbolTable aSymbolTable = SymbolTableFactory.getOrCreateSymbolTable();
//		List<String> anAllTypes;
//		if (isInterface)
//			anAllTypes = aSymbolTable.getAllInterfaceNames();
//		else
//			anAllTypes = aSymbolTable.getAllClassNames();		
//		List<String> aNormalizedTypes = toNormalizedList(anAllTypes);
//		List<String> anAllMyTypes = toNameList(getSuperTypes());
//		return difference(aNormalizedTypes, anAllMyTypes);		
//	}
//	/*
//	 * remove delegates non super types
//	 */
//	
//	@Override
//	public List<String> getSubTypes() {		
////		SymbolTable aSymbolTable = SymbolTableFactory.getOrCreateSymbolTable();
////		List<String> anAllTypes;
////		if (isInterface)
////			anAllTypes = aSymbolTable.getAllInterfaceNames();
////		else
////			anAllTypes = aSymbolTable.getAllClassNames();		
////		List<String> aNormalizedTypes = toNormalizedList(anAllTypes);
////		List<String> anAllMyTypes = toNameList(getSuperTypes());
//		List<String>  aNonSuperTypes = getNonSuperTypes();
//		List<String> result = new ArrayList();
//		String myShortName = TypeVisitedCheck.toShortTypeName(name);
//		for (String aNonSuperType:aNonSuperTypes) {
//			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aNonSuperType);
//			if (anSTType == null) return null;
//			List<String> aSuperTypes = toNormalizedList(anSTType.getSuperTypeNames());
//			if (aSuperTypes == null)
//				return null;
//			if (aSuperTypes.contains(myShortName))
//				result.add(aNonSuperType);
//		}
//		return result;
//	}
//	@Override
//	public List<String> getPeerTypes() {
//		List<String>  aNonSuperTypes = toNormalizedList(getNonSuperTypes());
//		if (aNonSuperTypes == null)
//			return null;
//		List<String> aSubTypes = toNormalizedList(getSubTypes());
//		if (aSubTypes == null)
//			return null;
//		List<String> aResult = difference(aNonSuperTypes, aSubTypes);
//		if (isInterface() || aResult == null)
//			return aResult;
//		// find delegates
//		if (delegates.size() == 0)
//			return aResult;
//		List<String> aFinalResult = new ArrayList();
//		for (String aType: aResult) {
//			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType);
//			if (anSTType == null) 
//				return null;
//			Boolean isDelegate = isDelegate(aType);
//			if (isDelegate == null)
//				return null;
//			Boolean isDelegator = anSTType.isDelegate(getShortName());
//			if (isDelegator == null) 
//				return null;
//			if (!isDelegator && !isDelegate) {
////			if (!delegates.contains(aType))
//				aFinalResult.add(aType);
//			}
//		}
//		return aFinalResult;		
//	}
	
	public String  getShortName() {
		return TypeVisitedCheck.toShortTypeName(getName());
	}
	
//	@Override
//	public Boolean isNonSuperType(String aTypeName) {
//		return getNonSuperTypes().contains(TypeVisitedCheck.toShortTypeName(aTypeName));
//	}
//	@Override
//	public Boolean isType(String aTypeName) {
//		List<STNameable> aTypes = getAllTypes();
//		if (aTypes == null) return null;
//		return toNameList(aTypes).contains(TypeVisitedCheck.toShortTypeName(aTypeName));
//	}
//	@Override
//	public Boolean hasPublicMethod(String aSignature) {
//		STMethod[] stMethods = getMethods();
//		if (stMethods == null) return null;
//		return Arrays.asList(stMethods).contains(aSignature);		
//	}
//	@Override
//	public Boolean hasDeclaredMethod(String aSignature) {
//		STMethod[] stMethods = getDeclaredMethods();
//		if (stMethods == null) return null;
//		return Arrays.asList(stMethods).contains(aSignature);		
//	}
//	public static List intersect(List aList1, List aList2) {
//		List aResult = new ArrayList();
//		for (Object anElement1:aList1) {
//			for (Object anElement2:aList2) {
//				if (anElement1.equals(anElement2)) {
//					aResult.add(anElement1);
//					break;
//				}					
//			}
//		}
//		return aResult;
//	}
//	public static List difference(List aList1, List aList2) {
//		List aResult = new ArrayList();
//			for (Object anElement:aList1) {
//				if (!aList2.contains(anElement))
//					aResult.add(anElement);
//			}
//		
//		return aResult;
//	}
//	public static List<STNameable> commonSuperTypes(String aType1, String aType2) {
////		List<STNameable> result = new ArrayList();
//		STType anSTType1 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType1);
//		if (anSTType1 == null) return null;
//		STType anSTType2 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType2);
//		if (anSTType2 == null) return null;
////		List<STNameable> aSuperTypes1 = anSTType1.getSuperTypes();
////		if (aSuperTypes1 == null)
////			return null;
////		List<STNameable> aSuperTypes2 = anSTType2.getSuperTypes();
////		if (aSuperTypes2 == null)
////			return null;
////		return intersect (aSuperTypes1, aSuperTypes2);	
//		return commonSuperTypes(anSTType1, anSTType2);
//	}
//	public static List<STNameable> commonSuperTypes(STType anSTType1, STType anSTType2) {
////
//		List<STNameable> aSuperTypes1 = anSTType1.getSuperTypes();
//		if (aSuperTypes1 == null)
//			return null;
//		List<STNameable> aSuperTypes2 = anSTType2.getSuperTypes();
//		if (aSuperTypes2 == null)
//			return null;
//		return intersect (aSuperTypes1, aSuperTypes2);		
//	}
//	@Override
//	public List<STNameable> superTypesInCommonWith (String anOtherType) {
//		return commonSuperTypes(this.getName(), anOtherType);
//	}
//	@Override
//	public List<String> namesOfSuperTypesInCommonWith (String anOtherType) {
//		return toNameList(superTypesInCommonWith(anOtherType));
//	}
//	@Override
//	public List<STNameable> superTypesInCommonWith (STType anOtherType) {
//		return commonSuperTypes(this, anOtherType);
//	}
//	@Override
//	public List<String> getAllSignatures() {
//		List<String> result = new ArrayList();
//		STMethod[] anSTMethods = getMethods();
//		if (anSTMethods == null)
//			return null;
//		for (STMethod anSTMethod:anSTMethods) {
//			result.add(anSTMethod.getSignature());
//		}
//		return result;
//	}
//	@Override
//	public List<String> getSignatures() {
//		List<String> result = new ArrayList();
//		STMethod[] anSTMethods = getMethods();
//		if (anSTMethods == null)
//			return null;
//		for (STMethod anSTMethod:anSTMethods) {
//			if (anSTMethod.isPublic() && anSTMethod.isInstance())
//			result.add(anSTMethod.getSignature());
//		}
//		return result;
//	}
//	public static List<String> commonSignatures(String aType1, String aType2) {
//		STType anSTType1 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType1);
//		if (anSTType1 == null) return null;
//		STType anSTType2 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType2);
//		if (anSTType2 == null) return null;
//		return commonSignatures(anSTType1, anSTType2);		
////			
//	}
//	public static List<String> commonSignatures(STType aType1, STType aType2) {
//		List<String> aSignatures1 = aType1.getSignatures();
//		if (aSignatures1 == null) return null;
//		List<String> aSignatures2 = aType2.getSignatures();
//		if (aSignatures2 == null) return null;
//		return intersect(aSignatures1, aSignatures2);		
////			
//	}
//	@Override
//	public List<String> signaturesCommonWith (STType aType) {
//		return commonSignatures(this, aType);
//	}
//	@Override
//	public List<String> signaturesCommonWith (String aTypeName) {
//		STType aPeerType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
//		if (aPeerType == null)
//			return null;
//		return commonSignatures(this, aPeerType);
//	}
//	public static Boolean containsSignature(String aTypeName, String aSignature) {
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
//		if (anSTType == null)
//			return null;
//		return containsSignature(anSTType, aSignature);		
//	}
//	public static Boolean  containsSignature(STType aType, String aSignature) {
//		List<String> aSignatures = aType.getSignatures();
//		if (aSignatures == null)
//			return null;
//		return aSignatures.contains(aSignature);		
//	}
//	public static Boolean haveDelegateRelatonship (String aTypeName1, String aTypeName2) {
//		STType aType1 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName1);
//		if (aType1 == null) 
//			return null;
//		
//		STType aType2 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName1);
//		
//	}
//	public static Boolean containsSignature (List<STType> aList) {
//		Boolean retVal = false;
//		for (STType aType:aList) {
//			retVal = containsSignature(aType);
//			if (retVal)
//				return true;
//		}
//		return retVal;		
//	}
//	public static Boolean containsSignature (List<String> aList, String aSignature) {
//		Boolean retVal = false;
//		for (String aType:aList) {
//			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType);
//			if (anSTType == null) {
//				retVal = null;
//				continue;
//			}
//			retVal = containsSignature(aType, aSignature);
//			if (retVal)
//				return true;
//		}
//		return retVal;		
//	}
	@Override
	public boolean isParsedClass() {
		return true;
	}
	@Override
	public Set<String> getDelegates() {
		return delegates;
	}
	@Override
	public boolean isEnum() {
		return isEnum;
	}
//	@Override
//	public Set<String> getDeclaredGlobals() {
//		return globalVariableToType.keySet();
//	}
	@Override
	public List<STVariable> getDeclaredSTGlobals() {
		return globalSTVariables;
	}
//	@Override
//	public STVariable getDeclaredGlobalSTVariable(String aGlobal) {
////		STVariable result = null;
//		for (STVariable anSTVariable:globalSTVariables) {
//			String aShortGlobalName = TagBasedCheck.toShortTypeOrVariableName(aGlobal);			 
//			if (anSTVariable.getName().equals(aShortGlobalName)) {
//				return anSTVariable;
//			}
//		}
//		return null;
//	}
//	@Override
//	public String getDeclaredGlobalVariableType(String aGlobal) {
//		return globalVariableToType.get(aGlobal);
//	}
//	@Override
//	public DetailAST getDeclaredGlobalVariableToRHS(String aGlobal) {
//		return globalVariableToRHS.get(aGlobal);
//	}
	@Override
	public List<CallInfo> getMethodsCalled() {
		return methodsCalled;
	}
   public List<CallInfo>  computeAllCalls() {
		
		List<CallInfo> retVal = new ArrayList();
		retVal.addAll(getMethodsCalled());
//		retVal.addAll(getAllMethodsCalled());

		STNameable aSuperType = getSuperClass();
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
			List<CallInfo> superTypeCalls = anSTType.getAllMethodsCalled();
			if (superTypeCalls == null) // some supertype not compiled
				return null;
//			addToList(retVal, anSTType.getMethods());
			retVal.addAll(superTypeCalls);
//			return retVal;

		}
		return retVal;
	}
   public static String getDefiningSuperType (STType anSTType, String aCallee) {
	   STMethod[] aDeclaredMethods = anSTType.getDeclaredMethods(aCallee);
	   if (aDeclaredMethods.length > 0) {
		   return anSTType.getName();
	   }
	   STNameable aSuperTypeNameable = anSTType.getSuperClass();
	   if (aSuperTypeNameable == null) {
		   return Object.class.getName();
	   }
	   String aSuperTypeName = aSuperTypeNameable.getName();
	   STType aSuperSTType = SymbolTableFactory.getSymbolTable().getSTClassByFullName(aSuperTypeName);
	   if (aSuperSTType == null) {
		   if (isObjectMethod(aCallee)) {
			   return Object.class.getName();
		   }
		   return  aSuperTypeName;
	   }
	   return getDefiningSuperType(aSuperSTType, aCallee);
   }
	public static void addCalledSuperTypes(STType anSTType, List<CallInfo> aCallInfos) {
		if (aCallInfos == null) {
			return;
		}
		for (CallInfo aCallInfo : aCallInfos) {
			STType aCalledType = aCallInfo.getCalledSTType();
			if (!aCallInfo.hasUnknownCalledType()) {
				return;
			}
//			if (aCalledType != anSTType && !"super".equals(aCallInfo.getCalledType()))
//				continue;

			String aCallee = aCallInfo.getCallee();
			STType aCalledSuperType = AnSTMethod.getDeclaringISAClass(anSTType, aCallInfo);
			if (aCalledSuperType != null) {
				aCallInfo.setCalledSTType(aCalledSuperType);
				aCallInfo.setCalledType(aCalledSuperType.getName());
			}
//			String aCalledSuperType = getDefiningSuperType(anSTType, aCallee);
//			if (aCalledType.getName().equals(aCalledSuperType)) {
//				continue;
//			}
//			aCallInfo.setCalledType(aCalledSuperType);
//
//			STType aCalledSTSuperType = SymbolTableFactory.getSymbolTable().getSTClassByFullName(aCalledSuperType);
//			aCallInfo.setCalledSTType(aCalledSTSuperType);
		}

	}
   /**
    * These are all methods in the class, not methods called by these methods
    */   
	@Override
	public List<CallInfo> getAllMethodsCalled() {
		if (allMethodsCalled == null) {
		
			allMethodsCalled = computeAllCalls();
			if (allMethodsCalled != null) {
			addCalledSuperTypes(this, allMethodsCalled);
			
			}
			
		}
		return allMethodsCalled;
	}
	@Override
	public List<STNameable> getTypesInstantiated() {
		return typesInstantiated;
	}
	@Override
	public Boolean instantiatesType (String aShortOrLongName) {
		for (STNameable anInsantiatedNameable:typesInstantiated) {
			String anInstantiatedType = 
					ComprehensiveVisitCheck.toShortTypeOrVariableName(anInsantiatedNameable.getName());
			String anExpectedType =
					ComprehensiveVisitCheck.toShortTypeOrVariableName(aShortOrLongName);
//			if (anInstantiatedType.equals(anExpectedType))
//				return true;
			Boolean result = ComprehensiveVisitCheck.matchesType(anExpectedType, anInstantiatedType);
			if (result == null)
				return null;
			return result;
		}
		return false;
	}
	@Override
	public List<STMethod> getInstantiatingMethods(String aTypeName) {
		List<STMethod> result = new ArrayList();
		STMethod[] anAllMethods = getMethods();
		if (anAllMethods == null)
			return null;
		STMethod[] anAllConstructors = getDeclaredConstructors();
		if (anAllConstructors == null)
			return null;
		List<STMethod> aMethodsAndConstructors = new ArrayList();
		aMethodsAndConstructors.addAll(Arrays.asList(anAllMethods));
		aMethodsAndConstructors.addAll(Arrays.asList(anAllConstructors));
		for (STMethod aMethod:aMethodsAndConstructors) {
			Boolean instantiates = aMethod.instantiatesType(aTypeName);
			if (instantiates == null)
				return null;
//			if (aMethod.instantiatesType(aTypeName)) {
			if (instantiates) {

				result.add(aMethod);
			}
		}
//		int i = 0;
		Boolean instantiates = instantiatesType(aTypeName);
		if (instantiates == null)
			return null;
		if (instantiates) {
			result.add(NoMethod.getInstance());
		}
		return result;
	}

	static Set<String> objectMethodNames;
	static Method[] objectMethods;

	public static boolean isObjectMethod(String aName) {
		if (objectMethodNames == null ) {
			objectMethodNames = new HashSet();
			objectMethods = Object.class.getMethods();
			for (Method aMethod:objectMethods) {
				objectMethodNames.add(aMethod.getName());
			}
		}
		return objectMethodNames.contains(aName);
	}

	public Map<String, List<DetailAST>> getGlobalIdentToRHS() {
		return globalIdentToRHS;
	}

	public Map<String, List<DetailAST>> getGlobalIdentToLHS() {
		return globalIdentToLHS;
	}
//	@Override
//	public STVariable toSTVariable (String aName) {
//		for (STVariable anSTVariable:globalSTVariables) {
//			if (anSTVariable.getName().equals(aName)) {
//				return anSTVariable;
//			}
//		}
//		return null;
//	}
	protected Integer numberOfAsserts;
	@Override
	public int getNumberOfAsserts() {
		if (numberOfAsserts == null) {
			numberOfAsserts = 0;
			for (STMethod aMethod: getDeclaredMethods()) {
				numberOfAsserts += aMethod.getNumberOfAsserts();
			}
		}
		return numberOfAsserts;
	}
	protected Integer numberOfTernaryConditionals;

	@Override
	public int getNumberOfTernaryConditionals() {
		if (numberOfTernaryConditionals == null) {
			numberOfTernaryConditionals = 0;
			for (STMethod aMethod: getDeclaredMethods()) {
				numberOfTernaryConditionals += aMethod.getNumberOfTernaryConditionals();
			}
		}
		return numberOfTernaryConditionals;
	}
//	protected Integer numberOfFunctions;
//	@Override
//	public int getNumberOfFunctions() {
//		if (numberOfFunctions == null) {
//			numberOfFunctions = 0;
//			for (STMethod aMethod: getDeclaredMethods()) {
//				numberOfFunctions += 
//						(aMethod.isProcedure()?0:1);
//			}
//		}
//		return numberOfFunctions;
//	}
//	protected Integer numberOfNonGetterFunctions;
//
//	@Override
//	public int getNumberOfNonGetterFunctions() {
//		if (numberOfNonGetterFunctions == null) {
//			numberOfNonGetterFunctions = 0;
//			for (STMethod aMethod: getDeclaredMethods()) {
//				numberOfNonGetterFunctions += 
//						(aMethod.isProcedure() || aMethod.isGetter()?0:1);
//			}
//		}
//		return numberOfNonGetterFunctions;
//	}
//	protected Integer numberOfGettersAndSetters;
//	@Override
//	public int getNumberOfGettersAndSetters() {
//		if (numberOfGettersAndSetters == null) {
//			numberOfGettersAndSetters = 0;
//			for (STMethod aMethod: getDeclaredMethods()) {
//				numberOfGettersAndSetters += 
//						(aMethod.isSetter() || aMethod.isGetter()?0:1);
//			}
//		}
//		return numberOfNonGetterFunctions;
//	}
	
	@Override
	public String getFileName() {
		return fileName;
	}



	@Override
	public boolean isGeneric() {
		if (ast == null) {
			return false;
		}
		DetailAST generic = ast.findFirstToken(TokenTypes.TYPE_PARAMETERS);
		return generic != null;
	}


}
