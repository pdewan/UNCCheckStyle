package unc.cs.parseTree;

import unc.cs.symbolTable.STMethod;

public interface MethodParseTree extends MethodSpecification {

	public abstract CheckedNode getParseTree();

}