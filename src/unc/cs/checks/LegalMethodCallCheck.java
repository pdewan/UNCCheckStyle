package unc.cs.checks;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import unc.cs.symbolTable.STType;

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
public  class LegalMethodCallCheck extends MethodCallVisitedCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "legalMethodCall";
    protected String[] expectedMethods;
    protected Set<String> expectedMethodsSet = new HashSet();
//   
//    @Override
//    public int[] getDefaultTokens() {
//        return new int[] {TokenTypes.METHOD_CALL};
//    }

//    @Override
//    public int[] getRequiredTokens() {
//        return getDefaultTokens();
//    }
//
//    @Override
//    public int[] getAcceptableTokens() {
//        return new int[] {TokenTypes.PACKAGE_DEF};
//    }

//    @Override
//    public void beginTree(DetailAST ast) {    	
//        defined = false;
//    }

//    @Override
//    public void finishTree(DetailAST ast) {
////        if (!defined) {
//////            log(ast.getLineNo(), MSG_KEY);
////        }
//    }
    
//    public static String getMethodName(DetailAST ast) {
//    	DetailAST methodComponent = ast.getFirstChild();
//    	while (methodComponent.getType() == TokenTypes.DOT)
//    		methodComponent = methodComponent.getLastChild();
//    	
//    	return methodComponent.getText();
//    	
//    }
//    public static DetailAST getLastDescendent(DetailAST ast) {
//    	DetailAST result = ast.getFirstChild();
//    	while (result.getChildCount() > 0)
//    		result = result.getLastChild();    	
//    	return result;    	
//    }
//
//    @Override
//    public void visitToken(DetailAST ast) {
//    	if (ast.getType() != TokenTypes.METHOD_CALL)
//    		return;
//        System.out.println("Method text:" + getLastDescendent(ast).getText());
//        log(ast.getLineNo(), msgKey(), getLastDescendent(ast).getText());
//
//    }
    @Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
    
    public String[] getExpectedMethods() {
    	return expectedMethods;
    }
    
    public void setExpectedMethods (String[] newValue) {
    	expectedMethods = newValue;
//		String[] toArray = newValue.split(",");
		for (String s:expectedMethods) {
			expectedMethodsSet.add(s);
		}		
	}
    // "fail" if method is in expected set
	@Override
	protected boolean check(DetailAST ast, String aShortMethodName, String aLongMethodName, String[] aCallParts) {
		return !expectedMethodsSet.contains(aShortMethodName);
	}

	
	
}
