package unc.cs.parseTree;

import java.util.ArrayList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ANodesCollection extends ACheckedNode implements NodesCollection {
	List<CheckedNode> nodes;
	

	public ANodesCollection(List<CheckedNode> aNodes) {
		super(new Integer[] {});
		this.nodes = aNodes;
	}


	@Override
	public List<CheckedNode> getNodes() {
		return nodes;
	}

	

}
