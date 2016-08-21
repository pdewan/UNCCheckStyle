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
import unc.tools.checkstyle.ProjectSTBuilderHolder;

public  class ExpectedSignaturesCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "expectedSignatures";

	protected Map<String, String[]> typeToSignatures = new HashMap<>();
	protected Map<String, List<STMethod>> typeToMethods = new HashMap<>();
	@Override
	// get full name
	public int[] getDefaultTokens() {
		return new int[] {
				 TokenTypes.PACKAGE_DEF,
				TokenTypes.CLASS_DEF,
//				TokenTypes.INTERFACE_DEF,

				};

	}

//	public static final String SEPARATOR = ">";


	// this should be in an abstract class
	public void setExpectedSignaturesOfType(String aPattern) {
		String[] extractTypeAndSignatures = aPattern.split(TYPE_SEPARATOR);
		String aType = extractTypeAndSignatures[0].trim();
		if (extractTypeAndSignatures.length < 2) {
			System.err.println ("Illegal pattern:" + aPattern + " does not have: " + TYPE_SEPARATOR);
			return;
		}
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

	
	protected void logSignatureNotMatched(DetailAST anAST, DetailAST aTreeAST, String aSignature) {
//		String aSourceName = shortFileName(astToFileContents.get(aTreeAST)
//				.getFilename());
		String aTypeName = getName(getEnclosingTypeDeclaration(anAST));
		super.log(anAST, aTreeAST, aSignature, aTypeName);
//		if (aTreeAST == currentTree) {
//			DetailAST aLoggedAST = aTreeAST;
//			log(aLoggedAST.getLineNo(),  msgKey(), aSignature, aTypeName, aSourceName);
//
//		} else {
//			log(0, msgKey(), aSignature, aTypeName, aSourceName);
//		}

	}

	public Boolean matchSignatures(String[] aSpecifications,
			STMethod[] aMethods, DetailAST aTypeAST, DetailAST aTreeAST) {
		boolean retVal = true;
		for (String aSpecification : aSpecifications) {
		
//			String[] aPropertiesPath = aPropertySpecification.split(".");			
			if (!matchSignature(aSpecification, aMethods)) {
				logSignatureNotMatched(aTypeAST, aTreeAST, aSpecification);
				retVal = false;
			}
		}
		return retVal;
	}
	public Boolean matchMethods(List<STMethod> aSpecifications,
			STMethod[] aMethods, DetailAST aTypeAST, DetailAST aTreeAST) {
		boolean retVal = true;
		List<STMethod> aMethodsCopy = new ArrayList<STMethod>(Arrays.asList(aMethods));
		for (STMethod aSpecification : aSpecifications) {
		
//			String[] aPropertiesPath = aPropertySpecification.split(".");	
			Boolean hasMatched = matchMethod(aSpecification, aMethodsCopy);
			if (hasMatched == null)
				return null;
			if (!hasMatched) {
				String anOriginalSignature = methodToSignature.get(aSpecification);
				if (anOriginalSignature == null) {
					anOriginalSignature = aSpecification.getSignature();
				}
				logSignatureNotMatched(aTypeAST, aTreeAST, 
						anOriginalSignature
//						aSpecification.getSignature()
						);
				retVal = false;
			} 
		}
		return retVal;
	}
//	public  List<STMethod> signaturesToMethods(String[] aSignatures) {
//		List<STMethod> aMethods = new ArrayList();
//		for (String aSignature:aSignatures) {
//			aMethods.add(signatureToMethod(aSignature));
//		}
//		return aMethods;		
//	}
//	
//	public  STMethod signatureToMethod(String aSignature) {
//		String[] aNameAndRest = aSignature.split(":");
//		if (aNameAndRest.length != 2) {
//			System.err.print("Illegal signature, missing :" + aSignature);
//			return null;
//		}
//		String aName = aNameAndRest[0].trim();
//		String[] aReturnTypeAndParameters = aNameAndRest[1].split("->");
//		if (aReturnTypeAndParameters.length != 2) {
//			System.err.print("Illegal signature, missing ->" + aSignature);
//			return null;
//		}
//		String aReturnType = aReturnTypeAndParameters[1].trim();
//		String aParametersString = aReturnTypeAndParameters[0];
//		String[] aParameterTypes = aParametersString.equals("")?new String[0]:  aParametersString.split(STMethod.PARAMETER_SEPARATOR);
//		for (int i = 0; i < aParameterTypes.length; i++) {
//			aParameterTypes[i] = aParameterTypes[i].trim();
//			
//		}
//		return new AnSTMethod(null, aName, null, aParameterTypes, true, true, false, aReturnType, true, null, null, false, null);
//		
//	}
	
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
			STMethod aSpecification, List<STMethod> aMethods) {
		for (STMethod aMethod : aMethods) {
			Boolean hasMatched = matchSignature(aSpecification, aMethod);
			if (hasMatched == null)
				return null;
			if (hasMatched) {
				aMethods.remove(aMethod);
				// return
				// aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
				return true;

			} else 
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
//		return matchSignature(signatureToMethod(aSpecification), aMethod);
		return matchSignature(signatureToMethodorOrConstructor(aSpecification), aMethod);

		
	}
//	public  STMethod signatureToMethodorOrConstructor(String aSignature) {
//		return signatureToMethod(aSignature);
//	}
	boolean isMatchAnyting (Object[] aList) {
//		return aList == null || ( aList != null && aList.length == 1 && MATCH_ANYTHING.equals(aList[0]);

		return aList == null || 
				(aList.length == 1 && MATCH_ANYTHING.equals(aList[0]));
	}

	public Boolean matchSignature(
			STMethod aSpecification, STMethod aMethod) {
		variablesAdded.clear();
		String aReturnType = aSpecification.getReturnType();
		STNameable[] typeTags = null;
		
		if (aReturnType != null && aReturnType.startsWith(TAG_STRING)) {
			
			STType aReturnSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aReturnType.substring(1));
			if (aReturnSTType == null)
				return null;
			typeTags = aReturnSTType.getComputedTags();
		}
		Boolean retVal  = 
				(isMatchAnyting(aSpecification.getParameterTypes()) ||
				aSpecification.getParameterTypes().length == aMethod.getParameterTypes().length) &&
				unifyingMatchesNameVariableOrTag(aSpecification.getName(), aMethod.getName(), aMethod.getComputedTags()) &&
				unifyingMatchesNameVariableOrTag(aSpecification.getReturnType(), aMethod.getReturnType(), typeTags);
				
		if (!retVal) {
			backTrackUnification();
			return false;
		}
		if (isMatchAnyting(aSpecification.getParameterTypes()))
				return true;
		return  matchParameters(aSpecification, aMethod);
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
	public Boolean matchParameters(
			STMethod aSpecification, STMethod aMethod) {		
		String[] aSpecificationParameterTypes = aSpecification.getParameterTypes();
		String[] aMethodParameterTypes = aMethod.getParameterTypes();
		for (int i = 0; i < aSpecificationParameterTypes.length; i++) {
			String aParameterType = aSpecificationParameterTypes[i];

			STNameable[] parameterTags =null;
			if (aParameterType.startsWith(TAG_STRING)) {
				
//				STType aParameterSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aParameterType.substring(1));
				STType aParameterSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aMethodParameterTypes[i]);

				if (aParameterSTType == null)
					return null;
				parameterTags = aParameterSTType.getComputedTags();
			}
			
			if (!unifyingMatchesNameVariableOrTag(aSpecificationParameterTypes[i], aMethodParameterTypes[i], parameterTags)) {
				backTrackUnification();
				return false;
			}
		}
		return true;		
		
	}
	
	protected STMethod[] getMatchedMethods(STType anSTType) {
		return anSTType.getMethods();
	}
	
	public Boolean matchSignatures(STType anSTType, String[] aSpecifiedSignatures, DetailAST aTree) {
//		STMethod[] aMethods = anSTType.getMethods();
		STMethod[] aMethods = getMatchedMethods(anSTType);
		if (aMethods == null) 
			return null;
		return matchSignatures(aSpecifiedSignatures, aMethods, anSTType.getAST(), aTree);
	}
//	protected STMethod[] getMethods(STType anSTType) {
//		return anSTType.getMethods();
//		
//	}
	public Boolean matchMethods(STType anSTType, List<STMethod> aSpecifiedSignatures, DetailAST aTree) {
//		STMethod[] aMethods = anSTType.getMethods();
		STMethod[] aMethods = getMatchedMethods(anSTType);
		if (aMethods == null) 
			return null;
		return matchMethods(aSpecifiedSignatures, aMethods, anSTType.getAST(), aTree);
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
//  protected boolean visitType(STType anSTType) {
//	  return true;
//  }

	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {

		STType anSTType;
		
		if (ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {
			int i = 0;
			anSTType = getSTType(anAST);
		} else {
			anSTType =getSTType(aTree);
		}
		if (anSTType == null) {
			System.err.println ("Did not find sttype for " + anAST);
			return true;
		}
		if (anSTType.isEnum() || anSTType.isInterface()) // do not want to tag interface methods
			return true;
//		if (!visitType(anSTType)) {
//			return true;
//		}
//		if (anSTType.toString().contains("ss")) {
//			System.out.println ("Found Ass");
//		}
		// this is redundant based on above check,but let us keep
		if (!doCheck(anSTType)) {
			return true;
		}
		
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
	@Override
	public void leaveType(DetailAST ast) {
		if (ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {
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
		if (!ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {

		maybeAddToPendingTypeChecks(ast);
		}
		super.doFinishTree(ast);

	}
	@Override
	public void beginTree(DetailAST ast) {
		super.beginTree(ast);
	}

//	@Override
//	// this should never be called
//	protected Boolean check(DetailAST ast, String aShortMethodName,
//			String aLongMethodName, CallInfo aCallInfo) {
//		// TODO Auto-generated method stub
//		return null;
//	}


}
