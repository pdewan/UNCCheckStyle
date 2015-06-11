package unc.cs.checks;

import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ClassDefinedCheck extends TypeVisitedCheck{
	
	public static final String MSG_KEY = "classDefined";
	
	
	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF, TokenTypes.PACKAGE_DEF};
	}
	public void visitType(DetailAST ast) {  

    	super.visitType(ast);
		log(ast.getLineNo(), msgKey(), typeName);


    }
	public void visitToken(DetailAST ast) {
		
		switch (ast.getType()) {
		case TokenTypes.PACKAGE_DEF: 
			visitPackage(ast);
			return;
		case TokenTypes.CLASS_DEF:
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
