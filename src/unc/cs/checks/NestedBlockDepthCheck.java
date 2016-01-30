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
public  class NestedBlockDepthCheck extends ComprehensiveVisitCheck {

	 /** maximum allowed nesting depth */
    protected int max;
    /** current nesting depth */
    protected int depth;

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
//    public static final String MSG_KEY = "nestedLoopDepth";
    public static final String IF_MSG_KEY = "nestedIfDepth";
    public static final String WHILE_MSG_KEY = "nestedWhileDepth";
    public static final String FOR_MSG_KEY = "nestedForDepth";
    public static final String SWITCH_MSG_KEY = "nestedSwitchDepth";

    public static final String MSG_KEY = "nestedBlockDepth";



    /** default allowed nesting depth. */
    private static final int DEFAULT_MAX = 1;

    /** Creates new check instance with default allowed nesting depth. */
    public NestedBlockDepthCheck()
    {
    	 setMax(max);
//        super(DEFAULT_MAX);
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {
//        		TokenTypes.LITERAL_IF,
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
    protected void doLeaveToken(DetailAST ast)
    {
        switch (ast.getType()) {
        	case TokenTypes.LITERAL_IF:
        			nestOut();
        			break;
        	 case TokenTypes.LITERAL_FOR:
                 nestOut();
                 break;
            case TokenTypes.LITERAL_WHILE:
                nestOut();
                break;
            case TokenTypes.LITERAL_SWITCH:
                nestOut();
                break;
            default:
                throw new IllegalStateException(ast.toString());
        }
    }
    @Override
    protected void doVisitToken(DetailAST ast)
    {
        switch (ast.getType()) {
        	case TokenTypes.LITERAL_IF:
        			nestIn(ast, IF_MSG_KEY);
        			break;
        	 case TokenTypes.LITERAL_FOR:
                 nestIn(ast, MSG_KEY);
                 break;
            case TokenTypes.LITERAL_WHILE:
                nestIn(ast, MSG_KEY);
                break;
            case TokenTypes.LITERAL_SWITCH:
                nestIn(ast, SWITCH_MSG_KEY);
                break;
            default:
                throw new IllegalStateException(ast.toString());
        }
    }
    @Override
    public void doBeginTree(DetailAST rootAST)
    {
        depth = 0;
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

    /**
     * Increasing current nesting depth.
     * @param ast note which increases nesting.
     * @param messageId message id for logging error.
     */
    protected final void nestIn(DetailAST ast, String messageId)
    {
        depth++;

        if (depth > max) {
//            log(ast, messageId, depth, max);
            log(messageId, ast, currentTree, getEnclosingShortTypeName(ast), depth, max);
        }
    }

    /** Decreasing current nesting depth */
    protected final void nestOut()
    {
        depth--;
    }

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	

   
}
