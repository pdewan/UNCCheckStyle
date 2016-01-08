package unc.cs.parseTree;

public class AnIFStatement extends AnAtomicOperation implements IFStatement {
	Statement thenPart;
	Statement elsePart;
	
	public AnIFStatement(int tokenType, Statement thenPart, Statement elsePart) {
		super(tokenType);
		this.thenPart = thenPart;
		this.elsePart = elsePart;
	}
	@Override
	public Statement getThenPart() {
		return thenPart;
	}
	@Override
	public Statement getElsePart() {
		return elsePart;
	}
	
	

}
