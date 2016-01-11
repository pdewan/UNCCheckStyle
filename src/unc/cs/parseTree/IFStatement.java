package unc.cs.parseTree;

public interface IFStatement extends CheckedNode{

	CheckedNode getThenPart();

	CheckedNode getElsePart();

}