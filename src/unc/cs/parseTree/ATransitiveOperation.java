package unc.cs.parseTree;

public abstract class ATransitiveOperation extends AnAtomicOperation implements TransitiveOperation {
	String name;

	public ATransitiveOperation(Integer[] tokenType, String name) {
		super(tokenType);
		this.name = name;
	}
	@Override
	public String getName() {
		return name;
	}

}
