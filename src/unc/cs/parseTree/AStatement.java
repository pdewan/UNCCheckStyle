package unc.cs.parseTree;

public abstract class AStatement implements CheckedStatement {
	int tokenType;
	public AStatement(int tokenType) {
	this.tokenType = tokenType;
}

	@Override
	public int getTokenType(){
		return tokenType;
	}
	
}
