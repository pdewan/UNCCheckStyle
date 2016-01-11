package unc.cs.parseTree;

import java.util.Arrays;
import java.util.List;

public abstract class ACheckedNode implements CheckedNode {
	List<Integer> tokenTypes;
	public ACheckedNode(Integer[] tokenType) {
	this.tokenTypes = Arrays.asList(tokenType);
}

	@Override
	public List<Integer> getTokenTypes(){
		return tokenTypes;
	}
	
}
