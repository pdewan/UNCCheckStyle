package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class StaticVariableCheck extends ComprehensiveVisitCheck {
	
	public static final String MSG_KEY = "staticVariable";	

//    private int max = 30;

    public StaticVariableCheck() {
    	
    }
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.CLASS_DEF, TokenTypes.PACKAGE_DEF,
        		TokenTypes.VARIABLE_DEF };
    }

    

    @Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
    @Override
	public void visitVariableDef(DetailAST ast) {
		if (!checkIncludeExcludeTagsOfCurrentType())
		return;
		if (isStaticAndNotFinal(ast)) {
			 log(ast.getLineNo(), ast.getColumnNo(),					 
					 msgKey(), getName(ast));
		}
		
	}

    
    
//	@Override
//	public void doVisitToken(DetailAST ast) {
////			System.out.println("Check called:" + MSG_KEY);
//			switch (ast.getType()) {
//			
//			case TokenTypes.CLASS_DEF:
//				visitType(ast);
//				return;
//			
//			
//			case TokenTypes.VARIABLE_DEF:
//				visitVariableDef(ast);
//				return;
//			
//			default:
//				System.err.println("Unexpected token");
//			}
//			
//		}
	
}
