package unc.cs.checks;

import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ClassDefinedCheck extends TypeDefinedCheck{
	
	public static final String MSG_KEY = "classDefined";

	
	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF, TokenTypes.PACKAGE_DEF};
	}
	public void visitToken(DetailAST ast) {
//    	System.out.println("Check called:" + MSG_KEY);

		if (maybeVisitPackage(ast) ) 
			return;
		
		DetailAST aClassNameAST = ast.findFirstToken(TokenTypes.IDENT);
		String aShortName = aClassNameAST.getText();
		String aFullName = packageName + "." + aShortName;
		SymbolTableFactory.getOrCreateSymbolTable().getClassNameToAST().
			put(aFullName, ast);
		extendibleLog(ast.getLineNo(), MSG_KEY, aFullName);
		System.out.println(MSG_KEY + " " + aFullName);
		
	}
}
