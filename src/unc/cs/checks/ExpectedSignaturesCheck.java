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
import unc.cs.symbolTable.PropertyInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public  class ExpectedSignaturesCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "expectedSignatures";

	protected Map<String, String[]> typeToSignatures = new HashMap<>();
	protected Map<String, List<STMethod>> typeToMethods = new HashMap<>();

//	public static final String SEPARATOR = ">";


	// this should be in an abstract class
	public void setExpectedSignaturesOfType(String aPattern) {
		String[] extractTypeAndSignatures = aPattern.split(TYPE_SEPARATOR);
		String aType = extractTypeAndSignatures[0].trim();
		String[] aSignatures = extractTypeAndSignatures[1].split(SET_MEMBER_SEPARATOR);
		
		typeToSignatures.put(aType, aSignatures);
		typeToMethods.put(aType, signaturesToMethods(aSignatures));
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
		String aTypeName = getName(getEnclosingTypeDeclaration(aTreeAST));
		if (aTreeAST == currentTree) {
			DetailAST aLoggedAST = matchedTypeOrTagAST == null?aTreeAST:matchedTypeOrTagAST;

			log(aLoggedAST.getLineNo(), aLoggedAST.getColumnNo(), msgKey(), aSignature, aTypeName, aSourceName);
		} else {
			log(0, msgKey(), aSignature, aTypeName, aSourceName);
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
	public Boolean matchMethods(List<STMethod> aSpecifications,
			STMethod[] aMethods, DetailAST aTreeAST) {
		boolean retVal = true;
		for (STMethod aSpecification : aSpecifications) {
		
//			String[] aPropertiesPath = aPropertySpecification.split(".");			
			if (!matchMethod(aSpecification, aMethods)) {
				logSignatureNotMatched(aTreeAST, aSpecification.getSignature());
				retVal = false;
			}
		}
		return retVal;
	}
	public List<STMethod> signaturesToMethods(String[] aSignatures) {
		List<STMethod> aMethods = new ArrayList();
		for (String aSignature:aSignatures) {
			aMethods.add(signatureToMethod(aSignature));
		}
		return aMethods;		
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
		String aReturnType = aReturnTypeAndParameters[1].trim();
		String aParametersString = aReturnTypeAndParameters[0];
		String[] aParameterTypes = aParametersString.equals("")?new String[0]:  aParametersString.split(STMethod.PARAMETER_SEPARATOR);
		for (int i = 0; i < aParameterTypes.length; i++) {
			aParameterTypes[i] = aParameterTypes[i].trim();
			
		}
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
	public Boolean matchMethod(
			STMethod aSpecification, STMethod[] aMethods) {
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
	public Boolean matchMethod(
			List<STMethod> aSpecifications, STMethod aMethod) {
		for (STMethod aSpecification : aSpecifications) {
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
	
	public Boolean matchSignatures(STType anSTType, String[] aSpecifiedSignatures, DetailAST aTree) {
		STMethod[] aMethods = anSTType.getMethods();

		if (aMethods == null) 
			return null;
		return matchSignatures(aSpecifiedSignatures, aMethods, aTree);
	}
	public Boolean matchMethods(STType anSTType, List<STMethod> aSpecifiedSignatures, DetailAST aTree) {
		STMethod[] aMethods = anSTType.getMethods();

		if (aMethods == null) 
			return null;
		return matchMethods(aSpecifiedSignatures, aMethods, aTree);
	}
	public Boolean matchSignatures(String aTypeName, String[] aSpecifiedSignatures, DetailAST aTree) {
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(
						getName(getEnclosingTypeDeclaration(aTree)));
		return matchSignatures(anSTType, aSpecifiedSignatures, aTree);
	}

//	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingTypeDeclaration(aTree)));
//		String aSpecifiedType = findMatchingType(typeToSignature.keySet(),
//				anSTType);
//		if (aSpecifiedType == null)
//			return true; // the constraint does not apply to us
//		
////		List<String> aSignatures = anSTType.getAllSignatures();
//		STMethod[] aMethods = anSTType.getMethods();
//
//		if (aMethods == null) 
//			return null;
//		String[] aSpecifiedSignatures = typeToSignature.get(aSpecifiedType);
//		return matchSignatures(aSpecifiedSignatures, aMethods, aTree);
//	}
//	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingTypeDeclaration(aTree)));
//		String aSpecifiedType = findMatchingType(typeToSignature.keySet(),
//				anSTType);
//		if (aSpecifiedType == null)
//			return true; // the constraint does not apply to us
//		String[] aSpecifiedSignatures = typeToSignature.get(aSpecifiedType);
//		return matchSignatures(anSTType, aSpecifiedSignatures, aTree);
//		


	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
				.getSTClassByShortName(
						getName(getEnclosingTypeDeclaration(aTree)));
		if (anSTType.isEnum())
			return true;
		
		String aSpecifiedType = findMatchingType(typeToSignatures.keySet(),
				anSTType);
		if (aSpecifiedType == null)
			return true; // the constraint does not apply to us
		List<STMethod> aSpecifiedSignatures = typeToMethods.get(aSpecifiedType);
		return matchMethods(anSTType, aSpecifiedSignatures, aTree);
	}

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
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


}
