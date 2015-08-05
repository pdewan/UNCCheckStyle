package unc.cs.checks;

import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.DetailAST;

public abstract class STTypeVisitedComprehensively extends ComprehensiveVisitCheck {
	@Override
//	public int[] getDefaultTokens() {
//		return new int[] {TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF, TokenTypes.PACKAGE_DEF};
//	} 
//	
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF, TokenTypes.PACKAGE_DEF};
	} 

	public STTypeVisitedComprehensively() {

	}
	protected abstract  Boolean typeCheck(STType anSTClass) ;
	@Override
	public void visitType(DetailAST ast) {
    	super.visitType(ast); // need this to get full type name for checking tags
    	maybeAddToPendingTypeChecks(ast);
//    	STType anSTClass = SymbolTableFactory.getOrCreateSymbolTable().
//    			getSTClassByFullName(fullTypeName);
//    	if (!typeCheck(anSTClass))
//    		log(ast);

    }
	
	public Boolean doPendingCheck(DetailAST ast, DetailAST aTreeAST) {
//    	String aTypeName = getName(getEnclosingTypeDeclaration(aTreeAST));
//    	DetailAST aPackageAST = getEnclosingPackageDeclaration(aTreeAST);
//    	String aPackageName = DEFAULT_PACKAGE;
//    	if (aPackageAST != null)
//    		 aPackageName = getPackageName(aPackageAST);
//    	String aFullName = aPackageName + "." + aTypeName;
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aFullName);
		STType anSTType = getSTType(aTreeAST);
		Boolean aTypeCheck = typeCheck(anSTType);
		if (aTypeCheck == null)
			return null;
		if (!aTypeCheck)
    		log(ast, aTreeAST, null);
		return aTypeCheck;
		
	}

	
}
