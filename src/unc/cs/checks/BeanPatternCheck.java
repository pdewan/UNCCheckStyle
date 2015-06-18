package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class BeanPatternCheck extends TypeVisitedCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF,
				TokenTypes.PACKAGE_DEF };

	}
	public void visitType(DetailAST ast) {
    	super.visitType(ast);
	}
	public void checkedVisitToken(DetailAST ast) {
		// System.out.println("Check called:" + MSG_KEY);
		switch (ast.getType()) {
		case TokenTypes.PACKAGE_DEF:
			visitPackage(ast);
			return;
		case TokenTypes.CLASS_DEF:
			visitType(ast);
			return;
		case TokenTypes.INTERFACE_DEF:
			visitType(ast);
			return;
		default:
			System.err.println("Unexpected token");
		}
	}
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}

}
