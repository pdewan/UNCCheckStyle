package unc.cs.checks;

import java.util.Arrays;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.ScopeUtils;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.coding.MagicNumberCheck;

public class LiberalMagicNumberCheck extends MagicNumberCheck{
	 @Override
	    public void visitToken(DetailAST ast)
	    {
		 DetailAST aContainingConstantDef = findContainingConstantDef(ast);
		 
		 DetailAST aPreviousSibling = ast.getPreviousSibling();
		 if (aPreviousSibling != null) {
			 FullIdent aFullIdent = FullIdent.createFullIdent(aPreviousSibling);
			 String aText = aFullIdent.getText();
		 }
		 DetailAST aLastExpressionAST = TagBasedCheck.findFirstContainingNode(ast, 
				 Arrays.asList(new Integer[]{TokenTypes.EXPR}));
		 if (aLastExpressionAST != null) {
			 String anExpression = aLastExpressionAST.toStringTree();
			 int i = 0;
		 }
				 
		 if (aContainingConstantDef == null)
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
