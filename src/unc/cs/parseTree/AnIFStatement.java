package unc.cs.parseTree;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class AnIFStatement extends AnAtomicOperation implements IFStatement {
	CheckedStatement thenPart;
	CheckedStatement elsePart;
	
	public AnIFStatement(CheckedStatement thenPart, CheckedStatement elsePart) {
		super(TokenTypes.LITERAL_IF);
		this.thenPart = thenPart;
		this.elsePart = elsePart;
	}
	@Override
	public CheckedStatement getThenPart() {
		return thenPart;
	}
	@Override
	public CheckedStatement getElsePart() {
		return elsePart;
	}
	
	

}
