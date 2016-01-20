package unc.cs.parseTree;

import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ABody extends ATransitiveOperation implements Body{

	public ABody(String name) {
		super(new Integer[TokenTypes.METHOD_DEF], name);
	}

}
