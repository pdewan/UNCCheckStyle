package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class MethodLimitCheck extends UNCCheck {
	
	public static final String MSG_KEY = "methodLimit";	

//    private int max = 30;
    private int max = 1;

    public MethodLimitCheck() {
    	
    }
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF };
    }

    public void setMax(int limit) {
        max = limit;
    }

    public void visitToken(DetailAST ast) {
    	System.out.println("Check called:" + msgKey());
        // find the OBJBLOCK node below the CLASS_DEF/INTERFACE_DEF
        DetailAST objBlock = ast.findFirstToken(TokenTypes.OBJBLOCK);
        // count the number of direct children of the OBJBLOCK
        // that are METHOD_DEFS
        int methodDefs = objBlock.getChildCount(TokenTypes.METHOD_DEF);
        // report error if limit is reached
        if (methodDefs > max) {
            log(ast.getLineNo(), msgKey(), max);
        }
    }
    @Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
}
