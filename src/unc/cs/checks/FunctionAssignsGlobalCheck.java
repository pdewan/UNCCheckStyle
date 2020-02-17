package unc.cs.checks;

import java.util.HashSet;
import java.util.Set;

import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class FunctionAssignsGlobalCheck extends MethodEffectCheck{
	public static final String MSG_KEY = "functionAssignsGlobal";	
	


	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY ;
	}
	/*
	 * Do notthing as we will assume the symbol table builder has done its job
	 */


	@Override
	protected boolean shouldVisitRootMethod(STMethod aMethod) {
		return !aMethod.isProcedure(); // we are visiting functions only
	}
	// functions should not have side effects
	@Override
	protected boolean methodEffectCheck(STMethod anSTMethod) {
		return !anSTMethod.assignsToGlobal();
	}

	@Override
	protected boolean shouldVisitCalledMethod(STMethod aMethod) {
		return aMethod.isProcedure(); // functions have been visited as roots, why visit again (for grading?)
	}

	@Override
	protected boolean shouldTraverseVisitedMethod(STMethod aMethod) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean stopOnFailure() {
		return true;
	}
	

	

}
