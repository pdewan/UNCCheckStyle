package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public abstract class TypeDefinedCheck extends UNCCheck {
	public static final String MSG_KEY = "typeDefined";	
	public static final String DEFAULT_PACKAGE = "default"; 
	protected String packageName;
	@Override
    public void beginTree(DetailAST ast) {
        packageName = DEFAULT_PACKAGE;
    } 
	
	public boolean maybeVisitPackage(DetailAST ast) {
		if (ast.getType() == TokenTypes.PACKAGE_DEF) {
//			packageName = ast.findFirstToken(TokenTypes.IDENT).getText();
//			System.out.println("found package:" + packageName);	
			visitPackage(ast);
			return true;
		}
		return false;
	}
	public void visitPackage(DetailAST ast) {
		packageName = ast.findFirstToken(TokenTypes.IDENT).getText();
		System.out.println("found package:" + packageName);	
	}
}
