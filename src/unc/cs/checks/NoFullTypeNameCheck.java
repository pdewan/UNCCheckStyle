package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class NoFullTypeNameCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "noFullTypeName";

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
				TokenTypes.TYPE,
				TokenTypes.LITERAL_NEW,
				TokenTypes.CLASS_DEF,
				TokenTypes.ANNOTATION
		};
	}
	public void visitTypeOrInstantiation(DetailAST ast) {
		FullIdent aFullIdent = FullIdent.createFullIdentBelow(ast);
		String aTypeName = aFullIdent.getText();
		if (aTypeName.indexOf(".") != -1) {
			log(ast.getLineNo(), ast.getColumnNo(), msgKey(),
					aTypeName);
		}
	}
	@Override
	public void visitTypeUse(DetailAST ast) {
    	visitTypeOrInstantiation(ast);
    }
	@Override
	public void visitNew(DetailAST ast) {
		visitTypeOrInstantiation(ast);
	}
	
//	public void visitInstantiation(DetailAST ast) {
//		FullIdent aFullIdent = FullIdent.createFullIdentBelow(ast);
//		String aTypeName = aFullIdent.getText();
//		if (aTypeName.indexOf(".") != -1) {
//			log(ast.getLineNo(), ast.getColumnNo(), msgKey(),
//					aTypeName);
//		}
//	}
	public void doVisitToken(DetailAST ast) {
		visitTypeOrInstantiation(ast);

	}
		

}
