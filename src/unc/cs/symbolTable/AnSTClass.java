package unc.cs.symbolTable;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTClass extends AnSTNameable implements STClass {
	STMethod[] declaredMethods;
	String[] interfaces;
	String packageName;
	boolean isInterface;
	String superClass;
	
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
	

}
