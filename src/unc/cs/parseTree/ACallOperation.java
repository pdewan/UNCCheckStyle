package unc.cs.parseTree;

import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ACallOperation extends ATransitiveOperation {

	public ACallOperation(String name) {
		super(new Integer[]{TokenTypes.METHOD_CALL}, name);
	}

}
