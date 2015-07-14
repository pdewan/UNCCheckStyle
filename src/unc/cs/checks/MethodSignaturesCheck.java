package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import unc.cs.symbolTable.AnSTMethod;
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public  class MethodSignaturesCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "methodSignatures";

	protected Map<String, String[]> typeToSignature = new HashMap<>();
	public static final String SEPARATOR = ">";



	public void setExpectedSignaturesOfType(String aPattern) {
		String[] extractTypeAndSignatures = aPattern.split(">");
		String aType = extractTypeAndSignatures[0].trim();
		String[] aSignatures = extractTypeAndSignatures[1].split("\\|");
		typeToSignature.put(aType, aSignatures);
	}

	/*
	 * @StructurePatternNames.LinePattern> X:int | Y:int | Width:int
	 * |Height:int,
	 * 
	 * @StructurePatternNames.OvalPatetrn> X:int | Y:int | Width:int |Height:int
	 */
	public void setExpectedSignatures(String[] aPatterns) {
		for (String aPattern : aPatterns) {
			setExpectedSignaturesOfType(aPattern);
		}

	}

	
	protected void logSignatureNotMatched(DetailAST aTreeAST, String aSignature) {
		String aSourceName = shortFileName(astToFileContents.get(aTreeAST)
				.getFilename());
		if (aTreeAST == currentTree) {
			DetailAST aLoggedAST = matchedTypeOrTagAST == null?aTreeAST:matchedTypeOrTagAST;

			log(aLoggedAST.getLineNo(), aLoggedAST.getColumnNo(), msgKey(), aSignature, aSourceName);
		} else {
			log(0, msgKey(), aSignature, aSourceName);
		}

	}

	public Boolean matchSignatures(String[] aSpecifications,
			STMethod[] aMethods, DetailAST aTreeAST) {
		boolean retVal = true;
		for (String aSpecification : aSpecifications) {
		
//			String[] aPropertiesPath = aPropertySpecification.split(".");			
			if (!matchSignature(aSpecification, aMethods)) {
				logSignatureNotMatched(aTreeAST, aSpecification);
				retVal = false;
			}
		}
		return retVal;
	}
	
	public STMethod signatureToMethod(String aSignature) {
		String[] aNameAndRest = aSignature.split(":");
		if (aNameAndRest.length != 2) {
			System.err.print("Illegal signature, missing :" + aSignature);
			return null;
		}
		String aName = aNameAndRest[0].trim();
		String[] aReturnTypeAndParameters = aNameAndRest[1].split("->");
		if (aReturnTypeAndParameters.length != 2) {
			System.err.print("Illegal signature, missing ->" + aSignature);
			return null;
		}
		String aReturnType = aReturnTypeAndParameters[0];
		String[] aParameterTypes = aReturnTypeAndParameters[0].split(",");
		return new AnSTMethod(null, aName, null, aParameterTypes, true, true, aReturnType, true, null, false, null);
		
	}

	public Boolean matchSignature(
			String aSpecification, STMethod[] aMethods) {
		for (STMethod aMethod : aMethods) {
			if (matchSignature(aSpecification, aMethod))
				// return
				// aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
				return true;

			else 
				continue;
		}
		return false;
	}
	public Boolean matchSignature(
			String aSpecification, STMethod aMethod) {
		return matchSignature(signatureToMethod(aSpecification), aMethod);
		
	}
	public Boolean matchSignature(
			STMethod aSpecification, STMethod aMethod) {
		variablesAdded.clear();
		Boolean retVal  = 
				aSpecification.getParameterTypes().length == aMethod.getParameterTypes().length &&
				matchesNameOrVariable(aSpecification.getName(), aMethod.getName()) &&
				matchesNameOrVariable(aSpecification.getReturnType(), aMethod.getReturnType());
				
		if (!retVal) {
			backTrackUnification();
			return false;
		}
		String[] aSpecificationParameterTypes = aSpecification.getParameterTypes();
		String[] aMethodParameterTypes = aMethod.getParameterTypes();
		for (int i = 0; i < aSpecificationParameterTypes.length; i++) {
			if (!matchesNameOrVariable(aSpecificationParameterTypes[i], aMethodParameterTypes[i])) {
				backTrackUnification();
				return false;
			}
		}
		return true;		
		
	}
	


	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(
						getName(getEnclosingTypeDeclaration(aTree)));
		String aSpecifiedType = findMatchingType(typeToSignature.keySet(),
				anSTType);
		if (aSpecifiedType == null)
			return true; // the constraint does not apply to us
		
//		List<String> aSignatures = anSTType.getAllSignatures();
		STMethod[] aMethods = anSTType.getMethods();

		if (aMethods == null) 
			return null;
		String[] aSpecifiedSignatures = typeToSignature.get(aSpecifiedType);
		return matchSignatures(aSpecifiedSignatures, aMethods, aTree);
	}

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}


}
