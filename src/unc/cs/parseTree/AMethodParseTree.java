package unc.cs.parseTree;

import unc.cs.symbolTable.STMethod;

public class AMethodParseTree implements MethodParseTree {
	STMethod method;
	CheckedStatement parseTree;
	public AMethodParseTree(STMethod method, CheckedStatement parseTree) {
		super();
		this.method = method;
		this.parseTree = parseTree;
	}
	@Override
	public STMethod getMethod() {
		return method;
	}
	@Override
	public CheckedStatement getParseTree() {
		return parseTree;
	}

}
