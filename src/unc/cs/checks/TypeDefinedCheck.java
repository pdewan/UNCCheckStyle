package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public abstract class TypeDefinedCheck extends Check {
	public static final String MSG_KEY = "typeDefined";	
	public static final String DEFAULT_PACKAGE = "default"; 
	protected String packageName;
	protected String typeName;
	protected DetailAST typeAST;
	@Override
    public void beginTree(DetailAST ast) {
        packageName = DEFAULT_PACKAGE;
    } 
//	@Override
//	public int[] getDefaultTokens() {
//		return new int[] {TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,  
//						TokenTypes.INTERFACE_DEF, TokenTypes.METHOD_DEF, TokenTypes.PARAMETER_DEF };
//	}
	
	
	public boolean maybeVisitPackage(DetailAST ast) {
		if (ast.getType() == TokenTypes.PACKAGE_DEF) {
//			packageName = ast.findFirstToken(TokenTypes.IDENT).getText();
//			System.out.println("found package:" + packageName);	
			visitPackage(ast);
			return true;
		}
		return false;
	}
	 public void visitType(DetailAST ast) {  
//	    	
//			maybeVisitPackage(ast);
	    	typeAST = ast;
	    	DetailAST aClassNameAST = ast.findFirstToken(TokenTypes.IDENT);
			String aShortName = aClassNameAST.getText();
			String aFullName = packageName + "." + aShortName;
			typeName = aFullName;
	 }
	public void visitPackage(DetailAST ast) {
		packageName = ast.findFirstToken(TokenTypes.IDENT).getText();
		System.out.println("found package:" + packageName);	
	}
	
    
}
