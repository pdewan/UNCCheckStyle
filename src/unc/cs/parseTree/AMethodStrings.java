package unc.cs.parseTree;

import unc.cs.symbolTable.STMethod;

public class AMethodStrings extends AMethodSpecification implements MethodStrings {
	String[] specifications;
	public AMethodStrings(STMethod method, String[] aSpecifications) {
		super(method);
		specifications = aSpecifications;
	}
	
	@Override
	public String[] getSpecifications() {
		return specifications;
	}

}
