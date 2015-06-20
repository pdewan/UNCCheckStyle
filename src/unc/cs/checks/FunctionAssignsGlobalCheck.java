package unc.cs.checks;

import java.util.HashSet;
import java.util.Set;

import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class FunctionAssignsGlobalCheck extends ComprehensiveVisitCheck{
	public static final String MSG_KEY = "functionAssignsGlobal";	
	
	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF, TokenTypes.PACKAGE_DEF};
	} 

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY ;
	}
	/*
	 * Do notthing as we will assume the symbol table builder has done its job
	 */
	public void doVisitToken(DetailAST ast) {
//    	System.out.println("Check called:" + MSG_KEY);
		switch (ast.getType()) {
		case TokenTypes.PACKAGE_DEF: 
			visitPackage(ast);
			return;
		case TokenTypes.CLASS_DEF:
			visitType(ast);
			return;
		default:
			System.err.println("Unexpected token");
		}
	}
	Set<STMethod> methodsVisited = new HashSet();
	
	 boolean visitMethod(STType aClass, STMethod aMethod) {
		 methodsVisited.clear();
		
		if (!checkMethod(aClass, aMethod)) {
			log(aMethod.getAST().getLineNo(), msgKey(), aMethod.getName());
			return  false;
		} 
		return true;
    }
	 
	 boolean checkMethod(STType aClass, STMethod aMethod) {		
//			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
			if ("void".equals(aMethod.getReturnType()))
					return true;
			return (checkFunction(aClass, aMethod)) ;
	    }
	 
	 boolean checkFunction(STType aClass, STMethod aMethod) {
		 	if (methodsVisited.contains(aMethod))
		 		return true; // it must have been ok
		 	methodsVisited.add(aMethod);
			if (aMethod.assignsToGlobal())
				return false;
			return checkCalledProceduresOf(aClass, aMethod);
			
	    	
	 }
	 boolean checkCalledProcedure (STType aClass, STMethod aPossibleCalledMethod) {
		 if (!"void".equals(aPossibleCalledMethod.getReturnType()))
				return true;
			if (methodsVisited.contains(aPossibleCalledMethod))
		 		return true; // it must have been ok
		 	methodsVisited.add(aPossibleCalledMethod);
		 	if (aPossibleCalledMethod.assignsToGlobal())
		 		return false;
			return checkCalledProceduresOf(aClass, aPossibleCalledMethod);
		 
	 }
	 boolean checkCalledProceduresOf(STType aClass, STMethod aMethod) {		
			String[][] aCalledMethods = aMethod.methodsCalled();
			for (String[] aCalledMethod: aCalledMethods) {
				if (!aCalledMethod[0].equals(fullTypeName)) break;
				String aCalledMethodName = aCalledMethod[1];
				STMethod[] allCalledMethods = aClass.getMethods(aCalledMethodName);
				for (STMethod aPossibleCalledMethod: allCalledMethods) {
					if (!checkCalledProcedure(aClass, aPossibleCalledMethod))
						return false;
//					if (!"void".equals(aPossibleCalledMethod.getReturnType()))
//						continue;
//					if (methodsVisited.contains(aPossibleCalledMethod))
//				 		continue; // it must have been ok
//				 	methodsVisited.add(aPossibleCalledMethod);
//					if (!checkCalledProceduresOf(aClass, aPossibleCalledMethod))
//						return false;
				}
			}
			return true;	    	
	 }
	 
	 public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		 STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName (
				 getName(getEnclosingClassDeclaration(aTree)));
		 STMethod[] aMethods = anSTType.getMethods();
		 Boolean retVal = true;
		 if (aMethods == null)
			 return null;
			for (STMethod aMethod: anSTType.getMethods()) {
				retVal &= visitMethod(anSTType, aMethod); // no short circuot
			}
		 return retVal;
	 }

	
	public void finishTree(DetailAST ast) {		
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
//		for (STMethod aMethod: anSTType.getMethods()) {
//			visitMethod(anSTType, aMethod);
//		}
		maybeAddToPendingTypeChecks(ast);
		super.finishTree(ast);
    	
    }
	

}
