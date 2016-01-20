package unc.cs.parseTree;

public abstract class ATransitiveOperation extends AnAtomicOperation implements TransitiveOperation {
	String operand;

	public ATransitiveOperation(Integer[] tokenType, String name) {
		super(tokenType);
		this.operand = name;
	}
	@Override
	public String getOperand() {
		return operand;
	}
	
	public String toString() {
		return super.toString() + "  " + operand;
	}
	

}
