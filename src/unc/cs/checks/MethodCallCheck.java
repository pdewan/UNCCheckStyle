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

public abstract  class MethodCallCheck extends MethodCallVisitedCheck {
	public static final String MSG_KEY = "expectedMethodCall";
	public static final String TYPE_SIGNATURE_SEPARATOR = "!";

	protected Map<String, String[]> typeToSignaturesWithTargets = new HashMap<>();
//	protected Map<String, List<STMethod>> typeToMethods = new HashMap<>();


//	public static final String SEPARATOR = ">";
	

	// this should be in an abstract class
	public void setExpectedSignaturesOfType(String aPattern) {
		String[] extractTypeAndSignatures = aPattern.split(TYPE_SEPARATOR);
		String aType = extractTypeAndSignatures[0].trim();
		String[] aSignaturesWithTarget = extractTypeAndSignatures[1].split(SET_MEMBER_SEPARATOR);
		
		typeToSignaturesWithTargets.put(aType, aSignaturesWithTarget);
//		typeToMethods.put(aType, signaturesToMethods(aSignaturesWithTarget));

	}

	/*
	 * @StructurePatternNames.LinePattern> X:int | Y:int | Width:int
	 * |Height:int,
	 * 
	 * @StructurePatternNames.OvalPatetrn> X:int | Y:int | Width:int |Height:int
	 */
	public void setExpectedCalls(String[] aPatterns) {
		for (String aPattern : aPatterns) {
			setExpectedSignaturesOfType(aPattern);
		}

	}
    // "fail" if method matches
	
	protected abstract boolean returnValueOnMatch();
    String matchedSignature;
	@Override
	protected Boolean check(DetailAST ast, String aShortMethodName,
			String aLongMethodName, CallInfo aCallInfo) {
		matchedSignature = "";
		String[] aSignaturesWithTargets = typeToSignaturesWithTargets.get(specifiedType);
		for (String aSignatureWithTarget:aSignaturesWithTargets) {
			
			Boolean retVal = matches(aSignatureWithTarget, aShortMethodName, aLongMethodName, aCallInfo);
			if (retVal == null)
				return null;
			if (retVal) {
				matchedSignature = aSignatureWithTarget;
				return returnValueOnMatch();
			}
		}
		return !returnValueOnMatch();
	}
	
	protected Boolean matches (String aSignatureWithTarget, String aShortMethodName,
			String aLongMethodName, CallInfo aCallInfo) {
		String[] aSignatureAndTarget = aSignatureWithTarget.split(TYPE_SIGNATURE_SEPARATOR);
		String aSignature ;
		String aSpecifiedTarget;
		if (aSignatureAndTarget == null ) {
			
			System.out.println ("Null signature!");
			return false;
		}
		if (aSignatureAndTarget.length == 0 ) {
			
			System.out.println ("signature with no elements");
			return false;
		}
		if (aSignatureAndTarget.length == 1 ) {
			
//			System.out.println ("signature with only one element");
			aSignature = aSignatureAndTarget[0];
			aSpecifiedTarget = aCallInfo.getCalledType(); // assuming local call
			if (aSpecifiedTarget.contains("]") || 
					aSpecifiedTarget.contains("[") ||
					aSpecifiedTarget.contains("(") ||
					aSpecifiedTarget.contains(")"))
				return false;
//			return false;
		}
//		if (aSignatureAndTarget.length < 2 ||
//				aSignatureAndTarget[1] == null ) {
//			
//			System.out.println ("Null signature");
//			return false;
//		}
//		String aSignature = aSignatureAndTarget[1].trim();
//		String aSpecifiedTarget = aSignatureAndTarget[0].trim();
		else {
		aSignature = aSignatureAndTarget[1].trim();
		aSpecifiedTarget = aSignatureAndTarget[0].trim();
		}
		STMethod aSpecifiedMethod = signatureToMethod(aSignature);
		return matches(aSpecifiedTarget, aSpecifiedMethod, aShortMethodName, aLongMethodName, aCallInfo);
		
	
		
	}
	protected void log(DetailAST ast, DetailAST aTreeAST, String aShortMethodName,
			String aLongMethodName, CallInfo aCallInfo) {
			log(ast, aTreeAST, specifiedType + " = " + matchedSignature);
			
		}
	public Boolean matchSignature(
			STMethod aSpecification, STMethod aMethod) {
		variablesAdded.clear();
		String aReturnType = aSpecification.getReturnType();
		STNameable[] typeTags = null;
		if (aReturnType.startsWith("@")) {
			
			STType aReturnSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aReturnType.substring(1));
			if (aReturnSTType == null)
				return null;
			typeTags = aReturnSTType.getComputedTags();
		}
		Boolean retVal  = 
//				aSpecification.getParameterTypes().length == aMethod.getParameterTypes().length &&
				matchesNameVariableOrTag(aSpecification.getName(), aMethod.getName(), aMethod.getComputedTags()) &&
				matchesNameVariableOrTag(aSpecification.getReturnType(), aMethod.getReturnType(), typeTags);
				
		if (!retVal) {
			backTrackUnification();
			return false;
		}
		String[] aSpecificationParameterTypes = aSpecification.getParameterTypes();
		String[] aMethodParameterTypes = aMethod.getParameterTypes();
		
		if (aSpecificationParameterTypes.length == 1) {
			if (aSpecificationParameterTypes[0].equals(MATCH_ANYTHING))
				return true;
		}
		if (aSpecificationParameterTypes.length != aMethodParameterTypes.length) {
			return false;
		}
		for (int i = 0; i < aSpecificationParameterTypes.length; i++) {
			
			String aParameterType = aSpecificationParameterTypes[i];

			STNameable[] parameterTags =null;
			if (aParameterType.startsWith("@")) {
				
				STType aParameterSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aParameterType.substring(1));
				if (aParameterSTType == null)
					return null;
				parameterTags = aParameterSTType.getComputedTags();
			}
			
			if (!matchesNameVariableOrTag(aSpecificationParameterTypes[i], aMethodParameterTypes[i], parameterTags)) {
//				backTrackUnification();
				return false;
			}
		}
		return true;		
		
	}
	protected static boolean isStarParameters(String[] aParameters) {
		return aParameters.length == 1 && aParameters[0].equals(MATCH_ANYTHING);
	}
	protected Boolean matches (String aSpecifiedTarget, STMethod aSpecifiedMethod, String aShortMethodName,
			String aLongMethodName, CallInfo aCallInfo) {
//		String aRegex = "(.*)" + aSpecifiedMethod.getName() + "(.*)";
//		String aRegex = aSpecifiedMethod.getName();
		if ( !isStarParameters(aSpecifiedMethod.getParameterTypes()) &&
				aCallInfo.getActuals().size() != aSpecifiedMethod.getParameterTypes().length)
			return false;
		String aTypeName = aCallInfo.getCalledType();
		if (aTypeName == null || aTypeName.contains(("["))) {
			return null;
		}
		
		 if (!aSpecifiedMethod.getName().startsWith("@") && !aSpecifiedTarget.startsWith("@")) { // we do not need to determine tags
			 return aShortMethodName.matches(aSpecifiedMethod.getName()) && aTypeName.matches(aSpecifiedTarget); 
		 }
//		 System.out.println ("Temp");
		 Boolean matchesType = matchesType(aSpecifiedTarget, aTypeName);
		 if (matchesType == null)
			 return null;
		 if (!matchesType) {
				 return false;
		 }

		 if (!aSpecifiedMethod.getName().startsWith("@") ) { // we do not need to determine method tags
			 return aShortMethodName.matches(aSpecifiedMethod.getName());
		 }
		
		
		STType aTargetSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
		if (aTargetSTType == null) {
			return null;
		}
		STMethod[] aMethods = aTargetSTType.getMethods();
		if (aMethods == null)
			return null;
		for (STMethod anSTMethod:aMethods) {
			if (!matchSignature(aSpecifiedMethod, anSTMethod))
				continue;
			if (anSTMethod.getName().equals(aCallInfo.getCalleee()) && 
					anSTMethod.getParameterTypes().length == aCallInfo.getActuals().size()) {
				return hasTag(anSTMethod, aSpecifiedMethod.getName());
			}
		}
		return false;
		

//		return aShortMethodName.matches(aSpecifiedMethod.getName());
		
	
		
	}
	
	String specifiedType;

	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
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
		return super.doPendingCheck(anAST, aTree);
	}
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}

}