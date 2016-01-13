package unc.cs.parseTree;

import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class AReturnOperation extends ATransitiveOperation{

	public AReturnOperation(String anExpression) {
		super(new Integer[] {TokenTypes.LITERAL_RETURN}, anExpression);
		// TODO Auto-generated constructor stub
	}

}
