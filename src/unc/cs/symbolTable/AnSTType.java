package unc.cs.symbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTType extends AnSTNameable implements STType {
	protected final STNameable[] declaredPropertyNames, declaredEditablePropertyNames, tags, imports;	
	protected final STMethod[] declaredMethods;
	protected final STMethod[] declaredConstructors;
	protected final STNameable[] interfaces;
	protected final String packageName;
	protected final boolean isInterface, isGeneric, isElaboration;
	protected final STNameable superClass;
	protected final  STNameable structurePatternName;	
	protected STMethod[] getters;
	protected STMethod[] setters;
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
			STNameable[] anImports
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
	@Override
	public STMethod[] getMethods() {
		// TODO Auto-generated method stub
		return declaredMethods;
	}
	@Override
	public STMethod[] getMethod(String aName, String[] aParameterTypes) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public STNameable getSuperClass() {
		return superClass;
	}
	@Override
	public STNameable[] getDeclaredPropertyNames() {
		return declaredPropertyNames;
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
	public  static final String GET = "get";
	public  static final String SET = "set";

	void maybeProcessGetter(STMethod anSTMethod) {
		if (!anSTMethod.getName().startsWith(GET) ||
				!anSTMethod.isPublic() ||
				anSTMethod.getParameterTypes().length != 0) return;
		String aPropertyName = anSTMethod.getName().substring(GET.length()).toLowerCase();
		String aPropertyType = anSTMethod.getReturnType();
		PropertyInfo aPropertyInfo = actualPropertyInfo.get(aPropertyName);
		if (aPropertyInfo == null) {
			aPropertyInfo = new APropertyInfo();
			actualPropertyInfo.put(aPropertyName, aPropertyInfo);
		}			
		aPropertyInfo.setGetter(anSTMethod);
	}
	void maybeProcessSetter(STMethod anSTMethod) {
		if (!anSTMethod.getName().startsWith(SET) ||
		!anSTMethod.isPublic() ||
		anSTMethod.getParameterTypes().length != 1) return;
			
		String aPropertyName = anSTMethod.getName().substring(SET.length()).toLowerCase();
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
}
