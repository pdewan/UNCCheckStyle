package unc.cs.checks;


import java.util.Set;

import unc.cs.symbolTable.STType;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public abstract  class MethodCallVisitedCheck extends ComprehensiveVisitCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "methodCallVisited";

    protected String shortMethodName;
    protected String longMethodName;

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.METHOD_CALL};
    }
//	protected  boolean typeCheck(STType anSTClass) {
//		return true; // we probably will not flag the type
//	}
	
	

    public static DetailAST getLastDescendent(DetailAST ast) {
    	DetailAST result = ast.getFirstChild();
    	while (result.getChildCount() > 0)
    		result = result.getLastChild();    	
    	return result;    	
    }
    protected void log(DetailAST ast, String aMethodName) {
        log(ast.getLineNo(), msgKey(), aMethodName);

    }
    
    protected abstract boolean check(DetailAST ast, String aShortMethodName, String aLongMethodName) ;
    
    public void visitCall(DetailAST ast) {
//    	if (ast.getType() != TokenTypes.METHOD_CALL)
//    		return;
    	shortMethodName = getLastDescendent(ast).getText();
    	FullIdent aFullIdent = FullIdent.createFullIdentBelow(ast);
    	longMethodName = aFullIdent.getText();
//        System.out.println("Method text:" + getLastDescendent(ast).getText());
    	if (!check(ast, shortMethodName, longMethodName))
           log(ast, shortMethodName);
//        log(ast.getLineNo(), msgKey(), getLastDescendent(ast).getText());

    }
    public void visitToken(DetailAST ast) {	    		
    		if (ast.getType() == TokenTypes.METHOD_CALL)
			visitCall(ast);
    		else 
    			super.visitToken(ast);
	}
    @Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
}
