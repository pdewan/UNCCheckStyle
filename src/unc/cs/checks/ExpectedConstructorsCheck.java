package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
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

public  class ExpectedConstructorsCheck extends ExpectedSignaturesCheck {
	public static final String MSG_KEY = "expectedConstructors";

	
	@Override
	// get full name
	public int[] getDefaultTokens() {
		return new int[] {
				 TokenTypes.PACKAGE_DEF,
				TokenTypes.CLASS_DEF
//				TokenTypes.ANNOTATION,
				// TokenTypes.INTERFACE_DEF,
				// TokenTypes.TYPE_ARGUMENTS,
				// TokenTypes.TYPE_PARAMETERS,
//				TokenTypes.VARIABLE_DEF, TokenTypes.PARAMETER_DEF,
//				TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF,
				// TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT,
				// TokenTypes.PARAMETER_DEF,
				// TokenTypes.LCURLY,
				// TokenTypes.RCURLY,
//				TokenTypes.CTOR_CALL,
//				TokenTypes.LITERAL_NEW,
//				TokenTypes.METHOD_CALL
				};

	}

	protected STMethod[] getMethods(STType anSTType) {
		return anSTType.getDeclaredConstructors();
		
	}
	public Boolean matchMethod(
			STMethod aSpecification, STMethod[] aMethods) {
		if (aMethods.length == 0 && aSpecification.getParameterTypes().length == 0)
			return true;
		return super.matchMethod(aSpecification, aMethods);
//		for (STMethod aMethod : aMethods) {
//			Boolean hasMatched = matchSignature(aSpecification, aMethod);
//			if (hasMatched == null)
//				return null;
//			if (hasMatched)
//				// return
//				// aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
//				return true;
//
//			else 
//				continue;
//		}
//		return false;
	}
	 protected boolean visitType(STType anSTType) {
		  return !anSTType.isInterface();
	  }

	public  STMethod signatureToMethodorOrConstructor(String aSignature) {
		return signatureToConstructor(aSignature);
	}

	public Boolean matchSignature(
			STMethod aSpecification, STMethod aMethod) {
		variablesAdded.clear();
		
		Boolean retVal  = 
				aSpecification.getParameterTypes().length == aMethod.getParameterTypes().length ;
				
		if (!retVal) {
//			backTrackUnification();
			return false;
		}
		return matchParameters(aSpecification, aMethod);
//		// this should be integrated with super class
//		String[] aSpecificationParameterTypes = aSpecification.getParameterTypes();
//		String[] aMethodParameterTypes = aMethod.getParameterTypes();
//		for (int i = 0; i < aSpecificationParameterTypes.length; i++) {
//			String aParameterType = aSpecificationParameterTypes[i];
//
//			STNameable[] parameterTags =null;
//			if (aParameterType.startsWith("@")) {
//				
//				STType aParameterSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aParameterType.substring(1));
//				if (aParameterSTType == null)
//					return null;
//				parameterTags = aParameterSTType.getComputedTags();
//			}
//			
//			if (!matchesNameVariableOrTag(aSpecificationParameterTypes[i], aMethodParameterTypes[i], parameterTags)) {
//				backTrackUnification();
//				return false;
//			}
//		}
//		return true;		
		
	}
	
	
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	



}
