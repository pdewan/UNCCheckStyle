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
	public void doFinishTree(DetailAST ast) {
		// STType anSTType =
		// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
		// for (STMethod aMethod: anSTType.getMethods()) {
		// visitMethod(anSTType, aMethod);
		// }
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}
	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		if (fullTypeName == null)
			return true;
		specifiedType = null;
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingTypeDeclaration(aTree)));
		STType anSTType = getSTType(aTree);

		if (anSTType.isEnum())
			return true;
		
		specifiedType = findMatchingType(typeToSignaturesWithTargets.keySet(),
				anSTType);
		if (specifiedType == null)
			return true; // the constraint does not apply to us
		List<CallInfo> aCalls = anSTType.getMethodsCalled();
		if (aCalls == null)
			return null;
		String[] aSpecifications = typeToSignaturesWithTargets.get(specifiedType);
		for (String aSpecification:aSpecifications) {
			boolean found = false;
			for (CallInfo aCallInfo:aCalls ) {
				String aNormalizedLongName = toLongName(aCallInfo.getNormalizedCall());
				String shortMethodName = toShortTypeName(aNormalizedLongName);
				Boolean matches = matches(aSpecification, shortMethodName, aNormalizedLongName, aCallInfo);
				if (matches == null)
					return null;
				if (matches) {
					found = true;
					break;
				}				
			}
			if (!found) {
				log(anAST, aTree, aSpecification);
			}
			
		}
		return true;
	}

}
