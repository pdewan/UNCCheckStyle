package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public abstract class TypeDefinedCheck extends Check {
	public static final String MSG_KEY = "typeDefined";	
	public static final String DEFAULT_PACKAGE = "default"; 
	String packageName;
	@Override
    public void beginTree(DetailAST ast) {
        packageName = DEFAULT_PACKAGE;
    } 
	
	public boolean maybeVisitPackage(DetailAST ast) {
		if (ast.getType() == TokenTypes.PACKAGE_DEF) {
			packageName = ast.findFirstToken(TokenTypes.IDENT).getText();
			System.out.println("found package:" + packageName);	
			return true;
		}
		return false;
	}

}
