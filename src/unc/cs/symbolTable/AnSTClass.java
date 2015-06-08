package unc.cs.symbolTable;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTClass extends AnSTAnnotatable implements STClass {
	STMethod[] declaredMethods;
	String[] interfaces;
	String packageName;
	boolean isInterface;
	String superClass;
	
	public AnSTClass(DetailAST ast, String name, String[] actualParameters,
			STMethod[] declaredMethods, String[] interfaces, String superClass,
			String packageName, boolean isInterface) {
		super(ast, name, actualParameters);
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
		return null;
	}
	@Override
	public STMethod[] getMethod(String aName, String[] aParameterTypes) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
