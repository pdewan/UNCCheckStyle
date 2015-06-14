package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class NoFullTypeNameCheck extends UNCCheck {
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
				TokenTypes.LITERAL_NEW			
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
	
//	public void visitInstantiation(DetailAST ast) {
//		FullIdent aFullIdent = FullIdent.createFullIdentBelow(ast);
//		String aTypeName = aFullIdent.getText();
//		if (aTypeName.indexOf(".") != -1) {
//			log(ast.getLineNo(), ast.getColumnNo(), msgKey(),
//					aTypeName);
//		}
//	}
	public void visitToken(DetailAST ast) {
		visitTypeOrInstantiation(ast);
//		FullIdent aFullIdent = FullIdent.createFullIdentBelow(ast);
//		String aTypeName = aFullIdent.getText();
//		if (aTypeName.indexOf(".") != -1) {
//			log(ast.getLineNo(), ast.getColumnNo(), msgKey(),
//					aTypeName);
//		}
	}
		

}
