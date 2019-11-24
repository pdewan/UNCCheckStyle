package unc.cs.symbolTable;

import java.util.Arrays;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.cs.checks.ComprehensiveVisitCheck;


public class AnSTNameable implements STNameable {
	DetailAST ast;
	String name;
	Object data;
//	String[] components;
	public AnSTNameable( String name) {
		super();
		this.name = name;
	}
	public AnSTNameable(DetailAST ast, String name) {
		super();
		this.ast = ast;
		this.name = name;
//		components = ComprehensiveVisitCheck.splitCamelCaseHyphenDash(name);
	}
	public AnSTNameable(DetailAST ast, String name, String aData) {
		this(ast, name);
		data = aData;
	}

	@Override
	public DetailAST getAST() {
		return ast;
	}
	public boolean equals(Object anObject) {
		if (anObject instanceof STNameable) {
			return ((STNameable) anObject).getName().equals(name);
		} else 
			return super.equals(anObject);
	}

	

	@Override
	public String getName() {
		return name;
	}
	@Override
	public Object getData() {
		return data;
	}
	public String toString() {
		return name;
	}
//	@Override
//	public String[] getComponents() {
//		return components;
//	}
	
//	public static void main (String[] args) {
//		String[] aSplit = ComprehensiveVisitCheck.splitCamelCase("hel_loABC23Goo-dbye");
//		System.out.println(Arrays.toString(aSplit));
//	}
}
