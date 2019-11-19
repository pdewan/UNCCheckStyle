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
import com.puppycrawl.tools.checkstyle.api.ScopeUtils;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.naming.AbstractAccessControlNameCheck;
import com.puppycrawl.tools.checkstyle.checks.naming.ConstantNameCheck;

/**
 * <p>
 * Checks that constant names conform to a format specified
 * by the format property.
 * A <em>constant</em> is a <strong>static</strong> and <strong>final</strong>
 * field or an interface/annotation field, except
 * <strong>serialVersionUID</strong> and <strong>serialPersistentFields
 * </strong>.  The format is a regular expression
 * and defaults to <strong>^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$</strong>.
 * </p>
 * <p>
 * An example of how to configure the check is:
 * </p>
 * <pre>
 * &lt;module name="ConstantName"/&gt;
 * </pre>
 *
 * <p>
 * An example of how to configure the check for names that are only upper case
 * letters and digits is:
 * </p>
 * <pre>
 * &lt;module name="ConstantName"&gt;
 *    &lt;property name="format" value="^[A-Z][A-Z0-9]*$"/&gt;
 * &lt;/module&gt;
 * </pre>
 *
 *
 * @author Rick Giles
 */
public class ConstantDefinedCheck
    extends ConstantNameCheck {
//    /** Creates a new <code>ConstantNameCheck</code> instance. */
//    public ConstantDefinedCheck() {
//        super("^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$");
//    }
	public static final String MSG_KEY = "constantDefined";

//    @Override
//    public int[] getDefaultTokens() {
//        return new int[] {TokenTypes.VARIABLE_DEF};
//    }
//
//    @Override
//    public int[] getAcceptableTokens() {
//        return new int[] {TokenTypes.VARIABLE_DEF};
//    }
    @Override
    public void visitToken(DetailAST ast) {
        if (mustCheckName(ast)) {
            final DetailAST nameAST = ast.findFirstToken(TokenTypes.IDENT);
//            if (!getRegexp().matcher(nameAST.getText()).find()) {
                log(nameAST.getLineNo(),
                    nameAST.getColumnNo(),
                    MSG_KEY,
                    nameAST.getText());
//            }
        }
    }

//    @Override
//    protected final boolean mustCheckName(DetailAST ast) {
//        boolean retVal = false;
//
//        final DetailAST modifiersAST =
//            ast.findFirstToken(TokenTypes.MODIFIERS);
//        final boolean isStatic = modifiersAST != null
//            && modifiersAST.branchContains(TokenTypes.LITERAL_STATIC);
//        final boolean isFinal = modifiersAST != null
//            && modifiersAST.branchContains(TokenTypes.FINAL);
//
//        if (isStatic  && isFinal && shouldCheckInScope(modifiersAST)
//                || ScopeUtils.inAnnotationBlock(ast)
//                || ScopeUtils.inInterfaceOrAnnotationBlock(ast)
//                        && !ScopeUtils.inCodeBlock(ast)) {
//            // Handle the serialVersionUID and serialPersistentFields constants
//            // which are used for Serialization. Cannot enforce rules on it. :-)
//            final DetailAST nameAST = ast.findFirstToken(TokenTypes.IDENT);
//            if (nameAST != null
//                && !"serialVersionUID".equals(nameAST.getText())
//                && !"serialPersistentFields".equals(nameAST.getText())) {
//                retVal = true;
//            }
//        }
//
//        return retVal;
//    }
}
