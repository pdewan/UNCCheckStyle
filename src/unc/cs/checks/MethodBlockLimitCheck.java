////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2015 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////
package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;
import com.puppycrawl.tools.checkstyle.checks.coding.AbstractNestedDepthCheck;

/**
 * Restricts nested if-else blocks to a specified depth (default = 1).
 *
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris</a>
 */
public  class MethodBlockLimitCheck extends ComprehensiveVisitCheck {

	 /** maximum allowed nesting depth */
    protected int max;
    /** current nesting depth */
    protected int blocksInCurrentMethod;

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
//    public static final String MSG_KEY = "nestedLoopDepth";
   

    public static final String MSG_KEY = "methodBlockLimit";



    /** default allowed nesting depth. */
    private static final int DEFAULT_MAX = 1;

    /** Creates new check instance with default allowed nesting depth. */
    public MethodBlockLimitCheck()
    {
    	 setMax(max);
//        super(DEFAULT_MAX);
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {
//        		TokenTypes.LITERAL_IF,
        		TokenTypes.METHOD_DEF,
        		TokenTypes.CTOR_DEF,
        		TokenTypes.LITERAL_WHILE,
        		TokenTypes.LITERAL_FOR,
//        		TokenTypes.LITERAL_SWITCH,
        		};
    }

//    @Override
//    public int[] getAcceptableTokens()
//    {
//        return new int[] {TokenTypes.LCURLY,
//        		TokenTypes.RCURLY};
//    }
    @Override
    protected void visitMethod(DetailAST anAST) {
    	blocksInCurrentMethod = 0;
    	super.visitMethod(anAST);
    }
    @Override
    protected void visitConstructor(DetailAST anAST) {
    	blocksInCurrentMethod = 0;
    	super.visitConstructor(anAST);
    }

//    @Override
//    protected void doLeaveToken(DetailAST ast)
//    {
//        switch (ast.getType()) {
//        	case TokenTypes.METHOD_CALL:
//        	
//        	case TokenTypes.LITERAL_IF:
//        			nestOut();
//        			break;
//        	 case TokenTypes.LITERAL_FOR:
//                 nestOut();
//                 break;
//            case TokenTypes.LITERAL_WHILE:
//                nestOut();
//                break;
//            case TokenTypes.LITERAL_SWITCH:
//                nestOut();
//                break;
//            default:
//                throw new IllegalStateException(ast.toString());
//        }
//    }
    @Override
    protected void doVisitToken(DetailAST ast)
    {
    	
        switch (ast.getType()) {
        	case TokenTypes.METHOD_DEF:
        		visitMethod(ast);
        		break;
        	case TokenTypes.CTOR_DEF:
        		visitConstructor(ast);
        		break;
        	 case TokenTypes.LITERAL_FOR:
                 blockVisited(ast, MSG_KEY);
                 break;
            case TokenTypes.LITERAL_WHILE:
                blockVisited(ast, MSG_KEY);
                break;
            
            default:
            	super.doVisitToken(ast);
//                throw new IllegalStateException(ast.toString());
        }
    }
    @Override
    public void doBeginTree(DetailAST rootAST)
    {
        blocksInCurrentMethod = 0;
        super.doBeginTree(rootAST);
    }

    /**
     * Getter for maximum allowed nesting depth.
     * @return maximum allowed nesting depth.
     */
    public final int getMax()
    {
        return max;
    }

    /**
     * Setter for maximum allowed nesting depth.
     * @param max maximum allowed nesting depth.
     */
    public final void setMax(int max)
    {
        this.max = max;
    }


    protected final void blockVisited(DetailAST ast, String messageId)
    {
    	if (!inMethodOrConstructor)
    		return;
        blocksInCurrentMethod++;

        if (blocksInCurrentMethod > max) {
//            log(ast, messageId, depth, max);
            log(messageId, ast, currentTree, getEnclosingShortTypeName(ast), currentMethodAST, blocksInCurrentMethod, max);
        }
    }

   
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
   
}
