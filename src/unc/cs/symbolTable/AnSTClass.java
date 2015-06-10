package unc.cs.symbolTable;

import java.util.HashMap;
import java.util.Map;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTClass extends AnSTNameable implements STClass {
	STNameable[] declaredPropertyNames, delcaredEditablePropertyNames, tags;
	
	protected final STMethod[] declaredMethods;
	protected final String[] interfaces;
	protected final String packageName;
	protected final boolean isInterface;
	protected final String superClass;
	protected  STNameable structurePatternName;
	
	protected STMethod[] getters;
	protected STMethod[] setters;
	protected Map<String, PropertyInfo> actualPropertyInfo = new HashMap();
	

	
	public AnSTClass(DetailAST ast, String name, 
			STMethod[] declaredMethods, String[] interfaces, String superClass,
			String packageName, boolean isInterface) {
		super(ast, name);
		this.declaredMethods = declaredMethods;
		this.interfaces = interfaces;
		this.superClass = superClass;
		this.packageName = packageName;
		this.isInterface = isInterface;
	}
	public STMethod[] getDeclaredMethods() {
		return declaredMethods;
	}
	public String[] getInterfaces() {
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
	public String getSuperClass() {
		return superClass;
	}
	@Override
	public STNameable[] getDeclaredPropertyNames() {
		return declaredPropertyNames;
	}
	public void initDeclaredPropertyNames(STNameable[] propertyNames) {
		this.declaredPropertyNames = propertyNames;
	}
	@Override
	public STNameable[] getDeclaredEditablePropertyNames() {
		return delcaredEditablePropertyNames;
	}
	public void initEditablePropertyNames(STNameable[] editablePropertyNames) {
		this.delcaredEditablePropertyNames = editablePropertyNames;
	}
	@Override
	public STNameable[] getTags() {
		return tags;
	}
	public void initTags(STNameable[] tags) {
		this.tags = tags;
	}
	@Override
	public STNameable getStructurePatternName() {
		return structurePatternName;
	}
	@Override
	public void initStructurePatternName(STNameable structurePatternName) {
		this.structurePatternName = structurePatternName;
	}
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
