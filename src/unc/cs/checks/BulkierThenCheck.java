package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

public class BulkierThenCheck extends ComprehensiveVisitCheck{
	public static final String MSG_KEY = "bulkierThen";
	protected int maxElsePartSize = 4;
	protected int minThenElseRatio = 3;

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
	public void setMaxElsePartSize (int anElsePartSize) {
		maxElsePartSize =  anElsePartSize;
	}
	public void setMinThenElseRatio (int aRatio) {
		minThenElseRatio =  aRatio;
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
	public static final String STATEMENT_SEPARATOR = ";|for|if|while|switch";
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
		   double numStatementsInThenPart = aThenString.split(STATEMENT_SEPARATOR).length - 1; // no semiucolon means length of 1
		   double numStatementsInElsePart = anElseString.split(STATEMENT_SEPARATOR).length - 1;
		   
//		   double aThenElseRatio = ((double) numStatementsInThenPart)/numStatementsInElsePart;
		   if (numStatementsInElsePart <= maxElsePartSize &&
				   (numStatementsInElsePart == 0) ||
				   numStatementsInThenPart/numStatementsInElsePart >= minThenElseRatio) {
			   logBulkierThen(aThenPart);
		   }
			   
		   
		   
	        
	    }
	   
		protected void logBulkierThen( DetailAST aSpecificAST) {
			log (aSpecificAST);

		}

}
