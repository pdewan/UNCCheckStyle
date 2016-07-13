package unc.cs.checks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public abstract class MethodEffectCheck extends ComprehensiveVisitCheck{
//	public static final String MSG_KEY = "setterAssignsGlobal";	
	
	@Override
	public int[] getDefaultTokens() {
		return new int[] {
//				TokenTypes.CLASS_DEF, 
//				TokenTypes.PACKAGE_DEF
				};
	} 

//	@Override
//	protected String msgKey() {
//		// TODO Auto-generated method stub
//		return MSG_KEY ;
//	}
	protected Set<STMethod> methodsVisited = new HashSet();

	/*
	 * Do notthing as we will assume the symbol table builder has done its job
	 */
//	public void doVisitToken(DetailAST ast) {
////    	System.out.println("Check called:" + MSG_KEY);
//		switch (ast.getType()) {
//		case TokenTypes.PACKAGE_DEF: 
//			visitPackage(ast);
//			return;
//		case TokenTypes.CLASS_DEF:
//			visitType(ast);
//			return;
//		default:
//			System.err.println("Unexpected token");
//		}
//	}
	
	 protected Boolean visitRootMethod(STMethod aMethod, DetailAST aTreeAST) {
		 methodsVisited.clear();
		 if (!checkIncludeExcludeTagsOfMethod(Arrays.asList(aMethod.getComputedTags())))
			 return true;
		 if (!shouldVisitRootMethod(aMethod))
			 return true;	
		 Boolean checkRoot = checkRootMethod(aMethod);
		 if (checkRoot ==null) {
			 return null;
		 }
		 
//		if (!checkRootMethod(aMethod)) {
		if (!checkRoot) {
//			DetailAST anSTTreeAST = getEnclosingTreeDeclaration(aMethod.getAST());
//			String aLongFileName = anSTTreeAST == STBuilderCheck.getSingleton().getSTBuilderTree()?getFileContents().getFilename():
//					
//					astToFileContents.get(aTreeAST)
//					.getFilename();
//			// make this conform to the superclass logs
//			log(aMethod.getAST().getLineNo(), 
//					msgKey(), 
//					aMethod.getName(),
//					shortFileName(aLongFileName)
//					);
//			super.log(aMethod, aMethod.getName());
			super.log(aMethod.getAST(), aMethod.getName()); // standard form?
			return  false;
		} 
		return true;
    }
	 
	 protected abstract boolean shouldVisitRootMethod(STMethod aMethod);
	 protected abstract boolean shouldVisitCalledMethod(STMethod aMethod) ;
	 protected abstract boolean shouldTraverseVisitedMethod(STMethod aMethod) ;



	 
//	 boolean checkMethod(STMethod aMethod) {		
////			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
////			if ("void".equals(aMethod.getReturnType()))
////					return true;
////			if (!visitRootMethod())
////				return true;
//			return checkRootMethod(aMethod) ;
//	    }
	 
	protected Boolean checkRootMethod(STMethod aMethod) {
		 	if (methodsVisited.contains(aMethod))
		 		return true; // it must have been ok
		 	methodsVisited.add(aMethod);
			if (!methodEffectCheck(aMethod)) {
				if (stopOnFailure())
				return false;
			} else {
				if (!stopOnFailure())
					return true;
			}
			return checkCalledMethodsOf(aMethod);
			
	    	
	 }
	 protected abstract boolean methodEffectCheck(STMethod anSTMethod) ;
//	 protected abstract boolean stopOnFailure(); // vs. stop on success
	 
	 protected Boolean checkCalledMethod (STMethod aPossibleCalledMethod) {
//		 if (!shouldVisitCalledMethod(aPossibleCalledMethod))
//			 // we want to continue, so if we are going to stopOnFailure, return true
//			 // if we are going to stopOnSuccess, return false
//			 return stopOnFailure(); 
//		 if (!"void".equals(aPossibleCalledMethod.getReturnType()))
//				return true;
			if (methodsVisited.contains(aPossibleCalledMethod))
		 		return true; // it must have been ok
		 	methodsVisited.add(aPossibleCalledMethod);
//		 	if (aPossibleCalledMethod.assignsToGlobal())
			if (!methodEffectCheck(aPossibleCalledMethod)) {
				if (stopOnFailure())
					return false;
			} else {
				if (!stopOnFailure()) // stop on success
					return true;
			}
			
			return checkCalledMethodsOf(aPossibleCalledMethod);
		 
	 }
	 // get rid of aClass as we are foing global checks
	 protected Boolean checkCalledMethodsOf (STMethod aMethod) {	
		 if (!shouldTraverseVisitedMethod(aMethod))
				return true;
//			String[][] aCalledMethods = aMethod.methodsCalled();
			CallInfo[] aCalledMethods = aMethod.getMethodsCalled();
			for (CallInfo aCallInfo: aCalledMethods) {
//				if (!aCalledMethod[0].equals(fullTypeName)) break;
				String[] aCalledMethod = aCallInfo.getNormalizedCall();
				String aCalledMethodName = aCalledMethod[1];
				String aCalledMethodClassName = aCalledMethod[0];
				if (aCalledMethod.length > 2 || aCalledMethodClassName == null || isExternalClass(aCalledMethodClassName))
					continue;
				STType aCalledMethodClass = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aCalledMethodClassName);
				
				if (aCalledMethodClass == null) {
//					System.err.println("Null called method class:" + aCalledMethodClassName);
					return null;
				}
				STMethod[] allOverloadedMethods = aCalledMethodClass.getMethods(aCalledMethodName);
				if (allOverloadedMethods == null) {
//					System.out.println ("Overloaded methods is null" + allOverloadedMethods);
					return null; //return to pending check
				}
				for (STMethod aPossibleCalledMethod: allOverloadedMethods) {
					if (!shouldVisitCalledMethod(aPossibleCalledMethod)) { 
						// called method passes check
						if (!stopOnFailure()) // stop on success
							return true; // check passed
						else
						   continue;
					}
					Boolean checkCalled = checkCalledMethod(aPossibleCalledMethod);
					if (checkCalled == null) {
						return null;
					}
//					if (!checkCalledMethod(aPossibleCalledMethod)) {
					if (!checkCalled) {

						if (stopOnFailure()) {
							return false; // one failed no point continuing
						}
					} else if (!stopOnFailure()) // one suceeded no point continuing
						return true;
//					if (!"void".equals(aPossibleCalledMethod.getReturnType()))
//						continue;
//					if (methodsVisited.contains(aPossibleCalledMethod))
//				 		continue; // it must have been ok
//				 	methodsVisited.add(aPossibleCalledMethod);
//					if (!checkCalledProceduresOf(aClass, aPossibleCalledMethod))
//						return false;
				}
			}
			return stopOnFailure();	    	//nothing failed, so true in one case, nothing succeeded, so false in another
	 }
	 
	 public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		 STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName (
//				 getName(getEnclosingTypeDeclaration(aTree)));
			STType anSTType = getSTType(aTree);
			if (anSTType.isEnum() || anSTType.isInterface())
				return true;

		 STMethod[] aMethods = anSTType.getDeclaredMethods();// we check only out methods
		 Boolean retVal = true;
		 if (aMethods == null)
			 return null;
			for (STMethod aMethod: aMethods) {
				Boolean visitRoot =  visitRootMethod(aMethod, aTree);
				if (visitRoot == null)
					return null;
				retVal &= visitRoot; // no short circuit
			}
		 return retVal;
	 }

	
	public void doFinishTree(DetailAST ast) {		
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
//		for (STMethod aMethod: anSTType.getMethods()) {
//			visitMethod(anSTType, aMethod);
//		}
//		if (shortTypeName == null)
//			return; // maybe visiting interface
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);
		shortTypeName = null;
    	
    }
	

}
