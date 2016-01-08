package unc.cs.parseTree;

public class AnAtomicOperation implements AtomicOperation {
	int tokenType;

	public AnAtomicOperation(int tokenType) {
		super();
		this.tokenType = tokenType;
	}

	@Override
	public int getTokenType() {
		return tokenType;
	}
}
