package unc.cs.checks;

import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.DetailAST;

public abstract class STClassVisitedComprehensively extends STTypeVisitedComprehensively {
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF,  TokenTypes.PACKAGE_DEF};
	} 
	
	public STClassVisitedComprehensively() {

	}
	@Override
	boolean doCheck(STType anSTType)  {
    	return !anSTType.isInterface() && !anSTType.isEnum();
    }

//	public void doVisitToken(DetailAST ast) {		
//		switch (ast.getType()) {
//		case TokenTypes.PACKAGE_DEF: 
//			visitPackage(ast);
//			return;
//		case TokenTypes.CLASS_DEF:
//			visitType(ast);
//			return;		
//		default:
//			System.err.println("Unexpected token");
//		}
//		
//	}
	
}
