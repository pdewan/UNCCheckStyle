package unc.cs.checks;

import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.DetailAST;

public abstract class STTypeVisited extends TypeVisitedCheck {
	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF, TokenTypes.PACKAGE_DEF};
	} 

	public STTypeVisited() {

	}
	protected abstract  boolean typeCheck(STType anSTClass) ;

	public void visitType(DetailAST ast) {
    	super.visitType(ast);
    	STType anSTClass = SymbolTableFactory.getOrCreateSymbolTable().
    			getSTClassByFullName(fullTypeName);
    	if (!typeCheck(anSTClass))
    		log(ast);

    }

	
}
