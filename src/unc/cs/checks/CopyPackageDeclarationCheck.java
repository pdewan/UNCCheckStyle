package unc.cs.checks;


import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Ensures there is a package declaration.
 * Rationale: Classes that live in the null package cannot be
 * imported. Many novice developers are not aware of this.
 *
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris</a>
 * @author Oliver Burn
 */
public final class CopyPackageDeclarationCheck extends Check {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "missing.package.declaration";

    /** is package defined. */
    private boolean defined;

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.PACKAGE_DEF};
    }

//    @Override
//    public int[] getRequiredTokens() {
//        return getDefaultTokens();
//    }
//
//    @Override
//    public int[] getAcceptableTokens() {
//        return new int[] {TokenTypes.PACKAGE_DEF};
//    }

    @Override
    public void beginTree(DetailAST ast) {
        defined = false;
    }

    @Override
    public void finishTree(DetailAST ast) {
        if (!defined) {
            log(ast.getLineNo(), MSG_KEY);
        }
    }

    @Override
    public void visitToken(DetailAST ast) {
        defined = true;
    }
}
