package unc.cs.checks;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
public  class IllegalMethodCallCheck extends MethodCallVisitedCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "illegalMethodCall";
    protected String[] disallowedMethods, disallowedClasses, allowedClasses, allowedMethods;
    
  
 // disallow all methods of this class except the allowed methods
    protected List<String> disallowedClassCollection = new ArrayList(); 
// if disallowed class set is missing, then it is *    
    protected List<String> allowedMethodCollection = new ArrayList();
 // all methods of these classes can be used, except the disallowed methods
    protected List<String> allowedClassCollection = new ArrayList();
    // if allowed clsas set is missing, then it is *
    protected List<String> disallowedMethodCollection = new ArrayList();
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
    
    public String[] getDisallowedMethods() {
    	return disallowedMethods;
    }
    
    public void setDisallowedMethods (String[] newValue) {
    	disallowedMethods = newValue;
//		String[] toArray = newValue.split(",");
		for (String s:disallowedMethods) {
			disallowedMethodCollection.add(s);
		}		
	}
    public void setAllowedMethods (String[] newValue) {
    	allowedMethods = newValue;
//		String[] toArray = newValue.split(",");
		for (String s:allowedMethods) {
			allowedMethodCollection.add(s);
		}		
	}
    public void setDisallowedClasses (String[] newValue) {
    	disallowedClasses = newValue;
//		String[] toArray = newValue.split(",");
		for (String s:disallowedClasses) {
			disallowedClassCollection.add(s);
		}		
	}
    public void setAllowedClasses (String[] newValue) {
    	allowedClasses = newValue;
//		String[] toArray = newValue.split(",");
		for (String s:allowedClasses) {
			allowedClassCollection.add(s);
		}		
	}
    
	protected Boolean checkAtomic(String aLongMethodName) {
		if (disallowedMethodCollection.size() > 1)
			return !disallowedMethodCollection.contains(aLongMethodName);
		else if (allowedMethodCollection.size() > 1)
			return allowedMethodCollection.contains(aLongMethodName);
		else
			return true; // succeeds if nothing specified
	}
//	protected boolean checkAtomicDisallowed(String aLongMethodName) {
//		return !allowedMethodSet.contains(aLongMethodName);
//	}
	/*
	 * need to allow calls from all local classes or ones with certain tags
	 */
	protected Boolean checkAllowedClassDisallowedMethods(String[] aCallParts, String aLongMethodName) {		
		String aCalledClass = aCallParts[0];
		if (!allowedClassCollection.contains(aCalledClass))
			return false;
		return !disallowedMethodCollection.contains(aLongMethodName);
	}
	protected Boolean checkDisallowedClassAllowedMethods(String[] aCallParts, String aLongMethodName) {		
		String aCalledClass = aCallParts[0];
		if (!disallowedClassCollection.contains(aCalledClass))
			return true;
		return allowedMethodCollection.contains(aLongMethodName);
	}


	@Override
	protected Boolean check(DetailAST ast, String aShortMethodName, String aLongMethodName, String[] aCallParts) {
		if (aCallParts.length > 2) // cannot dissect it into a class
		     return checkAtomic(aLongMethodName);
		// give precedence to disallowed
		else if (disallowedClassCollection.size() > 0) {
			return checkDisallowedClassAllowedMethods(aCallParts, aLongMethodName);			
		} else if (allowedClassCollection.size() > 0) {
			return checkAllowedClassDisallowedMethods(aCallParts, aLongMethodName);
		} else { // no inheritance, just check
			return checkAtomic(aLongMethodName);
		}
	}

	
	
}
