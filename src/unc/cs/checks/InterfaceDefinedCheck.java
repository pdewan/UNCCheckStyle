package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class InterfaceDefinedCheck extends TypeDefinedCheck{
	public static final String MSG_KEY = "interfaceDefined";	

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.INTERFACE_DEF, TokenTypes.PACKAGE_DEF};
	}
	public void visitToken(DetailAST ast) {
    	System.out.println("Check called:" + MSG_KEY);

		if (maybeVisitPackage(ast) ) 
			return;
		
		DetailAST anInterfaceNameAST = ast.findFirstToken(TokenTypes.IDENT);
		String anInterfaceName = anInterfaceNameAST.getText();
		String aFullName = packageName + "." + anInterfaceName;
		SymbolTableFactory.getOrCreateSymbolTable().
			getInterfaceNameToAST().put(aFullName, ast);
		log(ast.getLineNo(), MSG_KEY, aFullName);
		System.out.println(MSG_KEY + " " + aFullName);
	}
}
