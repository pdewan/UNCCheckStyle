package unc.cs.symbolTable;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class AnSTNameable implements STNameable {
	DetailAST ast;
	String name;
	public AnSTNameable(DetailAST ast, String name) {
		super();
		this.ast = ast;
		this.name = name;
	}

	@Override
	public DetailAST getAST() {
		return ast;
	}

	

	@Override
	public String getName() {
		return name;
	}

}
