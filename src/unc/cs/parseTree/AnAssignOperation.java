package unc.cs.parseTree;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class AnAssignOperation extends ATransitiveOperation {

	public AnAssignOperation(String anExpression) {
		super(new Integer[]{TokenTypes.ASSIGN}, anExpression);
	}


}
