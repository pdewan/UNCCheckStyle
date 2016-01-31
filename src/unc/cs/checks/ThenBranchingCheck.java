package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

public class ThenBranchingCheck extends ComprehensiveVisitCheck{
	public static final String MSG_KEY = "thenBranching";


	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
	public int[] getDefaultTokens() {
	return new int[] {
			TokenTypes.METHOD_DEF, // to get current method tags
			TokenTypes.CTOR_DEF,  // ditto
			TokenTypes.LITERAL_IF
			};
	}

//	  @Override
//	    public void visitToken(DetailAST ast)
//	    {
//	        switch (ast.getType()) {
//	            case TokenTypes.LITERAL_IF:
//	                visitLiteralIf(ast);
//	                break;
//	            default:
//	                throw new IllegalStateException(ast.toString());
//	        }
//	    }
	   protected void visitLiteralIf(DetailAST anIfAST)
	    {
		   if (!checkExcludeTagsOfCurrentType() ||
				   !checkIncludeExcludeTagsOfCurrentMethod()) {
			   return ;
		   }

		   
		   
		   
//		   String aType = getEnclosingShortClassName(currentTree);
		 
		   DetailAST aThenPart = getThenPart(anIfAST);
		   // se if there is an if there
		   DetailAST aThenIf = findFirstInOrderMatchingNode(aThenPart,TokenTypes.LITERAL_IF);
		   if (aThenIf == null)
			   return;
		   DetailAST anElsePart = getElsePart(anIfAST);

		   if (anElsePart == null) { // then part has branch, no else
			   logThenBranching (anIfAST);
			   return;
		   }
		   DetailAST anElseIf = findFirstInOrderMatchingNode(anElsePart,TokenTypes.LITERAL_IF);
		   if (anElseIf == null) {// there is no branching in else part, so move
			   logThenBranching(anIfAST);
		   }
		   
	    }
	   
		protected void logThenBranching( DetailAST aSpecificAST) {
			log (aSpecificAST, currentMethodName);

		}

}
