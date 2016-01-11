package unc.cs.parseTree;

import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class AnyNode extends ACheckedNode {
	public static CheckedNode singleton = new AnyNode();
	public AnyNode() {
		super(new Integer[] {});

	}
	public static CheckedNode getSingleton() {
		return singleton;
	}

}
