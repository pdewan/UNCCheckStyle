package unc.cs.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.ScopeUtils;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.coding.MagicNumberCheck;

public class LiberalMagicNumberCheck extends MagicNumberCheck{
	List<String> allowedSiblings = new ArrayList();
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
	    public void visitToken(DetailAST ast)
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
		
		super.visitToken(ast);
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
}
