package unc.cs.checks;

import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.DetailAST;

public abstract class STClassVisited extends STTypeVisited {

	public STClassVisited() {

	}
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF,  TokenTypes.PACKAGE_DEF};
	} 
	public void doVisitToken(DetailAST ast) {		
		switch (ast.getType()) {
		case TokenTypes.PACKAGE_DEF: 
			visitPackage(ast);
			return;
		case TokenTypes.CLASS_DEF:
			if (getFullTypeName() == null)

			visitType(ast);
			return;		
		default:
			System.err.println("Unexpected token");
		}
		
	}
	
}
