package unc.cs.parseTree;

public interface IFStatement {

	CheckedStatement getThenPart();

	CheckedStatement getElsePart();

}