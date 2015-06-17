package unc.cs.checks;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.STType;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public abstract  class MethodCallVisitedCheck extends ComprehensiveVisitCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "methodCallVisited";

//    protected String shortMethodName;
//    protected String longMethodName;

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
//        		TokenTypes.PACKAGE_DEF, 
				TokenTypes.CLASS_DEF,  
//				TokenTypes.INTERFACE_DEF, 
//				TokenTypes.TYPE_ARGUMENTS,
//				TokenTypes.TYPE_PARAMETERS,
				TokenTypes.VARIABLE_DEF,
				TokenTypes.PARAMETER_DEF,
				TokenTypes.METHOD_DEF, 
				TokenTypes.CTOR_DEF,
//				TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT,
//				TokenTypes.PARAMETER_DEF,
//				TokenTypes.LCURLY,
//				TokenTypes.RCURLY,
				TokenTypes.METHOD_CALL
        };
        		
        		
    }
//	protected  boolean typeCheck(STType anSTClass) {
//		return true; // we probably will not flag the type
//	}
	
	

    public static DetailAST getLastDescendent(DetailAST ast) {
    	DetailAST result = ast.getFirstChild();
    	while (result.getChildCount() > 0)
    		result = result.getLastChild();    	
    	return result;    	
    }
    protected void log(DetailAST ast, String aMethodName) {
        log(ast.getLineNo(), msgKey(), aMethodName);

    }
    
    
    protected abstract Boolean check(DetailAST ast, String aShortMethodName, String aLongMethodName, String[] aCallParts) ;
    
    public String[] toNormalizedClassBasedCall (String[] aCallParts) {
    	List<String> aCallPartsList = new ArrayList();
    	if (aCallParts.length == 1 || "this".equals(aCallParts[0])) { // put the name of the class in which the call occurs
    		aCallPartsList.add(fullTypeName);
    		aCallPartsList.add(aCallParts[aCallParts.length - 1]);
    	} else if (aCallParts.length == 2) {
    		String aType = lookupType(aCallParts[0]);
    		if (aType != null) { // not a static method
    			aCallPartsList.add(aType);
    			aCallPartsList.add(aCallParts[1]);
    		} else {
    			return aCallParts; // static call
    		}
    	} else {
    		return aCallParts; // System.out.println() probabluy
    	}
    	return aCallPartsList.toArray(new String[0]);
    }
    public String toLongName(String[] aNormalizedName) {
    	StringBuffer retVal = new StringBuffer();
    	int index = 0;
    	while (true) {
    		if (index >= aNormalizedName.length) {
    			return retVal.toString();
    		}
    		if (index > 0)
    			retVal.append(".");
    		retVal.append(aNormalizedName[index]);
    		index ++;
    	}
    }
    public void visitCall(DetailAST ast) {
    	maybeAddToPendingTypeChecks(ast);
////    	if (ast.getType() != TokenTypes.METHOD_CALL)
////    		return;
//    	String shortMethodName = getLastDescendent(ast).getText();
//    	FullIdent aFullIdent = FullIdent.createFullIdentBelow(ast);
//    	String longMethodName = aFullIdent.getText();
//    	String[] aCallParts = longMethodName.split("\\.");
//    	String[] aNormalizedParts = toNormalizedClassBasedCall(aCallParts);
//    	String aNormalizedLongName = toLongName(aNormalizedParts);
//    	
////        System.out.println("Method text:" + getLastDescendent(ast).getText());
//    	Boolean checkResult = check(ast, shortMethodName, aNormalizedLongName, aNormalizedParts);
//    	if (checkResult == null) {
//    		pendingChecks().add(ast);
//    		return;
//    	}
////    	if (!check(ast, shortMethodName, aNormalizedLongName, aNormalizedParts))
//        if (!checkResult) {
//           log(ast, shortMethodName);
////        log(ast.getLineNo(), msgKey(), getLastDescendent(ast).getText());
//        }

    }
    @Override
    public Boolean doPendingCheck(DetailAST ast, DetailAST aTreeAST) {
//    	if (ast.getType() != TokenTypes.METHOD_CALL)
//    		return;
    	String shortMethodName = getLastDescendent(ast).getText();
    	FullIdent aFullIdent = FullIdent.createFullIdentBelow(ast);
    	String longMethodName = aFullIdent.getText();
    	String[] aCallParts = longMethodName.split("\\.");
    	String[] aNormalizedParts = toNormalizedClassBasedCall(aCallParts);
    	String aNormalizedLongName = toLongName(aNormalizedParts);
    	
//        System.out.println("Method text:" + getLastDescendent(ast).getText());
    	Boolean checkResult = check(ast, shortMethodName, aNormalizedLongName, aNormalizedParts);
    	if (checkResult == null) {
//    		pendingChecks().add(ast);
    		return null;
    	}
//    	if (!check(ast, shortMethodName, aNormalizedLongName, aNormalizedParts))
        if (!checkResult) {
           log(ast, shortMethodName);
//        log(ast.getLineNo(), msgKey(), getLastDescendent(ast).getText());
        }
        return checkResult;

    }
    public void visitToken(DetailAST ast) {	    		
    		if (ast.getType() == TokenTypes.METHOD_CALL)
			visitCall(ast);
    		else 
    			super.visitToken(ast);
	}
    @Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
}
