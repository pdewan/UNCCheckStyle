package unc.cs.parseTree;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class AnAssignOperation extends ATransitiveOperation {

	public AnAssignOperation(String name) {
		super(new Integer[]{TokenTypes.ASSIGN}, name);
	}


}
