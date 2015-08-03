package unc.cs.checks;

import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class InterfaceDefinedCheck extends ComprehensiveVisitCheck{
	public static final String MSG_KEY = "interfaceDefined";	

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.INTERFACE_DEF, TokenTypes.PACKAGE_DEF, 
//				TokenTypes.ANNOTATION
				};
	}
	public void visitType(DetailAST ast) {  

    	super.visitType(ast);
    	if (checkIncludeExcludeTagsOfCurrentType())
		log(ast.getLineNo(), msgKey(), fullTypeName);


    }
//	public void doVisitToken(DetailAST ast) {
//		
//		switch (ast.getType()) {
//		case TokenTypes.PACKAGE_DEF: 
//			visitPackage(ast);
//			return;
//		case TokenTypes.INTERFACE_DEF:
//			visitType(ast);
//			return;
//		
//		default:
//			System.err.println("Unexpected token");
//		}
//		
//	}
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
}
