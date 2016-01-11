package unc.cs.parseTree;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class AnIFStatement extends AnAtomicOperation implements IFStatement {
	CheckedNode expression;
	CheckedNode thenPart;
	CheckedNode elsePart;
	
	public AnIFStatement(CheckedNode anExpression, CheckedNode thenPart, CheckedNode elsePart) {
		super(new Integer[] {TokenTypes.LITERAL_IF});
		this.thenPart = thenPart;
		this.elsePart = elsePart;
		expression = anExpression;
	}
	@Override
	public CheckedNode getThenPart() {
		return thenPart;
	}
	@Override
	public CheckedNode getElsePart() {
		return elsePart;
	}
	
	

}
