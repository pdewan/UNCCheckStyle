package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

public class BulkyThenCheck extends ComprehensiveVisitCheck{
	public static final String MSG_KEY = "expectedConstructs";

	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
	public int[] getDefaultTokens() {
	return new int[] {
			TokenTypes.LITERAL_IF
			};
	}
	  @Override
	    public void visitToken(DetailAST ast)
	    {
	        switch (ast.getType()) {
	            case TokenTypes.LITERAL_IF:
	                visitLiteralIf(ast);
	                break;
	            default:
	                throw new IllegalStateException(ast.toString());
	        }
	    }
	   private void visitLiteralIf(DetailAST literalIf)
	    {
		   if (!checkExcludeTagsOfCurrentType() ||
				   !CheckUtils.isElseIf(literalIf)   ) {
			   return ;
		   }
	        
	    }
	   


}
