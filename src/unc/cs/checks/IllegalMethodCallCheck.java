package unc.cs.checks;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.STType;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;


public  class IllegalMethodCallCheck extends MethodCallVisitedCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "illegalMethodCall";
    protected String[] exceptionCalls, disallowedCalls, allowedCalls;     
  
 // disallow all methods of this class except the allowed methods
    protected List<String> disallowedCallsCollection = new ArrayList(); 
// if disallowed class set is missing, then it is *    
    protected List<String> exceptionCallsCollection = new ArrayList();
 // all methods of these classes can be used, except the disallowed methods
    protected List<String> allowedCallsCollection = new ArrayList();
    // if allowed clsas set is missing, then it is *
//    protected List<String> disallowedMethodCollection = new ArrayList();
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
    
    public String[] getExceptionCalls() {
    	return exceptionCalls;
    }
    
    public void setExceptionCalls (String[] newValue) {
    	exceptionCalls = newValue;
//		String[] toArray = newValue.split(",");
		for (String s:exceptionCalls) {
			exceptionCallsCollection.add(s);
		}		
	}
//    public void setAllowedMethods (String[] newValue) {
//    	allowedMethods = newValue;
////		String[] toArray = newValue.split(",");
//		for (String s:allowedMethods) {
//			exceptionCallsCollection.add(s);
//		}		
//	}
    public void setDisallowedCalls (String[] newValue) {
    	disallowedCalls = newValue;
//		String[] toArray = newValue.split(",");
//		for (String s:disallowedCalls) {
//			disallowedCallsCollection.add(s);
//		}	
    	disallowedCallsCollection = Arrays.asList(disallowedCalls);
	}
    public void setAllowedCalls (String[] newValue) {
    	allowedCalls = newValue;
//		String[] toArray = newValue.split(",");
//		for (String s:allowedCalls) {
//			allowedCallsCollection.add(s);
//		}	
    	allowedCallsCollection = Arrays.asList(allowedCalls);
	}
    
    
//	protected Boolean checkAtomic(DetailAST aCalledMethodAST, String aLongMethodName) {
//		if (disallowedMethodCollection.size() > 1)
////			return !disallowedMethodCollection.contains(aLongMethodName);
//		return !methodCallContainedInSpecifications(aLongMethodName, disallowedMethodCollection, aCalledMethodAST);
//
//		else if (allowedMethodCollection.size() > 1)
//			return allowedMethodCollection.contains(aLongMethodName);
//		else
//			return true; // succeeds if nothing specified
//	}
//	protected boolean checkAtomicDisallowed(String aLongMethodName) {
//		return !allowedMethodSet.contains(aLongMethodName);
//	}
	/*
	 * need to allow calls from all local classes or ones with certain tags
	 */
	protected Boolean checkAllowedClassDisallowedMethods(DetailAST aCalledMethod, String[] aCallParts, String aLongMethodName) {		
//		String aCalledClass = aCallParts[0];
//		if (!allowedClassCollection.contains(aCalledClass))
//			return false;
//		return !disallowedMethodCollection.contains(aLongMethodName);
		String aCalledClass = aCallParts[0];
		Boolean aTypesMatch = methodCallContainedInSpecifications(aCallParts, allowedCallsCollection, aCalledMethod);
		if (aTypesMatch == null)
			return null;
		if (!aTypesMatch)
			return false;
		Boolean aMethodsMatch =  methodCallContainedInSpecifications(aCallParts, exceptionCallsCollection, aCalledMethod);
		if (aMethodsMatch == null)
			return null;
		return !aMethodsMatch;
	}
	protected Boolean checkDisallowedClassAllowedMethods(DetailAST aCalledMethod, String[] aCallParts, String aLongMethodName) {		
		String aCalledClass = aCallParts[0];
		Boolean aTypesMatch = methodCallContainedInSpecifications(aCallParts, disallowedCallsCollection, aCalledMethod);
		if (aTypesMatch == null)
			return null;
		if (!aTypesMatch)
			return true;
		Boolean aMethodsMatch =  methodCallContainedInSpecifications(aCallParts, exceptionCallsCollection, aCalledMethod);
		if (aMethodsMatch == null)
			return null;
		return aMethodsMatch;
//		
//		if (!disallowedClassCollection.contains(aCalledClass))
//			return true;
//		return allowedMethodCollection.contains(aLongMethodName);
	}


	@Override
	protected Boolean check(DetailAST aCalledMethodAST, String aShortMethodName, String aLongMethodName, CallInfo aCallInfo) {
		// why do this check
//		if (!checkTagsOfCurrentType())
//			return true;
		String[] aCallParts = aCallInfo.getNotmalizedCall();
		String[] aNormalizedCallParts = aCallParts;
		if (aCallParts.length > 2) { // cannot dissect it into a class 
//		     return checkAtomic(aCalledMethodAST, aLongMethodName);
			aNormalizedCallParts = new String[1];
			aNormalizedCallParts[0] = aLongMethodName;
		}
		
		// give precedence to disallowed
		if (disallowedCallsCollection.size() > 0) {
			return checkDisallowedClassAllowedMethods(aCalledMethodAST, aNormalizedCallParts, aLongMethodName);			
		} else {
			//if (allowedClassCollection.size() > 0) {
			return checkAllowedClassDisallowedMethods(aCalledMethodAST, aNormalizedCallParts, aLongMethodName);
		} 
//		else { // no inheritance, just check
//			//return checkAtomic(aCalledMethodAST, aLongMethodName);
//			
//		}
	}

	
	
}
