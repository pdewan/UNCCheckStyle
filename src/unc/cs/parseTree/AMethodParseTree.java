package unc.cs.parseTree;

import unc.cs.symbolTable.STMethod;

public class AMethodParseTree implements MethodParseTree {
	STMethod method;
	CheckedNode parseTree;
	public AMethodParseTree(STMethod method, CheckedNode parseTree) {
		super();
		this.method = method;
		this.parseTree = parseTree;
	}
	@Override
	public STMethod getMethod() {
		return method;
	}
	@Override
	public CheckedNode getParseTree() {
		return parseTree;
	}

}
