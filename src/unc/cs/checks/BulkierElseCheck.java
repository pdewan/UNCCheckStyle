package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

public class BulkierElseCheck extends BulkierThenCheck{
	public static final String MSG_KEY = "bulkierElse";
	protected double maxThenPartSize = 4;
	protected double minElseThenRatio = 3;

	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
//	public int[] getDefaultTokens() {
//	return new int[] {
//			TokenTypes.METHOD_DEF, // to get current method tags
//			TokenTypes.CTOR_DEF,  // ditto
//			TokenTypes.LITERAL_IF
//			};
//	}
	public void setMaxThenPartSize (double newVal) {
		maxThenPartSize =  newVal;
	}
	public void setMinElseThenRatio (double aRatio) {
		minElseThenRatio =  aRatio;
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
//	public static final String STATEMENT_SEPARATOR = ";|for|if|while|switch";
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
		   if (numStatementsInElsePart == 0) {
			   return;
		   }
		   if (numStatementsInThenPart == 0) {
			   return;
		   }

//		   double aThenElseRatio = ((double) numStatementsInThenPart)/numStatementsInElsePart;
		   if (
//				   (numStatementsInElsePart == 0) ||
				   numStatementsInElsePart/numStatementsInThenPart >= minElseThenRatio) {
//			   logBulkierThen(aThenPart);
//				log(aThenPart, "Then#:" + numStatementsInThenPart + " Else#: " +numStatementsInElsePart );
				log(anElsePart, "" + numStatementsInElsePart,  "" +numStatementsInThenPart, "" + numStatementsInThenPart/numStatementsInElsePart );

		   }
			   
		   
		   
	        
	    }
	   
		protected void logBulkierThen( DetailAST aSpecificAST) {
			log (aSpecificAST);
		

		}

}
