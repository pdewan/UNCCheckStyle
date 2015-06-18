package unc.cs.checks;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

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
    	String[] aNormalizedParts = null;
    	if (currentTree == aTreeAST) {
    	String[] aCallParts = longMethodName.split("\\.");
    	 aNormalizedParts = toNormalizedClassBasedCall(aCallParts);
    	 astToContinuationData.put(ast, aNormalizedParts);
    	} else {
    		aNormalizedParts = (String[]) astToContinuationData.get(ast);
    		if (aNormalizedParts == null) {
    			System.err.println("Nprmalizedname not saved");
    		}
    	}
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
    public void checkedVisitToken(DetailAST ast) {	    		
    		if (ast.getType() == TokenTypes.METHOD_CALL)
			visitCall(ast);
    		else 
    			super.checkedVisitToken(ast);
	}
    @Override
	protected String msgKey() {
		return MSG_KEY;
	}
    protected Boolean typesMatch (String aSourceTypeSpecification, 
    		String aDestinationTypeSpecification, String anActualDestinationType, DetailAST aCallAST ) {
    	String aSourceTypeName = this.getName(getEnclosingClassDeclaration(aCallAST));
    	Boolean aSourceTypeMatches = matchesType(aSourceTypeSpecification, aSourceTypeName);
    	if (aSourceTypeMatches == null) { // this should never occur
    		return null;
    	}
    	if (!aSourceTypeMatches)
    		return false;
    	Boolean aDestinationTypeMatches = matchesType(aDestinationTypeSpecification, anActualDestinationType);
    	if (aDestinationTypeMatches == null) { 
    		return null;
    	}
    	return aDestinationTypeMatches;   
    }
  
    public static Boolean hasTag(STMethod aMethod, String aTag) {
    	STNameable[] aTags = aMethod.getTags();
    	return hasTag(aMethod.getTags(), aTag);
    }
    // assume classes have been matched
    protected Boolean matchMethod (String aMethodSpecification,
    		String aShortMethodClassName,
    		String aShortMethodName
    		) {
    	if (aMethodSpecification == null)
    		return true;
    	if (aMethodSpecification.indexOf("@") == -1)
    		return aMethodSpecification.equals(aShortMethodName);
    	String aSpecificationTag = aMethodSpecification.substring(1);
    	STType aTypeST = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aShortMethodClassName);
    	if (aTypeST == null)
    		return null;
    	STMethod[] aMethods = aTypeST.getMethods();
    	if (aMethods == null)
    		return null;
    	for (STMethod aMethod:aMethods) {
    		if (hasTag(aMethod, aSpecificationTag))
    			return true;
    	}
    	return false;
    	
    }
    
    // assume that classes have already matched
    protected Boolean methodsMatch (String aSourceMethodSpecification, 
    		String aDestinationMethodSpecification,
    		String aCallingClass,
    		String aCalledClass,    		 
    		String aCallingMethodShortName,     		 
    		String aCalledMethodShortName ) {
    	Boolean aSourcesMatch = matchMethod(aSourceMethodSpecification, aCallingClass, aCallingMethodShortName);
    	if (aSourcesMatch == null)
    		return null;
    	Boolean aDestinationsMatch = matchMethod(aDestinationMethodSpecification, aCalledClass, aCalledMethodShortName);
    	if (aDestinationsMatch == null)
    		return null;
    	return aSourcesMatch && aDestinationsMatch;
    }    	
    
    
    //"String.substring, String.charAt, @Tag1>String.length, Bar>Scanner.nextLine, @Tag1.@Tag3>@Tag2.@Tag4"/>
    
    protected Boolean methodCallContainedInSpecifications (String[] aCallParts, List<String> aSpecifications, DetailAST aCallAST) {
		 Boolean retVal = false;
    	for (String aMember:aSpecifications) {
			 SourceAndDestination aSourceAndDestination = new ASourceAndDestination(aMember);
			 TypeAndMethod aSourceTypeAndMethod = new ATypeAndMethod(aSourceAndDestination.getSource());
			 TypeAndMethod aDestinationTypeAndMethod = new ATypeAndMethod(aSourceAndDestination.getDestination());
			 Boolean aTypesMatch = typesMatch(aSourceTypeAndMethod.getType(), 
					 aDestinationTypeAndMethod.getType(), 
					 aCallParts[0], aCallAST);
			 if (aTypesMatch == null) {
				 retVal = null; // if we do not return true, then we do not know, see if this is an imported class
				 continue;
			 }
			 if (!aTypesMatch)
				 continue; // some other type may match
//				 return false;
			 String aCallingClass = getName(getEnclosingClassDeclaration(aCallAST));
			 String aCalledClass = aCallParts[0];
			 String aCallingMethod = getName(getEnclosingMethodDeclaration(aCallAST));
			 String aCalledMethod = aCallParts[1];
			 
			 Boolean aMethodsMatch = methodsMatch(
					 aSourceTypeAndMethod.getMethod(),
					 aDestinationTypeAndMethod.getMethod(),
					 aCallingClass,
					 aCalledClass,
					 aCallingMethod,
					 aCalledMethod);
			if (aMethodsMatch == null)
				return null;
			if (aMethodsMatch)
				return true;
					 
//					 
//				 
//			 if ((aSourceAndDestination.length == 2) && !matchesType(myClassName, aSourceAndDestination[0]))
//				 continue; // not relevant
//			 String aTrueMember = aSourceAndDestination.length == 2?aSourceAndDestination[1]:aMember;
//			 if (aTarget.equals(aTrueMember))
//				 return true;
		 }
		 return retVal;
	 }
}
