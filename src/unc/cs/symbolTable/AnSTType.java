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
import com.sun.nio.sctp.SctpStandardSocketOptions.InitMaxStreams;

public class AnSTType extends AnSTNameable implements STType {
	protected final STNameable[] declaredPropertyNames, declaredEditablePropertyNames, tags, imports;	
	protected final STMethod[] declaredMethods;
	protected final STMethod[] declaredConstructors;
	protected final STNameable[] interfaces;
	protected final STNameable[] declaredFields;
	protected final String packageName;
	protected final boolean isInterface, isGeneric, isElaboration;
	protected final STNameable superClass;
	protected final  STNameable structurePatternName;	
	protected STMethod[] inits;
	protected Map<String, PropertyInfo> actualPropertyInfo = new HashMap();
	protected List<STMethod> declaredInits = new ArrayList();
	

	
	public AnSTType(DetailAST ast, String name, 
			STMethod[] declaredMethods,
			STMethod[] aDeclaredConstructors,
			STNameable[] interfaces, STNameable superClass,
			String packageName, boolean isInterface,
			boolean anIsGeneric,
			boolean anIsElaboration,
			STNameable aStructurePatternName,
			STNameable[] aDeclaredPropertyNames, 
			STNameable[] aDeclaredEditablePropertyNames, 
			STNameable[] aTags,
			STNameable[] anImports,
			STNameable[] aFields
			) {
		super(ast, name);
		this.declaredMethods = declaredMethods;
		this.declaredConstructors = aDeclaredConstructors;
		this.interfaces = interfaces;
		this.superClass = superClass;
		this.packageName = packageName;
		this.isInterface = isInterface;
		isGeneric = anIsGeneric;
		isElaboration = anIsElaboration;
		structurePatternName = aStructurePatternName;
		declaredPropertyNames = aDeclaredEditablePropertyNames;
		declaredEditablePropertyNames = aDeclaredEditablePropertyNames;
		tags = aTags;
		imports = anImports;
		declaredFields = aFields;
	}
	public STMethod[] getDeclaredMethods() {
		return declaredMethods;
	}
	@Override
	public STMethod[] getDeclaredConstructors() {
		return declaredConstructors;
	}
	public STNameable[] getInterfaces() {
		return interfaces;
	}
	
	public String getPackage() {
		return packageName;
	}
	public boolean isInterface() {
		return isInterface;
	}
	public static void addToList(List<STMethod> aList, STMethod[] anAdditions) {
		for (STMethod anAddition:anAdditions) {
			aList.add(anAddition);
		}
	}
	STMethod[] emptyMethods = new STMethod[0];
	@Override
	public STMethod[] getMethods() {
		List<STMethod> retVal = new ArrayList();
		addToList(retVal, getDeclaredMethods());
		STNameable aSuperType = getSuperClass();
		if (aSuperType != null &&
				!TagBasedCheck.isExternalClass(aSuperType.getName())) {
			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aSuperType.getName());
			if (anSTType == null)
				return null;
			addToList(retVal, anSTType.getMethods());
		}
		return retVal.toArray(emptyMethods);
	}
	@Override
	public STMethod getMethod(String aName, String[] aParameterTypes) {
		STMethod[] aMethods = getMethods();
		if (aMethods == null)
			return null;
		for (STMethod aMethod:aMethods) {
			if (aMethod.getName().equals(aName) && aMethod.getParameterTypes().equals(aParameterTypes))
				return aMethod;
		}
		return null;
	}
	STMethod[] emptyMethodArray = new STMethod[0];
	
		
	
	@Override
	public STMethod[] getMethods(String aName) {
		List<STMethod> resultList = new ArrayList();
		STMethod[] aMethods = getMethods();
		if (aMethods == null)
			return null;
		for (STMethod aMethod:aMethods) {
			if (aMethod.getName().equals(aName))
				resultList.add(aMethod);
		}
		return resultList.toArray(emptyMethodArray);
		
	}
	@Override
	public STNameable getSuperClass() {
		return superClass;
	}
	@Override
	public STNameable[] getDeclaredPropertyNames() {
		return declaredPropertyNames;
	}
	// recursion is safer
	@Override
	public STNameable[] getPropertyNames() {
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
	void maybeProcessInit(STMethod anSTMethod) {
//		if (!anSTMethod.getName().startsWith(INIT))  return;
//		if (isInit(anSTMethod)) return;
		if (anSTMethod.isInit()) return;

		String aPropertyName = anSTMethod.getName().substring(AnSTMethod.GET.length()).toLowerCase();
		String aPropertyType = anSTMethod.getReturnType();
		PropertyInfo aPropertyInfo = actualPropertyInfo.get(aPropertyName);
		if (aPropertyInfo == null) {
			aPropertyInfo = new APropertyInfo();
			actualPropertyInfo.put(aPropertyName, aPropertyInfo);
		}			
		aPropertyInfo.setGetter(anSTMethod);
	}
//	public static boolean isGetter(STMethod anSTMethod) {
//		return anSTMethod.getName().startsWith(GET) &&
//				anSTMethod.isPublic() &&
//				anSTMethod.getParameterTypes().length == 0;
//	}

	void maybeProcessGetter(STMethod anSTMethod) {
//		if (!isGetter(anSTMethod))
//			return;
		if (!anSTMethod.isGetter())
			return;
//		if (!anSTMethod.getName().startsWith(GET) ||
//				!anSTMethod.isPublic() ||
//				anSTMethod.getParameterTypes().length != 0) return;
//		String aPropertyName = anSTMethod.getName().substring(AnSTMethod.GET.length()).toLowerCase();
		String aPropertyName = anSTMethod.getName().substring(AnSTMethod.GET.length());
		String aPropertyType = anSTMethod.getReturnType();
		PropertyInfo aPropertyInfo = actualPropertyInfo.get(aPropertyName);
		if (aPropertyInfo == null) {
			aPropertyInfo = new APropertyInfo();
			actualPropertyInfo.put(aPropertyName, aPropertyInfo);
		}			
		aPropertyInfo.setGetter(anSTMethod);
	}
	public STNameable[] getDeclaredFields() {
		return declaredFields;
	}
//	public static boolean isSetter(STMethod anSTMethod) {
//		return anSTMethod.getName().startsWith(SET) &&
//				anSTMethod.isPublic() &&
//				anSTMethod.getParameterTypes().length != 1 &&
//				"void".equals(anSTMethod.getReturnType());
//	}
	void maybeProcessSetter(STMethod anSTMethod) {
//		if (!anSTMethod.getName().startsWith(SET) ||
//		!anSTMethod.isPublic() ||
//		anSTMethod.getParameterTypes().length != 1) return;
//		if (!isSetter(anSTMethod)) 
//			return;	
		if (!anSTMethod.isSetter()) 
			return;
//		String aPropertyName = anSTMethod.getName().substring(AnSTMethod.SET.length()).toLowerCase();
		String aPropertyName = anSTMethod.getName().substring(AnSTMethod.SET.length());

		String aPropertyType = anSTMethod.getReturnType();
		PropertyInfo aPropertyInfo = actualPropertyInfo.get(aPropertyName);
		if (aPropertyInfo == null) {
			aPropertyInfo = new APropertyInfo();
			actualPropertyInfo.put(aPropertyName, aPropertyInfo);
		}			
		aPropertyInfo.setSetter(anSTMethod);
	}
	@Override
	public void introspect() {
		for (STMethod anSTMethod:declaredMethods) {
			maybeProcessGetter(anSTMethod);
			maybeProcessSetter(anSTMethod);			
		}
	}
	@Override
	public Map<String, PropertyInfo> getDeclaredPropertyInfos() {
		return actualPropertyInfo;
	}
	// should use recursion actually
	@Override
	public Map<String, PropertyInfo> getPropertyInfos() {
		Map<String, PropertyInfo> result = new HashMap<>();
		Map<String, PropertyInfo> aPropertyInfos = new HashMap();
		STType anSTClass = this;
		while (true) {
			aPropertyInfos = anSTClass.getDeclaredPropertyInfos();	
			for (String aPropertyName:aPropertyInfos.keySet()) {
				result.put(aPropertyName, aPropertyInfos.get(aPropertyName));
			}
			STNameable aSuperClass = anSTClass.getSuperClass();
			if (aSuperClass == null || TagBasedCheck.isExternalClass(aSuperClass.getName()))
			     break;
			anSTClass = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aSuperClass.getName());
			if (anSTClass == null)
				return null; // assume that we are only inheriting our own types
		}
		return result;
	}
	public static List<STNameable> getAllTypes(STNameable aType) {
		if (TagBasedCheck.isExternalClass(TypeVisitedCheck.toShortTypeName(aType.getName())))
			return emptyList;
		List<STNameable> result = new ArrayList();
		result.add(aType);
		STType anSTType =  SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType.getName());
		if (anSTType == null) return null;
		STNameable[] anInterfaces = anSTType.getInterfaces();
		for (STNameable anInterface:anInterfaces) {
			 List<STNameable> anInterfaceTypes = getAllTypes(anInterface);
			 if (anInterfaceTypes == null)
				 return null;
			 result.addAll(anInterfaceTypes);			 
		}
		if (anSTType.isInterface())
			return result;
		STNameable aSuperClass = anSTType.getSuperClass();
		if (aSuperClass == null) 
			return result;
		List<STNameable> aSuperTypes = getAllTypes(anSTType.getSuperClass());
		if (aSuperTypes == null)
			return null;
		addAllNonDuplicates(result, aSuperTypes);
//		result.addAll(aSuperType);
		return result;		
	}
	public static void addAllNonDuplicates (List aList, List anAdditions ) {
		for (Object anAddition:anAdditions) {
			if (aList.contains(anAddition)) continue;
			aList.add(anAddition);
		}
	}
	public static List emptyList = new ArrayList();
	public static List<STNameable> getSuperTypes(STNameable aType) {
		if (TagBasedCheck.isExternalClass(TypeVisitedCheck.toShortTypeName(aType.getName())))
			return emptyList;	
		List<STNameable> result = new ArrayList();
		result.add(aType);
		
		STType anSTType =  SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType.getName());
		if (anSTType == null) return null;
		if (anSTType.isInterface()) {
		STNameable[] anInterfaces = anSTType.getInterfaces();
		for (STNameable anInterface:anInterfaces) {
			if (result.contains(anInterface)) // an interface may be extended by many
				continue;
			List<STNameable> anInterfaceTypes = getSuperTypes(anInterface);
			 if (anInterfaceTypes == null)
				 return null;
//			 result.addAll(anInterfaceTypes);	
			 addAllNonDuplicates(result, anInterfaceTypes);	
		}
		} else {
			
		
		STNameable aSuperClass = anSTType.getSuperClass();
		if (aSuperClass == null) 
			return result;	
		List<STNameable> aSuperTypes = getSuperTypes(anSTType.getSuperClass());
		if (aSuperTypes == null)
			return null;
		result.addAll(aSuperTypes);
//		addAllNonDuplicates(result, aSuperTypes);
		}
		return result;		
	}
	@Override
	public List<STNameable> getAllTypes() {		
		List<STNameable> result = new ArrayList();
		return getAllTypes(this);	
		
	}
	@Override
	public List<String> getAllTypeNames() {
		List<STNameable> allTypes = getAllTypes();
		if (allTypes == null) return null;
		return toNameList(allTypes);
	}
	@Override
	public List<String> getSuperTypeNames() {
		List<STNameable> aTypes = getSuperTypes();
		if (aTypes == null) return null;
		return toNameList(aTypes);
	}
	@Override
	public List<STNameable> getSuperTypes() {		
		List<STNameable> result = new ArrayList();
		return getSuperTypes(this);	
		
	}
	public static List<String> toNameList(List<STNameable> aNameableList) {
		if (aNameableList == null) return null;
		List<String> result = new ArrayList();
		for (STNameable aNameable:aNameableList) {
			String aShortName = TypeVisitedCheck.toShortTypeName(aNameable.getName());
			if (!result.contains(aShortName))
			     result.add(aShortName);
		}
		return result;
	}
	public static List<String> toNormalizedList(List<String> anOriginal) {
		if (anOriginal ==null) return null;
		List<String> result = new ArrayList();
		for (String aNonNormalizedEntry:anOriginal) {
			result.add(TypeVisitedCheck.toShortTypeName(aNonNormalizedEntry));
		}
		return result;
	}
	@Override
	public List<String> getNonSuperTypes() {		
		SymbolTable aSymbolTable = SymbolTableFactory.getOrCreateSymbolTable();
		List<String> anAllTypes;
		if (isInterface)
			anAllTypes = aSymbolTable.getAllInterfaceNames();
		else
			anAllTypes = aSymbolTable.getAllClassNames();		
		List<String> aNormalizedTypes = toNormalizedList(anAllTypes);
		List<String> anAllMyTypes = toNameList(getSuperTypes());
		return difference(aNormalizedTypes, anAllMyTypes);		
	}
	@Override
	public List<String> getSubTypes() {		
//		SymbolTable aSymbolTable = SymbolTableFactory.getOrCreateSymbolTable();
//		List<String> anAllTypes;
//		if (isInterface)
//			anAllTypes = aSymbolTable.getAllInterfaceNames();
//		else
//			anAllTypes = aSymbolTable.getAllClassNames();		
//		List<String> aNormalizedTypes = toNormalizedList(anAllTypes);
//		List<String> anAllMyTypes = toNameList(getSuperTypes());
		List<String>  aNonSuperTypes = getNonSuperTypes();
		List<String> result = new ArrayList();
		String myShortName = TypeVisitedCheck.toShortTypeName(name);
		for (String aNonSuperType:aNonSuperTypes) {
			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aNonSuperType);
			if (anSTType == null) return null;
			List<String> aSuperTypes = toNormalizedList(anSTType.getSuperTypeNames());
			if (aSuperTypes == null)
				return null;
			if (aSuperTypes.contains(myShortName))
				result.add(aNonSuperType);
		}
		return result;
	}
	@Override
	public List<String> getPeerTypes() {
		List<String>  aNonSuperTypes = toNormalizedList(getNonSuperTypes());
		if (aNonSuperTypes == null)
			return null;
		List<String> aSubTypes = toNormalizedList(getSubTypes());
		if (aSubTypes == null)
			return null;
		List<String> aResult = difference(aNonSuperTypes, aSubTypes);
		return aResult;		
	}
	
	@Override
	public Boolean isNonType(String aTypeName) {
		return getNonSuperTypes().contains(TypeVisitedCheck.toShortTypeName(aTypeName));
	}
	@Override
	public Boolean isType(String aTypeName) {
		List<STNameable> aTypes = getAllTypes();
		if (aTypes == null) return null;
		return toNameList(aTypes).contains(TypeVisitedCheck.toShortTypeName(aTypeName));
	}
	@Override
	public Boolean hasPublicMethod(String aSignature) {
		STMethod[] stMethods = getMethods();
		if (stMethods == null) return null;
		return Arrays.asList(stMethods).contains(aSignature);		
	}
	@Override
	public Boolean hasDeclaredMethod(String aSignature) {
		STMethod[] stMethods = getDeclaredMethods();
		if (stMethods == null) return null;
		return Arrays.asList(stMethods).contains(aSignature);		
	}
	public static List intersect(List aList1, List aList2) {
		List aResult = new ArrayList();
		for (Object anElement1:aList1) {
			for (Object anElement2:aList2) {
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
			for (Object anElement:aList1) {
				if (!aList2.contains(anElement))
					aResult.add(anElement);
			}
		
		return aResult;
	}
	public static List<STNameable> commonSuperTypes(String aType1, String aType2) {
//		List<STNameable> result = new ArrayList();
		STType anSTType1 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType1);
		if (anSTType1 == null) return null;
		STType anSTType2 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType2);
		if (anSTType2 == null) return null;
		List<STNameable> aSuperTypes1 = anSTType1.getSuperTypes();
		if (aSuperTypes1 == null)
			return null;
		List<STNameable> aSuperTypes2 = anSTType2.getSuperTypes();
		if (aSuperTypes2 == null)
			return null;
		return intersect (aSuperTypes1, aSuperTypes2);		
	}
	@Override
	public List<String> getAllSignatures() {
		List<String> result = new ArrayList();
		STMethod[] anSTMethods = getMethods();
		if (anSTMethods == null)
			return null;
		for (STMethod anSTMethod:anSTMethods) {
			result.add(anSTMethod.getSignature());
		}
		return result;
	}
	public static List<String> commonSignatures(String aType1, String aType2) {
		STType anSTType1 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType1);
		if (anSTType1 == null) return null;
		STType anSTType2 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType2);
		if (anSTType2 == null) return null;
		return commonSignatures(anSTType1, anSTType2);		
//			
	}
	public static List<String> commonSignatures(STType aType1, STType aType2) {
		List<String> aSignatures1 = aType1.getAllSignatures();
		if (aSignatures1 == null) return null;
		List<String> aSignatures2 = aType2.getAllSignatures();
		if (aSignatures2 == null) return null;
		return intersect(aSignatures1, aSignatures2);		
//			
	}
	@Override
	public List<String> signaturesCommonWith (STType aType) {
		return commonSignatures(this, aType);
	}
	@Override
	public List<String> signaturesCommonWith (String aTypeName) {
		STType aPeerType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
		if (aPeerType == null)
			return null;
		return commonSignatures(this, aPeerType);
	}
}
