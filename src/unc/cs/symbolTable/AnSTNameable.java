package unc.cs.symbolTable;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTNameable implements STNameable {
	DetailAST ast;
	String name;
	Object data;
	public AnSTNameable(DetailAST ast, String name) {
		super();
		this.ast = ast;
		this.name = name;
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

}
