package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class MinDeclaredMethodsCheck extends ComprehensiveVisitCheck {
	
	public static final String MSG_KEY = "minDeclaredMethods";	

//    private int max = 30;
    private int min = 2;

    public MinDeclaredMethodsCheck() {
    	
    }
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF };
    }

    public void setMinDeclaredMethods(int newVal) {
        min = newVal;
    }

    public void doVisitToken(DetailAST ast) {
//    	System.out.println("Check called:" + msgKey());
        // find the OBJBLOCK node below the CLASS_DEF/INTERFACE_DEF
        DetailAST objBlock = ast.findFirstToken(TokenTypes.OBJBLOCK);
        // count the number of direct children of the OBJBLOCK
        // that are METHOD_DEFS
        int methodDefs = objBlock.getChildCount(TokenTypes.METHOD_DEF);
        // report error if limit is reached
        if (methodDefs < min) {
//            log(ast.getLineNo(), msgKey(), min);
            log(ast, currentTree, methodDefs, min);
        }
    }
    @Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
}
