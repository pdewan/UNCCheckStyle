package unc.cs.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.ScopeUtils;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;
import com.puppycrawl.tools.checkstyle.checks.coding.MagicNumberCheck;
/**
 * 
 * Copying code from {@link MagicNumberCheck} as it is not extendible, too many private methods
 * Also want to use extendible log
 */
public class LiberalMagicNumberCheck extends ComprehensiveVisitCheck{
	List<String> allowedSiblings = new ArrayList();
	  /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "magic.number";

    /**
     * The token types that are allowed in the AST path from the
     * number literal to the enclosing constant definition.
     */
    private static final int[] ALLOWED_PATH_TOKENTYPES = {
        TokenTypes.ASSIGN,
        TokenTypes.ARRAY_INIT,
        TokenTypes.EXPR,
        TokenTypes.UNARY_PLUS,
        TokenTypes.UNARY_MINUS,
        TokenTypes.TYPECAST,
        TokenTypes.ELIST,
        TokenTypes.LITERAL_NEW,
        TokenTypes.METHOD_CALL,
        TokenTypes.STAR,
    };

    static {
        Arrays.sort(ALLOWED_PATH_TOKENTYPES);
    }

    /** the numbers to ignore in the check, sorted */
    private double[] ignoreNumbers = {-1, 0, 1, 2};
    /** Whether to ignore magic numbers in a hash code method. */
    private boolean ignoreHashCodeMethod;
    /** Whether to ignore magic numbers in annotation. */
    private boolean ignoreAnnotation;

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {
            TokenTypes.NUM_DOUBLE,
            TokenTypes.NUM_FLOAT,
            TokenTypes.NUM_INT,
            TokenTypes.NUM_LONG,
        };
    }

    @Override
    public int[] getAcceptableTokens()
    {
        return new int[] {
            TokenTypes.NUM_DOUBLE,
            TokenTypes.NUM_FLOAT,
            TokenTypes.NUM_INT,
            TokenTypes.NUM_LONG,
        };
    }
	
	
	public void setAllowedSiblings (String[] anAllowedSiblings) {
		allowedSiblings = Arrays.asList(anAllowedSiblings);
		
	}
	protected boolean isAllowed (DetailAST aSibling) {
		if (aSibling == null)
			return false;
		return ComprehensiveVisitCheck.matchesRegexes(
				FullIdent.createFullIdent(aSibling).getText(), allowedSiblings);
	}
	protected boolean isSiblingAllowed (DetailAST anAST) {
		return isAllowed(anAST.getPreviousSibling()) || isAllowed(anAST.getNextSibling());
	}
	 @Override
	    protected void doVisitToken(DetailAST ast)
	    {
//		 DetailAST aContainingConstantDef = findContainingConstantDef(ast);
		 if (findContainingConstantDef(ast) != null || isSiblingAllowed(ast))
			 return;
		 
//		 DetailAST aPreviousSibling = ast.getPreviousSibling();
//		 if (aPreviousSibling != null) {
//			 FullIdent aFullIdent = FullIdent.createFullIdent(aPreviousSibling);
//			 String aText = aFullIdent.getText();
//		 }
//		 DetailAST aLastExpressionAST = TagBasedCheck.findFirstContainingNode(ast, 
//				 Arrays.asList(new Integer[]{TokenTypes.EXPR}));
//		 if (aLastExpressionAST != null) {
//			 String anExpression = aLastExpressionAST.toStringTree();
//			 int i = 0;
//		 }				 
		 if (ignoreAnnotation && isInAnnotation(ast)) {
	            return;
	        }

	        if (inIgnoreList(ast)
	            || ignoreHashCodeMethod && isInHashCodeMethod(ast))
	        {
	            return;
	        }

	        final DetailAST constantDefAST = findContainingConstantDef(ast);

	        if (constantDefAST == null) {
	            reportMagicNumber(ast);
	        }
	        else {
	            DetailAST astNode = ast.getParent();
	            while (astNode != constantDefAST) {
	                final int type = astNode.getType();
	                if (Arrays.binarySearch(ALLOWED_PATH_TOKENTYPES, type) < 0) {
	                    reportMagicNumber(ast);
	                    break;
	                }

	                astNode = astNode.getParent();
	            }
	        }
	    }
	 // duplicating superclass private instance method,which should have been public and static
	   public static DetailAST findContainingConstantDef(DetailAST ast)
	    {
//	        DetailAST varDefAST = ast;
//	        while (varDefAST != null
//	                && varDefAST.getType() != TokenTypes.VARIABLE_DEF
//	                && varDefAST.getType() != TokenTypes.ENUM_CONSTANT_DEF)
//	        {
//	            varDefAST = varDefAST.getParent();
//	        }
		   DetailAST varDefAST = TagBasedCheck.findFirstContainingNode(ast,
	        		Arrays.asList(new Integer[] {TokenTypes.VARIABLE_DEF, TokenTypes.ENUM_CONSTANT_DEF}  ));

	        // no containing variable definition?
	        if (varDefAST == null) {
	            return null;
	        }   
	     

	        // implicit constant?
	        if (ScopeUtils.inInterfaceOrAnnotationBlock(varDefAST)
	            || varDefAST.getType() == TokenTypes.ENUM_CONSTANT_DEF)
	        {
	            return varDefAST;
	        }

	        // explicit constant
	        final DetailAST modifiersAST =
	                varDefAST.findFirstToken(TokenTypes.MODIFIERS);
	        if (modifiersAST.branchContains(TokenTypes.FINAL)) {
	            return varDefAST;
	        }

	        return null;
	    }
	   /**
	     * Reports aAST as a magic number, includes unary operators as needed.
	     * @param ast the AST node that contains the number to report
	     */
	    private void reportMagicNumber(DetailAST ast)
	    {
	        String text = ast.getText();
	        final DetailAST parent = ast.getParent();
	        DetailAST reportAST = ast;
	        if (parent.getType() == TokenTypes.UNARY_MINUS) {
	            reportAST = parent;
	            text = "-" + text;
	        }
	        else if (parent.getType() == TokenTypes.UNARY_PLUS) {
	            reportAST = parent;
	            text = "+" + text;
	        }
//	        log(reportAST.getLineNo(),
//	                reportAST.getColumnNo(),
//	                MSG_KEY,
//	                text);
	        DetailAST aTopMostExpression = getTopMostExpression(ast);
	        String aTopMostText = toStringList(aTopMostExpression).trim();
	        log (ast, text, aTopMostText);
	    }
	    
	    protected DetailAST getTopMostExpression(DetailAST anAST) {
	    	DetailAST aParent = anAST.getParent();
	    	if (aParent == null || !(aParent.getType() == TokenTypes.EXPR || aParent.getType() == TokenTypes.ELIST)) {
	    		return anAST;
	    	}
	    	return getTopMostExpression(aParent);
	    }

	    /**
	     * Determines whether or not the given AST is in a valid hash code method.
	     * A valid hash code method is considered to be a method of the signature
	     * {@code public int hashCode()}.
	     *
	     * @param ast the AST from which to search for an enclosing hash code
	     * method definition
	     *
	     * @return {@code true} if {@code ast} is in the scope of a valid hash
	     * code method
	     */
	    private boolean isInHashCodeMethod(DetailAST ast)
	    {
	        // if not in a code block, can't be in hashCode()
	        if (!ScopeUtils.inCodeBlock(ast)) {
	            return false;
	        }

	        // find the method definition AST
	        DetailAST methodDefAST = ast.getParent();
	        while (null != methodDefAST
	                && TokenTypes.METHOD_DEF != methodDefAST.getType())
	        {
	            methodDefAST = methodDefAST.getParent();
	        }

	        if (null == methodDefAST) {
	            return false;
	        }

	        // Check for 'hashCode' name.
	        final DetailAST identAST =
	            methodDefAST.findFirstToken(TokenTypes.IDENT);
	        if (!"hashCode".equals(identAST.getText())) {
	            return false;
	        }

	        // Check for no arguments.
	        final DetailAST paramAST =
	            methodDefAST.findFirstToken(TokenTypes.PARAMETERS);
	        if (0 != paramAST.getChildCount()) {
	            return false;
	        }

	        // we are in a 'public int hashCode()' method! The compiler will ensure
	        // the method returns an 'int' and is public.
	        return true;
	    }

	    /**
	     * Decides whether the number of an AST is in the ignore list of this
	     * check.
	     * @param ast the AST to check
	     * @return true if the number of ast is in the ignore list of this
	     * check.
	     */
	    private boolean inIgnoreList(DetailAST ast)
	    {
	        double value = CheckUtils.parseDouble(ast.getText(), ast.getType());
	        final DetailAST parent = ast.getParent();
	        if (parent.getType() == TokenTypes.UNARY_MINUS) {
	            value = -1 * value;
	        }
	        return Arrays.binarySearch(ignoreNumbers, value) >= 0;
	    }

	    /**
	     * Sets the numbers to ignore in the check.
	     * BeanUtils converts numeric token list to double array automatically.
	     * @param list list of numbers to ignore.
	     */
	    public void setIgnoreNumbers(double[] list)
	    {
	        if (list == null || list.length == 0) {
	            ignoreNumbers = new double[0];
	        }
	        else {
	            ignoreNumbers = new double[list.length];
	            System.arraycopy(list, 0, ignoreNumbers, 0, list.length);
	            Arrays.sort(ignoreNumbers);
	        }
	    }

	    /**
	     * Set whether to ignore hashCode methods.
	     * @param ignoreHashCodeMethod decide whether to ignore
	     * hash code methods
	     */
	    public void setIgnoreHashCodeMethod(boolean ignoreHashCodeMethod)
	    {
	        this.ignoreHashCodeMethod = ignoreHashCodeMethod;
	    }

	    /**
	     * Set whether to ignore Annotations.
	     * @param ignoreAnnotation decide whether to ignore annotations
	     */
	    public void setIgnoreAnnotation(boolean ignoreAnnotation)
	    {
	        this.ignoreAnnotation = ignoreAnnotation;
	    }

	    /**
	     * Determines if the column displays a token type of annotation or
	     * annotation member
	     *
	     * @param ast the AST from which to search for annotations
	     *
	     * @return {@code true} if the token type for this node is a annotation
	     */
	    private boolean isInAnnotation(DetailAST ast)
	    {
	        if (null == ast.getParent()
	                || null == ast.getParent().getParent())
	        {
	            return false;
	        }

	        return TokenTypes.ANNOTATION == ast.getParent().getParent().getType()
	                || TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR
	                        == ast.getParent().getParent().getType();
	    }

		@Override
		protected String msgKey() {
			// TODO Auto-generated method stub
			return MSG_KEY;
		}
}
