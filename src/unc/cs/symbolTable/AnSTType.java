package unc.cs.symbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import unc.cs.checks.ComprehensiveVisitCheck;
import unc.cs.checks.TagBasedCheck;

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
		String aPropertyName = anSTMethod.getName().substring(AnSTMethod.GET.length()).toLowerCase();
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
		String aPropertyName = anSTMethod.getName().substring(AnSTMethod.SET.length()).toLowerCase();
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
}
