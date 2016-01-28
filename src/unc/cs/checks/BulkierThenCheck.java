package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

public class BulkierThenCheck extends ComprehensiveVisitCheck{
	public static final String MSG_KEY = "bulkierThen";
	protected int elsePartSize = 4;
	protected int thenElseRatio = 3;

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
	public void setElsePartSize (int anElsePartSize) {
		elsePartSize =  anElsePartSize;
	}
	public void setThenElseRation (int aRatio) {
		thenElseRatio =  aRatio;
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
		   DetailAST anElsePart = getElsePart(anIfAST);
		   if (anElsePart == null)
			   return;
		   DetailAST aThenPart = getThenPart(anIfAST);
		   String aThenString = aThenPart.toStringTree();
		   String anElseString = anElsePart.toStringTree();
		   int numStatementsInThenPart = aThenString.split(";").length;
		   int numStatementsInElsePart = anElseString.split(";"	).length;
		   double aThenElseRatio = ((double) aThenString.length())/anElseString.length();
		   if (numStatementsInElsePart <= elsePartSize && aThenElseRatio >= thenElseRatio) {
			   logBulkierThen(aThenPart);
		   }
			   
		   
		   
	        
	    }
	   
		protected void logBulkierThen( DetailAST aSpecificAST) {
			log (aSpecificAST);

		}

}
