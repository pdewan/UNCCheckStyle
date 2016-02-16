package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.AnSTMethod;
import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public  class MissingMethodCallCheck extends MethodCallCheck {
	public static final String MSG_KEY = "missingMethodCall";
	public static final String WRONG_CALLER = "wrongCaller";

	
	@Override
	public int[] getDefaultTokens() {
		return new int[] {
				 TokenTypes.PACKAGE_DEF,
				TokenTypes.CLASS_DEF,
				
//				TokenTypes.ANNOTATION,
//				 TokenTypes.INTERFACE_DEF,

				};

	}
	public void setExpectedCalls(String[] aPatterns) {
		for (String aPattern : aPatterns) {
			setExpectedSignaturesOfType(aPattern);
		}

	}
    // "fail" if method matches
	

	
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}


	@Override
	protected boolean returnValueOnMatch() {
		return true;
	}
//	public void doFinishTree(DetailAST ast) {
//		// STType anSTType =
//		// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
//		// for (STMethod aMethod: anSTType.getMethods()) {
//		// visitMethod(anSTType, aMethod);
//		// }
//		maybeAddToPendingTypeChecks(ast);
//		super.doFinishTree(ast);
//
//	}
	@Override
	public void leaveType(DetailAST ast) {
		if (STBuilderCheck.getSingleton().getVisitInnerClasses()) {
			maybeAddToPendingTypeChecks(ast);
		}
		super.leaveType(ast);
	}
	public void doFinishTree(DetailAST ast) {
		// STType anSTType =
		// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
		// for (STMethod aMethod: anSTType.getMethods()) {
		// visitMethod(anSTType, aMethod);
		// }
		if (!STBuilderCheck.getSingleton().getVisitInnerClasses()) {

		maybeAddToPendingTypeChecks(ast);
		}
		super.doFinishTree(ast);

	}
	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		if (fullTypeName == null)
			return true;
		specifiedType = null;
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingTypeDeclaration(aTree)));
		STType anSTType = getSTType(anAST);

		if (anSTType.isEnum())
			return true;
		
		specifiedType = findMatchingType(typeToSignaturesWithTargets.keySet(),
				anSTType);
		if (specifiedType == null)
			return true; // the constraint does not apply to us
//		List<CallInfo> aCalls = anSTType.getMethodsCalled();
		// maybe have a separate check for local calls?
		List<CallInfo> aCalls = anSTType.getAllMethodsCalled();
		// for efficiency, let us remove mapped calls


		if (aCalls == null)
			return null;
		List<CallInfo> aCallsToBeChecked = new ArrayList(aCalls);

		String[] aSpecifications = typeToSignaturesWithTargets.get(specifiedType);
		boolean returnNull = false; 
//		int i = 0;
		for (String aSpecification:aSpecifications) {
			boolean found = false;
			for (CallInfo aCallInfo:aCallsToBeChecked ) {
				String aNormalizedLongName = toLongName(aCallInfo.getNormalizedCall());
				String shortMethodName = toShortTypeName(aNormalizedLongName);
				Boolean matches = matches(anSTType, maybeStripComment(aSpecification), shortMethodName, aNormalizedLongName, aCallInfo);

//				Boolean matches = matches(toShortTypeName(anSTType.getName()), aSpecification, shortMethodName, aNormalizedLongName, aCallInfo);
				if (matches == null) {
					if (!aSpecification.contains("!")) { // local call go onto another call
						continue;
					}
						
					returnNull = true;
					found = true; // we will come back to this
					continue;
//					return null;
				}
				if (matches) {
					found = true;
					// same call may be made directly or indirectly, and can cause problems if removed
//					aCallsToBeChecked.remove(aCallInfo);
					break;
				}				
			}
			if (!found) {
				log(anAST, aTree, aSpecification);
			}
			
		}
		if (returnNull)
			return null;
		return true;
	}

}
