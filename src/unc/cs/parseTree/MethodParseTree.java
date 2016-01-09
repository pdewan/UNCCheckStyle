package unc.cs.parseTree;

import unc.cs.symbolTable.STMethod;

public interface MethodParseTree {

	public abstract STMethod getMethod();

	public abstract CheckedStatement getParseTree();

}